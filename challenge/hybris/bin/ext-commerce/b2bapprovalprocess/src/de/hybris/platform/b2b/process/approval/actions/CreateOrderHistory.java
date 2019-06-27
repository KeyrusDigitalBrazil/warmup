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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.Date;

import org.apache.log4j.Logger;


/**
 * Creates an {@link OrderHistoryEntryModel} for an order that was rejected by an approver.
 */
public class CreateOrderHistory extends AbstractProceduralB2BOrderAproveAction
{

	private static final Logger LOG = Logger.getLogger(CreateOrderHistory.class);

	@Override
	public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		try
		{
			final OrderHistoryEntryModel historyEntry = modelService.create(OrderHistoryEntryModel.class);
			historyEntry.setTimestamp(new Date());
			historyEntry.setOrder(order);
			historyEntry.setDescription("Order was rejected.");
			modelService.save(historyEntry);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Created " + historyEntry + " for order " + order.getCode());
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			handleError(order, e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	protected void handleError(final OrderModel order, final Exception e)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(e.getMessage(), e);
	}
}
