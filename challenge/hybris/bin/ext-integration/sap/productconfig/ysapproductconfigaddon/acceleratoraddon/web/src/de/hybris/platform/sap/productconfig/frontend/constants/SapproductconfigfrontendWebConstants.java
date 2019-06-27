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
package de.hybris.platform.sap.productconfig.frontend.constants;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.controllers.AbstractProductConfigController;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;


/**
 * Global class for all ysapproductconfigaddon web constants. You can add global constants for your extension into this
 * class.
 */
public final class SapproductconfigfrontendWebConstants
{
	/**
	 * relative URL for sapproductconfig overview page
	 */
	public static final String CONFIG_OVERVIEW_URL = "/configOverview";
	/**
	 * relative URL for sapproductconfig read-only page
	 */
	public static final String GENERIC_CONFIG_DISPLAY = "/configurationDisplay";
	/**
	 * relative URL for sapproductconfig variant display page
	 */
	public static final String VARIANT_OVERVIEW_URL = "/variantOverview";
	/**
	 * relative URL for sapproductconfig variant display page
	 */
	public static final String VARIANT_OVERVIEW_FROM_CART_BOUND_URL = "/variantOvFromCartBound";
	/**
	 * relative URL for the sapproductconfig configuration page
	 */
	public static final String CONFIG_URL = "/configuratorPage/" + ConfiguratorType.CPQCONFIGURATOR.toString();
	/**
	 * relative URL for the sapproductconfig configuration page with cart context
	 */
	public static final String CART_CONFIG_URL = "/configuration/" + ConfiguratorType.CPQCONFIGURATOR.toString();
	/**
	 * relative URL for the sapproductconfig configuration page with cart context, supposed draft already exist
	 */
	public static final String CART_CONFIG_EXISTING_DRAFT_URL = "/configureOnDraft/" + ConfiguratorType.CPQCONFIGURATOR.toString();
	/**
	 * relative URL for the order display page
	 */
	public static final String ORDER_DETAILS = "/my-account/order/";
	/**
	 * relative URL for the my quotes page
	 */
	public static final String QUOTES = "/my-account/my-quotes/";
	/**
	 * relative URL for the saved cart page
	 */
	public static final String SAVED_CARTS = "/my-account/saved-carts/";

	/**
	 * viewname prefix for addon views
	 */
	public static final String ADDON_PREFIX = "addon:";
	/**
	 * viewname suffix for AJAX based views
	 */
	public static final String AJAX_SUFFIX = "ForAJAXRequests";
	/**
	 * relative JSP location of the confioguration page
	 */
	public static final String CONFIG_PAGE_ROOT = "/pages/configuration/configurationPage";
	/**
	 * relative JSP location of the configuration overview page
	 */
	public static final String OVERVIEW_PAGE_ROOT = "/pages/configuration/configurationOverviewPage";
	/**
	 * relative JSP location of the configuration error forwarding page for AJAX requests
	 */
	public static final String CONFIG_ERROR_ROOT = "/pages/configuration/errorForwardForAJAXRequests";
	/**
	 * relative JSP location of the search variant result page
	 */
	public static final String SEARCH_VARIANTS_ROOT = "/pages/configuration/searchVariants";

	/**
	 * view name of the configuration page
	 */
	public static final String CONFIG_PAGE_VIEW_NAME = ADDON_PREFIX + AbstractController.ROOT
			+ SapproductconfigaddonConstants.EXTENSIONNAME + CONFIG_PAGE_ROOT;
	/**
	 * view name of the configuration overview page
	 */
	public static final String OVERVIEW_PAGE_VIEW_NAME = ADDON_PREFIX + AbstractController.ROOT
			+ SapproductconfigaddonConstants.EXTENSIONNAME + OVERVIEW_PAGE_ROOT;
	/**
	 * view name of the error forwarding page for AJAX requests
	 */
	public static final String ERROR_FORWARD_FOR_AJAX_VIEW_NAME = ADDON_PREFIX + AbstractController.ROOT
			+ SapproductconfigaddonConstants.EXTENSIONNAME + CONFIG_ERROR_ROOT;
	/**
	 * view name of the search variant result page
	 */
	public static final String SEARCH_VARIANTS_VIEW_NAME = ADDON_PREFIX + AbstractProductConfigController.ROOT
			+ SapproductconfigaddonConstants.EXTENSIONNAME + SEARCH_VARIANTS_ROOT;

	/**
	 * {@link ConfiguratorType} for CPQ as string value.<br>
	 * Same as <code>ConfiguratorType.CPQCONFIGURATOR.toString()</code>
	 */
	public static final String CONFIGURATOR_TYPE = "CPQCONFIGURATOR";

	/**
	 * view attribute name for the {@link ProductData} DTO
	 */
	public static final String PRODUCT_ATTRIBUTE = "product";
	/**
	 * view attribute name for the {@link ConfigurationData} DTO
	 */
	public static final String CONFIG_ATTRIBUTE = SapproductconfigaddonConstants.CONFIG_ATTRIBUTE;
	/**
	 * view attribute name for the {@link OverviewUiData} DTO
	 */
	public static final String OVERVIEWUIDATA_ATTRIBUTE = "overviewUiData";
	/**
	 * view attribute name for the {@link VariantSearchResult} DTO
	 */
	public static final String VARIANT_SEARCH_RESULT_ATTRIBUTE = "variantSearchResult";

	/**
	 * Prefix for conflict groups
	 */
	public static final String CONFLICT_PREFIX = "CONFLICT";

	private SapproductconfigfrontendWebConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
