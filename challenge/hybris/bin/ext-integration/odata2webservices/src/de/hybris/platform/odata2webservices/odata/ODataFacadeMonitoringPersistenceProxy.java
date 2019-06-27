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
package de.hybris.platform.odata2webservices.odata;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Lists;
import de.hybris.platform.inboundservices.config.InboundServicesConfiguration;
import de.hybris.platform.inboundservices.model.InboundRequestMediaModel;
import de.hybris.platform.integrationservices.service.MediaPersistenceService;
import de.hybris.platform.odata2services.odata.monitoring.InboundRequestService;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntity;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntityExtractor;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntity;
import de.hybris.platform.odata2services.odata.monitoring.ResponseEntityExtractor;

/**
 * An implementation of {@link ODataFacade} for adding additional logic such as request logging and persistence of monitoring
 * objects before delegating to the default implementation of the facade.
 */
public class ODataFacadeMonitoringPersistenceProxy implements ODataFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ODataFacadeMonitoringPersistenceProxy.class);

	private InboundServicesConfiguration inboundServicesConfiguration;
	private ODataFacade oDataFacade;
	private MediaPersistenceService mediaPersistenceService;
	private InboundRequestService inboundRequestService;
	private RequestBatchEntityExtractor requestEntityExtractor;
	private ResponseEntityExtractor responseEntityExtractor;

	@Override
	public ODataResponse handlePost(final ODataContext context)
	{
		logDebug(context);

		final boolean isMonitoringEnabled = getInboundServicesConfiguration().isMonitoringEnabled();

		return isMonitoringEnabled
				? monitorAndGetResponse(context)
				: getResponse(context);
	}

	@Override
	public ODataResponse handleGetSchema(final ODataContext oDataContext)
	{
		logDebug(oDataContext);
		return getoDataFacade().handleGetSchema(oDataContext);
	}

	@Override
	public ODataResponse handleGetEntity(final ODataContext oDataContext)
	{
		logDebug(oDataContext);
		return getoDataFacade().handleGetEntity(oDataContext);
	}

	protected ODataResponse monitorAndGetResponse(final ODataContext oDataContext)
	{
		final List<RequestBatchEntity> requests = getRequestEntityExtractor().extractFrom(oDataContext);
		final ODataResponse oDataResponse = getResponse(oDataContext);
		final List<ResponseChangeSetEntity> responses = getResponseEntityExtractor().extractFrom(oDataResponse);

		final List<InputStream> contents = Lists.newArrayList();
		for(int i=0; i < requests.size(); i++)
		{
			final InputStream payload = requests.get(i).getContent();
			if( responses.get(i).isSuccessful() )
			{
				contents.add( getInboundServicesConfiguration().isPayloadRetentionForSuccessEnabled() ? payload : null);
			}
			else
			{
				contents.add( getInboundServicesConfiguration().isPayloadRetentionForErrorEnabled() ? payload : null);
			}
		}

		final List<InboundRequestMediaModel> persistedMedias = getMediaPersistenceService().persistMedias(contents, InboundRequestMediaModel.class);
		getInboundRequestService().register(requests, responses, persistedMedias);
		return oDataResponse;
	}

	protected ODataResponse getResponse(final ODataContext oDataContext)
	{
		return getoDataFacade().handlePost(oDataContext);
	}

	private static void logDebug(final ODataContext context)
	{
		if (LOGGER.isDebugEnabled())
		{
			try
			{
				LOGGER.debug("Processing {} {}", context.getHttpMethod(), context.getPathInfo().getRequestUri());
			}
			catch (final ODataException e)
			{
				/*
				 Do nothing - don't break the request handling because request cannot be logged.
				 The problem will manifest itself anyway
				*/
				LOGGER.trace("Exception while creating Debug log: ", e);
			}
		}
	}

	@Required
	public void setoDataFacade(final ODataFacade oDataFacade)
	{
		this.oDataFacade = oDataFacade;
	}

	protected ODataFacade getoDataFacade()
	{
		return oDataFacade;
	}

	@Required
	public void setMediaPersistenceService(final MediaPersistenceService mediaPersistenceServiceImpl)
	{
		this.mediaPersistenceService = mediaPersistenceServiceImpl;
	}

	protected MediaPersistenceService getMediaPersistenceService()
	{
		return mediaPersistenceService;
	}

	@Required
	public void setInboundRequestService(final InboundRequestService inboundRequestService)
	{
		this.inboundRequestService = inboundRequestService;
	}

	protected InboundRequestService getInboundRequestService()
	{
		return inboundRequestService;
	}

	@Required
	public void setInboundServicesConfiguration(final InboundServicesConfiguration inboundServicesConfiguration)
	{
		this.inboundServicesConfiguration = inboundServicesConfiguration;
	}

	protected InboundServicesConfiguration getInboundServicesConfiguration()
	{
		return inboundServicesConfiguration;
	}

	@Required
	public void setRequestEntityExtractor(final RequestBatchEntityExtractor extractor)
	{
		requestEntityExtractor = extractor;
	}

	protected RequestBatchEntityExtractor getRequestEntityExtractor()
	{
		return requestEntityExtractor;
	}

	@Required
	public void setResponseEntityExtractor(final ResponseEntityExtractor extractor)
	{
		responseEntityExtractor = extractor;
	}

	protected ResponseEntityExtractor getResponseEntityExtractor()
	{
		return responseEntityExtractor;
	}
}
