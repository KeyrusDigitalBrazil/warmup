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
package com.hybris.backoffice.widgets.workflows;

import static com.hybris.backoffice.widgets.workflows.WorkflowsController.SETTING_IS_WORKFLOW_FINISHED_ENABLED;
import static com.hybris.backoffice.widgets.workflows.WorkflowsController.SETTING_IS_WORKFLOW_PLANNED_ENABLED;
import static com.hybris.backoffice.widgets.workflows.WorkflowsController.SETTING_IS_WORKFLOW_RUNNING_ENABLED;
import static com.hybris.backoffice.widgets.workflows.WorkflowsController.SETTING_IS_WORKFLOW_TERMINATED_ENABLED;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;

import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.backoffice.workflow.WorkflowSearchData;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dnd.DragAndDropStrategy;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(Parameterized.class)
public class WorkflowsControllerDataTest
{
	public static final String THE_RENDERER = "theRenderer";
	@Mock
	private Listbox workflowListBox;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	protected WorkflowFacade workflowFacade;
	@Mock
	private DragAndDropStrategy dragAndDropStrategy;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@InjectMocks
	@Spy
	private WorkflowsController controller;

	private final TypedSettingsMap widgetSettings = new TypedSettingsMap();

	@Parameterized.Parameter
	public List<String> settings;

	@Parameterized.Parameter(1)
	public List<WorkflowStatus> statuses;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		CockpitTestUtil.mockZkEnvironment();
		doReturn(widgetInstanceManager).when(controller).getWidgetInstanceManager();
		doReturn(dragAndDropStrategy).when(controller).getDragAndDropStrategy();
		when(widgetInstanceManager.getWidgetSettings()).thenReturn(widgetSettings);
	}

	@Parameterized.Parameters
	public static Collection<Object[]> workflowStatuses()
	{
		return Arrays.asList(new Object[][]
		{
				{ Collections.singletonList(SETTING_IS_WORKFLOW_RUNNING_ENABLED), Collections.singletonList(WorkflowStatus.RUNNING) },
				{ Collections.singletonList(SETTING_IS_WORKFLOW_PLANNED_ENABLED), Collections.singletonList(WorkflowStatus.PLANNED) },
				{ Collections.singletonList(SETTING_IS_WORKFLOW_FINISHED_ENABLED),
						Collections.singletonList(WorkflowStatus.FINISHED) },
				{ Collections.singletonList(SETTING_IS_WORKFLOW_TERMINATED_ENABLED),
						Collections.singletonList(WorkflowStatus.TERMINATED) },
				{ Arrays.asList(SETTING_IS_WORKFLOW_RUNNING_ENABLED, SETTING_IS_WORKFLOW_PLANNED_ENABLED,
						SETTING_IS_WORKFLOW_FINISHED_ENABLED),
						Arrays.asList(WorkflowStatus.RUNNING, WorkflowStatus.PLANNED, WorkflowStatus.FINISHED) },
				{ Arrays.asList(SETTING_IS_WORKFLOW_TERMINATED_ENABLED, SETTING_IS_WORKFLOW_PLANNED_ENABLED,
						SETTING_IS_WORKFLOW_FINISHED_ENABLED),
						Arrays.asList(WorkflowStatus.TERMINATED, WorkflowStatus.PLANNED, WorkflowStatus.FINISHED) } });
	}

	@Test
	public void testLoadedData()
	{
		widgetSettings.put(WorkflowsController.SETTING_WORKFLOW_RENDERER, THE_RENDERER, String.class);
		settings.forEach(setting -> widgetSettings.put(setting, Boolean.valueOf(true), Boolean.class));

		controller.initialize(new Div());

		verify(workflowFacade).getWorkflows(Mockito.argThat(new ArgumentMatcher<WorkflowSearchData>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				final WorkflowSearchData workflowSearchData = (WorkflowSearchData) argument;
				return workflowSearchData.getStatuses().containsAll(statuses)
						&& workflowSearchData.getStatuses().size() == statuses.size();
			}
		}));
		verify(controller).getRenderer(THE_RENDERER);
	}
}
