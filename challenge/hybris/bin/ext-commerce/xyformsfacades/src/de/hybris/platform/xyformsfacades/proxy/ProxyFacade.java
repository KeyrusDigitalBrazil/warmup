/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*/
package de.hybris.platform.xyformsfacades.proxy;

import de.hybris.platform.xyformsservices.enums.YFormDataActionEnum;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;
import de.hybris.platform.xyformsservices.proxy.ProxyException;
import de.hybris.platform.xyformsservices.proxy.ProxyService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Orchestrates calls to {@link ProxyService}
 */
public interface ProxyFacade
{
	/**
	 * Gets the embedded HTML representation of a form definition.
	 *
	 * @param applicationId
	 * 			the application id of the form definition
	 * @param formId
	 * 			the form id of the form definition
	 * @param action
	 * 			the form action
	 * @param formDataId
	 * 			the form data id of the form definition
	 *	@return the inline representation of the form definition as string
	 * @throws YFormServiceException if request with specified parameter cannot be proxied or response is corrupted
	 */
	public String getInlineFormHtml(final String applicationId, final String formId, final YFormDataActionEnum action,
			final String formDataId) throws YFormServiceException;

	/**
	 * Proxies content
	 *
	 * @param request
	 *           the {@link HttpServletRequest} associated with the call
	 * @param response
	 *           the {@link HttpServletResponse} associated with the call
	 * @throws ProxyException when request cannot be proxied
	 */
	public void proxy(final HttpServletRequest request, final HttpServletResponse response) throws ProxyException;
}
