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
package de.hybris.platform.odata2webservices.interceptor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class InboundIntegrationObjectInterceptorUnitTest
{
	private static final String INTEGRATON_OBJECT_CODE = "IntegrationObj";

	@Mock
	private HttpServletRequest request;

	private HttpServletResponse response;

	@Mock
	private IntegrationObjectService integrationObjectService;

	@InjectMocks
	private InboundIntegrationObjectInterceptor interceptor;

	@Before
	public void setUp()
	{
		response = new MockHttpServletResponse();
		when(request.getPathInfo()).thenReturn("/" + INTEGRATON_OBJECT_CODE + "/Products");
	}

	@Test
	public void testInvalidURI() throws Exception
	{
		when(request.getPathInfo()).thenReturn("//Products");
		doThrow(IllegalArgumentException.class).when(integrationObjectService).findIntegrationObject(any());

		final boolean result = interceptor.preHandle(request, response, null);
		assertThat(result).isFalse();
	}


	@Test
	public void testIONotFound() throws Exception
	{
		doThrow(ModelNotFoundException.class).when(integrationObjectService).findIntegrationObject(any());

		final boolean result = interceptor.preHandle(request, response, null);
		assertThat(result).isFalse();
		assertThat(response.getStatus()).isEqualTo(404);
	}

	@Test
	public void testIOFound_Inbound() throws Exception
	{
		givenInboundObject();
		final boolean result = interceptor.preHandle(request, response, null);
		assertThat(result).isTrue();
	}

	@Test
	public void testIOFound_Any() throws Exception
	{
		givenOtherObject();
		final boolean result = interceptor.preHandle(request, response, null);
		assertThat(result).isFalse();
		assertThat(response.getStatus()).isEqualTo(404);
	}

	private IntegrationObjectModel givenInboundObject()
	{
		final IntegrationObjectModel mock = mock(IntegrationObjectModel.class);
		when(mock.getCode()).thenReturn(INTEGRATON_OBJECT_CODE);
		when(mock.getIntegrationType()).thenReturn(IntegrationType.INBOUND);
		when(integrationObjectService.findIntegrationObject(any())).thenReturn(mock);
		return mock;
	}

	private IntegrationObjectModel givenOtherObject()
	{
		final IntegrationObjectModel mock = mock(IntegrationObjectModel.class);
		when(mock.getCode()).thenReturn(INTEGRATON_OBJECT_CODE);
		when(mock.getIntegrationType()).thenReturn(null);
		when(integrationObjectService.findIntegrationObject(any())).thenReturn(mock);
		return mock;
	}

	@Test
	public void testExtractCode_NoPathInfo()
	{
		final String result = interceptor.extractCode(null);
		assertThat(result).isEmpty();
	}

	@Test
	public void testExtractCode_EmptyPathInfo()
	{
		final String result = interceptor.extractCode("");
		assertThat(result).isEmpty();
	}

	@Test
	public void testExtractCode_SlashPathInfo()
	{
		final String result = interceptor.extractCode("/");
		assertThat(result).isEmpty();
	}

	@Test
	public void testExtractCode_IntegrationObjectOnly()
	{
		final String result = interceptor.extractCode("/" + INTEGRATON_OBJECT_CODE);
		assertThat(result).isEqualTo(INTEGRATON_OBJECT_CODE);
	}

	@Test
	public void testExtractCode_IntegrationObjectSlash()
	{
		final String result = interceptor.extractCode("/" + INTEGRATON_OBJECT_CODE + "/");
		assertThat(result).isEqualTo(INTEGRATON_OBJECT_CODE);
	}

	@Test
	public void testExtractCode_IntegrationObjectSlashAnything()
	{
		final String result = interceptor.extractCode("/" + INTEGRATON_OBJECT_CODE + "/IntegrationObjetItems");
		assertThat(result).isEqualTo(INTEGRATON_OBJECT_CODE);
	}

	@Test
	public void testExtractCode_DoubleSlash()
	{
		final String result = interceptor.extractCode("//" );
		assertThat(result).isEmpty();
	}

	@Test
	public void testExtractCode_DoubleSlashAnything()
	{
		final String result = interceptor.extractCode("//IntegrationObjetItems" );
		assertThat(result).isEmpty();
	}

	@Test
	public void postHandle() throws Exception
	{
		interceptor.postHandle(request, response, null, null);
		verifyZeroInteractions(integrationObjectService);
	}

	@Test
	public void afterCompletion() throws Exception
	{
		interceptor.afterCompletion(request, response, null, null);
		verifyZeroInteractions(integrationObjectService);
	}
}
