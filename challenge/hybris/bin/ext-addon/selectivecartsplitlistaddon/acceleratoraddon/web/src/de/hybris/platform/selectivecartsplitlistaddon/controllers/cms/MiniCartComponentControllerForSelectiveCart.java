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
package de.hybris.platform.selectivecartsplitlistaddon.controllers.cms;

import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;
import de.hybris.platform.selectivecartfacades.data.Wishlist2EntryData;
import de.hybris.platform.selectivecartsplitlistaddon.controllers.SelectivecartsplitlistaddonControllerConstants;

import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Override the MiniCartComponentControllerForSelectiveCart to add wish list quantity into checked list
 * total items.
 */
@Controller
@RequestMapping(value = SelectivecartsplitlistaddonControllerConstants.Actions.Cms.MiniCartComponent)
public class MiniCartComponentControllerForSelectiveCart extends AbstractCMSComponentController<MiniCartComponentModel>
{
	public static final String TOTAL_PRICE = "totalPrice";
	public static final String TOTAL_ITEMS = "totalItems";
	public static final String TOTAL_DISPLAY = "totalDisplay";
	public static final String TOTAL_NO_DELIVERY = "totalNoDelivery";
	public static final String SUB_TOTAL = "subTotal";

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@Resource
	private SelectiveCartFacade selectiveCartFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final MiniCartComponentModel component)
	{
		final CartData cartData = cartFacade.getMiniCart();
		model.addAttribute(SUB_TOTAL, cartData.getSubTotal());
		if (cartData.getDeliveryCost() != null)
		{
			final PriceData withoutDelivery = cartData.getDeliveryCost();
			withoutDelivery.setValue(cartData.getTotalPrice().getValue().subtract(cartData.getDeliveryCost().getValue()));
			model.addAttribute(TOTAL_NO_DELIVERY, withoutDelivery);
		}
		else
		{
			model.addAttribute(TOTAL_NO_DELIVERY, cartData.getTotalPrice());
		}
		model.addAttribute(TOTAL_PRICE, cartData.getTotalPrice());
		model.addAttribute(TOTAL_DISPLAY, component.getTotalDisplay());
		model.addAttribute(TOTAL_ITEMS, Optional.ofNullable(cartData.getTotalUnitCount()).orElse(Integer.valueOf(0)));

		final Integer counts = calculateTotalNumber();
		model.addAttribute(TOTAL_ITEMS, counts);
	}
	
	@Override
	protected String getView(final MiniCartComponentModel component)
	{
		return SelectivecartsplitlistaddonControllerConstants.Views.Cms.ComponentPrefix
				+ StringUtils.lowerCase(getTypeCode(component));
	}

	protected Integer calculateTotalNumber()
	{
		final CartData cartData = getCartFacade().getSessionCart();

		final Wishlist2Data wishList = getSelectiveCartFacade().getWishlistForSelectiveCart();
		int counts = 0;
		if (wishList != null)
		{
			for (final Wishlist2EntryData entry : wishList.getEntries())
			{
				counts += entry.getQuantity().intValue();
			}
		}
		counts = counts + cartData.getTotalUnitCount().intValue();
		return Integer.valueOf(counts);
	}


	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

}