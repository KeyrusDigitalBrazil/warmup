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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CompleteOrderActionTest
{
	OrderProcessModel orderProcessModel;
	OrderModel orderModel;
	ConsignmentModel consignment;
	Set<ConsignmentModel> consignments;

	@InjectMocks
	CompleteOrderAction action = new CompleteOrderAction();

	@Mock
	private ModelService modelService;

	@Before
	public void setup()
	{
		orderModel = new OrderModel();
		orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);

		consignment = new ConsignmentModel();
		consignments = new HashSet<ConsignmentModel>();
		orderModel.setConsignments(consignments);
	}

	@Test
	public void shouldOK() throws Exception
	{
		final String transition = action.execute(orderProcessModel);
		assertTrue(AbstractProceduralAction.Transition.OK.toString().equals(transition));
	}

}
