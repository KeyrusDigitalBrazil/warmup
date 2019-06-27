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
package de.hybris.platform.warehousing.shipping.quantityhandlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.warehousing.orderentry.service.OrderEntryQuantityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryShippedQuantityHandlerTest
{
	private final OrderEntryShippedQuantityHandler handler = new OrderEntryShippedQuantityHandler();
	private OrderEntryModel orderEntry;

	@Mock
	private OrderEntryQuantityService orderEntryQuantityService;

	@Before
	public void setUp()
	{
		orderEntry = new OrderEntryModel();
		handler.setOrderEntryQuantityService(orderEntryQuantityService);
	}

	@Test
	public void shouldGetQuantityShipped_SingleConsignmentEntry()
	{
		when(orderEntryQuantityService.getQuantityShipped(orderEntry)).thenReturn(Long.valueOf(3L));
		final Long quantityShipped = handler.get(orderEntry);
		assertEquals(Long.valueOf(3L), quantityShipped);
	}

	@Test
	public void shouldGetQuantityShipped_2ConsignmentEntries()
	{
		when(orderEntryQuantityService.getQuantityShipped(orderEntry)).thenReturn(Long.valueOf(4L));
		final Long quantityShipped = handler.get(orderEntry);
		assertEquals(Long.valueOf(4L), quantityShipped);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(orderEntry, Long.valueOf(5L));
	}
}
