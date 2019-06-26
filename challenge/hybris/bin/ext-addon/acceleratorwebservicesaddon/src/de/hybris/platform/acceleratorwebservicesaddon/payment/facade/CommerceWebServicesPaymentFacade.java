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
package de.hybris.platform.acceleratorwebservicesaddon.payment.facade;

import de.hybris.platform.acceleratorfacades.payment.PaymentFacade;
import de.hybris.platform.acceleratorfacades.payment.data.PaymentSubscriptionResultData;


/**
 * Interface which extends standard HOP/SOP facade with method for payment subscription result management.
 */
public interface CommerceWebServicesPaymentFacade extends PaymentFacade
{
	/**
	 * Save payment subscription result for cart with given id
	 *
	 * @param paymentSubscriptionResultData
	 *           Object to be saved
	 * @param cartId
	 *           Cart identifier
	 */
	void savePaymentSubscriptionResult(final PaymentSubscriptionResultData paymentSubscriptionResultData, final String cartId);

	/**
	 * Get payment subscription result related to cart with given id
	 *
	 * @param cartId
	 *           Cart identifier
	 * @return payment subscription result data
	 */
	PaymentSubscriptionResultData getPaymentSubscriptionResult(final String cartId);

	/**
	 * Delete payment subscription result related to cart with given id
	 *
	 * @param cartId
	 *           Cart identifier
	 */
	void removePaymentSubscriptionResult(final String cartId);

}
