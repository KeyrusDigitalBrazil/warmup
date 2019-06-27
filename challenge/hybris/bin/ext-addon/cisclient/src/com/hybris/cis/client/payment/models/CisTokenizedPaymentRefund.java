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
 * A payment request to refund an amount of money based on a previous tokenized capture.
 */
@XmlRootElement(name = "tokenizedPaymentRefund")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisTokenizedPaymentRefund extends CisPaymentRequest
{

	/** specifies the capture id to do the refund on. */
	@XmlElement(name = "captureRequestId")
	private String captureRequestId;

	/** specifies the capture request token to refund on. */
	@XmlElement(name = "captureRequestToken")
	private String captureRequestToken;

	public String getCaptureRequestId()
	{
		return this.captureRequestId;
	}

	public void setCaptureRequestId(final String captureRequestId)
	{
		this.captureRequestId = captureRequestId;
	}

	public String getCaptureRequestToken()
	{
		return this.captureRequestToken;
	}

	public void setCaptureRequestToken(final String captureRequestToken)
	{
		this.captureRequestToken = captureRequestToken;
	}

	@Override
	public String toString()
	{
		return "CisTokenizedPaymentRefund [amount=" + this.getAmount() + ", currency=" + this.getCurrency() + ", captureRequestId="
				+ this.captureRequestId + ", captureRequestToken=" + this.captureRequestToken + "]";
	}

}
