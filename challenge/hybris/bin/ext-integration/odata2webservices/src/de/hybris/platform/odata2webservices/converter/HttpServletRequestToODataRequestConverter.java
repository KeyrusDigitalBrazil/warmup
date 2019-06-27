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
package de.hybris.platform.odata2webservices.converter;

import de.hybris.platform.odata2services.converter.ContentTypeNotSupportedException;
import de.hybris.platform.odata2services.converter.PathInfoInvalidException;
import de.hybris.platform.odata2services.converter.RequestMethodNotSupportedException;
import de.hybris.platform.odata2webservices.odata.IntegrationODataRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataUnsupportedMediaTypeException;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.core.servlet.RestUtil;
import org.springframework.core.convert.converter.Converter;


public class HttpServletRequestToODataRequestConverter implements Converter<HttpServletRequest, ODataRequest>
{
	/**
	 * Converts a HttpServletRequest into a ODataRequest.
	 *
	 * @param request - HttpServletRequest that needs to be converted to a ODataRequest.
	 * @return - ODataRequest object based off of the request param and oDattaHttpMethod
	 * set during the instantiation of this class.
	 */
	@Override
	public ODataRequest convert(final HttpServletRequest request)
	{
		try
		{
			final IntegrationODataRequest integrationODataRequest = new IntegrationODataRequest();

			return integrationODataRequest.customBuilder().method(convertStringToODataHttpMethod(request.getMethod()))
					.httpMethod(request.getMethod())
					.contentType(extractContentType(request))
					.acceptHeaders(extractAcceptHeader(request))
					.acceptableLanguages(extractAcceptLanguageHeader(request))
					.pathInfo(extractPathInfo(request))
					.requestHeaders(RestUtil.extractHeaders(request))
					.allQueryParameters(extractQueryParametersNoFormEncoding(request))
					.body(request.getInputStream())
					.build();
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestMethodNotSupportedException(request.getMethod(), e);
		}
		catch (final ODataUnsupportedMediaTypeException e)
		{
			throw new ContentTypeNotSupportedException(request.getContentType(), e);
		}
		catch (final ODataException e)
		{
			throw new PathInfoInvalidException(request.getRequestURI(), e);
		}
		catch (final IOException e)
		{
			throw new IllegalArgumentException("Unable to process request content", e);
		}
	}

	/**
	 * Translates the name of the HttpServletRequest method into a ODataHttpMethod
	 *
	 * @param method - httpServletRequestMethod
	 * @return - equivalent ODataHttpMethod that has a name equal to the method String parameter.
	 */
	protected ODataHttpMethod convertStringToODataHttpMethod(final String method)
	{
		return ODataHttpMethod.valueOf(method);
	}

	protected String extractContentType(final HttpServletRequest req) throws ODataUnsupportedMediaTypeException
	{
		return RestUtil.extractRequestContentType(req.getContentType()).toContentTypeString();
	}

	protected PathInfo extractPathInfo(final HttpServletRequest req) throws ODataException
	{
		// Setting the pathSplit to 1 so the service path parameter of the URL is considered
		// part of the "preceding path segment" and only $metadata is a path segment
		return RestUtil.buildODataPathInfo(req, 1);
	}

	protected Map<String, List<String>> extractQueryParametersNoFormEncoding(final HttpServletRequest req)
	{
		return RestUtil.extractAllQueryParameters(req.getQueryString(), Boolean.FALSE.toString());
	}

	protected List<Locale> extractAcceptLanguageHeader(final HttpServletRequest req)
	{
		return RestUtil.extractAcceptableLanguage(req.getHeader(HttpHeaders.ACCEPT_LANGUAGE));
	}

	protected List<String> extractAcceptHeader(final HttpServletRequest req)
	{
		return RestUtil.extractAcceptHeaders(req.getHeader(HttpHeaders.ACCEPT));
	}
}
