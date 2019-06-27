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

import de.hybris.platform.acceleratorservices.payment.data.OrderInfoData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

/**
 *
 *
 */
public interface PaymentTransactionStrategy
{
	PaymentTransactionEntryModel savePaymentTransactionEntry(CustomerModel customerModel, String requestId,
	                                                         OrderInfoData orderInfoData);

	void setPaymentTransactionReviewResult(PaymentTransactionEntryModel reviewDecisionEntry, String guid);
}
