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
package de.hybris.platform.commerceservices.order.hook;

import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 * Hook interface for Payment Authroization
 */
public interface AuthorizePaymentMethodHook
{
	/**
	 * Executed before authorizing payment
	 *
	 * @param  parameter object containing all the information for checkout
	 */
	void beforeAuthorizePaymentAmount(CommerceCheckoutParameter parameter);

	/**
	 * Executed after authorizing payment
	 *
	 * @param parameter object containing all the information for checkout
	 * @param paymentTransactionEntryModel object containing all information for payment transaction
	 */
	void afterAuthorizePaymentAmount(CommerceCheckoutParameter parameter, PaymentTransactionEntryModel paymentTransactionEntryModel);
}
