/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;


/**
 * Default implementation for {@link SapDigitalPaymentAuthorizationStrategy}
 */
public class DefaultSapDigitalPaymentAuthorizationStrategy implements SapDigitalPaymentAuthorizationStrategy
{


	private CommerceCheckoutService commerceCheckoutService;


	@Override
	public boolean authorizePayment(final CommerceCheckoutParameter parameter)
	{
		final PaymentTransactionEntryModel paymentTransactionEntryModel = getCommerceCheckoutService().authorizePayment(parameter);

		final boolean authorizePayment = paymentTransactionEntryModel != null
				&& (TransactionStatus.ACCEPTED.name().equals(paymentTransactionEntryModel.getTransactionStatus())
						|| TransactionStatus.REVIEW.name().equals(paymentTransactionEntryModel.getTransactionStatus()));

		return authorizePayment;
	}

	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}



}