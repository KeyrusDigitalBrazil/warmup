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
package de.hybris.platform.selectivecartaddon.controllers;

import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.selectivecartaddon.model.components.SelectiveCartCMSComponentModel;



/**
 */
public interface SelectivecartaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/selectivecartaddon";

	/**
	 * Class with action name constants
	 */
	interface Actions
	{
		interface Cms // NOSONAR
		{
			String _Prefix = "/view/"; // NOSONAR
			String _Suffix = "Controller"; // NOSONAR

			/**
			 * Default CMS component controller
			 */
			String SelectiveCartCMSComponent = _Prefix + SelectiveCartCMSComponentModel._TYPECODE + _Suffix; // NOSONAR
			String MiniCartComponent = _Prefix + MiniCartComponentModel._TYPECODE + _Suffix; // NOSONAR
		}
	}

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Cms // NOSONAR
		{
			String ComponentPrefix = "cms/"; // NOSONAR
		}

		interface Fragments
		{
			interface Cart // NOSONAR
			{
				String MiniCartPanel = "fragments/cart/miniCartPanel"; // NOSONAR
				String CartPopup = "fragments/cart/cartPopup"; // NOSONAR
			}
		}
	}
}
