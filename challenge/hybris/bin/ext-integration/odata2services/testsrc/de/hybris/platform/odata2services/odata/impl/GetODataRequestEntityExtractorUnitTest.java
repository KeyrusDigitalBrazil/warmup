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
package de.hybris.platform.odata2services.odata.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.IncorrectQueryParametersException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GetODataRequestEntityExtractorUnitTest
{
	private static final String ENTITY_NAME = "Product1";
	private static final String METADATA_REQUEST_URI = "https://localhost:9002/odata2webservices/MyProduct/$metadata";
	
	private final GetODataRequestEntityExtractor getODataRequestHandler = new GetODataRequestEntityExtractor();
	
	@Test
	public void testIsApplicableTrue()
	{
		final ODataRequest request = givenGetRequest(givenValidQueryParams());
		assertThat(getODataRequestHandler.isApplicable(request)).isTrue();
	}

	@Test
	public void testIsApplicableFalseWhenRequestIsPost()
	{
		final ODataRequest request = givenRequest(HttpMethod.POST, givenValidQueryParams(), METADATA_REQUEST_URI);
		assertThat(getODataRequestHandler.isApplicable(request)).isFalse();
	}

	@Test
	public void testIsApplicableFalseWhenRequestIsNotForMetadata()
	{
		final ODataRequest request = givenRequest(HttpMethod.GET, givenValidQueryParams(), "https://localhost:9002/odata2webservices/MyProduct/Products");
		assertThat(getODataRequestHandler.isApplicable(request)).isFalse();
	}

	@Test
	public void testIsApplicableFalseWhenRequestHasNoQueryParameter()
	{
		final ODataRequest request = givenGetRequest(new HashMap<>());
		assertThat(getODataRequestHandler.isApplicable(request)).isFalse();
	}

	@Test
	public void testExtractThrowsExceptionWhenRequestHasMoreThanOneQueryParameter()
	{
		final ODataRequest request = givenGetRequest(givenInvalidQueryParams());
		assertThatThrownBy(() -> getODataRequestHandler.extract(request)).isInstanceOf(IncorrectQueryParametersException.class);
	}
	
	@Test
	public void testExtract()
	{
		final ODataRequest request = givenGetRequest(givenValidQueryParams());
		assertThat(getODataRequestHandler.extract(request)).isEqualTo(ENTITY_NAME);
	}

	private static ODataRequest givenRequest(final HttpMethod httpMethod, final Map<String, String> queryParameters, final String requestUri)
	{
		final PathInfo pathInfo = mock(PathInfo.class);
		final ODataRequest request = mock(ODataRequest.class);
		when(request.getHttpMethod()).thenReturn(httpMethod.name());
		when(request.getQueryParameters()).thenReturn(queryParameters);
		when(pathInfo.getRequestUri()).thenReturn(URI.create(requestUri));
		when(request.getPathInfo()).thenReturn(pathInfo);
		return request;
	}

	private static ODataRequest givenGetRequest(final Map<String, String> queryParameters)
	{
		return givenRequest(HttpMethod.GET, queryParameters, METADATA_REQUEST_URI);
	}

	private Map<String, String> givenValidQueryParams()
	{
		final Map<String, String> queryParams = new HashMap<>();
		queryParams.put(ENTITY_NAME, "");
		return queryParams;
	}

	private  Map<String, String> givenInvalidQueryParams()
	{
		final Map<String, String> queryParams = givenValidQueryParams();
		//query parameters are invalid if there is more than 1 entry in the map
		queryParams.put("AdditionalParam", "MakingParamsInvalid");
		return queryParams;
	}
}