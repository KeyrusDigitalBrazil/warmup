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
package de.hybris.platform.acceleratorservices.payment.dao;


import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

public interface CreditCardPaymentSubscriptionDao extends Dao
{
	CCPaySubValidationModel findSubscriptionValidationBySubscription(String subscriptionId);

	CreditCardPaymentInfoModel findCreditCartPaymentBySubscription(String subscriptionId);
}
