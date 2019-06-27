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

import com.hybris.cis.client.shared.models.CisResult;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The result of a payment transaction such as authorizing or capturing.
 */
@XmlRootElement(name = "paymentTransactionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisPaymentTransactionResult extends CisResult
{
	/** The original payment request. */
	@XmlElement(name = "request")
	private CisPaymentRequest request;

	/** The amount that was cleared (i.e. authorized or captured). */
	@XmlElement(name = "amount")
	private BigDecimal amount;

	/** The remaining balance of the given payment method (might be null). */
	@XmlElement(name = "balance")
	private BigDecimal balance;

	/** The transaction verification key. */
	@XmlElement(name = "transactionVerificationKey")
	private String transactionVerificationKey;

	/** ID provided by the client that is unique per authorization (e.g. the shipment, return or order id) */
	private transient String clientAuthorizationId;

	public CisPaymentRequest getRequest()
	{
		return this.request;
	}

	public void setRequest(final CisPaymentRequest request)
	{
		this.request = request;
	}

	public BigDecimal getAmount()
	{
		return this.amount;
	}

	public void setAmount(final BigDecimal amount)
	{
		this.amount = amount;
	}

	public BigDecimal getBalance()
	{
		return this.balance;
	}

	public void setBalance(final BigDecimal balance)
	{
		this.balance = balance;
	}

	public String getClientAuthorizationId()
	{
		return this.clientAuthorizationId;
	}

	public void setClientAuthorizationId(final String clientAuthorizationId)
	{
		this.clientAuthorizationId = clientAuthorizationId;
	}

	public String getTransactionVerificationKey()
	{
		return this.transactionVerificationKey;
	}

	public void setTransactionVerificationKey(final String transactionVerificationKey)
	{
		this.transactionVerificationKey = transactionVerificationKey;
	}

	@Override
	public String toString()
	{
		return "CisPaymentTransactionResult [request=" + this.request + ", amount=" + this.amount + ", balance=" + this.balance
				+ ", transactionVerificationKey=" + this.transactionVerificationKey + ", clientAuthorizationId="
				+ this.clientAuthorizationId + ", decision=" + this.getDecision() + ", id=" + this.getId() + ", vendorReasonCode="
				+ this.getVendorReasonCode() + ", vendorStatusCode=" + this.getVendorStatusCode() + ", clientRefId="
				+ this.getClientRefId() + "]";
	}
}
