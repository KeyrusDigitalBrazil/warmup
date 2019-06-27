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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.warehousing.util.builder.PaymentTransactionEntryModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;

import org.springframework.beans.factory.annotation.Required;


public class PaymentTransactionEntries extends AbstractItems<PaymentTransactionEntryModel>
{
	public static final String AUTHORIZATION_CODE = "0";

	private WarehousingDao<PaymentTransactionEntryModel> paymentTransactionEntryDao;
	private Currencies currencies;

	public PaymentTransactionEntryModel AuthorizationPaymentTransactionEntry()
	{
		return getOrSaveAndReturn(() -> getPaymentTransactionEntryDao().getByCode(AUTHORIZATION_CODE),
				() -> PaymentTransactionEntryModelBuilder.aModel().withCode(AUTHORIZATION_CODE)
						.withType(PaymentTransactionType.AUTHORIZATION).withCurrency(getCurrencies().AmericanDollar()).build());
	}

	protected WarehousingDao<PaymentTransactionEntryModel> getPaymentTransactionEntryDao()
	{
		return paymentTransactionEntryDao;
	}

	@Required
	public void setPaymentTransactionEntryDao(final WarehousingDao<PaymentTransactionEntryModel> paymentTransactionDao)
	{
		this.paymentTransactionEntryDao = paymentTransactionDao;
	}

	protected Currencies getCurrencies()
	{
		return currencies;
	}

	@Required
	public void setCurrencies(Currencies currencies)
	{
		this.currencies = currencies;
	}
}
