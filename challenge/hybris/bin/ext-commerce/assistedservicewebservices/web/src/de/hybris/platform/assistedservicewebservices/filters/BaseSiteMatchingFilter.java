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
package de.hybris.platform.assistedservicewebservices.filters;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter that resolves base site id from the requested url and activates it.
 */
public class BaseSiteMatchingFilter extends OncePerRequestFilter
{
	private BaseSiteService baseSiteService;
	private static final String BASE_SITE_PARAM = "baseSite";
	private List<String> excludedUrls;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (isFilterRequired(request.getPathInfo()))
		{
			final String baseSite = getBaseSiteFromRequest(request);
			if (StringUtils.isBlank(baseSite))
			{
				throw new IllegalArgumentException("Request must have a baseSite");
			}
			else
			{
				setCurrentBaseSite(baseSite);
			}
		}
		filterChain.doFilter(request, response);
	}

	private boolean isFilterRequired(final String url)
	{
		return !excludedUrls.stream().anyMatch(excluded -> url.matches(excluded));
	}

	protected String getBaseSiteFromRequest(final HttpServletRequest request)
	{
		return request.getParameter(BASE_SITE_PARAM);
	}

	private void setCurrentBaseSite(final String baseSite) throws ServletException
	{
		final BaseSiteModel requestedBaseSite = getBaseSiteService().getBaseSiteForUID(baseSite);
		if (requestedBaseSite == null)
		{
			throw new ServletException("Requested BaseSite: " + baseSite + " cannot be null");
		}
		if (isDifferentThanCurrentSite(requestedBaseSite))
		{
			getBaseSiteService().setCurrentBaseSite(requestedBaseSite, true);
		}
	}

	private boolean isDifferentThanCurrentSite(final BaseSiteModel requestedBaseSite)
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		return !requestedBaseSite.equals(currentBaseSite);
	}


	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected List<String> getExcludedUrls()
	{
		return excludedUrls;
	}

	@Required
	public void setExcludedUrls(final List<String> excludedUrls)
	{
		this.excludedUrls = excludedUrls;
	}
}
