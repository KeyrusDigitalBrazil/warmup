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
package de.hybris.platform.sap.productconfig.b2bfrontend.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.sap.productconfig.b2bfrontend.constants.Sapproductconfigb2baddonWebConstants;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationOrderIntegrationFacadeImpl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



/**
 * CPQ Controller for Re-Order functionality. In contrast to the hybris re-order controller 'ReorderCheckoutController'
 * this controller implements a special handling for configurable products to handle the case when the knowledge
 * base version of the configuration attached to the order item to be re-ordered isn't known anymore by the
 * configuration engine.
 */
@Controller
@RequestMapping(value = "/my-account/order")
public class CPQReorderController extends AbstractController
{
	static final String MSG_CODE_LESS_ITEMS = "basket.page.message.update.reducedNumberOfItemsAdded.noStock";
	static final String MSG_CODE_QUANTITY_ADJUSTED = "basket.information.quantity.adjusted";
	static final String MSG_CODE_REORDER_NOT_POSSIBLE = "sapproductconfigb2baddon.reorder.not.possible";

	@Resource(name = "b2bCheckoutFacade")
	private CheckoutFacade b2bCheckoutFacade;

	@Resource(name = "sapProductConfigOrderIntegrationFacade")
	private ConfigurationOrderIntegrationFacade cpqOrderIntegrationFacade;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	/**
	 * Re-Order handler.
	 *
	 * @param orderCode
	 * @param redirectModel
	 * @return target view
	 *
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/cpqreorder", method =
	{ RequestMethod.PUT, RequestMethod.POST })
	@RequireHardLogIn
	public String reorder(@RequestParam(value = "orderCode") final String orderCode, final RedirectAttributes redirectModel)
			throws CommerceCartModificationException
	{
		final boolean isReorderPossible = getCpqOrderIntegrationFacade().isReorderable(orderCode);
		final String view = getRedirectTarget(isReorderPossible);

		getB2bCheckoutFacade().createCartFromOrder(orderCode);

		final List<CartModificationData> cartModifications = getCartFacade().validateCartData();
		for (final CartModificationData cartModification : cartModifications)
		{
			handleCoreStatus(redirectModel, cartModification);
			handleCPQStatus(redirectModel, cartModification);
		}

		return view;
	}

	protected String getRedirectTarget(final boolean isReorderPossible)
	{
		String view;
		if (isReorderPossible)
		{
			view = Sapproductconfigb2baddonWebConstants.REDIRECT_TO_CHECKOUT;
		}
		else
		{
			view = Sapproductconfigb2baddonWebConstants.REDIRECT_TO_CART;
		}
		return view;
	}


	protected void handleCoreStatus(final RedirectAttributes redirectModel, final CartModificationData cartModification)
	{
		if (CommerceCartModificationStatus.NO_STOCK.equals(cartModification.getStatusCode()))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, MSG_CODE_LESS_ITEMS, new Object[]
			{ XSSFilterUtil.filter(cartModification.getEntry().getProduct().getName()) });
		}
		else if (cartModification.getQuantity() != cartModification.getQuantityAdded())
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, MSG_CODE_QUANTITY_ADJUSTED);
		}
	}



	protected void handleCPQStatus(final RedirectAttributes redirectModel, final CartModificationData cartModification)
	{
		if (ConfigurationOrderIntegrationFacadeImpl.KB_NOT_VALID.equals(cartModification.getStatusCode()))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, MSG_CODE_REORDER_NOT_POSSIBLE,
					new Object[]
					{ XSSFilterUtil.filter(cartModification.getEntry().getProduct().getName()) });
		}
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}


	/**
	 * @param cartFacade
	 */
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected CheckoutFacade getB2bCheckoutFacade()
	{
		return b2bCheckoutFacade;
	}

	/**
	 * @param b2bCheckoutFacade
	 */
	public void setB2bCheckoutFacade(final CheckoutFacade b2bCheckoutFacade)
	{
		this.b2bCheckoutFacade = b2bCheckoutFacade;
	}


	protected ConfigurationOrderIntegrationFacade getCpqOrderIntegrationFacade()
	{
		return cpqOrderIntegrationFacade;
	}

	/**
	 * @param cpqOrderIntegrationFacade
	 */
	public void setCpqOrderIntegrationFacade(final ConfigurationOrderIntegrationFacade cpqOrderIntegrationFacade)
	{
		this.cpqOrderIntegrationFacade = cpqOrderIntegrationFacade;
	}
}
