/**
 *
 */
package de.hybris.platform.gigya.gigyaloginaddon.controllers;

public interface ControllerConstants
{
	final String ADDON_PREFIX = "addon:/gigyaloginaddon/";

	interface Views
	{
		interface Pages
		{
			interface Account
			{
				String AccountLoginPage = ADDON_PREFIX + "pages/account/accountLoginPage";// NOSONAR
			}

			interface Checkout
			{
				String CheckoutLoginPage = ADDON_PREFIX + "pages/checkout/checkoutLoginPage";// NOSONAR
			}
		}

		interface Fragments
		{
			interface Checkout // NOSONAR
			{
				String TermsAndConditionsPopup = "fragments/checkout/termsAndConditionsPopup"; // NOSONAR
			}
		}
	}
}
