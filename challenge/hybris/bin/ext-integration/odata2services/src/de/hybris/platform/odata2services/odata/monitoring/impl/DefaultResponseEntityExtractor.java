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

import static org.apache.olingo.odata2.api.commons.HttpContentType.MULTIPART_MIXED;
import static org.apache.olingo.odata2.api.commons.HttpHeaders.CONTENT_TYPE;

import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser;
import de.hybris.platform.odata2services.odata.RuntimeIOException;
import de.hybris.platform.odata2services.odata.monitoring.IntegrationKeyExtractor;
import de.hybris.platform.odata2services.odata.monitoring.InboundMonitoringException;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntity;
import de.hybris.platform.odata2services.odata.monitoring.ResponseChangeSetEntityBuilder;
import de.hybris.platform.odata2services.odata.monitoring.ResponseEntityExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.client.batch.BatchSingleResponse;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.batch.v2.BatchParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DefaultResponseEntityExtractor implements ResponseEntityExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultResponseEntityExtractor.class);
	private List<IntegrationKeyExtractor> integrationKeyExtractors;
	private List<MonitoredRequestErrorParser<InboundRequestErrorModel>> errorParsers;

	@Override
	public List<ResponseChangeSetEntity> extractFrom(final ODataResponse response)
	{
		if (response.getContentHeader().startsWith(MULTIPART_MIXED))
		{
			return extractFromBatchResponse(response);
		}
		else
		{
			return extractFromSingleResponse(response);
		}
	}

	protected List<ResponseChangeSetEntity> extractFromBatchResponse(final ODataResponse oDataResponse)
	{
		final List<BatchSingleResponse> batchSingleResponses = parseBatchResponses(oDataResponse);
		return batchSingleResponses.stream()
				.map(response ->
						ResponseChangeSetEntityBuilder.responseChangeSetEntity()
								.withStatusCode(response.getStatusCode())
								.withIntegrationKey(extractIntegrationKey(response))
								.withRequestError(parseResponseErrors(response))
								.build()
				)
				.collect(Collectors.toList());
	}

	protected List<ResponseChangeSetEntity> extractFromSingleResponse(final ODataResponse oDataResponse)
	{
		return Collections.singletonList((ResponseChangeSetEntityBuilder.responseChangeSetEntity()
				.withStatusCode(oDataResponse.getStatus().getStatusCode())
				.withIntegrationKey(extractIntegrationKey(oDataResponse))
				.withRequestError(parseResponseErrors(oDataResponse))
				.build()));
	}

	protected InboundRequestErrorModel parseResponseErrors(final BatchSingleResponse singleResponse)
	{
		return parseErrorsFrom(singleResponse.getHeader(CONTENT_TYPE), Integer.valueOf(singleResponse.getStatusCode()), singleResponse.getBody());
	}

	protected InboundRequestErrorModel parseResponseErrors(final ODataResponse oDataResponse)
	{
		return parseErrorsFrom(oDataResponse.getContentHeader(), oDataResponse.getStatus().getStatusCode(), getBodyAsString(oDataResponse));
	}

	protected InboundRequestErrorModel parseErrorsFrom(final String contentType, final int statusCode, final String responseBody)
	{
		return errorParsers.stream()
				.filter(extractor -> extractor.isApplicable(contentType, statusCode))
				.map(extractor -> extractor.parseErrorFrom(InboundRequestErrorModel.class, statusCode, responseBody))
				.findFirst().orElse(null);
	}

	protected String extractIntegrationKey(final BatchSingleResponse singleResponse)
	{
		return applyExtractors(singleResponse.getHeader(CONTENT_TYPE), Integer.valueOf(singleResponse.getStatusCode()), singleResponse.getBody());
	}

	protected String extractIntegrationKey(final ODataResponse oDataResponse)
	{
		return applyExtractors(oDataResponse.getContentHeader(), oDataResponse.getStatus().getStatusCode(), getBodyAsString(oDataResponse));
	}

	protected String applyExtractors(final String contentType, final int statusCode, final String responseBody)
	{
		return integrationKeyExtractors.stream()
				.filter(extractor -> extractor.isApplicable(contentType))
				.map(extractor -> extractor.extractIntegrationKey(responseBody, statusCode))
				.findFirst().orElse(null);
	}

	protected String getBodyAsString(final ODataResponse oDataResponse)
	{
		try (final InputStream entityAsStream = oDataResponse.getEntityAsStream())
		{
			return IOUtils.toString(entityAsStream, StandardCharsets.UTF_8);
		}
		catch (final ODataException e)
		{
			LOGGER.error("Error when trying to parse response body to String.", e);
			return StringUtils.EMPTY;
		}
		catch (final IOException ie)
		{
			LOGGER.error("There was a problem reading or closing the stream of the request body");
			throw new RuntimeIOException(ie);
		}
	}

	protected List<BatchSingleResponse> parseBatchResponses(final ODataResponse response)
	{
		final BatchParser batchParser = new BatchParser(response.getContentHeader(), true);
		try (final InputStream entityAsStream = response.getEntityAsStream())
		{
			return batchParser.parseBatchResponse(entityAsStream);
		}
		catch (final ODataException e)
		{
			LOGGER.error("Error when trying to parse ODataResponse: {}", response);
			throw new InboundMonitoringException(e);
		}
		catch (final IOException ie)
		{
			LOGGER.error("There was a problem reading or closing the stream of the request body");
			throw new RuntimeIOException(ie);
		}
	}

	@Required
	public void setErrorParsers(final List<MonitoredRequestErrorParser<InboundRequestErrorModel>> errorParsers)
	{
		this.errorParsers = errorParsers;
	}

	@Required
	public void setIntegrationKeyExtractors(final List<IntegrationKeyExtractor> integrationKeyExtractors)
	{
		this.integrationKeyExtractors = integrationKeyExtractors;
	}
}
