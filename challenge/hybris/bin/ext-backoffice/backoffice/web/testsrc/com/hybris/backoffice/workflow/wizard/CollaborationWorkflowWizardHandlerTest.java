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
package com.hybris.backoffice.workflow.wizard;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.Before;
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
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class CollaborationWorkflowWizardHandlerTest
{
	@Spy
	@InjectMocks
	private CollaborationWorkflowWizardHandler handler;

	@Mock
	private WorkflowEventPublisher workflowEventPublisher;
	@Mock
	private WorkflowFacade workflowFacade;
	@Mock
	private FlowActionHandlerAdapter adapter;
	@Mock
	private WorkflowModel createdWorkflow;
	@Spy
	private DefaultNotificationService notificationService;

	private Map<String, String> parameters;
	private CollaborationWorkflowWizardForm form;

	@Before
	public void setUp()
	{
		parameters = new HashMap<>();
		form = new CollaborationWorkflowWizardForm();
		form.setName(new HashMap<>());
		form.setDescription(new HashMap<>());
		form.setAttachments(Lists.newArrayList(mock(WorkflowItemAttachmentModel.class)));
		form.setWorkflowTemplate(mock(WorkflowTemplateModel.class));
		form.setAssignee(mock(PrincipalModel.class));

		final WidgetInstanceManager wim = CockpitTestUtil.mockWidgetInstanceManager();
		wim.getModel().put(CollaborationWorkflowWizardHandler.MODEL_WORKFLOW_FORM, form);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);

		when(workflowFacade.createWorkflow(form.getWorkflowTemplate(), form.getName(), form.getDescription(),
				form.getAttachments())).thenReturn(Optional.of(createdWorkflow));

		doReturn(WorkflowConstants.EVENT_LINK_WORKFLOW_DETAILS_DESTINATION).when(handler).getDestination(adapter);
	}

	@Test
	public void testWorkflowCreated()
	{
		handler.perform(new CustomType(), adapter, parameters);

		verify(workflowFacade).createWorkflow(form.getWorkflowTemplate(), form.getName(), form.getDescription(),
				form.getAttachments());

		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testWorkflowCreatedAndStartedSuccess()
	{
		parameters.put(CollaborationWorkflowWizardHandler.PARAM_START_WORKFLOW, Boolean.TRUE.toString());
		doReturn(Boolean.TRUE).when(workflowFacade).startWorkflow(createdWorkflow);
		doReturn(Boolean.TRUE).when(workflowFacade).canBeStarted(createdWorkflow);

		handler.perform(new CustomType(), adapter, parameters);

		verify(workflowFacade).startWorkflow(createdWorkflow);
		verify(notificationService).notifyUser(WorkflowConstants.HANDLER_NOTIFICATION_SOURCE,
				WorkflowConstants.EVENT_TYPE_WORKFLOW_CREATED_AND_STARTED, NotificationEvent.Level.SUCCESS,
				handler.getReferenceObject(createdWorkflow, handler.getDestination(adapter)));
		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testWorkflowStartedFailure()
	{
		doReturn(Boolean.FALSE).when(workflowFacade).startWorkflow(createdWorkflow);
		doReturn(Boolean.TRUE).when(workflowFacade).canBeStarted(createdWorkflow);

		parameters.put(CollaborationWorkflowWizardHandler.PARAM_START_WORKFLOW, Boolean.TRUE.toString());
		handler.perform(new CustomType(), adapter, parameters);

		verify(workflowFacade).startWorkflow(createdWorkflow);
		verify(handler).notifyUser(createdWorkflow, WorkflowConstants.EVENT_TYPE_WORKFLOW_STARTED, NotificationEvent.Level.FAILURE);
		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testWorkflowCannotBeStarted()
	{
		doReturn(Boolean.FALSE).when(workflowFacade).canBeStarted(createdWorkflow);

		parameters.put(CollaborationWorkflowWizardHandler.PARAM_START_WORKFLOW, Boolean.TRUE.toString());
		handler.perform(new CustomType(), adapter, parameters);

		verify(handler).notifyUser(createdWorkflow, WorkflowConstants.EVENT_TYPE_WORKFLOW_STARTED, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void testAdHocWorkflowUserNotAssigned()
	{
		doReturn(Boolean.TRUE).when(workflowFacade).isAdHocTemplate(form.getWorkflowTemplate());
		doReturn(Boolean.FALSE).when(workflowFacade).isCorrectAdHocAssignee(form.getAssignee());

		handler.perform(new CustomType(), adapter, parameters);

		verify(handler).notifyUser(null, CollaborationWorkflowWizardHandler.EVENT_TYPE_WORKFLOW_INCORRECT_ASSIGNEE,
				NotificationEvent.Level.FAILURE);
		verify(workflowEventPublisher, never()).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testAdHocWorkflowWorkflowCannotBeCreated()
	{
		doReturn(Boolean.TRUE).when(workflowFacade).isAdHocTemplate(form.getWorkflowTemplate());
		doReturn(Boolean.TRUE).when(workflowFacade).isCorrectAdHocAssignee(form.getAssignee());
		when(workflowFacade.createAdHocWorkflow(form.getAssignee(), form.getName(), form.getDescription(), form.getAttachments()))
				.thenReturn(Optional.empty());

		handler.perform(new CustomType(), adapter, parameters);

		verify(handler).notifyUser(null, WorkflowConstants.EVENT_TYPE_WORKFLOW_CREATED, NotificationEvent.Level.FAILURE);
		verify(workflowEventPublisher, never()).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testAdHocWorkflowWorkflowCreated()
	{
		doReturn(Boolean.TRUE).when(workflowFacade).isAdHocTemplate(form.getWorkflowTemplate());
		doReturn(Boolean.TRUE).when(workflowFacade).isCorrectAdHocAssignee(form.getAssignee());
		when(workflowFacade.createAdHocWorkflow(form.getAssignee(), form.getName(), form.getDescription(), form.getAttachments()))
				.thenReturn(Optional.of(createdWorkflow));

		handler.perform(new CustomType(), adapter, parameters);

		verify(notificationService).notifyUser(WorkflowConstants.HANDLER_NOTIFICATION_SOURCE,
				WorkflowConstants.EVENT_TYPE_WORKFLOW_CREATED, NotificationEvent.Level.SUCCESS,
				handler.getReferenceObject(createdWorkflow, handler.getDestination(adapter)));
		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testAdHocWorkflowWorkflowCreatedAndStarted()
	{
		doReturn(Boolean.TRUE).when(workflowFacade).isAdHocTemplate(form.getWorkflowTemplate());
		doReturn(Boolean.TRUE).when(workflowFacade).isCorrectAdHocAssignee(form.getAssignee());
		doReturn(Boolean.TRUE).when(workflowFacade).canBeStarted(createdWorkflow);
		doReturn(Boolean.TRUE).when(workflowFacade).startWorkflow(createdWorkflow);
		parameters.put(CollaborationWorkflowWizardHandler.PARAM_START_WORKFLOW, Boolean.TRUE.toString());

		when(workflowFacade.createAdHocWorkflow(form.getAssignee(), form.getName(), form.getDescription(), form.getAttachments()))
				.thenReturn(Optional.of(createdWorkflow));

		handler.perform(new CustomType(), adapter, parameters);

		verify(notificationService).notifyUser(WorkflowConstants.HANDLER_NOTIFICATION_SOURCE,
				WorkflowConstants.EVENT_TYPE_WORKFLOW_CREATED_AND_STARTED, NotificationEvent.Level.SUCCESS,
				handler.getReferenceObject(createdWorkflow, handler.getDestination(adapter)));
		verify(workflowEventPublisher).publishWorkflowUpdatedEvent(createdWorkflow);
	}

	@Test
	public void testNotifyErrorWhenCreatedAndStartedWithoutAttachments()
	{
		doReturn(Boolean.TRUE).when(workflowFacade).canBeStarted(createdWorkflow);
		doReturn(Boolean.TRUE).when(workflowFacade).startWorkflow(createdWorkflow);
		parameters.put(CollaborationWorkflowWizardHandler.PARAM_START_WORKFLOW, Boolean.TRUE.toString());
		form.setAttachments(Lists.emptyList());
		when(workflowFacade.createWorkflow(form.getWorkflowTemplate(), form.getName(), form.getDescription(),
				form.getAttachments())).thenReturn(Optional.of(createdWorkflow));

		handler.perform(new CustomType(), adapter, parameters);

		verify(handler).notifyUser(null, WorkflowConstants.EVENT_TYPE_WORKFLOW_WITHOUT_ATTACHMENTS,
				NotificationEvent.Level.FAILURE);
	}
}
