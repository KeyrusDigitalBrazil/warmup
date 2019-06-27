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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * Default interceptor to run before controllers' execution to extract the itemUuid from the request URI and to set the
 * catalog version of the item in the session.
 */
public class CmsItemCatalogVersionInterceptor extends HandlerInterceptorAdapter
{

	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CMSAdminSiteService cmsAdminSiteService;
	private CatalogVersionService catalogVersionService;

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception
	{
		final Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		if (Objects.nonNull(pathVariables))
		{
			final String itemUUID = pathVariables.get(CmswebservicesConstants.URI_ITEM_UUID);

			if (StringUtils.isNotEmpty(itemUUID))
			{

				final Optional<CMSItemModel> cmsItemModel = getUniqueItemIdentifierService().getItemModel(itemUUID,
						CMSItemModel.class);
				// Set the current session site id only; no other session default values are initialized
				if (cmsItemModel.isPresent())
				{
					final String catalog = cmsItemModel.get().getCatalogVersion().getCatalog().getId();
					final String catalogVersion = cmsItemModel.get().getCatalogVersion().getVersion();

					getCmsAdminSiteService().setActiveCatalogVersion(catalog, catalogVersion);
					getCatalogVersionService().setSessionCatalogVersion(catalog, catalogVersion);
				}
			}
		}

		return true;

	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
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

}
