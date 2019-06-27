/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ysapdpordermanagement.actions.order.payment;

import java.util.Optional;

import org.apache.log4j.Logger;

import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentCaptureException;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;


/**
 * The TakePayment step captures the payment transaction.
 */
public class SapDigitalPaymentTakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SapDigitalPaymentTakePaymentAction.class);

	private SapDigitalPaymentService sapDigitalPaymentService;
	
	private PaymentService paymentService;

	@Override
	public Transition executeAction(final OrderProcessModel process) throws SapDigitalPaymentCaptureException
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
		
		PaymentTransactionModel txn;
		PaymentTransactionEntryModel txnEntry;

		final OrderModel order = process.getOrder();
		final Optional<PaymentTransactionModel> txnOpt = order.getPaymentTransactions().stream().findFirst();
		if(txnOpt.isPresent())
		{
			txn = txnOpt.get();
   		if(getSapDigitalPaymentService().isSapDigitalPaymentTransaction(txn))
   		{
   		txnEntry = getSapDigitalPaymentService().capture(txn);
   		}
   		else
   		{
   			txnEntry = getPaymentService().capture(txn);
   		}
   		
   		if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
   		{
   			setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
   			return Transition.OK;
   			
   		}
   		else
   		{
   			setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
   			return Transition.NOK;
   		}
		}
		setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
		return Transition.OK;
	}



	/**
	 * @return the sapDigitalPaymentService
	 */
	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}

	/**
	 * @param sapDigitalPaymentService the sapDigitalPaymentService to set
	 */
	public void setSapDigitalPaymentService(SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}



	/**
	 * @return the paymentService
	 */
	public PaymentService getPaymentService()
	{
		return paymentService;
	}



	/**
	 * @param paymentService the paymentService to set
	 */
	public void setPaymentService(PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
	
	


}
