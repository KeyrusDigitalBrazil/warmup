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
package de.hybris.platform.cmsfacades.synchronization.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.cmsfacades.common.itemcollector.ItemCollectorRegistry;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.data.ItemSynchronizationData;
import de.hybris.platform.cmsfacades.data.SyncItemStatusConfig;
import de.hybris.platform.cmsfacades.data.SyncItemStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationData;
import de.hybris.platform.cmsfacades.data.SynchronizationItemDetailsData;
import de.hybris.platform.cmsfacades.synchronization.ItemSynchronizationFacade;
import de.hybris.platform.cmsfacades.synchronization.service.ItemSynchronizationService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;


/**
 * Default implementation of the {@link ItemSynchronizationFacade} facade.
 *
 * Its main purpose is to coordinate the recursive calls for the {@link ItemSynchronizationService}
 * respecting the {@code maxDepth} attribute (either given in the method or using the default value).
 * It makes usage of the {@link ItemCollectorRegistry} and {@link de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector} classes
 * to identify item models that are directly referenced or that are shared by a given item model.
 */
public class DefaultItemSynchronizationFacade implements ItemSynchronizationFacade
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private ItemCollectorRegistry basicItemCollectorRegistry;
	private ItemCollectorRegistry sharedItemCollectorRegistry;
	private ItemCollectorRegistry dependentItemCollectorRegistry;
	private ItemSynchronizationService itemSynchronizationService;
	private Converter<SynchronizationItemDetailsData, SyncItemStatusData> syncItemStatusConverter;
	private SyncItemStatusConfig syncItemStatusConfig;
	private SyncConfig syncConfig;
	private Validator catalogSynchronizationCompositeValidator;
	private FacadeValidationService facadeValidationService;

	@Override
	public SyncItemStatusData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemSynchronizationData itemSynchronizationData)
	{
		return getSynchronizationItemStatus(syncRequestData, itemSynchronizationData, getSyncItemStatusConfig());
	}

	@Override
	public SyncItemStatusData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemSynchronizationData itemSynchronizationData,
			final SyncItemStatusConfig config)
	{
		checkArgument(config.getMaxDepth() != 0, "Max Depth attribute should not be null.");
		checkArgument(config.getMaxDepth() > 0, "Max Depth attribute should be greater than zero.");

		final ItemModel item = getItem(itemSynchronizationData);

		return getSynchronizationItemStatus(syncRequestData, item, config, 1);
	}

	@Override
	public void performItemSynchronization(final SyncRequestData syncRequestData, final SynchronizationData synchronizationData)
	{
		performItemSynchronization(syncRequestData, synchronizationData, getSyncConfig());
	}

	@Override
	public void performItemSynchronization(final SyncRequestData syncRequestData, final SynchronizationData synchronizationData, final SyncConfig config)
	{
		getFacadeValidationService().validate(getCatalogSynchronizationCompositeValidator(), syncRequestData);

		final List<ItemModel> items = getListOfItems(synchronizationData);

		getItemSynchronizationService().performItemSynchronization(syncRequestData, items, config);

	}

	/**
	 * Will convert a list of itemId/itemType pairs to a list of {@link ItemModel}
	 * @param synchronizationData the list of {@link ItemSynchronizationData} candidates on which to perform synchronization
	 * @return the list of {@link ItemModel} to be synchronized
	 */
	protected List<ItemModel> getListOfItems(final SynchronizationData synchronizationData)
	{
		return synchronizationData.getItems().stream().map(itemSynchronizationData -> {

			return getItem(itemSynchronizationData);

		}).collect(toList());
	}

	/**
	 * Will convert a itemId/itemType pair to a of {@link ItemModel}
	 * @param itemSynchronizationData the {@link ItemSynchronizationData} candidate on which to perform synchronization
	 * @return the {@link ItemModel} to be synchronized
	 */
	protected ItemModel getItem(final ItemSynchronizationData itemSynchronizationData)
	{
		final ItemData itemData = new ItemData();
		itemData.setItemId(itemSynchronizationData.getItemId());
		itemData.setItemType(itemSynchronizationData.getItemType());

		return getUniqueItemIdentifierService().getItemModel(itemData).get();
	}

	/**
	 * Gets the synchronization status by calling {@link ItemSynchronizationService#getSynchronizationItemStatus(SyncRequestData, ItemModel)}.
	 * It also converts the result into an instance of {@link SyncItemStatusData} that will be then post populated by recursive calls to this method.
	 *
	 * @param syncRequestData the synchronization request data containing the source and target catalog versions.
	 * @param item the item model currently being inspected
	 * @param config the configuration for getting the synchronization status
	 * @param level the current level on the data structure.
	 * @return the {@link SyncItemStatusData} information about a given item model and all related items collected during execution.
	 */
	protected SyncItemStatusData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemModel item,
			final SyncItemStatusConfig config, final Integer level)
	{
		if (level > config.getMaxDepth())
		{
			return null;
		}
		// get synchronization status for the current item and all related items
		final SynchronizationItemDetailsData synchronizationItemDetails = getItemSynchronizationService()
				.getSynchronizationItemStatus(syncRequestData, item);

		final SyncItemStatusData syncItemStatusData = getSyncItemStatusConverter().convert(synchronizationItemDetails);

		syncItemStatusData.setUnavailableDependencies(findUnavailableDependencies(syncRequestData, item, level));

		syncItemStatusData.setSelectedDependencies(collectItemsAndGetSynchronizationItemStatus(syncRequestData, item, config,
				level + 1, getBasicItemCollectorRegistry()));

		syncItemStatusData.setSharedDependencies(collectItemsAndGetSynchronizationItemStatus(syncRequestData, item, config,
				level + 1, getSharedItemCollectorRegistry()));

		return syncItemStatusData;
	}

	/**
	 * Find all dependent items which have never been synchronized to the target catalog.
	 * 
	 * @param syncRequestData the synchronization request data containing the source and target catalog versions.
	 * @param item the item model currently being inspected
	 * @param level the current level on the data structure
	 * @return a list of {@link SyncItemStatusData} for never-synchronized dependent items 
	 */
	protected List<SyncItemStatusData> findUnavailableDependencies(final SyncRequestData syncRequestData, final ItemModel item,
			final Integer level)
	{
		final SyncItemStatusConfig parentConfig = new SyncItemStatusConfig();
		parentConfig.setMaxDepth(1);
		final List<SyncItemStatusData> parentDependencies = collectItemsAndGetSynchronizationItemStatus(syncRequestData, item,
				parentConfig, level, getDependentItemCollectorRegistry());
		return parentDependencies.stream()
				.filter(syncData -> syncData.getLastSyncStatus() == null)
				.collect(Collectors.toList());
	}

	/**
	 * Collects the items that also requires information about the synchronization status details and make more calls to
	 * {@link DefaultItemSynchronizationFacade#getSynchronizationItemStatus(SyncRequestData, ItemModel, SyncItemStatusConfig, Integer)}
	 * from individual items that were collected.
	 *
	 * @param syncRequestData the synchronization request data containing the source and target catalog versions.
	 * @param item the item model currently being inspected
	 * @param config the configuration for getting the synchronization status
	 * @param level the current level on the data structure.
	 * @param collectorRegistry the collector registry to get an instance of {@link de.hybris.platform.cmsfacades.common.itemcollector.ItemCollector}
	 * @return a list of {@link SyncItemStatusData} beans collected and converted recursively.
	 */
	protected List<SyncItemStatusData> collectItemsAndGetSynchronizationItemStatus(final SyncRequestData syncRequestData,
			final ItemModel item, final SyncItemStatusConfig config, final Integer level,
			final ItemCollectorRegistry collectorRegistry)
	{
		@SuppressWarnings("unchecked")
		final List<ItemModel> itemsCollected = collectorRegistry.getItemCollector(item) //
		.map(itemCollector -> itemCollector.collect(item)) //
		.orElse(new ArrayList<ItemModel>());

		// the function to be executed on the stream
		final Function<ItemModel, SyncItemStatusData> performGetSynchronizationStatus = itemModel -> getSynchronizationItemStatus(
				syncRequestData, itemModel, config, level);

		return itemsCollected.stream().map(performGetSynchronizationStatus).collect(toList());
	}

	/**
	 * Get the fork/join pool for the current node initialized with the runtime number of available processors.
	 * @return the fork/join pool
	 */
	protected ForkJoinPool getForkJoinPool()
	{
		return new ForkJoinPool(Runtime.getRuntime().availableProcessors());
	}


	protected ItemCollectorRegistry getBasicItemCollectorRegistry()
	{
		return basicItemCollectorRegistry;
	}

	@Required
	public void setBasicItemCollectorRegistry(final ItemCollectorRegistry basicItemCollectorRegistry)
	{
		this.basicItemCollectorRegistry = basicItemCollectorRegistry;
	}

	protected ItemCollectorRegistry getSharedItemCollectorRegistry()
	{
		return sharedItemCollectorRegistry;
	}

	@Required
	public void setSharedItemCollectorRegistry(final ItemCollectorRegistry sharedItemCollectorRegistry)
	{
		this.sharedItemCollectorRegistry = sharedItemCollectorRegistry;
	}

	protected ItemCollectorRegistry getDependentItemCollectorRegistry()
	{
		return dependentItemCollectorRegistry;
	}

	@Required
	public void setDependentItemCollectorRegistry(final ItemCollectorRegistry dependentItemCollectorRegistry)
	{
		this.dependentItemCollectorRegistry = dependentItemCollectorRegistry;
	}

	@Required
	public void setItemSynchronizationService(final ItemSynchronizationService itemSynchronizationService)
	{
		this.itemSynchronizationService = itemSynchronizationService;
	}

	protected ItemSynchronizationService getItemSynchronizationService()
	{
		return itemSynchronizationService;
	}

	protected Converter<SynchronizationItemDetailsData, SyncItemStatusData> getSyncItemStatusConverter()
	{
		return syncItemStatusConverter;
	}

	@Required
	public void setSyncItemStatusConverter(final Converter<SynchronizationItemDetailsData, SyncItemStatusData> syncItemStatusConverter)
	{
		this.syncItemStatusConverter = syncItemStatusConverter;
	}

	protected SyncItemStatusConfig getSyncItemStatusConfig()
	{
		return syncItemStatusConfig;
	}

	@Required
	public void setSyncItemStatusConfig(final SyncItemStatusConfig syncItemStatusConfig)
	{
		this.syncItemStatusConfig = syncItemStatusConfig;
	}

	@Required
	public void setSyncConfig(final SyncConfig syncConfig)
	{
		this.syncConfig = syncConfig;
	}

	protected SyncConfig getSyncConfig()
	{
		return syncConfig;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	protected Validator getCatalogSynchronizationCompositeValidator()
	{
		return catalogSynchronizationCompositeValidator;
	}

	@Required
	public void setCatalogSynchronizationCompositeValidator(
			final Validator catalogSynchronizationCompositeValidator)
	{
		this.catalogSynchronizationCompositeValidator = catalogSynchronizationCompositeValidator;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}
}
