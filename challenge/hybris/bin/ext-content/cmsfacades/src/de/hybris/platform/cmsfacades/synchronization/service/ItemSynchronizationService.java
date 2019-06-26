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
package de.hybris.platform.cmsfacades.synchronization.service;

import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationItemDetailsData;
import de.hybris.platform.core.model.ItemModel;

import java.util.List;

/**
 * The {@code ItemSynchronizationService} is responsible for getting the synchronization status details for an item. 
 */
public interface ItemSynchronizationService
{
	/**
	 * Gets the synchronization item status for a given {@link ItemModel} taking in consideration the source and target catalog versions.  
	 * Implementations should be able to provide details of the synchronization status of related items as well. 
	 * @param syncRequestData the synchronization request containing the source and target catalog versions
	 * @param item the item model we are interested in getting the synchronization status
	 * @return an instance of {@link SynchronizationItemDetailsData}, never {@code null}.
	 * @throws IllegalArgumentException when any of the given parameters is {@code null}.
	 */
	SynchronizationItemDetailsData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemModel item);

	/**
	 * performs the synchronization of a list of {@link ItemModel} taking in consideration the source and target catalog versions.  
	 * Implementations should be able to provide details of the synchronization status of related items as well. 
	 * @param syncRequestData the synchronization request containing the source and target catalog versions
	 * @param items the list of item models that we want to synchronize
	 * @param config the synchronization configuration . See {@link SyncConfig} for more details. 
	 */
	void performItemSynchronization(SyncRequestData syncRequestData, List<ItemModel> items, SyncConfig config);

}
