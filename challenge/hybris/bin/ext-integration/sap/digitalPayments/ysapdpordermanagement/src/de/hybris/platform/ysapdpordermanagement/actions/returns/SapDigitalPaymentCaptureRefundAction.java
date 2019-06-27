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
package de.hybris.platform.ysapdpordermanagement.actions.returns;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRefundException;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;


/**
 * Part of the refund process that returns the money to the customer.
 */
public class SapDigitalPaymentCaptureRefundAction extends AbstractSimpleDecisionAction<ReturnProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SapDigitalPaymentCaptureRefundAction.class);

	private SapDigitalPaymentService sapDigitalPaymentService;
	private PaymentService paymentService;
	private RefundAmountCalculationService refundAmountCalculationService;
	
	
	private static final String REFUND_REQ_FAIL_MSG = "Unable to refund for ReturnRequest ";

	@Override
	public Transition executeAction(final ReturnProcessModel process) throws Exception
	{
		LOG.debug("Process: " + process.getCode() + " in step " + getClass().getSimpleName());

		final ReturnRequestModel returnRequest = process.getReturnRequest();
		final List<PaymentTransactionModel> transactions = returnRequest.getOrder().getPaymentTransactions();

		if (transactions.isEmpty())
		{
			LOG.info( REFUND_REQ_FAIL_MSG + returnRequest.getCode() + ", no PaymentTransactions found");
			setReturnRequestStatus(returnRequest, ReturnStatus.PAYMENT_REVERSAL_FAILED);
			return Transition.NOK;
		}
		//This assumes that the Order only has one PaymentTransaction
		final PaymentTransactionModel transaction = transactions.stream().findFirst().get();
		
		Transition result = null;
		if (transaction.getPaymentProvider() != null)
		{
			final BigDecimal customRefundAmount = refundAmountCalculationService.getCustomRefundAmount(returnRequest);
			BigDecimal amountToRefund = null;

			if (customRefundAmount != null && customRefundAmount.compareTo(BigDecimal.ZERO) > 0)
			{
				amountToRefund = customRefundAmount;
			}
			else
			{
				amountToRefund = refundAmountCalculationService.getOriginalRefundAmount(returnRequest);
			}

			try
			{
				PaymentTransactionEntryModel txnEntry = null;
				
				if(getSapDigitalPaymentService().isSapDigitalPaymentTransaction(transaction))
				{
					txnEntry  = getSapDigitalPaymentService().refund(transaction, amountToRefund);
				}
				else
				{
					txnEntry = getPaymentService().refundFollowOn(transaction, amountToRefund);
				}
				
				if(TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
				setReturnRequestStatus(returnRequest, ReturnStatus.PAYMENT_REVERSED);
				result = Transition.OK;
				}
				else
				{
					setReturnRequestStatus(returnRequest, ReturnStatus.PAYMENT_REVERSAL_FAILED);
					result = Transition.NOK;
				}
			}
			catch (final SapDigitalPaymentRefundException e) 
			{
				if(LOG.isDebugEnabled())
				{
					LOG.debug( REFUND_REQ_FAIL_MSG + returnRequest.getCode() + ", exception ocurred: " + e);
				}
				LOG.error( REFUND_REQ_FAIL_MSG + returnRequest.getCode() + ", exception ocurred: " + e.getMessage());
				setReturnRequestStatus(returnRequest, ReturnStatus.PAYMENT_REVERSAL_FAILED);
				result = Transition.NOK;
			}
		}
		else
		{
			LOG.info("Payment Provider not available in the Payment Transaction.");
			result = Transition.OK;
		}

		return result;
	}

	/**
	 * Update the return status for all return entries in {@link ReturnRequestModel}
	 *
	 * @param returnRequest
	 *           - the return request
	 * @param status
	 *           - the return status
	 */
	protected void setReturnRequestStatus(final ReturnRequestModel returnRequest, final ReturnStatus status)
	{
		returnRequest.setStatus(status);
		returnRequest.getReturnEntries().stream().forEach(entry -> {
			entry.setStatus(status);
			getModelService().save(entry);
		});
		getModelService().save(returnRequest);
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

	protected RefundAmountCalculationService getRefundAmountCalculationService()
	{
		return refundAmountCalculationService;
	}

	@Required
	public void setRefundAmountCalculationService(RefundAmountCalculationService refundAmountCalculationService)
	{
		this.refundAmountCalculationService = refundAmountCalculationService;
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
