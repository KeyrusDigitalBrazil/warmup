/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingfacades.strategies;

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Test class for testing the functionality of the TicketOrdersAssociationStrategy.
 */
@UnitTest
public class TicketOrdersAssociationStrategyTest
{
	@InjectMocks
	private TicketOrdersAssociationStrategy ticketOrdersAssociationStrategy;

	@Mock
	private Converter<OrderModel, TicketAssociatedData> ticketAssociationCoverter;


	/**
	 * Tests setup before each test run.
	 */
	@Before
	public void setup()
	{
		ticketOrdersAssociationStrategy = new TicketOrdersAssociationStrategy();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test should return an empty map if user dose not have any orders.
	 */
	@Test
	public void shouldReturnEmptyMap()
	{
		UserModel userModel = Mockito.mock(UserModel.class);
		Mockito.when(userModel.getOrders()).thenReturn(Collections.emptyList());

		Map<String, List<TicketAssociatedData>> result = ticketOrdersAssociationStrategy.getObjects(userModel);

		Assert.assertEquals(Collections.emptyMap(), result);
	}

	/**
	 * Test should return an map which contents 2 ticket associate data.
	 */
	@Test
	public void shouldReturnOrderObjectsMap()
	{
		UserModel userModel = Mockito.mock(UserModel.class);
		OrderModel order1 = Mockito.mock(OrderModel.class);
		OrderModel order2 = Mockito.mock(OrderModel.class);
		TicketAssociatedData data1 = new TicketAssociatedData();
		TicketAssociatedData data2 = new TicketAssociatedData();
		Mockito.when(userModel.getOrders()).thenReturn(Lists.newArrayList(order1, order2));
		Mockito.when(ticketAssociationCoverter.convert(order1)).thenReturn(data1);
		Mockito.when(ticketAssociationCoverter.convert(order2)).thenReturn(data2);

		Map<String, List<TicketAssociatedData>> result = ticketOrdersAssociationStrategy.getObjects(userModel);

		Assert.assertEquals(data1, result.get("Order").get(0));
		Assert.assertEquals(data2, result.get("Order").get(1));
	}

	/**
	 * Test of the strategy getter method.
	 */
	@Test
	public void testGetter()
	{
		Assert.assertEquals(ticketAssociationCoverter, ticketOrdersAssociationStrategy.getTicketAssociationCoverter());
	}
}
