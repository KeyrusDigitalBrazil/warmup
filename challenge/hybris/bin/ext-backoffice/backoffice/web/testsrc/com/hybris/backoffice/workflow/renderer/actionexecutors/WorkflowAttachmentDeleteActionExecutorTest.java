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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.workflow.WorkflowEventPublisher;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowAttachmentDeleteActionExecutorTest
{
	@InjectMocks
	private WorkflowAttachmentDeleteActionExecutor executor;

	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private WorkflowEventPublisher workflowEventPublisher;
	@Mock
	private NotificationService notificationService;

	@Mock
	private WorkflowItemAttachmentModel workflowItemAttachment;
	@Mock
	private WorkflowModel workflow;

	@Before
	public void setUp() throws ObjectNotFoundException
	{
		when(workflowItemAttachment.getWorkflow()).thenReturn(workflow);
		when(objectFacade.reload(workflow)).thenReturn(workflow);
	}

	@Test
	public void testEventsPublishedAfterObjectDeletion()
	{
		//when
		executor.accept(workflowItemAttachment);

		//then
		verify(workflowEventPublisher).publishWorkflowAttachmentDeletedEvent(workflowItemAttachment);
		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(workflow);
	}

	@Test
	public void testEventsNotPublishedWhenDeletionFailed() throws ObjectDeletionException
	{
		//given
		doThrow(new ObjectDeletionException("attachment")).when(objectFacade).delete(workflowItemAttachment);

		//when
		executor.accept(workflowItemAttachment);

		//then
		verifyZeroInteractions(workflowEventPublisher);
	}

	@Test
	public void testEventNotPublishedWhenReloadingFailed() throws ObjectNotFoundException
	{
		//given
		doThrow(new ObjectNotFoundException("workflow")).when(objectFacade).reload(workflow);

		//when
		executor.accept(workflowItemAttachment);

		//then
		verify(workflowEventPublisher).publishWorkflowAttachmentDeletedEvent(workflowItemAttachment);
		verify(workflowEventPublisher, never()).publishWorkflowUpdatedEvent(workflow);
	}
}
