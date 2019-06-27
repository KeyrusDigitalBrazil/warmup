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

import de.hybris.platform.core.model.ItemModel;

import java.util.List;
import java.util.Optional;


/**
 * Registry that stores a collection of {@link NavigationEntryItemModelConverter}.
 */
public interface NavigationEntryConverterRegistry
{
	/**
	 * Get a specific {@code NavigationEntryItemModelConverter} by its itemType.
	 *
	 * @param itemType
	 * 		the itemType {@link ItemModel#getItemtype()} of the element to retrieve from the registry
	 * @return an {@code Optional} element matching the itemType
	 */
	Optional<NavigationEntryItemModelConverter> getNavigationEntryItemModelConverter(String itemType);


	/**
	 * Get a list of supported navigation entry item types.
	 *
	 * @return an {@code Optional} list of item types supported by this registry.
	 * @deprecated since 1811.
	 */
	@Deprecated
	Optional<List<String>> getSupportedItemTypes();
}
