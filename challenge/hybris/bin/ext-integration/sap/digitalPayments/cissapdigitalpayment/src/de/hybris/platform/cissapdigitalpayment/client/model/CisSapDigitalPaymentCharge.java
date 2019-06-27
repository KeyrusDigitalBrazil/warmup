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
 * Payment Charge fields for SAP Digital payment
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentCharge
{

	@JsonProperty("PaymentByPaymentServicePrvdr")
	private String paymentByPaymentServicePrvdr;

	@JsonProperty("AmountInPaymentCurrency")
	private String amountInPaymentCurrency;

	@JsonProperty("PaymentCurrency")
	private String paymentCurrency;

	@JsonProperty("PaymentDateTime")
	private String paymentDateTime;

	@JsonProperty("PaymentStatus")
	private String paymentStatus;

	@JsonProperty("PaymentStatusName")
	private String paymentStatusName;

	/**
	 * @return the paymentByPaymentServicePrvdr
	 */
	public String getPaymentByPaymentServicePrvdr()
	{
		return paymentByPaymentServicePrvdr;
	}

	/**
	 * @param paymentByPaymentServicePrvdr
	 *           the paymentByPaymentServicePrvdr to set
	 */
	public void setPaymentByPaymentServicePrvdr(final String paymentByPaymentServicePrvdr)
	{
		this.paymentByPaymentServicePrvdr = paymentByPaymentServicePrvdr;
	}

	/**
	 * @return the amountInPaymentCurrency
	 */
	public String getAmountInPaymentCurrency()
	{
		return amountInPaymentCurrency;
	}

	/**
	 * @param amountInPaymentCurrency
	 *           the amountInPaymentCurrency to set
	 */
	public void setAmountInPaymentCurrency(final String amountInPaymentCurrency)
	{
		this.amountInPaymentCurrency = amountInPaymentCurrency;
	}

	/**
	 * @return the paymentCurrency
	 */
	public String getPaymentCurrency()
	{
		return paymentCurrency;
	}

	/**
	 * @param paymentCurrency
	 *           the paymentCurrency to set
	 */
	public void setPaymentCurrency(final String paymentCurrency)
	{
		this.paymentCurrency = paymentCurrency;
	}

	/**
	 * @return the paymentDateTime
	 */
	public String getPaymentDateTime()
	{
		return paymentDateTime;
	}

	/**
	 * @param paymentDateTime
	 *           the paymentDateTime to set
	 */
	public void setPaymentDateTime(final String paymentDateTime)
	{
		this.paymentDateTime = paymentDateTime;
	}

	/**
	 * @return the paymentStatus
	 */
	public String getPaymentStatus()
	{
		return paymentStatus;
	}

	/**
	 * @param paymentStatus
	 *           the paymentStatus to set
	 */
	public void setPaymentStatus(final String paymentStatus)
	{
		this.paymentStatus = paymentStatus;
	}

	/**
	 * @return the paymentStatusName
	 */
	public String getPaymentStatusName()
	{
		return paymentStatusName;
	}

	/**
	 * @param paymentStatusName
	 *           the paymentStatusName to set
	 */
	public void setPaymentStatusName(final String paymentStatusName)
	{
		this.paymentStatusName = paymentStatusName;
	}

}
