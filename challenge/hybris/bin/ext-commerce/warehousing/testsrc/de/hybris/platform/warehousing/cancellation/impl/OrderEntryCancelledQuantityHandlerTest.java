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
package de.hybris.platform.warehousing.cancellation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.warehousing.model.CancellationEventModel;
import de.hybris.platform.warehousing.orderentry.service.OrderEntryQuantityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryCancelledQuantityHandlerTest
{
	private final OrderEntryCancelledQuantityHandler handler = new OrderEntryCancelledQuantityHandler();
	private OrderEntryModel orderEntry;

	@Mock
	private OrderEntryQuantityService orderEntryQuantityService;

	@Before
	public void setUp()
	{
		orderEntry = new OrderEntryModel();

		final CancellationEventModel event1 = new CancellationEventModel();
		event1.setQuantity(5L);
		final CancellationEventModel event2 = new CancellationEventModel();
		event2.setQuantity(4L);
		final CancellationEventModel event3 = new CancellationEventModel();
		event3.setQuantity(3L);

		handler.setOrderEntryQuantityService(orderEntryQuantityService);
	}

	@Test
	public void shouldGetQuantityCancelled()
	{
		when(orderEntryQuantityService.getQuantityCancelled(orderEntry)).thenReturn(Long.valueOf(15L));
		final Long quantityCancelled = handler.get(orderEntry);
		assertEquals(Long.valueOf(15L), quantityCancelled);
	}

	@Test
	public void shouldGetQuantityCancelled_NoEventsAndConsignments()
	{
		orderEntry.setConsignmentEntries(Collections.emptySet());
		final Long quantityCancelled = handler.get(orderEntry);
		assertEquals(Long.valueOf(0L), quantityCancelled);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(orderEntry, Long.valueOf(5L));
	}
}
