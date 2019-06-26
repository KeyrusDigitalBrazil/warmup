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
package de.hybris.platform.cmsfacades.common.itemcollector;

import de.hybris.platform.core.model.ItemModel;

import java.util.Optional;

/**
 * Generic Item Collector Registry interface that should be responsible for managing (memory cache) and retrieving the corresponding 
 * {@link ItemCollector} for a given {@link ItemModel}. 
 */
public interface ItemCollectorRegistry
{
	/**
	 * Returns the corresponding {@link ItemCollector} for a given {@link ItemModel}
	 * @param itemModel the item model that will be used to retrieve the {@link ItemCollector} instance. 
	 * @return the {@code Optional<ItemCollector>} object for the given item model passed.   
	 */
	Optional<ItemCollector> getItemCollector(final ItemModel itemModel);
}
