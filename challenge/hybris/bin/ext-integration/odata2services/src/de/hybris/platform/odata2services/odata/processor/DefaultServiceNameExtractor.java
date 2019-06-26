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
package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;

import java.net.URI;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the {@link ServiceNameExtractor}
 */
public class DefaultServiceNameExtractor implements ServiceNameExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultServiceNameExtractor.class);

	@Override
	public String extract(final ODataContext context, final String integrationKey)
	{
		try
		{
			final URI serviceRoot = context.getPathInfo().getServiceRoot();
			if (serviceRoot == null || serviceRoot.getPath() == null)
			{
				throw new InternalProcessingException("Service Name was not found.", integrationKey);
			}
			String path = serviceRoot.getPath();
			if (path.endsWith("/"))
			{
				path = path.substring(0, path.length() - 1);
			}
			return path.substring(path.lastIndexOf('/') + 1);
		}
		catch (final ODataException e)
		{
			LOGGER.warn("Failed to extract the service name from the context");
			throw new InternalProcessingException(e);
		}
	}
}
