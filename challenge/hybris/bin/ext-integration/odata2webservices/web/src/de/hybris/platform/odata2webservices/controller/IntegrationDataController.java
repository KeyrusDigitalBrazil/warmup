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
package de.hybris.platform.odata2webservices.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hybris.platform.odata2webservices.odata.ODataFacade;

/**
 * Controller for exchanging integration data and metadata
 */
@RestController
public class IntegrationDataController
{
	private final ODataFacade oDataFacade;
	private final Converter<ODataResponse, ResponseEntity<String>> oDataResponseToResponseEntityConverter;
	private final Converter<HttpServletRequest, ODataContext> httpServletRequestToODataContextConverter;

	public IntegrationDataController(final ODataFacade oDataWebMonitoringFacade,
			final Converter<ODataResponse, ResponseEntity<String>> oDataResponseToResponseEntityConverter,
			final Converter<HttpServletRequest, ODataContext> httpServletRequestToODataContextConverter)
	{
		this.oDataFacade = oDataWebMonitoringFacade;
		this.oDataResponseToResponseEntityConverter = oDataResponseToResponseEntityConverter;
		this.httpServletRequestToODataContextConverter = httpServletRequestToODataContextConverter;
	}

	@GetMapping(value = "/{service}/$metadata", produces = {MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> getSchema(final HttpServletRequest request)
	{
		final ODataContext requestContext = httpServletRequestToODataContextConverter.convert(request);
		final ODataResponse oDataResponse = oDataFacade.handleGetSchema(requestContext);
		return oDataResponseToResponseEntityConverter.convert(oDataResponse);
	}

	@GetMapping(value = "/{service}/{entity}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
	public ResponseEntity<String> getEntity(final HttpServletRequest request)
	{
		final ODataContext requestContext = httpServletRequestToODataContextConverter.convert(request);
		final ODataResponse oDataResponse = oDataFacade.handleGetEntity(requestContext);
		return oDataResponseToResponseEntityConverter.convert(oDataResponse);
	}

	@GetMapping(value = "/{service}/{entity}/{property}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
	public ResponseEntity<String> getPropertyFromEntity(final HttpServletRequest request)
	{
		final ODataContext requestContext = httpServletRequestToODataContextConverter.convert(request);
		final ODataResponse oDataResponse = oDataFacade.handleGetEntity(requestContext);
		return oDataResponseToResponseEntityConverter.convert(oDataResponse);
	}

	@GetMapping(value = "/{service}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<String> getEntities(final HttpServletRequest request)
	{
		final ODataContext requestContext = httpServletRequestToODataContextConverter.convert(request);
		final ODataResponse oDataResponse = oDataFacade.handleGetEntity(requestContext);
		return oDataResponseToResponseEntityConverter.convert(oDataResponse);
	}

	@PostMapping(value = "/{service}/$batch", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
	public ResponseEntity<String> handleBatch(final HttpServletRequest request)
	{
		final ODataContext requestContext = httpServletRequestToODataContextConverter.convert(request);
		final ODataResponse oDataResponse = oDataFacade.handlePost(requestContext);
		return oDataResponseToResponseEntityConverter.convert(oDataResponse);
	}

	@PostMapping(value = "/{service}/{entity}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_ATOM_XML_VALUE})
	public ResponseEntity<String> handleCreateOrUpdateEntity(final HttpServletRequest request)
	{
		final ODataContext requestContext = httpServletRequestToODataContextConverter.convert(request);
		final ODataResponse oDataResponse = oDataFacade.handlePost(requestContext);
		return oDataResponseToResponseEntityConverter.convert(oDataResponse);
	}
}
