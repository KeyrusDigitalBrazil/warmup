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
package com.hybris.backoffice.widgets.workflowactions.renderer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;

import com.hybris.backoffice.renderer.utils.UIDateRendererProvider;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class DashboardWorkflowActionsRendererTest extends AbstractCockpitngUnitTest<DashboardWorkflowActionsRenderer>
{

	@Spy
	@InjectMocks
	private DashboardWorkflowActionsRenderer renderer;

	@Mock
	private TimeService timeService;
	@Mock
	private WorkflowActionModel workflowAction;
	@Mock
	private WorkflowModel workflow;
	@Mock
	private UIDateRendererProvider uiDateRendererProvider;
	@Mock
	private SessionService sessionService;
	@Mock
	private LabelService labelService;
	@Mock
	private Object config;
	@Mock
	private DataType dataType;

	private WidgetInstanceManager widgetInstanceManager;


	@Override
	protected Class<? extends DashboardWorkflowActionsRenderer> getWidgetType()
	{
		return DashboardWorkflowActionsRenderer.class;
	}

	@Before
	public void setUp()
	{
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
	}

	@Test
	public void shouldCreateOnlyTopAndMiddleContent()
	{
		// given
		final Listitem listitem = new Listitem();
		doReturn(new Label()).when(renderer).createNoOfAttachmentsLabel(widgetInstanceManager, workflowAction);

		// when
		renderer.render(listitem, config, workflowAction, dataType, widgetInstanceManager);

		// then
		verify(renderer).createTopContent(any(), any(), any());
		verify(renderer).createMiddleContent(widgetInstanceManager, workflowAction);
		verify(renderer, never()).createBottomContent(any(), any());
	}

	@Test
	public void shouldCreateTopContentWithDateAndNoOfAttachements()
	{
		// given
		final String title = new String();
		doReturn(new Label()).when(renderer).createNoOfAttachmentsLabel(widgetInstanceManager, workflowAction);

		// when
		renderer.createTopContent(title, workflowAction, widgetInstanceManager);

		// then
		verify(renderer).createTitleButton(title, workflowAction, widgetInstanceManager);
		verify(renderer).createDateLabel(workflowAction);
		verify(renderer).createNoOfAttachmentsLabel(widgetInstanceManager, workflowAction);
	}

}
