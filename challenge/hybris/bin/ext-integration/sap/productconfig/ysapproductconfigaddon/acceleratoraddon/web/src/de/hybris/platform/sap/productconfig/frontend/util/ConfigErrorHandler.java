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
package de.hybris.platform.sap.productconfig.frontend.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Allows to define navigation end point if a error occurs during configuration process.
 */
public interface ConfigErrorHandler
{
	/**
	 * Returns navigation end point for AJAX request in session failover case.
	 *
	 * @param request
	 * @param model
	 * @return navigation end point for AJAX request in session failover case.
	 */
	ModelAndView handleErrorForAjaxRequest(HttpServletRequest request, Model model);

	/**
	 *
	 * @return Navigation end point for session failover case.
	 */
	String handleError();

	/**
	 *
	 * @return Navigation end point for configuration access error case.
	 */
	String handleConfigurationAccessError();


	/**
	 * @param orderCode
	 * @param entryNumber
	 * @param redirectModel
	 * @return navigation end point for error during configuration from order history
	 */
	String handleErrorFromOrderHistory(String orderCode, int entryNumber, RedirectAttributes redirectModel);

	/**
	 * @param quotationId
	 * @param entryNumber
	 * @param redirectModel
	 * @return navigation end point for error during configuration from quotes
	 */
	String handleErrorFromQuotes(String quotationId, int entryNumber, RedirectAttributes redirectModel);

	/**
	 * @param cartCode
	 * @param entryNumber
	 * @param redirectModel
	 * @return navigation end point for error during configuration from saved carts
	 */
	String handleErrorFromSavedCarts(String cartCode, int entryNumber, RedirectAttributes redirectModel);

}
