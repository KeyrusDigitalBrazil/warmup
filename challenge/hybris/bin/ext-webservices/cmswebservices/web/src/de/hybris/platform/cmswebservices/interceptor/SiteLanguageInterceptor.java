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
package de.hybris.platform.cmswebservices.interceptor;

import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.site.BaseSiteService;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Default interceptor to run before controllers' execution to extract the site-id from the request URI and to set the
 * current session value.
 */
public class SiteLanguageInterceptor extends HandlerInterceptorAdapter
{
	private BaseSiteService baseSiteService;

	private CMSAdminSiteService adminSiteService;

	/**
	 * Adds the siteId to the session. When siteId is not valid, the current session siteId is set to <tt>null</tt>.
	 */
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
	{
		final Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (Objects.nonNull(pathVariables))
		{
			final String siteId = pathVariables.get(CmswebservicesConstants.URI_SITE_ID);

			if (StringUtils.isNotEmpty(siteId))
			{
				// Set the current session site id only; no other session default values are initialized
				getBaseSiteService().setCurrentBaseSite(siteId, Boolean.FALSE);
				getAdminSiteService().setActiveSiteForId(siteId);
			}
		}
		return true;
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

	protected CMSAdminSiteService getAdminSiteService()
	{
		return adminSiteService;
	}

	@Required
	public void setAdminSiteService(final CMSAdminSiteService adminSiteService)
	{
		this.adminSiteService = adminSiteService;
	}

}
