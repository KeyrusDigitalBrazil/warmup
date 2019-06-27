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

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;


/**
 *
 */
public class OrderCancelHistoryEntriesTest extends AbstractOrderCancelTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(OrderCancelHistoryEntriesTest.class);

	@Resource
	private OrderCancelStateMappingStrategy defaultOrderCancelStateMappingStrategy;

	@Mock
	private OrderCancelNotificationServiceAdapter notificationServiceAdapter;

	@Mock
	private OrderCancelPaymentServiceAdapter paymentServiceAdapter;

	@Before
	public void customSetUp() throws InvalidCartException
	{
		//clearMockedDependencied();
		setCurrentPrincipal(REQUEST_ORIGIN_STRATEGY_CUSTOMER.getCallingPrincipal());
		injectStateMappingStrategy(defaultOrderCancelStateMappingStrategy);
		setConfiguration(getOrCreateOrderCancelConfig(3600));
		getConfiguration().setPartialCancelAllowed(true);
		getConfiguration().setPartialOrderEntryCancelAllowed(true);
		getConfiguration().setOrderCancelAllowed(true);
		getConfiguration().setCancelAfterWarehouseAllowed(true);
		getConfiguration().setCompleteCancelAfterShippingStartedAllowed(true);

		initMocks(this);
		injectAdaptersDependencies(notificationServiceAdapter, paymentServiceAdapter, null, getOrderCancelService());
	}

	@Test
	public void testCompleteCancel() throws Exception
	{

		final CancelReason reason = CancelReason.LATEDELIVERY;
		final String notes = "Delivery was too late";
		final OrderCancelRequest completeCancelRequest = createCompleteRequest(getOrder(), reason, notes);

		final OrderCancelRecordEntryModel historyEntry = getOrderCancelService().requestOrderCancel(completeCancelRequest,
				getCurrentPrincipal());

		Assert.assertEquals("The cancel reason is not as expected", reason, historyEntry.getCancelReason());
		Assert.assertEquals("The cancel notes are not as expected", notes, historyEntry.getNotes());
	}

	@Test
	public void testPartialCancel() throws Exception
	{

		final AbstractOrderEntryModel orderEntry0 = getOrder().getEntries().get(0);
		final AbstractOrderEntryModel orderEntry1 = getOrder().getEntries().get(1);

		final Map<PK, CancelReason> reasons = new HashMap<PK, CancelReason>(2);
		reasons.put(orderEntry0.getPk(), CancelReason.OUTOFSTOCK);
		reasons.put(orderEntry1.getPk(), CancelReason.OTHER);

		final Map<PK, String> notes = new HashMap<PK, String>(2);
		notes.put(orderEntry0.getPk(), "Product out of stock");
		notes.put(orderEntry1.getPk(), "Product had wrong color");

		final String commonNotes = "Order cancel entries have different cancel reasoning";
		final List<OrderCancelEntry> cancelRequestEntries = new ArrayList<OrderCancelEntry>();
		//first order entry canceled due to : out of stock
		cancelRequestEntries.add(createOrderCancelRequestEntry(orderEntry0, "Product out of stock", CancelReason.OUTOFSTOCK));
		//the second one (but only 1 item) due to : Other -> Wrong color;  
		cancelRequestEntries.add(createOrderCancelRequestEntry(orderEntry1, "Product had wrong color", CancelReason.OTHER, 1));

		final OrderCancelRequest partialOrderRequest = createPartialRequest(getOrder(), cancelRequestEntries, commonNotes);

		final OrderCancelRecordEntryModel historyEntry = getOrderCancelService().requestOrderCancel(partialOrderRequest,
				getCurrentPrincipal());

		//check the order cancel entry
		Assert.assertEquals("The cancel reason on the whole entry should be NA", CancelReason.NA, historyEntry.getCancelReason());
		Assert.assertEquals("The cancel notes are not as expected", commonNotes, historyEntry.getNotes());

		//...and consecutive order entries cancel entries
		final List<OrderEntryModificationRecordEntryModel> modificationRecords = (List<OrderEntryModificationRecordEntryModel>) historyEntry
				.getOrderEntriesModificationEntries();
		final int size = modificationRecords.size();

		Assert.assertEquals("record entries number not as expexted", 2, size);

		for (final OrderEntryModificationRecordEntryModel modificationEntry : modificationRecords)
		{
			final OrderEntryCancelRecordEntryModel cancelEntry = (OrderEntryCancelRecordEntryModel) modificationEntry;
			final PK orderEntryPK = cancelEntry.getOrderEntry().getPk();

			Assert.assertEquals("Cancel reason was not as expected", reasons.get(orderEntryPK), cancelEntry.getCancelReason());
			Assert.assertEquals("Notes were not as expected", notes.get(orderEntryPK), cancelEntry.getNotes());
		}
	}

	private OrderCancelRequest createPartialRequest(final OrderModel order, final List<OrderCancelEntry> cancelRequestEntries,
			final String commonNotes)
	{
		return new OrderCancelRequest(order, cancelRequestEntries, commonNotes);
	}

	private OrderCancelRequest createCompleteRequest(final OrderModel order, final CancelReason reason, final String notes)
	{
		return new OrderCancelRequest(order, reason, notes);
	}

	private OrderCancelEntry createOrderCancelRequestEntry(final AbstractOrderEntryModel orderEntry, final String notes,
			final CancelReason cancelReason)
	{
		return new OrderCancelEntry(orderEntry, notes, cancelReason);
	}

	private OrderCancelEntry createOrderCancelRequestEntry(final AbstractOrderEntryModel orderEntry, final String notes,
			final CancelReason cancelReason, final long cancelQuantity)
	{
		return new OrderCancelEntry(orderEntry, cancelQuantity, notes, cancelReason);
	}
}
