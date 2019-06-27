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
package de.hybris.platform.yacceleratorordermanagement.actions.order.payment;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * The TakePayment step captures the payment transaction.
 */
public class TakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(TakePaymentAction.class);

	private PaymentService paymentService;

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());

		final OrderModel order = process.getOrder();
		boolean paymentFailed = false;
		for (final PaymentTransactionModel txn : order.getPaymentTransactions())
		{
			if (txn.getPaymentProvider() == null)
			{
				LOG.info("Payment Provider not available in the Payment Transaction.");
			}
			else
			{
				final PaymentTransactionEntryModel txnEntry = getPaymentService().capture(txn);

				if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
					LOG.debug("The payment transaction has been captured. Order: {}. Txn: {}", order.getCode(), txn.getCode());
				}
				else
				{
					paymentFailed = true;
					LOG.info("The payment transaction capture has failed. Order: {}. Txn: {}", order.getCode(), txn.getCode());
				}
			}
		}

		if (paymentFailed)
		{
			setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
			return Transition.NOK;
		}
		else
		{
			setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
			return Transition.OK;
		}
	}

	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}
