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
package de.hybris.platform.cmsfacades.header;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.UriTemplate;


/**
 * Utility class to create the location URI which is added in the header when creating a resource using POST.
 */
public class LocationHeaderResource
{
	/**
	 * Create the location URI to be added to the header when creating a resource using POST
	 *
	 * @param request
	 * @param childIdentifier
	 * @return the location header URI
	 */
	public String createLocationForChildResource(final HttpServletRequest request, final Object childIdentifier)
	{
		final StringBuffer url = request.getRequestURL();
		final UriTemplate template = new UriTemplate(url.append("/{childId}").toString());
		return template.expand(childIdentifier).toASCIIString();
	}
}
