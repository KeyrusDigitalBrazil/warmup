/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.util.List;


public class PaymentTransactionModelBuilder
{
	private final PaymentTransactionModel model;

	private PaymentTransactionModelBuilder()
	{
		model = new PaymentTransactionModel();
	}

	public static PaymentTransactionModelBuilder aModel()
	{
		return new PaymentTransactionModelBuilder();
	}

	private PaymentTransactionModel getModel()
	{
		return model;
	}

	public PaymentTransactionModel build()
	{
		return getModel();
	}

	public PaymentTransactionModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public PaymentTransactionModelBuilder withPaymentProvider(final String paymentProvider)
	{
		getModel().setPaymentProvider(paymentProvider);
		return this;
	}

	public PaymentTransactionModelBuilder withEntries(final List<PaymentTransactionEntryModel> entries)
	{
		getModel().setEntries(entries);
		return this;
	}
}
