/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sappricingbol.constants;

import java.io.Serializable;


/**
 * Enumeration that stores satandard hybris payment modes
 */
public enum PaymentModeEnum implements Serializable
{
	/**
	 * invoice payment mode
	 */
	InvoicePaymentInfo("invoice"),
	/**
	 * credit cart payment mode
	 */
	CreditCardPaymentInfo("creditcard"),
	/**
	 * debit payment mode
	 */
	DebitPaymentInfo("debitentry"),
	/**
	 * advance payment mode
	 */
	AdvancePaymentInfo("advance");

	private String paymentType;

	private PaymentModeEnum(final String paymentType)
	{
		this.paymentType = paymentType;
	}

	/**
	 * @param paymentType
	 * @return the payment code corresponding to parameter payment type
	 */
	public static String getPaymentTypeCode(final String paymentType)
	{
		return PaymentModeEnum.valueOf(paymentType).paymentType;

	}



}
