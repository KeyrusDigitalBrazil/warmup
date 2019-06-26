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
package de.hybris.platform.commerceservices.strategies;

import de.hybris.platform.core.model.user.CustomerModel;

public interface CheckoutCustomerStrategy
{
	/**
	 * Checks if the checkout is a anonymous Checkout
	 *
	 * @return  boolean
	 */
	boolean isAnonymousCheckout();

	/**
	 * Returns {@link CustomerModel} for the current checkout.
	 *
	 * @return
	 */
	CustomerModel getCurrentUserForCheckout();
}
