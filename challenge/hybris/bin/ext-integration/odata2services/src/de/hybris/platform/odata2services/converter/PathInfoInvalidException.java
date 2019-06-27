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
package de.hybris.platform.odata2services.converter;

/**
 * An exception that occurs when the incoming HttpServletRequest method is not supported by the ODataRequest.
 *
 * @see org.apache.olingo.odata2.api.commons.ODataHttpMethod
 */
public class PathInfoInvalidException extends RuntimeException
{
	private final String requestUri;

	public PathInfoInvalidException(final String requestUri, Throwable cause)
	{
		super(cause);
		this.requestUri = requestUri;
	}

	/**
	 * Retrieves the request URI from the HttpServletRequest that failed to parse
	 *
	 * @return String with the URI
	 */
	public String getRequestUri()
	{
		return requestUri;
	}
}
