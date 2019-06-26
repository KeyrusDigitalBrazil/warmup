/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.returns.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.refund.model.OrderRefundRecordEntryModel;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.dao.OrderReturnDao;
import de.hybris.platform.returns.model.OrderEntryReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousingOrderReturnRecordsHandlerTest
{
	private static final String RETURN_CODE = "Return_Request_1";
	private static final String ORDER_CODE = "Order_Code_1";
	private static final Long IN_PROGRESS_RETURNED_QTY = 5L;
	private static final Long FINISHED_RETURNED_QTY = 2L;
	private static final Long NEW_RETURN_QTY = 4L;
	private static final Long PK = 123L;

	@InjectMocks
	private WarehousingOrderReturnRecordsHandler warehousingOrderReturnRecordsHandler;

	@Mock
	private ModelService modelService;
	@Mock
	private OrderHistoryService orderHistoryService;
	@Mock
	private OrderReturnDao orderReturnDao;
	@Mock
	private ReturnRequestModel returnRequest;
	@Mock
	private RefundEntryModel refundEntry;
	@Mock
	private OrderModel order;
	@Mock
	private OrderEntryModel orderEntry;
	@Mock
	private OrderHistoryEntryModel historyEntry;

	private OrderReturnRecordModel orderReturnRecord;
	private OrderReturnRecordEntryModel inProgressReturnRecordEntry;
	private OrderReturnRecordEntryModel finishedReturnRecordEntry;
	private OrderRefundRecordEntryModel newReturnRecordEntry;
	private OrderEntryReturnRecordEntryModel inProgressOrderEntryReturnRecordEntry;
	private OrderEntryReturnRecordEntryModel finishedOrderEntryReturnRecordEntry;
	private OrderEntryReturnRecordEntryModel newOrderEntryReturnRecordEntry;

	@Before
	public void setup()
	{
		when(returnRequest.getCode()).thenReturn(RETURN_CODE);
		when(returnRequest.getOrder()).thenReturn(order);
		when(returnRequest.getReturnEntries()).thenReturn(Collections.singletonList(refundEntry));
		when(refundEntry.getOrderEntry()).thenReturn(orderEntry);
		when(refundEntry.getExpectedQuantity()).thenReturn(NEW_RETURN_QTY);
		when(orderEntry.getEntryNumber()).thenReturn(0);
		when(orderEntry.getPk()).thenReturn(de.hybris.platform.core.PK.fromLong(PK));
		when(order.getCode()).thenReturn(ORDER_CODE);
		when(order.getEntries()).thenReturn(Arrays.asList(orderEntry));

		orderReturnRecord = new OrderReturnRecordModel();
		inProgressReturnRecordEntry = new OrderReturnRecordEntryModel();
		finishedReturnRecordEntry = new OrderReturnRecordEntryModel();
		newReturnRecordEntry = new OrderRefundRecordEntryModel();
		inProgressOrderEntryReturnRecordEntry = new OrderEntryReturnRecordEntryModel();
		finishedOrderEntryReturnRecordEntry = new OrderEntryReturnRecordEntryModel();
		newOrderEntryReturnRecordEntry = new OrderEntryReturnRecordEntryModel();

		orderReturnRecord.setInProgress(true);
		orderReturnRecord.setModificationRecordEntries(Arrays.asList(inProgressReturnRecordEntry, finishedReturnRecordEntry));
		inProgressReturnRecordEntry
				.setOrderEntriesModificationEntries(Collections.singletonList(inProgressOrderEntryReturnRecordEntry));
		inProgressReturnRecordEntry.setStatus(OrderModificationEntryStatus.INPROGRESS);
		inProgressReturnRecordEntry.setModificationRecord(orderReturnRecord);
		inProgressReturnRecordEntry.setReturnRequest(returnRequest);
		inProgressOrderEntryReturnRecordEntry.setExpectedQuantity(IN_PROGRESS_RETURNED_QTY);

		finishedReturnRecordEntry
				.setOrderEntriesModificationEntries(Collections.singletonList(finishedOrderEntryReturnRecordEntry));
		finishedReturnRecordEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);
		finishedReturnRecordEntry.setModificationRecord(orderReturnRecord);
		finishedReturnRecordEntry.setReturnRequest(returnRequest);
		finishedOrderEntryReturnRecordEntry.setExpectedQuantity(FINISHED_RETURNED_QTY);

		when(historyEntry.getOrder()).thenReturn(order);
		when(historyEntry.getPreviousOrderVersion()).thenReturn(order);
		when(orderReturnDao.getOrderReturnRecord(order)).thenReturn(orderReturnRecord);
		when(modelService.create(OrderHistoryEntryModel.class)).thenReturn(historyEntry);
		when(modelService.create(OrderRefundRecordEntryModel.class)).thenReturn(newReturnRecordEntry);
		when(modelService.create(OrderEntryReturnRecordEntryModel.class)).thenReturn(newOrderEntryReturnRecordEntry);
		doNothing().when(modelService).save(any());
	}

	@Test
	public void shouldCreateMultipleInProgressRefundEntries() throws OrderReturnRecordsHandlerException
	{
		//When
		final OrderReturnRecordEntryModel orderReturnRecordEntry = warehousingOrderReturnRecordsHandler
				.createRefundEntry(order, Arrays.asList(refundEntry), null);

		final OrderReturnRecordModel returnRecord = (OrderReturnRecordModel) orderReturnRecordEntry.getModificationRecord();
		final Collection<OrderModificationRecordEntryModel> returnRecordEntriesInProgress = returnRecord
				.getModificationRecordEntries().stream()
				.filter(returnRecordEntry -> OrderModificationEntryStatus.INPROGRESS.equals(returnRecordEntry.getStatus()))
				.collect(Collectors.toSet());
		returnRecordEntriesInProgress.add(orderReturnRecordEntry);

		//Then
		assertEquals(2, returnRecordEntriesInProgress.size());
		assertTrue(returnRecord.isInProgress());
	}

	@Test
	public void shouldNotUpdateReturnRecordWhenOtherRecordEntryInProgress() throws OrderReturnRecordsHandlerException
	{
		//Given
		final ReturnRequestModel anotherReturnRequest = new ReturnRequestModel();
		final RefundEntryModel anotherRefundEntry = new RefundEntryModel();
		anotherRefundEntry.setExpectedQuantity(3L);
		anotherRefundEntry.setOrderEntry(orderEntry);
		anotherReturnRequest.setReturnEntries(Lists.newArrayList(anotherRefundEntry));

		final OrderReturnRecordEntryModel anotherOrderReturnRecordEntry = warehousingOrderReturnRecordsHandler
				.createRefundEntry(order, Arrays.asList(anotherRefundEntry), null);
		anotherOrderReturnRecordEntry.setReturnRequest(anotherReturnRequest);

		orderReturnRecord.setModificationRecordEntries(
				Arrays.asList(finishedReturnRecordEntry, inProgressReturnRecordEntry, anotherOrderReturnRecordEntry));

		//When finalizing the older return request, it should confirm only inProgressReturnRecordEntry
		final OrderReturnRecordModel returnRecord = warehousingOrderReturnRecordsHandler
				.finalizeOrderReturnRecordForReturnRequest(returnRequest);
		final Collection<OrderModificationRecordEntryModel> returnRecordEntriesInProgress = returnRecord
				.getModificationRecordEntries().stream()
				.filter(returnRecordEntry -> OrderModificationEntryStatus.INPROGRESS.equals(returnRecordEntry.getStatus()))
				.collect(Collectors.toSet());

		//Then the newly created anotherOrderReturnRecordEntry should still stay in Progress and the parent returnRecord should stay in progress as well
		assertEquals(1, returnRecordEntriesInProgress.size());
		final OrderEntryReturnRecordEntryModel returnRecordEntryInProgress = (OrderEntryReturnRecordEntryModel) returnRecordEntriesInProgress
				.iterator().next().getOrderEntriesModificationEntries().iterator().next();
		assertEquals(3L, returnRecordEntryInProgress.getExpectedQuantity().longValue());
		assertTrue(returnRecord.isInProgress());
	}


	@Test
	public void shouldFinalizeReturnRecord()
	{
		//When
		final OrderReturnRecordModel returnRecord = warehousingOrderReturnRecordsHandler
				.finalizeOrderReturnRecordForReturnRequest(returnRequest);
		final Collection<OrderModificationRecordEntryModel> returnRecordEntriesInProgress = returnRecord
				.getModificationRecordEntries().stream()
				.filter(returnRecordEntry -> OrderModificationEntryStatus.INPROGRESS.equals(returnRecordEntry.getStatus()))
				.collect(Collectors.toSet());

		//Then
		assertTrue(!returnRecord.isInProgress());
		assertEquals(0, returnRecordEntriesInProgress.size());
		assertEquals(IN_PROGRESS_RETURNED_QTY, inProgressOrderEntryReturnRecordEntry.getReturnedQuantity());
		assertEquals(OrderModificationEntryStatus.SUCCESSFULL, inProgressReturnRecordEntry.getStatus());
	}

}
