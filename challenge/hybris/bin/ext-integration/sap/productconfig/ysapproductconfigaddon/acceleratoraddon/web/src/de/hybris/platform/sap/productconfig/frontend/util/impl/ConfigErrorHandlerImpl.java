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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Default implementation of the {@link ConfigErrorHandler}
 */
public class ConfigErrorHandlerImpl implements ConfigErrorHandler
{
	private static final String KB_DOES_NOT_EXIST = "Knowledgebase does not exist for configuration of entry %s (%s: %s) Redirect to %s page.";

	private static final String ERROR_FORWARD_URL = AbstractController.REDIRECT_PREFIX + AbstractController.ROOT;

	private static final String KB_ERROR_ORDER_HISTORY_URL = AbstractController.REDIRECT_PREFIX
			+ SapproductconfigfrontendWebConstants.ORDER_DETAILS;

	private static final String KB_ERROR_QUOTES_URL = AbstractController.REDIRECT_PREFIX
			+ SapproductconfigfrontendWebConstants.QUOTES;

	private static final String KB_ERROR_SAVED_CARTS_URL = AbstractController.REDIRECT_PREFIX
			+ SapproductconfigfrontendWebConstants.SAVED_CARTS;

	private static final Logger LOGGER = Logger.getLogger(ConfigErrorHandlerImpl.class.getName());

	@Override
	public ModelAndView handleErrorForAjaxRequest(final HttpServletRequest request, final Model model)
	{
		return forwardToErrorPageAndRedirectToHomePage(model, request);
	}

	@Override
	public String handleError()
	{
		LOGGER.warn("Configuration session has been lost - redirect to : '" + ERROR_FORWARD_URL + "'");
		return ERROR_FORWARD_URL;
	}

	protected ModelAndView forwardToErrorPageAndRedirectToHomePage(final Model model, final HttpServletRequest request)
	{
		return forwardToErrorPageAndRedirect(model, request.getContextPath());
	}

	protected ModelAndView forwardToErrorPageAndRedirect(final Model model, final String redirectUrl)
	{
		model.addAttribute("redirectUrl", redirectUrl);
		LOGGER.warn("Configuration session has been lost - jump to page: '"
				+ SapproductconfigfrontendWebConstants.ERROR_FORWARD_FOR_AJAX_VIEW_NAME + "', which redirects to '" + redirectUrl
				+ "'");
		return new ModelAndView(SapproductconfigfrontendWebConstants.ERROR_FORWARD_FOR_AJAX_VIEW_NAME);
	}

	@Override
	public String handleErrorFromOrderHistory(final String orderCode, final int entryNumber,
			final RedirectAttributes redirectModel)
	{
		LOGGER.warn(String.format(KB_DOES_NOT_EXIST, Integer.valueOf(entryNumber), "order", orderCode, "order details"));
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
				"sapproductconfig.text.account.order.kberror");
		return KB_ERROR_ORDER_HISTORY_URL + orderCode;
	}

	@Override
	public String handleErrorFromQuotes(final String quotationId, final int entryNumber, final RedirectAttributes redirectModel)
	{
		LOGGER.warn(String.format(KB_DOES_NOT_EXIST, Integer.valueOf(entryNumber), "quote", quotationId, "quote details"));
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
				"sapproductconfig.text.account.quotes.kberror");
		return KB_ERROR_QUOTES_URL + quotationId;
	}

	@Override
	public String handleErrorFromSavedCarts(final String cartCode, final int entryNumber, final RedirectAttributes redirectModel)
	{
		LOGGER.warn(String.format(KB_DOES_NOT_EXIST, Integer.valueOf(entryNumber), "saved cart", cartCode, "saved cart details"));
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
				"sapproductconfig.text.account.savedCarts.kberror");
		return KB_ERROR_SAVED_CARTS_URL + cartCode;
	}

	@Override
	public String handleConfigurationAccessError()
	{
		LOGGER.warn("Access to configuration session denied - redirect to : '" + ERROR_FORWARD_URL + "'");
		return ERROR_FORWARD_URL;
	}

}
