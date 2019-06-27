/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmswebservices.resolvers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmswebservices.resolvers.impl.DefaultRequestMethodResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;


import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultRequestMethodResolverTest
{
	@InjectMocks
	private DefaultRequestMethodResolver requestMethodResolver;

	@Mock
	private HttpServletRequest request;

	private List<String> postToGetUriList = new ArrayList<>();

	private String actualUri = "/post/wow/request/uri1";
	private String uriPostRegex1 = "/post/*/request/uri1";
	private String uriPostRegex2 = "/post/*/request/uri2";

	@Before
	public void start()
	{
		postToGetUriList.add(uriPostRegex1);
		postToGetUriList.add(uriPostRegex2);
		requestMethodResolver.setPostToGetUriList(postToGetUriList);
		requestMethodResolver.setPathMatcher(new AntPathMatcher());
	}

	@Test
	public void shouldReturnGetForQualifiedPostRequest()
	{
		// GIVEN
		when(request.getMethod()).thenReturn(POST.name());
		when(request.getServletPath()).thenReturn(actualUri);

		// WHEN
		String methodName = requestMethodResolver.resolvePostToGet(request);

		// THEN
		assertThat(methodName, is(GET.name()));
	}

	@Test
	public void shouldReturnPostForNotQualifiedPostRequest()
	{
		// GIVEN
		when(request.getMethod()).thenReturn(POST.name());
		when(request.getServletPath()).thenReturn("/not/qualified/request");

		// WHEN
		String methodName = requestMethodResolver.resolvePostToGet(request);

		// THEN
		assertThat(methodName, is(POST.name()));
	}

	@Test
	public void shouldReturnRequestMethodIfNotPost()
	{
		// GIVEN
		when(request.getMethod()).thenReturn(DELETE.name());
		when(request.getServletPath()).thenReturn(actualUri);

		// WHEN
		String methodName = requestMethodResolver.resolvePostToGet(request);

		// THEN
		assertThat(methodName, is(DELETE.name()));
	}
}
