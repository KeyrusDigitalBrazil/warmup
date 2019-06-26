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
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.warehousing.consignmententry.service.ConsignmentEntryQuantityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentEntryPendingQuantityHandlerTest
{
	private final ConsignmentEntryPendingQuantityHandler handler = new ConsignmentEntryPendingQuantityHandler();

	@Mock
	private ConsignmentEntryModel consignmentEntry;

	@Mock
	private ConsignmentEntryQuantityService consignmentEntryQuantityService;

	@Before
	public void setup(){
		handler.setConsignmentEntryQuantityService(consignmentEntryQuantityService);
	}

	@Test
	public void shouldGetQuantityPending()
	{
		when(consignmentEntryQuantityService.getQuantityPending(consignmentEntry)).thenReturn(Long.valueOf(5L));
		final Long quantityPending = handler.get(consignmentEntry);
		assertEquals(Long.valueOf(5L), quantityPending);
	}

	@Test
	public void shouldGetQuantityPending_FullShipped()
	{
		when(consignmentEntryQuantityService.getQuantityPending(consignmentEntry)).thenReturn(Long.valueOf(0L));
		final Long quantityPending = handler.get(consignmentEntry);
		assertEquals(Long.valueOf(0L), quantityPending);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(consignmentEntry, Long.valueOf(5L));
	}
}
