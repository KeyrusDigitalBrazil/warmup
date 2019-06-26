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

import de.hybris.platform.chinesepspalipayservices.data.AlipayCancelPaymentRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayDirectPayRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayPaymentStatusRequestData;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawCancelPaymentResult;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRawPaymentStatus;
import de.hybris.platform.chinesepspalipayservices.data.AlipayRefundRequestData;
import de.hybris.platform.chinesepspalipayservices.exception.AlipayException;


/**
 * Prepares payment related content before the request is created
 */
public interface AlipayCreateRequestStrategy
{
	/**
	 * Creates direct_pay_url from request data
	 *
	 * @param requestData
	 *           the request data
	 * @return created direct pay url
	 * @throws AlipayException
	 *            throw AlipayException when creating url failed
	 */
	String createDirectPayUrl(final AlipayDirectPayRequestData requestData) throws AlipayException;

	/**
	 *
	 * Sends check request with post method to alipay
	 *
	 * @param checkRequest
	 *           the request data
	 * @return payment status {@link AlipayRawPaymentStatus}
	 * @throws ReflectiveOperationException
	 *            throw ReflectiveOperationException when relective request parameters failed
	 *
	 */
	AlipayRawPaymentStatus submitPaymentStatusRequest(final AlipayPaymentStatusRequestData checkRequest)
			throws ReflectiveOperationException;

	/**
	 *
	 * Sends close request with POST method to alipay
	 *
	 * @param closeRequest
	 *           the request data
	 * @return The result of close request {@link AlipayRawCancelPaymentResult}
	 * @throws ReflectiveOperationException
	 *            throw ReflectiveOperationException when relective request parameters failed
	 */
	AlipayRawCancelPaymentResult submitCancelPaymentRequest(final AlipayCancelPaymentRequestData closeRequest)
			throws ReflectiveOperationException;

	/**
	 *
	 * Creates refund url by alipay refund request data
	 *
	 * @param refundData
	 *           refund request data needed by alipay {@link AlipayRefundRequestData}
	 * @return created url by the refundData
	 * @throws AlipayException
	 *            throw when create refund url error
	 *
	 */
	String createRefundUrl(final AlipayRefundRequestData refundData) throws AlipayException;
}
