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
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.basecommerce.enums.ConsignmentStatus.READY;
import static de.hybris.platform.basecommerce.enums.ConsignmentStatus.SHIPPED;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CapturePaymentOnConsignmentOrderCancelableEntriesStrategyTest
{
	protected static final Integer ORDER_ENTRY_ONE = 1;
	protected static final Integer ORDER_ENTRY_TWO = 2;

	@InjectMocks
	private CapturePaymentOnConsignmentOrderCancelableEntriesStrategy orderCancelableEntriesStrategy;

	@Mock
	private OrderModel orderModel;
	@Mock
	private OrderEntryModel orderEntryModel1;
	@Mock
	private OrderEntryModel orderEntryModel2;
	@Mock
	private ConsignmentModel consignmentModel1;
	@Mock
	private ConsignmentModel consignmentModel2;
	@Mock
	private ConsignmentEntryModel consignmentEntryModel1;
	@Mock
	private ConsignmentEntryModel consignmentEntryModel2;
	@Mock
	private Collection<ConsignmentStatus> notCancellableConsignmentStatuses;
	private Long consignmentQty1;
	private Long consignmentQty2;

	@Before
	public void setup()
	{
		when(orderEntryModel1.getEntryNumber()).thenReturn(ORDER_ENTRY_ONE);
		when(orderEntryModel2.getEntryNumber()).thenReturn(ORDER_ENTRY_TWO);
		when(orderEntryModel1.getQuantityUnallocated()).thenReturn(0L);
		when(orderEntryModel2.getQuantityUnallocated()).thenReturn(0L);
		when(orderEntryModel1.getOrder()).thenReturn(orderModel);
		when(orderEntryModel2.getOrder()).thenReturn(orderModel);
		when(orderModel.getEntries()).thenReturn(asList(orderEntryModel1, orderEntryModel2));

		when(consignmentModel1.getConsignmentEntries()).thenReturn(Collections.set(consignmentEntryModel1));
		when(consignmentModel2.getConsignmentEntries()).thenReturn(Collections.set(consignmentEntryModel2));
		when(consignmentEntryModel1.getOrderEntry()).thenReturn(orderEntryModel1);
		when(consignmentEntryModel2.getOrderEntry()).thenReturn(orderEntryModel2);

		final Set<ConsignmentModel> consignmentModels = new HashSet<>();
		consignmentModels.add(consignmentModel1);
		consignmentModels.add(consignmentModel2);
		when(orderModel.getConsignments()).thenReturn(consignmentModels);

		when(notCancellableConsignmentStatuses.contains(SHIPPED)).thenReturn(true);
		when(notCancellableConsignmentStatuses.contains(READY)).thenReturn(false);

		consignmentQty1 = 3L;
		consignmentQty2 = 8L;
		when(consignmentEntryModel1.getQuantity()).thenReturn(consignmentQty1);
		when(consignmentEntryModel2.getQuantity()).thenReturn(consignmentQty2);
	}

	@Test
	public void shouldCalculateItemsInConsignments()
	{
		//when
		final Map<AbstractOrderEntryModel, Long> result = orderCancelableEntriesStrategy.getAllCancelableEntries(orderModel, null);

		//then
		assertEquals(consignmentQty1, result.get(orderEntryModel1));
		assertEquals(consignmentQty2, result.get(orderEntryModel2));
	}

	@Test
	public void shouldCalculateUnallocatedItemsAndConsignments()
	{
		//given
		final Long unallocatedQty = 1L;
		when(orderEntryModel1.getQuantityUnallocated()).thenReturn(unallocatedQty);
		consignmentQty1 -= unallocatedQty;
		when(consignmentEntryModel1.getQuantity()).thenReturn(consignmentQty1);

		//when
		final Map<AbstractOrderEntryModel, Long> result = orderCancelableEntriesStrategy.getAllCancelableEntries(orderModel, null);

		//then
		assertEquals(consignmentQty1 + unallocatedQty, result.get(orderEntryModel1).longValue());
		assertEquals(consignmentQty2, result.get(orderEntryModel2));
	}

	@Test
	public void shouldCalculateUnallocatedItemsButNotConsignments()
	{
		//given
		final Long unallocatedQty = 1L;
		when(orderEntryModel1.getQuantityUnallocated()).thenReturn(unallocatedQty);
		consignmentQty1 -= unallocatedQty;
		when(consignmentEntryModel1.getQuantity()).thenReturn(consignmentQty1);
		when(consignmentModel1.getStatus()).thenReturn(SHIPPED);
		when(consignmentModel2.getStatus()).thenReturn(SHIPPED);

		//when
		final Map<AbstractOrderEntryModel, Long> result = orderCancelableEntriesStrategy.getAllCancelableEntries(orderModel, null);

		//then
		assertEquals(1, result.size());
		assertEquals(unallocatedQty, result.get(orderEntryModel1));
	}

	@Test
	public void shouldCalculateUnallocatedItemsAndConsignmentsWithCancelableStatus()
	{
		//given
		final Long unallocatedQty = 1L;
		when(orderEntryModel1.getQuantityUnallocated()).thenReturn(unallocatedQty);
		consignmentQty1 -= unallocatedQty;
		when(consignmentEntryModel1.getQuantity()).thenReturn(consignmentQty1);
		when(consignmentModel1.getStatus()).thenReturn(SHIPPED);

		//when
		final Map<AbstractOrderEntryModel, Long> result = orderCancelableEntriesStrategy.getAllCancelableEntries(orderModel, null);

		//then
		assertEquals(unallocatedQty, result.get(orderEntryModel1));
		assertEquals(consignmentQty2, result.get(orderEntryModel2));
	}

	@Test
	public void shouldCalculateItemsMultipleConsignmentsOneOrderEntry()
	{
		//given
		when(consignmentEntryModel1.getOrderEntry()).thenReturn(orderEntryModel1);
		when(consignmentEntryModel2.getOrderEntry()).thenReturn(orderEntryModel1);

		//when
		final Map<AbstractOrderEntryModel, Long> result = orderCancelableEntriesStrategy.getAllCancelableEntries(orderModel, null);

		//then
		assertEquals(1, result.size());
		assertEquals(consignmentQty1 + consignmentQty2, result.get(orderEntryModel1).longValue());
	}
}
