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
package de.hybris.platform.chineseprofile.controllers;

/**
 */
public interface ControllerConstants
{
	String ADDON_PREFIX = "addon:/chineseprofileaddon/";

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Fragments
		{
			interface Account
			{
				String CountryAddressForm = ADDON_PREFIX + "fragments/address/countryAddressForm";
			}

			interface Checkout
			{
				String TermsAndConditionsPopup = "fragments/checkout/termsAndConditionsPopup";
			}

		}

		interface Pages
		{
			interface Account
			{
				String AccountLoginPage = "pages/account/accountLoginPage";

				String ChineseMobileProfileBindingPage = ADDON_PREFIX + "pages/account/chineseMobileProfileBindingPage";

				String ChineseMobileRegisterBindingPage = ADDON_PREFIX + "pages/account/chineseMobileRegisterBindingPage";

				String ChineseMobileUnbindingPage = ADDON_PREFIX + "pages/account/chineseMobileUnbindingPage";

				String VerificationCodeMockPage = ADDON_PREFIX + "pages/mock/showVerificationCodePage";
			}
		}
	}
}
