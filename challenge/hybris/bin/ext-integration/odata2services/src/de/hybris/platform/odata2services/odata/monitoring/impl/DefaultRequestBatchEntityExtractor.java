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
package de.hybris.platform.odata2services.odata.monitoring.impl;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ODATA_REQUEST;
import static org.apache.olingo.odata2.api.commons.HttpContentType.MULTIPART_MIXED;
import static org.apache.olingo.odata2.api.commons.HttpHeaders.CONTENT_TYPE;

import de.hybris.platform.odata2services.config.ODataServicesConfiguration;
import de.hybris.platform.odata2services.odata.monitoring.InboundMonitoringException;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntity;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntityBuilder;
import de.hybris.platform.odata2services.odata.monitoring.RequestBatchEntityExtractor;
import de.hybris.platform.odata2services.odata.processor.NewLineSanitizerInputStream;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.batch.BatchException;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.core.batch.v2.BatchParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultRequestBatchEntityExtractor implements RequestBatchEntityExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRequestBatchEntityExtractor.class);

	private ODataServicesConfiguration configuration;

	@Override
	public List<RequestBatchEntity> extractFrom(final ODataContext context)
	{
		return context.getRequestHeader(CONTENT_TYPE).startsWith(MULTIPART_MIXED)
				? extractBatchRequests(context)
				: Collections.singletonList(requestEntity(context, getRequest(context).getBody()));
	}

	protected List<RequestBatchEntity> extractBatchRequests(final ODataContext context)
	{
		try
		{
			final BatchParser batchParser = new BatchParser(context.getRequestHeader(CONTENT_TYPE), getProps(context), true);
			final List<BatchRequestPart> requestParts = getRequestParts(context, batchParser);
			return requestParts.size() > getConfiguration().getBatchLimit()
					? Collections.singletonList(requestEntity(context))
					: requestParts.stream()
					.map(part -> handleBatchPart(part, context))
					.collect(Collectors.toList());
		}
		catch (final RuntimeException e)
		{
			LOGGER.error("Failed to parse batch request for monitoring purposes. Persisting all batches as single request", e);
			return Collections.singletonList(requestEntity(context, getRequest(context).getBody()));
		}
	}

	protected RequestBatchEntity handleBatchPart(final BatchRequestPart part, final ODataContext context)
	{
		final List<ODataRequest> requests = part.getRequests();
		final InputStream singleBatch = combineChangeSetsToSingleBatch(requests);
		return requestEntity(context, singleBatch, requests.size());
	}

	protected InputStream combineChangeSetsToSingleBatch(final List<ODataRequest> requests)
	{
		return new SequenceInputStream(Collections.enumeration(
				requests.stream()
						.map(ODataRequest::getBody)
						.collect(Collectors.toList()))
		);
	}

	protected EntityProviderBatchProperties getProps(final ODataContext context)
	{
		try
		{
			return EntityProviderBatchProperties.init()
					.pathInfo(context.getPathInfo())
					.setStrict(true)
					.build();
		}
		catch (final ODataException e)
		{
			LOGGER.error("Exception while trying to get path info", e);
			throw new InboundMonitoringException(e);
		}
	}

	private RequestBatchEntity requestEntity(final ODataContext context)
	{
		return requestEntity(context, null, 1);
	}

	private RequestBatchEntity requestEntity(final ODataContext context, final InputStream body)
	{
		return requestEntity(context, body, 1);
	}

	protected RequestBatchEntity requestEntity(final ODataContext context, final InputStream body, final int numberOfChangesetsInBatch)
	{
		return RequestBatchEntityBuilder.requestBatchEntity()
				.withContext(context)
				.withBatchContent(body)
				.withNumberOfChangeSets(numberOfChangesetsInBatch)
				.build();
	}

	protected List<BatchRequestPart> getRequestParts(final ODataContext context, final BatchParser batchParser)
	{
		try
		{
			return batchParser.parseBatchRequest(new NewLineSanitizerInputStream(getRequest(context).getBody()));
		}
		catch (final BatchException e)
		{
			LOGGER.error("Error while parsing batch parts from request");
			throw new InboundMonitoringException(e);
		}
	}

	protected ODataRequest getRequest(final ODataContext context)
	{
		return (ODataRequest) context.getParameter(ODATA_REQUEST);
	}

	protected ODataServicesConfiguration getConfiguration()
	{
		return configuration;
	}

	@Required
	public void setConfiguration(final ODataServicesConfiguration configuration)
	{
		this.configuration = configuration;
	}
}
