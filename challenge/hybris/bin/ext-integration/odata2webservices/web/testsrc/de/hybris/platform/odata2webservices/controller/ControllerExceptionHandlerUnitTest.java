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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2services.odata.ODataWebException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataForbiddenException;
import org.apache.olingo.odata2.api.exception.ODataHttpException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ControllerExceptionHandlerUnitTest
{
	@Mock
	private Converter<ODataResponse, ResponseEntity<String>> oDataResponseToResponseEntityConverter;
	@InjectMocks
	private ControllerExceptionHandler controllerExceptionHandler;
	@Captor
	private ArgumentCaptor<ODataResponse> oDataResponseArgumentCaptor;

	@Before
	public void setup()
	{
		controllerExceptionHandler = new ControllerExceptionHandler(mock(ODataServiceFactory.class), oDataResponseToResponseEntityConverter);
	}

	@Test
	public void testHandleODataHttpException()
	{
		final ODataHttpException exception = new ODataForbiddenException(ODataForbiddenException.COMMON);

		whenHandlingODataHttpException(exception);

		verifyODataResponse(HttpStatusCodes.FORBIDDEN);
	}

	@Test
	public void testHandleException()
	{
		final Exception exception = new Exception("It's ok, just testing exception handling");

		whenHandlingException(exception);

		verifyODataResponse(HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testHandleExceptionWhenCauseContainsAnODataHttpException()
	{
		final Exception exception = new ODataWebException("Just an expected exception in test", new ODataNotFoundException(ODataNotFoundException.ENTITY));

		whenHandlingException(exception);

		verifyODataResponse(HttpStatusCodes.NOT_FOUND);
	}

	@Test
	public void testHandleInvalidODataSchemaExceptionWithMessage() throws Exception
	{
		final InvalidODataSchemaException invalidSchemaException = new InvalidODataSchemaException(new RuntimeException("Testing InvalidODataSchemaException"));

		whenHandlingInvalidODataSchemaException(invalidSchemaException);

		verifyODataResponse(HttpStatusCodes.BAD_REQUEST, "EDMX schema could not be generated.",  "Testing InvalidODataSchemaException");
	}

	@Test
	public void testHandleInvalidODataSchemaExceptionWithMessageWithNullCause() throws Exception
	{
		final InvalidODataSchemaException invalidSchemaException = new InvalidODataSchemaException(null);

		whenHandlingInvalidODataSchemaException(invalidSchemaException);

		verifyODataResponse(HttpStatusCodes.BAD_REQUEST, "EDMX schema could not be generated.");
	}

	private void whenHandlingInvalidODataSchemaException(final InvalidODataSchemaException invalidSchemaException)
	{
		captureODataResponse();
		controllerExceptionHandler.handleInvalidODataSchemaException(getRequest(), invalidSchemaException);
	}

	private void whenHandlingODataHttpException(final ODataHttpException exception)
	{
		captureODataResponse();
		controllerExceptionHandler.handleODataHttpException(getRequest(), exception);
	}

	private void whenHandlingException(final Exception exception)
	{
		captureODataResponse();
		controllerExceptionHandler.handleException(getRequest(), exception);
	}

	private HttpServletRequest getRequest()
	{
		return MockMvcRequestBuilders.get("/$metadata")
				.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML).buildRequest(null);
	}

	private void captureODataResponse()
	{
		when(oDataResponseToResponseEntityConverter.convert(oDataResponseArgumentCaptor.capture())).thenReturn(ResponseEntity.badRequest().build());
	}

	private void verifyODataResponse(final HttpStatusCodes status)
	{
		final ODataResponse oDataResponse = oDataResponseArgumentCaptor.getValue();
		assertThat(oDataResponse.getStatus()).isEqualTo(status);
	}

	private void verifyODataResponse(final HttpStatusCodes status, final String... messages) throws Exception
	{
		final ODataResponse oDataResponse = oDataResponseArgumentCaptor.getValue();
		final String returnMessage = IOUtils.toString(oDataResponse.getEntityAsStream(), StandardCharsets.UTF_8);
		Arrays.asList(messages).forEach(msg -> assertThat(returnMessage).contains(msg));
		verifyODataResponse(status);
	}
}