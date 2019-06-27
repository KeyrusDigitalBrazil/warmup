/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.kymaintegrationservices.strategies.impl;

import static de.hybris.platform.kymaintegrationservices.utils.KymaHttpHelper.getDefaultHeaders;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.apiregistryservices.dao.EventConfigurationDao;
import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.exceptions.DestinationNotFoundException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.strategies.ApiRegistrationStrategy;
import de.hybris.platform.kymaintegrationservices.dto.ApiRegistrationResponseData;
import de.hybris.platform.kymaintegrationservices.dto.EventsSpecificationSourceData;
import de.hybris.platform.kymaintegrationservices.dto.ServiceRegistrationData;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Kyma impl of @{@link ApiRegistrationStrategy}
 */
public class KymaApiRegistrationStrategy implements ApiRegistrationStrategy
{
	public static final String API_REG_SERVICE_ID = "kymaintegrationservices.kyma_api_reg_service_id";
	public static final String EVENTS_ED_ID = "kymaintegrationservices.events.exposedDestinationId";
	public static final String DESTINATION_NOT_FOUND = "kymaintegrationservices.destination_not_found.message.format";

	private static final Logger LOG = LoggerFactory.getLogger(KymaApiRegistrationStrategy.class);
	private static final String CREDENTIAL_ERROR_MESSAGE = "Cannot sign request to Kyma; reason: [{%s}]";
	private Converter<ExposedDestinationModel, ServiceRegistrationData> webserviceSpecificationConverter;
	private Converter<EventsSpecificationSourceData, ServiceRegistrationData> eventsSpecificationConverter;
	private ObjectMapper jacksonObjectMapper;
	private RestTemplateWrapper restTemplate;
	private ConfigurationService configurationService;
	private ModelService modelService;

	private EventConfigurationDao eventConfigurationDao;
	private DestinationService<AbstractDestinationModel> destinationService;

	@Override
	public void registerExposedDestination(final ExposedDestinationModel exposedDestination) throws ApiRegistrationException
	{
		final ServiceRegistrationData serviceRegistrationData;

		if (exposedDestination.getId().equals(Config.getParameter(EVENTS_ED_ID)))
		{ // special logic for event registration only
			final EventsSpecificationSourceData eventSpecificationSourceData = new EventsSpecificationSourceData();
			eventSpecificationSourceData.setExposedDestination(exposedDestination);
			eventSpecificationSourceData.setEvents(getEventConfigurationDao()
					.findActiveEventConfigsByDestinationTargetId(exposedDestination.getDestinationTarget().getId()));
			serviceRegistrationData = getEventsSpecificationConverter().convert(eventSpecificationSourceData);
		}
		else
		{
			try
			{
				serviceRegistrationData = getWebserviceSpecificationConverter().convert(exposedDestination);
			}
			catch (final ConversionException e)
			{
				throw new ApiRegistrationException(e.getMessage(), e);
			}
		}

		final HttpHeaders headers = getDefaultHeaders();
		final HttpEntity<String> request = new HttpEntity(serviceRegistrationData, headers);


		if (LOG.isInfoEnabled())
		{
			LOG.info("Sending Exposed Destination '{}' to the kyma", serviceRegistrationData.getIdentifier());
		}

		if (StringUtils.isBlank(exposedDestination.getTargetId()))
		{
			registerNewSpecAtKyma(serviceRegistrationData, exposedDestination, request);
		}
		else
		{
			updateExistingSpecAtKyma(serviceRegistrationData, exposedDestination, request);
		}

	}

	@Override
	public void unregisterExposedDestination(final ExposedDestinationModel destination) throws ApiRegistrationException
	{
		final HttpHeaders headers = getDefaultHeaders();
		final HttpEntity<String> request = new HttpEntity<>(headers);

		LOG.info("Sending Exposed Destination '{}' to the kyma", destination.getId());

		final AbstractDestinationModel apiDestination = getApiDestination();
		final String url = apiDestination.getUrl() + "/{serviceId}";
		final String serviceId = destination.getTargetId();
		try
		{
			getRestTemplate().updateCredentials(apiDestination);

			final ResponseEntity<String> response = getRestTemplate().getUpdatedRestTemplate().exchange(url, HttpMethod.DELETE,
					request, String.class, serviceId);

			if (response.getStatusCode().series() == HttpStatus.Series.SUCCESSFUL)
			{
				LOG.info("Unregistration of Destination  [{}] successful response=[{}]", destination.getId(), response);
				updateApiConfigurationUid(null, destination);
			}
			else
			{
				final String errorMessage = String.format(
						"Failed to unregister Exposed Destination in kyma with URL: [{%s}]; response status: [{%s}]", url,
						response.getStatusCode());
				LOG.error(errorMessage);
				throw new ApiRegistrationException(errorMessage);
			}
		}
		catch (final HttpClientErrorException e)
		{
			final String errorMessage = e.getResponseBodyAsString();
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage, e);
		}
		catch (final RestClientException e)
		{
			final String errorMessage = String.format("Failed to unregister Exposed Destination in kyma with URL: [{%s}]", url);
			LOG.error(errorMessage, e);
			throw new ApiRegistrationException(errorMessage, e);
		}
		catch (final CredentialException e)
		{
			final String errorMessage = String.format(CREDENTIAL_ERROR_MESSAGE, e.getMessage());
			LOG.error(errorMessage, e);
			throw new ApiRegistrationException(errorMessage);
		}
	}

	protected void registerNewSpecAtKyma(final ServiceRegistrationData serviceRegistrationData,
			final ExposedDestinationModel exposedDestination, final HttpEntity<String> request) throws ApiRegistrationException
	{

		final AbstractDestinationModel apiDestination = getApiDestination();

		final String url = apiDestination.getUrl();

		try
		{
			getRestTemplate().updateCredentials(apiDestination);
		}
		catch (final CredentialException e)
		{
			final String errorMessage = String.format(CREDENTIAL_ERROR_MESSAGE, e.getMessage());
			LOG.error(errorMessage, e);
			throw new ApiRegistrationException(errorMessage);
		}

		final ResponseEntity<String> response;
		try
		{
			response = getRestTemplate().getUpdatedRestTemplate().postForEntity(url, request, String.class);
		}
		catch (final HttpClientErrorException e)
		{
			final String errorMessage = e.getResponseBodyAsString();
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage, e);
		}
		catch (final RestClientException e)
		{
			final String errorMessage = String.format("Failed to register Exposed Destination in kyma with URL: [{%s}]", url);
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage, e);
		}


		if (response.getStatusCode().series() == HttpStatus.Series.SUCCESSFUL)
		{
			LOG.info("Registration of Exposed Destination '{}' successful, response={}", serviceRegistrationData.getIdentifier(),
					response);

			final String serviceId = extractServiceIdFromResponseBody(response.getBody());

			if (StringUtils.isNotBlank(serviceId))
			{
				updateApiConfigurationUid(serviceId, exposedDestination);
			}
		}
		else
		{
			final String errorMessage = String.format(
					"Failed to register Exposed Destination in kyma with URL: [{%s}]; response status: [{%s}]", url,
					response.getStatusCode());
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage);
		}
	}

	protected AbstractDestinationModel getApiDestination() throws ApiRegistrationException
	{
		final String apiDestinationId = Config.getParameter(API_REG_SERVICE_ID);
		final AbstractDestinationModel apiDestination = getDestinationService().getDestinationById(apiDestinationId);
		if (apiDestination == null)
		{
			throw new ApiRegistrationException(
					String.format("Missed Services Consumed Destination with id: [{%s}]", apiDestinationId));
		}
		return apiDestination;
	}

	protected void updateExistingSpecAtKyma(final ServiceRegistrationData serviceRegistrationData,
			final ExposedDestinationModel apiConfiguration, final HttpEntity<String> request) throws ApiRegistrationException
	{

		final AbstractDestinationModel apiDestination = getApiDestination();
		try
		{
			getRestTemplate().updateCredentials(apiDestination);
		}
		catch (final CredentialException e)
		{
			final String errorMessage = String.format(CREDENTIAL_ERROR_MESSAGE, e.getMessage());
			LOG.error(errorMessage, e);
			throw new ApiRegistrationException(errorMessage);
		}

		final String url = apiDestination.getUrl() + "/{serviceId}";
		final String serviceId = apiConfiguration.getTargetId();
		final ResponseEntity<String> response;
		try
		{
			response = getRestTemplate().getUpdatedRestTemplate().exchange(url, HttpMethod.PUT, request, String.class, serviceId);
		}
		catch (final HttpClientErrorException e)
		{
			final String messageFormat = Config.getString(DESTINATION_NOT_FOUND, "service with ID '%s' has no API");
			final String serviceNotFoundErrorMessage = String.format(messageFormat, serviceId);
			if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())
					&& serviceNotFoundErrorMessage.equals(e.getResponseBodyAsString().trim()))
			{
				final String errorMessage = String.format(
						"Failed to update registered Exposed Destination with id: %s. There is no targetId: %s in kyma",
						apiConfiguration.getId(), serviceId);
				LOG.error(errorMessage);
				throw new DestinationNotFoundException(errorMessage, e);
			}
			else
			{
				final String errorMessage = e.getResponseBodyAsString();
				LOG.error(errorMessage);
				throw new ApiRegistrationException(errorMessage, e);
			}
		}
		catch (final RestClientException e)
		{
			final String errorMessage = String.format("Failed to register Exposed Destination in kyma with URL: [{%s}]", url);
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage, e);
		}

		if (response.getStatusCode().series() == HttpStatus.Series.SUCCESSFUL)
		{
			LOG.info("Update of Exposed Destination '{}' succeskymaul, response={}", serviceRegistrationData.getIdentifier(),
					response);
		}
		else
		{
			final String errorMessage = String.format(
					"Failed to register Exposed Destination in kyma with URL: [{%s}]; response status: [{%s}]", url,
					response.getStatusCode());
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage);
		}
	}


	protected String extractServiceIdFromResponseBody(final String responseBody)
	{
		if (StringUtils.isNotBlank(responseBody))
		{
			try
			{
				final ApiRegistrationResponseData registrationResponse = getJacksonObjectMapper().readValue(responseBody,
						ApiRegistrationResponseData.class);
				return registrationResponse.getId();
			}
			catch (final IOException e)
			{
				LOG.error("Cannot deserialize Json response body from kyma; body: {}", responseBody, e);
			}
		}

		LOG.error("responseBody is empty - body: {}", responseBody);
		return StringUtils.EMPTY;
	}

	protected void updateApiConfigurationUid(final String systemId, final ExposedDestinationModel destination)
	{
		validateParameterNotNull(destination, "Exposed Destination model cannot be null");

		destination.setTargetId(systemId);
		getModelService().save(destination);
	}

	protected ObjectMapper getJacksonObjectMapper()
	{
		return jacksonObjectMapper;
	}

	@Required
	public void setJacksonObjectMapper(final ObjectMapper jacksonObjectMapper)
	{
		this.jacksonObjectMapper = jacksonObjectMapper;
	}

	protected RestTemplateWrapper getRestTemplate()
	{
		return restTemplate;
	}

	@Required
	public void setRestTemplate(final RestTemplateWrapper restTemplate)
	{
		this.restTemplate = restTemplate;
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

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Converter<ExposedDestinationModel, ServiceRegistrationData> getWebserviceSpecificationConverter()
	{
		return webserviceSpecificationConverter;
	}

	@Required
	public void setWebserviceSpecificationConverter(
			final Converter<ExposedDestinationModel, ServiceRegistrationData> webserviceSpecificationConverter)
	{
		this.webserviceSpecificationConverter = webserviceSpecificationConverter;
	}

	protected Converter<EventsSpecificationSourceData, ServiceRegistrationData> getEventsSpecificationConverter()
	{
		return eventsSpecificationConverter;
	}

	@Required
	public void setEventsSpecificationConverter(
			final Converter<EventsSpecificationSourceData, ServiceRegistrationData> eventsSpecificationConverter)
	{
		this.eventsSpecificationConverter = eventsSpecificationConverter;
	}

	protected EventConfigurationDao getEventConfigurationDao()
	{
		return eventConfigurationDao;
	}

	public void setEventConfigurationDao(final EventConfigurationDao eventConfigurationDao)
	{
		this.eventConfigurationDao = eventConfigurationDao;
	}

	protected DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}
}
