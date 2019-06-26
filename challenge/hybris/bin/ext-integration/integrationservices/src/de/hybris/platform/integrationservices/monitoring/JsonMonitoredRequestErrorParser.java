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
package de.hybris.platform.integrationservices.monitoring;

import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.http.MediaType;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;

public class JsonMonitoredRequestErrorParser<T extends MonitoredRequestErrorModel> extends AbstractErrorParser<T>
{
	private static final JsonPath CODE_EXPRESSION = JsonPath.compile("$.error.code");
	private static final JsonPath MSG_EXPRESSION = JsonPath.compile("$.error.message.value");

	@Override
	protected Collection<String> getSupportedMediaType()
	{
		return Arrays.asList(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE);
	}

	@Override
	public T parseErrorFrom(Class<T> errorClass, final int statusCode, final String responseBody)
	{
		try
		{
			final DocumentContext jsonPayload = JsonPath.parse(responseBody);
			return error(errorClass, jsonPayload.read(CODE_EXPRESSION, String.class), jsonPayload.read(MSG_EXPRESSION, String.class));
		}
		catch (final JsonPathException e)
		{
			return handleParserException(errorClass, e);
		}
	}
}
