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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * CPQ Controller for actions that interact with the Cart, such as add to cart or update cart action.
 */
@Controller
public class AddConfigToCartController extends AbstractProductConfigController
{
	private static final Logger LOGGER = Logger.getLogger(AddConfigToCartController.class.getName());
	private static final String LOG_URL = "Redirect to: '";

	/**
	 * Updates a configuration within the cart. In case the configuration contains any validation errors, the update cart
	 * action is canceled, and the user remains on the configuration page, so he can fix the validation errors.
	 *
	 * @param configData
	 *           runtime configuration
	 * @param bindingErrors
	 *           error store
	 * @param model
	 *           view model
	 * @param redirectModel
	 *           redirect attributes
	 * @param request
	 *           http servlet request
	 * @return redirect URL, either cart if all was OK or configuration page in case validation errors
	 */
	@RequestMapping(value = "cart/{entryNumber}/configur*/addToCart", method = RequestMethod.POST)
	public String updateConfigInCart(
			@ModelAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE) @Valid final ConfigurationData configData,
			final BindingResult bindingErrors, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request)
	{
		return addConfigToCart(configData.getKbKey().getProductCode(), configData, bindingErrors, model, redirectModel, request);
	}

	/**
	 * Adds a configuration to the cart, so that a new cart item will be created. In case the configuration contains any
	 * validation errors, the add to cart action is canceled, and the user remains on the configuration page, so he can
	 * fix the validation errors.
	 *
	 * @param productCodeEncoded
	 *           code of the product the configuration belongs to
	 * @param configData
	 *           runtime configuration
	 * @param bindingResult
	 *           error store
	 * @param model
	 *           view model
	 * @param redirectAttributes
	 *           redirect attributes
	 * @param request
	 *           http servlet request
	 * @return redirect URL, either cart if all was OK or configuration page in case validation errors
	 */
	@RequestMapping(value = "/**/{productCode:.*}/" + "configur*" + "/addToCart", method = RequestMethod.POST)
	public String addConfigToCart(@PathVariable("productCode") final String productCodeEncoded,
			@ModelAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE) @Valid final ConfigurationData configData,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectAttributes,
			final HttpServletRequest request)
	{
		final String productCode = decodeWithScheme(productCodeEncoded, UTF_8);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("AddConfigToCart POST for '" + productCode + "'");
		}
		if (!isConfigLinkedToCart(configData.getConfigId()) && isConfigRemoved(productCode))
		{
			return getConfigurationErrorHandler().handleError();
		}
		removeNullCstics(configData.getGroups());
		getConfigFacade().updateConfiguration(configData);

		final boolean validationErrors = bindingResult.hasErrors();
		if (validationErrors)
		{
			return REDIRECT_PREFIX + ROOT + productCode + SapproductconfigfrontendWebConstants.CONFIG_URL;
		}

		final ConfigurationData latestConfiguration = getConfigFacade().getConfiguration(configData);
		logModelmetaData(latestConfiguration);
		final String configId = latestConfiguration.getConfigId();
		final boolean isNewCartItem = !isConfigLinkedToCart(configId);

		final String cartItemKey;
		try
		{
			final UiStatus uiStatus = getUiStatusForConfig(latestConfiguration);

			cartItemKey = getConfigCartFacade().addConfigurationToCart(latestConfiguration);
			getSessionAccessFacade().setUiStatusForCartEntry(cartItemKey, uiStatus);

			redirectAttributes.addFlashAttribute("addedToCart", Boolean.valueOf(isNewCartItem));
		}
		catch (final CommerceCartModificationException ex)
		{
			// In our case log level error is fine, as we don't foresee exceptions in our
			// standard process (in case no stock available, one would not be allowed to configure
			// at all).
			// Extensions and inproper setup can cause these extensions, in this case error handling
			// needs to be reconsidered here
			GlobalMessages.addErrorMessage(model, "sapproductconfig.addtocart.product.error");
			LOGGER.error("Add-To-Cart failed", ex);
		}

		final String redirectURL = REDIRECT_PREFIX + ROOT + configId + SapproductconfigfrontendWebConstants.CONFIG_OVERVIEW_URL;

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(LOG_URL + redirectURL + "'");
		}

		return redirectURL;
	}

	/**
	 * Resets the existing configuration to its's default values.
	 *
	 * @param productCodeEncoded
	 *           code of the product the configuration belongs to
	 * @param url
	 *           URL to call
	 * @return redirect URL
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/**/{productCode:.*}/reset")
	public String resetConfiguration(@PathVariable("productCode") final String productCodeEncoded, final String url)
	{
		final String productCode = decodeWithScheme(productCodeEncoded, UTF_8);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Reset POST for '" + productCode + "'");
		}

		getSessionAccessFacade().removeUiStatusForProduct(productCode);
		getConfigCartFacade().removeConfigurationLink(productCode);

		//We keep the SSC session belonging to the configuration for later
		//configure from cart
		final String redirectUrl = REDIRECT_PREFIX + url;

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(LOG_URL + redirectUrl + "'");
		}
		return redirectUrl;
	}
}
