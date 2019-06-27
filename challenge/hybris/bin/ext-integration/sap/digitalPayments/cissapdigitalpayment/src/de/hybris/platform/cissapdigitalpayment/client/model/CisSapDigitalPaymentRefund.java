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
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Payment Refund fields for SAP Digital payment
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentRefund
{

	@JsonProperty("RefundByPaymentServiceProvider")
	private String refundByPaymentServiceProvider;

	@JsonProperty("AmountInRefundCurrency")
	private String amountInRefundCurrency;

	@JsonProperty("RefundCurrency")
	private String refundCurrency;

	@JsonProperty("RefundDateTime")
	private String refundDateTime;

	@JsonProperty("RefundStatus")
	private String refundStatus;

	@JsonProperty("RefundStatusName")
	private String refundStatusName;




	/**
	 * @return the refundByPaymentServiceProvider
	 */
	public String getRefundByPaymentServiceProvider()
	{
		return refundByPaymentServiceProvider;
	}

	/**
	 * @param refundByPaymentServiceProvider
	 *           the refundByPaymentServiceProvider to set
	 */
	public void setRefundByPaymentServiceProvider(final String refundByPaymentServiceProvider)
	{
		this.refundByPaymentServiceProvider = refundByPaymentServiceProvider;
	}

	/**
	 * @return the amountInRefundCurrency
	 */
	public String getAmountInRefundCurrency()
	{
		return amountInRefundCurrency;
	}

	/**
	 * @param amountInRefundCurrency
	 *           the amountInRefundCurrency to set
	 */
	public void setAmountInRefundCurrency(final String amountInRefundCurrency)
	{
		this.amountInRefundCurrency = amountInRefundCurrency;
	}

	/**
	 * @return the refundCurrency
	 */
	public String getRefundCurrency()
	{
		return refundCurrency;
	}

	/**
	 * @param refundCurrency
	 *           the refundCurrency to set
	 */
	public void setRefundCurrency(final String refundCurrency)
	{
		this.refundCurrency = refundCurrency;
	}

	/**
	 * @return the refundDateTime
	 */
	public String getRefundDateTime()
	{
		return refundDateTime;
	}

	/**
	 * @param refundDateTime
	 *           the refundDateTime to set
	 */
	public void setRefundDateTime(final String refundDateTime)
	{
		this.refundDateTime = refundDateTime;
	}

	/**
	 * @return the refundStatus
	 */
	public String getRefundStatus()
	{
		return refundStatus;
	}

	/**
	 * @param refundStatus
	 *           the refundStatus to set
	 */
	public void setRefundStatus(final String refundStatus)
	{
		this.refundStatus = refundStatus;
	}

	/**
	 * @return the refundStatusName
	 */
	public String getRefundStatusName()
	{
		return refundStatusName;
	}

	/**
	 * @param refundStatusName
	 *           the refundStatusName to set
	 */
	public void setRefundStatusName(final String refundStatusName)
	{
		this.refundStatusName = refundStatusName;
	}


}
