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
 * SAP Digital payment refund request fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentRefundRequest
{

	@JsonProperty("Source")
	private CisSapDigitalPaymentSource cisSapDigitalPaymentSource;

	@JsonProperty("AmountInRefundCurrency")
	private String amountInRefundCurrency;

	@JsonProperty("RefundCurrency")
	private String refundCurrency;

	@JsonProperty("PaymentTransactionDescription")
	private String paymentTransactionDescription;

	@JsonProperty("PaymentByPaymentServicePrvdr")
	private String paymentByPaymentServicePrvdr;


	@JsonProperty("ReferenceDocument")
	private String referenceDocument;


	/**
	 * @return the cisSapDigitalPaymentSource
	 */
	public CisSapDigitalPaymentSource getCisSapDigitalPaymentSource()
	{
		return cisSapDigitalPaymentSource;
	}


	/**
	 * @param cisSapDigitalPaymentSource
	 *           the cisSapDigitalPaymentSource to set
	 */
	public void setCisSapDigitalPaymentSource(final CisSapDigitalPaymentSource cisSapDigitalPaymentSource)
	{
		this.cisSapDigitalPaymentSource = cisSapDigitalPaymentSource;
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
	 * @return the paymentTransactionDescription
	 */
	public String getPaymentTransactionDescription()
	{
		return paymentTransactionDescription;
	}


	/**
	 * @param paymentTransactionDescription
	 *           the paymentTransactionDescription to set
	 */
	public void setPaymentTransactionDescription(final String paymentTransactionDescription)
	{
		this.paymentTransactionDescription = paymentTransactionDescription;
	}


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
	 * @return the referenceDocument
	 */
	public String getReferenceDocument()
	{
		return referenceDocument;
	}


	/**
	 * @param referenceDocument
	 *           the referenceDocument to set
	 */
	public void setReferenceDocument(final String referenceDocument)
	{
		this.referenceDocument = referenceDocument;
	}


}
