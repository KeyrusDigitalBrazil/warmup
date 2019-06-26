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

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;


public class PaymentTransactionEntryModelBuilder
{
	private final PaymentTransactionEntryModel model;

	private PaymentTransactionEntryModelBuilder()
	{
		model = new PaymentTransactionEntryModel();
	}

	public static PaymentTransactionEntryModelBuilder aModel()
	{
		return new PaymentTransactionEntryModelBuilder();
	}

	private PaymentTransactionEntryModel getModel()
	{
		return model;
	}

	public PaymentTransactionEntryModel build()
	{
		return getModel();
	}

	public PaymentTransactionEntryModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public PaymentTransactionEntryModelBuilder withType(final PaymentTransactionType paymentTransactionType)
	{
		getModel().setType(paymentTransactionType);
		return this;
	}

	public PaymentTransactionEntryModelBuilder withCurrency(final CurrencyModel currency)
	{
		getModel().setCurrency(currency);
		return this;
	}
}
