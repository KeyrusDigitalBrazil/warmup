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
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

/**
 * Test class for OrderProcessContextStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderProcessContextStrategyTest
{
	@Mock
	private OrderModel orderModel;

	@Mock
	private OrderProcessModel businessProcessModel;

	@InjectMocks
	private OrderProcessContextStrategy strategy = new OrderProcessContextStrategy();

	@Test
	public void testGetOrderModel() throws Exception
	{
		given(businessProcessModel.getOrder()).willReturn(orderModel);

		final Optional<AbstractOrderModel> orderModelOptional = strategy.getOrderModel(businessProcessModel);

		assertSame(orderModel, orderModelOptional.get());
	}
}
