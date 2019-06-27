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

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.catalogversions.service.CatalogVersionPermissionService;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.cmswebservices.resolvers.RequestMethodResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Default interceptor to run before controllers' execution to extract the catalog-id and version-id from the request URI and to set the
 * current session values. Also determines if the current user has the required permission to access the catalog version.
 */
public class CatalogVersionInterceptor extends HandlerInterceptorAdapter
{
	private CMSAdminSiteService cmsAdminSiteService;
	private CatalogService catalogService;
	private CatalogVersionService catalogVersionService;
	private CMSCatalogVersionService cmsCatalogVersionService;
	private CatalogVersionPermissionService catalogVersionPermissionService;
	private RequestMethodResolver requestMethodResolver;

	/**
	 * Adds the catalog and version to the session. Also determines if the current user has read or write permission to
	 * the catalog version for an operation given by the request's HTTP method. In the case that the user does not have
	 * permission, we return an error in the HTTP response using the status code 403 - Forbidden.
	 *
	 * @throws Exception <ul>
	 *                   <li>{@link UnknownIdentifierException} if no CatalogVersion with the specified catalog id and version
	 *                   exists
	 *                   <li>{@link AmbiguousIdentifierException} if more than one CatalogVersion is found with the specified
	 *                   catalog id and version
	 *                   </ul>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		final Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (Objects.nonNull(pathVariables))
		{
			final String catalog = pathVariables.get(CmswebservicesConstants.URI_CATALOG_ID);
			final String catalogVersion = pathVariables.get(CmswebservicesConstants.URI_VERSION_ID);
			final String siteId = pathVariables.get(CmswebservicesConstants.URI_SITE_ID);

			if (StringUtils.isNotEmpty(catalog) && StringUtils.isNotEmpty(catalogVersion))
			{
				getCmsAdminSiteService().setActiveCatalogVersion(catalog, catalogVersion);
				getCatalogVersionService().setSessionCatalogVersion(catalog, catalogVersion);
				setSessionParentCatalogVersionsForSite(siteId, catalog, catalogVersion);

				return getCatalogVersionPermissionService()
						.hasPermission(getRequestMethodResolver().resolvePostToGet(request), response, catalog, catalogVersion);
			}
		}
		return true;
	}

	/**
	 * Add the active catalog versions for all parent catalog defined for a given site. This is used in a multi-country
	 * setup.
	 *
	 * @param siteId         the site identifier
	 * @param catalog        the catalog identifier
	 * @param catalogVersion the catalog version
	 */
	protected void setSessionParentCatalogVersionsForSite(final String siteId, final String catalog, final String catalogVersion)
	{
		if (StringUtils.isNotEmpty(siteId))
		{
			final CMSSiteModel site = getCmsAdminSiteService().getSiteForId(siteId);
			final ContentCatalogModel contentCatalog = (ContentCatalogModel) getCatalogService().getCatalogForId(catalog);

			final List<CatalogVersionModel> catalogVersions = getCmsCatalogVersionService()
					.getSuperCatalogsActiveCatalogVersions(contentCatalog, site);
			catalogVersions.add(getCatalogVersionService().getCatalogVersion(catalog, catalogVersion));

			getCatalogVersionService().setSessionCatalogVersions(catalogVersions);
		}
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

	protected CatalogService getCatalogService()
	{
		return catalogService;
	}

	@Required
	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
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

	protected CMSCatalogVersionService getCmsCatalogVersionService()
	{
		return cmsCatalogVersionService;
	}

	@Required
	public void setCmsCatalogVersionService(final CMSCatalogVersionService cmsCatalogVersionService)
	{
		this.cmsCatalogVersionService = cmsCatalogVersionService;
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

	protected RequestMethodResolver getRequestMethodResolver()
	{
		return requestMethodResolver;
	}

	@Required
	public void setRequestMethodResolver(RequestMethodResolver requestMethodResolver)
	{
		this.requestMethodResolver = requestMethodResolver;
	}
}
