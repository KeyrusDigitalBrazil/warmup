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
import de.hybris.platform.basecommerce.constants.GeneratedBasecommerceConstants;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.warehousing.cancellation.CancellationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOmsOrderCancelServiceTest
{
	@InjectMocks
	private DefaultOmsOrderCancelService defaultOmsOrderCancelService;
	@Mock
	private ConsignmentModel consignment1;
	@Mock
	private ConsignmentModel consignment2;
	@Mock
	private ConsignmentEntryModel consignmentEntry1;
	@Mock
	private ConsignmentEntryModel consignmentEntry2;
	@Mock
	private ConsignmentEntryModel consignmentEntry3;
	@Mock
	private OrderModel order;
	@Mock
	private OrderEntryModel orderEntry1;
	@Mock
	private OrderEntryModel orderEntry2;
	@Mock
	private OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntryModel1;
	@Mock
	private OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntryModel2;
	@Mock
	private OrderCancelService orderCancelService;
	@Mock
	private UserService userService;
	@Mock
	private UserModel userModel;

	private List<OrderEntryCancelRecordEntryModel> orderCancelRecordEntryModel;
	private Set<ConsignmentModel> consignments;
	private Set<ConsignmentEntryModel> consignmentEntries1;
	private Set<ConsignmentEntryModel> consignmentEntries2;
	private Map<AbstractOrderEntryModel, Long> allCancelableEntries;

	@Before
	public void setup()
	{
		orderCancelRecordEntryModel = new ArrayList<>();
		consignments = new HashSet<>();
		consignmentEntries1 = new HashSet<>();
		consignmentEntries2 = new HashSet<>();

		when(order.getConsignments()).thenReturn(consignments);
		when(orderEntry1.getEntryNumber()).thenReturn(1);
		when(orderEntry2.getEntryNumber()).thenReturn(2);

		when(consignment1.getConsignmentEntries()).thenReturn(consignmentEntries1);
		when(consignment2.getConsignmentEntries()).thenReturn(consignmentEntries2);

		when(userService.getCurrentUser()).thenReturn(userModel);
		allCancelableEntries = new HashMap<>();
		when(orderCancelService.getAllCancelableEntries(order, userModel)).thenReturn(allCancelableEntries);
	}

	@Test
	public void cancelOrder_singleEntry_singleConsignment_consignmentStatusReady_success()
	{
		//given
		prepareOrderCancellationOneEntry(3L, 2);
		prepareConsignmentOneEntry(3L, orderEntry1, ConsignmentStatus.READY);

		//when
		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	@Test
	public void cancelFullOrder_singleEntry_singleConsignment_consignmentStatusReady_success()
	{
		//given
		prepareOrderCancellationOneEntry(3L, 3);
		prepareConsignmentOneEntry(3L, orderEntry1, ConsignmentStatus.READY);

		//when
		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	@Test(expected = CancellationException.class)
	public void cancelMoreThanFullOrder_singleEntry_singleConsignment_consignmentStatusReady_failure()
	{
		//given
		prepareOrderCancellationOneEntry(3L, 4);
		prepareConsignmentOneEntry(3L, orderEntry1, ConsignmentStatus.READY);

		//when
		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	@Test
	public void cancelOrder_singleEntry_singleConsignment_consignmentStatusPaymentNotCaptured_success()
	{
		//given
		prepareOrderCancellationOneEntry(3L, 2);
		prepareConsignmentOneEntry(3L, orderEntry1, ConsignmentStatus.PAYMENT_NOT_CAPTURED);
		//when
		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	@Test(expected = CancellationException.class)
	public void cancelOrder_singleEntry_singleConsignment_NotCancelable_failure()
	{
		//given
		final Long consignmentQty = 3L;
		prepareOrderCancellationOneEntry(3L, 2);
		prepareConsignmentOneEntry(consignmentQty, orderEntry1, ConsignmentStatus.READY_FOR_SHIPPING);
		allCancelableEntries.put(orderEntry1, allCancelableEntries.get(orderEntry1) - consignmentQty);

		//when
		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	/**
	 * Order:
	 * item1 * 3
	 * <p>
	 * Consignment 1: READY_FOR_SHIPPING
	 * item 1 * 2
	 * <p>
	 * Consignment2: READY
	 * item 1 * 1
	 * <p>
	 * Cancel Request:
	 * item 1 * 1
	 * <p>
	 * Expected: success
	 */
	@Test
	public void cancelOrder_singleEntry_twoConsignments_consignmentStatusMixed_success() throws OrderCancelException
	{
		//given
		prepareOrderCancellationOneEntry(3, 1);
		prepareConsignmentTwoEntries(2L, orderEntry1, ConsignmentStatus.READY_FOR_SHIPPING, 1L, orderEntry1,
				ConsignmentStatus.READY);

		//when
		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	/**
	 * Order:
	 * item1 * 3
	 * <p>
	 * Consignment 1: READY_FOR_SHIPPING
	 * item 1 * 2
	 * <p>
	 * Consignment2: READY
	 * item 1 * 1
	 * <p>
	 * Cancel Request:
	 * item 1 * 2
	 * <p>
	 * Expected: success
	 */
	@Test(expected = CancellationException.class)
	public void cancelOrder_singleEntry_twoConsignments_consignmentStatusMixed_failure() throws OrderCancelException
	{
		prepareOrderCancellationOneEntry(3, 2);
		prepareConsignmentTwoEntries(2L, orderEntry1, ConsignmentStatus.READY_FOR_SHIPPING, 1L, orderEntry1,
				ConsignmentStatus.READY);
		allCancelableEntries.put(orderEntry1, 1L);

		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	/**
	 * Order:
	 * item1 * 3
	 * item2 * 3
	 * <p>
	 * Consignment 1: READY
	 * item 1 * 3
	 * item 2 * 2
	 * <p>
	 * Consignment2: READY_FOR_SHIPPING
	 * item 2 * 1
	 * <p>
	 * Cancel Request:
	 * item 1 * 2
	 * item 2 * 2
	 * <p>
	 * Expected: success
	 */
	@Test
	public void cancelOrder_multiEntries_TwoConsignments_multiConsignmentEntries_consignmentStatusMixed_success()
	{
		prepareOrderCancellationTwoEntries(3L, 2, 3L, 2);
		prepareConsignmentTwoEntries(2L, orderEntry1, ConsignmentStatus.READY, 1L, orderEntry2,
				ConsignmentStatus.READY_FOR_SHIPPING);
		addConsignmentEntry(consignmentEntry3, 2, orderEntry2, consignment1);

		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	/**
	 * Order:
	 * item1 * 3
	 * item2 * 3
	 * <p>
	 * Consignment 1: READY
	 * item 1 * 3
	 * item 2 * 1
	 * <p>
	 * Consignment2: READY_FOR_SHIPPING
	 * item 2 * 2
	 * <p>
	 * Cancel Request:
	 * item 1 * 2
	 * item 2 * 2
	 * <p>
	 * Expected: failure
	 */
	@Test(expected = CancellationException.class)
	public void cancelOrder_multiEntries_TwoConsignments_multiConsignmentEntries_consignmentStatusMixed_failure()
	{
		prepareOrderCancellationTwoEntries(3L, 2, 3L, 2);
		prepareConsignmentTwoEntries(2L, orderEntry1, ConsignmentStatus.READY, 2L, orderEntry2,
				ConsignmentStatus.READY_FOR_SHIPPING);
		addConsignmentEntry(consignmentEntry3, 1, orderEntry2, consignment1);
		allCancelableEntries.put(orderEntry2, 1L);

		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	@Test(expected = CancellationException.class)
	public void cancelOrder_noCancelableEntriesAvailable()
	{
		prepareOrderCancellationOneEntry(1L, 0);
		prepareConsignmentOneEntry(1L, orderEntry1, ConsignmentStatus.SHIPPED);
		allCancelableEntries.clear();

		defaultOmsOrderCancelService.checkCancellationQuantitiesOnConsignments(order, orderCancelRecordEntryModel);
	}

	/**
	 * prepares an {@link OrderModel} with one {@link OrderEntryModel}, and a corresponding single entry order {@link OrderCancelRecordEntryModel}
	 *
	 * @param orderedQuantity
	 * 		quantity of item ordered in the {@link OrderEntryModel}
	 * @param cancelQuantity
	 * 		quantity of item to cancel in the {@link OrderEntryModel}
	 */
	protected void prepareOrderCancellationOneEntry(final long orderedQuantity, final int cancelQuantity)
	{
		when(orderEntry1.getQuantity()).thenReturn(orderedQuantity);
		when(orderEntryCancelRecordEntryModel1.getOrderEntry()).thenReturn(orderEntry1);
		when(orderEntryCancelRecordEntryModel1.getCancelRequestQuantity()).thenReturn(cancelQuantity);
		orderCancelRecordEntryModel.add(orderEntryCancelRecordEntryModel1);
		allCancelableEntries.put(orderEntry1, orderedQuantity);
	}

	/**
	 * prepares an {@link OrderModel} with two {@link OrderEntryModel}s, and a corresponding two entry {@link OrderCancelRecordEntryModel}
	 *
	 * @param orderedQuantity1
	 * 		quantity of item ordered in the first {@link OrderEntryModel}
	 * @param cancelQuantity1
	 * 		quantity of item to cancel in the first {@link OrderEntryModel}
	 * @param orderedQuantity2
	 * 		quantity of item ordered in the second {@link OrderEntryModel}
	 * @param cancelQuantity2
	 * 		quantity of item to cancel in the second {@link OrderEntryModel}
	 */
	protected void prepareOrderCancellationTwoEntries(final long orderedQuantity1, final int cancelQuantity1,
			final long orderedQuantity2, int cancelQuantity2)
	{
		prepareOrderCancellationOneEntry(orderedQuantity1, cancelQuantity1);

		when(orderEntry2.getQuantity()).thenReturn(orderedQuantity2);
		when(orderEntryCancelRecordEntryModel2.getOrderEntry()).thenReturn(orderEntry2);
		when(orderEntryCancelRecordEntryModel2.getCancelRequestQuantity()).thenReturn(cancelQuantity2);
		orderCancelRecordEntryModel.add(orderEntryCancelRecordEntryModel2);

		allCancelableEntries.put(orderEntry2, orderedQuantity2);
	}

	/**
	 * Prepares a consignment for an order with one entry.
	 *
	 * @param entryQuantity
	 * 		quantity for consignment entry
	 * @param orderEntry
	 * 		corresponding order entry
	 * @param status
	 * 		consignment status
	 */
	protected void prepareConsignmentOneEntry(final Long entryQuantity, final OrderEntryModel orderEntry,
			final ConsignmentStatus status)
	{
		consignmentEntries1 = new HashSet<>();

		when(consignmentEntry1.getOrderEntry()).thenReturn(orderEntry);
		when(consignmentEntry1.getQuantity()).thenReturn(entryQuantity);

		consignmentEntries1.add(consignmentEntry1);

		when(consignment1.getConsignmentEntries()).thenReturn(consignmentEntries1);
		when(consignment1.getStatus()).thenReturn(status);

		consignments.add(consignment1);
	}

	/**
	 * prepares consignments for orders with multiple entries
	 *
	 * @param entry1Quantity
	 * 		quantity for consignment entry 1
	 * @param entry1
	 * 		corresponding order entry 1
	 * @param consignment1Status
	 * 		consignment 1 status
	 * @param entry2Quantity
	 * 		quantity for consignment entry 2
	 * @param entry2
	 * 		corresponding order entry 2
	 * @param consignment2Status
	 * 		consignment 2 status
	 */
	protected void prepareConsignmentTwoEntries(final long entry1Quantity, final OrderEntryModel entry1,
			final ConsignmentStatus consignment1Status, final long entry2Quantity, final OrderEntryModel entry2,
			final ConsignmentStatus consignment2Status)
	{
		prepareConsignmentOneEntry(entry1Quantity, entry1, consignment1Status);

		consignmentEntries2 = new HashSet<>();
		when(consignmentEntry2.getOrderEntry()).thenReturn(entry2);
		when(consignmentEntry2.getQuantity()).thenReturn(entry2Quantity);

		consignmentEntries2.add(consignmentEntry2);
		consignments.add(consignment2);

		when(consignment2.getConsignmentEntries()).thenReturn(consignmentEntries2);
		when(consignment2.getStatus()).thenReturn(consignment2Status);
	}

	/**
	 * adds a consignment entry to a consignment
	 *
	 * @param consignmentEntry
	 * 		the consignment entry mock to add
	 * @param entryQuantity
	 * 		the quantity of the consignment entry
	 * @param orderEntry
	 * 		the order entry that the consignment entry should correspond to
	 * @param consignmentModel
	 * 		the consignment that the entry should belong to
	 */
	protected void addConsignmentEntry(final ConsignmentEntryModel consignmentEntry, long entryQuantity,
			final OrderEntryModel orderEntry, final ConsignmentModel consignmentModel)
	{
		when(consignmentEntry.getOrderEntry()).thenReturn(orderEntry);
		when(consignmentEntry.getQuantity()).thenReturn(entryQuantity);

		consignmentModel.getConsignmentEntries().add(consignmentEntry);
	}
}
