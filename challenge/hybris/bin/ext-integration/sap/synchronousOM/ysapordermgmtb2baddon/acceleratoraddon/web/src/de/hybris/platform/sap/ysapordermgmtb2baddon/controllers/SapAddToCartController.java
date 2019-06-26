/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.ysapordermgmtb2baddon.controllers;

import de.hybris.platform.acceleratorfacades.product.data.ProductWrapperData;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddToCartOrderForm;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl.DefaultSapCartFacade;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 *
 */

/**
 * Controller for Add to Cart functionality for Quick order.
 */
@Controller
public class SapAddToCartController extends AddToCartController
{

	@Resource(name = "cartFacade")
	private DefaultSapCartFacade cartFacade;


	@Override
	@RequestMapping(value = "/cart/addQuickOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public final String addQuickOrderToCart(@RequestBody final AddToCartOrderForm form, final Model model)
	{
		final List<CartModificationData> modificationDataList = new ArrayList();
		final List<ProductWrapperData> productWrapperDataList = new ArrayList();
		final int maxQuickOrderEntries = Config.getInt("yb2bacceleratorstorefront.quick.order.rows.max", 25);
		final int sizeOfCartEntries = CollectionUtils.size(form.getCartEntries());
		form.getCartEntries().stream().limit(Math.min(sizeOfCartEntries, maxQuickOrderEntries)).forEach(cartEntry -> {
			String errorMsg = StringUtils.EMPTY;
			final String sku = !isValidProductEntry(cartEntry) ? StringUtils.EMPTY : cartEntry.getProduct().getCode();
			if (StringUtils.isEmpty(sku))
			{
				errorMsg = "text.quickOrder.product.code.invalid";
			}
			else if (!isValidQuantity(cartEntry))
			{
				errorMsg = "text.quickOrder.product.quantity.invalid";
			}

			if (StringUtils.isNotEmpty(errorMsg))
			{
				productWrapperDataList.add(createProductWrapperData(sku, errorMsg));
			}
		});

		if (CollectionUtils.isNotEmpty(productWrapperDataList))
		{
			model.addAttribute("quickOrderErrorData", productWrapperDataList);
			model.addAttribute("quickOrderErrorMsg", "basket.quick.order.error");
		}

		addItemsToCart(modificationDataList, form.getCartEntries(), false);
		model.addAttribute("quickOrderErrorMsg", "basket.quick.order.error");

		if (CollectionUtils.isNotEmpty(modificationDataList))
		{
			model.addAttribute("modifications", modificationDataList);
		}

		return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
	}

	protected String addItemsToCart(final List<CartModificationData> modificationDataList, final List<OrderEntryData> cartEntries,
			final boolean isReducedQtyError)
	{
		String errorMsg = StringUtils.EMPTY;
		try
		{
			modificationDataList.addAll(cartFacade.addItemsToCart(cartEntries));
		}
		catch (final CommerceCartModificationException ex)
		{
			errorMsg = "basket.error.occurred";
			logDebugException(ex);
		}
		return errorMsg;
	}

}
