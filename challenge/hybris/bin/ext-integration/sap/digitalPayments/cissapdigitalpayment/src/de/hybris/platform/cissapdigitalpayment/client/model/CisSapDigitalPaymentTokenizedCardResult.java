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
 * SAP Digital payment Tokenized card result fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentTokenizedCardResult
{

	@JsonProperty("PaytCardByDigitalPaymentSrvc")
	private String paytCardByDigitalPaymentSrvc;

	@JsonProperty("PaymentCardType")
	private String paymentCardType;

	@JsonProperty("PaymentCardExpirationMonth")
	private String paymentCardExpirationMonth;

	@JsonProperty("PaymentCardExpirationYear")
	private String paymentCardExpirationYear;

	@JsonProperty("PaymentCardMaskedNumber")
	private String paymentCardMaskedNumber;

	@JsonProperty("PaymentCardHolderName")
	private String paymentCardHolderName;

	/**
	 * @return the paytCardByDigitalPaymentSrvc
	 */
	public String getPaytCardByDigitalPaymentSrvc()
	{
		return paytCardByDigitalPaymentSrvc;
	}

	/**
	 * @param paytCardByDigitalPaymentSrvc
	 *           the paytCardByDigitalPaymentSrvc to set
	 */
	public void setPaytCardByDigitalPaymentSrvc(final String paytCardByDigitalPaymentSrvc)
	{
		this.paytCardByDigitalPaymentSrvc = paytCardByDigitalPaymentSrvc;
	}

	/**
	 * @return the paymentCardType
	 */
	public String getPaymentCardType()
	{
		return paymentCardType;
	}

	/**
	 * @param paymentCardType
	 *           the paymentCardType to set
	 */
	public void setPaymentCardType(final String paymentCardType)
	{
		this.paymentCardType = paymentCardType;
	}

	/**
	 * @return the paymentCardExpirationMonth
	 */
	public String getPaymentCardExpirationMonth()
	{
		return paymentCardExpirationMonth;
	}

	/**
	 * @param paymentCardExpirationMonth
	 *           the paymentCardExpirationMonth to set
	 */
	public void setPaymentCardExpirationMonth(final String paymentCardExpirationMonth)
	{
		this.paymentCardExpirationMonth = paymentCardExpirationMonth;
	}

	/**
	 * @return the paymentCardExpirationYear
	 */
	public String getPaymentCardExpirationYear()
	{
		return paymentCardExpirationYear;
	}

	/**
	 * @param paymentCardExpirationYear
	 *           the paymentCardExpirationYear to set
	 */
	public void setPaymentCardExpirationYear(final String paymentCardExpirationYear)
	{
		this.paymentCardExpirationYear = paymentCardExpirationYear;
	}

	/**
	 * @return the paymentCardMaskedNumber
	 */
	public String getPaymentCardMaskedNumber()
	{
		return paymentCardMaskedNumber;
	}

	/**
	 * @param paymentCardMaskedNumber
	 *           the paymentCardMaskedNumber to set
	 */
	public void setPaymentCardMaskedNumber(final String paymentCardMaskedNumber)
	{
		this.paymentCardMaskedNumber = paymentCardMaskedNumber;
	}

	/**
	 * @return the paymentCardHolderName
	 */
	public String getPaymentCardHolderName()
	{
		return paymentCardHolderName;
	}

	/**
	 * @param paymentCardHolderName
	 *           the paymentCardHolderName to set
	 */
	public void setPaymentCardHolderName(final String paymentCardHolderName)
	{
		this.paymentCardHolderName = paymentCardHolderName;
	}

	@Override
	public String toString()
	{
		// YTODO Auto-generated method stub
		return "{" + "PaytCardByDigitalPaymentSrvc='" + paytCardByDigitalPaymentSrvc + '\'' + "PaymentCardType='" + paymentCardType
				+ "PaymentCardExpirationMonth='" + paymentCardExpirationMonth + "'PaymentCardExpirationYear='"
				+ paymentCardExpirationYear + "PaymentCardMaskedNumber='" + paymentCardMaskedNumber + "PaymentCardHolderName='"
				+ paymentCardHolderName + '}';
	}




}
