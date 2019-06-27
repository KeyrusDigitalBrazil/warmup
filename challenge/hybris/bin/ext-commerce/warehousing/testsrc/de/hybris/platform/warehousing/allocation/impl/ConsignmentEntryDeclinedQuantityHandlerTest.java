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

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.warehousing.consignmententry.service.ConsignmentEntryQuantityService;
import de.hybris.platform.warehousing.model.DeclineConsignmentEntryEventModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConsignmentEntryDeclinedQuantityHandlerTest
{
	@InjectMocks
	private final ConsignmentEntryDeclinedQuantityHandler handler = new ConsignmentEntryDeclinedQuantityHandler();

	@Mock
	private ConsignmentEntryQuantityService consignmentEntryQuantityService;

	private ConsignmentEntryModel consignmentEntry;

	@Before
	public void setUp()
	{
		consignmentEntry = new ConsignmentEntryModel();

		final DeclineConsignmentEntryEventModel event1 = new DeclineConsignmentEntryEventModel();
		event1.setQuantity(Long.valueOf(5L));
		final DeclineConsignmentEntryEventModel event2 = new DeclineConsignmentEntryEventModel();
		event2.setQuantity(Long.valueOf(4L));
		final DeclineConsignmentEntryEventModel event3 = new DeclineConsignmentEntryEventModel();
		event3.setQuantity(Long.valueOf(3L));
		consignmentEntry.setDeclineEntryEvents(Sets.newHashSet(event1, event2, event3));
	}

	@Test
	public void shouldGetQuantityDeclined()
	{
		final Long quantityDeclined = handler.get(consignmentEntry);
		assertEquals(Long.valueOf(12L), quantityDeclined);
	}

	@Test
	public void shouldGetZero_EmptySet()
	{
		consignmentEntry.setDeclineEntryEvents(Collections.emptySet());
		final Long quantityDeclined = handler.get(consignmentEntry);
		assertEquals(Long.valueOf(0L), quantityDeclined);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldNotSupportSet()
	{
		handler.set(consignmentEntry, Long.valueOf(5L));
	}
}
