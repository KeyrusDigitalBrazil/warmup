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
package de.hybris.platform.ordercancel;

import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.ordercancel.OrderCancelResponse.ResponseStatus;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Tests complete cancel execution in different scenarios.
 */
public class OrderCancelCompleteTest extends AbstractOrderCancelTest
{
	private static final Logger LOG = Logger.getLogger(OrderCancelCompleteTest.class);

	@Mock
	private OrderCancelNotificationServiceAdapter notificationServiceAdapter;

	@Mock
	private OrderCancelPaymentServiceAdapter paymentServiceAdapter;

	@Before
	public void customSetUp() throws InvalidCartException, CalculationException
	{
		LOG.info("Setting up OrderCancelDaoTest");
		setOrder(placeTestOrder());
		setCurrentPrincipal(REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal());
		injectStateMappingStrategy(STATE_STRATEGY_HOLDING_AREA_QUEUE_WAITING);

		initMocks(this);
		injectAdaptersDependencies(notificationServiceAdapter, paymentServiceAdapter, null, getOrderCancelService());

		setConfiguration(getOrCreateOrderCancelConfig(3600));
		getConfiguration().setPartialCancelAllowed(true);
		getConfiguration().setPartialOrderEntryCancelAllowed(true);
		getConfiguration().setOrderCancelAllowed(true);
		getConfiguration().setCancelAfterWarehouseAllowed(true);
		getConfiguration().setCompleteCancelAfterShippingStartedAllowed(true);

		final OrderCancelDaoMock ocdMock = new OrderCancelDaoMock(getConfiguration());
		this.getOrderCancelService().setOrderCancelDao(ocdMock);
	}


	/**
	 * Exception should be throws when issuing a complete cancel request when cancel is not possible.
	 */
	@Test(expected = OrderCancelException.class)
	public void testExceptionOnCompleteCancelImpossible() throws OrderCancelException
	{
		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(getOrder());
		getConfiguration().setOrderCancelAllowed(false);
		getOrderCancelService().requestOrderCancel(completeCancelRequest, getCurrentPrincipal());
	}

	/**
	 * Exception should be throws when issuing a partial cancel request when cancel is not possible.
	 */
	@Test(expected = OrderCancelException.class)
	public void testExceptionOnPartialCancelImpossible() throws OrderCancelException
	{
		final OrderCancelRequest partialCancelRequest = preparePartialCancelRequest(getOrder(), 1);
		getConfiguration().setOrderCancelAllowed(false);
		getOrderCancelService().requestOrderCancel(partialCancelRequest, getCurrentPrincipal());
	}

	@Test
	public void testCompleteCancelInHoldingArea() throws OrderCancelException
	{
		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(getOrder());

		final int numberOfEntries = getOrder().getEntries().size();

		final double originalOrderTotalsSum = calculateOrderTotalSum(getOrder());

		final OrderCancelRecordEntryModel cancelRecord = getOrderCancelService().requestOrderCancel(completeCancelRequest,
				getCurrentPrincipal());

		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}

		final double cancelSnapshotTotalsSum = calculateSnapshotTotal(cancelRecord);

		verify(notificationServiceAdapter, times(1)).sendCancelFinishedNotifications(any());

		Assert.assertEquals("Order should have 3 entries", 3, completeCancelRequest.getOrder().getEntries().size());

		Assert.assertEquals("Order cancel record entry should have " + numberOfEntries + " entries ", numberOfEntries, cancelRecord
				.getOrderEntriesModificationEntries().size());

		Assert.assertEquals(
				"Cancel request order entries snapshot must reflect original order entries (that does no longer exist)",
				originalOrderTotalsSum, cancelSnapshotTotalsSum, 0.00001);

		Assert.assertFalse("Order cancel record should NOT be in progress ", cancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
				OrderModificationEntryStatus.SUCCESSFULL, cancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.FULL.getCode(),
				OrderCancelEntryStatus.FULL, cancelRecord.getCancelResult());

		Assert.assertEquals("Order should be in " + OrderStatus.CANCELLED.getCode() + " state", OrderStatus.CANCELLED,
				completeCancelRequest.getOrder().getStatus());

	}

	private double calculateSnapshotTotal(final OrderCancelRecordEntryModel cancelRecord)
	{
		final double cancelSnapshotTotalsSum;
		{
			double tmp = 0;
			for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
			{
				tmp += oemrem.getOriginalOrderEntry().getTotalPrice().doubleValue();
			}
			cancelSnapshotTotalsSum = tmp;
		}
		return cancelSnapshotTotalsSum;
	}

	private double calculateOrderTotalSum(final OrderModel order)
	{
		final double originalOrderTotalsSum;
		{
			double tmp = 0;
			for (final AbstractOrderEntryModel orderEntry : order.getEntries())
			{
				tmp += orderEntry.getTotalPrice().doubleValue();
			}
			originalOrderTotalsSum = tmp;
		}
		return originalOrderTotalsSum;
	}

	private double calculateOrderBaseSum(final OrderModel order)
	{
		final double originalOrderBaseSum;
		{
			double tmp = 0;
			for (final AbstractOrderEntryModel orderEntry : order.getEntries())
			{
				tmp += orderEntry.getBasePrice().doubleValue() * orderEntry.getQuantity().doubleValue();
			}
			originalOrderBaseSum = tmp;
		}
		return originalOrderBaseSum;
	}

	private OrderCancelRecordEntryModel performCompleteCancelWarehouseRequest(final OrderModel order) throws OrderCancelException
	{

		injectStateMappingStrategy(STATE_STRATEGY_SENT_TO_WAREHOUSE);

		final OrderCancelRequest completeCancelRequest = prepareCompleteCancelRequest(order);

		final int numberOfEntries = order.getEntries().size();

		final double originalOrderTotalsSum = calculateOrderTotalSum(order);

		final OrderCancelRecordEntryModel cancelRecord = getOrderCancelService().requestOrderCancel(completeCancelRequest,
				getCurrentPrincipal());

		//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
		for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
		{
			getModelService().refresh(oemrem);
		}

		final double cancelSnapshotTotalsSum = calculateSnapshotTotal(cancelRecord);

		verify(notificationServiceAdapter, times(1)).sendCancelPendingNotifications(any());

		Assert.assertEquals("Order should have" + numberOfEntries + " entries ", numberOfEntries, completeCancelRequest.getOrder()
				.getEntries().size());

		Assert.assertEquals("Order cancel record entry should have " + numberOfEntries + " entries ", numberOfEntries, cancelRecord
				.getOrderEntriesModificationEntries().size());

		Assert.assertEquals("Cancel request order entries snapshot must reflect original order entries", originalOrderTotalsSum,
				cancelSnapshotTotalsSum, 0.00001);

		Assert.assertTrue("Order cancel record should be in progress ", cancelRecord.getModificationRecord().isInProgress());

		Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.INPROGRESS,
				OrderModificationEntryStatus.INPROGRESS, cancelRecord.getStatus());

		Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.FULL.getCode(),
				OrderCancelEntryStatus.FULL, cancelRecord.getCancelResult());

		Assert.assertEquals("Order should be in CANCELLING state", OrderStatus.CANCELLING, order.getStatus());

		return cancelRecord;
	}

	@Test
	public void testCompleteCancelWarehouseNotShipping() throws OrderCancelException
	{

		//Perform customer cancel request part
		performCompleteCancelWarehouseRequest(getOrder());

		//Perform warehouse complete cancel successful response part
		{
			//Warehouse response: Complete Cancel (All order entries)
			final OrderCancelResponse completeCancelResponse = new OrderCancelResponse(getOrder());

			final int numberOfEntries = getOrder().getEntries().size();

			final double originalOrderTotalsSum;
			{
				double tmp = 0;
				for (final AbstractOrderEntryModel orderEntry : getOrder().getEntries())
				{
					tmp += orderEntry.getTotalPrice().doubleValue();
				}
				originalOrderTotalsSum = tmp;
			}

			OrderCancelRecordEntryModel cancelRecord = null;
			final OrderCancelRecordModel ocrm = getOrderCancelService().getCancelRecordForOrder(getOrder());
			for (final OrderModificationRecordEntryModel omrem : ocrm.getModificationRecordEntries())
			{
				if (OrderModificationEntryStatus.INPROGRESS == omrem.getStatus())
				{
					cancelRecord = (OrderCancelRecordEntryModel) omrem;
				}
			}

			getOrderCancelService().onOrderCancelResponse(completeCancelResponse);

			//refresh OrderEntryModificationRecordEntries so that any references to (now-should-be-gone) OrderEntries are discarded.
			for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
			{
				getModelService().refresh(oemrem);
			}

			final double cancelSnapshotTotalsSum;
			{
				double tmp = 0;
				for (final OrderEntryModificationRecordEntryModel oemrem : cancelRecord.getOrderEntriesModificationEntries())
				{
					tmp += oemrem.getOriginalOrderEntry().getTotalPrice().doubleValue();
				}
				cancelSnapshotTotalsSum = tmp;
			}

			Assert.assertEquals("Order should have 3 entries", 3, completeCancelResponse.getOrder().getEntries().size());

			Assert.assertEquals("Order cancel record entry should have " + numberOfEntries + " entries ", numberOfEntries,
					cancelRecord.getOrderEntriesModificationEntries().size());

			Assert.assertEquals(
					"Cancel request order entries snapshot must reflect original order entries (that does no longer exist)",
					originalOrderTotalsSum, cancelSnapshotTotalsSum, 0.00001);

			Assert.assertFalse("Order cancel record should NOT be in progress ", cancelRecord.getModificationRecord().isInProgress());

			Assert.assertEquals("Order cancel record entry should be " + OrderModificationEntryStatus.SUCCESSFULL,
					OrderModificationEntryStatus.SUCCESSFULL, cancelRecord.getStatus());

			Assert.assertEquals("Order cancel record entry status should be " + OrderCancelEntryStatus.FULL.getCode(),
					OrderCancelEntryStatus.FULL, cancelRecord.getCancelResult());

			Assert.assertEquals("Order should be in " + OrderStatus.CANCELLED.getCode() + " state", OrderStatus.CANCELLED,
					getOrder().getStatus());

		}

	}

	@Test
	public void testWarehouseResponseCancelDenied() throws Exception
	{
		final OrderStatus previousOrderStatus = getOrder().getStatus();

		//Perform customer cancel request part
		final OrderCancelRecordEntryModel cancelRecord = performCompleteCancelWarehouseRequest(getOrder());

		//response :  denied
		final OrderCancelResponse cancelDeniedResponse = new OrderCancelResponse(getOrder(), ResponseStatus.denied,
				"Order already shipped");
		getOrderCancelService().onOrderCancelResponse(cancelDeniedResponse);

		getModelService().refresh(cancelRecord);
		getModelService().refresh(getOrder());

		Assert.assertEquals("Cancel Record Entry should have status SUCCESSFULL", OrderModificationEntryStatus.SUCCESSFULL,
				cancelRecord.getStatus());
		Assert.assertEquals("Cancel Record Entry should have cancel status DENIED", OrderCancelEntryStatus.DENIED,
				cancelRecord.getCancelResult());
		Assert.assertEquals("Order should be returned to previous state", previousOrderStatus, getOrder().getStatus());
	}


	/**
	 * This test case tests scenario when FULL order cancel was requested, however warehouse performed PARTIAL cancel -
	 * due to some internal issues (partial shipment already done?)
	 *
	 * @throws Exception
	 */
	@Test
	public void testWarehouseResponsePartialCancelOK() throws Exception
	{

		final List<AbstractOrderEntryModel> originalEntries = getOrder().getEntries();
		final OrderStatus previousOrderState = getOrder().getStatus();
		final double intitalOrderEntrySum = calculateOrderBaseSum(getOrder());

		//Perform customer cancel request part
		final OrderCancelRecordEntryModel cancelRecord = performCompleteCancelWarehouseRequest(getOrder());

		//response
		{
			final List<OrderCancelEntry> responseEntries = new ArrayList<OrderCancelEntry>();

			double priceDifference = 0.0;

			//first entry cancelled fully
			responseEntries.add(new OrderCancelEntry(originalEntries.get(0), originalEntries.get(0).getQuantity().longValue()));

			priceDifference += originalEntries.get(0).getTotalPrice().doubleValue();

			//second entry cancel only 1 product
			responseEntries.add(new OrderCancelEntry(originalEntries.get(1), 1));
			priceDifference += originalEntries.get(1).getBasePrice().doubleValue();

			final OrderCancelResponse partialResponse = new OrderCancelResponse(getOrder(), responseEntries);

			////////////////////////////////////////

			getOrderCancelService().onOrderCancelResponse(partialResponse);

			getModelService().refresh(cancelRecord);
			getModelService().refresh(getOrder());

			final double finalOrderEntrySum = calculateOrderBaseSum(getOrder());

			Assert.assertEquals("Order Entries totals sum should be decreased after partial cancel: ", intitalOrderEntrySum
					- finalOrderEntrySum, priceDifference, 0.00001);

			Assert.assertEquals("Cancel Record Entry should have status SUCCESSFULL", OrderModificationEntryStatus.SUCCESSFULL,
					cancelRecord.getStatus());
			Assert.assertEquals("Cancel Record Entry should have cancel status PARTIAL", OrderCancelEntryStatus.PARTIAL,
					cancelRecord.getCancelResult());
			Assert.assertEquals("Order should be returned to previous state", previousOrderState, getOrder().getStatus());

			Assert.assertEquals("Order should have 3 entries after partial cancel", 3, getOrder().getEntries().size());

		}

	}

	/**
	 * Tests consecutive cancel requests when the first one didnt finish
	 *
	 * @throws OrderCancelException
	 */
	@Test
	public void testSecondCancelRequest() throws OrderCancelException
	{

		boolean passed = false;
		performCompleteCancelWarehouseRequest(getOrder());
		try
		{
			performCompleteCancelWarehouseRequest(getOrder());
		}
		catch (final OrderCancelDeniedException e)
		{
			passed = true;
		}
		Assert.assertTrue(
				"Second order cancel request should have failed due to OrderCancelDeniedException (Cancel already in progress)",
				passed);


	}



	private OrderCancelRequest prepareCompleteCancelRequest(final OrderModel order)
	{
		return new OrderCancelRequest(order);
	}

	private OrderCancelRequest preparePartialCancelRequest(final OrderModel order, final int entryIndex)
	{
		final List<OrderCancelEntry> entries = new ArrayList<OrderCancelEntry>();
		entries.add(new OrderCancelEntry(order.getEntries().get(entryIndex), order.getEntries().get(entryIndex).getQuantity()
				.longValue() - 1));
		return new OrderCancelRequest(order, entries);
	}
}
