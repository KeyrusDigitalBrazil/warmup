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
 * A payment request to initiate payment session.
 */
@XmlRootElement(name = "paymentSessionInitRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisPaymentSessionInitRequest extends CisPaymentRequest
{

	/** If the customer approves the transaction, the customer will be redirected to this URL. */
	@XmlElement(name = "returnUrl")
	private String returnUrl;

	/** If the customer cancels the transaction, the customer will be redirected to this URL. */
	@XmlElement(name = "cancelReturnUrl")
	private String cancelReturnUrl;

	/** The billing address. */
	@XmlElement(name = "address")
	private CisAddress address;

	/**
	 * Email address of the buyer as entered during checkout. PayPal uses this value to pre-fill the PayPal membership
	 * sign-up portion of the PayPal login page.
	 */
	@XmlElement(name = "customerEmail")
	private String customerEmail;

	public String getReturnUrl()
	{
		return this.returnUrl;
	}

	public void setReturnUrl(final String returnUrl)
	{
		this.returnUrl = returnUrl;
	}

	public String getCancelReturnUrl()
	{
		return this.cancelReturnUrl;
	}

	public void setCancelReturnUrl(final String cancelReturnUrl)
	{
		this.cancelReturnUrl = cancelReturnUrl;
	}

	public CisAddress getAddress()
	{
		return this.address;
	}

	public void setAddress(final CisAddress address)
	{
		this.address = address;
	}

	public String getCustomerEmail()
	{
		return this.customerEmail;
	}

	public void setCustomerEmail(final String customerEmail)
	{
		this.customerEmail = customerEmail;
	}

	@Override
	public String toString()
	{
		return "CisPaymentSessionInitRequest [amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", returnUrl="
				+ this.returnUrl + ", cancelReturnUrl=" + this.cancelReturnUrl + ", address=" + this.address + ", customerEmail="
				+ this.customerEmail + "]";
	}
}
