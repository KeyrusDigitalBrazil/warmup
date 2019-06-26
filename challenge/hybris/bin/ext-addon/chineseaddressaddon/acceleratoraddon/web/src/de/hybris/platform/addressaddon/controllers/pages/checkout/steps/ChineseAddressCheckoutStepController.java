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
package de.hybris.platform.addressaddon.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.addressaddon.constants.ControllerConstants;
import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Scope("tenant")
@RequestMapping(value = "/checkout/multi")
public class ChineseAddressCheckoutStepController extends AbstractCheckoutController
{
	@Resource(name = "chineseAddressFacade")
	private AddressFacade chineseAddressFacade;

	@RequestMapping(value = "/chineseAddressForm", method = RequestMethod.GET)
	public String getOrderSummaryAddress(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		return ControllerConstants.Views.Fragments.Account.ChineseAddress;
	}


	@RequestMapping(value = "/chineseAddressInShippingItems", method = RequestMethod.GET)
	public String getShippingItemAddress(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		return ControllerConstants.Views.Fragments.Account.ChineseAddressInShippingItem;
	}

}
