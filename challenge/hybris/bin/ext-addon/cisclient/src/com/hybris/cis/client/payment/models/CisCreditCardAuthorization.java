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

import com.hybris.cis.client.shared.models.CisAddress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * A payment request to authorize an amount of money based on credit card details.
 */
@XmlRootElement(name = "creditCardAuthorization")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisCreditCardAuthorization extends CisPaymentAuthorization
{

	/** The credit card details. */
	@XmlElement(name = "creditCard")
	private CisCreditCard creditCard;

	/** The billing address. */
	@XmlElement(name = "address")
	private CisAddress address;

	/**
	 * Default constructor.
	 */
	public CisCreditCardAuthorization()
	{
		super();
	}

	public CisCreditCardAuthorization(final String clientAuthorizationId)
	{
		super(clientAuthorizationId);
	}

	public CisCreditCard getCreditCard()
	{
		return this.creditCard;
	}

	public void setCreditCard(final CisCreditCard creditCard)
	{
		this.creditCard = creditCard;
	}

	public CisAddress getAddress()
	{
		return this.address;
	}

	public void setAddress(final CisAddress address)
	{
		this.address = address;
	}

	@Override
	public String toString()
	{
		return "CisCreditCardAuthorization [creditCard=" + this.creditCard + ", address=" + this.address
				+ ", clientAuthorizationId=" + this.getClientAuthorizationId() + ", amount=" + this.getAmount() + ", currency="
				+ this.getCurrency() + "]";
	}

}
