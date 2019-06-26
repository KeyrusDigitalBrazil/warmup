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
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;


/**
 * Controller implementation to be used, when a <b>variant</b> shall be displayed on the configuration overview page.
 *
 * @see ConfigurationOverviewController
 */
@Controller
@RequestMapping()
public class VariantOverviewController extends AbstractConfigurationOverviewController
{
	private static final String CLOSING_QUOTE = "'";
	private static final String CONFIG_GET_RECEIVED_FOR = "Config GET received for '";
	private static final String CONFIG_POST_RECEIVED_FOR = "Config POST received for '";
	private static final String CURRENT_SESSION = "' - Current Session: '";

	private static final String AJAX_VIEW_NAME = SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME
			+ SapproductconfigfrontendWebConstants.AJAX_SUFFIX;

	private static final Logger LOGGER = Logger.getLogger(VariantOverviewController.class.getName());


	/**
	 * Renders the product configuration overview page, using a product configuration variant as input.
	 *
	 * @param productCodeEncoded
	 *           product code of the variant
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/{productCode:.*}"
			+ SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_URL, method = RequestMethod.GET)
	public String getVariantOverview(@PathVariable("productCode") final String productCodeEncoded, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException
	{
		final ConfigurationOverviewData configOverviewData = prepareOverviewData(productCodeEncoded);

		final OverviewUiData overviewUiData = initializeOverviewUiDataForVariant();
		prepareUiModel(request, model, overviewUiData, configOverviewData);

		return SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME;
	}

	/**
	 * Renders the product configuration overview page, using an entry number and a product configuration variant as input.
	 * 
	 * @param productCodeEncoded
	 * 	product code of the variant
	 * @param entryNumber
	 * 	entry number
	 * @param model
	 * 	view model
	 * @param request
	 * 	http request
	 * @return view name
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/{productCode:.*}/{entryNumber}/"
			+ SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_FROM_CART_BOUND_URL, method = RequestMethod.GET)
	public String getVariantOverviewFromCartBoundConfig(@PathVariable("productCode") final String productCodeEncoded,
			@PathVariable("entryNumber") final int entryNumber, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final ConfigurationOverviewData configOverviewData = prepareOverviewData(productCodeEncoded);

		final OverviewUiData overviewUiData = initializeOverviewUiDataForVariantFromCartBound(entryNumber);
		prepareUiModel(request, model, overviewUiData, configOverviewData);

		return SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME;
	}

	protected ConfigurationOverviewData prepareOverviewData(final String productCodeEncoded)
	{
		final String productCode = decodeWithScheme(productCodeEncoded, UTF_8);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CONFIG_GET_RECEIVED_FOR + productCode + CURRENT_SESSION
					+ getSessionService().getCurrentSession().getSessionId() + CLOSING_QUOTE);
		}

		final ConfigurationOverviewData configOverviewData = populateConfigurationModel(productCode);
		getUiRecorder().recordUiAccessVariantOverview(productCode);
		return configOverviewData;
	}

	/**
	 * Cleans up the UI-State, after a variant has been added to the cart
	 *
	 * @param productCodeEncoded
	 *           product code of variant
	 */
	@RequestMapping(value = "/**/{productCode:.*}/addVariantToCartCleanUp", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void addVariantToCartCleanUp(@PathVariable("productCode") final String productCodeEncoded)
	{
		final String productCode = decodeWithScheme(productCodeEncoded, UTF_8);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("AddVariantToCart GET received for '" + productCode + CURRENT_SESSION
					+ getSessionService().getCurrentSession().getSessionId() + CLOSING_QUOTE);
		}

		final ProductData productData = getProductFacade().getProductForCodeAndOptions(productCode,
				Arrays.asList(ProductOption.BASIC));

		final String baseProduct = productData.getBaseProduct();
		resetUiCartConfigurationForProduct(baseProduct);
	}

	protected OverviewUiData initializeOverviewUiDataForVariant()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		overviewUiData.setOverviewMode(OverviewMode.VARIANT_OVERVIEW);
		return overviewUiData;
	}

	protected OverviewUiData initializeOverviewUiDataForVariantFromCartBound(final int entryNumber)
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		overviewUiData.setOverviewMode(OverviewMode.VARIANT_OVERVIEW_FROM_CART_BOUND_CONFIG);
		overviewUiData.setCartEntryNumber(entryNumber);
		return overviewUiData;
	}

	protected void resetUiCartConfigurationForProduct(final String productCode)
	{
		final String configId = getProductLinkStrategy().getConfigIdForProduct(productCode);
		if (configId != null)
		{
			final boolean configIsAttachedToCartEntry = null != getAbstractOrderEntryLinkStrategy()
					.getCartEntryForConfigId(configId);
			if (!configIsAttachedToCartEntry)
			{
				getConfigCartFacade().resetConfiguration(configId);
			}
			getSessionAccessFacade().removeUiStatusForProduct(productCode);
			getProductLinkStrategy().removeConfigIdForProduct(productCode);
		}
	}

	protected ConfigurationOverviewData populateConfigurationModel(final String productCode)
	{
		final ConfigurationOverviewData configOverview = getConfigurationOverviewFacade().getOverviewForProductVariant(productCode,
				null);
		configOverview.setProductCode(productCode);
		return configOverview;
	}

	protected OverviewUiData prepareOverviewUiData(final ConfigurationOverviewData configOverviewData,
			final ProductData productData)
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		overviewUiData.setProductCode(productData.getCode());
		overviewUiData.setQuantity(getQuantity(productData.getBaseProduct()));
		overviewUiData.setGroups(configOverviewData.getGroups());
		overviewUiData.setOverviewMode(OverviewMode.VARIANT_OVERVIEW);

		return overviewUiData;
	}

	@Override
	protected void addBreadCrumb(final Model model, final ProductData productData, final OverviewUiData overviewUiData)
	{
		if (overviewUiData.getCartEntryNumber() == null)
		{
			model.addAttribute(WebConstants.BREADCRUMBS_KEY,
					getBreadcrumbBuilder().getVariantOverviewBreadcrumbs(productData.getBaseProduct(), productData.getCode()));
		}
		else
		{

			model.addAttribute(WebConstants.BREADCRUMBS_KEY, getBreadcrumbBuilder().getVariantOverviewCartContextBreadcrumbs(
					productData.getBaseProduct(), productData.getCode(), overviewUiData.getCartEntryNumber()));
		}

	}


	/**
	 * Updates the product config overview page for variants. For example if a filter value was changed.
	 *
	 * @param productCode
	 *           product code of the variant
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/**/{productCode:.*}"
			+ SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_URL, method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateVariantOverview(@PathVariable("productCode") final String productCode, final Model model,
			final HttpServletRequest request) throws CMSItemNotFoundException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(CONFIG_POST_RECEIVED_FOR + productCode + CURRENT_SESSION
					+ getSessionService().getCurrentSession().getSessionId() + CLOSING_QUOTE);
		}

		final ConfigurationOverviewData configOverviewData = populateConfigurationModel(productCode);
		final OverviewUiData overviewUiData = new OverviewUiData();
		prepareUiModel(request, model, overviewUiData, configOverviewData);

		return new ModelAndView(AJAX_VIEW_NAME);
	}
}
