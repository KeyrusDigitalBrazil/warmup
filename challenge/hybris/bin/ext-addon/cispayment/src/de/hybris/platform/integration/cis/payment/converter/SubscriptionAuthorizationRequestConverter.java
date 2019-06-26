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
package de.hybris.platform.integration.cis.payment.converter;

import com.hybris.cis.client.payment.models.CisPaymentAuthorization;
import de.hybris.platform.payment.commands.request.SubscriptionAuthorizationRequest;


public class SubscriptionAuthorizationRequestConverter
{
	public CisPaymentAuthorization convert(final SubscriptionAuthorizationRequest request)
	{
		final CisPaymentAuthorization cisPaymentAuthorization = new CisPaymentAuthorization();
		cisPaymentAuthorization.setAmount(request.getTotalAmount());
		cisPaymentAuthorization.setClientAuthorizationId(request.getMerchantTransactionCode());
		cisPaymentAuthorization.setCurrency(request.getCurrency().getCurrencyCode());
		return cisPaymentAuthorization;
	}
}
