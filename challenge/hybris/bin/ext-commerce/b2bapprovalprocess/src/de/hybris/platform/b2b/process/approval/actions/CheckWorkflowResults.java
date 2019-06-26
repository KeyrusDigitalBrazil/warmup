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
import de.hybris.platform.task.RetryLaterException;

import org.apache.log4j.Logger;


/**
 * Checks order's Permission result and updates order status
 */
public class CheckWorkflowResults extends AbstractSimpleB2BApproveOrderDecisionAction
{
	private static final Logger LOG = Logger.getLogger(CheckWorkflowResults.class);

	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel order = null;
		try
		{
			order = process.getOrder();
			if (order.getStatus().equals(OrderStatus.REJECTED))
			{
				// create order history and exit process.
				return Transition.NOK;

			}
			else
			{
				// if order was approved delegate to PerformMerchantCheck action
				order.setStatus(OrderStatus.PENDING_APPROVAL_FROM_MERCHANT);
				this.modelService.save(order);
				return Transition.OK;
			}
		}
		catch (final Exception e)
		{
			this.handleError(order, e);
			return Transition.NOK;
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
