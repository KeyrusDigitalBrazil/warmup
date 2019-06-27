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
package de.hybris.platform.acceleratorservices.payment.strategies;

import de.hybris.platform.acceleratorservices.payment.data.CustomerInfoData;
import de.hybris.platform.acceleratorservices.payment.data.PaymentInfoData;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;


/**
 * A strategy for creating and persisting {@link CreditCardPaymentInfoModel}
 * 
 */
public interface CreditCardPaymentInfoCreateStrategy
{

	CreditCardPaymentInfoModel createCreditCardPaymentInfo(final SubscriptionInfoData subscriptionInfo,
			final PaymentInfoData paymentInfo, final AddressModel billingAddress, final CustomerModel customerModel,
			final boolean saveInAccount);

	CreditCardPaymentInfoModel saveSubscription(CustomerModel customerModel, CustomerInfoData customerInfoData,
			SubscriptionInfoData subscriptionInfo, PaymentInfoData paymentInfoData, boolean saveInAccount);
}
