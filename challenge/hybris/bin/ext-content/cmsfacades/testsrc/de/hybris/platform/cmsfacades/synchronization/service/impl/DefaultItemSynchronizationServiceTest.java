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

import static de.hybris.platform.catalog.enums.SyncItemStatus.IN_SYNC;
import static de.hybris.platform.catalog.enums.SyncItemStatus.NOT_APPLICABLE;
import static de.hybris.platform.catalog.enums.SyncItemStatus.NOT_SYNC;
import static de.hybris.platform.core.PK.fromLong;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
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
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.collector.RelatedItemsCollector;
import de.hybris.platform.servicelayer.session.MockSessionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemSynchronizationServiceTest
{
	
	@Spy
	private MockSessionService mockSessionService = new MockSessionService();
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private RelatedItemsCollector relatedItemsCollector;
	@Mock
	private SynchronizationStatusService platformSynchronizationStatusService;
	@Mock
	private CatalogSynchronizationService catalogSynchronizationService;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	
	@InjectMocks
	private DefaultItemSynchronizationService itemSynchronizationService;

	
	private 	List<ItemModel> relatedItems;

	@Mock
	private ItemModel item;
	private PK itemPK =  fromLong(123);

	@Mock
	private ItemModel relatedItem1;
	private PK relatedItem1PK =  fromLong(1234);

	@Mock
	private ItemModel relatedItem2;	
	private PK relatedItem2PK =  fromLong(12345);

	private String catalogId = "theCatalogId";
	
	@Mock
	private CatalogVersionModel sourceVersion;
	private String sourceVersionId = "sourceVersionId";

	@Mock
	private CatalogVersionModel targetVersion;
	private String targetVersionId = "targetVersionId";

	private SyncRequestData syncRequestData;

	@Mock
	private SyncItemJobModel targetToSourceJob;
	@Mock
	private SyncItemJobModel sourceToTargetJob;
	@Mock
	private SyncItemJobModel wrongJob2;
	@Mock
	private SyncItemJobModel wrongJob3;
	
	@Mock
	private SyncItemInfo syncItemInfo;
	private PK lastSyncTimePK = fromLong(123456);
	@Mock
	private ItemSyncTimestampModel timeStamp;
	@Mock
	private Date lastSyncTime;
	
	@Mock
	private SyncItemInfo syncRelatedItem1Info;
	@Mock
	private SyncItemInfo syncRelatedItem2Info;
	
	@Mock
	private SyncConfig config;
	
	@Captor
	private ArgumentCaptor<List<ItemModel>> itemListCaptor;

	@Before
	public void setUp(){
		
		syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);

		when(sourceVersion.getVersion()).thenReturn(sourceVersionId);
		when(targetVersion.getVersion()).thenReturn(targetVersionId);

		when(catalogVersionService.getCatalogVersion(catalogId, sourceVersionId)).thenReturn(sourceVersion);
		when(catalogVersionService.getCatalogVersion(catalogId, targetVersionId)).thenReturn(targetVersion);
		
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item)).thenReturn(sourceVersion);
		
		when(targetToSourceJob.getSourceVersion()).thenReturn(targetVersion);
		when(targetToSourceJob.getTargetVersion()).thenReturn(sourceVersion);

		when(sourceToTargetJob.getSourceVersion()).thenReturn(sourceVersion);
		when(sourceToTargetJob.getTargetVersion()).thenReturn(targetVersion);
		
		when(wrongJob2.getSourceVersion()).thenReturn(sourceVersion);
		when(wrongJob2.getTargetVersion()).thenReturn(sourceVersion);
		
		when(wrongJob3.getSourceVersion()).thenReturn(targetVersion);
		when(wrongJob3.getTargetVersion()).thenReturn(targetVersion);

		when(platformSynchronizationStatusService.getInboundSynchronizations(item)).thenReturn(asList(targetToSourceJob, sourceToTargetJob, wrongJob2, wrongJob3));
		when(platformSynchronizationStatusService.getOutboundSynchronizations(item)).thenReturn(asList(targetToSourceJob, sourceToTargetJob, wrongJob2, wrongJob3));
		
		when(item.getPk()).thenReturn(itemPK);
		when(relatedItem1.getPk()).thenReturn(relatedItem1PK);
		when(relatedItem2.getPk()).thenReturn(relatedItem2PK);
		relatedItems = asList(item, relatedItem1, relatedItem2);
		
		when(relatedItemsCollector.collect(eq(item), anyMapOf(String.class, Object.class))).thenReturn(relatedItems);
		when(sessionSearchRestrictionsDisabler.execute(any())).thenReturn(relatedItems);
	
		when(syncItemInfo.getSyncStatus()).thenReturn(NOT_SYNC);
		when(syncItemInfo.getSyncTimestampPk()).thenReturn(lastSyncTimePK);
		when(modelService.get(lastSyncTimePK)).thenReturn(timeStamp);
		when(timeStamp.getLastSyncTime()).thenReturn(lastSyncTime);;
		
		when(syncRelatedItem1Info.getSyncStatus()).thenReturn(IN_SYNC);
		when(syncRelatedItem1Info.getItemPk()).thenReturn(relatedItem1PK);
		
		when(syncRelatedItem2Info.getSyncStatus()).thenReturn(NOT_SYNC);
		when(syncRelatedItem2Info.getItemPk()).thenReturn(relatedItem2PK);

		
		when(platformSynchronizationStatusService.getSyncInfo(relatedItems, sourceToTargetJob)).thenReturn(new ArrayList<SyncItemInfo>(asList(syncItemInfo, syncRelatedItem1Info, syncRelatedItem2Info)));
		when(platformSynchronizationStatusService.getSyncInfo(relatedItems, targetToSourceJob)).thenReturn(new ArrayList<SyncItemInfo>(asList(syncItemInfo, syncRelatedItem1Info, syncRelatedItem2Info)));
	
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void willTrowExceptionIfSyncRequestDataNotProvided(){
		
		itemSynchronizationService.getSynchronizationItemStatus(null, item);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void willTrowExceptionIfItemNotProvided(){
		
		itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, null);
	}
		
	@Test
	public void whenSourceVersionIsItemVersion_getSyncStatus_Use_OutboundSynchronizations_and_returns_aggregated_status_of_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);
		//prepare
		syncRequestData.setSourceVersionId(sourceVersionId);
		syncRequestData.setTargetVersionId(targetVersionId);

		//execute
		SynchronizationItemDetailsData synchronizationItemStatus = itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, item);
		
		//assert
		assertThat(synchronizationItemStatus.getItem(), is(item));
		assertThat(synchronizationItemStatus.getCatalogId(), is(catalogId));
		assertThat(synchronizationItemStatus.getSourceVersionId(), is(sourceVersionId));
		assertThat(synchronizationItemStatus.getTargetVersionId(), is(targetVersionId));
		assertThat(synchronizationItemStatus.getSyncStatus(), is(NOT_SYNC.name()));
		assertThat(synchronizationItemStatus.getLastSyncStatusDate(), is(lastSyncTime));
		
		List<SyncItemInfoJobStatusData> relatedItemStatuses = synchronizationItemStatus.getRelatedItemStatuses();
		assertThat(relatedItemStatuses.size(), is(1));
		assertThat(relatedItemStatuses.get(0).getItem(), is(relatedItem2));
		assertThat(relatedItemStatuses.get(0).getSyncStatus(), is(NOT_SYNC.name()));
		
		
		//verify
		verify(platformSynchronizationStatusService, times(1)).getOutboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getInboundSynchronizations(any(ItemModel.class));
		
		verify(platformSynchronizationStatusService, times(1)).getSyncInfo(relatedItems, sourceToTargetJob);
		
		verify(searchRestrictionService, times(1)).disableSearchRestrictions();
		verify(searchRestrictionService, times(1)).enableSearchRestrictions();
	}

	@Test
	public void shouldFilterNotApplicableStatus()
	{
		//prepare
		when(syncItemInfo.getSyncStatus()).thenReturn(IN_SYNC);
		when(syncRelatedItem1Info.getSyncStatus()).thenReturn(IN_SYNC);
		when(syncRelatedItem2Info.getSyncStatus()).thenReturn(NOT_APPLICABLE);
		syncRequestData.setSourceVersionId(sourceVersionId);
		syncRequestData.setTargetVersionId(targetVersionId);

		//execute
		SynchronizationItemDetailsData synchronizationItemStatus = itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, item);

		assertThat(synchronizationItemStatus.getSyncStatus(), is(IN_SYNC.name()));
	}
	
	@Test
	public void whenSourceVersionIsNotItemVersion_getSyncStatus_Use_InboundSynchronizations_and_returns_aggregated_status_of_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(false);
		
		//prepare
		syncRequestData.setSourceVersionId(targetVersionId);
		syncRequestData.setTargetVersionId(sourceVersionId);
		
		//execute
		SynchronizationItemDetailsData synchronizationItemStatus = itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, item);
		
		//assert
		assertThat(synchronizationItemStatus.getItem(), is(item));
		assertThat(synchronizationItemStatus.getCatalogId(), is(catalogId));
		assertThat(synchronizationItemStatus.getSourceVersionId(), is(targetVersionId));
		assertThat(synchronizationItemStatus.getTargetVersionId(), is(sourceVersionId));
		assertThat(synchronizationItemStatus.getSyncStatus(), is(NOT_SYNC.name()));
		
		List<SyncItemInfoJobStatusData> relatedItemStatuses = synchronizationItemStatus.getRelatedItemStatuses();
		assertThat(relatedItemStatuses.size(), is(1));
		assertThat(relatedItemStatuses.get(0).getItem(), is(relatedItem2));
		assertThat(relatedItemStatuses.get(0).getSyncStatus(), is(NOT_SYNC.name()));
		
		
		//verify
		verify(platformSynchronizationStatusService, times(1)).getInboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getOutboundSynchronizations(any(ItemModel.class));
		
		verify(platformSynchronizationStatusService, times(1)).getSyncInfo(relatedItems, targetToSourceJob);
		
		verify(searchRestrictionService, never()).disableSearchRestrictions();
		verify(searchRestrictionService, never()).enableSearchRestrictions();
		
	}
	
	@Test
	public void whenSourceVersionIsItemVersion_performSync_Use_OutboundSynchronizations_and_performs_on_all_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);
		
		//prepare
		syncRequestData.setSourceVersionId(sourceVersionId);
		syncRequestData.setTargetVersionId(targetVersionId);
		
		//execute
		itemSynchronizationService.performItemSynchronization(syncRequestData, asList(item, relatedItem2), config);
		
		//assert
		verify(catalogSynchronizationService, times(1)).performSynchronization(itemListCaptor.capture(), eq(sourceToTargetJob), eq(config));
		
		assertThat(itemListCaptor.getValue(), containsInAnyOrder(item, relatedItem1, relatedItem2));
		
		//verify
		
		verify(platformSynchronizationStatusService, times(1)).getOutboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getInboundSynchronizations(any(ItemModel.class));

		verify(searchRestrictionService, times(1)).disableSearchRestrictions();
		verify(searchRestrictionService, times(1)).enableSearchRestrictions();

	}

	@Test
	public void whenSourceVersionIsNotItemVersion_performSync_Use_OutboundSynchronizations_and_performs_on_all_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(false);
		//prepare
		syncRequestData.setSourceVersionId(targetVersionId);
		syncRequestData.setTargetVersionId(sourceVersionId);
		
		//execute
		itemSynchronizationService.performItemSynchronization(syncRequestData, asList(item, relatedItem1), config);
		
		//assert
		verify(catalogSynchronizationService, times(1)).performSynchronization(itemListCaptor.capture(), eq(targetToSourceJob), eq(config));
		
		assertThat(itemListCaptor.getValue(), containsInAnyOrder(item, relatedItem1, relatedItem2));

		//verify
		
		verify(platformSynchronizationStatusService, times(1)).getInboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getOutboundSynchronizations(any(ItemModel.class));

		verify(searchRestrictionService, never()).disableSearchRestrictions();
		verify(searchRestrictionService, never()).enableSearchRestrictions();

	}

}
