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

import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zul.Listitem;

import com.hybris.backoffice.renderer.utils.UIDateRendererProvider;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class DefaultWorkflowActionsRendererTest extends AbstractCockpitngUnitTest<DefaultWorkflowActionsRenderer>
{

	@InjectMocks
	private DefaultWorkflowActionsRenderer renderer;

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

	private WidgetInstanceManager widgetInstanceManager;


	@Override
	protected Class<? extends DefaultWorkflowActionsRenderer> getWidgetType()
	{
		return DefaultWorkflowActionsRenderer.class;
	}

	@Before
	public void setUp()
	{
		widgetInstanceManager = CockpitTestUtil.mockWidgetInstanceManager();
	}

	@Test
	public void shouldNotBreakOnNullActivatedDate()
	{
		when(workflowAction.getName()).thenReturn("workflowAction");
		when(workflowAction.getWorkflow()).thenReturn(workflow);
		when(workflow.getName()).thenReturn("workflow");
		when(workflow.getAttachments()).thenReturn(Collections.emptyList());

		renderer.render(new Listitem(), null, workflowAction, null, widgetInstanceManager);
	}

}
