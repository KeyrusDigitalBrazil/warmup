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
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;

import java.util.Collections;
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
public class DefaultAcceptHeaderRequestDecoratorUnitTest
{
	@InjectMocks
	private DefaultAcceptHeaderRequestDecorator decorator;

	@Mock
	private DecoratorExecution execution;

	@Test
	public void testDecorate()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		final Map<String, Object> payload = Maps.newHashMap();
		final DecoratorContext context = mock(DecoratorContext.class);

		decorator.decorate(httpHeaders, payload, context, execution);

		assertThat(httpHeaders).contains(entry("Accept", Collections.singletonList("application/json")));
		verify(execution).createHttpEntity(httpHeaders, payload, context);
	}
}
