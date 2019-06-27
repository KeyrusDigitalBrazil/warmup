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
package de.hybris.platform.cmsfacades.catalogversions.service.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.catalogversions.service.CatalogVersionPermissionService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;


/**
 * Verifies that the current user has read or write access for a given catalog version according to the requested CRUD
 * operation.
 */
public class DefaultCatalogVersionPermissionService implements CatalogVersionPermissionService
{
	private CatalogVersionService catalogVersionService;
	private UserService userService;

	/**
	 * {@inheritDoc}
	 * <p>
	 * In the case that the user does not have permission, an error in the HTTP response using the status code
	 * {@code 403 - Forbidden} is returned.
	 */
	@Override
	public boolean hasPermission(final String requestMethod, final HttpServletResponse response, final String catalog,
			final String catalogVersion) throws IOException
	{
		final UserModel userModel = getUserService().getCurrentUser();
		if (getUserService().isAnonymousUser(userModel))
		{
			// return true such that a {401 - Unauthorized error} is thrown instead to force SmartEdit to load the login page
			return true;
		}

		final CatalogVersionModel catalogVersionModel = getCatalogVersionService().getCatalogVersion(catalog, catalogVersion);
		final boolean canRead = getCatalogVersionService().canRead(catalogVersionModel, userModel) || (catalogVersionModel.getActive()  != null && catalogVersionModel.getActive());
		final boolean canWrite = getCatalogVersionService().canWrite(catalogVersionModel, userModel);

		boolean status = true;
		if (!canRead && HttpMethod.GET.name().equalsIgnoreCase(requestMethod))
		{
			response.sendError(HttpStatus.FORBIDDEN.value(),
					"The current user does not have read permission for the catalog version to perform this operation.");
			status = false;
		}
		else if (!canWrite && Stream.of(HttpMethod.PUT.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name()).parallel()
				.anyMatch(requestMethod::equalsIgnoreCase))
		{
			response.sendError(HttpStatus.FORBIDDEN.value(),
					"The current user does not have write permission for the catalog version to perform this operation.");
			status = false;
		}
		return status;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
