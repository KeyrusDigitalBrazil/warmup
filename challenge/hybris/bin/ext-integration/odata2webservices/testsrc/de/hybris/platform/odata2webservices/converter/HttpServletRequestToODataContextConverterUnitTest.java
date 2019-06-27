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
package de.hybris.platform.odata2webservices.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.ODataContextGenerator;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class HttpServletRequestToODataContextConverterUnitTest
{
	@Mock
	private Converter<HttpServletRequest, ODataRequest> requestConverter;
	@Mock
	private ODataContextGenerator oDataContextGenerator;
	@InjectMocks
	private HttpServletRequestToODataContextConverter converter;

	@Test
	public void testConvertSuccessful()
	{
		final ODataContext context = mock(ODataContext.class);
		when(requestConverter.convert(any(HttpServletRequest.class))).thenReturn(mock(ODataRequest.class));
		when(oDataContextGenerator.generate(any(ODataRequest.class))).thenReturn(context);

		assertThat(converter.convert(mock(HttpServletRequest.class))).isEqualTo(context);
	}
}