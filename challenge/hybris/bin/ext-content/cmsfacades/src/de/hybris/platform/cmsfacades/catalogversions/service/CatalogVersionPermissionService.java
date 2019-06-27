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
package de.hybris.platform.cmsfacades.catalogversions.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;


/**
 * Service to verify if the current session user has read or write permission for a catalog version which matches the
 * request HTTP CRUD operation.
 * <ul>
 * <li>A GET request requires at least a read permission for a catalog version.
 * <li>A POST, PUT and DELETE request requires at least a write permission for a catalog version.
 * </ul>
 */
public interface CatalogVersionPermissionService
{
	/**
	 * Determines if the current user has read or write permission to the catalog version for an operation given by the
	 * request's HTTP method. In the case that the user does not have permission, an error in the HTTP response with the
	 * status {@link HttpStatus#FORBIDDEN} is returned.
	 *
	 * @param requestMethod
	 *           the HTTP request method: GET, PUT, POST or DELETE
	 * @param response
	 *           the HTTP servlet response containing the error message if the user does not have access to the requested
	 *           catalog version
	 * @param catalog
	 *           the catalog identifier
	 * @param catalogVersion
	 *           the catalog version identifier
	 * @return {@code true} when the current user has read or write permission to the given catalog version or is an
	 *         anonymous user; {@code false} otherwise.
	 * @throws IOException
	 *            when an error occurs while raising the error
	 */
	boolean hasPermission(final String requestMethod, final HttpServletResponse response, final String catalog,
			final String catalogVersion) throws IOException;
}
