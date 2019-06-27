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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class BackofficeResponseHeadersFilterTest
{
	@Mock
	ServletRequest servletRequest;
	@Mock
	ServletResponse servletResponse;
	@Mock
	FilterChain filterChain;
	@Mock
	private BackofficeResponseHeadersHandler backofficeResponseHeadersHandler;

	@InjectMocks
	private final BackofficeResponseHeadersFilter backofficeResponseHeadersFilter = new BackofficeResponseHeadersFilter();

	@Test
	public void testDoFilter() throws IOException, ServletException
	{
		// when
		backofficeResponseHeadersFilter.doFilter(servletRequest, servletResponse, filterChain);

		// then
		verify(backofficeResponseHeadersHandler).handleResponseHeaders(servletRequest, servletResponse);
		verify(filterChain).doFilter(servletRequest, servletResponse);
	}

	@Test(expected = ServletException.class)
	public void testDoFilterWithServletException() throws IOException, ServletException
	{
		// given
		doThrow(ServletException.class).when(filterChain).doFilter(servletRequest, servletResponse);

		// when
		backofficeResponseHeadersFilter.doFilter(servletRequest, servletResponse, filterChain);

		// then ServletException is thrown
	}

	@Test(expected = IOException.class)
	public void testDoFilterWithIOException() throws IOException, ServletException
	{
		// given
		doThrow(IOException.class).when(filterChain).doFilter(servletRequest, servletResponse);

		// when
		backofficeResponseHeadersFilter.doFilter(servletRequest, servletResponse, filterChain);

		// then IOException is thrown
	}

}
