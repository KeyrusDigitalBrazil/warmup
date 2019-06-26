/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.taskassingment.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.warehousing.taskassignment.services.impl.DefaultWarehousingConsignmentWorkflowService;
import de.hybris.platform.warehousing.taskassignment.strategy.UserSelectionStrategy;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Arrays;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousingConsignmentWorkflowServiceTest
{
	public static final String WRONG_VALUE = "WRONG_VALUE";
	@Spy
	@InjectMocks
	private final WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService = new DefaultWarehousingConsignmentWorkflowService();

	protected static final String CONSIGNMENT_TEMPLATE_NAME = "warehousing.consignment.workflow.template";
	protected static final String WORKFLOW_NAME = "consignmentworkflowName";
	protected static final String WORKFLOW_CODE = "workflowCode";
	protected static final String PROCESS_CHOICE = "processChoice";

	@Mock
	private ModelService modelService;
	@Mock
	private UserSelectionStrategy userSelectionStrategy;
	@Mock
	private WorkflowService workflowService;
	@Mock
	private WorkflowTemplateService workflowTemplateService;
	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private UserService userService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessProcessService;
	@Mock
	private Configuration configuration;
	@Mock
	private WorkflowTemplateModel workflowTemplateModel;
	@Mock
	private WorkflowModel workflowModel;
	@Mock
	private UserModel userModel;
	@Mock
	private WorkflowActionModel workflowActionModel;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private WorkflowActionTemplateModel workflowActionTemplateModel;
	@Mock
	private WorkflowDecisionModel workflowDecisionModel;

	@Before
	public void setUp() throws Exception
	{
		doNothing().when(modelService).save(any());
		doNothing().when(workflowProcessingService).decideAction(any(), any());
		doNothing().when(consignmentBusinessProcessService).triggerChoiceEvent(any(), any(), any());

		when(consignmentModel.getTaskAssignmentWorkflow()).thenReturn(WORKFLOW_CODE);
		when(workflowProcessingService.startWorkflow(workflowModel)).thenReturn(true);
		when(workflowProcessingService.terminateWorkflow(workflowModel)).thenReturn(true);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(CONSIGNMENT_TEMPLATE_NAME)).thenReturn(WORKFLOW_NAME);
		when(workflowTemplateService.getWorkflowTemplateForCode(WORKFLOW_NAME)).thenReturn(workflowTemplateModel);
		when(userSelectionStrategy.getUserForConsignmentAssignment(workflowModel)).thenReturn(userModel);
		when(workflowService
				.createWorkflow(anyString(), any(WorkflowTemplateModel.class), anyListOf(ItemModel.class), any(UserModel.class)))
				.thenReturn(workflowModel);
		when(workflowModel.getCode()).thenReturn(WORKFLOW_CODE);
		when(workflowService.getWorkflowForCode(WORKFLOW_CODE)).thenReturn(workflowModel);
		when(workflowService.getWorkflowForCode(WRONG_VALUE)).thenReturn(null);
		when(userService.getCurrentUser()).thenReturn(userModel);
		when(workflowActionModel.getTemplate()).thenReturn(workflowActionTemplateModel);
		when(workflowActionTemplateModel.getCode()).thenReturn(CONSIGNMENT_TEMPLATE_NAME);
		when(workflowModel.getActions()).thenReturn(Arrays.asList(workflowActionModel));
		when(workflowActionModel.getDecisions()).thenReturn(Arrays.asList(workflowDecisionModel));
	}

	@Test
	public void shouldStartConsignmentWorkflow()
	{
		//when
		warehousingConsignmentWorkflowService.startConsignmentWorkflow(consignmentModel);

		//then
		verify(workflowModel, times(1)).setOwner(userModel);
		verify(workflowActionModel, times(1)).setPrincipalAssigned(userModel);
		verify(consignmentModel, times(1)).setTaskAssignmentWorkflow(WORKFLOW_CODE);
	}

	@Test
	public void startConsignmentWorkflowNoTemplate()
	{
		//when
		when(workflowTemplateService.getWorkflowTemplateForCode(any())).thenReturn(null);
		warehousingConsignmentWorkflowService.startConsignmentWorkflow(consignmentModel);

		//then
		verify(consignmentModel, times(0)).setTaskAssignmentWorkflow(WORKFLOW_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void startConsignmentWorkflowwithoutConsignment()
	{
		//when
		warehousingConsignmentWorkflowService.startConsignmentWorkflow(null);
	}

	@Test
	public void shouldTerminateConsignmentWorkflow()
	{
		//when
		warehousingConsignmentWorkflowService.terminateConsignmentWorkflow(consignmentModel);

		//then
		verify(workflowActionModel, times(1)).setStatus(WorkflowActionStatus.TERMINATED);
	}

	@Test
	public void terminateConsignmentWorkflowNoWorkflowCode()
	{
		//when
		when(consignmentModel.getTaskAssignmentWorkflow()).thenReturn(null);
		warehousingConsignmentWorkflowService.terminateConsignmentWorkflow(consignmentModel);

		//then
		verify(workflowService, times(0)).getWorkflowForCode(anyString());
	}

	@Test
	public void terminateConsignmentWorkflowNoWorkflow()
	{
		//when
		when(consignmentModel.getTaskAssignmentWorkflow()).thenReturn(WRONG_VALUE);
		warehousingConsignmentWorkflowService.terminateConsignmentWorkflow(consignmentModel);

		//then
		verify(workflowService, times(1)).getWorkflowForCode(anyString());
		verify(workflowActionModel, times(0)).setStatus(any());
	}

	@Test(expected = IllegalArgumentException.class)
	public void terminateConsignmentWorkflowwithoutConsignment()
	{
		//when
		warehousingConsignmentWorkflowService.terminateConsignmentWorkflow(null);
	}

	@Test
	public void shouldgetConsignmentTaskForTemplateCode()
	{
		//when
		WorkflowActionModel myWorkflowActionModel = warehousingConsignmentWorkflowService
				.getWorkflowActionForTemplateCode(CONSIGNMENT_TEMPLATE_NAME, consignmentModel);

		//then
		verify(workflowActionModel, times(1)).getTemplate();
		verify(workflowActionTemplateModel, times(1)).getCode();
	}

	@Test
	public void noConsignmentTaskAssignedWorkflow()
	{
		//when
		when(consignmentModel.getTaskAssignmentWorkflow()).thenReturn(null);
		WorkflowActionModel myWorkflowActionModel = warehousingConsignmentWorkflowService
				.getWorkflowActionForTemplateCode(CONSIGNMENT_TEMPLATE_NAME, consignmentModel);

		//then
		assertNull(myWorkflowActionModel);
	}

	@Test
	public void shouldDecideWorkflowAction()
	{
		//when
		warehousingConsignmentWorkflowService.decideWorkflowAction(consignmentModel, CONSIGNMENT_TEMPLATE_NAME, PROCESS_CHOICE);

		//then
		verify(workflowActionModel, times(1)).setPrincipalAssigned(any());
	}

	@Test
	public void decideWorkflowActionNoTask()
	{
		//when
		when(consignmentModel.getTaskAssignmentWorkflow()).thenReturn(null);
		warehousingConsignmentWorkflowService.decideWorkflowAction(consignmentModel, CONSIGNMENT_TEMPLATE_NAME, PROCESS_CHOICE);

		//then
		verify(workflowActionModel, times(0)).setPrincipalAssigned(any());
	}

}
