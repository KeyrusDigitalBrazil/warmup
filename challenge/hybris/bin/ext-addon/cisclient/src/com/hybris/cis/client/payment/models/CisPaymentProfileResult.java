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
import com.hybris.cis.client.shared.models.CisCvnDecision;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The result of a profile creation.
 */
@XmlRootElement(name = "paymentProfileResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class CisPaymentProfileResult extends CisPaymentTransactionResult
{
	/** 3 letter ISO 4217 currency code. */
	@XmlElement(name = "currency")
	private String currency;

	/** Result of either a credit card authorization or of a set up fee capture used to validate the credit card. */
	@XmlElement(name = "validationResult")
	private CisPaymentTransactionResult validationResult;

	/** Credit card number. */
	@XmlElement(name = "creditCard")
	private CisCreditCard creditCard;

	/** Customer information. */
	@XmlElement(name = "customerAddress")
	private CisAddress customerAddress;

	/** Comments about the creation. */
	@XmlElement(name = "comments")
	private String comments;

	/** CVN decision. */
	@XmlElement(name = "cvnDecision")
	private CisCvnDecision cvnDecision;

	public CisPaymentTransactionResult getValidationResult()
	{
		return this.validationResult;
	}

	public void setValidationResult(final CisPaymentTransactionResult validationResult)
	{
		this.validationResult = validationResult;
	}

	public String getCurrency()
	{
		return this.currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	public CisAddress getCustomerAddress()
	{
		return this.customerAddress;
	}

	public void setCustomerAddress(final CisAddress customerAddress)
	{
		this.customerAddress = customerAddress;
	}

	public String getComments()
	{
		return this.comments;
	}

	public void setComments(final String comments)
	{
		this.comments = comments;
	}

	public CisCreditCard getCreditCard()
	{
		return this.creditCard;
	}

	public void setCreditCard(final CisCreditCard creditCard)
	{
		this.creditCard = creditCard;
	}

	public void setCvnDecision(final CisCvnDecision cvnDecision)
	{
		this.cvnDecision = cvnDecision;
	}

	public CisCvnDecision getCvnDecision()
	{
		return cvnDecision;
	}
}
