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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;

import com.google.common.collect.Lists;
import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@DeclaredInput(value = SyncPopupController.SOCKET_IN_INPUT_OBJECT, socketType = ItemModel.class)
@DeclaredInput(value = SyncPopupController.SOCKET_IN_INPUT_OBJECTS, socketType = List.class)
@DeclaredViewEvent(componentID = SyncPopupController.PULLLIST_ID, eventName = Events.ON_SELECT)
@DeclaredViewEvent(componentID = SyncPopupController.PUSHLIST_ID, eventName = Events.ON_SELECT)
@DeclaredViewEvent(componentID = SyncPopupController.SEARCHBOX_ID, eventName = Events.ON_CHANGING)
@DeclaredViewEvent(componentID = SyncPopupController.CANCEL_BUTTON_ID, eventName = Events.ON_CLICK)
@DeclaredViewEvent(componentID = SyncPopupController.SYNC_BUTTON_ID, eventName = Events.ON_CLICK)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class SyncPopupControllerTest extends AbstractWidgetUnitTest<SyncPopupController>
{
	private static final String SAMPLE_JOB_MODEL_DESCRIPTION = "sample description";

	@Spy
	@InjectMocks
	private SyncPopupController controller;

	@Mock
	private Listbox pullList;
	@Mock
	private Listbox pushList;
	@Mock
	private Textbox searchbox;
	@Mock
	private Tabbox tabbox;
	@Mock
	private Label title;
	@Mock
	private Button sync;
	@Mock
	private LabelService labelService;
	@Mock
	private SynchronizationFacade synchronizationFacade;
	@Mock
	private Tab pullTab;
	@Mock
	private Tab pushTab;

	@Before
	public void before()
	{
		controller.initialize(new Div());
		doNothing().when(controller).showWarningMessageBox(anyString(), anyString());
		doAnswer(inv -> {
			final SyncJobsLoader loader = spy(new SyncJobsLoader((List<ItemModel>) inv.getArguments()[0]));
			doReturn(synchronizationFacade).when(loader).getSynchronizationFacade();
			return loader;
		}).when(controller).createSyncDataLoader(any());
		when(synchronizationFacade.getSyncCatalogVersion(any())).thenReturn(Optional.empty());
		when(synchronizationFacade.performSynchronization(any())).thenReturn(Optional.empty());
		when(synchronizationFacade.isInSync(anyList(), any(), anyMap())).thenReturn(Optional.empty());
	}

	@Test
	public void testSelectedSync()
	{
		final ItemModel item = mock(ItemModel.class);

		final List<SyncItemJobModel> pullModels = new ArrayList<>();
		pullModels.add(createSampleJobModelMock());
		pullModels.add(createSampleJobModelMock());
		pullModels.add(createSampleJobModelMock());
		pullModels.add(createSampleJobModelMock());

		final List<SyncItemJobModel> pushModels = new ArrayList<>();
		pushModels.add(createSampleJobModelMock());
		pushModels.add(createSampleJobModelMock());
		pushModels.add(createSampleJobModelMock());
		pushModels.add(createSampleJobModelMock());

		final CatalogVersionModel cv = mock(CatalogVersionModel.class);

		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item))).thenReturn(Optional.of(cv));
		when(synchronizationFacade.getInboundSynchronizations(cv)).thenReturn(pullModels);
		when(synchronizationFacade.getOutboundSynchronizations(cv)).thenReturn(pushModels);
		when(synchronizationFacade.getCatalogVersionAwareItems(any())).thenAnswer(inv -> inv.getArguments()[0]);
		when(synchronizationFacade.performSynchronization(any())).thenReturn(Optional.of("testCode"));
		when(synchronizationFacade.canSync(any())).thenReturn(true);

		controller.showSyncJobsForInputObject(item);
		controller.getPullListModel().addToSelection(controller.getPullListModel().get(1));
		executeViewEvent(SyncPopupController.PULLLIST_ID, Events.ON_SELECT);
		controller.getPushListModel().addToSelection(controller.getPushListModel().get(2));
		executeViewEvent(SyncPopupController.PUSHLIST_ID, Events.ON_SELECT);

		executeViewEvent(SyncPopupController.SYNC_BUTTON_ID, Events.ON_CLICK);

		assertSocketOutput(SyncPopupController.SOCKET_STARTED_SYNC_CRON_JOB, (Predicate<String>) "testCode"::equals);
	}

	@Test
	public void testPullListSelectDeselectsPushList()
	{
		final List<SyncPopupViewModel> pullModels = new ArrayList<>();
		pullModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE));
		pullModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE));
		pullModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE));
		pullModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE));

		final ListModelList<SyncPopupViewModel> pullListModel = controller.getPullListModel();
		pullListModel.addAll(pullModels);

		assertThat(pullListModel.getSelection()).isEmpty();
		assertThat(controller.getPushListModel().getSelection()).isEmpty();

		pullListModel.setSelection(Lists.newArrayList(pullModels.get(2)));
		assertThat(pullListModel.getSelection().size()).isEqualTo(1);
		assertThat(pullListModel.getSelection().iterator().next()).isEqualTo(pullModels.get(2));

		executeViewEvent(SyncPopupController.PUSHLIST_ID, Events.ON_SELECT);

		assertThat(pullListModel.getSelection()).isEmpty();
	}

	@Test
	public void testPushListSelectDeselectsPullList()
	{
		final List<SyncPopupViewModel> pushModels = new ArrayList<>();
		pushModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PUSH, Boolean.FALSE));
		pushModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PUSH, Boolean.FALSE));
		pushModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PUSH, Boolean.FALSE));
		pushModels.add(new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PUSH, Boolean.FALSE));
		final ListModelList<SyncPopupViewModel> pushListModel = controller.getPushListModel();
		pushListModel.addAll(pushModels);

		assertThat(pushListModel.getSelection()).isEmpty();
		assertThat(controller.getPullListModel().getSelection()).isEmpty();

		pushListModel.setSelection(Lists.newArrayList(pushModels.get(2)));
		assertThat(pushListModel.getSelection().size()).isEqualTo(1);
		assertThat(pushListModel.getSelection().iterator().next()).isEqualTo(pushModels.get(2));

		executeViewEvent(SyncPopupController.PULLLIST_ID, Events.ON_SELECT);

		assertThat(pushListModel.getSelection()).isEmpty();
	}

	@Test
	public void testPullJobsAreLoadedOnlyOnce()
	{
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final CatalogVersionModel cv2 = mock(CatalogVersionModel.class);
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);

		final SyncItemJobModel job1 = createSampleJobModelMock();
		final SyncItemJobModel job2 = createSampleJobModelMock();
		final SyncItemJobModel job3 = createSampleJobModelMock();

		final ArrayList<SyncItemJobModel> jobsCv1 = Lists.newArrayList(job1, job2);
		final ArrayList<SyncItemJobModel> jobsCv2 = Lists.newArrayList(job3);

		when(synchronizationFacade.getInboundSynchronizations(same(cv1))).thenReturn(jobsCv1);
		when(synchronizationFacade.getInboundSynchronizations(same(cv2))).thenReturn(jobsCv2);
		when(synchronizationFacade.canSync(any())).thenReturn(true);
		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item1))).thenReturn(Optional.of(cv1));
		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item2))).thenReturn(Optional.of(cv2));
		when(synchronizationFacade.getCatalogVersionAwareItems(any())).thenAnswer(inv -> inv.getArguments()[0]);

		//first object came
		executeInputSocketEvent(SyncPopupController.SOCKET_IN_INPUT_OBJECT, item1);

		assertThat(controller.getPullListModel()).hasSize(2);
		assertThat(controller.getPullListModel().get(0).getJobModel()).isEqualTo(job1);
		assertThat(controller.getPullListModel().get(1).getJobModel()).isEqualTo(job2);

		//second object - old jobs should be removed
		executeInputSocketEvent(SyncPopupController.SOCKET_IN_INPUT_OBJECT, item2);

		assertThat(controller.getPullListModel()).hasSize(1);
		assertThat(controller.getPullListModel().get(0).getJobModel()).isEqualTo(job3);
	}

	@Test
	public void testPushJobsAreLoadedOnlyOnce()
	{
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final CatalogVersionModel cv2 = mock(CatalogVersionModel.class);
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);

		final SyncItemJobModel job1 = createSampleJobModelMock();
		final SyncItemJobModel job2 = createSampleJobModelMock();
		final SyncItemJobModel job3 = createSampleJobModelMock();

		final ArrayList<SyncItemJobModel> jobsCv1 = Lists.newArrayList(job1, job2);
		final ArrayList<SyncItemJobModel> jobsCv2 = Lists.newArrayList(job3);

		when(synchronizationFacade.getOutboundSynchronizations(same(cv1))).thenReturn(jobsCv1);
		when(synchronizationFacade.getOutboundSynchronizations(same(cv2))).thenReturn(jobsCv2);

		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item1))).thenReturn(Optional.of(cv1));
		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item2))).thenReturn(Optional.of(cv2));
		when(synchronizationFacade.canSync(any())).thenReturn(true);
		when(synchronizationFacade.getCatalogVersionAwareItems(any())).thenAnswer(inv -> inv.getArguments()[0]);

		//first object came
		executeInputSocketEvent(SyncPopupController.SOCKET_IN_INPUT_OBJECTS, Lists.newArrayList(item1));

		assertThat(controller.getPushListModel()).hasSize(2);
		assertThat(controller.getPushListModel().get(0).getJobModel()).isEqualTo(job1);
		assertThat(controller.getPushListModel().get(1).getJobModel()).isEqualTo(job2);

		//second object - old jobs should be removed
		executeInputSocketEvent(SyncPopupController.SOCKET_IN_INPUT_OBJECTS, Lists.newArrayList(item2));

		assertThat(controller.getPushListModel()).hasSize(1);
		assertThat(controller.getPushListModel().get(0).getJobModel()).isEqualTo(job3);
	}


	@Test
	public void testListsClearedWhenSecondObjectIsNotCatalogVersionAware()
	{
		final CatalogVersionModel cv1 = mock(CatalogVersionModel.class);
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);

		final SyncItemJobModel job1 = createSampleJobModelMock();
		final SyncItemJobModel job2 = createSampleJobModelMock();

		final ArrayList<SyncItemJobModel> jobsCv1 = Lists.newArrayList(job1, job2);

		when(synchronizationFacade.getOutboundSynchronizations(same(cv1))).thenReturn(jobsCv1);

		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item1))).thenReturn(Optional.of(cv1));
		when(synchronizationFacade.getSyncCatalogVersion(Lists.newArrayList(item2))).thenReturn(Optional.empty());
		when(synchronizationFacade.canSync(any())).thenReturn(true);
		when(synchronizationFacade.getCatalogVersionAwareItems(any())).thenAnswer(inv -> inv.getArguments()[0]);

		//first object came
		executeInputSocketEvent(SyncPopupController.SOCKET_IN_INPUT_OBJECT, item1);

		assertThat(controller.getPushListModel()).hasSize(2);
		assertThat(controller.getPushListModel().get(0).getJobModel()).isEqualTo(job1);
		assertThat(controller.getPushListModel().get(1).getJobModel()).isEqualTo(job2);

		//second object - no common catalog found - list cleared
		executeInputSocketEvent(SyncPopupController.SOCKET_IN_INPUT_OBJECT, item2);

		assertThat(controller.getPushListModel()).hasSize(0);
	}

	@Test
	public void testPackModels()
	{
		final List<SyncPopupViewModel> pullList = controller.packModels(createSampleJobModelList(5), SyncJobType.PULL);
		final List<SyncPopupViewModel> pushList = controller.packModels(createSampleJobModelList(4), SyncJobType.PUSH);

		assertThat(pullList.size()).isEqualTo(5);
		assertThat(pushList.size()).isEqualTo(4);

		assertThat(pullList.get(0).getType()).isEqualTo(SyncJobType.PULL);
		assertThat(pushList.get(0).getType()).isEqualTo(SyncJobType.PUSH);

		assertThat(pullList.get(1).getDescription()).isEqualTo(SAMPLE_JOB_MODEL_DESCRIPTION);
		assertThat(pushList.get(1).getDescription()).isEqualTo(SAMPLE_JOB_MODEL_DESCRIPTION);

		assertThat(pullList.get(2).getJobModel()).isNotNull();
		assertThat(pushList.get(2).getJobModel()).isNotNull();

		assertThat(pullList.get(3).getName()).isNotNull();
		assertThat(pushList.get(3).getName()).isNotNull();
	}

	@Test
	public void testUnpackSelectedModels()
	{
		final SyncPopupViewModel[] pullModels = new SyncPopupViewModel[4];
		pullModels[0] = new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE);
		pullModels[1] = new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE);
		pullModels[2] = new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE);
		pullModels[3] = new SyncPopupViewModel(createSampleJobModelMock(), SyncJobType.PULL, Boolean.FALSE);
		final ListModelList<SyncPopupViewModel> pullListModel = new ListModelList<>(pullModels);

		pullListModel.setSelection(Lists.newArrayList(pullModels[1]));

		final List<SyncItemJobModel> syncItemJobModels = controller.unpackModels(pullListModel.getSelection());

		assertThat(syncItemJobModels.size()).isEqualTo(1);
		assertThat(syncItemJobModels.get(0).getCode()).isEqualTo(SAMPLE_JOB_MODEL_DESCRIPTION);
	}

	@Test
	public void testGetSyncItems()
	{
		assertThat(controller.getSyncItems()).isNotNull();
		assertThat(controller.getSyncItems()).isEmpty();

		final List<ItemModel> models = new ArrayList<>();
		models.add(new ItemModel());
		models.add(new ItemModel());
		models.add(new ItemModel());
		controller.setValue(SyncPopupController.MODEL_SYNC_ITEMS, models);

		assertThat(controller.getSyncItems().size()).isEqualTo(3);
	}

	private List<SyncItemJobModel> createSampleJobModelList(final int size)
	{
		final List<SyncItemJobModel> list = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			list.add(createSampleJobModelMock());
		}
		return list;
	}

	private SyncItemJobModel createSampleJobModelMock()
	{
		final SyncItemJobModel model = mock(SyncItemJobModel.class);
		Mockito.when(model.getCode()).thenReturn(SAMPLE_JOB_MODEL_DESCRIPTION);
		final CatalogVersionModel catalogVersionModel = mock(CatalogVersionModel.class);
		Mockito.when(catalogVersionModel.getCatalog()).thenReturn(mock(CatalogModel.class));
		Mockito.when(model.getSourceVersion()).thenReturn(catalogVersionModel);
		return model;
	}

	@Override
	protected SyncPopupController getWidgetController()
	{
		return controller;
	}
}
