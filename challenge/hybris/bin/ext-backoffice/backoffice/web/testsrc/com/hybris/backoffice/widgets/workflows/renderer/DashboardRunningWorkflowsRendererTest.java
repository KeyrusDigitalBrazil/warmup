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
package com.hybris.backoffice.widgets.workflows.renderer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Listitem;

import com.hybris.backoffice.renderer.utils.UIDateRendererProvider;
import com.hybris.cockpitng.common.renderer.AbstractCustomMenuActionRenderer;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DashboardRunningWorkflowsRendererTest
{
	public static final String TITLE = "title";
	@Mock
	private Object config;
	@Mock
	private WorkflowModel data;
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private DataType dataType;
	@Mock
	private TimeService timeService;
	@Mock
	private AbstractCustomMenuActionRenderer abstractCustomMenuActionRenderer;
	@Mock
	private LabelService labelService;
	@Mock
	private UIDateRendererProvider uiDateRendererProvider;
	@Spy
	@InjectMocks
	private DashboardRunningWorkflowsRenderer runningWorkflowsRenderer;

	@BeforeClass
	public static void initialize()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
		final WorkflowTemplateModel job = mock(WorkflowTemplateModel.class);
		when(job.getName()).thenReturn("template name");
		when(data.getJob()).thenReturn(job);
		when(data.getName()).thenReturn("instance name");
		final Date referenceDate = new Date(123456789);
		when(data.getModifiedtime()).thenReturn(referenceDate);
		when(timeService.getCurrentTime()).thenReturn(new Date(referenceDate.getTime() + 1000000));
	}

	@Test
	public void shouldNotCreateBottomContent()
	{
		// given
		final int size = 6;
		final List<WorkflowActionModel> actions = prepareData(size);
		given(data.getActions()).willReturn(actions);

		// when
		runningWorkflowsRenderer.createContent(data, dataType, wim);

		// then
		verify(runningWorkflowsRenderer).createMiddleContent(wim, data);
		verify(runningWorkflowsRenderer, never()).createBottomContent(any(), any());
	}

	@Test
	public void shouldCreateTitleContentWithButtonsDateAndNoOfAttachements()
	{
		// given
		final int size = 6;
		final List<WorkflowActionModel> actions = prepareData(size);
		given(data.getActions()).willReturn(actions);
		final Listitem listitem = new Listitem();

		// when
		runningWorkflowsRenderer.createTitle(listitem, config, data, dataType, wim);

		// then
		verify(runningWorkflowsRenderer).renderThreeDots(any(), any(), any(), any(), any(), any());
		verify(runningWorkflowsRenderer).createTitleButton(data, wim);
		verify(runningWorkflowsRenderer).createDateLabel(data);
		verify(runningWorkflowsRenderer).createNoOfAttachmentsLabel(wim, data);
	}

	private List<WorkflowActionModel> prepareData(final int size)
	{
		final List<WorkflowActionModel> actions = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			final WorkflowActionModel action = mock(WorkflowActionModel.class);
			when(action.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
			actions.add(action);
		}

		final WorkflowActionModel finishedAction = mock(WorkflowActionModel.class);
		when(finishedAction.getStatus()).thenReturn(WorkflowActionStatus.COMPLETED);

		return actions;
	}

}
