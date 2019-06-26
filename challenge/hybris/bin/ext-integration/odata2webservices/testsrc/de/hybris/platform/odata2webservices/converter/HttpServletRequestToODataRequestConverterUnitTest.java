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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.converter.ContentTypeNotSupportedException;
import de.hybris.platform.odata2services.converter.PathInfoInvalidException;
import de.hybris.platform.odata2services.converter.RequestMethodNotSupportedException;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.assertj.core.util.Maps;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;

@UnitTest
public class HttpServletRequestToODataRequestConverterUnitTest
{
	private static final String MERGE = "MERGE";
	private static final String NON_SUPPORTED = "BADBADMETHOD";

	/* Odata field names */
	private static final String METHOD = "method";
	private static final String ACCEPT_HEADER_ODATA = "acceptHeaders";
	private static final String ACCEPTABLE_LANGUAGES = "acceptableLanguages";
	private static final String CONTENT_TYPE = "contentType";
	private static final String REQUEST_HEADERS = "requestHeaders";
	private static final String QUERY_PARAMETERS = "queryParameters";

	/* Http Servlet Request header names */
	private static final String ACCEPT_HEADER = "Accept";
	private static final String ACCEPT_LANGUAGE = "Accept-Language";

	private static final String APPLICATION_JSON = "application/json";
	private static final String APPLICATION_XML = "application/xml";
	private static final String BAD_CONTENT_TYPE = "badContentType";
	private static final String INVALID_ACCEPT_HEADER = "invalidHeader";
	private static final String HEADER_SEPARATOR = ", ";
	private static final String VALID_SERVER_NAME = "my.server";
	private static final int DEFAULT_PORT = 8080;
	private static final String PRODUCT_REQUEST = "product";
	private static final String HTTP = "http://";
	private static final String URI_PORT_SEPARATOR = ":";
	private static final String HEADER_1 = "header1";
	private static final String HEADER_VALUE = "headervalue";

	private final HttpServletRequestToODataRequestConverter converter = new HttpServletRequestToODataRequestConverter();

	@Test
	public void testConvertGetMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(METHOD, ODataHttpMethod.GET);
	}

	@Test
	public void testConvertPutMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.PUT.name(), null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(METHOD, ODataHttpMethod.PUT);
	}

	@Test
	public void testConvertPostMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.POST.name(), null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(METHOD, ODataHttpMethod.POST);
	}

	@Test
	public void testConvertDeleteMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.DELETE.name(), null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(METHOD, ODataHttpMethod.DELETE);
	}

	@Test
	public void testConvertPatchMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.PATCH.name(), null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(METHOD, ODataHttpMethod.PATCH);
	}

	@Test
	public void testConvertMergeMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(MERGE, null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(METHOD, ODataHttpMethod.MERGE);
	}

	@Test
	public void testConvertNonSupportedMethod()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(NON_SUPPORTED, null);

		assertThatThrownBy(() -> converter.convert(httpServletRequest))
				.isInstanceOf(RequestMethodNotSupportedException.class)
				.hasFieldOrPropertyWithValue(METHOD, NON_SUPPORTED);
	}

	@Test
	public void testConvertWellFormedContentType()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(CONTENT_TYPE, APPLICATION_JSON);
	}

	@Test
	public void testConvertMalformedContentType()
	{
		final HttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), BAD_CONTENT_TYPE);

		assertThatThrownBy(() -> converter.convert(httpServletRequest))
				.isInstanceOf(ContentTypeNotSupportedException.class)
				.hasFieldOrPropertyWithValue(CONTENT_TYPE, BAD_CONTENT_TYPE);
	}

	@Test
	public void testConvertAcceptHeaderOneAcceptHeader()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(ACCEPT_HEADER, APPLICATION_JSON);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(ACCEPT_HEADER_ODATA, Collections.singletonList(APPLICATION_JSON));
	}

	@Test
	public void testConvertAcceptHeaderMultipleAcceptHeaders()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(ACCEPT_HEADER, APPLICATION_JSON + HEADER_SEPARATOR + APPLICATION_XML);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(ACCEPT_HEADER_ODATA, Arrays.asList(APPLICATION_JSON, APPLICATION_XML));
	}

	@Test
	public void testConvertAcceptHeaderInvalidAcceptHeaders()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(ACCEPT_HEADER, INVALID_ACCEPT_HEADER);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(ACCEPT_HEADER_ODATA, Collections.emptyList());
	}

	@Test
	public void testConvertAcceptableLanguagesOneAcceptableLanguage()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(ACCEPT_LANGUAGE, Locale.ENGLISH.toString());

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(ACCEPTABLE_LANGUAGES, Collections.singletonList(Locale.ENGLISH));
	}

	@Test
	public void testConvertAcceptableLanguagesMultipleLanguages()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(ACCEPT_LANGUAGE, Locale.ENGLISH.toString() + HEADER_SEPARATOR + Locale.FRENCH.toString());

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(ACCEPTABLE_LANGUAGES, Arrays.asList(Locale.ENGLISH, Locale.FRENCH));
	}

	@Test
	public void testConvertAcceptableLanguagesInvalidLanguage()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(ACCEPT_LANGUAGE, "////");

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(ACCEPTABLE_LANGUAGES, Collections.emptyList());
	}

	@Test
	public void testConvertPathInfoValidPath()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.setServerName(VALID_SERVER_NAME);
		httpServletRequest.setServerPort(DEFAULT_PORT);

		final ODataRequest convertedRequest = converter.convert(httpServletRequest);


		assertNotNull(convertedRequest);
		assertEquals(
				URI.create(HTTP + VALID_SERVER_NAME + URI_PORT_SEPARATOR + DEFAULT_PORT + "/" + PRODUCT_REQUEST),
				convertedRequest.getPathInfo().getRequestUri());
	}

	@Test
	public void testConvertPathInfoInvalidPath()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.setServerName("l;kfdsf;adslkdfs;");
		httpServletRequest.setServerPort(DEFAULT_PORT);

		assertThatThrownBy(() -> converter.convert(httpServletRequest))
				.isInstanceOf(PathInfoInvalidException.class);
	}

	@Test
	public void testConvertRequestHeadersOneHeader()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), null);
		httpServletRequest.addHeader(HEADER_1, HEADER_VALUE);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(REQUEST_HEADERS,
						Maps.newHashMap(HEADER_1, Collections.singletonList(HEADER_VALUE)));
	}

	@Test
	public void testConvertRequestHeadersMultipleHeaders()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), APPLICATION_JSON);
		httpServletRequest.addHeader(HEADER_1, HEADER_VALUE);

		final Map<String, List<String>> expectedHeaders = Maps.newHashMap(
				HEADER_1, Collections.singletonList(HEADER_VALUE));
		expectedHeaders.put("content-type", Collections.singletonList(APPLICATION_JSON));
		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(REQUEST_HEADERS,
						expectedHeaders);
	}

	@Test
	public void testConvertRequestHeadersNoHeaders()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), null);

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(REQUEST_HEADERS, new HashMap<>())
				.hasFieldOrPropertyWithValue(ACCEPT_HEADER_ODATA, Collections.emptyList())
				.hasFieldOrPropertyWithValue(ACCEPTABLE_LANGUAGES, Collections.emptyList());
	}

	@Test
	public void testConvertAllQueryParametersOneParameterEntityType()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), null);
		httpServletRequest.setQueryString("Product");

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(QUERY_PARAMETERS, Maps.newHashMap("Product", ""));
	}

	@Test
	public void testConvertAllQueryParametersOneParameter()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), null);
		httpServletRequest.setQueryString("name=Jane");

		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(QUERY_PARAMETERS, Maps.newHashMap("name", "Jane"));
	}

	@Test
	public void testConvertAllQueryParametersMultipleParameters()
	{
		final MockHttpServletRequest httpServletRequest = createHttpServletRequest(HttpMethod.GET.name(), null);
		httpServletRequest.setQueryString("name=Jane&lastName=Doe");

		final Map<String, String> expectedParameters = Maps.newHashMap("name", "Jane");
		expectedParameters.put("lastName", "Doe");
		assertThat(converter.convert(httpServletRequest))
				.isNotNull()
				.hasFieldOrPropertyWithValue(QUERY_PARAMETERS, expectedParameters);
	}

	private MockHttpServletRequest createHttpServletRequest(final String method, final String contentType)
	{
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod(method);
		request.setContentType(contentType);
		request.setRequestURI("/" + PRODUCT_REQUEST);
		return request;
	}
}