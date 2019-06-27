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
package de.hybris.platform.commerceservices.order;

/**
 *  A strategy for getting a payment provider
 *
 */
public interface CommercePaymentProviderStrategy
{
	/**
	 * Get payment provider assigned to the {@link de.hybris.platform.store.BaseStoreModel}
	 * @return A payment provider name.
	 */
	String getPaymentProvider();
}
