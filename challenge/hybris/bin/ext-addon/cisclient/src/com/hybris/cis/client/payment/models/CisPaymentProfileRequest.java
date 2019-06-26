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
 * The result of a payment transaction such as authorizing or capturing.
 */
@XmlRootElement(name = "paymentProfileRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisPaymentProfileRequest extends CisPaymentRequest
{
	/** ID of a payment profile. */
	@XmlElement(name = "profileId")
	private String profileId;

	/** The credit card details. */
	@XmlElement(name = "creditCard")
	private CisCreditCard creditCard;

	/** The billing address. */
	@XmlElement(name = "billingAddress")
	private CisAddress billingAddress;

	/** The shipping address. */
	@XmlElement(name = "shippingAddress")
	private CisAddress shippingAddress;

	/** The frequency of the recurring payment. */
	@XmlElement(name = "recurringFrequency")
	private String recurringFrequency;

	public String getRecurringFrequency()
	{
		return this.recurringFrequency;
	}

	public void setRecurringFrequency(final String recrringFrequency)
	{
		this.recurringFrequency = recrringFrequency;
	}

	public String getProfileId()
	{
		return this.profileId;
	}

	public void setProfileId(final String profileId)
	{
		this.profileId = profileId;
	}

	public CisCreditCard getCreditCard()
	{
		return this.creditCard;
	}

	public void setCreditCard(final CisCreditCard creditCard)
	{
		this.creditCard = creditCard;
	}

	public CisAddress getBillingAddress()
	{
		return this.billingAddress;
	}

	public void setBillingAddress(final CisAddress billingAddress)
	{
		this.billingAddress = billingAddress;
	}

	public CisAddress getShippingAddress()
	{
		return this.shippingAddress;
	}

	public void setShippingAddress(final CisAddress shippingAddress)
	{
		this.shippingAddress = shippingAddress;
	}
}
