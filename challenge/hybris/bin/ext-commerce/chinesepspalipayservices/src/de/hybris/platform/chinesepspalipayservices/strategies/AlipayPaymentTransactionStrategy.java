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
package de.hybris.platform.chinesepspalipayservices.strategies;


import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayErrorInfo;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawDirectPayNotification;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Deals with payment transaction when the transaction is in process
 *
 */
public interface AlipayPaymentTransactionStrategy
{

	/**
	 * Saves new transaction entry once new direct_pay request is created
	 *
	 * @param orderModel
	 *           the order
	 * @param requestUrl
	 *           accessible URL
	 */
	void createForNewRequest(OrderModel orderModel, String requestUrl);


	/**
	 *
	 * Saves alipay payment transaction entry upon the completion of payment status check
	 *
	 * @param orderModel
	 *           the order
	 * @param checkTradeResponseData
	 *           the trade status check
	 * @return AlipayPaymentTransactionEntryModel
	 *
	 */
	AlipayPaymentTransactionEntryModel saveForStatusCheck(OrderModel orderModel, AlipayRawPaymentStatus checkTradeResponseData);

	/**
	 * Updates alipay payment transaction and entry upon receiving notification data from alipay
	 *
	 * @param orderModel
	 *           order handled by the notification data {@link OrderModel}
	 * @param directPayNotifyResponseData
	 *           notification from alipay {@link AlipayRawDirectPayNotification}
	 */
	void updateForNotification(OrderModel orderModel, AlipayRawDirectPayNotification directPayNotifyResponseData);

	/**
	 * Updates alipay payment transaction and entry upon canceling trade
	 *
	 * @param orderModel
	 *           transaction related order
	 * @param alipayRawCancelPaymentResult
	 *           response from alipay upon canceling trade {@link AlipayRawCancelPaymentResult}
	 */
	void updateForCancelPayment(OrderModel orderModel, final AlipayRawCancelPaymentResult alipayRawCancelPaymentResult);

	/**
	 * Updates alipay payment transaction and entry upon receiving error data from alipay
	 *
	 * @param orderModel
	 *           order handled by the error data
	 * @param aipayRawDirectPayErrorInfo
	 *           error data from alipay {@link AlipayRawDirectPayErrorInfo}
	 */
	void updateForError(OrderModel orderModel, AlipayRawDirectPayErrorInfo aipayRawDirectPayErrorInfo);

	/**
	 * Checks if the alipay payment transaction exists under an order that has capture entry
	 *
	 * @param orderModel
	 *           order needed to check
	 * @param status
	 *           transaction status {@link TransactionStatus}
	 * @return false if the transaction exists
	 */
	boolean checkCaptureTransactionEntry(final OrderModel orderModel, final TransactionStatus status);

	/**
	 * Finds payment transaction entry with given parameters
	 *
	 * @param orderModel
	 *           order needed to check
	 * @param status
	 *           transaction status {@link TransactionStatus}
	 * @param paymentTransactionType
	 *           payment transaction status {@link PaymentTransactionType}
	 * @return an optional describing the result of AlipayPaymentTransactionEntryModel if a value is present, otherwise
	 *         an empty Optional
	 */
	Optional<AlipayPaymentTransactionEntryModel> getPaymentTransactionEntry(final OrderModel orderModel,
			final TransactionStatus status, final PaymentTransactionType paymentTransactionType);

	/**
	 * Finds payment transaction whose type is capture
	 *
	 * @param orderModel
	 *           order needed to check
	 * @param status
	 *           transaction status {@link TransactionStatus}
	 * @return an Optional describing the result of AlipayPaymentTransactionEntryModel if a value is present, otherwise
	 *         an empty Optional
	 */
	Optional<AlipayPaymentTransactionModel> getPaymentTransactionWithCaptureEntry(final OrderModel orderModel,
			final TransactionStatus status);


	/**
	 * Updates transaction for refund notification
	 *
	 * @param alipayRefundDatas
	 *           refund data {@link AlipayRefundData}}
	 * @return refund order payment status map
	 */
	Map<OrderModel, Boolean> updateForRefundNotification(final List<AlipayRefundData> alipayRefundData);

	/**
	 * Creates a transaction entry when user creates a refund request
	 *
	 * @param orderModel
	 *           order needed to refund
	 * @param alipayRefundRequestData
	 *           request data sent to alipay
	 */
	void updateTransactionForRefundRequest(OrderModel orderModel, AlipayRefundRequestData alipayRefundRequestData);
}
