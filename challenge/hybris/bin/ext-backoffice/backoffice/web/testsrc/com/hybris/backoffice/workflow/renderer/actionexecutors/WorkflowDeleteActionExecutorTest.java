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
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.workflow.WorkflowEventPublisher;
import com.hybris.backoffice.workflow.WorkflowFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowDeleteActionExecutorTest
{

	@Mock
	WorkflowFacade workflowFacade;
	@Mock
	WorkflowEventPublisher workflowEventPublisher;
	@Mock
	NotificationService notificationService;
	@Spy
	@InjectMocks
	final WorkflowDeleteActionExecutor workflowDeleteActionExecutor = new WorkflowDeleteActionExecutor();

	@Test
	public void shouldNotificationBeDisplayedAndEventBeSendWhenWorkflowIsSuccessfullyDeleted() throws ObjectDeletionException
	{
		// given
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		doNothing().when(workflowFacade).deleteWorkflow(any());

		// when
		final boolean result = workflowDeleteActionExecutor.apply(workflowModel);

		// then
		assertThat(result).isTrue();
		then(workflowDeleteActionExecutor).should().notifyUser(any(), any(), eq(NotificationEvent.Level.SUCCESS));
		then(workflowEventPublisher).should().publishWorkflowActionsDeletedEvent(any());
	}

	@Test
	public void shouldNotificationBeDisplayedAndEventNotBeSendWhenWorkflowIsNotDeleted() throws ObjectDeletionException
	{
		// given
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		doThrow(ObjectDeletionException.class).when(workflowFacade).deleteWorkflow(any());

		// when
		final boolean result = workflowDeleteActionExecutor.apply(workflowModel);

		// then
		assertThat(result).isFalse();
		then(workflowDeleteActionExecutor).should().notifyUser(any(), any(), eq(NotificationEvent.Level.FAILURE));
		then(workflowEventPublisher).should(never()).publishWorkflowActionsDeletedEvent(any());
	}

}
