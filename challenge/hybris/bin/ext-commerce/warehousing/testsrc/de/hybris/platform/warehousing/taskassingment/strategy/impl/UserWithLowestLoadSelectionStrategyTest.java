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
package de.hybris.platform.warehousing.taskassingment.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.taskassignment.strategy.impl.UserWithLowestLoadSelectionStrategy;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UserWithLowestLoadSelectionStrategyTest
{
	@Mock
	private WorkflowService workflowService;
	@Spy
	private WorkflowModel workflow = new WorkflowModel();
	@Spy
	private ConsignmentModel consignment = new ConsignmentModel();
	@Spy
	private WorkflowItemAttachmentModel workflowItemAttachment = new WorkflowItemAttachmentModel();
	@Spy
	private WarehouseModel montrealWarehouse = new WarehouseModel();
	@Spy
	private StoreEmployeeGroupModel storeEmployeeGroup = new StoreEmployeeGroupModel();
	@Spy
	private WorkflowActionModel workflowAction = new WorkflowActionModel();
	@Spy
	private PrincipalGroupModel principalGroupModel = new PrincipalGroupModel();
	@Spy
	private PointOfServiceModel posPlateau = new PointOfServiceModel();
	@Spy
	private Set<PrincipalModel> principals = new HashSet<>();
	@Mock
	private UserModel employee1,employee2,employee3,employee4;


	@Spy
	@InjectMocks
	private UserWithLowestLoadSelectionStrategy userSelectionStrategy;

	@Before
	public void setUp() throws Exception
	{
		when(employee1.getName()).thenReturn("employee1");
		when(employee2.getName()).thenReturn("employee2");
		when(employee3.getName()).thenReturn("employee3");
		when(employee4.getName()).thenReturn("employee4");


		when(workflow.getActions()).thenReturn(Arrays.asList(workflowAction));
		when(workflow.getAttachments()).thenReturn(Arrays.asList(workflowItemAttachment));
		when(consignment.getWarehouse()).thenReturn(montrealWarehouse);

		when(montrealWarehouse.getPointsOfService()).thenReturn(Arrays.asList(posPlateau));
		when(posPlateau.getStoreEmployeeGroups()).thenReturn(new HashSet<>(Arrays.asList(storeEmployeeGroup)));
		when(storeEmployeeGroup.getMembers()).thenReturn(principals);

		when(principalGroupModel.getMembers()).thenReturn(principals);
		when(workflowAction.getPrincipalAssigned()).thenReturn(principalGroupModel);
		when(workflowItemAttachment.getItem()).thenReturn(consignment);

		when(workflowService.getWorkflowsForTemplateAndUser(workflow.getJob(), employee1))
				.thenReturn(Arrays.asList(workflow, workflow));
		when(workflowService.getWorkflowsForTemplateAndUser(workflow.getJob(), employee2)).thenReturn(Arrays.asList(workflow));
		when(workflowService.getWorkflowsForTemplateAndUser(workflow.getJob(), employee3))
				.thenReturn(Arrays.asList(workflow, workflow, workflow));
		when(workflowService.getWorkflowsForTemplateAndUser(workflow.getJob(), employee4))
				.thenReturn(Arrays.asList(workflow, workflow));
		workflowAction.setStatus(WorkflowActionStatus.IN_PROGRESS);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_nullContext()
	{
		userSelectionStrategy.getUserForConsignmentAssignment(null);
	}

	@Test
	public void shouldReturn_lowestWorkload()
	{
		principals.addAll(Arrays.asList(employee1, employee2, employee3,employee4));
		assertEquals(employee2.getName(), userSelectionStrategy.getUserForConsignmentAssignment(workflow).getName());
	}

	@Test
	public void shouldReturn_Employee()
	{
		principals.add(employee3);
		assertEquals(employee3.getName(), userSelectionStrategy.getUserForConsignmentAssignment(workflow).getName());
	}

	@Test
	public void shouldReturn_EmployeeSameWorkload()
	{
		principals.addAll(Arrays.asList(employee1,employee4));

		String employeeName = userSelectionStrategy.getUserForConsignmentAssignment(workflow).getName();

		assertTrue(employeeName.equals(employee1.getName()) || employeeName.equals(employee4.getName()));
	}
}


