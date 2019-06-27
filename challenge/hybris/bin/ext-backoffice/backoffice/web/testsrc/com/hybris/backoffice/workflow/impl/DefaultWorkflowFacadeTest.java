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

import static com.hybris.backoffice.workflow.impl.DefaultWorkflowFacade.AD_HOC_WORKFLOW_DUMMY_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowStatus;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.workflow.WorkflowsTypeFacade;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;


@RunWith(MockitoJUnitRunner.class)
public class DefaultWorkflowFacadeTest
{

	@InjectMocks
	@Spy
	private DefaultWorkflowFacade facade;

	@Mock
	private WorkflowTemplateService workflowTemplateService;
	@Mock
	private WorkflowService workflowService;
	@Mock
	private WorkflowAttachmentService workflowAttachmentService;
	@Mock
	private UserService userService;
	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private WorkflowTemplateModel template;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private WorkflowsTypeFacade workflowsTypeService;
	@Mock
	private WorkflowModel workflow;
	@Mock
	private WorkflowModel attachment1;
	@Mock
	private WorkflowModel attachment2;
	@Mock
	private WorkflowModel attachment3;
	@Mock
	private WorkflowActionModel workflowActionModel;
	@Mock
	private ObjectFacade objectFacade;

	private UserModel currentUser;
	private Map<Locale, String> localizedName;
	private Map<Locale, String> localizedDesc;
	private List<ItemModel> attachments;

	@Before
	public void setUp()
	{
		localizedName = new HashMap<>();
		localizedName.put(Locale.CANADA, "a");
		localizedName.put(Locale.FRENCH, "b");

		localizedDesc = new HashMap<>();
		localizedDesc.put(Locale.CANADA, "c");
		localizedDesc.put(Locale.FRENCH, "d");

		attachments = Lists.newArrayList(attachment1, attachment2, attachment3);
		when(userService.getCurrentUser()).thenReturn(currentUser);
		when(workflow.getActions()).thenReturn(Collections.singletonList(workflowActionModel));
	}

	@Test
	public void testCreateWorkflow() throws ObjectSavingException
	{
		when(workflowService.createWorkflow(anyString(), same(template), eq(attachments), any(UserModel.class)))
				.thenReturn(workflow);
		doReturn(workflow).when(objectFacade).save(any(WorkflowModel.class), any(DefaultContext.class));

		facade.createWorkflow(template, localizedName, localizedDesc, attachments);

		verifyLocalizedNameAndDescApplied();
		verify(objectFacade).save(eq(workflow), any(DefaultContext.class));
		verify(facade).assureAttachmentsWithoutDuplicates(attachments);

	}

	@Test
	public void testCreateAdHocWorkflow() throws ObjectSavingException
	{
		when(workflowService.createAdhocWorkflow(eq(AD_HOC_WORKFLOW_DUMMY_NAME), eq(attachments), same(currentUser)))
				.thenReturn(workflow);
		final PrincipalModel assignee = mock(PrincipalModel.class);
		doReturn(Boolean.TRUE).when(workflowService).assignUser(assignee, workflow);
		doReturn(workflow).when(objectFacade).save(any(WorkflowModel.class), any(DefaultContext.class));

		facade.createAdHocWorkflow(assignee, localizedName, localizedDesc, attachments);

		verify(workflowService).assignUser(assignee, workflow);
		verifyLocalizedNameAndDescApplied();
		verify(facade).assureAttachmentsWithoutDuplicates(attachments);
	}

	@Test
	public void testCreateAdHocWorkflowNotSavedOnlyIfAssignmentFails() throws ObjectSavingException {
		when(workflowService.createAdhocWorkflow(eq(AD_HOC_WORKFLOW_DUMMY_NAME), eq(attachments), same(currentUser)))
				.thenReturn(workflow);
		final PrincipalModel assignee = mock(PrincipalModel.class);
		doReturn(Boolean.FALSE).when(workflowService).assignUser(assignee, workflow);

		facade.createAdHocWorkflow(assignee, localizedName, localizedDesc, attachments);

		verify(workflowService).assignUser(assignee, workflow);
		verify(objectFacade, never()).save(eq(workflow), any(DefaultContext.class));
		verify(facade).assureAttachmentsWithoutDuplicates(attachments);
	}

	@Test
	public void attachmentsAreDistinct()
	{
		attachments.add(attachment3);

		final List<ItemModel> distinct = facade.assureAttachmentsWithoutDuplicates(attachments);

		assertThat(attachments).hasSize(4);
		assertThat(distinct).hasSize(3);
		assertThat(distinct).containsOnly(attachment1, attachment2, attachment3);
	}

	protected void verifyLocalizedNameAndDescApplied()
	{
		localizedName.forEach((loc, name) -> verify(workflow).setName(name, loc));
		localizedDesc.forEach((loc, desc) -> verify(workflow).setDescription(desc, loc));
	}

	@Test
	public void shouldReturnPlannedStatus()
	{
		//given
		when(new Boolean(workflowService.isPlanned(workflow))).thenReturn(Boolean.TRUE);

		//when
		final WorkflowStatus workflowStatus = facade.getWorkflowStatus(workflow);

		//then
		assertThat(workflowStatus).isEqualTo(WorkflowStatus.PLANNED);
	}

	@Test
	public void shouldReturnRunningStatus()
	{
		//given
		when(new Boolean(workflowService.isRunning(workflow))).thenReturn(Boolean.TRUE);

		//when
		final WorkflowStatus workflowStatus = facade.getWorkflowStatus(workflow);

		//then
		assertThat(workflowStatus).isEqualTo(WorkflowStatus.RUNNING);
	}

	@Test
	public void shouldReturnRunningStatusForPaused()
	{
		//given
		when(new Boolean(workflowService.isPaused(workflow))).thenReturn(Boolean.TRUE);

		//when
		final WorkflowStatus workflowStatus = facade.getWorkflowStatus(workflow);

		//then
		assertThat(workflowStatus).isEqualTo(WorkflowStatus.RUNNING);
	}

	@Test
	public void shouldReturnTerminatedStatus()
	{
		//given
		when(new Boolean(workflowService.isTerminated(workflow))).thenReturn(Boolean.TRUE);

		//when
		final WorkflowStatus workflowStatus = facade.getWorkflowStatus(workflow);

		//then
		assertThat(workflowStatus).isEqualTo(WorkflowStatus.TERMINATED);
	}

	@Test
	public void shouldReturnFinishedStatus()
	{
		//given
		when(new Boolean(workflowService.isFinished(workflow))).thenReturn(Boolean.TRUE);

		//when
		final WorkflowStatus workflowStatus = facade.getWorkflowStatus(workflow);

		//then
		assertThat(workflowStatus).isEqualTo(WorkflowStatus.FINISHED);
	}

	@Test
	public void shouldReturnNullWhenStatusIsUnknown()
	{
		//when
		final WorkflowStatus workflowStatus = facade.getWorkflowStatus(workflow);

		//then
		assertThat(workflowStatus).isNull();
	}

	@Test
	public void shouldFacadeCallServiceMethodWhenWorkflowIsToBeTerminated()
	{
		// when
		facade.terminateWorkflow(workflow);

		// then
		verify(workflowProcessingService).terminateWorkflow(workflow);
	}

	@Test
	public void shouldFacadeCallServiceMethodWhenWorkflowIsToBeDeleted() throws ObjectDeletionException
	{
		// when
		facade.deleteWorkflow(workflow);

		// then
		verify(objectFacade).delete(workflow);
	}

	@Test
	public void shouldFacadeCallServiceMethodWhenWorkflowIsToBeStarted()
	{
		// when
		facade.startWorkflow(workflow);

		// then
		verify(workflowProcessingService).startWorkflow(workflow);
	}

	@Test
	public void testCurrentTasksFiltering()
	{
		//given
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		final WorkflowActionModel workflowActionModel1 = new WorkflowActionModel(),
				workflowActionModel2 = new WorkflowActionModel(), workflowActionModel3 = new WorkflowActionModel();
		workflowActionModel1.setStatus(WorkflowActionStatus.COMPLETED);
		workflowActionModel2.setStatus(WorkflowActionStatus.IN_PROGRESS);
		workflowActionModel3.setStatus(WorkflowActionStatus.PENDING);
		when(workflowModel.getActions())
				.thenReturn(Lists.newArrayList(workflowActionModel1, workflowActionModel2, workflowActionModel3));

		//when
		final List<WorkflowActionModel> currentTasks = facade.getCurrentTasks(workflowModel);

		//then
		assertThat(currentTasks).containsExactly(workflowActionModel2);
	}

	@Test
	public void testCountDecisions()
	{
		//given
		final WorkflowModel workflowModel = mock(WorkflowModel.class);
		final WorkflowActionModel workflowActionModel1 = new WorkflowActionModel(),
				workflowActionModel2 = new WorkflowActionModel(), workflowActionModel3 = new WorkflowActionModel();
		workflowActionModel1.setDecisions(
				Lists.newArrayList(new WorkflowDecisionModel(), new WorkflowDecisionModel(), new WorkflowDecisionModel()));
		workflowActionModel2.setDecisions(Lists.newArrayList(new WorkflowDecisionModel()));
		when(workflowModel.getActions())
				.thenReturn(Lists.newArrayList(workflowActionModel1, workflowActionModel2, workflowActionModel3));

		//when
		final int decisionsNumber = facade.countDecisions(workflowModel);

		//then
		assertThat(decisionsNumber).isEqualTo(4);
	}

}
