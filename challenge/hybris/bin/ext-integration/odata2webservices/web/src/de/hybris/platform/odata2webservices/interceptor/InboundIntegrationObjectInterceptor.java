/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


public class InboundIntegrationObjectInterceptor implements HandlerInterceptor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InboundIntegrationObjectInterceptor.class);
	private IntegrationObjectService integrationObjectService;

	@Override
	public boolean preHandle(final HttpServletRequest httpServletRequest, final HttpServletResponse
			httpServletResponse, final Object o) throws Exception
	{
		final String integrationObjectCode = extractCode(httpServletRequest.getPathInfo());
		try
		{
			final IntegrationObjectModel integrationObject =
					getIntegrationObjectService().findIntegrationObject(integrationObjectCode);
			if (IntegrationType.INBOUND.equals(integrationObject.getIntegrationType()))
			{
				return true;
			}
		}
		catch (final ModelNotFoundException | IllegalArgumentException e)
		{
			LOGGER.trace("IntegrationObject not found for code: '{}'", integrationObjectCode, e);
		}
		httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		return false;
	}

	@Override
	public void postHandle(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
			final Object o, final ModelAndView modelAndView) throws Exception
	{
		// not implemented
	}

	@Override
	public void afterCompletion(final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse, final Object o, final Exception e) throws Exception
	{
		// not implemented
	}

	protected String extractCode(final String pathInfo)
	{
		if(pathInfo == null)
		{
			return StringUtils.EMPTY;
		}
		final String[] elements = pathInfo.split("/", 3);
		return elements.length >= 2 ? elements[1] : StringUtils.EMPTY;
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return integrationObjectService;
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService flexibleSearch)
	{
		this.integrationObjectService = flexibleSearch;
	}
}
