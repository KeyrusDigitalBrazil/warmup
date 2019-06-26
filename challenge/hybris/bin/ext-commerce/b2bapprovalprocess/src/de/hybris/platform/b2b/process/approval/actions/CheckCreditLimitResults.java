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


public class CheckCreditLimitResults extends AbstractSimpleB2BApproveOrderDecisionAction
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(CheckCreditLimitResults.class);

	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel order = null;
		try
		{
			order = process.getOrder();


			if (OrderStatus.REJECTED.equals(order.getStatus()))
			{
				order.setStatus(OrderStatus.REJECTED_BY_MERCHANT);
				this.modelService.save(order);
				return Transition.NOK;
			}

			order.setStatus(OrderStatus.APPROVED);
			this.modelService.save(order);
			return Transition.OK;

		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
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
