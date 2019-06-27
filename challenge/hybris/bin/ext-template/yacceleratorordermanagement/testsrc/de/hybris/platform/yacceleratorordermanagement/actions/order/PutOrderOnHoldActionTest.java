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
package de.hybris.platform.yacceleratorordermanagement.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.onhold.service.OrderOnHoldService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PutOrderOnHoldActionTest
{
	@InjectMocks
	private PutOrderOnHoldAction putOrderOnHoldAction;
	@Mock
	private OrderOnHoldService orderOnHoldService;
	@Mock
	private ModelService modelService;

	private OrderProcessModel orderProcessModel;
	private OrderModel orderModel;

	@Before
	public void setup()
	{
		orderModel = new OrderModel();
		orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExecuteActionWhenOrderProcessIsNull() throws Exception
	{
		putOrderOnHoldAction.executeAction(null);
	}

	@Test
	public void testExecuteActionSuccess() throws Exception
	{
		putOrderOnHoldAction.executeAction(orderProcessModel);

		assertTrue(OrderStatus.ON_HOLD.toString().equals(orderModel.getStatus().toString()));
	}
}
