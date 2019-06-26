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
/**
 * A hook strategy to run custom code before adding product to cart
 */
package de.hybris.platform.notificationservices.service.hooks;

import de.hybris.platform.core.model.user.CustomerModel;

/**
 * Hook for Customer setting changing
 */
public interface CustomerSettingsChangedHook
{
	/**
	 * Specific customized logic after unbinding mobile number
	 *
	 * @param parameters
	 *           A customer who wants to unbind mobile number.
	 *
	 */
	void afterUnbindMobileNumber(final CustomerModel customer);

}
