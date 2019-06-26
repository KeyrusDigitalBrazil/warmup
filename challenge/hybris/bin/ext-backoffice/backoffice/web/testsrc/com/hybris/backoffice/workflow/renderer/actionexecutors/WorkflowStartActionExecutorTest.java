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
package com.hybris.backoffice.workflow.renderer.actionexecutors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.DefaultNotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.workflow.WorkflowConstants;
import com.hybris.backoffice.workflow.WorkflowEventPublisher;
import com.hybris.backoffice.workflow.WorkflowFacade;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowStartActionExecutorTest
{
	@Spy
	@InjectMocks
	private WorkflowStartActionExecutor workflowStartActionExecutor;

	@Mock
	private WorkflowFacade workflowFacade;
	@Mock
	private WorkflowEventPublisher workflowEventPublisher;
	@Spy
	private DefaultNotificationService notificationService;

	@Mock
	private WorkflowModel data;

	@Test
	public void shouldStartingWorkflowPublishGlobalEventAndNotifyUserAboutSuccess()
	{
		// given
		doReturn(Boolean.TRUE).when(workflowFacade).startWorkflow(data);

		// when
		final Boolean result = workflowStartActionExecutor.apply(data);

		// then
		assertThat(result).isTrue();
		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(data);
		verify(notificationService).notifyUser(WorkflowConstants.HANDLER_NOTIFICATION_SOURCE,
				WorkflowConstants.EVENT_TYPE_WORKFLOW_STARTED, NotificationEvent.Level.SUCCESS, workflowStartActionExecutor.getReferenceObject(data));
	}

	@Test
	public void shouldStartingWorkflowWithAProblemNotifyUserAboutFailure()
	{
		// given
		doReturn(Boolean.FALSE).when(workflowFacade).startWorkflow(data);

		// when
		final Boolean result = workflowStartActionExecutor.apply(data);

		// then
		assertThat(result).isFalse();
		verify(notificationService).notifyUser(WorkflowConstants.HANDLER_NOTIFICATION_SOURCE,
				WorkflowConstants.EVENT_TYPE_WORKFLOW_STARTED, NotificationEvent.Level.FAILURE, workflowStartActionExecutor.getReferenceObject(data));
	}
}
