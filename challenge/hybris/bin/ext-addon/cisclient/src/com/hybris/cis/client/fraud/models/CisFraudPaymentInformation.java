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
package com.hybris.cis.client.fraud.models;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class CisFraudPaymentInformation
{
	@XmlElement(name = "type")
	private String type;

	@XmlElement(name = "amount")
	private BigDecimal amount;

	@XmlElement(name = "cardType")
	private String cardType;

	@XmlElement(name = "cardNumber")
	private String cardNumber;

	@XmlElement(name = "cardAuthorizationCode")
	private String cardAuthorizationCode;

	@XmlElement(name = "cardExpireMonth")
	private String cardExpireMonth;

	@XmlElement(name = "cardExpireYear")
	private String cardExpireYear;

	@XmlElement(name = "cardHolderName")
	private String cardHolderName;

	@XmlElement(name = "cardAuthorizedAmount")
	private BigDecimal cardAuthorizedAmount;

	@XmlElement(name = "avsResponse")
	private String avsResponse;

	@XmlElement(name = "cvvResponse")
	private String cvvResponse;

	@XmlElement(name = "paypalRequestId")
	private String paypalRequestId;

	@XmlElement(name = "paypalEmail")
	private String paypalEmail;

	@XmlElement(name = "paypalStatus")
	private String paypalStatus;

	@XmlElement(name = "paypalAuthorizedAmount")
	private BigDecimal paypalAuthorizedAmount;

	public String getType()
	{
		return this.type;
	}

	public void setType(final String type)
	{
		this.type = type;
	}

	public BigDecimal getAmount()
	{
		return this.amount;
	}

	public void setAmount(final BigDecimal amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the type of credit card
	 */
	public String getCardType()
	{
		return this.cardType;
	}

	/**
	 * @param cardType sets the type of credit card to be used.
	 */
	public void setCardType(final String cardType)
	{
		this.cardType = cardType;
	}

	/**
	 * @return the credit card number
	 */
	public String getCardNumber()
	{
		return this.cardNumber;
	}

	/**
	 * @param cardNumber sets the creditcard number
	 */
	public void setCardNumber(final String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public String getCardHolderName()
	{
		return this.cardHolderName;
	}

	public void setCardHolderName(final String cardHolderName)
	{
		this.cardHolderName = cardHolderName;
	}


	/**
	 * @return the response of the address verification
	 */
	public String getAvsResponse()
	{
		return this.avsResponse;
	}

	/**
	 * @param avsResponse sets the response of the address verification
	 */
	public void setAvsResponse(final String avsResponse)
	{
		this.avsResponse = avsResponse;
	}

	public String getCvvResponse()
	{
		return this.cvvResponse;
	}

	public void setCvvResponse(final String cvvResponse)
	{
		this.cvvResponse = cvvResponse;
	}

	public String getPaypalRequestId()
	{
		return this.paypalRequestId;
	}

	public void setPaypalRequestId(final String paypalRequestId)
	{
		this.paypalRequestId = paypalRequestId;
	}

	/**
	 * @return the email address specified at paypal
	 */
	public String getPaypalEmail()
	{
		return this.paypalEmail;
	}

	/**
	 * @param paypalEmail set the email address specified at paypal
	 */
	public void setPaypalEmail(final String paypalEmail)
	{
		this.paypalEmail = paypalEmail;
	}

	/**
	 * @return the paypal status
	 */
	public String getPaypalStatus()
	{
		return this.paypalStatus;
	}

	/**
	 * @param paypalStatus sets the status at paypal
	 */
	public void setPaypalStatus(final String paypalStatus)
	{
		this.paypalStatus = paypalStatus;
	}

	/**
	 * @return the authorization code of the credit card.
	 */
	public String getCardAuthorizationCode()
	{
		return this.cardAuthorizationCode;
	}

	/**
	 * @param cardAuthorizationCode sets the authorization code of the credit card
	 */
	public void setCardAuthorizationCode(final String cardAuthorizationCode)
	{
		this.cardAuthorizationCode = cardAuthorizationCode;
	}

	/**
	 * @return the expiration month of the credit card
	 */
	public String getCardExpireMonth()
	{
		return this.cardExpireMonth;
	}

	/**
	 * @param cardExpireMonth sets the expiration month of the credit card
	 */
	public void setCardExpireMonth(final String cardExpireMonth)
	{
		this.cardExpireMonth = cardExpireMonth;
	}

	public String getCardExpireYear()
	{
		return this.cardExpireYear;
	}

	/**
	 * @param cardExpireYear the expiration year of the credit card
	 */
	public void setCardExpireYear(final String cardExpireYear)
	{
		this.cardExpireYear = cardExpireYear;
	}

	/**
	 * @return the by creditcard authorized amount
	 */
	public BigDecimal getCardAuthorizedAmount()
	{
		return this.cardAuthorizedAmount;
	}

	/**
	 * @param cardAuthorizedAmount sets the by credit card authorized amount
	 */
	public void setCardAuthorizedAmount(final BigDecimal cardAuthorizedAmount)
	{
		this.cardAuthorizedAmount = cardAuthorizedAmount;
	}

	/**
	 * @return the by paypal authorized amount
	 */
	public BigDecimal getPaypalAuthorizedAmount()
	{
		return this.paypalAuthorizedAmount;
	}

	/**
	 * @param paypalAuthorizedAmount the by paypal authorized amount
	 */
	public void setPaypalAuthorizedAmount(final BigDecimal paypalAuthorizedAmount)
	{
		this.paypalAuthorizedAmount = paypalAuthorizedAmount;
	}
}
