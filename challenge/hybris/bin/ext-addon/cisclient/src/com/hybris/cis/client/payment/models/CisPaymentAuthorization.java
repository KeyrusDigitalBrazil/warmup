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
import javax.xml.bind.annotation.XmlRootElement;


/**
 * A request to authorize an amount of money.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "authorization")
public class CisPaymentAuthorization extends CisPaymentRequest
{
	/** ID provided by the client that is unique per authorization (e.g. the shipment, return or order id) */
	@XmlElement(name = "clientAuthorizationId")
	private String clientAuthorizationId;

	public CisPaymentAuthorization()
	{
		super();
		// required for jaxb
	}

	public CisPaymentAuthorization(final String clientAuthorizationId)
	{
		super();
		this.clientAuthorizationId = clientAuthorizationId;
	}

	public String getClientAuthorizationId()
	{
		return this.clientAuthorizationId;
	}

	public void setClientAuthorizationId(final String clientAuthorizationId)
	{
		this.clientAuthorizationId = clientAuthorizationId;
	}

	@Override
	public String toString()
	{
		return "CisPaymentAuthorization [clientAuthorizationId=" + this.clientAuthorizationId + ", amount()=" + this.getAmount()
				+ ", currency=" + this.getCurrency() + "]";
	}

}
