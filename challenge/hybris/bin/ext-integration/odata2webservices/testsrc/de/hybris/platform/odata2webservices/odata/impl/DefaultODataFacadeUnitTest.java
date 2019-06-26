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
package de.hybris.platform.odata2webservices.odata.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.EdmxProviderValidator;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2webservices.odata.DefaultIntegrationODataRequestHandler;

@UnitTest
@RunWith(PowerMockRunner.class)
@PrepareForTest({DefaultIntegrationODataRequestHandler.class})
@PowerMockIgnore({"org.apache.logging.log4j.spi.Provider", "javax.management.*"})
public class DefaultODataFacadeUnitTest
{
	private static final ODataContext ODATA_CONTEXT = mock(ODataContext.class);

	@Mock
	private EdmxProviderValidator edmxProviderValidator;
	@Mock
	private DefaultIntegrationODataRequestHandler requestHandler;
	@InjectMocks
	private DefaultODataFacade facade;

	@Before
	public void setUp()
	{
		PowerMockito.mockStatic(DefaultIntegrationODataRequestHandler.class);
		when(DefaultIntegrationODataRequestHandler.createHandler(any(ODataServiceFactory.class), any(ODataService.class), any(ODataContext.class)))
				.thenReturn(requestHandler);
		givenRequestHandlerRespondsWith(successfulResponse());
	}

	@Test
	public void testGetSchemaDelegatesToRequestHandler()
	{
		final ODataResponse response = successfulResponse();
		givenRequestHandlerRespondsWith(response);

		final ODataResponse actual = facade.handleGetSchema(ODATA_CONTEXT);

		assertThat(actual).isSameAs(response);
	}

	@Test
	public void testGetSchemaODataResponseFailedValidation()
	{
		givenResponseValidationFailure();

		assertThatThrownBy(() -> facade.handleGetSchema(ODATA_CONTEXT)).isInstanceOf(InvalidODataSchemaException.class);
	}

	@Test
	public void testGetSchemaDoesNotValidateErrorResponse()
	{
		givenRequestHandlerRespondsWith(unsuccessfulResponse());

		facade.handleGetSchema(ODATA_CONTEXT);

		verifyZeroInteractions(edmxProviderValidator);
	}

	@Test
	public void testGetEntityDelegatesToRequestHandler()
	{
		final ODataResponse response = successfulResponse();
		givenRequestHandlerRespondsWith(response);

		final ODataResponse actual = facade.handleGetEntity(ODATA_CONTEXT);

		assertThat(actual).isSameAs(response);
	}

	@Test
	public void testGetEntityODataResponseIsSuccessful()
	{
		assertThat(facade.handleGetEntity(ODATA_CONTEXT))
				.isNotNull()
				.hasFieldOrPropertyWithValue("status", HttpStatusCodes.OK);
	}

	@Test
	public void testPostDelegatesToRequestHandler()
	{
		final ODataResponse response = successfulResponse();
		givenRequestHandlerRespondsWith(response);

		final ODataResponse actual = facade.handlePost(ODATA_CONTEXT);

		assertThat(actual).isSameAs(response);
	}

	private void givenResponseValidationFailure()
	{
		doThrow(InvalidODataSchemaException.class).when(edmxProviderValidator).validateResponse(any());
	}

	private void givenRequestHandlerRespondsWith(final ODataResponse response)
	{
		doReturn(response).when(requestHandler).handle(any(ODataRequest.class));
	}

	private ODataResponse successfulResponse()
	{
		return responseWithStatus(HttpStatusCodes.OK);
	}

	private ODataResponse unsuccessfulResponse()
	{
		return responseWithStatus(HttpStatusCodes.BAD_REQUEST);
	}

	private ODataResponse responseWithStatus(final HttpStatusCodes status)
	{
		final ODataResponse response = mock(ODataResponse.class);
		doReturn(status).when(response).getStatus();
		return response;
	}
}