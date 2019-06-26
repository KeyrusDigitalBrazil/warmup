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
package com.hybris.backoffice.filter.responseheaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.util.CockpitProperties;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BackofficeResponseHeadersHandlerTest
{
	@Mock
	private CockpitProperties cockpitProperties;

	@InjectMocks
	@Spy
	private final BackofficeResponseHeadersHandler backofficeResponseHeadersHandler = new BackofficeResponseHeadersHandler();

	@Test
	public void testPropertiesWithResponseHeadersOnly()
	{
		// given
		final Map<String, String> properties = generatePropertiesWithSecurityResponseHeaders();
		doReturn(properties).when(cockpitProperties).getProperties();
		properties.keySet().forEach(prop -> doReturn(properties.get(prop)).when(cockpitProperties).getProperty(prop));

		// when
		final Map<String, String> headers = backofficeResponseHeadersHandler.resolveResponseHeaders();

		// then
		assertThat(headers.size()).isEqualTo(5);
		assertThat(headers.get("X-Frame-Options")).isEqualTo("SAMEORIGIN");
		assertThat(headers.get("Strict-Transport-Security")).isEqualTo("max-age=31536000; includeSubDomains");
		assertThat(headers.get("X-XSS-Protection")).isEqualTo("1; mode=block");
		assertThat(headers.get("X-Content-Type-Options")).isEqualTo("nosniff");
		assertThat(headers.get("Content-Security-Policy")).isEqualTo(
				"default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; connect-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'; font-src 'self'");
	}

	@Test
	public void testPropertiesWithoutResponseHeaders()
	{
		// given
		final Map<String, String> properties = generatePropertiesWithNoResponseHeaders();
		doReturn(properties).when(cockpitProperties).getProperties();
		properties.keySet().forEach(prop -> doReturn(properties.get(prop)).when(cockpitProperties).getProperty(prop));

		// when
		final Map<String, String> headers = backofficeResponseHeadersHandler.resolveResponseHeaders();

		// then
		assertThat(headers).isEmpty();
	}

	@Test
	public void testPropertiesWithSomeResponseHeaders()
	{
		// given
		final Map<String, String> properties = generatePropertiesWithSomeResponseHeaders();
		doReturn(properties).when(cockpitProperties).getProperties();
		properties.keySet().forEach(prop -> doReturn(properties.get(prop)).when(cockpitProperties).getProperty(prop));

		// when
		final Map<String, String> headers = backofficeResponseHeadersHandler.resolveResponseHeaders();

		// then
		assertThat(headers.size()).isEqualTo(2);
		assertThat(headers.get("header1")).isEqualTo("val1");
		assertThat(headers.get("header2")).isEqualTo("val2");
	}

	@Test
	public void testEmptyPropertiesWithoutResponseHeaders()
	{
		// given
		final Map<String, String> properties = generateEmptyProperties();
		doReturn(properties).when(cockpitProperties).getProperties();
		properties.keySet().forEach(prop -> doReturn(properties.get(prop)).when(cockpitProperties).getProperty(prop));

		// when
		final Map<String, String> headers = backofficeResponseHeadersHandler.resolveResponseHeaders();

		// then
		assertThat(headers.size()).isEqualTo(0);
	}

	@Test
	public void testPropertiesWithEmptyResponseHeaders()
	{
		// given
		final Map<String, String> properties = generatePropertiesWithEmptyResponseHeaders();
		doReturn(properties).when(cockpitProperties).getProperties();
		properties.keySet().forEach(prop -> doReturn(properties.get(prop)).when(cockpitProperties).getProperty(prop));

		// when
		final Map<String, String> headers = backofficeResponseHeadersHandler.resolveResponseHeaders();

		// then
		assertThat(headers.size()).isEqualTo(2);
		assertThat(headers.get("header1")).isEqualTo("");
		assertThat(headers.get("header2")).isEqualTo(" ");
	}

	@Test
	public void testApplyResponseHeaders()
	{
		// given
		final Map<String, String> responseHeaders = generateResponseHeaders();
		final HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		responseHeaders.keySet()
				.forEach(header -> doNothing().when(servletResponse).setHeader(header, responseHeaders.get(header)));

		// when
		backofficeResponseHeadersHandler.applyResponseHeaders(responseHeaders, servletResponse);

		// then
		verify(servletResponse, times(3)).setHeader(any(), any());
		responseHeaders.keySet().forEach(header -> verify(servletResponse).setHeader(header, responseHeaders.get(header)));
	}

	@Test
	public void testIsPropertyWithHeader()
	{
		// given
		final Map<String, String> properties = generatePropertyWithHeader();
		doReturn(properties).when(cockpitProperties).getProperties();
		properties.keySet().forEach(prop -> doReturn(properties.get(prop)).when(cockpitProperties).getProperty(prop));

		// when and then
		assertThat(backofficeResponseHeadersHandler.isPropertyWithHeader("response.header.header1")).isTrue();
		assertThat(backofficeResponseHeadersHandler.isPropertyWithHeader("response.header.header2.subheader")).isTrue();
		assertThat(backofficeResponseHeadersHandler.isPropertyWithHeader("response.header.1")).isTrue();
		assertThat(backofficeResponseHeadersHandler.isPropertyWithHeader("response.header")).isFalse();
		assertThat(backofficeResponseHeadersHandler.isPropertyWithHeader("response.header.")).isFalse();
	}

	private static Map<String, String> generatePropertiesWithSecurityResponseHeaders()
	{
		final Map<String, String> properties = new HashMap<>();
		properties.put("response.header.X-Frame-Options", "SAMEORIGIN");
		properties.put("response.header.Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		properties.put("response.header.X-XSS-Protection", "1; mode=block");
		properties.put("response.header.X-Content-Type-Options", "nosniff");
		properties.put("response.header.Content-Security-Policy",
				"default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; connect-src 'self'; img-src 'self'; style-src 'self' 'unsafe-inline'; font-src 'self'");
		return properties;
	}

	private static Map<String, String> generatePropertiesWithNoResponseHeaders()
	{
		final Map<String, String> properties = new HashMap<>();
		properties.put("header1", "val1");
		properties.put("header2", "val2");
		properties.put("header3", "val3");
		return properties;
	}

	private static Map<String, String> generatePropertiesWithSomeResponseHeaders()
	{
		final Map<String, String> properties = new HashMap<>();
		properties.put("header1", "val1");
		properties.put("header2", "val2");
		properties.put("header3", "val3");
		properties.put("response.header.header1", "val1");
		properties.put("response.header.header2", "val2");
		return properties;
	}

	private static Map<String, String> generateEmptyProperties()
	{
		return new HashMap<>();
	}

	private static Map<String, String> generatePropertiesWithEmptyResponseHeaders()
	{
		final Map<String, String> properties = new HashMap<>();
		properties.put("response.header.header1", "");
		properties.put("response.header.header2", " ");
		properties.put("response.header.header3", null);
		return properties;
	}

	private static Map<String, String> generateResponseHeaders()
	{
		final Map<String, String> properties = new HashMap<>();
		properties.put("response.header.header1", "val1");
		properties.put("response.header.header2", "val2");
		properties.put("response.header.header3", "val3");
		return properties;
	}

	private static Map<String, String> generatePropertyWithHeader()
	{
		final Map<String, String> properties = new HashMap<>();
		properties.put("header1", "val1");
		properties.put("header2", "val2");
		properties.put("header3", "val3");
		properties.put("response.header.header1", "val1");
		properties.put("response.header.header2.subheader", "val2");
		properties.put("response.header", "val4");
		properties.put("response.header.", "val5");
		properties.put("response.header.1", "val6");
		return properties;
	}

}
