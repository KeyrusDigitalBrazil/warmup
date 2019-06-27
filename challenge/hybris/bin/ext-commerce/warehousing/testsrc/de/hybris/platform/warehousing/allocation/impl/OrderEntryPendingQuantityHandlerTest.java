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
public class OrderEntryPendingQuantityHandlerTest
{
	private final OrderEntryPendingQuantityHandler handler = new OrderEntryPendingQuantityHandler();

	@Mock
	private OrderEntryModel orderEntry;

	@Mock
	private OrderEntryQuantityService orderEntryQuantityService;

	@Before
	public void setup(){
		handler.setOrderEntryQuantityService(orderEntryQuantityService);
	}

	@Test
	public void shouldGetQuantityPending()
	{
		when(orderEntryQuantityService.getQuantityPending(orderEntry)).thenReturn(Long.valueOf(1L));
		final Long quantityPending = handler.get(orderEntry);
		assertEquals(Long.valueOf(1L), quantityPending);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(orderEntry, Long.valueOf(5L));
	}
}
