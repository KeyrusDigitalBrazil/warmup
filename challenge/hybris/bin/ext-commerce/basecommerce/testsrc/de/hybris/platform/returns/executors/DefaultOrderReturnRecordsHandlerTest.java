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
package de.hybris.platform.returns.executors;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.valueOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.refund.model.OrderRefundRecordEntryModel;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.dao.OrderReturnDao;
import de.hybris.platform.returns.impl.DefaultOrderReturnRecordsHandler;
import de.hybris.platform.returns.model.OrderEntryReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordEntryModel;
import de.hybris.platform.returns.model.OrderReturnRecordModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderReturnRecordsHandlerTest
{
	private static final String RETURN_CODE = "Return_Request_1";
	private static final String ORDER_CODE = "Order_Code_1";
	private static final Long IN_PROGRESS_RETURNED_QTY = Long.valueOf(5L);
	private static final Long FINISHED_RETURNED_QTY = Long.valueOf(2L);
	private static final Long NEW_RETURN_QTY = Long.valueOf(4L);
	private static final Long PK = Long.valueOf(123L);

	@InjectMocks
	private DefaultOrderReturnRecordsHandler defaultOrderReturnRecordsHandler;

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
		when(orderEntry.getEntryNumber()).thenReturn(Integer.valueOf(0));
		when(orderEntry.getPk()).thenReturn(de.hybris.platform.core.PK.fromLong(PK.longValue()));
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
		inProgressOrderEntryReturnRecordEntry.setExpectedQuantity(IN_PROGRESS_RETURNED_QTY);

		finishedReturnRecordEntry
				.setOrderEntriesModificationEntries(Collections.singletonList(finishedOrderEntryReturnRecordEntry));
		finishedReturnRecordEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);
		finishedReturnRecordEntry.setModificationRecord(orderReturnRecord);
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
	public void shouldCreateRefundEntry() throws OrderReturnRecordsHandlerException
	{
		//Given
		orderReturnRecord.setInProgress(false);
		inProgressReturnRecordEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);

		//When
		final OrderReturnRecordEntryModel returnRecordEntry = defaultOrderReturnRecordsHandler
				.createRefundEntry(order, Arrays.asList(refundEntry), null);

		//Then
		assertEquals(OrderModificationEntryStatus.INPROGRESS, returnRecordEntry.getStatus());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCreateMultipleRefundEntry() throws OrderReturnRecordsHandlerException
	{
		//When
		defaultOrderReturnRecordsHandler.createRefundEntry(order, Arrays.asList(refundEntry), null);
	}

	@Test
	public void shouldGetInProgressReturnRecordEntry()
	{
		//When
		final OrderReturnRecordEntryModel returnRecordEntryModel = defaultOrderReturnRecordsHandler
				.getPendingReturnRecordEntryForReturnRequest(returnRequest);

		//Then
		assertEquals(inProgressReturnRecordEntry, returnRecordEntryModel);
	}

	@Test
	public void shouldGetNullWhenNoReturnRecordEntryInProgress()
	{
		//Given
		inProgressReturnRecordEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);

		//When
		final OrderReturnRecordEntryModel returnRecordEntry = defaultOrderReturnRecordsHandler
				.getPendingReturnRecordEntryForReturnRequest(returnRequest);

		//Then
		assertEquals(null, returnRecordEntry);
	}


	@Test
	public void shouldFinalizeReturnRecord()
	{
		//When
		defaultOrderReturnRecordsHandler.finalizeOrderReturnRecordForReturnRequest(returnRequest);

		//Then
		assertEquals(FALSE, valueOf(orderReturnRecord.isInProgress()));
		assertEquals(IN_PROGRESS_RETURNED_QTY, inProgressOrderEntryReturnRecordEntry.getReturnedQuantity());
		assertEquals(OrderModificationEntryStatus.SUCCESSFULL, inProgressReturnRecordEntry.getStatus());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotFinalizeReturnRecordWhenNoEntryInProgress()
	{
		//Given
		inProgressReturnRecordEntry.setStatus(OrderModificationEntryStatus.SUCCESSFULL);

		//When
		defaultOrderReturnRecordsHandler.finalizeOrderReturnRecordForReturnRequest(returnRequest);
	}

}
