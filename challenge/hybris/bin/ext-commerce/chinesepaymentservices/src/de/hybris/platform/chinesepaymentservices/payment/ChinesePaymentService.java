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
package de.hybris.platform.chinesepaymentservices.payment;

import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.PaymentService;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The interface to be implemented by the 3rd part payment service provider
 */
public interface ChinesePaymentService extends PaymentService
{
	/**
	 * Handling the Asyn-response of the 3rd part payment service provider server
	 *
	 * @param request
	 *           The HttpServletRequest
	 * @param response
	 *           The HttpServletResponse
	 * @return the code of the order
	 */
	String handleAsyncResponse(final HttpServletRequest request, final HttpServletResponse response);

	/**
	 * Handling the Sync-response of the 3rd part payment service provider server
	 *
	 * @param request
	 *           The HttpServletRequest
	 * @param response
	 *           The HttpServletResponse
	 * @return the code of the order
	 */
	String handleSyncResponse(final HttpServletRequest request, final HttpServletResponse response);

	/**
	 * Canceling the payment with the 3rd part payment service provider server
	 *
	 * @param orderCode
	 *           The code of the order
	 * @return true when canceling payment is successful
	 */
	boolean cancelPayment(final String orderCode);

	/**
	 * Getting the PaymentRequestUrl to be send to the 3rd part payment service provider server
	 *
	 * @param orderCode
	 *           The code of the order
	 * @return the payment request Url of the 3rd part payment service provider
	 */
	String getPaymentRequestUrl(final String orderCode);

	/**
	 * Synchronizing the PaymentStatus with the 3rd part payment service provider server
	 *
	 * @param orderCode
	 *           The code of the order
	 */
	void syncPaymentStatus(final String orderCode);

	/**
	 * Saving the PaymentInfo
	 *
	 * @param cartModel
	 *           The current cart
	 * @param chinesePaymentInfoModel
	 *           The ChinesePaymentInfo of the cart
	 * @return true when setting PaymentInfo is successful
	 */
	boolean setPaymentInfo(final CartModel cartModel, final ChinesePaymentInfoModel chinesePaymentInfoModel);

	/**
	 * Getting the Logo of the 3rd part payment service providers
	 *
	 * @return the url of the payment service provider logo
	 */
	String getPspLogoUrl();

	/**
	 * Getting the RefundRequestUrl to be send to the 3rd part payment service provider server
	 *
	 * @param orderCode
	 *           The code of the order
	 * @return the refund request Url of the 3rd part payment service provider if order can be refunded, otherwise return
	 *         an empty Optional
	 */
	Optional<String> getRefundRequestUrl(final String orderCode);

	/**
	 * Update payment info after place order
	 *
	 * @param orderCode
	 *           The code of the order
	 */
	default void updatePaymentInfoForPlaceOrder(final String orderCode)
	{
	}

}
