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

import org.apache.commons.lang3.StringUtils;

public class DefaultExceptionOutboundRequestErrorParser<T extends MonitoredRequestErrorModel> extends AbstractErrorParser<T>
{
	private static final String ERROR_CODE = "client_error";
	private static final String ERROR_MESSAGE = "%s";

	@Override
	public boolean isApplicable(final String contentType, final int statusCode)
	{
		return true;
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
			return error(errorClass, ERROR_CODE, StringUtils.abbreviate(String.format(ERROR_MESSAGE, responseBody), 255));
		}
		catch (final IllegalArgumentException e)
		{
			return handleParserException(errorClass, e);
		}
	}
}
