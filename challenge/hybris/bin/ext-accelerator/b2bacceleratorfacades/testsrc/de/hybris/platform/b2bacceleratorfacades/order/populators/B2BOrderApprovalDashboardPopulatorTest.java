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

package de.hybris.platform.b2bacceleratorfacades.order.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BOrderApprovalDashboardPopulatorTest
{
	private static final String WORKFLOW_ACTION_MODEL_CODE = "workflowActionModelCode";

	@Mock
	private WorkflowActionModel workflowActionModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private OrderData b2bOrderData;
	@Mock
	private Converter<OrderModel, OrderData> b2bOrderApprovalDashboardListConverter;
	@Mock
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;


	private final B2BOrderApprovalDashboardPopulator b2BOrderApprovalDashboardPopulator = new B2BOrderApprovalDashboardPopulator();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		b2BOrderApprovalDashboardPopulator.setB2bOrderApprovalDashboardListConverter(b2bOrderApprovalDashboardListConverter);
		b2BOrderApprovalDashboardPopulator.setB2bWorkflowIntegrationService(b2bWorkflowIntegrationService);
		when(b2bWorkflowIntegrationService.getOrderFromAction(workflowActionModel)).thenReturn(orderModel);
		when(b2bOrderApprovalDashboardListConverter.convert(orderModel)).thenReturn(b2bOrderData);
	}

	@Test
	public void testConvert()
	{
		given(workflowActionModel.getCode()).willReturn(WORKFLOW_ACTION_MODEL_CODE);

		final B2BOrderApprovalData b2BOrderApprovalData = new B2BOrderApprovalData();
		b2BOrderApprovalDashboardPopulator.populate(workflowActionModel, b2BOrderApprovalData);

		Assert.assertEquals(WORKFLOW_ACTION_MODEL_CODE, b2BOrderApprovalData.getWorkflowActionModelCode());
		verify(b2bWorkflowIntegrationService).getOrderFromAction(workflowActionModel);
		verify(b2bOrderApprovalDashboardListConverter).convert(orderModel);
		Assert.assertEquals(b2bOrderData, b2BOrderApprovalData.getB2bOrderData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSourceNull()
	{
		b2BOrderApprovalDashboardPopulator.populate(null, mock(B2BOrderApprovalData.class));
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTargetNull()
	{
		b2BOrderApprovalDashboardPopulator.populate(workflowActionModel, null);
		Assert.fail(" IllegalArgumentException should be thrown. ");
	}

}
