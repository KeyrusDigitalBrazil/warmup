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
package de.hybris.platform.sap.productconfig.frontend.beforeview;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;



/**
 * Product Configuration {@link BeforeViewHandlerAdaptee}. Disables browser side caching for product configuration page
 * and product configuration overview page, by setting the corresponding HHTP-Headers.<br>
 * Content of the configuration related pages is dynamic, hence caching does not make any sense.
 */
public class ProductDetailsBeforeViewHandler implements BeforeViewHandlerAdaptee
{
	/**
	 * Path to the product configuration page
	 */
	public static final String PRODUCT_CONFIG_PAGE = "addon:/" + SapproductconfigaddonConstants.EXTENSIONNAME
			+ "/pages/configuration/configurationPage";
	/**
	 * Path to the product configuration overview page
	 */
	public static final String PRODUCT_CONFIG_OVERVIEW_PAGE = "addon:/" + SapproductconfigaddonConstants.EXTENSIONNAME
			+ "/pages/configuration/configurationOverviewPage";
	private static final Logger LOG = Logger.getLogger(ProductDetailsBeforeViewHandler.class);

	private ProductConfigBeforeViewExtender extender;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName)
	{
		extender.execute(request, response, model, viewName);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Intercepting view:" + viewName);
		}
		if (viewName.equals(PRODUCT_CONFIG_PAGE) || viewName.equals(PRODUCT_CONFIG_OVERVIEW_PAGE))
		{
			response.setHeader("Cache-control", "no-cache, no-store");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "-1");
		}

		return viewName;
	}

	/**
	 * @return the extender
	 */
	protected ProductConfigBeforeViewExtender getExtender()
	{
		return extender;
	}

	/**
	 * @param extender
	 *           the extender to set
	 */
	public void setExtender(final ProductConfigBeforeViewExtender extender)
	{
		this.extender = extender;
	}

}
