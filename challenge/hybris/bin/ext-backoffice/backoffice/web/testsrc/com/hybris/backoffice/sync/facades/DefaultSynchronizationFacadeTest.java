/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.sync.facades;

import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_CREATE_SAVED_VALUES;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_ERROR_MODE;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_FORCE_UPDATE;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_KEEP_CRON_JOB;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_LOG_LEVEL_DATABASE;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_LOG_LEVEL_FILE;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_LOG_TO_DATABASE;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_LOG_TO_FILE;
import static com.hybris.backoffice.sync.facades.DefaultSynchronizationFacade.SYNC_CONFIG_SYNCHRONOUS;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.SyncItemStatus;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ItemSyncTimestampModel;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncScheduleMediaModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.catalog.synchronization.SyncResult;
import de.hybris.platform.catalog.synchronization.SynchronizationStatusService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.ErrorMode;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.collector.RelatedItemsCollector;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.backoffice.sync.PartialSyncInfo;
import com.hybris.backoffice.sync.SyncTask;


@UnitTest
public class DefaultSynchronizationFacadeTest
{

	private final ItemModel item1 = mock(ItemModel.class);
	private final ItemModel item2 = mock(ItemModel.class);
	private final ItemModel item3 = mock(ItemModel.class);
	private final SyncItemJobModel jobA = mock(SyncItemJobModel.class);
	private final SyncItemJobModel jobB = mock(SyncItemJobModel.class);
	private final SyncItemJobModel jobC = mock(SyncItemJobModel.class);
	private final SyncItemJobModel jobD = mock(SyncItemJobModel.class);

	@InjectMocks
	@Spy
	private DefaultSynchronizationFacade facade;
	@Mock
	private CatalogSynchronizationService catalogSynchronizationService;
	@Mock
	private SynchronizationStatusService synchronizationStatusService;
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private RelatedItemsCollector relatedItemsCollector;
	@Mock
	private ModelService modelService;
	@Mock
	private MediaService mediaService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private UserService userService;
	private UserModel currentUser;
	@Mock
	private UserGroupModel userGroup1;
	@Mock
	private UserGroupModel userGroup2;
	@Mock
	private UserGroupModel userGroup3;

	@Before
	public void before()
	{
		MockitoAnnotations.initMocks(this);
		when(catalogSynchronizationService.synchronize(any(SyncItemJobModel.class), any())).thenAnswer(invocationOnMock -> {

			final PK jobPK = ((SyncItemJobModel) invocationOnMock.getArguments()[0]).getPk();
			final SyncItemCronJobModel cj = mock(SyncItemCronJobModel.class);
			when(cj.getPk()).thenReturn(PK.fromLong(jobPK.getLongValue() + 1000));
			when(cj.getCode()).thenReturn(getCronJobCode(jobPK));
			final SyncResult syncResult = mock(SyncResult.class);
			when(syncResult.getCronJob()).thenReturn(cj);
			return syncResult;
		});

		when(catalogSynchronizationService.performSynchronization(anyList(), any(SyncItemJobModel.class), any()))
				.thenAnswer(invocationOnMock -> {
					final PK jobPK = ((SyncItemJobModel) invocationOnMock.getArguments()[1]).getPk();
					final SyncItemCronJobModel cj = mock(SyncItemCronJobModel.class);
					when(cj.getPk()).thenReturn(PK.fromLong(jobPK.getLongValue() + 1000));
					when(cj.getCode()).thenReturn(getCronJobCode(jobPK));
					final SyncResult syncResult = mock(SyncResult.class);
					when(syncResult.getCronJob()).thenReturn(cj);
					return syncResult;
				});
		doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]).when(facade).getCtxWithMaxRecursionDepth(any());

		currentUser = spy(new UserModel());
		BackofficeTestUtil.setPk(currentUser, 1);
		when(userService.getCurrentUser()).thenReturn(currentUser);
	}

	protected String getCronJobCode(final PK jobPK)
	{
		return getCronJobCode(jobPK.getLong().longValue());
	}

	protected String getCronJobCode(final long pk)
	{
		return "cronJobCode:" + PK.fromLong(pk).toString();
	}

	@Test
	public void testPerformCatalogSync()
	{
		// given
		final SyncItemJobModel syncItemJob = mockItemModelWithPK(100, SyncItemJobModel.class);
		final SyncTask syncTask = new SyncTask(syncItemJob);
		syncTask.setSyncConfig(new SyncConfig());
		// when
		final Optional<String> cronJobCode = facade.performSynchronization(syncTask);
		// then
		assertThat(cronJobCode.isPresent()).isTrue();
		assertThat(cronJobCode.get()).isEqualTo(getCronJobCode(100));
		verify(catalogSynchronizationService).synchronize(syncItemJob, syncTask.getSyncConfig());
	}

	@Test
	public void testPerformItemsSync()
	{
		// given
		doAnswer(inv -> inv.getArguments()[0]).when(facade).collectRelatedItems(any(), any());

		final List<ItemModel> items = mockItemsWithPK(2, 20, ItemModel.class);
		final SyncItemJobModel syncItemJob = mockItemModelWithPK(100, SyncItemJobModel.class);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));
		final SyncTask syncTask = new SyncTask(items, syncItemJob);
		syncTask.setSyncConfig(new SyncConfig());
		// when
		final Optional<String> cronJobCode = facade.performSynchronization(syncTask);
		// then
		assertThat(cronJobCode.isPresent()).isTrue();
		assertThat(cronJobCode.get()).isEqualTo(getCronJobCode(100));
		verify(catalogSynchronizationService).performSynchronization(items, syncItemJob, syncTask.getSyncConfig());
	}

	@Test
	public void testPerformItemsSyncWithRelatedItems()
	{
		// given
		final List<ItemModel> items = mockItemsWithPK(2, 20, ItemModel.class);
		final SyncItemJobModel syncItemJob = mockItemModelWithPK(100, SyncItemJobModel.class);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));
		final SyncConfig syncConfig = new SyncConfig();
		final SyncTask syncTask = new SyncTask(items, syncItemJob);
		syncTask.setSyncConfig(syncConfig);

		final List<ItemModel> relatedItems = mockItemsWithPK(10, 200, ItemModel.class);
		relatedItems.add(items.get(0));

		when(relatedItemsCollector.collect(items.get(0), Collections.emptyMap())).thenReturn(relatedItems);
		when(relatedItemsCollector.collect(items.get(1), Collections.emptyMap())).thenReturn(Lists.newArrayList(items.get(1)));
		// when
		final Optional<String> cronJobCode = facade.performSynchronization(syncTask);

		// then
		assertThat(cronJobCode.isPresent()).isTrue();
		assertThat(cronJobCode.get()).isEqualTo(getCronJobCode(100));
		verify(relatedItemsCollector).collect(items.get(0), Collections.emptyMap());
		verify(relatedItemsCollector).collect(items.get(1), Collections.emptyMap());

		final List<ItemModel> allItems = new ArrayList<>(relatedItems);
		allItems.add(items.get(1));
		verify(catalogSynchronizationService).performSynchronization(allItems, syncItemJob, syncConfig);
	}

	@Test
	public void testSyncStatusWithRelatedItems()
	{
		// given
		final ItemModel item1 = mockItemModelWithPK(21L, ItemModel.class);
		final ItemModel item2 = mockItemModelWithPK(22L, ItemModel.class);
		final List<ItemModel> item1RelatedItems = mockItemsWithPK(10, 200, ItemModel.class);

		final SyncItemJobModel syncJob = mock(SyncItemJobModel.class);

		final List<ItemModel> item1WithRelatedItems = Lists.newArrayList(item1RelatedItems);
		item1WithRelatedItems.add(item1);
		when(relatedItemsCollector.collect(eq(item1), anyMap())).thenReturn(item1WithRelatedItems);
		when(relatedItemsCollector.collect(eq(item2), anyMap())).thenReturn(Lists.newArrayList(item2));

		final List<ItemModel> itemGroup = new ArrayList<>(item1RelatedItems);
		itemGroup.add(item1);
		itemGroup.add(item2);
		doReturn(Boolean.TRUE).when(synchronizationStatusService).matchesSyncStatus(itemGroup, Lists.newArrayList(syncJob),
				SyncItemStatus.IN_SYNC);

		// when
		final Optional<Boolean> inSync = facade.isInSync(Lists.newArrayList(item1, item2), syncJob, Collections.emptyMap());

		// then
		assertThat(inSync.isPresent()).isTrue();
	}

	@Test
	public void shouldReturnEmptyOptionalWhenSynchronizationStatusServiceThrowsNpeForIsSyncForSingleItem()
	{
		// given
		final ItemModel itemModel = mockItemModelWithPK(21L, ItemModel.class);
		doReturn(Optional.of(new CatalogVersionModel())).when(facade).getSyncCatalogVersion(any());
		doReturn(Lists.newArrayList()).when(facade).getSynchronizations(any());
		doThrow(NullPointerException.class).when(synchronizationStatusService).matchesSyncStatus(any(), any(),
				eq(SyncItemStatus.IN_SYNC));

		// when
		final Optional<Boolean> inSync = facade.isInSync(itemModel, Collections.emptyMap());

		// then
		assertThat(inSync.isPresent()).isFalse();
	}

	@Test
	public void testItemIsInSyncTrue()
	{
		final ItemModel item1 = mockItemModelWithPK(21L, ItemModel.class);
		final List<ItemModel> item1RelatedItems = mockItemsWithPK(10, 200, ItemModel.class);
		final List<ItemModel> item1WithRelatedItems = mockRelatedItemsFor(item1, item1RelatedItems);

		mockFacadeInOutSynchronizationForItems(Lists.newArrayList(jobA), Lists.newArrayList(jobB), item1WithRelatedItems);
		doReturn(Boolean.TRUE).when(synchronizationStatusService).matchesSyncStatus(item1WithRelatedItems,
				Lists.newArrayList(jobA, jobB), SyncItemStatus.IN_SYNC);

		assertThat(facade.isInSync(item1, new HashMap<>()).get()).isTrue();
	}

	@Test
	public void testItemIsInSyncItemIsNew()
	{
		final ItemModel item1 = mockItemModelWithPK(21L, ItemModel.class);
		final List<ItemModel> item1RelatedItems = mockItemsWithPK(10, 200, ItemModel.class);
		final List<ItemModel> item1WithRelatedItems = mockRelatedItemsFor(item1, item1RelatedItems);

		mockFacadeInOutSynchronizationForItems(Lists.newArrayList(jobA), Lists.newArrayList(jobB), item1WithRelatedItems);
		doReturn(Boolean.TRUE).when(synchronizationStatusService).matchesSyncStatus(item1WithRelatedItems,
				Lists.newArrayList(jobA, jobB), SyncItemStatus.IN_SYNC);
		doReturn(Boolean.TRUE).when(modelService).isNew(item1);

		assertThat(facade.isInSync(item1, new HashMap<>()).isPresent()).isFalse();
	}

	@Test
	public void testSyncStatusNotAvailable()
	{
		final ItemModel item1 = mockItemModelWithPK(21L, ItemModel.class);

		doReturn(Boolean.FALSE).when(catalogTypeService).isCatalogVersionAwareModel(item1);

		assertThat(facade.isInSync(item1, new HashMap<>()).isPresent()).isFalse();
	}

	@Test
	public void testItemIsInSyncFalse()
	{
		final ItemModel item1 = mockItemModelWithPK(21L, ItemModel.class);
		final List<ItemModel> item1RelatedItems = mockItemsWithPK(10, 200, ItemModel.class);
		final List<ItemModel> item1WithRelatedItems = mockRelatedItemsFor(item1, item1RelatedItems);

		mockFacadeInOutSynchronizationForItems(Lists.newArrayList(jobA), Lists.newArrayList(jobB), item1WithRelatedItems);
		doReturn(Boolean.FALSE).when(synchronizationStatusService).matchesSyncStatus(item1WithRelatedItems,
				Lists.newArrayList(jobA, jobB), SyncItemStatus.IN_SYNC);

		assertThat(facade.isInSync(item1, new HashMap<>()).get()).isFalse();
	}

	protected void mockFacadeInOutSynchronizationForItems(final List<SyncItemJobModel> inboundJobs,
			final List<SyncItemJobModel> outboundJobs, final List<ItemModel> itemWithRelatedItems)
	{
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(any(ItemModel.class))).thenReturn(catalogVersion);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));

		final Optional<CatalogVersionModel> catalog = facade.getSyncCatalogVersion(itemWithRelatedItems);
		when(facade.getInboundSynchronizations(catalog.get())).thenReturn(Lists.newArrayList(inboundJobs));
		when(facade.getOutboundSynchronizations(catalog.get())).thenReturn(Lists.newArrayList(outboundJobs));
	}

	@Test
	public void testItemsCatalogVersionSync()
	{
		//given
		final List<CatalogVersionModel> catalogs = mockItemsWithPK(10, 0, CatalogVersionModel.class);
		final List<MediaModel> medias = mockItemsWithPK(10, 100, MediaModel.class);
		final List<ItemModel> mixed = new ArrayList<>(catalogs);
		mixed.addAll(medias);
		//when
		final List<CatalogVersionModel> itemsCatalogVersions = facade.getItemsCatalogVersions(mixed);
		//then
		assertThat(itemsCatalogVersions).containsOnly(catalogs.toArray());
	}

	@Test
	public void testIsInSync()
	{
		final List<ItemModel> items = mockEmptyRelatedItemsFor(item1);
		doReturn(Boolean.TRUE).when(synchronizationStatusService).matchesSyncStatus(items, Lists.newArrayList(jobA),
				SyncItemStatus.IN_SYNC);
		assertThat(facade.isInSync(Lists.newArrayList(item1), jobA, new HashMap<>()).get()).isTrue();
	}

	@Test
	public void testPartialSyncInfo()
	{
		//given
		final ItemModel product = mock(ProductModel.class);
		final ItemModel thumbnail = mock(MediaModel.class);

		final ArrayList<ItemModel> productAndThumbnail = Lists.newArrayList(product, thumbnail);
		when(relatedItemsCollector.collect(same(product), anyMap())).thenReturn(productAndThumbnail);


		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(product);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(thumbnail);

		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(product)).thenReturn(catalogVersion);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(thumbnail)).thenReturn(catalogVersion);

		final SyncItemJobModel inSyncJob = mock(SyncItemJobModel.class);
		final SyncItemJobModel notInSyncJob = mock(SyncItemJobModel.class);
		when(catalogVersion.getIncomingSynchronizations()).thenReturn(Lists.newArrayList(inSyncJob, notInSyncJob));
		when(catalogVersion.getSynchronizations()).thenReturn(Lists.newArrayList(inSyncJob, notInSyncJob));

		doReturn(Boolean.TRUE).when(synchronizationStatusService).matchesSyncStatus(productAndThumbnail,
				Lists.newArrayList(inSyncJob), SyncItemStatus.IN_SYNC);
		doReturn(Boolean.FALSE).when(synchronizationStatusService).matchesSyncStatus(productAndThumbnail,
				Lists.newArrayList(notInSyncJob), SyncItemStatus.IN_SYNC);

		//when
		final Optional<PartialSyncInfo> info = facade.getPartialSyncStatusInfo(product, SyncItemStatus.IN_SYNC, new HashMap<>());

		//then
		assertThat(info.isPresent()).isTrue();

		final PartialSyncInfo partialSyncInfo = info.get();
		assertThat(partialSyncInfo.getInboundSyncStatus()).hasSize(2);
		assertThat(partialSyncInfo.getInboundSyncStatus().get(inSyncJob)).isTrue();
		assertThat(partialSyncInfo.getInboundSyncStatus().get(notInSyncJob)).isFalse();

		assertThat(partialSyncInfo.getOutboundSyncStatus()).hasSize(2);
		assertThat(partialSyncInfo.getOutboundSyncStatus().get(inSyncJob)).isTrue();
		assertThat(partialSyncInfo.getOutboundSyncStatus().get(notInSyncJob)).isFalse();
	}

	@Test
	public void testIsInSyncEmptyList()
	{
		assertThat(facade.isInSync(Collections.emptyList(), jobA, new HashMap<>()).isPresent()).isFalse();
	}

	protected List<ItemModel> mockEmptyRelatedItemsFor(final ItemModel item)
	{
		return mockRelatedItemsFor(item, Collections.emptyList());
	}

	protected List<ItemModel> mockRelatedItemsFor(final ItemModel item, final List<ItemModel> relatedItems)
	{
		final List<ItemModel> itemWithRelatedItems = Lists.newArrayList(item);
		itemWithRelatedItems.addAll(relatedItems);
		when(relatedItemsCollector.collect(eq(item), anyMap())).thenReturn(itemWithRelatedItems);
		return itemWithRelatedItems;
	}

	@Test
	public void testOnlyItemsSyncRun()
	{
		// given
		doAnswer(inv -> inv.getArguments()[0]).when(facade).collectRelatedItems(any(), any());

		final List<ItemModel> items = mockItemsWithPK(2, 20, ItemModel.class);
		final List<CatalogVersionModel> cvs = mockItemsWithPK(1, 30, CatalogVersionModel.class);
		final SyncItemJobModel syncItemJob = mockItemModelWithPK(100, SyncItemJobModel.class);

		doAnswer(inv -> {
			final ItemModel item = ((ItemModel) inv.getArguments()[0]);
			return Boolean.valueOf(items.contains(item));
		}).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));

		final List<ItemModel> mixed = new ArrayList<>();
		mixed.addAll(items);
		mixed.addAll(cvs);
		final SyncTask syncTask = new SyncTask(mixed, syncItemJob);
		syncTask.setSyncConfig(new SyncConfig());
		syncTask.setParameter(RelatedItemsCollector.MAX_RECURSION_DEPTH, Integer.valueOf(10));
		// when
		facade.performSynchronization(syncTask);
		// then

		verify(catalogSynchronizationService, never()).synchronize(syncItemJob, syncTask.getSyncConfig());
		verify(catalogSynchronizationService).performSynchronization(items, syncItemJob, syncTask.getSyncConfig());
	}

	protected List<PK> getPKs(final List<? extends ItemModel> items)
	{
		return items.stream().map(ItemModel::getPk).collect(Collectors.toList());
	}

	protected <T extends ItemModel> List<T> mockItemsWithPK(final int numberOfItems, final int pkOffset, final Class<T> tClass)
	{
		final List<T> items = new ArrayList<>();
		for (int i = 0; i < numberOfItems; i++)
		{
			final T itemModel = mockItemModelWithPK(pkOffset + i, tClass);
			items.add(itemModel);
		}
		return items;
	}

	protected <T extends ItemModel> T mockItemModelWithPK(final long pk, final Class<T> tClass)
	{
		final T itemModel = mock(tClass);
		when(itemModel.getPk()).thenReturn(PK.fromLong(pk));
		return itemModel;
	}

	@Test
	public void getSyncCatalogVersion()
	{
		//given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(any(ItemModel.class))).thenReturn(catalogVersion);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));
		//when
		final List<ItemModel> itemModels = Lists.newArrayList(item1, item2, item3);
		final Optional<CatalogVersionModel> commonCatalogVersion = facade.getSyncCatalogVersion(itemModels);
		//then
		assertThat(commonCatalogVersion.isPresent()).isTrue();
	}

	@Test
	public void getSyncCatalogVersionNotAllItemsAreCatalogAware()
	{
		//given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(any(ItemModel.class))).thenReturn(catalogVersion);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(item1);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(item2);
		doReturn(Boolean.FALSE).when(catalogTypeService).isCatalogVersionAwareModel(item2);
		//when
		final List<ItemModel> itemModels = Lists.newArrayList(item1, item2, item3);
		final Optional<CatalogVersionModel> commonCatalogVersion = facade.getSyncCatalogVersion(itemModels);
		//then
		assertThat(commonCatalogVersion.isPresent()).isFalse();
	}

	@Test
	public void getSyncCatalogVersionNotAllItemsInTheSameCatalogVersion()
	{
		//given
		final CatalogVersionModel catalogVersionOne = mock(CatalogVersionModel.class);
		final CatalogVersionModel catalogVersionTwo = mock(CatalogVersionModel.class);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item1)).thenReturn(catalogVersionOne);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item2)).thenReturn(catalogVersionOne);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item3)).thenReturn(catalogVersionTwo);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));
		//when
		final List<ItemModel> itemModels = Lists.newArrayList(item1, item2, item3);
		final Optional<CatalogVersionModel> commonCatalogVersion = facade.getSyncCatalogVersion(itemModels);
		//then
		assertThat(commonCatalogVersion.isPresent()).isFalse();
	}

	@Test
	public void getSyncCatalogVersionForCatalogVersionItem()
	{
		//given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		//when
		final Optional<CatalogVersionModel> commonCatalogVersion = facade.getSyncCatalogVersion(Lists.newArrayList(catalogVersion));
		//then
		assertThat(commonCatalogVersion.isPresent()).isTrue();
		assertThat(commonCatalogVersion.get()).isEqualTo(catalogVersion);
	}

	@Test
	public void getSyncCatalogVersionForCatalogVersionItems()
	{
		//given
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		//when
		final ArrayList<ItemModel> catalogs = Lists.newArrayList(catalogVersion, catalogVersion);//the same catalog twice
		final Optional<CatalogVersionModel> commonCatalogVersion = facade.getSyncCatalogVersion(catalogs);
		//then
		assertThat(commonCatalogVersion.isPresent()).isTrue();
		assertThat(commonCatalogVersion.get()).isEqualTo(catalogVersion);
	}

	@Test
	public void getSyncCatalogVersionForDifferentCatalogVersionItems()
	{
		//given
		final CatalogVersionModel catalogVersion1 = mock(CatalogVersionModel.class);
		final CatalogVersionModel catalogVersion2 = mock(CatalogVersionModel.class);
		//when
		final ArrayList<ItemModel> catalogs = Lists.newArrayList(catalogVersion1, catalogVersion2);
		final Optional<CatalogVersionModel> commonCatalogVersion = facade.getSyncCatalogVersion(catalogs);
		//then
		assertThat(commonCatalogVersion.isPresent()).isFalse();
	}

	@Test
	public void testGetInboundSynchronizationsList()
	{
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		when(catalogVersion.getIncomingSynchronizations()).thenReturn(Lists.newArrayList(jobA, jobB));
		when(catalogVersion.getSynchronizations()).thenReturn(Lists.newArrayList(jobC, jobD));

		final List<SyncItemJobModel> inboundSynchronizations = facade.getInboundSynchronizations(catalogVersion);
		assertThat(inboundSynchronizations).containsOnly(jobA, jobB);
	}

	@Test
	public void testGetOutboundSynchronizationsList()
	{
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

		when(catalogVersion.getIncomingSynchronizations()).thenReturn(Lists.newArrayList(jobA, jobB));
		when(catalogVersion.getSynchronizations()).thenReturn(Lists.newArrayList(jobC, jobD));

		final List<SyncItemJobModel> inboundSynchronizations = facade.getOutboundSynchronizations(catalogVersion);
		assertThat(inboundSynchronizations).containsOnly(jobC, jobD);
	}

	@Test
	public void testCronJobRerun()
	{
		//given
		doAnswer(inv -> ((Supplier) inv.getArguments()[0]).get()).when(facade).copyCronJobSafely(any());
		final CatalogVersionSyncCronJobModel newCronJob = new CatalogVersionSyncCronJobModel();
		when(modelService.create("CatalogVersionSyncCronJob")).thenReturn(newCronJob);

		final CatalogVersionSyncScheduleMediaModel newScheduledMediaModel = spy(new CatalogVersionSyncScheduleMediaModel());
		doReturn(PK.fromLong(100)).when(newScheduledMediaModel).getPk();
		when(modelService.create(CatalogVersionSyncScheduleMediaModel.class)).thenReturn(newScheduledMediaModel);

		final CatalogVersionSyncCronJobModel cronJob = getCronJob();

		//when
		facade.reRunCronJob(cronJob);

		//then
		verify(facade).copyCronJobSafely(any());

		//when
		final ArgumentCaptor<CatalogVersionSyncCronJobModel> argCaptor = ArgumentCaptor
				.forClass(CatalogVersionSyncCronJobModel.class);
		//then
		verify(cronJobService).performCronJob(argCaptor.capture());
		verify(facade).copyCronJobData(argCaptor.getValue(), cronJob);
		verify(facade).copyScheduledMedia(argCaptor.getValue(), cronJob);
		verify(modelService).save(argCaptor.getValue());
	}

	@Test
	public void testCopyCronJobData()
	{
		// given
		final CatalogVersionSyncCronJobModel cronJobForRerun = getCronJob();
		final CatalogVersionSyncCronJobModel newCronJobModel = new CatalogVersionSyncCronJobModel();

		// when
		facade.copyCronJobData(newCronJobModel, cronJobForRerun);

		// then
		assertThat(newCronJobModel.getJob()).isEqualTo(cronJobForRerun.getJob());
		assertThat(newCronJobModel.getForceUpdate()).isEqualTo(cronJobForRerun.getForceUpdate());
		assertThat(newCronJobModel.getCreateSavedValues()).isEqualTo(cronJobForRerun.getCreateSavedValues());
		assertThat(newCronJobModel.getLogToDatabase()).isEqualTo(cronJobForRerun.getLogToDatabase());
		assertThat(newCronJobModel.getLogToFile()).isEqualTo(cronJobForRerun.getLogToFile());
		assertThat(newCronJobModel.getLogLevelDatabase()).isEqualTo(cronJobForRerun.getLogLevelDatabase());
		assertThat(newCronJobModel.getLogLevelFile()).isEqualTo(cronJobForRerun.getLogLevelFile());
		assertThat(newCronJobModel.getErrorMode()).isEqualTo(cronJobForRerun.getErrorMode());
		assertThat(newCronJobModel.getFullSync()).isEqualTo(cronJobForRerun.getFullSync());
	}

	@Test
	public void testCopyScheduledMedia()
	{
		// given
		final CatalogVersionSyncScheduleMediaModel firstScheduledMedia = spy(new CatalogVersionSyncScheduleMediaModel());
		doReturn(PK.fromLong(100)).when(firstScheduledMedia).getPk();

		final List<CatalogVersionSyncScheduleMediaModel> scheduledMedias = Lists.newArrayList(firstScheduledMedia,
				new CatalogVersionSyncScheduleMediaModel());

		final CatalogVersionSyncCronJobModel cronJobForRerun = getCronJob();
		when(cronJobForRerun.getScheduleMedias()).thenReturn(scheduledMedias);
		final CatalogVersionSyncCronJobModel newCronJobModel = new CatalogVersionSyncCronJobModel();

		final CatalogVersionSyncScheduleMediaModel newScheduledMediaModel = spy(new CatalogVersionSyncScheduleMediaModel());
		doReturn(PK.fromLong(101)).when(newScheduledMediaModel).getPk();
		when(modelService.create(CatalogVersionSyncScheduleMediaModel.class)).thenReturn(newScheduledMediaModel);

		// when
		facade.copyScheduledMedia(newCronJobModel, cronJobForRerun);

		// then
		assertThat(newCronJobModel.getScheduleMedias().size()).isEqualTo(1);
		assertThat(newCronJobModel.getScheduleMedias().get(0)).isEqualTo(newScheduledMediaModel);
		assertThat(newScheduledMediaModel.getCronjob()).isEqualTo(newCronJobModel);
		verify(mediaService).copyData(firstScheduledMedia, newScheduledMediaModel);
		verify(modelService).save(newScheduledMediaModel);
	}

	private CatalogVersionSyncCronJobModel getCronJob()
	{
		final CatalogVersionSyncCronJobModel cronJobForRerun = spy(new CatalogVersionSyncCronJobModel());
		cronJobForRerun.setJob(new SyncItemJobModel());
		cronJobForRerun.setForceUpdate(Boolean.TRUE);
		cronJobForRerun.setCreateSavedValues(Boolean.TRUE);
		cronJobForRerun.setLogToDatabase(Boolean.TRUE);
		cronJobForRerun.setLogToFile(Boolean.TRUE);
		cronJobForRerun.setLogLevelDatabase(JobLogLevel.FATAL);
		cronJobForRerun.setLogLevelFile(JobLogLevel.ERROR);
		cronJobForRerun.setErrorMode(ErrorMode.FAIL);
		doReturn("CatalogVersionSyncCronJob").when(cronJobForRerun).getItemtype();
		return cronJobForRerun;
	}

	@Test
	public void testCreateSyncConfigWithDefaults()
	{
		doAnswer(inv -> inv.getArguments()[1]).when(facade).getBooleanFromSystemConfig(anyString(), any());
		doAnswer(inv -> inv.getArguments()[2]).when(facade).getEnumValueFromSystemConfig(any(), anyString(), any());

		final SyncItemJobModel jobModel = mock(SyncItemJobModel.class);
		when(jobModel.getLogToFile()).thenReturn(Boolean.TRUE);
		when(jobModel.getLogToDatabase()).thenReturn(Boolean.TRUE);
		when(jobModel.getRemoveOnExit()).thenReturn(Boolean.TRUE);
		when(jobModel.getLogLevelDatabase()).thenReturn(JobLogLevel.DEBUG);
		when(jobModel.getLogLevelFile()).thenReturn(JobLogLevel.ERROR);
		when(jobModel.getErrorMode()).thenReturn(ErrorMode.FAIL);

		final SyncConfig syncConfigWithDefaults = facade.createSyncConfigWithDefaults(jobModel);

		assertThat(syncConfigWithDefaults.getCreateSavedValues()).isFalse();
		verify(facade).getBooleanFromSystemConfig(SYNC_CONFIG_CREATE_SAVED_VALUES, Boolean.FALSE);
		assertThat(syncConfigWithDefaults.getForceUpdate()).isTrue();
		verify(facade).getBooleanFromSystemConfig(SYNC_CONFIG_FORCE_UPDATE, Boolean.TRUE);
		assertThat(syncConfigWithDefaults.getSynchronous()).isFalse();
		verify(facade).getBooleanFromSystemConfig(SYNC_CONFIG_SYNCHRONOUS, Boolean.FALSE);
		assertThat(syncConfigWithDefaults.getAbortWhenCollidingSyncIsRunning()).isTrue();
		verify(facade).getBooleanFromSystemConfig(DefaultSynchronizationFacade.BACKOFFICE_SYNC_CONFIG_ABORT_ON_COLLIDING,
				Boolean.TRUE);

		assertThat(syncConfigWithDefaults.getLogToFile()).isEqualTo(jobModel.getLogToFile());
		verify(facade).getBooleanFromSystemConfig(SYNC_CONFIG_LOG_TO_FILE, jobModel.getLogToFile());
		assertThat(syncConfigWithDefaults.getLogToDatabase()).isEqualTo(jobModel.getLogToDatabase());
		verify(facade).getBooleanFromSystemConfig(SYNC_CONFIG_LOG_TO_DATABASE, jobModel.getLogToDatabase());
		assertThat(syncConfigWithDefaults.getKeepCronJob()).isEqualTo(jobModel.getRemoveOnExit());
		verify(facade).getBooleanFromSystemConfig(SYNC_CONFIG_KEEP_CRON_JOB, jobModel.getRemoveOnExit());

		assertThat(syncConfigWithDefaults.getLogLevelDatabase()).isEqualTo(jobModel.getLogLevelDatabase());
		verify(facade).getEnumValueFromSystemConfig(JobLogLevel.class, SYNC_CONFIG_LOG_LEVEL_DATABASE,
				jobModel.getLogLevelDatabase());
		assertThat(syncConfigWithDefaults.getLogLevelFile()).isEqualTo(jobModel.getLogLevelFile());
		verify(facade).getEnumValueFromSystemConfig(JobLogLevel.class, SYNC_CONFIG_LOG_LEVEL_FILE, jobModel.getLogLevelFile());
		assertThat(syncConfigWithDefaults.getErrorMode()).isEqualTo(jobModel.getErrorMode());
		verify(facade).getEnumValueFromSystemConfig(ErrorMode.class, SYNC_CONFIG_ERROR_MODE, jobModel.getErrorMode());

	}

	@Test
	public void testGettingEmptyCatalogVersion()
	{
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(any(ItemModel.class));
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(any(ItemModel.class)))
				.thenThrow(new IllegalStateException());

		assertThat(facade.getSyncCatalogVersion(Lists.newArrayList(new ProductModel()))).isEqualTo(Optional.empty());
	}

	@Test
	public void testIsSyncInProgressWhenNoJobsAvailable()
	{
		final ItemModel item = mock(ItemModel.class);
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(item);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item)).thenReturn(cv);

		final boolean inProgress = facade.isSyncInProgress(item);

		assertThat(inProgress).isFalse();
	}

	@Test
	public void testIsSyncInProgressWhenIncomingSyncJobIsRunning()
	{
		final ItemModel item = mock(ItemModel.class);
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		when(cv.getIncomingSynchronizations()).thenReturn(Lists.newArrayList(jobA, jobB));
		doReturn(Boolean.TRUE).when(catalogSynchronizationService).isInProgress(jobA);
		doReturn(Boolean.FALSE).when(catalogSynchronizationService).isInProgress(jobB);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(item);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item)).thenReturn(cv);


		final boolean inProgress = facade.isSyncInProgress(item);

		assertThat(inProgress).isTrue();
	}

	@Test
	public void testIsSyncInProgressItemIsNew()
	{
		final ItemModel item = mock(ItemModel.class);
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		when(cv.getIncomingSynchronizations()).thenReturn(Lists.newArrayList(jobA, jobB));
		doReturn(Boolean.TRUE).when(catalogSynchronizationService).isInProgress(jobA);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item)).thenReturn(cv);
		doReturn(Boolean.TRUE).when(modelService).isNew(item);

		final boolean inProgress = facade.isSyncInProgress(item);

		assertThat(inProgress).isFalse();
	}

	@Test
	public void testIsSyncInProgressWhenOutgoingSyncJobIsRunning()
	{
		final ItemModel item = mock(ItemModel.class);
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		when(cv.getSynchronizations()).thenReturn(Lists.newArrayList(jobA, jobB));
		doReturn(Boolean.TRUE).when(catalogSynchronizationService).isInProgress(jobA);
		doReturn(Boolean.FALSE).when(catalogSynchronizationService).isInProgress(jobB);
		doReturn(Boolean.TRUE).when(catalogTypeService).isCatalogVersionAwareModel(item);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item)).thenReturn(cv);


		final boolean inProgress = facade.isSyncInProgress(item);

		assertThat(inProgress).isTrue();
	}

	@Test
	public void testGetEnumValueFromSystemConfigForNullForDefaultInput()
	{
		// given
		doReturn("some config").when(facade).getConfigValue(any(), any());

		// when
		facade.getEnumValueFromSystemConfig(JobLogLevel.class, DefaultSynchronizationFacade.SYNC_CONFIG_LOG_LEVEL_DATABASE, null);

		// then
		final ArgumentMatcher<String> argumentMatcher = new ArgumentMatcher<String>()
		{
			@Override
			public boolean matches(final Object o)
			{
				final String value = (String) o;
				return "".equals(value);
			}
		};
		verify(facade).getConfigValue(any(), argThat(argumentMatcher));
	}

	@Test
	public void testSyncConfigOfNullProperties()
	{
		// given
		final JobLogLevel logLevelJobA = JobLogLevel.INFO;
		final JobLogLevel defaultLogLevel = JobLogLevel.WARNING;
		doReturn("some config").when(facade).getConfigValue(any(), any());
		doReturn(Boolean.TRUE).when(facade).getBooleanFromSystemConfig(any(), any());
		doReturn(logLevelJobA).when(facade).getEnumValueFromSystemConfig(JobLogLevel.class,
				DefaultSynchronizationFacade.SYNC_CONFIG_LOG_LEVEL_DATABASE, logLevelJobA);
		doReturn(null).when(facade).getEnumValueFromSystemConfig(JobLogLevel.class,
				DefaultSynchronizationFacade.SYNC_CONFIG_LOG_LEVEL_DATABASE, null);
		given(jobA.getLogLevelDatabase()).willReturn(logLevelJobA);
		given(jobB.getLogLevelDatabase()).willReturn(null);

		// when
		final SyncConfig syncConfigJobA = facade.createSyncConfigWithDefaults(jobA);
		final SyncConfig syncConfigJobB = facade.createSyncConfigWithDefaults(jobB);

		// then
		assertThat(syncConfigJobA.getLogLevelDatabase()).isEqualTo(logLevelJobA);
		assertThat(syncConfigJobB.getLogLevelDatabase()).isEqualTo(defaultLogLevel);
	}

	@Test
	public void testCanSyncWhenCurrentUserIsAdmin()
	{
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		when(jobA.getSyncPrincipalsOnly()).thenReturn(true);
		when(jobA.getTargetVersion()).thenReturn(cv);

		when(userService.isAdmin(currentUser)).thenReturn(true);

		assertThat(facade.canSync(jobA)).isTrue();
		verify(catalogVersionService, never()).canWrite(any(), any());
	}

	@Test
	public void testCanSyncWhenUserIsInAllowedPrincipals()
	{
		final PrincipalModel somePrincipal = mock(PrincipalModel.class);
		when(jobA.getSyncPrincipals()).thenReturn(Lists.newArrayList(somePrincipal, currentUser));

		assertThat(facade.canSync(jobA)).isTrue();
		verify(catalogVersionService, never()).canWrite(any(), any());
	}

	@Test
	public void testCanSyncWhenUserGroupIsInAllowedPrincipals()
	{
		doReturn(Sets.newHashSet(userGroup3)).when(currentUser).getAllGroups();
		when(jobA.getSyncPrincipals()).thenReturn(Lists.newArrayList(userGroup1, userGroup2, userGroup3));

		assertThat(facade.canSync(jobA)).isTrue();
		verify(catalogVersionService, never()).canWrite(any(), any());
	}

	@Test
	public void testCanNotSyncIfPrincipalSyncRequired()
	{

		when(jobA.getSyncPrincipalsOnly()).thenReturn(true);
		when(jobA.getSyncPrincipals()).thenReturn(Lists.newArrayList(userGroup1, userGroup2, userGroup3));
		doReturn(Sets.newHashSet()).when(currentUser).getAllGroups();

		assertThat(facade.canSync(jobA)).isFalse();
		verify(catalogVersionService, never()).canWrite(any(), any());
	}

	@Test
	public void testCanSyncWithWritePermissions()
	{
		final CatalogVersionModel srcCV = mock(CatalogVersionModel.class);
		final CatalogVersionModel targetCV = mock(CatalogVersionModel.class);
		when(jobA.getTargetVersion()).thenReturn(targetCV);
		when(jobA.getSourceVersion()).thenReturn(srcCV);
		when(jobA.getSyncPrincipals()).thenReturn(Lists.newArrayList());
		when(catalogVersionService.canWrite(targetCV, currentUser)).thenReturn(true);
		when(catalogVersionService.canRead(srcCV, currentUser)).thenReturn(true);

		assertThat(facade.canSync(jobA)).isTrue();
		verify(catalogVersionService).canWrite(targetCV, currentUser);
		verify(catalogVersionService).canRead(srcCV, currentUser);
	}

	@Test
	public void testCanNotWithoutWritePermissions()
	{
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		when(jobA.getTargetVersion()).thenReturn(cv);
		when(jobA.getSyncPrincipals()).thenReturn(Lists.newArrayList());
		when(catalogVersionService.canWrite(cv, currentUser)).thenReturn(false);

		assertThat(facade.canSync(jobA)).isFalse();
		verify(catalogVersionService).canWrite(cv, currentUser);
	}

	@Test
	public void testCanNotWithoutReadPermissions()
	{
		final CatalogVersionModel srcCV = mock(CatalogVersionModel.class);
		final CatalogVersionModel targetCV = mock(CatalogVersionModel.class);
		when(jobA.getTargetVersion()).thenReturn(targetCV);
		when(jobA.getSourceVersion()).thenReturn(srcCV);
		when(jobA.getSyncPrincipals()).thenReturn(Lists.newArrayList());
		when(catalogVersionService.canWrite(targetCV, currentUser)).thenReturn(true);
		when(catalogVersionService.canRead(srcCV, currentUser)).thenReturn(false);

		assertThat(facade.canSync(jobA)).isFalse();
		verify(catalogVersionService).canWrite(targetCV, currentUser);
		verify(catalogVersionService).canRead(srcCV, currentUser);
	}

	@Test
	public void testGetCounterpart()
	{
		testGetCounterpart(true);
		testGetCounterpart(false);

	}

	public void testGetCounterpart(final boolean fromSource)
	{
		//given
		final CatalogVersionModel srcCV = new CatalogVersionModel();
		BackofficeTestUtil.setPk(srcCV, 100);

		final CatalogVersionModel targetCV = new CatalogVersionModel();
		BackofficeTestUtil.setPk(targetCV, 200);
		final SyncItemJobModel syncItemJob = mock(SyncItemJobModel.class);
		when(syncItemJob.getSourceVersion()).thenReturn(srcCV);
		when(syncItemJob.getTargetVersion()).thenReturn(targetCV);

		final ItemModel srcItem = createItemWithPk(1);
		final ItemModel targetItem = createItemWithPk(2);

		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(srcItem)).thenReturn(srcCV);
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(targetItem)).thenReturn(targetCV);

		final ItemSyncTimestampModel itemSyncTimestamp = mock(ItemSyncTimestampModel.class);
		when(itemSyncTimestamp.getSourceItem()).thenReturn(srcItem);
		when(itemSyncTimestamp.getTargetItem()).thenReturn(targetItem);

		when(catalogSynchronizationService.getSynchronizationSourceTimestampFor(syncItemJob, srcItem))
				.thenReturn(itemSyncTimestamp);
		when(catalogSynchronizationService.getSynchronizationTargetTimestampFor(syncItemJob, targetItem))
				.thenReturn(itemSyncTimestamp);
		//when
		final Optional<ItemModel> syncCounterpart = facade.findSyncCounterpart(fromSource ? srcItem : targetItem, syncItemJob);
		//then
		assertThat(syncCounterpart.isPresent()).isTrue();
		assertThat(syncCounterpart.get()).isEqualTo(fromSource ? targetItem : srcItem);
	}


	private ItemModel createItemWithPk(final long pk)
	{
		final ItemModel item = new ProductModel();
		BackofficeTestUtil.setPk(item, pk);
		return item;
	}

}
