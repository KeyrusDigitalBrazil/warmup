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
package com.hybris.backoffice.widgets.processes;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbarbutton;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.backoffice.cronjob.CronJobHistoryDataQuery;
import com.hybris.backoffice.cronjob.CronJobHistoryFacade;
import com.hybris.backoffice.events.processes.ProcessFinishedEvent;
import com.hybris.backoffice.events.processes.ProcessStartEvent;
import com.hybris.backoffice.widgets.processes.settings.DefaultTimeRangeFactory;
import com.hybris.backoffice.widgets.processes.settings.ProcessesSettingsManager;
import com.hybris.backoffice.widgets.processes.settings.TimeRange;
import com.hybris.cockpitng.admin.CockpitMainWindowComposer;
import com.hybris.cockpitng.core.events.CockpitEvent;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.core.user.CockpitUserService;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredGlobalCockpitEvent;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


@DeclaredGlobalCockpitEvent(eventName = CockpitMainWindowComposer.HEARTBEAT_EVENT, scope = CockpitEvent.SESSION)
@DeclaredGlobalCockpitEvent(eventName = ProcessStartEvent.EVENT_NAME, scope = CockpitEvent.APPLICATION)
@DeclaredGlobalCockpitEvent(eventName = ProcessFinishedEvent.EVENT_NAME, scope = CockpitEvent.APPLICATION)
@DeclaredGlobalCockpitEvent(eventName = ProcessesController.GLOBAL_EVENT_UPDATE_PROCESS_FOR_CRON_JOB, scope = CockpitEvent.SESSION)
@DeclaredInput(value = ProcessesController.SOCKET_IN_UPDATE_CRON_JOB, socketType = String.class)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = ProcessesController.COMP_ID_OPEN_BTN)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = ProcessesController.COMP_ID_CLOSE_BTN)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = ProcessesController.COMP_ID_AUTO_CLOSE_COMPONENT)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
@NullSafeWidget
public class ProcessesControllerTest extends AbstractWidgetUnitTest<ProcessesController>
{
	public static final String CURRENT_USER = "currentUser";
	public static final long JOB_PK = 1;
	public static final String CRON_JOB_CODE = "cronJobCode";
	public static final String JOB_TYPE_TWO = "jobTypeTwo";
	public static final String JOB_TYPE_ONE = "jobTypeOne";
	public static final String THE_CODE = "theCode";

	@Spy
	@InjectMocks
	private ProcessesController controller;

	@Mock
	private Label finishedJobsStateLabel;
	@Mock
	private Label globalJobsStateLabel;
	@Mock
	private Label timeRangeStateLabel;
	@Mock
	private Listbox processesListbox;
	@Spy
	private Toolbarbutton openBtn;
	@Mock
	private CockpitUserService cockpitUserService;
	@Mock
	private TimeService timeService;
	@Mock
	private CronJobHistoryFacade cronJobHistoryFacade;
	@Mock
	private TypeFacade typeFacade;
	@Spy
	private final List<ProcessesQueryDecorator> processesQueryDecorators = new ArrayList<>();

	@Spy
	private Timer startedCronJobLookupTimer;
	private final Checkbox globalJobsCheckbox = new Checkbox();
	private final Checkbox finishedJobsCheckbox = new Checkbox();

	private final Listbox timeRangeList = new Listbox();

	@Before
	public void setUp()
	{
		final ProcessesSettingsManager settingsManager = spy(new ProcessesSettingsManager(widgetInstanceManager));
		doReturn(new DefaultTimeRangeFactory()).when(settingsManager).getTimeRangeFactory();
		doReturn(settingsManager).when(controller).getSettingsManager();
		when(cockpitUserService.getCurrentUser()).thenReturn(CURRENT_USER);
		when(timeService.getCurrentTime()).thenReturn(new Date(1234567890));
		when(controller.createProgressData()).thenReturn(mock(ProgressData.class));
	}

	@Test
	public void testShowGlobalJobsSettings()
	{
		controller.initialize(new Div());

		CockpitTestUtil.simulateEvent(globalJobsCheckbox, new CheckEvent(Events.ON_CHECK, globalJobsCheckbox, true));

		verify(controller).onProcessesSettingChanged(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.TRUE);
		assertThat(controller.getSettingsManager().getSettingValue(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.class))
				.isTrue();
		verify(cronJobHistoryFacade).getCronJobHistory(argThat(new ArgumentMatcher<CronJobHistoryDataQuery>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o instanceof CronJobHistoryDataQuery && ((CronJobHistoryDataQuery) o).isShowExecutedByOtherUsers();
			}
		}));
	}

	@Test
	public void testShowFinishedJobsSetting()
	{
		controller.initialize(new Div());

		CockpitTestUtil.simulateEvent(finishedJobsCheckbox, new CheckEvent(Events.ON_CHECK, globalJobsCheckbox, true));

		verify(controller).onProcessesSettingChanged(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.TRUE);
		assertThat(controller.getSettingsManager().getSettingValue(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.class))
				.isTrue();
		verify(cronJobHistoryFacade).getCronJobHistory(argThat(new ArgumentMatcher<CronJobHistoryDataQuery>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o instanceof CronJobHistoryDataQuery && ((CronJobHistoryDataQuery) o).isShowFinishedJobs();
			}
		}));
	}

	@Test
	public void testTimeRangeChangeSetting()
	{
		widgetSettings.put(ProcessesController.SETTING_TIME_RANGES, "10m,2h,3h,2w");
		controller.initialize(new Div());


		final TimeRange twoHrsRange = new DefaultTimeRangeFactory().createTimeRange("2h");
		final SelectEvent<Listitem, TimeRange> selectEvent = new SelectEvent<>(Events.ON_SELECT, timeRangeList, null, null, null,
				Sets.newHashSet(twoHrsRange), null, null, null, null, 0);


		CockpitTestUtil.simulateEvent(timeRangeList, selectEvent);

		verify(controller).onProcessesSettingChanged(ProcessesController.SETTING_TIME_RANGES, twoHrsRange);
		final TimeRange settingValue = controller.getSettingsManager().getSettingValue(ProcessesController.SETTING_TIME_RANGES,
				TimeRange.class);
		assertThat(settingValue).isEqualTo(twoHrsRange);

		verify(cronJobHistoryFacade).getCronJobHistory(argThat(new ArgumentMatcher<CronJobHistoryDataQuery>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o instanceof CronJobHistoryDataQuery
						&& ((CronJobHistoryDataQuery) o).getTimeRange().equals(twoHrsRange.getDuration());
			}
		}));
	}

	@Test
	public void testOnProcessDataResultOldDataRemoved()
	{
		controller.initialize(new Div());
		controller.getProcessesListModel().add(new CronJobHistoryModel());
		final List<CronJobHistoryModel> jobs = Lists.newArrayList(new CronJobHistoryModel(), new CronJobHistoryModel());

		when(cronJobHistoryFacade.getCronJobHistory(any(CronJobHistoryDataQuery.class))).thenReturn(jobs);

		controller.fetchProcesses();

		assertThat(controller.getProcessesListModel()).hasSize(jobs.size());
		assertThat(controller.isDataLoaded()).isTrue();
	}

	@Test
	public void testProcessStartedByCurrentUser()
	{
		controller.initialize(new Div());
		final CronJobHistoryModel cronJobHistory = createProcess(true, JOB_PK, CronJobStatus.RUNNING);

		when(cronJobHistoryFacade.getCronJobHistory(any(CronJobHistoryDataQuery.class)))
				.thenReturn(Lists.newArrayList(cronJobHistory));

		controller.fetchProcesses();

		assertThat(controller.getProcessesListModel()).contains(cronJobHistory);
		verify(openBtn).setSclass(contains(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN));
	}

	@Test
	public void testProcessStartedTwice()
	{
		controller.initialize(new Div());
		final CronJobHistoryModel cronJobHistory = createProcess(true, 1, CronJobStatus.RUNNING);
		when(cronJobHistoryFacade.getCronJobHistory(any(CronJobHistoryDataQuery.class)))
				.thenReturn(Lists.newArrayList(cronJobHistory));

		controller.fetchProcesses();

		final CronJobHistoryModel second = createProcess(true, 1, CronJobStatus.RUNNING);
		when(cronJobHistoryFacade.getCronJobHistory(any(CronJobHistoryDataQuery.class))).thenReturn(Lists.newArrayList(second));

		controller.fetchProcesses();

		assertThat(controller.getProcessesListModel()).containsOnly(second);
		verify(openBtn, times(2)).setSclass(contains(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN));
	}

	@Test
	public void testStartedByOtherUserShowGlobalEnabled()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.TRUE, Boolean.class);
		controller.initialize(new Div());
		final long jobPk = 1;

		final CronJobHistoryModel cronJobHistory = createProcess(false, jobPk, CronJobStatus.RUNNING);

		controller.updateProcesses(Lists.newArrayList(cronJobHistory));

		assertThat(controller.getProcessesListModel()).contains(cronJobHistory);
		verify(openBtn).setSclass(contains(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN));
	}

	@Test
	public void testStartedByOtherUserShowGlobalDisabled()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.FALSE, Boolean.class);
		controller.initialize(new Div());
		final long jobPk = 1;

		final CronJobHistoryModel cronJobHistory = createProcess(false, jobPk, CronJobStatus.RUNNING);

		controller.updateProcesses(Lists.newArrayList(cronJobHistory));

		assertThat(controller.getProcessesListModel()).isEmpty();
		verify(openBtn, never()).setSclass(contains(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN));
	}

	@Test
	public void testFinishedByCurrentUserFinishedJobsEnabled()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.TRUE, Boolean.class);
		controller.initialize(new Div());
		final long jobPk = 1;

		final CronJobHistoryModel cronJobHistory = createProcess(true, jobPk, CronJobStatus.RUNNING);
		controller.getProcessesListModel().add(cronJobHistory);


		final CronJobHistoryModel finished = createProcess(true, jobPk, CronJobStatus.FINISHED);
		controller.updateProcesses(Lists.newArrayList(finished));

		assertThat(controller.getProcessesListModel()).containsOnly(finished);
		assertThat(controller.getProcessesListModel().get(0).getStatus()).isEqualTo(CronJobStatus.FINISHED);
		assertThat(openBtn.getSclass()).doesNotContain(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN);
	}

	@Test
	public void testFinishedByCurrentShowFinishedDisabled()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.FALSE, Boolean.class);
		controller.initialize(new Div());
		final long jobPk = 1;

		final CronJobHistoryModel cronJobHistory = createProcess(true, jobPk, CronJobStatus.RUNNING);
		controller.getProcessesListModel().add(cronJobHistory);


		final CronJobHistoryModel finished = createProcess(true, jobPk, CronJobStatus.FINISHED);
		controller.updateProcesses(Lists.newArrayList(finished));

		assertThat(controller.getProcessesListModel()).isEmpty();
		assertThat(openBtn.getSclass()).doesNotContain(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN);
	}

	@Test
	public void testFinishedByOtherUserFinishedJobsAndShowGlobalEnabled()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.TRUE, Boolean.class);
		widgetSettings.put(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.TRUE, Boolean.class);
		controller.initialize(new Div());
		final long jobPk = 1;

		final CronJobHistoryModel cronJobHistory = createProcess(false, jobPk, CronJobStatus.RUNNING);
		controller.getProcessesListModel().add(cronJobHistory);


		final CronJobHistoryModel finished = createProcess(false, jobPk, CronJobStatus.FINISHED);
		controller.updateProcesses(Lists.newArrayList(finished));

		assertThat(controller.getProcessesListModel()).containsOnly(finished);
		assertThat(controller.getProcessesListModel().get(0).getStatus()).isEqualTo(CronJobStatus.FINISHED);
		assertThat(openBtn.getSclass()).doesNotContain(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN);
	}

	@Test
	public void testFinishedByOtherUserShowGlobalEnabledShowFinishedDisabled()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.FALSE, Boolean.class);
		widgetSettings.put(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.TRUE, Boolean.class);
		controller.initialize(new Div());
		final long jobPk = 1;

		final CronJobHistoryModel cronJobHistory = createProcess(false, jobPk, CronJobStatus.RUNNING);
		controller.getProcessesListModel().add(cronJobHistory);


		final CronJobHistoryModel finished = createProcess(false, jobPk, CronJobStatus.FINISHED);
		controller.updateProcesses(Lists.newArrayList(finished));

		assertThat(controller.getProcessesListModel()).isEmpty();
		assertThat(openBtn.getSclass()).doesNotContain(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN);
	}

	@Test
	public void testNotFinishedStatuses()
	{
		widgetSettings.put(ProcessesController.SETTING_SHOW_FINISHED_JOBS, Boolean.FALSE, Boolean.class);
		widgetSettings.put(ProcessesController.SETTING_SHOW_GLOBAL_JOBS, Boolean.TRUE, Boolean.class);
		controller.initialize(new Div());

		final CronJobHistoryModel running = createProcess(false, 1, CronJobStatus.RUNNING);
		final CronJobHistoryModel aborted = createProcess(false, 2, CronJobStatus.ABORTED);
		final CronJobHistoryModel paused = createProcess(false, 3, CronJobStatus.PAUSED);
		final CronJobHistoryModel runStart = createProcess(false, 4, CronJobStatus.RUNNINGRESTART);
		final CronJobHistoryModel unknown = createProcess(false, 5, CronJobStatus.UNKNOWN);
		final List<CronJobHistoryModel> updated = Lists.newArrayList(running, aborted, paused, runStart, unknown);

		controller.updateProcesses(updated);

		assertThat(controller.getProcessesListModel()).containsOnly(running, aborted, paused, runStart, unknown);
		verify(openBtn).setSclass(contains(ProcessesController.YW_PROCESSES_OPENING_BTN_SPIN));
	}

	@Test
	public void testProcessesSorted()
	{
		// given
		controller.initialize(new Div());

		final CronJobHistoryModel process1 = createProcess(true, 1, CronJobStatus.RUNNING, new Date(300));
		final CronJobHistoryModel process2 = createProcess(true, 2, CronJobStatus.RUNNING, new Date(200));
		final CronJobHistoryModel process3 = createProcess(true, 3, CronJobStatus.RUNNING, new Date(100));
		final CronJobHistoryModel process4 = createProcess(true, 4, CronJobStatus.RUNNING, null);

		// when
		controller.updateProcesses(Lists.newArrayList(process4, process1, process3, process2));

		// then
		assertThat(controller.getProcessesListModel()).containsExactly(process1, process2, process3, process4);
	}

	@Test
	public void testUnseenProcess()
	{
		// given
		controller.initialize(new Div());
		final CronJobHistoryModel newProcess = createProcess(true, 1, CronJobStatus.RUNNING, new Date());

		// when
		controller.updateProcesses(Lists.newArrayList(newProcess));

		// then
		assertThat(openBtn.getSclass()).contains(ProcessesController.YW_PROCESSES_UNSEEN);
		assertThat(controller.getValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Set.class)).containsOnly(newProcess);
	}

	@Test
	public void testUnseenStatusChange()
	{
		// given
		controller.initialize(new Div());
		final CronJobHistoryModel process = createProcess(true, 1, CronJobStatus.RUNNING, new Date());
		controller.getProcessesListModel().add(process);
		final CronJobHistoryModel updatedProcess = createProcess(true, 1, CronJobStatus.FINISHED, new Date());

		// when
		controller.updateProcesses(Lists.newArrayList(updatedProcess));

		// then
		assertThat(openBtn.getSclass()).contains(ProcessesController.YW_PROCESSES_UNSEEN);
		assertThat(controller.getValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Set.class)).containsOnly(updatedProcess);
	}

	@Test
	public void testOpenButtonNotMarkedAfterInitialization()
	{
		// when
		controller.initialize(new Div());

		// then
		assertThat(openBtn.getSclass()).isNull();
	}

	@Test
	public void testOpenButtonMarkedAfterInitialization()
	{
		// given
		final CronJobHistoryModel process = createProcess(true, 1, CronJobStatus.RUNNING, new Date());
		controller.setValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Sets.newHashSet(process));

		// when
		controller.initialize(new Div());

		// then
		assertThat(openBtn.getSclass()).contains(ProcessesController.YW_PROCESSES_UNSEEN);
	}

	@Test
	public void testRendererSeen() throws Exception
	{
		// given
		final ArgumentCaptor<ListitemRenderer> capturedRenderer = captureRenderer();

		final CronJobHistoryModel process = createProcess(true, 1, CronJobStatus.RUNNING, new Date());
		final Listitem listitem = new Listitem();

		// when
		capturedRenderer.getValue().render(listitem, process, 0);

		// then
		assertThat(listitem.getSclass()).isNull();
	}

	@Test
	public void testRendererUnseen() throws Exception
	{
		// given
		final ArgumentCaptor<ListitemRenderer> capturedRenderer = captureRenderer();

		final CronJobHistoryModel process = createProcess(true, 1, CronJobStatus.RUNNING, new Date());
		controller.setValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Sets.newHashSet(process));
		final Listitem listitem = new Listitem();

		// when
		capturedRenderer.getValue().render(listitem, process, 0);

		// then
		assertThat(listitem.getSclass()).contains(ProcessesController.YW_PROCESSES_UNSEEN);
		assertThat(controller.getValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Set.class)).contains(process);
	}

	private ArgumentCaptor<ListitemRenderer> captureRenderer()
	{
		doReturn(mock(WidgetComponentRenderer.class)).when(controller).getRenderer();
		final ArgumentCaptor<ListitemRenderer> capturedRenderer = ArgumentCaptor.forClass(ListitemRenderer.class);
		controller.initialize(new Div());
		verify(processesListbox).setItemRenderer(capturedRenderer.capture());
		return capturedRenderer;
	}

	@Test
	public void testOpenBtnClearsUnseenMarker()
	{
		// given
		UITools.addSClass(openBtn, ProcessesController.YW_PROCESSES_UNSEEN);

		// when
		executeViewEvent(ProcessesController.COMP_ID_OPEN_BTN, Events.ON_CLICK);

		// then
		assertThat(openBtn.getSclass()).isNull();
	}

	@Test
	public void testCloseBtnClearsUnseenMarkers()
	{
		testClickClearsUnseenMarkers(ProcessesController.COMP_ID_CLOSE_BTN);
	}

	@Test
	public void testAutocloseClearsUnseenMarkers()
	{
		testClickClearsUnseenMarkers(ProcessesController.COMP_ID_AUTO_CLOSE_COMPONENT);
	}

	private void testClickClearsUnseenMarkers(final String componentId)
	{
		// given
		controller.initialize(new Div());
		controller.setValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Lists.newArrayList(new CronJobHistoryModel()));

		final Div item1 = new Div();
		final Div item2 = new Div();
		UITools.addSClass(item1, ProcessesController.YW_PROCESSES_UNSEEN);
		UITools.addSClass(item2, ProcessesController.YW_PROCESSES_UNSEEN);

		when(processesListbox.queryAll('.' + ProcessesController.YW_PROCESSES_UNSEEN)).thenReturn(Lists.newArrayList(item1, item2));

		// when
		executeViewEvent(componentId, Events.ON_CLICK);

		// then
		assertThat(openBtn.getSclass()).isNull();
		assertThat(controller.getValue(ProcessesController.MODEL_UNSEEN_PROCESSES, Set.class)).isNull();
		assertThat(item1.getSclass()).isNull();
		assertThat(item2.getSclass()).isNull();
	}

	@Test
	public void testBroadcastEvent()
	{
		//given
		final CronJobHistoryModel cronJobHistoryModel = new CronJobHistoryModel();
		cronJobHistoryModel.setCronJobCode(CRON_JOB_CODE);
		cronJobHistoryModel.setStatus(CronJobStatus.RUNNING);
		controller.getProcessesListModel().add(cronJobHistoryModel);

		//when
		controller.onApplicationHeartbeat(new DefaultCockpitEvent("name", null, null));

		//then
		cronJobHistoryFacade.getCronJobHistory(argThat(new ArgumentMatcher<CronJobHistoryDataQuery>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o instanceof List && ((List<String>) o).contains(CRON_JOB_CODE);
			}
		}));
	}

	@Test
	public void checkProgressDataAfterEmptyUpdate()
	{
		//when
		controller.updateProcesses(new ArrayList<>());

		//then
		assertThat(controller.getProgressDataMap()).isEmpty();
	}

	@Test
	public void checkProgressDataAfterRefresh()
	{
		//given
		final CronJobHistoryModel modelA = createProcess(true, JOB_PK, CronJobStatus.RUNNING);
		controller.updateProcesses(Lists.newArrayList(modelA));

		final CronJobHistoryModel modelB = createProcess(true, JOB_PK + 1, CronJobStatus.FINISHED);
		final CronJobHistoryModel modelC = createProcess(true, JOB_PK + 2, CronJobStatus.RUNNING);

		when(cronJobHistoryFacade.getCronJobHistory(any(List.class))).thenReturn(Lists.newArrayList(modelB, modelC));

		//when
		controller.refreshRunningProcesses();

		//then
		assertThat(controller.getProgressDataMap()).hasSize(2);
	}

	@Test
	public void checkProgressDataAfterEmptyDataResult()
	{
		//when
		controller.setProcesses(new ArrayList<>());

		//then
		assertThat(controller.getProgressDataMap()).isEmpty();
	}

	@Test
	public void checkProgressDataAfterDataResult()
	{
		//given
		final CronJobHistoryModel modelA = createProcess(true, JOB_PK, CronJobStatus.RUNNING);
		controller.setProcesses(Lists.newArrayList(modelA));

		final CronJobHistoryModel modelB = createProcess(true, JOB_PK + 1, CronJobStatus.FINISHED);
		final CronJobHistoryModel modelC = createProcess(true, JOB_PK + 2, CronJobStatus.RUNNING);

		//when
		controller.setProcesses(Lists.newArrayList(modelB, modelC));

		//then
		assertThat(controller.getProgressDataMap()).hasSize(1);
	}

	@Test
	public void lookupIsStoppedOnHearBeat()
	{
		//given
		doReturn(Boolean.TRUE).when(startedCronJobLookupTimer).isRunning();
		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		//when
		executeGlobalEvent(CockpitMainWindowComposer.HEARTBEAT_EVENT, CockpitEvent.SESSION, cockpitEvent);
		//then
		verify(startedCronJobLookupTimer).stop();
	}

	@Test
	public void continueMissingCronjobsLookupOnHeartbeat()
	{
		//given
		final CockpitEvent cockpitEvent = mock(CockpitEvent.class);
		controller.getStartedCronJobsToLookup().add(THE_CODE);
		final CronJobHistoryModel cronJobHistory = mock(CronJobHistoryModel.class);
		when(cronJobHistory.getCronJobCode()).thenReturn(THE_CODE);
		when(cronJobHistoryFacade.getCronJobHistory(Lists.newArrayList(THE_CODE))).thenReturn(Lists.newArrayList(cronJobHistory));
		//when
		executeGlobalEvent(CockpitMainWindowComposer.HEARTBEAT_EVENT, CockpitEvent.SESSION, cockpitEvent);
		//then
		verify(controller).updateProcesses(Lists.newArrayList(cronJobHistory));
	}

	@Test
	public void lookupIsStoppedOnLastMissingProcessUpdate()
	{
		//given
		final CronJobHistoryModel cronJobHistory = mock(CronJobHistoryModel.class);
		when(cronJobHistory.getCronJobCode()).thenReturn(THE_CODE);
		doReturn(Boolean.TRUE).when(startedCronJobLookupTimer).isRunning();
		controller.getStartedCronJobsToLookup().add(THE_CODE);

		//when
		controller.updateProcesses(Lists.newArrayList(cronJobHistory));
		//then
		verify(startedCronJobLookupTimer).stop();
	}

	@Test
	public void lookupIsStartedOnNewCronJobCode()
	{
		//given
		final CronJobHistoryModel cronJobHistory = mock(CronJobHistoryModel.class);
		when(cronJobHistory.getCronJobCode()).thenReturn(THE_CODE);
		when(cronJobHistoryFacade.getCronJobHistory(THE_CODE)).thenReturn(Collections.emptyList());
		doReturn(Boolean.FALSE).when(startedCronJobLookupTimer).isRunning();
		//when
		executeInputSocketEvent(ProcessesController.SOCKET_IN_UPDATE_CRON_JOB, THE_CODE);
		//then
		verify(startedCronJobLookupTimer).start();
	}

	@Test
	public void lookupIsNotStartedOnSecondCronJobCode()
	{
		//given
		controller.getStartedCronJobsToLookup().add(THE_CODE);
		doReturn(Boolean.TRUE).when(startedCronJobLookupTimer).isRunning();
		//when
		executeInputSocketEvent(ProcessesController.SOCKET_IN_UPDATE_CRON_JOB, "code2");
		//then
		verify(startedCronJobLookupTimer, never()).start();
	}

	@Test
	public void testUpdateProcessesOnGlobalEvent()
	{

		executeGlobalEvent(ProcessesController.GLOBAL_EVENT_UPDATE_PROCESS_FOR_CRON_JOB, CockpitEvent.SESSION,
				new DefaultCockpitEvent(ProcessesController.GLOBAL_EVENT_UPDATE_PROCESS_FOR_CRON_JOB, "test", null));

		verify(controller).updateProcessForCronJob("test");
	}

	@Test
	public void verifyUpdateProcessesOnTimerEvent()
	{
		//given
		controller.initialize(new Div());
		controller.getStartedCronJobsToLookup().add(THE_CODE);

		final CronJobHistoryModel cronJobHistory = mock(CronJobHistoryModel.class);
		when(cronJobHistory.getCronJobCode()).thenReturn(THE_CODE);
		when(cronJobHistoryFacade.getCronJobHistory(Lists.newArrayList(THE_CODE))).thenReturn(Lists.newArrayList(cronJobHistory));
		//when
		CockpitTestUtil.simulateEvent(startedCronJobLookupTimer, Events.ON_TIMER, new Integer(1));
		//then
		verify(controller).updateProcesses(Lists.newArrayList(cronJobHistory));
	}

	private CronJobHistoryModel createProcess(final boolean executedByCurrentUser, final long cronJobPk,
			final CronJobStatus status)
	{
		return createProcess(executedByCurrentUser, cronJobPk, status, null);
	}

	private CronJobHistoryModel createProcess(final boolean executedByCurrentUser, final long cronJobPk,
			final CronJobStatus status, final Date startTime)
	{
		final CronJobHistoryModel job = new CronJobHistoryModel();
		BackofficeTestUtil.setPk(job, cronJobPk);
		job.setStatus(status);
		job.setStartTime(startTime);
		if (executedByCurrentUser)
		{
			job.setUserUid(CURRENT_USER);
		}
		else
		{
			job.setUserUid("testUser");
		}
		return job;
	}

	@Override
	protected ProcessesController getWidgetController()
	{
		return controller;
	}
}
