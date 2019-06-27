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
package de.hybris.platform.customercouponaddon.controllers;


/**
 */
public interface CustomercouponaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/customercouponaddon";

	interface Views
	{
		interface Pages
		{
			interface COUPONS // NOSONAR
			{
				String ConponsPage = "/my-account/coupons"; // NOSONAR
			}

		}

		interface Fragments
		{
			interface Coupons
			{
				String CustomerCouponSubPage = ADDON_PREFIX + "/fragments/customer360/customerCouponSubPage"; // NOSONAR
			}
		}

	}

	/**
	 * Class with action name constants
	 */
	interface Actions
	{
		interface Cms // NOSONAR
		{
			String _Prefix = "/view/"; // NOSONAR
			String _Suffix = "Controller"; // NOSONAR
		}
	}

}
