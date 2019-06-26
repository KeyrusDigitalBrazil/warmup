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
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2webservices.converter.HttpServletRequestToODataContextConverter;
import de.hybris.platform.odata2webservices.converter.ODataResponseToResponseEntityConverter;
import de.hybris.platform.odata2webservices.odata.ODataFacade;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IntegrationDataControllerUnitTest
{
	private IntegrationDataController integrationDataController;
	@Mock
	private ODataFacade oDataFacade;
	@Mock
	private HttpServletRequestToODataContextConverter httpServletRequestToODataContextConverter;
	@Mock
	private ODataResponseToResponseEntityConverter oDataResponseToResponseEntityConverter;
	@Mock
	private HttpServletRequest request;
	@Mock
	private ODataContext context;
	@Mock
	private ResponseEntity<String> expectedResponse;

	@Before
	public void setUp()
	{
		integrationDataController = new IntegrationDataController(oDataFacade, oDataResponseToResponseEntityConverter, httpServletRequestToODataContextConverter);
		when(httpServletRequestToODataContextConverter.convert(request)).thenReturn(context);
	}

	@Test
	public void testGetSchema()
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		when(oDataFacade.handleGetSchema(context)).thenReturn(oDataResponse);
		when(oDataResponseToResponseEntityConverter.convert(oDataResponse)).thenReturn(expectedResponse);

		final ResponseEntity<String> responseEntity = integrationDataController.getSchema(request);

		assertThat(responseEntity).isSameAs(expectedResponse);
	}

	@Test
	public void testPostBatch()
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		when(oDataFacade.handlePost(context)).thenReturn(oDataResponse);
		when(oDataResponseToResponseEntityConverter.convert(oDataResponse)).thenReturn(expectedResponse);

		final ResponseEntity<String> responseEntity = integrationDataController.handleBatch(request);

		assertThat(responseEntity).isSameAs(expectedResponse);
	}

	@Test
	public void testGetEntity()
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		when(oDataFacade.handleGetEntity(context)).thenReturn(oDataResponse);
		when(oDataResponseToResponseEntityConverter.convert(oDataResponse)).thenReturn(expectedResponse);

		final ResponseEntity<String> responseEntity = integrationDataController.getEntity(request);

		assertThat(responseEntity).isSameAs(expectedResponse);
	}

	@Test
	public void testGetPropertyFromEntity()
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		when(oDataFacade.handleGetEntity(context)).thenReturn(oDataResponse);
		when(oDataResponseToResponseEntityConverter.convert(oDataResponse)).thenReturn(expectedResponse);

		final ResponseEntity<String> responseEntity = integrationDataController.getPropertyFromEntity(request);

		assertThat(responseEntity).isSameAs(expectedResponse);
	}

	@Test
	public void testCreateOrUpdateEntity()
	{
		final ODataResponse oDataResponse = mock(ODataResponse.class);
		when(oDataFacade.handlePost(context)).thenReturn(oDataResponse);
		when(oDataResponseToResponseEntityConverter.convert(oDataResponse)).thenReturn(expectedResponse);

		final ResponseEntity<String> responseEntity = integrationDataController.handleCreateOrUpdateEntity(request);

		assertThat(responseEntity).isSameAs(expectedResponse);
	}
}
