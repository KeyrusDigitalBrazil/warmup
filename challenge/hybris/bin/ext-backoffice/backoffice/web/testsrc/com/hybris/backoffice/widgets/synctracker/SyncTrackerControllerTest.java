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
package com.hybris.backoffice.widgets.synctracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.event.events.AfterCronJobFinishedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.Lists;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.backoffice.events.processes.ProcessFinishedEvent;
import com.hybris.backoffice.sync.SyncTask;
import com.hybris.backoffice.sync.SyncTaskExecutionInfo;
import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;


@DeclaredGlobalCockpitEvent(eventName = ProcessFinishedEvent.EVENT_NAME, scope = CockpitEvent.APPLICATION)
@DeclaredInput(value = SyncTrackerController.SOCKET_IN_SYNC_TASK, socketType = SyncTaskExecutionInfo.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
@NullSafeWidget
public class SyncTrackerControllerTest extends AbstractWidgetUnitTest<SyncTrackerController>
{

	@InjectMocks
	private SyncTrackerController controller;
	@Mock
	private transient ObjectFacade objectFacade;
	@Mock
	private transient CockpitEventQueue cockpitEventQueue;
	@Mock
	private transient SynchronizationFacade synchronizationFacade;


	@Override
	protected SyncTrackerController getWidgetController()
	{
		return controller;
	}

	@Test
	public void shouldStartTrackingNewSynchronization()
	{
		//given
		final SyncTaskExecutionInfo executionInfo = mocExecutionInfo("cronJobCode", 1, 2);
		//when
		executeInputSocketEvent(SyncTrackerController.SOCKET_IN_SYNC_TASK, executionInfo);
		//then
		assertThat(getTrackingMap().get("cronJobCode")).containsOnly("1", "2");
	}

	@Test
	public void shouldSendUpdateSockedAndGlobalEvent()
	{
		//given
		widgetSettings.put(SyncTrackerController.SETTING_SEND_GLOBAL_EVENT, Boolean.TRUE, Boolean.class);
		final SyncTaskExecutionInfo executionInfo = mocExecutionInfo("cronJobCode", 1, 2);
		executeInputSocketEvent(SyncTrackerController.SOCKET_IN_SYNC_TASK, executionInfo);
		//when
		final ProcessFinishedEvent processFinishedEvent = mockProcessFinishedEvent("cronJobCode");
		final DefaultCockpitEvent cockpitEvent = new DefaultCockpitEvent(ProcessFinishedEvent.EVENT_NAME, processFinishedEvent,
				null);
		executeGlobalEvent(ProcessFinishedEvent.EVENT_NAME, CockpitEvent.APPLICATION, cockpitEvent);
		//then
		assertSocketOutput(SyncTrackerController.SOCKET_OUT_SYNCED_ITEMS, (Predicate<List<ItemModel>>) itemModels -> CollectionUtils
				.isEqualCollection(itemModels, executionInfo.getSyncTask().getItems()));
		assertThat(getTrackingMap().get("cronJobCode")).isNull();
		verify(cockpitEventQueue).publishEvent(argThat(new ArgumentMatcher<CockpitEvent>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return ObjectCRUDHandler.OBJECTS_UPDATED_EVENT.equals(((CockpitEvent) o).getName()) && CollectionUtils
						.isEqualCollection(((CockpitEvent) o).getDataAsCollection(), executionInfo.getSyncTask().getItems());
			}
		}));
	}

	@Test
	public void shouldTrackWithCounterpartItems()
	{
		//given
		widgetSettings.put(SyncTrackerController.SETTING_FIND_SYNC_COUNTERPARTS, Boolean.TRUE, Boolean.class);
		final SyncTaskExecutionInfo executionInfo = mocExecutionInfo("cronJobCode", 1, 2);
		final SyncItemJobModel syncItemJob = executionInfo.getSyncTask().getSyncItemJob();
		final ItemModel item1 = executionInfo.getSyncTask().getItems().get(0);
		final ItemModel item2 = executionInfo.getSyncTask().getItems().get(1);
		final ItemModel counterPartItem = mockItemWithPK(3);
		when(synchronizationFacade.findSyncCounterpart(item1, syncItemJob)).thenReturn(Optional.of(counterPartItem));
		when(synchronizationFacade.findSyncCounterpart(item2, syncItemJob)).thenReturn(Optional.empty());
		//when
		executeInputSocketEvent(SyncTrackerController.SOCKET_IN_SYNC_TASK, executionInfo);
		//then
		assertThat(getTrackingMap().get("cronJobCode")).containsOnly("1", "2", "3");
	}

	@Test
	public void shouldGetCatalogVersionCounterPart()
	{
		shouldSendCounterPartCatalogVersion(false);
		shouldSendCounterPartCatalogVersion(false);
	}

	public void shouldSendCounterPartCatalogVersion(final boolean fromSource)
	{
		//given
		widgetSettings.put(SyncTrackerController.SETTING_FIND_SYNC_COUNTERPARTS, Boolean.TRUE, Boolean.class);
		final CatalogVersionModel srcCV = new CatalogVersionModel();
		BackofficeTestUtil.setPk(srcCV, 1);
		final CatalogVersionModel targetCV = new CatalogVersionModel();
		BackofficeTestUtil.setPk(targetCV, 2);
		final SyncItemJobModel syncItemJob = mock(SyncItemJobModel.class);
		when(syncItemJob.getSourceVersion()).thenReturn(srcCV);
		when(syncItemJob.getTargetVersion()).thenReturn(targetCV);

		final SyncTask syncTask = new SyncTask(Lists.newArrayList(fromSource ? srcCV : targetCV), syncItemJob);
		final SyncTaskExecutionInfo executionInfo = new SyncTaskExecutionInfo(syncTask, "cronJobCode");

		//when
		executeInputSocketEvent(SyncTrackerController.SOCKET_IN_SYNC_TASK, executionInfo);
		//then
		assertThat(getTrackingMap().get("cronJobCode")).containsOnly("1", "2");
	}

	protected ProcessFinishedEvent mockProcessFinishedEvent(final String cronJobCode)
	{
		final AfterCronJobFinishedEvent cronJobEvent = mock(AfterCronJobFinishedEvent.class);
		when(cronJobEvent.getCronJob()).thenReturn(cronJobCode);
		return new ProcessFinishedEvent(cronJobEvent);
	}

	protected Map<String, Set<String>> getTrackingMap()
	{
		return (Map<String, Set<String>>) widgetModel.getValue(SyncTrackerController.MODEL_TRACKED_SYNCHRONIZATIONS, Map.class);
	}

	protected SyncTaskExecutionInfo mocExecutionInfo(final String cronJobCode, final long... pks)
	{
		final CatalogVersionModel srcCV = mock(CatalogVersionModel.class);
		final CatalogVersionModel targetCV = mock(CatalogVersionModel.class);
		final SyncItemJobModel syncItemJob = mock(SyncItemJobModel.class);
		when(syncItemJob.getSourceVersion()).thenReturn(srcCV);
		when(syncItemJob.getTargetVersion()).thenReturn(targetCV);

		final List<ItemModel> items = new ArrayList<>();
		for (final long pk : pks)
		{
			final ItemModel item = mockItemWithPK(pk);
			items.add(item);

		}

		final SyncTask syncTask = new SyncTask(items, syncItemJob);
		return new SyncTaskExecutionInfo(syncTask, cronJobCode);
	}

	private ItemModel mockItemWithPK(final long pk)
	{
		final ItemModel item = new ProductModel();
		BackofficeTestUtil.setPk(item, pk);
		try
		{
			when(objectFacade.load(String.valueOf(pk))).thenReturn(item);
		}
		catch (final ObjectNotFoundException e)
		{
			e.printStackTrace();
		}
		return item;
	}
}
