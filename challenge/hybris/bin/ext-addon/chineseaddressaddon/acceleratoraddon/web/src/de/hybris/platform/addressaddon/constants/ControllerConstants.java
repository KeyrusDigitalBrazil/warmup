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
package de.hybris.platform.addressaddon.constants;



public interface ControllerConstants
{

	interface Views
	{
		String _AddonPrefix = "addon:/chineseaddressaddon/";


		interface Fragments
		{
			interface Account
			{
				String CountryAddressForm = _AddonPrefix + "fragments/address/countryAddressForm";
				String ChineseAddress = _AddonPrefix + "fragments/address/chineseAddress";
				String ChineseAddressInShippingItem = _AddonPrefix + "fragments/address/chineseAddressInShippingItem";
			}
		}


		interface Pages
		{

			interface Checkout
			{
				String CheckoutConfirmationPage = _AddonPrefix + "pages/checkout/checkoutConfirmationPage";
			}

			interface MultiStepCheckout
			{
				String AddEditDeliveryAddressPage = _AddonPrefix + "pages/checkout/multi/addEditDeliveryAddressPage";
			}
		}
	}
}
