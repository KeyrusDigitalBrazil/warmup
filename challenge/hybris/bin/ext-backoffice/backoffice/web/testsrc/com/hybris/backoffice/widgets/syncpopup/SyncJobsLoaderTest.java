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
package com.hybris.backoffice.widgets.syncpopup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.hybris.backoffice.sync.facades.SynchronizationFacade;


public class SyncJobsLoaderTest
{
	@Mock
	private SynchronizationFacade synchronizationFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(synchronizationFacade.getSyncCatalogVersion(Collections.emptyList())).thenReturn(Optional.empty());
	}

	@Test
	public void testMoreThanOneCatalogVersion()
	{
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final CatalogVersionModel cv2 = mock(CatalogVersionModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(cv1, cv2);

		when(synchronizationFacade.getItemsCatalogVersions(selectedItems)).thenReturn(Lists.newArrayList(cv1, cv2));
		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.empty());

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.MULTIPLE_CATALOG_VERSIONS);
	}

	@Test
	public void testOneItemIsNotCatalogAware()
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(item1, item2);

		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.empty());
		when(synchronizationFacade.getCatalogVersionAwareItems(selectedItems)).thenReturn(Lists.newArrayList(item1));

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.ITEMS_CONTAIN_CV_UNAWARE);
	}

	@Test
	public void testMixedCatalogVersionsWithOtherItems()
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(item1, item2, cv1);

		when(synchronizationFacade.getCatalogVersionAwareItems(selectedItems)).thenReturn(Lists.newArrayList(item1, item2));
		when(synchronizationFacade.getItemsCatalogVersions(selectedItems)).thenReturn(Lists.newArrayList(cv1));
		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.empty());

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.MIXED_ITEMS);
	}

	@Test
	public void testDifferentCatalogVersions()
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(item1, item2);

		when(synchronizationFacade.getCatalogVersionAwareItems(selectedItems)).thenReturn(Lists.newArrayList(item1, item2));
		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.empty());

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.DIFFERENT_CATALOG_VERSIONS);
	}

	@Test
	public void testNoSyncJobs()
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(item1, item2);
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);

		when(synchronizationFacade.getCatalogVersionAwareItems(selectedItems)).thenReturn(Lists.newArrayList(item1, item2));
		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.of(cv1));

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.NO_SYNC_JOBS);
	}


	@Test
	public void testEmptySelection()
	{
		final SyncJobsLoader syncJobsLoader = createSyncLoader(Lists.newArrayList());

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.EMPTY_SELECTION);
	}

	@Test
	public void testGetSyncJobs()
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(item1, item2);
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final SyncItemJobModel job1 = mock(SyncItemJobModel.class);
		final SyncItemJobModel job2 = mock(SyncItemJobModel.class);

		when(synchronizationFacade.getCatalogVersionAwareItems(selectedItems)).thenReturn(Lists.newArrayList(item1, item2));
		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.of(cv1));
		when(synchronizationFacade.getInboundSynchronizations(cv1)).thenReturn(Lists.newArrayList(job1));
		when(synchronizationFacade.getOutboundSynchronizations(cv1)).thenReturn(Lists.newArrayList(job2));
		when(synchronizationFacade.canSync(job1)).thenReturn(true);
		when(synchronizationFacade.canSync(job2)).thenReturn(true);

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus().isOK()).isTrue();
		assertThat(syncJobsLoader.getPullJobs()).containsOnly(job1);
		assertThat(syncJobsLoader.getPushJobs()).containsOnly(job2);
	}

	@Test
	public void testFilterNonAccessibleJobs()
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		final List<ItemModel> selectedItems = Lists.newArrayList(item1, item2);
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final SyncItemJobModel job1 = mock(SyncItemJobModel.class);
		final SyncItemJobModel job2 = mock(SyncItemJobModel.class);

		when(synchronizationFacade.getCatalogVersionAwareItems(selectedItems)).thenReturn(Lists.newArrayList(item1, item2));
		when(synchronizationFacade.getSyncCatalogVersion(selectedItems)).thenReturn(Optional.of(cv1));
		when(synchronizationFacade.getInboundSynchronizations(cv1)).thenReturn(Lists.newArrayList(job1));
		when(synchronizationFacade.getOutboundSynchronizations(cv1)).thenReturn(Lists.newArrayList(job2));
		when(synchronizationFacade.canSync(job1)).thenReturn(false);
		when(synchronizationFacade.canSync(job2)).thenReturn(false);

		final SyncJobsLoader syncJobsLoader = createSyncLoader(selectedItems);

		assertThat(syncJobsLoader.getLoadingStatus()).isEqualTo(SyncJobsLoader.SyncJobsLoadingStatus.MISSING_PERMISSIONS);
		assertThat(syncJobsLoader.getPullJobs()).isEmpty();
		assertThat(syncJobsLoader.getPushJobs()).isEmpty();
	}

	protected SyncJobsLoader createSyncLoader(final List<ItemModel> selectedItems)
	{
		final SyncJobsLoader loader = spy(new SyncJobsLoader(selectedItems));
		doReturn(synchronizationFacade).when(loader).getSynchronizationFacade();
		return loader;
	}
}
