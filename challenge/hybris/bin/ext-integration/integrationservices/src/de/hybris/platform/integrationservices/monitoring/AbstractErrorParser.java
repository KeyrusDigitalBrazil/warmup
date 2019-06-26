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
import de.hybris.platform.integrationservices.util.HttpStatus;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public abstract class AbstractErrorParser<T extends MonitoredRequestErrorModel> implements MonitoredRequestErrorParser<T>
{
	private static final String UNKNOWN_ERROR_CODE = "unknown_error";
	private static final String UNKNOWN_ERROR_MSG = "Please check the log for details.";
	private static final Logger LOG = LoggerFactory.getLogger(AbstractErrorParser.class);

	@Override
	public boolean isApplicable(final String contentType, final int statusCode)
	{
		return hasErrorStatusCode(statusCode) &&
				getSupportedMediaType().contains(contentType);
	}

	protected T error(final Class<T> errorClass, final String code, final String message)
	{
		Preconditions.checkArgument(errorClass != null, "Error cannot be null");
		final T error = createInstance(errorClass);
		error.setCode(StringUtils.isNotBlank(code) ? StringUtils.abbreviate(code, 255) : UNKNOWN_ERROR_CODE);
		error.setMessage(StringUtils.isNotBlank(message) ? StringUtils.abbreviate(message, 255) : UNKNOWN_ERROR_MSG);
		return error;
	}

	protected T createInstance(final Class<T> klazz)
	{
		try
		{
			return klazz.newInstance();
		}
		catch (final IllegalAccessException | InstantiationException e)
		{
			throw new MonitoredRequestException(e);
		}
	}

	protected T handleParserException(final Class<T> errorClass, final Exception e)
	{
		LOG.error("An exception occurred while parsing the error", e);
		return error(errorClass, UNKNOWN_ERROR_CODE, UNKNOWN_ERROR_MSG);
	}
	
	protected boolean hasErrorStatusCode(final int code)
	{
		return HttpStatus.valueOf(code).isError();
	}
	
	protected abstract Collection<String> getSupportedMediaType();
}
