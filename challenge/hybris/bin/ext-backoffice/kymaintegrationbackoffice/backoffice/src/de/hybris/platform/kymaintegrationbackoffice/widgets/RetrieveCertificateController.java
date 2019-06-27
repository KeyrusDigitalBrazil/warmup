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

package de.hybris.platform.kymaintegrationbackoffice.widgets;

import static de.hybris.platform.kymaintegrationservices.strategies.impl.KymaApiRegistrationStrategy.API_REG_SERVICE_ID;
import static de.hybris.platform.kymaintegrationservices.utils.KymaApiExportHelper.getDestinationId;
import static de.hybris.platform.kymaintegrationservices.utils.KymaHttpHelper.getDefaultHeaders;

import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistrationService;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.kymaintegrationbackoffice.constants.KymaintegrationbackofficeConstants;
import de.hybris.platform.kymaintegrationservices.event.InvalidateCertificateCredentialsCacheEvent;
import de.hybris.platform.kymaintegrationservices.dto.RegisteredDestinationData;
import de.hybris.platform.kymaintegrationservices.services.CertificateService;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;


public class RetrieveCertificateController extends DefaultWidgetController
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(RetrieveCertificateController.class);

	private static final String CERTIFICATE_RETRIEVAL_INVALID_URL = "retrieveCertificate.invalidUrl";
	private static final String CERTIFICATE_RETRIEVAL_SUCCESS = "retrieveCertificate.success";
	private static final String CERTIFICATE_RETRIEVAL_SECURITY_ERROR = "retrieveCertificate.securityError";
	private static final String ALL_DESTINATIONS_REGISTER_SUCCESS = "registerDestination.allSuccess";
	private static final String NO_DESTINATIONS_FOR_TARGET = "registerDestination.noDestinationsForTarget";
	private static final String SOME_DESTINATION_REGISTER_ERROR = "registerDestination.oneOrMore.error";
	private static final String REGISTRATION_FAILED_WEBSERVICES_DELIMITER = ", ";
	private static final String RETRIEVE_CERTIFICATE = "retrieveCertificate";
	private static final String FAIL = "failed";
	private static final String SUCCESS = "completed";

	@Wire
	private Textbox certificateUrl;

	@Resource
	private transient CertificateService certificateService;
	@Resource
	private transient NotificationService notificationService;
	@Resource
	private transient DestinationService<AbstractDestinationModel> destinationService;
	@Resource
	private transient ApiRegistrationService apiRegistrationService;
	@Resource
	private transient ModelService modelService;
	@Resource
	private transient EventService eventService;
	@Resource(name = "kymaDestinationRestTemplateWrapper")
	private transient RestTemplateWrapper kymaDestinationRestTemplateWrapper;
	@Resource(name = "kymaEventRestTemplateWrapper")
	private transient RestTemplateWrapper kymaEventRestTemplateWrapper;
	@Resource(name = "kymaExportJacksonObjectMapper")
	private transient ObjectMapper kymaExportJacksonObjectMapper;


	@SocketEvent(socketId = "existingCertificate")
	public void initializeWithContext(final ConsumedCertificateCredentialModel certificationCredential)
	{
		this.setValue("existingCertificate", certificationCredential);
	}

	@ViewEvent(componentID = "retrieveCertificateButton", eventName = Events.ON_CLICK)
	public void retrieveCertificate()
	{

		final ConsumedCertificateCredentialModel credential = this.getValue("existingCertificate",
				ConsumedCertificateCredentialModel.class);

		final ConsumedCertificateCredentialModel updatedCertificate = updateCertificate(certificateUrl.getValue(), credential);

		if (updatedCertificate != null)
		{
			eventService.publishEvent(new InvalidateCertificateCredentialsCacheEvent(updatedCertificate));

			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS, getLabel(CERTIFICATE_RETRIEVAL_SUCCESS));

			destinationService.getAllDestinations().stream()
					.filter(d -> d.getCredential() instanceof ConsumedCertificateCredentialModel
							&& d.getCredential().getId().equals(credential.getId()))
					.map(d -> d.getDestinationTarget().getId()).collect(Collectors.toSet()).forEach(this::registerExposedDestinations);

			sendOutput(RETRIEVE_CERTIFICATE, SUCCESS);
		}
	}

	private ConsumedCertificateCredentialModel updateCertificate(final String url,
			final ConsumedCertificateCredentialModel credential)
	{
		try
		{
			final URI uri = new URI(url);
			return certificateService.retrieveCertificate(uri, credential);
		}
		catch (final CredentialException e)
		{
			final String errorMessage = getLabel(CERTIFICATE_RETRIEVAL_SECURITY_ERROR) + ' ' + e.getMessage();
			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.FAILURE, errorMessage);
			LOG.error(e);
			sendOutput(RETRIEVE_CERTIFICATE, FAIL);
		}
		catch (final URISyntaxException | IllegalArgumentException e)
		{
			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.FAILURE, getLabel(CERTIFICATE_RETRIEVAL_INVALID_URL, new String[]
					{ certificateUrl.getValue() }));
			LOG.error(e);
			sendOutput(RETRIEVE_CERTIFICATE, FAIL);
		}
		return null;
	}

	private void registerExposedDestinations(final String destinationTargetId)
	{
		final List<String> registrationFailedDestinations = new ArrayList<>();
		final Map<String, String> registeredDestinations;

		try
		{
			registeredDestinations = retrieveRegisteredExposedDestinations().stream()
					.collect(Collectors.toMap(RegisteredDestinationData::getIdentifier, RegisteredDestinationData::getTargetId));
		}
		catch (final ApiRegistrationException e)
		{
			LOG.error("Can not retrieve registered destinations from kyma");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);

			}
			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.FAILURE, e.getMessage());
			return;
		}

		final List<ExposedDestinationModel> exposedDestinations = destinationService
				.getDestinationsByDestinationTargetId(destinationTargetId).stream()
				.filter(destination -> destination instanceof ExposedDestinationModel && destination.isActive())
				.map(ExposedDestinationModel.class::cast).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(exposedDestinations))
		{
			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS, getLabel(NO_DESTINATIONS_FOR_TARGET, new String[]
					{ destinationTargetId }));
			return;
		}

		for (final ExposedDestinationModel exposedDestination : exposedDestinations)
		{
			try
			{
				final String destinationId = getDestinationId(exposedDestination);
				final String targetId = registeredDestinations.get(destinationId);
				// if destination was already registered, just save targetId
				if (targetId != null)
				{
					exposedDestination.setTargetId(targetId);
					modelService.save(exposedDestination);
					registeredDestinations.remove(destinationId);
				}
				// if we have ID on our side only, it's no more valid, new targetID will obtained soon
				else if (exposedDestination.getTargetId() != null)
				{
					exposedDestination.setTargetId(null);
					modelService.save(exposedDestination);
				}
				apiRegistrationService.registerExposedDestination(exposedDestination);
			}
			catch (final ApiRegistrationException e)
			{
				registrationFailedDestinations.add(exposedDestination.getId());
				LOG.error(e);
			}
		}

		deleteRegisteredExposedDestinations(registeredDestinations.values());

		if (registrationFailedDestinations.isEmpty())
		{
			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS, getLabel(ALL_DESTINATIONS_REGISTER_SUCCESS));
		}
		else
		{
			final String errorMessage = getLabel(SOME_DESTINATION_REGISTER_ERROR, new String[]
			{ String.join(REGISTRATION_FAILED_WEBSERVICES_DELIMITER, registrationFailedDestinations) });
			notificationService.notifyUser(getWidgetInstanceManager(), KymaintegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.FAILURE, errorMessage);
		}
	}

	private void deleteRegisteredExposedDestinations(final Collection<String> targetIds)
	{
		targetIds.forEach(targetId -> {
			try
			{
				LOG.warn(String.format(
						"Registered in kyma Destination with targetId: [%s] is unknown from EC side. Deleting the Destination in kyma",
						targetId));
				deleteRegisteredExposedDestinationByTargetId(targetId);
			}
			catch (final ApiRegistrationException e)
			{
				LOG.error(e);
			}
		});
	}

	private void deleteRegisteredExposedDestinationByTargetId(final String targetId) throws ApiRegistrationException
	{
		final AbstractDestinationModel apiDestination = getApiDestination();
		final String url = apiDestination.getUrl() + "/{targetId}";
		final HttpHeaders headers = getDefaultHeaders();
		final ResponseEntity<String> response;
		final HttpEntity request = new HttpEntity<>(headers);

		try
		{
			kymaDestinationRestTemplateWrapper.updateCredentials(apiDestination);
			response = kymaDestinationRestTemplateWrapper.getUpdatedRestTemplate().exchange(url, HttpMethod.DELETE, request,
					String.class, targetId);
			if (response.getStatusCode().series() == HttpStatus.Series.SUCCESSFUL)
			{
				LOG.info(String.format("Deletion of Destination in kyma with targetId [%s] successful", targetId));
			}
			else
			{
				final String errorMessage = String.format(
						"Cannot delete Destination in kyma with targetId: [{%s}]; response status: [{%s}]", targetId,
						response.getStatusCode());
				throw new ApiRegistrationException(errorMessage);
			}
		}
		catch (final HttpClientErrorException e)
		{
			throw new ApiRegistrationException(e.getResponseBodyAsString(), e);
		}
		catch (final RestClientException e)
		{
			throw new ApiRegistrationException(
					String.format("Cannot delete Registered Exposed Destination in kyma with targetId: [{%s}]", targetId), e);
		}
		catch (final CredentialException e)
		{
			LOG.error(e);
			throw new ApiRegistrationException(String.format("Cannot sign request to Kyma; reason: [{%s}]", e.getMessage()));
		}
	}


	private List<RegisteredDestinationData> retrieveRegisteredExposedDestinations() throws ApiRegistrationException
	{
		final AbstractDestinationModel apiDestination = getApiDestination();
		final String url = apiDestination.getUrl();
		final HttpHeaders headers = getDefaultHeaders();
		final ResponseEntity<String> response;
		try
		{
			kymaDestinationRestTemplateWrapper.updateCredentials(apiDestination);
			response = kymaDestinationRestTemplateWrapper.getUpdatedRestTemplate().getForEntity(url, String.class, headers);
			return kymaExportJacksonObjectMapper.readValue(response.getBody(), kymaExportJacksonObjectMapper.getTypeFactory()
					.constructCollectionType(List.class, RegisteredDestinationData.class));
		}
		catch (final HttpClientErrorException e)
		{
			final String errorMessage = e.getResponseBodyAsString();
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage, e);
		}
		catch (final RestClientException e)
		{
			final String errorMessage = String.format("Cannot register Exposed Destination in kyma with URL: [{%s}]", url);
			LOG.error(errorMessage);
			throw new ApiRegistrationException(errorMessage, e);
		}
		catch (final CredentialException e)
		{
			final String errorMessage = String.format("Cannot sign request to Kyma; reason: [{%s}]", e.getMessage());
			LOG.error(errorMessage, e);
			throw new ApiRegistrationException(errorMessage);
		}
		catch (final IOException e)
		{
			final String errorMessage = String
					.format("Cannot parse response for \"get all registered destinations\" request: [{%s}]", e.getMessage());
			LOG.error(errorMessage, e);
			throw new ApiRegistrationException(errorMessage);
		}
	}

	private AbstractDestinationModel getApiDestination() throws ApiRegistrationException
	{
		final String apiDestinationId = Config.getParameter(API_REG_SERVICE_ID);
		final AbstractDestinationModel apiDestination = destinationService.getDestinationById(apiDestinationId);
		if (apiDestination == null)
		{
			throw new ApiRegistrationException(String.format("Cannot find Consumed Destination with id: [{%s}]", apiDestinationId));
		}
		return apiDestination;
	}
}
