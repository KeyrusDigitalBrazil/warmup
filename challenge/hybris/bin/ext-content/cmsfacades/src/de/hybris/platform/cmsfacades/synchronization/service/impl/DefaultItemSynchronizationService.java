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
package de.hybris.platform.cmsfacades.synchronization.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.catalog.enums.SyncItemStatus.*;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_LOCALES;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_TARGET_CATALOG_VERSION;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ItemSyncTimestampModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.catalog.synchronization.SyncItemInfo;
import de.hybris.platform.catalog.synchronization.SynchronizationStatusService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cmsfacades.data.SyncItemInfoJobStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationItemDetailsData;
import de.hybris.platform.cmsfacades.synchronization.service.ItemSynchronizationService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.collector.RelatedItemsCollector;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Sets;


/**
 * Default implementation of {@link ItemSynchronizationService}
 */
public class DefaultItemSynchronizationService implements ItemSynchronizationService
{
	private SearchRestrictionService searchRestrictionService;
	private SessionService sessionService;
	private ModelService modelService;
	private CatalogTypeService catalogTypeService;
	private CatalogVersionService catalogVersionService;
	private RelatedItemsCollector relatedItemsCollector;
	private SynchronizationStatusService platformSynchronizationStatusService;
	private CatalogSynchronizationService catalogSynchronizationService;
	private CommonI18NService commonI18NService;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	/**
	 * Gets the synchronization item status for a given {@link ItemModel} taking in consideration the source and target
	 * catalog versions. Implementations should be able to provide details of the synchronization status of related items
	 * as well.
	 *
	 * @param syncRequestData
	 * 		the synchronization request containing the source and target catalog versions
	 * @param item
	 * 		the item model we are interested in getting the synchronization status
	 * @return an instance of {@link SynchronizationItemDetailsData}, never {@code null}.
	 * @throws IllegalArgumentException
	 * 		when any of the given parameters is {@code null}.
	 */
	@Override
	public SynchronizationItemDetailsData getSynchronizationItemStatus(final SyncRequestData syncRequestData, final ItemModel item)
	{

		checkArgument(syncRequestData != null, "syncRequestData cannot be null");
		checkArgument(item != null, "item cannot be null");

		final SyncItemJobModel syncItemJobModel = getRelevantSyncItemJob(syncRequestData, item);

		final List<ItemModel> relatedItems = getSessionSearchRestrictionsDisabler().execute(() ->
				collectRelatedItems(item, getItemCollectorContext(syncItemJobModel)));

		final List<SyncItemInfo> syncInfo = getPlatformSynchronizationStatusService().getSyncInfo(relatedItems, syncItemJobModel);

		final SyncItemStatus rootSyncStatus = aggregatedStatusFromRelatedItems(syncInfo);
		final SyncItemInfo rootSyncInfo = syncInfo.remove(0);
		rootSyncInfo.setSyncStatus(rootSyncStatus);

		return buildData(syncRequestData, item, relatedItems, syncInfo, rootSyncInfo);
	}

	/**
	 * Calculate an aggregated {@link SyncItemStatus} from a given list of {@link SyncItemInfo}
	 *
	 * @param syncInfo
	 * 		the list of {@link SyncItemInfo}, including the root syncItemInfo
	 */
	protected SyncItemStatus aggregatedStatusFromRelatedItems(final List<SyncItemInfo> syncInfo)
	{
		SyncItemStatus rootSyncStatus = NOT_SYNC;
		final boolean isRootInSync = syncInfo.stream().filter(syncItemInfo -> !syncItemInfo.getSyncStatus().equals(NOT_APPLICABLE))
				.allMatch(syncItemInfo -> syncItemInfo.getSyncStatus().equals(IN_SYNC));
		if (isRootInSync)
		{
			rootSyncStatus = IN_SYNC;
		}
		else
		{
			final boolean isInProgress = syncInfo.stream()
					.anyMatch(syncItemInfo -> syncItemInfo.getSyncStatus().equals(IN_PROGRESS));
			if (isInProgress)
			{
				rootSyncStatus = IN_PROGRESS;
			}
		}
		return rootSyncStatus;
	}

	/**
	 * performs the synchronization of a list of {@link ItemModel} taking in consideration the source and target catalog
	 * versions. Implementations should be able to provide details of the synchronization status of related items as
	 * well.
	 *
	 * @param syncRequestData
	 * 		the synchronization request containing the source and target catalog versions
	 * @param items
	 * 		the list of item models that we want to synchronize
	 * @param config
	 * 		the synchronization configuration . See {@link SyncConfig} for more details.
	 */
	/*
	 * Suppress sonar warning (squid:S2259 | Null pointers should not be dereferenced) : "items" is already validated not
	 * to be null.
	 */
	@SuppressWarnings("squid:S2259")
	@Override
	public void performItemSynchronization(final SyncRequestData syncRequestData, final List<ItemModel> items,
			final SyncConfig config)
	{
		checkArgument(items != null && !items.isEmpty(), "items must neither be null nor empty");

		final SyncItemJobModel syncItemJob = getRelevantSyncItemJob(syncRequestData, items.get(0));

		final Map<String, Object> context = getItemCollectorContext(syncItemJob);

		final Set<ItemModel> itemSet = Sets.newHashSet();
		items.forEach(item -> itemSet.addAll(collectRelatedItems(item, context)));
		getCatalogSynchronizationService().performSynchronization(newArrayList(itemSet), syncItemJob, config);
	}

	/**
	 * Gets the item collector configuration context.
	 * It sets {@code CTX_LOCALES} attribute as a collection of locales that are common for both source and target catalog versions.
	 * It also sets {@code VISITORS_CTX_TARGET_CATALOG_VERSION} attribute as a target catalog version that is used in
	 * synchronization process.
	 *
	 * @param syncItemJob
	 * 		the sync item job being investigated.
	 * @return the collector configuration context
	 */
	protected Map<String, Object> getItemCollectorContext(final SyncItemJobModel syncItemJob)
	{
		final Map<String, Object> context = new HashMap<>();
		context.put(VISITORS_CTX_LOCALES, syncItemJob.getEffectiveSyncLanguages() //
				.stream() //
				.map(languageModel -> getCommonI18NService().getLocaleForLanguage(languageModel)) //
				.collect(Collectors.toList()));

		context.put(VISITORS_CTX_TARGET_CATALOG_VERSION, syncItemJob.getTargetVersion());

		return context;
	}


	/**
	 * Retrieves the list of {@link ItemModel} related to an item of which we want to get the synchronization status
	 *
	 * @param item
	 * 		the item of which we want to retrieve the related items from a synchronization perspective
	 * @param context
	 * 		the configuration context in which the item collector will be executed
	 * @return
	 */
	protected List<ItemModel> collectRelatedItems(final ItemModel item, final Map<String, Object> context)
	{
		return getRelatedItemsCollector().collect(item, context);
	}

	/**
	 * Will retrieve from the {@link CatalogVersionModel} of the {@link ItemModel} the {@link SyncItemJobModel} matching
	 * the request synchronization direction
	 *
	 * @param syncRequestData
	 * 		the requested direction for the synchronization from a catalog version perspective
	 * @param item
	 * 		the catalog aware item from which a catalog version is extracted
	 * @return
	 */
	protected SyncItemJobModel getRelevantSyncItemJob(final SyncRequestData syncRequestData, final ItemModel item)
	{

		final boolean isOutbound = isOutboundSynchronization(syncRequestData, item);

		final List<SyncItemJobModel> synchronizations;
		if (isOutbound)
		{
			synchronizations = getPlatformSynchronizationStatusService().getOutboundSynchronizations(item);
		}
		else
		{
			synchronizations = getPlatformSynchronizationStatusService().getInboundSynchronizations(item);
		}

		//select synchronization matching source and target catalog versions
		return synchronizations.stream()
				.filter(job -> job.getSourceVersion().getVersion().equals(syncRequestData.getSourceVersionId())
						&& job.getTargetVersion().getVersion().equals(syncRequestData.getTargetVersionId())

				).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(format(
						"No SyncItemJobModel was found from %s to %s versions for catalog id %s", syncRequestData.getSourceVersionId(),
						syncRequestData.getTargetVersionId(), syncRequestData.getCatalogId())));
	}

	/**
	 * Determines whether the requested synchronisation status is outbound or inbound based on the active flag of
	 * {@link CatalogVersionModel} (sees {@link SynchronizationStatusService#getOutboundSynchronizations(ItemModel)} and
	 * {@link SynchronizationStatusService#getInboundSynchronizations(ItemModel)}) An outbound synchronization is from a
	 * non active source version to an active target version. An inbound synchronization is from an active source version
	 * to a non active target version.
	 *
	 * @param syncRequestData
	 * 		the requested direction for the synchronization from a catalog version perspective
	 * @return boolean stating whether the synchronization is outbound
	 * @throws IllegalArgumentException
	 * 		if the direction could not be determined because of both catalog versions being active or inactive
	 */
	protected synchronized boolean isOutboundSynchronization(final SyncRequestData syncRequestData, final ItemModel item)
	{

		return (Boolean) getSessionService().executeInLocalView(new SessionExecutionBody()
		{

			@Override
			public Object execute()
			{
				boolean wasEnabled = false;
				try
				{
					wasEnabled = getSearchRestrictionService().isSearchRestrictionsEnabled();

					if (wasEnabled)
					{
						getSearchRestrictionService().disableSearchRestrictions();
					}
					final CatalogVersionModel sourceVersion = getCatalogVersionService()
							.getCatalogVersion(syncRequestData.getCatalogId(), syncRequestData.getSourceVersionId());
					final CatalogVersionModel itemCatalogVersion = getCatalogTypeService()
							.getCatalogVersionForCatalogVersionAwareModel(item);

					return ObjectUtils.equals(sourceVersion, itemCatalogVersion);
				}
				finally
				{
					if (wasEnabled)
					{
						getSearchRestrictionService().enableSearchRestrictions();
					}
				}
			}

		});
	}

	/**
	 * @param syncRequestData
	 * 		the requested direction for the synchronization from a catalog version perspective
	 * @param item
	 * 		the catalog aware item for which a synchronization status is returned
	 * @param relatedItems
	 * 		the collected related items to the main item
	 * @param syncInfo
	 * 		the list of {@link SyncItemInfo} for the given relatedItems
	 * @param rootSyncInfo
	 * 		the {@link SyncItemInfo} of the main item
	 * @return SynchronizationItemDetailsData
	 */
	protected SynchronizationItemDetailsData buildData(final SyncRequestData syncRequestData, final ItemModel item,
			final List<ItemModel> relatedItems, final List<SyncItemInfo> syncInfo, final SyncItemInfo rootSyncInfo)
	{
		final Map<PK, ItemModel> relatedItemsMap = relatedItems.stream().collect(toMap(e -> e.getPk(), e -> e));

		final SynchronizationItemDetailsData data = new SynchronizationItemDetailsData();

		if (rootSyncInfo.getSyncTimestampPk() != null)
		{
			final ItemSyncTimestampModel timestamp = getModelService().get(rootSyncInfo.getSyncTimestampPk());
			data.setLastSyncStatusDate(timestamp.getLastSyncTime());
		}
		data.setItem(item);
		data.setCatalogId(syncRequestData.getCatalogId());
		data.setSourceVersionId(syncRequestData.getSourceVersionId());
		data.setTargetVersionId(syncRequestData.getTargetVersionId());
		data.setSyncStatus(rootSyncInfo.getSyncStatus().name());

		data.setRelatedItemStatuses(
				syncInfo.stream().filter(syncItemInfo -> !syncItemInfo.getSyncStatus().equals(IN_SYNC)).map(syncItemInfo ->
				{

					final SyncItemInfoJobStatusData syncItemInfoJobStatusData = new SyncItemInfoJobStatusData();
					syncItemInfoJobStatusData.setItem(relatedItemsMap.get(syncItemInfo.getItemPk()));
					syncItemInfoJobStatusData.setSyncStatus(syncItemInfo.getSyncStatus().name());

					return syncItemInfoJobStatusData;

				}).collect(toList()));
		return data;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setRelatedItemsCollector(final RelatedItemsCollector relatedItemsCollector)
	{
		this.relatedItemsCollector = relatedItemsCollector;
	}

	protected RelatedItemsCollector getRelatedItemsCollector()
	{
		return relatedItemsCollector;
	}

	@Required
	public void setPlatformSynchronizationStatusService(
			final de.hybris.platform.catalog.synchronization.SynchronizationStatusService platformSynchronizationStatusService)
	{
		this.platformSynchronizationStatusService = platformSynchronizationStatusService;
	}

	protected de.hybris.platform.catalog.synchronization.SynchronizationStatusService getPlatformSynchronizationStatusService()
	{
		return platformSynchronizationStatusService;
	}

	@Required
	public void setCatalogSynchronizationService(final CatalogSynchronizationService catalogSynchronizationService)
	{
		this.catalogSynchronizationService = catalogSynchronizationService;
	}

	protected CatalogSynchronizationService getCatalogSynchronizationService()
	{
		return catalogSynchronizationService;
	}

	@Required
	public void setCatalogTypeService(final CatalogTypeService catalogTypeService)
	{
		this.catalogTypeService = catalogTypeService;
	}

	protected CatalogTypeService getCatalogTypeService()
	{
		return catalogTypeService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	@Required
	public SessionService getSessionService()
	{
		return sessionService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(
			final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
	}
}
