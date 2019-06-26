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
package de.hybris.platform.cmssmarteditwebservices.interceptor;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.catalogversions.service.CatalogVersionPermissionService;
import de.hybris.platform.cmssmarteditwebservices.constants.CmssmarteditwebservicesConstants;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Default interceptor to run before controllers' execution to extract the catalog-id and version-id from the request URI and to set the
 * current session values. Also determines if the current user has the required permission to access the catalog version.
 */
public class CatalogVersionInterceptor extends HandlerInterceptorAdapter
{
	private CMSAdminSiteService cmsAdminSiteService;
	private CatalogVersionService catalogVersionService;
	private CatalogVersionPermissionService catalogVersionPermissionService;

	/**
	 * Adds the catalog and version to the session. Also determines if the current user has read or write permission to
	 * the catalog version for an operation given by the request's HTTP method. In the case that the user does not have
	 * permission, we return an error in the HTTP response using the status code 403 - Forbidden.
	 *
	 * @throws Exception
	 *            <ul>
	 *            <li>{@link UnknownIdentifierException} if no CatalogVersion with the specified catalog id and version
	 *            exists
	 *            <li>{@link AmbiguousIdentifierException} if more than one CatalogVersion is found with the specified
	 *            catalog id and version
	 *            </ul>
	 */
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (Objects.nonNull(pathVariables))
		{
			final String catalog = pathVariables.get(CmssmarteditwebservicesConstants.URI_CATALOG_ID);
			final String catalogVersion = pathVariables.get(CmssmarteditwebservicesConstants.URI_VERSION_ID);

			if (StringUtils.isNotEmpty(catalog) && StringUtils.isNotEmpty(catalogVersion))
			{
				getCmsAdminSiteService().setActiveCatalogVersion(catalog, catalogVersion);
				getCatalogVersionService().setSessionCatalogVersion(catalog, catalogVersion);

				return getCatalogVersionPermissionService().hasPermission(request.getMethod(), response, catalog, catalogVersion);
			}
		}
		return true;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected CatalogVersionPermissionService getCatalogVersionPermissionService()
	{
		return catalogVersionPermissionService;
	}

	@Required
	public void setCatalogVersionPermissionService(final CatalogVersionPermissionService catalogVersionPermissionService)
	{
		this.catalogVersionPermissionService = catalogVersionPermissionService;
	}
}
