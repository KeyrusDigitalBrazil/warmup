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
package de.hybris.platform.cmsfacades.navigations.service;


import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.core.model.ItemModel;

import java.util.function.Function;


/**
 * Interface that defines the converter functions for a given item type. It defines two functions, one to convert from
 * {@link NavigationEntryData} to {@link ItemModel} and another to convert from {@link ItemModel} to its Unique
 * Identifier value.
 */
public interface NavigationEntryItemModelConverter
{

	/**
	 * Returns the item type {@code ItemModel#getItemType} where the converter should be applied to.
	 *
	 * @return item type
	 */
	String getItemType();

	/**
	 * Gets the function to be applied on the conversion step.
	 *
	 * @return the conversion function to be applied for the type {@code NavigationEntryItemModelConverter#getItemType}
	 */
	Function<NavigationEntryData, ItemModel> getConverter();

	/**
	 * Gets the function to be applied on the conversion step.
	 *
	 * @return the conversion function to be applied for the type matching
	 *         {@code NavigationEntryItemModelConverter#getItemType}.
	 * @see {@link NavigationEntryItemModelConverter#getItemType()}.
	 */
	Function<ItemModel, String> getUniqueIdentifierConverter();

}
