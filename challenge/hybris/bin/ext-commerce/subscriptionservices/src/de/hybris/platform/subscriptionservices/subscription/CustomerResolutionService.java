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
package de.hybris.platform.subscriptionservices.subscription;

import de.hybris.platform.core.model.user.CustomerModel;

import javax.annotation.Nullable;


/**
 * Service interface to resolve information about the current customer in different contexts (e.g. accelerator
 * storefronts, CS cockpit).
 */
public interface CustomerResolutionService
{
	/**
	 * Returns the current customer.
	 * 
	 * @return {@link CustomerModel} the current customer
	 */
	@Nullable
	CustomerModel getCurrentCustomer();

	/**
	 * Returns the ISO currency code.
	 * 
	 * @return {@link String} the ISO currency code
	 */
	@Nullable
	String getCurrencyIso();
}
