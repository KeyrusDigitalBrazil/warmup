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

import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_ATOM_XML;
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_ATOM_XML_UTF8;
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_XML_UTF8;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import de.hybris.platform.integrationservices.util.HttpStatus;
import de.hybris.platform.integrationservices.util.XmlObject;
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractionException;
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractor;

import java.util.Set;

import org.fest.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link IntegrationKeyExtractor} extracts the integration key value from a XML response
 */
public class XmlIntegrationKeyExtractor implements IntegrationKeyExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlIntegrationKeyExtractor.class);
	private static final String SUCCESS_PATH_EXPRESSION = "//entry//content//properties//integrationKey";
	private static final String ERROR_PATH_EXPRESSION = "//error//innererror";

	private static final Set<String> XML_MEDIA_TYPES = Collections.set(APPLICATION_XML_VALUE, APPLICATION_XML_UTF8, APPLICATION_ATOM_XML, APPLICATION_ATOM_XML_UTF8);

	@Override
	public boolean isApplicable(final String contentType)
	{
		return (XML_MEDIA_TYPES.contains(contentType));
	}

	@Override
	public String extractIntegrationKey(final String responseBody, final int statusCode)
	{
		try
		{
			final XmlObject xml = XmlObject.createFrom(responseBody);
			return xml.get(getPathExpression(statusCode));
		}
		catch (final IllegalArgumentException e)
		{
			LOGGER.error("Failed extracting the integrationKey value from the XML response", e);
			throw new IntegrationKeyExtractionException(e);
		}
	}

	private static String getPathExpression(final int responseStatusCode)
	{
		return HttpStatus.valueOf(responseStatusCode).isError() ? ERROR_PATH_EXPRESSION : SUCCESS_PATH_EXPRESSION;
	}
}
