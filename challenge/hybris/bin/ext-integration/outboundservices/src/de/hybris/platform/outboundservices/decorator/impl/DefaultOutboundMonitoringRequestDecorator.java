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
package de.hybris.platform.outboundservices.decorator.impl;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME;
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.SAP_PASSPORT_HEADER_NAME;
import static de.hybris.platform.outboundservices.constants.OutboundservicesConstants.OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME;

import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.outboundservices.monitoring.OutboundMonitoringException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class DefaultOutboundMonitoringRequestDecorator implements OutboundRequestDecorator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOutboundMonitoringRequestDecorator.class);

	private ModelService modelService;
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> exceptionErrorParser;

	@Override
	public HttpEntity<Map<String, Object>> decorate(final HttpHeaders httpHeaders, final Map<String, Object> payload,
													final DecoratorContext context, final DecoratorExecution execution)
	{
		final String messageId = UUID.randomUUID().toString();
		httpHeaders.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, messageId);

		final HttpEntity<Map<String, Object>> httpEntity =
				extractHttpEntityFrom(httpHeaders, payload, context, execution);

		final String sapPassport = extractSapPassportFrom(httpEntity, context);

		final String integrationKey = extractIntegrationKey(httpEntity);

		saveOutboundRequest(messageId, sapPassport, integrationKey, IntegrationRequestStatus.ERROR, context);

		return httpEntity;
	}

	protected String extractSapPassportFrom(final HttpEntity<Map<String, Object>> httpEntity, final DecoratorContext context)
	{
		final OutboundRequestModel outboundRequestModel;
		try
		{
			final List<String> list = httpEntity.getHeaders().get(SAP_PASSPORT_HEADER_NAME);
			if( list == null || list.isEmpty() || StringUtils.isEmpty(list.get(0)) )
			{
				throw new OutboundMonitoringException(String.format("No %s header present in request.", SAP_PASSPORT_HEADER_NAME));
			}

			return list.get(0);
		}
		catch(final OutboundMonitoringException e)
		{
			final String messageId = httpEntity.getHeaders().getFirst(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME);
			final String integrationKey = extractIntegrationKey(httpEntity);

			outboundRequestModel =
					saveOutboundRequest(messageId, null, integrationKey, IntegrationRequestStatus.ERROR, context);

			updateOutboundRequestWithException(outboundRequestModel,e);
			throw e;
		}
	}

	protected HttpEntity<Map<String, Object>> extractHttpEntityFrom(final HttpHeaders httpHeaders,
			final Map<String, Object> payload, final DecoratorContext context, final DecoratorExecution execution)
	{
		// because of the decorator framework, after getting the value from the execution chain, the SAP-PASSPORT
		// and other headers should be present.
		try
		{
			return execution.createHttpEntity(httpHeaders, payload, context);
		}
		catch (final RuntimeException e)
		{
			final String messageId = httpHeaders.getFirst(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME);
			final String sapPassport = httpHeaders.getFirst(SAP_PASSPORT_HEADER_NAME);
			final String integrationKey = extractIntegrationKey(new HttpEntity<>(payload));

			final OutboundRequestModel outboundRequestModel =
			saveOutboundRequest(messageId, sapPassport, integrationKey, IntegrationRequestStatus.ERROR, context);

			updateOutboundRequestWithException(outboundRequestModel, e);
			throw e;
		}
	}

	protected void updateOutboundRequestWithException(final OutboundRequestModel outboundRequestModel, final Throwable t)
	{
		outboundRequestModel.setStatus(IntegrationRequestStatus.ERROR);
		outboundRequestModel.setError(extractError(t));
		getModelService().save(outboundRequestModel);
	}

	protected String extractError(final Throwable t)
	{
		return getExceptionErrorParser().parseErrorFrom(MonitoredRequestErrorModel.class, -1, t.getMessage()).getMessage();
	}

	protected OutboundRequestModel saveOutboundRequest(final String messageId, final String sapPassport,
													   final String integrationKey, final IntegrationRequestStatus status,
													   final DecoratorContext context)
	{
		final OutboundRequestModel outboundRequestModel = getModelService().create(OutboundRequestModel.class);
		outboundRequestModel.setMessageId(messageId);
		outboundRequestModel.setDestination(context.getDestinationModel().getUrl());
		outboundRequestModel.setSapPassport(sapPassport);
		outboundRequestModel.setIntegrationKey(integrationKey);
		outboundRequestModel.setStatus(status);
		outboundRequestModel.setType(context.getIntegrationObjectCode());
		getModelService().save(outboundRequestModel);
		return outboundRequestModel;
	}

	protected String extractIntegrationKey(final HttpEntity<Map<String, Object>> httpEntity)
	{
		final Map<String, Object> payload = httpEntity.getBody();
		if( ! payload.containsKey(INTEGRATION_KEY_PROPERTY_NAME) ||
				StringUtils.isEmpty((String) payload.get(INTEGRATION_KEY_PROPERTY_NAME)))
		{
			LOGGER.warn("No integrationKey was present in payload.");
			return null;
		}
		return (String) payload.get(INTEGRATION_KEY_PROPERTY_NAME);
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

	protected MonitoredRequestErrorParser<MonitoredRequestErrorModel> getExceptionErrorParser()
	{
		return exceptionErrorParser;
	}

	@Required
	public void setExceptionErrorParser(final MonitoredRequestErrorParser<MonitoredRequestErrorModel> exceptionErrorParser)
	{
		this.exceptionErrorParser = exceptionErrorParser;
	}
}
