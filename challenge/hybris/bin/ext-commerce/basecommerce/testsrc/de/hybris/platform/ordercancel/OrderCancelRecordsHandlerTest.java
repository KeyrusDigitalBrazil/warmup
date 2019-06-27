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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class OrderCancelRecordsHandlerTest extends AbstractOrderCancelTest
{

	@Resource
	private OrderCancelRecordsHandler orderCancelRecordsHandler;

	private OrderCancelRequest fullCancelRequest = null;
	private OrderCancelRequest partialCancelRequest = null;

	@Before
	public void createRequest()
	{
		fullCancelRequest = new OrderCancelRequest(getOrder());

		final List<OrderCancelEntry> entries = new ArrayList<OrderCancelEntry>();
		entries.add(new OrderCancelEntry(getOrder().getEntries().get(1), 1));
		entries.add(new OrderCancelEntry(getOrder().getEntries().get(2), 2));

		partialCancelRequest = new OrderCancelRequest(getOrder(), entries);
	}

	@Test
	public void testCreateRecordEntriesFullCancell() throws Exception
	{
		final OrderCancelRecordEntryModel entry = orderCancelRecordsHandler.createRecordEntry(fullCancelRequest);
		getModelService().refresh(getOrder());
		assertNotNull("Order Should have modification record", getOrder().getModificationRecords());
		final Collection<OrderCancelRecordEntryModel> resultingEntries = getOrderCancelEntries(getOrder());
		assertTrue("resulting Cancel Entry is not as expected", resultingEntries.contains(entry));

	}

	@Test
	public void testCreateRecordEntriesPartialCancell() throws Exception
	{
		final OrderCancelRecordEntryModel entry = orderCancelRecordsHandler.createRecordEntry(partialCancelRequest);
		getModelService().refresh(getOrder());
		assertNotNull("Order Should have modification record", getOrder().getModificationRecords());
		final Collection<OrderCancelRecordEntryModel> resultingEntries = getOrderCancelEntries(getOrder());
		assertTrue("resulting Cancel Entry is not as expected", resultingEntries.contains(entry));

	}

	protected Collection<OrderCancelRecordEntryModel> getOrderCancelEntries(final OrderModel order)
	{
		for (final Iterator<OrderModificationRecordModel> iter = order.getModificationRecords().iterator(); iter.hasNext();)
		{
			final OrderModificationRecordModel modificationRecord = iter.next();
			if (modificationRecord instanceof OrderCancelRecordModel)
			{
				final OrderCancelRecordModel cancelRecord = (OrderCancelRecordModel) modificationRecord;
				return transform2CancelEntries(cancelRecord.getModificationRecordEntries());
			}
		}
		return null;
	}

	protected Collection<OrderCancelRecordEntryModel> transform2CancelEntries(
			final Collection<OrderModificationRecordEntryModel> input)
	{
		final List<OrderCancelRecordEntryModel> result = new ArrayList<OrderCancelRecordEntryModel>();
		for (final OrderModificationRecordEntryModel entry : input)
		{
			if (entry instanceof OrderCancelRecordEntryModel)
			{
				result.add((OrderCancelRecordEntryModel) entry);
			}
		}
		return result;
	}

}
