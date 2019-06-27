/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTestLogic;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.ApplicationContext;

/**
 * Test utilities.
 */
public class IntegrationTestUtil extends ServicelayerTestLogic
{
	private static final String SELECT_TEMPLATE = "SELECT {%s} FROM {%s}";

	/**
	 * Imports impex into the platform
	 *
	 * @param content IMPEX content to be imported.
	 * @throws ImpExException when import failed. Most likely the IMPEX is invalid.
	 */
	public static void importCsv(final String content) throws ImpExException
	{
		importStream(IOUtils.toInputStream(content), Charset.defaultCharset().name(), randomName(".impex"));
	}

	/**
	 * Imports impex into the platform
	 *
	 * @param lines IMPEX lines to be imported.
	 * @throws ImpExException when import failed. Most likely the IMPEX is invalid.
	 */
	public static void importImpEx(final String... lines) throws ImpExException
	{
		final String content = String.join(System.lineSeparator(), lines);
		importCsv(content);
	}

	public static void assertModelDoesNotExist(final ItemModel model)
	{
		assertThat(getModelByExample(model)).isNull();
	}

	public static <T> T assertModelExists(final T sample)
	{
		final T persisted = getModelByExample(sample);
		assertThat(persisted).isNotNull();
		return persisted;
	}

	public static <T> T getModelByExample(final T sample)
	{
		final FlexibleSearchService flexibleSearchService = getFlexibleSearch();
		try
		{
			return flexibleSearchService.getModelByExample(sample);
		}
		catch (final ModelNotFoundException e)
		{
			return null;
		}
	}

	/**
	 * Retrieves an item model matching the specified condition.
	 * @param type type of the item to find
	 * @param condition a condition the item should match
	 *
	 * @return an optional containing first found matching item (it's possible more than one item matches the condition)
	 * or an empty optional, if there are no items matching the condition in the persistent storage.
	 */
	public static <T extends ItemModel> Optional<T> findAny(final Class<T> type, final Predicate<T> condition)
	{
		return findAll(type).stream()
				.map(type::cast)
				.filter(condition)
				.findAny();
	}

	/**
	 * Retrieves all model items existing in the persistent storage.
	 *
	 * @return a collection of all items or an empty collection, if there are no items in the persistent storage.
	 */
	public static Collection<Object> findAll(final Class<? extends ItemModel> type)
	{
		try
		{
			final String pk = staticStringFieldValue(type, "PK");
			final String typeCode = staticStringFieldValue(type, "_TYPECODE");
			final String query = String.format(SELECT_TEMPLATE, pk, typeCode);
			return findAll(query);
		}
		catch (final NoSuchFieldException | IllegalAccessException e)
		{
			throw new IllegalArgumentException(type + " does not have public static PK/_TYPECODE fields", e);
		}
	}

	/**
	 * Removes all model items existing in the persistent storage.
	 *
	 * @return a collection of all deleted items or an empty collection, if there are no items in the persistent storage.
	 */
	public static Collection<Object> removeAll(final Class<? extends ItemModel> type)
	{
		final Collection<Object> allItems = findAll(type);
		getModelService().removeAll(allItems);
		return allItems;
	}

	private static String randomName(final String suffix)
	{
		return RandomStringUtils.randomAlphanumeric(10) + suffix;
	}

	private static String staticStringFieldValue(final Class<? extends ItemModel> type, final String fieldName)
			throws NoSuchFieldException, IllegalAccessException
	{
		return (String) type.getField(fieldName).get(null);
	}

	/**
	 * Executes the specified Flexible Search query.
	 *
	 * @param query query to execute
	 * @return result of the query. It may be an empty collection, if the query did not find matching data.
	 */
	private static Collection<Object> findAll(final String query)
	{
		return getFlexibleSearch().search(query).getResult();
	}

	private static FlexibleSearchService getFlexibleSearch()
	{
		return getService("flexibleSearchService", FlexibleSearchService.class);
	}

	private static ModelService getModelService()
	{
		return getService("modelService", ModelService.class);
	}

	public static <T> T getService(final String name, final Class<T> type)
	{
		final ApplicationContext context = Registry.getApplicationContext();
		return context.getBean(name, type);
	}
}
