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
package de.hybris.platform.selectivecartsplitlistaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCartPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/cart")
public class SelectiveCartSplitListController extends AbstractCartPageController
{

	private static final String REDIRECT_CART_URL = REDIRECT_PREFIX + "/cart";
	private static final String SAVEFORLATER_ENTRY_MOVETOCART_ERROR = "saveforlater.entry.error.movetocart";
	private static final String SAVEFORLATER_ENTRY_MOVETOCART_INFO = "saveforlater.entry.message.movetocart";
	private static final String SAVEFORLATER_ENTRY_REMOVE_INFO = "saveforlater.entry.info.remove";


	@Resource
	private SelectiveCartFacade selectiveCartFacade;
	@Resource
	private CartFacade cartFacade;


	@RequireHardLogIn
	@RequestMapping(value = "/entries/check", method = RequestMethod.POST)
	public String checkCartEntries(@RequestParam("productCode") final String productCode, final RedirectAttributes redirectModel)
	{
		try
		{
			getSelectiveCartFacade().addToCartFromWishlist(productCode);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, SAVEFORLATER_ENTRY_MOVETOCART_INFO,
					null);
		}
		catch (final CommerceCartModificationException e)//NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, SAVEFORLATER_ENTRY_MOVETOCART_ERROR,
					null);
		}
		return REDIRECT_CART_URL;
	}

	@RequireHardLogIn
	@RequestMapping(value = "/entries/remove", method = RequestMethod.POST)
	public String removeWishListEntries(@RequestParam("productCode") final String productCode,
			final RedirectAttributes redirectModel)
	{
		getSelectiveCartFacade().removeWishlistEntryForProduct(productCode);
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, SAVEFORLATER_ENTRY_REMOVE_INFO, null);
		return REDIRECT_CART_URL;
	}

	@ResponseBody
	@RequestMapping(value = "/entries", method = RequestMethod.GET)
	public boolean hasWishListDataOnly()
	{
		final Wishlist2Data wishList = getSelectiveCartFacade().getWishlistForSelectiveCart();
		final boolean hasWishlist = wishList == null ? false : !wishList.getEntries().isEmpty();
		final List<EntryGroupData> rootGroups = getCartFacade().getSessionCart().getRootGroups();
		final boolean hasCartData = rootGroups == null ? false : !rootGroups.isEmpty();
		return !hasCartData && hasWishlist;
	}


	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

	@Override
	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

}
