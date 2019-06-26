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
package de.hybris.platform.odata2services.odata.impl;


import de.hybris.platform.odata2services.odata.ODataRequestEntityExtractor;

import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.processor.ODataRequest;

public class PostODataRequestEntityExtractor implements ODataRequestEntityExtractor
{
	@Override
	public boolean isApplicable(final ODataRequest request)
	{
		return isPostRequest(request);
	}

	@Override
	public String extract(final ODataRequest request)
	{
		final String[] pathSegments = request.getPathInfo().getRequestUri().getPath().split("/");
		return pathSegments[pathSegments.length - 1];
	}

	private static boolean isPostRequest(final ODataRequest oDataRequest)
	{
		return ODataHttpMethod.POST.name().equals(oDataRequest.getHttpMethod());
	}
}
