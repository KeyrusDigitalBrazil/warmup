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
package de.hybris.platform.chinesecommerceorgaddressaddon.controllers;

import de.hybris.platform.chinesecommerceorgaddressaddon.constants.ChinesecommerceorgaddressaddonConstants;


/**
 */
public interface ChinesecommerceorgaddressaddonControllerConstants
{
	// implement here controller constants used by this extension
	interface Views
	{
		String _AddonPrefix = "addon:/chinesecommerceorgaddressaddon/";


		interface Fragments
		{
			interface Account
			{
				String CountryAddressForm = _AddonPrefix + "fragments/address/countryAddressForm";
				String ChineseAddressForm = _AddonPrefix + "fragments/address/chineseAddressForm";
			}
		}

		interface Pages
		{
			interface Error
			{
				String ErrorNotFoundPage = "pages/error/errorNotFoundPage";
			}

			interface MyCompany
			{
				String ADD_ON_PREFIX = "addon:";
				String VIEW_PAGE_PREFIX = ADD_ON_PREFIX + "/" + ChinesecommerceorgaddressaddonConstants.EXTENSIONNAME + "/";
				String MyCompanyManageUnitAddAddressPage = VIEW_PAGE_PREFIX + "pages/company/myCompanyManageUnitAddAddressPage";
			}
		}
	}
}