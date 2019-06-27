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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;

import org.junit.rules.ExternalResource;

/**
 * The base class to contexts to provide common functionality, such as setup and clean up after tests.
 */
public abstract class BaseContext extends ExternalResource
{
	private static final String SELECT_TEMPLATE = "SELECT {%s} FROM {%s}";

	private ModelService modelService;
	private FlexibleSearchService flexibleSearch;

	public BaseContext()
	{
	}

	protected <T> T getService(final String name, final Class<T> type)
	{
		return IntegrationTestUtil.getService(name, type);
	}

	private static String staticStringFieldValue(final Class<? extends ItemModel> type, final String fieldName)
			throws NoSuchFieldException, IllegalAccessException
	{
		return (String) type.getField(fieldName).get(null);
	}

	public ModelService modelService()
	{
		return modelService;
	}

	public FlexibleSearchService flexibleSearch()
	{
		return flexibleSearch;
	}

	@Override
	public void before()
	{
		modelService = getService("modelService", ModelService.class);
		flexibleSearch = getService("flexibleSearchService", FlexibleSearchService.class);
	}

	protected <T extends ItemModel> void removeAll(final Class<T> type)
	{
		final Collection<T> all = findAll(type);
		modelService().removeAll(all);
	}

	/**
	 * Retrieves all model items existing in the persistent storage.
	 *
	 * @return a collection of all items or an empty collection, if there are no items in the persistent storage.
	 */
	protected <T extends ItemModel> Collection<T> findAll(final Class<T> type)
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
	 * Executes the specified Flexible Search query.
	 *
	 * @param query query to execute
	 * @return result of the query. It may be an empty collection, if the query did not find matching data.
	 */
	private <T extends ItemModel> Collection<T> findAll(final String query)
	{
		final SearchResult<T> searchResult = flexibleSearch().search(query);
		return searchResult.getResult();
	}
}
