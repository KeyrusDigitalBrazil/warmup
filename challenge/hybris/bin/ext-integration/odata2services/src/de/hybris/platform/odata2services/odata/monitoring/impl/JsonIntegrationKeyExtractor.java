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
package de.hybris.platform.odata2services.odata.monitoring.impl;

import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_JSON;
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_JSON_UTF8;

import de.hybris.platform.integrationservices.util.HttpStatus;
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * This {@link IntegrationKeyExtractor} extracts the integration key value from a JSON response
 */
public class JsonIntegrationKeyExtractor implements IntegrationKeyExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonIntegrationKeyExtractor.class);
	private static final String SUCCESS_PATH_EXPRESSION = "$.d.integrationKey";
	private static final String ERROR_PATH_EXPRESSION = "$.error.innererror";

	@Override
	public boolean isApplicable(final String contentType)
	{
		return (APPLICATION_JSON.equalsIgnoreCase(contentType) || APPLICATION_JSON_UTF8.equalsIgnoreCase(contentType));
	}

	@Override
	public String extractIntegrationKey(final String responseBody, final int statusCode)
	{
		try
		{
			final DocumentContext ctx = JsonPath.parse(responseBody);
			return ctx.read(getPathExpression(statusCode), String.class);
		}
		catch(final PathNotFoundException e)
		{
			LOGGER.error("Failed extracting the integrationKey value from the JSON response", e);
			return StringUtils.EMPTY;
		}
	}

	private static String getPathExpression(final int statusCode)
	{
		return HttpStatus.valueOf(statusCode).isError() ?  ERROR_PATH_EXPRESSION : SUCCESS_PATH_EXPRESSION;
	}
}
