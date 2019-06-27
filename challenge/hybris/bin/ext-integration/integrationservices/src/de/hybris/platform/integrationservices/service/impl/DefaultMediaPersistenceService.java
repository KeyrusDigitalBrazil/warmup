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
package de.hybris.platform.integrationservices.service.impl;

import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.integrationservices.service.MediaPersistenceService;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation for persisting media
 */
public class DefaultMediaPersistenceService implements MediaPersistenceService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMediaPersistenceService.class);

	private MediaService mediaService;
	private ModelService modelService;
	private IntegrationServicesConfiguration integrationServicesConfiguration;


	@Override
	public <T extends IntegrationApiMediaModel> List<T> persistMedias(final List<InputStream> payloads, final Class<T> mediaType)
	{
		return payloads.stream()
					   .map(payload -> persistMedia(payload, mediaType))
					   .collect(Collectors.toList());
	}

	protected <T extends IntegrationApiMediaModel> T persistMedia(final InputStream payload, final Class<T> mediaType)
	{
		return payload != null
				? persistNonNullPayload(payload, mediaType)
				: null;
	}

	private <T extends IntegrationApiMediaModel>  T persistNonNullPayload(final InputStream payload, final Class<T> mediaType)
	{
		try
		{
			final String mediaName = generateMediaName();
			LOGGER.debug("Persisting media with name '{}'", mediaName);
			final T media = createMediaModel(mediaName, mediaType);
			getMediaService().setStreamForMedia(media, payload, mediaName, "text/plain");
			return media;
		}
		catch (final MediaIOException | IllegalArgumentException e)
		{
			LOGGER.error("Failed persisting media", e);
			return null;
		}
	}

	protected <T extends IntegrationApiMediaModel> T createMediaModel(final String mediaName, final Class<T> mediaType)
	{
		final T mediaModel = getModelService().create(mediaType);
		mediaModel.setCode(mediaName);
		getModelService().save(mediaModel);
		return mediaModel;
	}

	protected String generateMediaName()
	{
		return getIntegrationServicesConfiguration().getMediaPersistenceMediaNamePrefix() + UUID.randomUUID().toString();
	}

	protected MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected IntegrationServicesConfiguration getIntegrationServicesConfiguration()
	{
		return integrationServicesConfiguration;
	}

	@Required
	public void setIntegrationServicesConfiguration(final IntegrationServicesConfiguration integrationServicesConfiguration)
	{
		this.integrationServicesConfiguration = integrationServicesConfiguration;
	}
}
