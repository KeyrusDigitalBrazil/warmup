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
package de.hybris.platform.cmsfacades.synchronization;

import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmsfacades.data.SyncItemStatusConfig;
import de.hybris.platform.cmsfacades.data.SyncItemStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.core.model.ItemModel;

/**
 * The {@link ItemSynchronizationFacade} facade is responsible for providing methods for getting the 
 * synchronization status of a specific {@link ItemModel}. 
 */
public interface ItemSynchronizationFacade
{

	/**
	 * Retrieves the synchronization status for a given {@link ItemModel}. In the method signature the client needs to provide 
	 * the {@link SyncRequestData} which contains the source and target catalog versions. 
	 * 
	 * The implementation should provide a default maximum depth property to prevent the mechanism to crawl too many items. 
	 * 
	 * @param syncRequestData the synchronization request containing the source and target catalog versions 
	 * @param itemSynchronizationData the pair of identifier/type to retrieve the {@link ItemModel} of which we want to get the synchronization status  
	 * @return the synchronization status details for a given {@link ItemModel}. 
	 * @throws java.lang.IllegalArgumentException when the default maximum depth configuration is not greater than zero.
	 */
	SyncItemStatusData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemSynchronizationData itemSynchronizationData);

	/**
	 * Retrieves the synchronization status for a given {@link ItemModel}. In the method signature the client needs to provide 
	 * the {@link SyncRequestData} which contains the source and target catalog versions. 
	 * 
	 * This method lets the client decide what is the maximum depth level to navigate on the item's dependencies.  
	 * 
	 * @param syncRequestData the synchronization request containing the source and target catalog versions 
	 * @param itemSynchronizationData the pair of identifier/type to retrieve the {@link ItemModel} of which we want to get the synchronization status  
	 * @param config the get status configuration. See {@link SyncItemStatusConfig} for more details. 
	 * @return the synchronization status details for a given {@link ItemModel}.
	 * @throws java.lang.IllegalArgumentException when the maximum depth configuration is not greater than zero.
	 */
	SyncItemStatusData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemSynchronizationData itemSynchronizationData, final SyncItemStatusConfig config);
	
	/**
	 * Performs the synchronization for a list of {@link ItemModel}. In the method signature the client needs to provide 
	 * the {@link SyncRequestData} which contains the source and target catalog versions. 
	 * 
	 * @param syncRequestData the synchronization request containing the source and target catalog versions 
	 * @param synchronizationData contains the list of {@link ItemSynchronizationData} pairs of itemId/itemType that we want to synchronize
	 */	
	void performItemSynchronization(SyncRequestData syncRequestData, SynchronizationData synchronizationData);
	
	/**
	 * Performs the synchronization for a list of {@link ItemModel}. In the method signature the client needs to provide 
	 * the {@link SyncRequestData} which contains the source and target catalog versions. 
	 * 
	 * @param syncRequestData the synchronization request containing the source and target catalog versions 
	 * @param synchronizationData contains the list of pairs of {@link ItemSynchronizationData} itemId/itemType that we want to synchronize
	 * @param config the synchronization configuration . See {@link SyncConfig} for more details. 
	 */	
	void performItemSynchronization(SyncRequestData syncRequestData, SynchronizationData synchronizationData, SyncConfig config);

}
