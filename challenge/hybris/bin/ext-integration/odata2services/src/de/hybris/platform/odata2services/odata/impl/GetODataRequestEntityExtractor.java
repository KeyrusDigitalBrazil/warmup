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

import de.hybris.platform.odata2services.odata.IncorrectQueryParametersException;
import de.hybris.platform.odata2services.odata.ODataRequestEntityExtractor;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.processor.ODataRequest;

public class GetODataRequestEntityExtractor implements ODataRequestEntityExtractor
{
	@Override
	public boolean isApplicable(final ODataRequest request)
	{
		return isMetadataGetRequest(request) && isQueryParametersPresent(request);
	}

	@Override
	public String extract(final ODataRequest request)
	{
		final Map<String, String> queryParameters = request.getQueryParameters();
		if (isQueryParametersValid(queryParameters))
		{
			throw new IncorrectQueryParametersException();
		}
		return queryParameters.entrySet().iterator().next().getKey();
	}

	private static boolean isQueryParametersPresent(final ODataRequest oDataRequest)
	{
		return !oDataRequest.getQueryParameters().isEmpty();
	}

	private static boolean isQueryParametersValid(final Map<String, String> queryParameters)
	{
		return queryParameters.size() != 1;
	}

	private static boolean isMetadataGetRequest(final ODataRequest oDataRequest)
	{
		return ODataHttpMethod.GET.name().equals(oDataRequest.getHttpMethod()) && StringUtils.contains(oDataRequest.getPathInfo().getRequestUri().getPath(), "$metadata");
	}
}
