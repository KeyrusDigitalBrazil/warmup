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
package com.hybris.backoffice.mobile.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class BackofficeMobileFilterTest
{

	private final static String EMPTY_HEADER_VALUE = "";
	private final static String IPHONE_HEADER_VALUE = "cpu iphone os 3_0 like mac os x";
	private final static String MAC_HEADER_VALUE = "cpu macintosh os 3_0 like mac os x";
	private final static String ANDROID_HEADER_VALUE = "android 3.0.1";
	private final static String CHROME_HEADER_VALUE = "chrome 3.0.1";

	@Parameter(0)
	public String headerValue;

	@Parameter(1)
	public String expectedHeaderValue;

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][]
		{
				{ EMPTY_HEADER_VALUE, EMPTY_HEADER_VALUE },
				{ IPHONE_HEADER_VALUE, MAC_HEADER_VALUE },
				{ ANDROID_HEADER_VALUE, CHROME_HEADER_VALUE } });
	}

	@Test
	public void shouldPrepareUserAgentHeader()
	{
		// given
		final HttpServletRequest request = mock(HttpServletRequest.class);
		given(request.getHeader(BackofficeMobileFilter.USER_AGENT_HTTP_HEADER)).willReturn(headerValue);
		final BackofficeMobileFilter backofficeMobileFilter = new BackofficeMobileFilter();

		// when
		final String userAgentHeader = backofficeMobileFilter.prepareUserAgentHeader(request);

		// then
		assertThat(userAgentHeader).isEqualTo(expectedHeaderValue);
	}
}
