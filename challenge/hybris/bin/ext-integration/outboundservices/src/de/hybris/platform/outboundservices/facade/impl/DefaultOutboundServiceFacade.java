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

package de.hybris.platform.outboundservices.facade.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.DefaultDecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import rx.Observable;

/**
 * Default implementation of OutboundServiceFacade.
 */
public class DefaultOutboundServiceFacade implements OutboundServiceFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOutboundServiceFacade.class);

	private DestinationService<ConsumedDestinationModel> destinationService;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private ClientHttpRequestInterceptor monitoringInterceptor;
	private IntegrationObjectService integrationObjectService;
	private List<OutboundRequestDecorator> decorators;
	private OutboundServicesConfiguration outboundServicesConfiguration;
	private OutboundRequestDecorator monitoringDecorator;

	@Override
	public Observable<ResponseEntity<Map>> send(final ItemModel itemModel, final String integrationObjectCode,
			final String destinationId)
	{
		Preconditions.checkArgument(itemModel != null, "itemModel cannot be null");
		Preconditions.checkArgument(StringUtils.isNotBlank(integrationObjectCode),
				"integrationObjectCode cannot be null or empty");
		Preconditions.checkArgument(StringUtils.isNotBlank(destinationId), "destination cannot be null or empty");

		return orchestrate(itemModel, integrationObjectCode, destinationId);
	}

	protected Observable<ResponseEntity<Map>> orchestrate(final ItemModel itemModel, final String integrationObjectCode,
			final String destinationId)
	{
		final ConsumedDestinationModel destinationModel = getDestinationService().getDestinationById(destinationId);
		if( destinationModel == null )
		{
			throw new ModelNotFoundException("Provided destination was not found.");
		}

		final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
		final RestTemplate restTemplate = obtainRestTemplate(restOperations);

		addMonitoringInterceptor(restTemplate);

		// The payload is merged later on by DefaultPayloadBuildingRequestDecorator. This way, exceptions can be
		// handled by the Monitoring Decorator.
		final Map<String, Object> payload = Maps.newHashMap();

		return createObservable(restOperations, itemModel, integrationObjectCode, destinationModel, payload);
	}

	protected void addMonitoringInterceptor(final RestTemplate restTemplate)
	{
		if (getOutboundServicesConfiguration().isMonitoringEnabled())
		{
			final List<ClientHttpRequestInterceptor> interceptors = Lists.newArrayList(restTemplate.getInterceptors());
			interceptors.add(getMonitoringInterceptor());
			restTemplate.setInterceptors(interceptors);
		}
	}

	protected List<OutboundRequestDecorator> addMonitoringDecorator(final List<OutboundRequestDecorator> requestDecorators)
	{
		if (getOutboundServicesConfiguration().isMonitoringEnabled())
		{
			final List<OutboundRequestDecorator> list = Lists.newArrayList(getMonitoringDecorator());
			list.addAll(requestDecorators);
			return list;
		}
		return requestDecorators;
	}

	protected RestTemplate obtainRestTemplate(final RestOperations restOperations)
	{
		return (RestTemplate)restOperations;
	}

	protected Observable<ResponseEntity<Map>> createObservable(final RestOperations restOperations,
															   final ItemModel itemModel,
															   final String integrationObjectCode,
															   final ConsumedDestinationModel destinationModel,
															   final Map<String, Object> payload)
	{
		return Observable.just(restOperations).map(restTemplate -> {
			final HttpEntity<Map<String, Object>> entity =
					createHttpEntity(itemModel, integrationObjectCode, destinationModel, payload);
			return restTemplate.postForEntity(destinationModel.getUrl(), entity, Map.class);
		});
	}

	protected HttpEntity<Map<String, Object>> createHttpEntity(final ItemModel itemModel,
															   final String integrationObjectCode,
															   final ConsumedDestinationModel destinationModel,
															   final Map<String, Object> payload)
	{
		final String integrationObjectItemCode =
				findIntegrationObjectItemCode(integrationObjectCode, itemModel);

		final HttpHeaders httpHeaders = new HttpHeaders();
		final DecoratorContext context = DecoratorContext.decoratorContextBuilder()
														 .withDestinationModel(destinationModel)
														 .withIntegrationObjectCode(integrationObjectCode)
														 .withIntegrationObjectItemCode(integrationObjectItemCode)
														 .withItemModel(itemModel)
														 .build();

		final List<OutboundRequestDecorator> requestDecorators = addMonitoringDecorator(getOutboundRequestDecorators());
		final DecoratorExecution execution = new DefaultDecoratorExecution(requestDecorators.iterator());

		return execution.createHttpEntity(httpHeaders, payload, context);
	}

	protected DestinationService<ConsumedDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	protected String findIntegrationObjectItemCode(final String integrationObjectCode, final ItemModel itemModel)
	{
		try
		{
			return getIntegrationObjectService().findIntegrationObjectItemByTypeCode(integrationObjectCode, itemModel.getItemtype()).getCode();
		}
		catch(final ModelNotFoundException | AmbiguousIdentifierException e)
		{
			LOGGER.trace(e.getMessage(), e);
			return null;
		}
	}

	@Required
	public void setDestinationService(final DestinationService<ConsumedDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}

	protected IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	@Required
	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	protected List<OutboundRequestDecorator> getOutboundRequestDecorators()
	{
		return decorators;
	}

	@Required
	public void setOutboundRequestDecorators(final List<OutboundRequestDecorator> decorators)
	{
		this.decorators = decorators;
	}

	protected ClientHttpRequestInterceptor getMonitoringInterceptor()
	{
		return monitoringInterceptor;
	}

	@Required
	public void setMonitoringInterceptor(final ClientHttpRequestInterceptor monitoringInterceptor)
	{
		this.monitoringInterceptor = monitoringInterceptor;
	}

	protected OutboundRequestDecorator getMonitoringDecorator()
	{
		return monitoringDecorator;
	}

	@Required
	public void setMonitoringDecorator(final OutboundRequestDecorator monitoringDecorator)
	{
		this.monitoringDecorator = monitoringDecorator;
	}

	protected IntegrationObjectService getIntegrationObjectService()
	{
		return integrationObjectService;
	}

	@Required
	public void setIntegrationObjectService(final IntegrationObjectService integrationObjectService)
	{
		this.integrationObjectService = integrationObjectService;
	}

	protected OutboundServicesConfiguration getOutboundServicesConfiguration()
	{
		return outboundServicesConfiguration;
	}

	@Required
	public void setOutboundServicesConfiguration(final OutboundServicesConfiguration outboundServicesConfiguration)
	{
		this.outboundServicesConfiguration = outboundServicesConfiguration;
	}
}
