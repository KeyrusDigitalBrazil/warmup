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
public class ConsignmentEntryShippedQuantityHandlerTest
{
	private final ConsignmentEntryShippedQuantityHandler handler = new ConsignmentEntryShippedQuantityHandler();
	private ConsignmentEntryModel consignmentEntry;

	@Mock
	private ConsignmentEntryQuantityService consignmentEntryQuantityService;

	@Before
	public void setUp()
	{
		consignmentEntry = new ConsignmentEntryModel();
		handler.setConsignmentEntryQuantityService(consignmentEntryQuantityService);
	}

	@Test
	public void shouldGetQuantityShipped()
	{
		when(consignmentEntryQuantityService.getQuantityShipped(consignmentEntry)).thenReturn(Long.valueOf(9L));
		final Long quantityShipped = handler.get(consignmentEntry);
		assertEquals(Long.valueOf(9L), quantityShipped);
	}

	@Test
	public void shouldGetZero_EmptySet()
	{
		when(consignmentEntryQuantityService.getQuantityShipped(consignmentEntry)).thenReturn(Long.valueOf(0L));
		final Long quantityShipped = handler.get(consignmentEntry);
		assertEquals(Long.valueOf(0L), quantityShipped);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(consignmentEntry, Long.valueOf(10L));
	}

}
