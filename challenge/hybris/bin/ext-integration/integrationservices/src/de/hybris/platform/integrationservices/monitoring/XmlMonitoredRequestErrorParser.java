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

import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;
import de.hybris.platform.integrationservices.util.XmlObject;

import java.util.Arrays;
import java.util.Collection;

public class XmlMonitoredRequestErrorParser<T extends MonitoredRequestErrorModel> extends AbstractErrorParser<T>
{
	private static final String CODE_EXPRESSION = "//error//code";
	private static final String MSG_EXPRESSION = "//error//message";

	@Override
	protected Collection<String> getSupportedMediaType()
	{
		return Arrays.asList(APPLICATION_XML_VALUE, APPLICATION_XML_VALUE+";charset=UTF-8");
	}

	@Override
	public T parseErrorFrom(Class<T> errorClass, final int statusCode, final String responseBody)
	{
		try
		{
			final XmlObject xml = XmlObject.createFrom(responseBody);
			final String errorCode = xml.get(CODE_EXPRESSION);
			final String errorMsg = xml.get(MSG_EXPRESSION);
			return error(errorClass, errorCode, errorMsg);
		}
		catch (final IllegalArgumentException e)
		{
			return handleParserException(errorClass, e);
		}
	}
}
