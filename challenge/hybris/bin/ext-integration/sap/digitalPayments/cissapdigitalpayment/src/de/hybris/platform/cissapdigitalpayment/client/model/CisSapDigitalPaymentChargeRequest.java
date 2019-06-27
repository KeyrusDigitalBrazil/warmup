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
 * SAP Digital payment charge request fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentChargeRequest
{
	@JsonProperty("Source")
	private CisSapDigitalPaymentSource cisSapDigitalPaymentSource;

	@JsonProperty("AmountInPaymentCurrency")
	private String amountInPaymentCurrency;

	@JsonProperty("PaymentCurrency")
	private String paymentCurrency;

	@JsonProperty("PaymentTransactionDescription")
	private String paymentTransactionDescription;

	@JsonProperty("PaymentIsToBeCaptured")
	private String paymentIsToBeCaptured;

	@JsonProperty("ReferenceDocument")
	private String referenceDocument;

	@JsonProperty("Authorization")
	private CisSapDigitalPaymentAuthorization cisSapDigitalPaymentAuthorization;

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
	 * @return the paymentIsToBeCaptured
	 */
	public String getPaymentIsToBeCaptured()
	{
		return paymentIsToBeCaptured;
	}

	/**
	 * @param paymentIsToBeCaptured
	 *           the paymentIsToBeCaptured to set
	 */
	public void setPaymentIsToBeCaptured(final String paymentIsToBeCaptured)
	{
		this.paymentIsToBeCaptured = paymentIsToBeCaptured;
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

	/**
	 * @return the cisSapDigitalPaymentAuthorization
	 */
	public CisSapDigitalPaymentAuthorization getCisSapDigitalPaymentAuthorization()
	{
		return cisSapDigitalPaymentAuthorization;
	}

	/**
	 * @param cisSapDigitalPaymentAuthorization
	 *           the cisSapDigitalPaymentAuthorization to set
	 */
	public void setCisSapDigitalPaymentAuthorization(final CisSapDigitalPaymentAuthorization cisSapDigitalPaymentAuthorization)
	{
		this.cisSapDigitalPaymentAuthorization = cisSapDigitalPaymentAuthorization;
	}


}
