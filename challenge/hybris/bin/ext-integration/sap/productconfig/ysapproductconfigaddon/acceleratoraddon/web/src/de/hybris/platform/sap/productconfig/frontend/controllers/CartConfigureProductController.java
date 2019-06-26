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

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Cart-Configuration integration
 */
@Controller
@RequestMapping()
public class CartConfigureProductController extends AbstractProductConfigController
{
	private static final Logger LOGGER = Logger.getLogger(CartConfigureProductController.class);



	/**
	 * Prepares a configuration session for the given cart item. This includes re-creation of the configuration sessions,
	 * in case it was already releases, as well as restoring of the UI-State.
	 *
	 * @param entryNumber
	 *           of the configurable cart item
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "cart/{entryNumber}/configuration/" + SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE)
	public String configureCartEntry(@PathVariable("entryNumber") final int entryNumber, final Model model,
			final HttpServletRequest request) throws CommerceCartModificationException
	{
		// 1. get Cart
		final CartData sessionCart = getCartFacade().getSessionCart();
		if (CollectionUtils.isEmpty(sessionCart.getEntries()))
		{
			// user clicked logout ==> cart empty
			return REDIRECT_PREFIX + ROOT;
		}

		// 2. get Cart Item
		final OrderEntryData currentEntry = getCartEntry(entryNumber, sessionCart);

		// 3. get Configuration
		final ConfigurationData configData = getConfigCartFacade().configureCartItem(currentEntry.getItemPK());
		if (null == configData)
		{
			return REDIRECT_PREFIX + ROOT;
		}

		// 4. Populate Ui models
		return populateModelAndGetNextPage(model, request, currentEntry, configData);
	}

	protected String populateModelAndGetNextPage(final Model model, final HttpServletRequest request,
			final OrderEntryData currentEntry, final ConfigurationData configData) throws CommerceCartModificationException
	{
		try
		{
			logLoadConfig(configData, "Load from CartItem  ");
			populateConfigurationModel(request, model, currentEntry, configData);
		}
		catch (final CMSItemNotFoundException cnfe)
		{
			throw new CommerceCartModificationException("Root cause: CMSItemNotFoundException", cnfe);
		}

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Retrieve content for cartEntry via GET ('" + currentEntry.getItemPK() + "')");
			LOGGER.debug("Current Session: '" + getSessionService().getCurrentSession().getSessionId() + "'");
		}

		return SapproductconfigfrontendWebConstants.CONFIG_PAGE_VIEW_NAME;
	}

	protected OrderEntryData getCartEntry(final int entryNumber, final CartData sessionCart)
			throws CommerceCartModificationException
	{
		final OrderEntryData currentEntry;
		try
		{
			currentEntry = getOrderEntry(entryNumber, sessionCart);
		}
		catch (final BusinessException bex)
		{
			throw new CommerceCartModificationException("Could not find cart entry!", bex);
		}
		return currentEntry;
	}

	protected void populateConfigurationModel(final HttpServletRequest request, final Model model,
			final OrderEntryData currentEntry, final ConfigurationData configData) throws CMSItemNotFoundException
	{
		final String productCode = configData.getKbKey().getProductCode();
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getBreadcrumbBuilder().getBreadcrumbsForConfigFromCart(productCode, currentEntry.getEntryNumber()));
		populateCMSAttributes(model);
		model.addAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, configData);
		populateProductData(productCode, model, request);

		// assume currentEntry != null
		configData.setLinkedToCartItem(true);

		configData.setQuantity(currentEntry.getQuantity().longValue());

		final UiStatus uiStatus = handleUIStatus(configData);

		final BindingResult errors = getBindingResultForConfiguration(configData, uiStatus);
		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, errors);

		determineGroupToDisplay(configData, uiStatus);

		getUiRecorder().recordUiAccessFromCart(configData, productCode);

		getUiStateHandler().handleConflictSolverMessage(uiStatus, getUiStatusSync().getNumberOfConflicts(configData), model);
		getUiStateHandler().handleProductConfigMessages(configData, model);
	}

	protected void determineGroupToDisplay(final ConfigurationData configData, final UiStatus uiStatus)
	{
		configData.setAutoExpand(true);
		final UiGroupData expandedGroup = getUiStateHandler().handleAutoExpand(configData, uiStatus);
		if (expandedGroup != null)
		{
			uiStatus.setGroupIdToDisplay(expandedGroup.getId());
		}
		getUiStateHandler().compileGroupForDisplay(configData, uiStatus);
		final UiStatus updatedUiStatus = getUiStatusSync().extractUiStatusFromConfiguration(configData);
		setUiStatusForConfig(configData, updatedUiStatus);
	}

	protected UiStatus handleUIStatus(final ConfigurationData configData)
	{
		UiStatus uiStatus = getSessionAccessFacade().getUiStatusForCartEntry(getCartItemByConfigId(configData.getConfigId()));
		if (null == uiStatus)
		{
			getUiStatusSync().setInitialStatus(configData);
			uiStatus = getUiStatusSync().extractUiStatusFromConfiguration(configData);
		}
		else
		{
			getUiStatusSync().applyUiStatusToConfiguration(configData, uiStatus);
		}

		return uiStatus;
	}

	/**
	 * Prepares a configuration session for the given cart item in case a configuration draft already exists. Example:
	 * User triggered configuration from cart, navigated to vaariant display and afterwards returns to the configuration
	 * page
	 *
	 * @param entryNumber
	 *           Entry number of the configurable cart item
	 * @param model
	 *           View model
	 * @param request
	 *           Http request
	 * @return view name
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "cart/{entryNumber}/configureOnDraft/" + SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE)
	public String configureCartEntryOnExistingDraft(@PathVariable("entryNumber") final int entryNumber, final Model model,
			final HttpServletRequest request) throws CommerceCartModificationException
	{

		// 1. get Cart
		final CartData sessionCart = getCartFacade().getSessionCart();
		if (CollectionUtils.isEmpty(sessionCart.getEntries()))
		{
			// user clicked logout ==> cart empty
			return REDIRECT_PREFIX + ROOT;
		}

		// 2. get Cart Item
		final OrderEntryData currentEntry = getCartEntry(entryNumber, sessionCart);

		// 3. get Configuration
		final ConfigurationData configData = getConfigCartFacade().configureCartItemOnExistingDraft(currentEntry.getItemPK());
		if (null == configData)
		{
			return REDIRECT_PREFIX + ROOT;
		}

		// 4. Populate Ui models
		return populateModelAndGetNextPage(model, request, currentEntry, configData);
	}


}
