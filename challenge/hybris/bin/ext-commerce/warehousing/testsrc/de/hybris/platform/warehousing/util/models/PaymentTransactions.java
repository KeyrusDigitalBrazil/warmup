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

import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.warehousing.util.builder.PaymentTransactionModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;


public class PaymentTransactions extends AbstractItems<PaymentTransactionModel>
{
	public static final String TRANSACTION_CODE = "0";
	public static final String MOCK_PAYMENT_PROVIDER = "Mockup";

	private WarehousingDao<PaymentTransactionModel> paymentTransactionDao;
	private PaymentTransactionEntries paymentTransactionEntries;

	public PaymentTransactionModel CreditCardTransaction()
	{
		return getOrSaveAndReturn(() -> getPaymentTransactionDao().getByCode(TRANSACTION_CODE),
				() -> PaymentTransactionModelBuilder.aModel().withCode(TRANSACTION_CODE).withPaymentProvider(MOCK_PAYMENT_PROVIDER)
						.withEntries(Collections.singletonList(getPaymentTransactionEntries().AuthorizationPaymentTransactionEntry()))
						.build());
	}

	protected WarehousingDao<PaymentTransactionModel> getPaymentTransactionDao()
	{
		return paymentTransactionDao;
	}

	@Required
	public void setPaymentTransactionDao(final WarehousingDao<PaymentTransactionModel> paymentTransactionDao)
	{
		this.paymentTransactionDao = paymentTransactionDao;
	}

	protected PaymentTransactionEntries getPaymentTransactionEntries()
	{
		return paymentTransactionEntries;
	}

	@Required
	public void setPaymentTransactionEntries(final PaymentTransactionEntries paymentTransactionEntries)
	{
		this.paymentTransactionEntries = paymentTransactionEntries;
	}
}
