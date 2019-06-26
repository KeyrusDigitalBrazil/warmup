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
package de.hybris.platform.warehousing.returns.impl;

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


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class OrderEntryReturnedQuantityHandlerTest
{
	private OrderEntryReturnedQuantityHandler handler;
	private OrderEntryModel entry;

	@Mock
	private OrderEntryQuantityService orderEntryQuantityService;

	@Before
	public void setUp()
	{
		handler = new OrderEntryReturnedQuantityHandler();
		handler.setOrderEntryQuantityService(orderEntryQuantityService);
		entry = new OrderEntryModel();
	}

	@Test
	public void shouldGetValueWhenCompletedRefundEntriesPresent()
	{
		when(orderEntryQuantityService.getQuantityReturned(entry)).thenReturn(Long.valueOf(7L));
		final long qtyReturned = handler.get(entry).longValue();
		assertEquals(7L, qtyReturned);
	}

	@Test
	public void shouldGetZeroWhenNoReturnEntry()
	{
		final long qtyReturned = handler.get(entry).longValue();
		assertEquals(0L, qtyReturned);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(entry, Long.valueOf(0L));
	}
}
