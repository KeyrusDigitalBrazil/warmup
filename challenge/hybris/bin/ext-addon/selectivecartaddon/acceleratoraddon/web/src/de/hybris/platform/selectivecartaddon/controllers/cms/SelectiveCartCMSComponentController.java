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
package de.hybris.platform.selectivecartaddon.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.selectivecartaddon.controllers.SelectivecartaddonControllerConstants;
import de.hybris.platform.selectivecartaddon.model.components.SelectiveCartCMSComponentModel;
import de.hybris.platform.selectivecartfacades.SelectiveCartFacade;
import de.hybris.platform.selectivecartfacades.strategies.CartEntriesOrderingStrategy;
import de.hybris.platform.selectivecartfacades.strategies.SelectiveCartUpdateStrategy;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for CMS SelectiveCartCMSComponent.
 */
@Controller("SelectiveCartCMSComponentController")
@RequestMapping(SelectivecartaddonControllerConstants.Actions.Cms.SelectiveCartCMSComponent)
public class SelectiveCartCMSComponentController extends AbstractCMSAddOnComponentController<SelectiveCartCMSComponentModel>
{

	@Resource(name = "selectiveCartFacade")
	private SelectiveCartFacade selectiveCartFacade;

	@Resource(name = "cartEntriesOrderingStrategy")
	private CartEntriesOrderingStrategy cartEntriesOrderingStrategy;

	@Resource
	private CartFacade cartFacade;

	@Resource(name = "selectiveCartUpdateStrategy")
	private SelectiveCartUpdateStrategy selectiveCartUpdateStrategy;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final SelectiveCartCMSComponentModel component)
	{
		selectiveCartUpdateStrategy.update();
		final CartData cartData = cartFacade.getSessionCart();
		final List<OrderEntryData> wishlistOrders = getSelectiveCartFacade().getWishlistOrdersForSelectiveCart();
		final List<OrderEntryData> entries = cartData.getEntries();
		if (CollectionUtils.isNotEmpty(entries))
		{
			if (CollectionUtils.isNotEmpty(wishlistOrders))
			{
				entries.addAll(wishlistOrders);
				model.addAttribute("wishlistOrders", wishlistOrders);
			}
			model.addAttribute("cartData", cartEntriesOrderingStrategy.ordering(cartData));
		}
		else
		{
			if (CollectionUtils.isNotEmpty(wishlistOrders))
			{
				Collections.sort(wishlistOrders, (e1, e2) -> e2.getAddToCartTime().compareTo(e1.getAddToCartTime()));
				model.addAttribute("wishlistOrders", wishlistOrders);
			}
		}
	}

	protected SelectiveCartFacade getSelectiveCartFacade()
	{
		return selectiveCartFacade;
	}

}
