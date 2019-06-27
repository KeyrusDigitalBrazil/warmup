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

package de.hybris.platform.integrationservices.util;

import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A rule for cleaning up media created in the platform as a side effect of a test
 */
public abstract class RequestPersistenceContext extends BaseContext
{
	private static final Logger LOG = LoggerFactory.getLogger(RequestPersistenceContext.class);
	private static final String MEDIA_QUERY = "SELECT {" + IntegrationApiMediaModel.PK + "} " +
			"FROM {" + IntegrationApiMediaModel._TYPECODE + "*} " +
			"WHERE {code} LIKE CONCAT(?code,'%')";

	private String mediaNamePrefix;
	private MediaService mediaService;
	private ConfigurationService configurationService;

	@Override
	public void before()
	{
		super.before();
		mediaService = getService("mediaService", MediaService.class);

		configurationService = getService("configurationService", ConfigurationService.class);
		final IntegrationServicesConfiguration integrationServicesConfiguration = getService("integrationServicesConfiguration", IntegrationServicesConfiguration.class);
		setMediaNamePrefix(integrationServicesConfiguration.getMediaPersistenceMediaNamePrefix());
	}

	@Override
	protected void after()
	{
		try
		{
			modelService().removeAll(getAllMedia());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Failed to clean up media", e);
		}
	}

	public List<IntegrationApiMediaModel> getAllMedia()
	{
		final SearchResult<IntegrationApiMediaModel> query = flexibleSearch().search(MEDIA_QUERY, Collections.singletonMap("code", mediaNamePrefix));
		return query.getResult();
	}

	public String getMediaContentAsString(final IntegrationApiMediaModel model) throws IOException
	{
		final InputStream mediaContent = mediaService.getStreamFromMedia(model);
		return IOUtils.toString(mediaContent, StandardCharsets.UTF_8.name());
	}

	public void turnMonitoringOff()
	{
		configurationService.getConfiguration().setProperty(getMonitoringProperty(), Boolean.FALSE.toString());
	}

	private void setMediaNamePrefix(final String prefix)
	{
		mediaNamePrefix = prefix;
	}

	protected abstract String getMonitoringProperty();
}
