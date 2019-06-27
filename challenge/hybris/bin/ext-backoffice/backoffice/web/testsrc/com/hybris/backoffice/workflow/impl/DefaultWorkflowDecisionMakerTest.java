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
package com.hybris.backoffice.workflow.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.workflow.WorkflowEventPublisher;
import com.hybris.cockpitng.core.util.impl.TypedSettingsMap;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.engine.WidgetInstanceManager;


@RunWith(MockitoJUnitRunner.class)
public class DefaultWorkflowDecisionMakerTest
{
	@InjectMocks
	private DefaultWorkflowDecisionMaker workflowDecisionMaker;

	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private WorkflowEventPublisher workflowEventPublisher;
	@Mock
	private NotificationService notificationService;

	@Mock
	private WorkflowModel workflow;
	@Mock
	private WorkflowActionModel workflowAction;
	@Mock
	private WorkflowDecisionModel workflowDecision;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private TypedSettingsMap widgetSetting;

	@Test
	public void shouldSendGlobalEventOnUpdatingWorkflowActions()
	{
		//given
		when(workflowAction.getWorkflow()).thenReturn(workflow);
		when(widgetInstanceManager.getWidgetSettings()).thenReturn(widgetSetting);

		//when
		workflowDecisionMaker.makeDecision(workflowAction, workflowDecision, widgetInstanceManager);

		//then
		verify(workflowDecisionMaker.getWorkflowEventPublisher()).publishWorkflowUpdatedEvent(eq(workflow),
				any(DefaultContext.class));
	}
}
