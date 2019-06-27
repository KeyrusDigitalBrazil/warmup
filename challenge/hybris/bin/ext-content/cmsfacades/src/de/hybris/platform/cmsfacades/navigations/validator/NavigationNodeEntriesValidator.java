/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.navigations.validator;

import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryConverterRegistry;
import de.hybris.platform.cmsfacades.navigations.service.NavigationEntryItemModelConverter;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates all the entries of a given {@link NavigationNodeData}.
 * @deprecated since 1811 - no longer needed
 */
@Deprecated
public class NavigationNodeEntriesValidator implements Validator
{
	private static final Logger LOG = LoggerFactory.getLogger(NavigationNodeEntriesValidator.class);

	private static final String ITEM_SUPER_TYPE = "itemSuperType";
	private static final String ITEM_ID = "itemId";
	private static final String ENTRIES = "entries";

	private NavigationEntryConverterRegistry navigationEntryConverterRegistry;

	private Predicate<ItemModel> validEntryItemModelPredicate;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return NavigationNodeData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final NavigationNodeData target = (NavigationNodeData) obj;

		if (!CollectionUtils.isEmpty(target.getEntries()))
		{
			IntStream.range(0, target.getEntries().size()).forEach(idx -> {
				final NavigationEntryData entryData = target.getEntries().get(idx);
				if (StringUtils.isEmpty(entryData.getItemId()))
				{
					errors.rejectValue(ENTRIES, CmsfacadesConstants.FIELD_REQUIRED_NAVIGATION_NODE_ENTRY, new Object[]
							{ ITEM_ID, idx }, null);
				}
				if (StringUtils.isEmpty(entryData.getItemSuperType()))
				{
					errors.rejectValue(ENTRIES, CmsfacadesConstants.FIELD_REQUIRED_NAVIGATION_NODE_ENTRY, new Object[]
							{ ITEM_SUPER_TYPE, idx }, null);
				}
				if (!StringUtils.isEmpty(entryData.getItemSuperType()) && !StringUtils.isEmpty(entryData.getItemId()))
				{
					validateEntryItem(errors, idx, entryData);
				}
			});
		}
	}

	/**
	 * Validates if it is a valid item
	 *
	 * @param errors
	 *           the errors object created for this validation
	 * @param idx
	 *           the index of the current entry
	 * @param entryData
	 *           the entry being validated
	 */
	protected void validateEntryItem(final Errors errors, final int idx, final NavigationEntryData entryData)
	{
		final Optional<NavigationEntryItemModelConverter> optionalConverter = getNavigationEntryConverterRegistry()
				.getNavigationEntryItemModelConverter(entryData.getItemSuperType());
		if (!optionalConverter.isPresent())
		{
			errors.rejectValue(ENTRIES, CmsfacadesConstants.FIELD_NAVIGATION_NODE_ENTRY_CONVERTER_NOT_FOUND, new Object[]
					{ ITEM_SUPER_TYPE, idx }, null);
		}
		else
		{
			try
			{
				if (!getValidEntryItemModelPredicate().test(optionalConverter.get().getConverter().apply(entryData)))
				{
					errors.rejectValue(ENTRIES, CmsfacadesConstants.FIELD_CIRCULAR_DEPENDENCY_ON_NAVIGATION_NODE_ENTRY, new Object[]
							{ ITEM_ID, idx }, null);
				}
			}
			catch (final ConversionException e)
			{
				LOG.info(e.getMessage(), e);
				errors.rejectValue(ENTRIES, CmsfacadesConstants.FIELD_NAVIGATION_NODE_ENTRY_CONVERSION_ERROR, new Object[]
						{ ITEM_ID, idx }, null);
			}
		}
	}

	protected NavigationEntryConverterRegistry getNavigationEntryConverterRegistry()
	{
		return navigationEntryConverterRegistry;
	}

	@Required
	public void setNavigationEntryConverterRegistry(final NavigationEntryConverterRegistry navigationEntryConverterRegistry)
	{
		this.navigationEntryConverterRegistry = navigationEntryConverterRegistry;
	}

	protected Predicate<ItemModel> getValidEntryItemModelPredicate()
	{
		return validEntryItemModelPredicate;
	}

	@Required
	public void setValidEntryItemModelPredicate(final Predicate<ItemModel> validEntryItemModelPredicate)
	{
		this.validEntryItemModelPredicate = validEntryItemModelPredicate;
	}
}
