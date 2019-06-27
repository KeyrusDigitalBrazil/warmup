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
package de.hybris.platform.outboundservices.decorator.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPayloadBuildingRequestDecoratorUnitTest
{
	@Mock
	private IntegrationObjectConversionService conversionService;
	@InjectMocks
	private DefaultPayloadBuildingRequestDecorator decorator;

	@Mock
	private DecoratorExecution execution;
	@InjectMocks
	private DecoratorContext decoratorContext;

	@Test
	public void testDecorate()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		final Map<String, Object> payload = Maps.newHashMap();
		payload.put("EXISTING_ATTR", "EXISTING VALUE");

		final Map<String, Object> conversionMap = Maps.newHashMap();
		conversionMap.put("HEADER", "VALUE");

		when(conversionService.convert(any(), any())).thenReturn(conversionMap);

		decorator.decorate(httpHeaders, payload, decoratorContext, execution);

		assertThat(payload).contains(entry("HEADER", "VALUE"), entry("EXISTING_ATTR", "EXISTING VALUE"));
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);
	}

	@Test
	public void testDecoratorDoesNotHandleExceptions()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		doThrow(NullPointerException.class).when(conversionService).convert(any(), any());

		assertThatThrownBy(() ->
				decorator.decorate(httpHeaders, Maps.newHashMap(), decoratorContext, execution))
				.isInstanceOf(NullPointerException.class);
	}
}
