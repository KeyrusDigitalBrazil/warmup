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

import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_ID;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_CATALOG_VERSION;
import static de.hybris.platform.cmswebservices.constants.CmswebservicesConstants.URI_UUID;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.web.servlet.HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.catalogversions.service.CatalogVersionPermissionService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.hybris.platform.cmswebservices.resolvers.RequestMethodResolver;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Default interceptor to run before controllers' execution to extract the uuid from the request URI and determines if
 * the current user has the required permission to access the catalog version.
 */
public class CatalogVersionPermissionInterceptor extends HandlerInterceptorAdapter
{
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CatalogVersionPermissionService catalogVersionPermissionService;
	private ObjectFactory<ItemData> itemDataDataFactory;
	private RequestMethodResolver requestMethodResolver;

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception
	{
		final Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		if (Objects.nonNull(pathVariables))
		{
			final String[] catalogAndVersion = new String[2];

			final String uuid = pathVariables.get(URI_UUID);
			if (StringUtils.isNotEmpty(uuid))
			{
				getUniqueItemIdentifierService().getItemModel(uuid, CMSItemModel.class).ifPresent(cmsItem -> {
					catalogAndVersion[0] = cmsItem.getCatalogVersion().getCatalog().getId();
					catalogAndVersion[1] = cmsItem.getCatalogVersion().getVersion();
				});
			}
			else if (request.getMethod().equalsIgnoreCase(POST.name()))
			{
				final HashMap<?, ?> bodyData = new ObjectMapper().readValue(request.getInputStream(), HashMap.class);
				final String catalogVersion = (String) bodyData.get(URI_CATALOG_VERSION);

				getUniqueItemIdentifierService().getItemModel(catalogVersion, CatalogVersionModel.class)
				.ifPresent(catalogVersionModel -> {
					catalogAndVersion[0] = catalogVersionModel.getCatalog().getId();
					catalogAndVersion[1] = catalogVersionModel.getVersion();
				});
			}
			else
			{
				// For cmsItems search, get the catalogId and catalogVersion from the query parameters
				catalogAndVersion[0] = request.getParameter(URI_CATALOG_ID);
				catalogAndVersion[1] = request.getParameter(URI_CATALOG_VERSION);
			}

			if (Objects.nonNull(catalogAndVersion[0]) && Objects.nonNull(catalogAndVersion[1]))
			{
				return getCatalogVersionPermissionService() //
						.hasPermission(getRequestMethodResolver().resolvePostToGet(request), response, catalogAndVersion[0], catalogAndVersion[1]);
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

	protected CatalogVersionPermissionService getCatalogVersionPermissionService()
	{
		return catalogVersionPermissionService;
	}

	@Required
	public void setCatalogVersionPermissionService(final CatalogVersionPermissionService catalogVersionPermissionService)
	{
		this.catalogVersionPermissionService = catalogVersionPermissionService;
	}

	protected ObjectFactory<ItemData> getItemDataDataFactory()
	{
		return itemDataDataFactory;
	}

	@Required
	public void setItemDataDataFactory(final ObjectFactory<ItemData> itemDataDataFactory)
	{
		this.itemDataDataFactory = itemDataDataFactory;
	}

	protected RequestMethodResolver getRequestMethodResolver()
	{
		return requestMethodResolver;
	}

	@Required
	public void setRequestMethodResolver(
			RequestMethodResolver requestMethodResolver)
	{
		this.requestMethodResolver = requestMethodResolver;
	}
}
