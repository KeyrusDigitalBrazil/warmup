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

package de.hybris.platform.odata2webservices.odata.builders;

import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2webservices.odata.IntegrationODataRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.core.PathInfoImpl;

/**
 * A builder to create {@link org.apache.olingo.odata2.api.processor.ODataRequest}s according to specifications in the tests.
 */
public class ODataRequestBuilder
{
	private final Map<String, List<String>> requestHeaders;
	private final List<String> acceptHeaders;
	private final List<Locale> acceptableLanguages;
	private final Map<String, String> queryParameters;
	private final ODataHttpMethod httpMethod;
	private String contentType;
	private PathInfoImpl pathInfo;
	private InputStream requestBody;

	private ODataRequestBuilder(final ODataHttpMethod method)
	{
		httpMethod = method;
		requestHeaders = new HashMap<>();
		acceptHeaders = new ArrayList<>();
		acceptableLanguages = new ArrayList<>();
		queryParameters = new HashMap<>();
	}

	/**
	 * Creates new instance of the builder for building a GET request.
	 *
	 * @return instance of the builder, which has no specifications done to it.
	 */
	public static ODataRequestBuilder oDataGetRequest()
	{
		return new ODataRequestBuilder(ODataHttpMethod.GET);
	}

	/**
	 * Creates new instance of the builder for building a POST request.
	 *
	 * @return instance of the builder, which has no specifications done to it.
	 */
	public static ODataRequestBuilder oDataPostRequest()
	{
		return new ODataRequestBuilder(ODataHttpMethod.POST);
	}

	public ODataRequestBuilder withAccepts(final String mediaType)
	{
		acceptHeaders.add(mediaType);
		return withHeader(HttpHeaders.ACCEPT, mediaType);
	}

	public ODataRequestBuilder withAcceptLanguage(final Locale locale)
	{
		if (locale != null)
		{
			acceptableLanguages.add(locale);
			return withHeader(HttpHeaders.ACCEPT_LANGUAGE, locale.getLanguage());
		}
		return this;
	}

	public ODataRequestBuilder withContentLanguage(final Locale locale)
	{
		return locale != null
				? withHeader(HttpHeaders.CONTENT_LANGUAGE, locale.getLanguage())
				: this;
	}

	public ODataRequestBuilder withContentType(final String type)
	{
		contentType = type;
		return type != null
				? withHeader(HttpHeaders.CONTENT_TYPE, contentType)
				: this;
	}

	public ODataRequestBuilder withHeader(final String headerName, final String... values)
	{
		return withHeader(headerName, Arrays.asList(values));
	}

	private ODataRequestBuilder withHeader(final String headerName, final List<String> values)
	{
		requestHeaders.put(headerName, values);
		return this;
	}

	public ODataRequestBuilder withParameters(final Map<String, String> params)
	{
		queryParameters.putAll(params);
		return this;
	}

	public ODataRequestBuilder withParameters(final String... paramNames)
	{
		Stream.of(paramNames).forEach(name -> withParameter(name, ""));
		return this;
	}

	public ODataRequestBuilder withParameter(final String name, final Object value)
	{
		final String strValue = value != null ? String.valueOf(value) : null;
		queryParameters.put(name, strValue);
		return this;
	}

	public ODataRequestBuilder withPathInfo(final PathInfoBuilder builder)
	{
		return withPathInfo(builder.build());
	}

	public ODataRequestBuilder withPathInfo(final PathInfoImpl info)
	{
		pathInfo = info;
		return this;
	}

	/**
	 * Specifies body for the POST request. It's implied that UTF-8 encoding is used for the specified body.
	 *
	 * @param content a builder containing specification of the body to be sent as "application/json" content type.
	 * @return a builder with the POST request body specified
	 */
	public ODataRequestBuilder withBody(final JsonBuilder content)
	{
		return withContentType(MediaType.APPLICATION_JSON)
				.withBody(content.build());
	}

	/**
	 * Specifies body for the POST request. It's implied that UTF-8 encoding is used for the specified body.
	 *
	 * @param content content of the body
	 * @return a builder with the POST request body specified
	 */
	public ODataRequestBuilder withBody(final String content)
	{
		requestBody = IOUtils.toInputStream(content, StandardCharsets.UTF_8);
		return this;
	}

	/**
	 * Specifies body for the POST request
	 *
	 * @param content content of the body
	 * @param charset encoding of the body
	 * @return a builder with the POST request body specified
	 */
	public ODataRequestBuilder withBody(final String content, final Charset charset)
	{
		requestBody = IOUtils.toInputStream(content, charset);
		return this;
	}

	public ODataRequest build()
	{
		final ODataRequest.ODataRequestBuilder builder = new IntegrationODataRequest().customBuilder()
				.acceptHeaders(acceptHeaders)
				.requestHeaders(requestHeaders)
				.queryParameters(queryParameters)
				.body(new ByteArrayInputStream(new byte[0]))
				.acceptableLanguages(acceptableLanguages)
				.pathInfo(pathInfo)
				.httpMethod(httpMethod.name())
				.method(httpMethod);

		if (contentType != null)
		{
			builder.contentType(contentType);
		}
		if (requestBody != null)
		{
			builder.body(requestBody);
		}
		return builder.build();
	}
}
