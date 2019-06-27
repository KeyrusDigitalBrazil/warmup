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
import java.math.BigDecimal;
import com.hybris.cis.client.shared.models.AnnotationHashMap;


/**
 * A payment request to for example authorize or capture an amount.
 */
@XmlRootElement(name = "paymentRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisPaymentRequest
{
	/** The amount of the transaction. */
	@XmlElement(name = "amount")
	private BigDecimal amount;

	/** 3 letter ISO 4217 currency code. */
	@XmlElement(name = "currency")
	private String currency;

	/** Vendor specific parameters which aren't represented in the generic interface. */
	@XmlElement(name = "vendorParameters")
	private AnnotationHashMap vendorParameters;

	public CisPaymentRequest()
	{
		// default constructor required by jaxb
	}

	public CisPaymentRequest(final CisPaymentRequest request)
	{
		super();
		this.amount = request.getAmount();
		this.currency = request.getCurrency();
	}

	public BigDecimal getAmount()
	{
		return this.amount;
	}

	public void setAmount(final BigDecimal value)
	{
		this.amount = value;
	}

	public String getCurrency()
	{
		return this.currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	@Override
	public String toString()
	{
		return "CisPaymentRequest [amount=" + this.amount + ", currency=" + this.currency + "]";
	}

	public AnnotationHashMap getVendorParameters()
	{
		return this.vendorParameters;
	}

	public void setVendorParameters(final AnnotationHashMap vendorParameters)
	{
		this.vendorParameters = vendorParameters;
	}
}
