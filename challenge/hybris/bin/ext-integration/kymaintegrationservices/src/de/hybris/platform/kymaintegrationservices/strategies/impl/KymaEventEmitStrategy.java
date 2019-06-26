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

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DATE_FORMAT_PROP;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DEFAULT_DATE_FORMAT;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.DEFAULT_VALIDATION_ERROR;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.EVENTS_SERVICE_ID;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.MAX_CONSECUTIVE_RETRIES;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.VALIDATION_ERROR_KEY;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.eventPayloadToString;

import de.hybris.platform.apiregistryservices.dto.EventExportDeadLetterData;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.services.EventDlqService;
import de.hybris.platform.apiregistryservices.strategies.EventEmitStrategy;
import de.hybris.platform.kymaintegrationservices.dto.JsonPublishRequestData;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static de.hybris.platform.kymaintegrationservices.utils.KymaHttpHelper.getDefaultHeaders;


/**
 * Implementation of @{@link EventEmitStrategy} for kyma event emitting with retries
 */
public class KymaEventEmitStrategy implements EventEmitStrategy
{

	private static final Logger LOG = Logger.getLogger(KymaEventEmitStrategy.class);

	private RestTemplateWrapper restTemplate;
	private DestinationService<AbstractDestinationModel> destinationService;
	private EventDlqService eventDlqService;
	private ObjectMapper jacksonObjectMapper;
	private MessageChannel eventChannel;
	private ModelService modelService;
	private TaskService taskService;
	private Converter<PublishRequestData, JsonPublishRequestData> kymaJsonEventConverter;

	@Override
	public void sendEvent(final Object object)
	{
		if (!(object instanceof Message) || !(((Message) object).getPayload() instanceof PublishRequestData))
		{
			LOG.error("Provided payload is not instance of PublishRequestData");
			return;
		}

		final AbstractDestinationModel destination = getEventDestination();

		if (destination == null || StringUtils.isEmpty(destination.getUrl()) || destination.getCredential() == null)
		{
			LOG.error("The Destination for exporting events is invalid or missing.");
			return;
		}

		final Message message = (Message) object;
		final PublishRequestData publishRequestData = (PublishRequestData) message.getPayload();
		LOG.info(eventPayloadToString(publishRequestData));

		final JsonPublishRequestData jsonPublishRequestData = getKymaJsonEventConverter().convert(publishRequestData);

		final HttpHeaders headers = getDefaultHeaders();
		final String url = destination.getUrl();
		final HttpEntity<String> request = new HttpEntity(jsonPublishRequestData, headers);

		try
		{
			getRestTemplate().updateCredentials(destination);

			final ResponseEntity<String> response = getRestTemplate().getUpdatedRestTemplate().postForEntity(url, request,
					String.class);

			LOG.info(String.format("Event (EventId : %s) sending response : %s", publishRequestData.getEventId(), response));
		}
		catch (final RestClientException | CredentialException e)
		{
			LOG.error(String.format("Can not send event to the kyma, event type : %s, cause: %s",
                  publishRequestData.getEventType(), e.getMessage()));

			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}

			if (shouldSendToDlq(e))
			{
				sendToDlq(publishRequestData, destination.getDestinationTarget(),
						((HttpClientErrorException) e).getResponseBodyAsString());
				return;
			}

			if (!(e instanceof CredentialException) && retryEventEmitting(request, publishRequestData, url))
			{
				return;
			}

			LOG.info(String.format("Scheduling EventEmit task, Event (EventId : %s)", publishRequestData.getEventId()));
			final TaskModel task = getModelService().create(TaskModel.class);
			task.setRunnerBean("kymaEventEmitTaskRunner");
			task.setContext(message);
			getTaskService().scheduleTask(task);
		}
	}

	protected boolean retryEventEmitting(final HttpEntity<String> request, final PublishRequestData publishRequestData,
			final String url)
	{
		final int maxConsecutiveRetries = Config.getInt(MAX_CONSECUTIVE_RETRIES, 3);
		for (int i = 0; i < maxConsecutiveRetries; i++)
		{
			try
			{
				final ResponseEntity<String> response = getRestTemplate().getUpdatedRestTemplate().postForEntity(url, request,
						String.class);
				LOG.info(String.format("Consecutive retry (Number : %d), Event (EventId : %s) sending response : %s", i,
						publishRequestData.getEventId(), response));
				return true;
			}
			catch (final RestClientException e)
			{
				LOG.error(String.format("Consecutive retry failed (Number : %d), Event (EventId : %s), cause: %s", i,
						publishRequestData.getEventId(), e.getMessage()));
                if (LOG.isDebugEnabled())
                {
                    LOG.debug(e);
                }
			}
		}
		return false;
	}

	protected boolean shouldSendToDlq(final Exception e)
	{
		if (!(e instanceof HttpClientErrorException))
		{
			return false;
		}
		LOG.info(((HttpClientErrorException) e).getResponseBodyAsString());
		final String validationErrorType = Config.getString(VALIDATION_ERROR_KEY, DEFAULT_VALIDATION_ERROR);
		final String errorType;
		try
		{
			errorType = getJacksonObjectMapper().readTree(((HttpClientErrorException) e).getResponseBodyAsString()).get("type")
					.asText();
		}
		catch (final IOException e1)
		{
			LOG.info(String.format("Error does not have message type. Details: %s", e1.getMessage()));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e1);
			}
			return false;
		}

		return HttpStatus.BAD_REQUEST.equals(((HttpClientErrorException) e).getStatusCode())
				&& validationErrorType.equals(errorType);
	}

	protected void sendToDlq(final PublishRequestData publishRequestData, final DestinationTargetModel destinationTarget,
			final String error)
	{

		final EventExportDeadLetterData deadLetterData = new EventExportDeadLetterData();
		deadLetterData.setDestinationTarget(destinationTarget);
		deadLetterData.setError(error);
		deadLetterData.setEventType(publishRequestData.getEventType());
		deadLetterData.setId(publishRequestData.getEventId());

		try
		{
			final String requestBody = getJacksonObjectMapper().writeValueAsString(publishRequestData);
			deadLetterData.setPayload(requestBody);
			final String dateFormat = Config.getString(DATE_FORMAT_PROP, DEFAULT_DATE_FORMAT);
			deadLetterData.setTimestamp(new SimpleDateFormat(dateFormat).parse(publishRequestData.getEventTime()));
		}
		catch (final JsonProcessingException e1)
		{
			LOG.error(String.format("Cannot parse event export requestBody, cause: %s", e1.getMessage()));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e1);
			}
			return;
		}
		catch (final ParseException e2)
		{
			LOG.error(String.format("Cannot parse event time, cause: %s", e2));
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e2);
			}
			return;
		}

		LOG.info(String.format("Sending EventExportDeadLetter to queue. EventId : %s", publishRequestData.getEventId()));
		getEventDlqService().sendToQueue(deadLetterData);
	}

	protected AbstractDestinationModel getEventDestination()
	{
		final String eventsDestinationId = Config.getParameter(EVENTS_SERVICE_ID);
		final AbstractDestinationModel destination = getDestinationService().getDestinationById(eventsDestinationId);
		getModelService().refresh(destination);
		return destination;
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

	protected TaskService getTaskService()
	{
		return taskService;
	}

	@Required
	public void setTaskService(final TaskService taskService)
	{
		this.taskService = taskService;
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

	protected DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}

	protected EventDlqService getEventDlqService()
	{
		return eventDlqService;
	}

	@Required
	public void setEventDlqService(final EventDlqService eventDlqService)
	{
		this.eventDlqService = eventDlqService;
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

	protected MessageChannel getEventChannel()
	{
		return eventChannel;
	}

	@Required
	public void setEventChannel(final MessageChannel eventChannel)
	{
		this.eventChannel = eventChannel;
	}

	protected Converter<PublishRequestData, JsonPublishRequestData> getKymaJsonEventConverter()
	{
		return kymaJsonEventConverter;
	}

	@Required
	public void setKymaJsonEventConverter(final Converter<PublishRequestData, JsonPublishRequestData> kymaJsonEventConverter)
	{
		this.kymaJsonEventConverter = kymaJsonEventConverter;
	}
}
