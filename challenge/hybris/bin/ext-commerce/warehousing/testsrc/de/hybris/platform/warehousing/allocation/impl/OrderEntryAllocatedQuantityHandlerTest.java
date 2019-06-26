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
package de.hybris.platform.warehousing.allocation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.warehousing.orderentry.service.OrderEntryQuantityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryAllocatedQuantityHandlerTest
{
	@InjectMocks
	private final OrderEntryAllocatedQuantityHandler handler = new OrderEntryAllocatedQuantityHandler();

	@Mock
	private OrderEntryModel orderEntry;

	@Mock
	private OrderEntryQuantityService orderEntryQuantityService;

	@Before
	public void setup()
	{
		handler.setOrderEntryQuantityService(orderEntryQuantityService);
	}

	@Test
	public void shouldInvokeDynamicAttributeHanlder_WhenConsignmentsExist()
	{
		// Given
		OrderModel order = Mockito.mock(OrderModel.class);
		ConsignmentModel consignment = Mockito.mock(ConsignmentModel.class);

		// When
		when(order.getConsignments()).thenReturn(Sets.newSet(consignment));
		when(orderEntry.getOrder()).thenReturn(order);
		handler.get(orderEntry);

		// Then
		verify(orderEntryQuantityService, times(1)).getQuantityAllocated(orderEntry);
	}

}
