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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;


/**
 * Provides extension possibility for related before view handler
 */
public interface ProductConfigBeforeViewExtender
{
	/**
	 * Callback that is called before any view rendered (also not CPQ-related views)<br>
	 * checking on the viewName to ensure that certain logic is only executed in context of CPQ might be required.
	 *
	 * @param request
	 *           Http-Request
	 * @param response
	 *           Http-Response
	 * @param model
	 *           view model
	 * @param viewName
	 *           view name
	 */
	void execute(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model, final String viewName);
}
