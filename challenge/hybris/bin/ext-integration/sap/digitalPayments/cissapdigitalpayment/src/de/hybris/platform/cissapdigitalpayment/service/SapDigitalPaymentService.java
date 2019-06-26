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
package de.hybris.platform.cissapdigitalpayment.service;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResult;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentCaptureException;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRefundException;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Map;


/**
 * Defines payment related services
 */
public interface SapDigitalPaymentService
{


	/**
	 * Requests a payment authorization
	 *
	 * @param merchantTransactionCode
	 *           - merchant transaction code
	 * @param paymentProvider
	 *           - payment service provider
	 * @param deliveryAddress
	 *           - delivery address
	 * @param cisSapDigitalPaymentAuthorizationResult
	 *           - payment authorization result
	 *
	 * @return PaymentTransactionEntryModel
	 */
	PaymentTransactionEntryModel authorize(String merchantTransactionCode, String paymentProvider, AddressModel deliveryAddress,
			CisSapDigitalPaymentAuthorizationResult cisSapDigitalPaymentAuthorizationResult);

	/**
	 * Requests a payment capture
	 *
	 * @param transaction
	 *           - payment transaction
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 * @throws SapDigitalPaymentCaptureException
	 *            - Capture payment exception
	 */
	PaymentTransactionEntryModel capture(PaymentTransactionModel transaction) throws SapDigitalPaymentCaptureException;



	/**
	 * Requests a payment refund
	 *
	 * @param transaction
	 *           - payment transaction
	 * @param amountToRefund
	 *           - amount to refund
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 * @throws SapDigitalPaymentRefundException
	 *            - Payment refund exception
	 *
	 */
	PaymentTransactionEntryModel refund(PaymentTransactionModel transaction, BigDecimal amountToRefund)
			throws SapDigitalPaymentRefundException;



	/**
	 * Requests card registration URL from Digital payment
	 *
	 * @return registration URL
	 */
	String getCardRegistrationUrl();

	/**
	 * triggers a process which polls backend for a card
	 *
	 * @param sessionId
	 *           - sessionId w.r.t registered card
	 */
	void createPollRegisteredCardProcess(String sessionId);


	/**
	 * creates payment subscription
	 *
	 * @param paymentInfoData
	 *           has card information
	 * @param params
	 *           contains card and user details
	 * @return credit card details from backend
	 *
	 */
	CreditCardPaymentInfoModel createPaymentSubscription(CCPaymentInfoData paymentInfoData, Map<String, Object> params);

	/**
	 * save credit card details to the cart
	 *
	 * @param paymentInfoId
	 *           - paymentID
	 *
	 * @param params
	 *           - contains card and user details
	 * @return success or failure
	 */
	boolean saveCreditCardPaymentDetailsToCart(String paymentInfoId, Map<String, Object> params);


	/**
	 * Check if the Transaction is SAP Digital payment transaction
	 *
	 * @param txn
	 *           - Payment Transaction
	 *
	 * @return success or failure
	 */
	public boolean isSapDigitalPaymentTransaction(final PaymentTransactionModel txn);


}
