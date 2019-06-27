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
 * A request to reverse a previous {@link CisTokenizedPaymentAuthorization}.
 */
@XmlRootElement(name = "tokenizedPaymentReverse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisTokenizedPaymentReverse extends CisPaymentRequest
{

	/** specifies the authorization request id. */
	@XmlElement(name = "authorizationRequestId")
	private String authorizationRequestId;

	/** specifies the authorization request token. */
	@XmlElement(name = "authorizationRequestToken")
	private String authorizationRequestToken;

	public String getAuthorizationRequestId()
	{
		return this.authorizationRequestId;
	}

	public void setAuthorizationRequestId(final String authorizationRequestId)
	{
		this.authorizationRequestId = authorizationRequestId;
	}

	public String getAuthorizationRequestToken()
	{
		return this.authorizationRequestToken;
	}

	public void setAuthorizationRequestToken(final String authorizationRequestToken)
	{
		this.authorizationRequestToken = authorizationRequestToken;
	}

	@Override
	public String toString()
	{
		return "CisTokenizedPaymentReverse [amount=" + this.getAmount() + ", currency=" + this.getCurrency()
				+ ", authorizationRequestId=" + this.authorizationRequestId + ", authorizationRequestToken="
				+ this.authorizationRequestToken + "]";
	}
}
