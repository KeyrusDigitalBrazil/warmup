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
package de.hybris.platform.timedaccesspromotionengineaddon.controllers;

import de.hybris.platform.acceleratorcms.model.actions.AddToCartActionModel;


/**
 * Timedaccesspromotionengineaddon constants
 */
public interface TimedaccesspromotionengineaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/timedaccesspromotionengineaddon";

	interface Actions
	{
		interface Cms // NOSONAR
		{
			String _Prefix = "/view/"; // NOSONAR
			String _Suffix = "Controller"; // NOSONAR

			/**
			 * Customized AddToCartAction controller
			 */
			String AddToCartAction = _Prefix + AddToCartActionModel._TYPECODE + _Suffix; // NOSONAR

		}
	}
}
