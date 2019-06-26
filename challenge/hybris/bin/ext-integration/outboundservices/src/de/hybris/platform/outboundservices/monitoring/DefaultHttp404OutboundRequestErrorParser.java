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
package de.hybris.platform.outboundservices.monitoring;

import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.AbstractErrorParser;

import java.util.Collection;
import java.util.Collections;

import org.springframework.http.HttpStatus;

public class DefaultHttp404OutboundRequestErrorParser<T extends MonitoredRequestErrorModel> extends AbstractErrorParser<T>
{
	private static final String HTTP_404_ERROR_CODE = "http_404";
	private static final String HTTP_404_ERROR_MESSAGE = "Destination not found";

	@Override
	public boolean isApplicable(final String contentType, final int statusCode)
	{
		return statusCode == HttpStatus.NOT_FOUND.value();
	}

	@Override
	protected Collection<String> getSupportedMediaType()
	{
		return Collections.emptyList(); // meaning all of them
	}

	@Override
	public T parseErrorFrom(Class<T> errorClass, final int statusCode, final String responseBody)
	{
		try
		{
			return error(errorClass, HTTP_404_ERROR_CODE, HTTP_404_ERROR_MESSAGE);
		}
		catch (final IllegalArgumentException e)
		{
			return handleParserException(errorClass, e);
		}
	}
}
