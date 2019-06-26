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
package de.hybris.platform.kymaintegrationservices.task;

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.EVENTS_SERVICE_ID;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.EVENT_RETRY_DELAY;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.MAX_CONSECUTIVE_RETRIES;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.MAX_RETRIES;
import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.eventPayloadToString;

import de.hybris.platform.apiregistryservices.event.EventExportFailedEvent;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.kymaintegrationservices.dto.JsonPublishRequestData;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.RestClientException;

import static de.hybris.platform.kymaintegrationservices.utils.KymaHttpHelper.getDefaultHeaders;


/**
 * A Task Runner for Event Emitting
 */
public class KymaEventEmitTaskRunner implements TaskRunner<TaskModel>
{
	private static final Logger LOG = Logger.getLogger(KymaEventEmitTaskRunner.class);

	private RestTemplateWrapper restTemplate;
	private DestinationService<AbstractDestinationModel> destinationService;
	private MessageChannel eventChannel;
	private EventService eventService;
	private Converter<PublishRequestData, JsonPublishRequestData> kymaJsonEventConverter;

	@Override
	public void run(final TaskService taskService, final TaskModel task)
	{
		if (!(task.getContext() instanceof Message) && !(((Message) task.getContext()).getPayload() instanceof PublishRequestData))
		{
			LOG.error("Provided payload is not instance of PublishRequestData");
			return;
		}
		final Message message = (Message) task.getContext();

		final String eventsDestinationId = Config.getParameter(EVENTS_SERVICE_ID);
		final AbstractDestinationModel destination = getDestinationService().getDestinationById(eventsDestinationId);

		if (destination == null || destination.getUrl() == null || destination.getCredential() == null)
		{
			LOG.error("The Destination for exporting events is invalid or missing.");
			return;
		}

		final PublishRequestData publishRequestData = (PublishRequestData) message.getPayload();
		LOG.info(eventPayloadToString(publishRequestData));

		final HttpHeaders headers = getDefaultHeaders();
		final String url = destination.getUrl();

		final JsonPublishRequestData jsonPublishRequestData = getKymaJsonEventConverter().convert(publishRequestData);
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
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e.getMessage(), e);
			}
			LOG.error(String.format("Can not send event to the kyma, event type : %s, cause: %s", publishRequestData.getEventType(), e.getMessage()));
			LOG.info(String.format("Starting consecutive retries to send event to the kyma, event type : %s",
					publishRequestData.getEventType()));

			if (!(e instanceof CredentialException) && retryEventEmitting(request, publishRequestData, url))
			{
				return;
			}

			final Integer retry = (e instanceof CredentialException) ? 1
				: (task.getRetry() + 1) * (Config.getInt(MAX_RETRIES, 3) + 1);

			final String exportEventFailedMessage = String.format(
					"There have already been %d attempt(s) to send the Event (EventId : %s).", retry, publishRequestData.getEventId());
			getEventService().publishEvent(new EventExportFailedEvent(exportEventFailedMessage, retry));

			if (Config.getInt(MAX_RETRIES, 3) <= task.getRetry())
			{
				LOG.info(exportEventFailedMessage + " Maximum of retries is reached, putting Event back to queue");
				getEventChannel().send(message);
				return;
			}

			LOG.info(exportEventFailedMessage + " Maximum of retries is not reached, triggering retry");
			scheduleRetry(retry, publishRequestData);
		}
	}

	@Override
	public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable error)
	{
		LOG.error(String.format("Failed to emit the Event, cause: %s", error.getMessage()));
		if (LOG.isDebugEnabled())
		{
			LOG.debug(error.getMessage(), error);
		}
	}

	protected void scheduleRetry(final Integer retry, final PublishRequestData publishRequestData)
	{
		final RetryLaterException ex = new RetryLaterException(StringUtils.EMPTY); // to trigger the retry
		ex.setDelay(Config.getInt(EVENT_RETRY_DELAY, 5000));
		throw ex;
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
					LOG.debug(e.getMessage(), e);
				}
			}
		}
		return false;
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

	protected MessageChannel getEventChannel()
	{
		return eventChannel;
	}

	@Required
	public void setEventChannel(final MessageChannel eventChannel)
	{
		this.eventChannel = eventChannel;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
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
