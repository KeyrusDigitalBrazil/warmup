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
package com.hybris.cis.client.payment.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;


/**
 * Container for credit card information.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CisCreditCard
{
	/** The credit card type. */
	@XmlElement(name = "cardType")
	private String cardType;

	/** The credit card holder. */
	@XmlElement(name = "cardHolder")
	private String cardHolder;

	/** The credit card number. */
	@XmlElement(name = "ccNumber")
	private String ccNumber;

	/** The card verification code (CVC). */
	@XmlElement(name = "cvc")
	private String cvc;

	/** The expiration month. */
	@XmlElement(name = "expirationMonth")
	private int expirationMonth;

	/** The expiration year. */
	@XmlElement(name = "expirationYear")
	private int expirationYear;

	public String getCardType()
	{
		return this.cardType;
	}

	public void setCardType(final String cardType)
	{
		this.cardType = cardType;
	}

	public String getCardHolder()
	{
		return this.cardHolder;
	}

	public void setCardHolder(final String cardHolder)
	{
		this.cardHolder = cardHolder;
	}

	public String getCcNumber()
	{
		return this.ccNumber;
	}

	public void setCcNumber(final String ccNumber)
	{
		this.ccNumber = ccNumber;
	}

	public String getCvc()
	{
		return this.cvc;
	}

	public void setCvc(final String cvc)
	{
		this.cvc = cvc;
	}

	public int getExpirationMonth()
	{
		return this.expirationMonth;
	}

	public void setExpirationMonth(final int expirationMonth)
	{
		this.expirationMonth = expirationMonth;
	}

	public int getExpirationYear()
	{
		return this.expirationYear;
	}

	public void setExpirationYear(final int expirationYear)
	{
		this.expirationYear = expirationYear;
	}

	@Override
	public String toString()
	{
		return "CisCreditCard [cardType=" + this.cardType + ", cardHolder=" + this.cardHolder + ", expirationMonth="
				+ this.expirationMonth + ", expirationYear=" + this.expirationYear + "]";
	}

}
