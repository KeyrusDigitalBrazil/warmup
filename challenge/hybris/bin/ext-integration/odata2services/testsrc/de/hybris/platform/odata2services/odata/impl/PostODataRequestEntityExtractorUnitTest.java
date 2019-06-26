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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.net.URI;

import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.core.PathInfoImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PostODataRequestEntityExtractorUnitTest
{
	private static final String ENTITY_NAME = "Product1";

	private final PostODataRequestEntityExtractor postODataRequestHandler = new PostODataRequestEntityExtractor();

	@Test
	public void testIsApplicableTrue()
	{
		final ODataRequest request = givenPostRequest("");
		assertThat(postODataRequestHandler.isApplicable(request)).isTrue();
	}

	@Test
	public void testIsApplicableFalse()
	{
		final ODataRequest request = givenRequest(HttpMethod.GET, "");
		assertThat(postODataRequestHandler.isApplicable(request)).isFalse();
	}

	@Test
	public void testExtractWhenEntityExists()
	{
		final ODataRequest request = givenPostRequest("/" + ENTITY_NAME);
		assertThat(postODataRequestHandler.extract(request)).isEqualTo(ENTITY_NAME);
	}

	@Test
	public void testExtractWhenEntityDoesNotExist()
	{
		final ODataRequest request = givenPostRequest("");
		assertThat(postODataRequestHandler.extract(request)).isEqualTo("");
	}

	private static ODataRequest givenPostRequest(final String uri)
	{
		return givenRequest(HttpMethod.POST, uri);
	}

	private static ODataRequest givenRequest(final HttpMethod httpMethod, final String uri)
	{
		final ODataRequest request = mock(ODataRequest.class);
		when(request.getHttpMethod()).thenReturn(httpMethod.name());

		final PathInfoImpl pathInfo = new PathInfoImpl();
		pathInfo.setRequestUri(URI.create(uri));
		when(request.getPathInfo()).thenReturn(pathInfo);
		
		return request;
	}
}

