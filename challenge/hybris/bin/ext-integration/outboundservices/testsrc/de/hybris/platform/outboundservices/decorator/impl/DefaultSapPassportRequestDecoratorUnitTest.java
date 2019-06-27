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

import static de.hybris.platform.outboundservices.decorator.DecoratorContext.decoratorContextBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.service.SapPassportService;
import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapPassportRequestDecoratorUnitTest
{
	@Mock
	private SapPassportService sapPassportService;

	@InjectMocks
	private DefaultSapPassportRequestDecorator decorator;

	@Mock
	private DecoratorExecution execution;

	private DecoratorContext decoratorContext;

	@Before
	public void setUp()
	{
		decoratorContext = decoratorContextBuilder().withDestinationModel(mock(ConsumedDestinationModel.class))
													.withItemModel(mock(ItemModel.class))
													.withIntegrationObjectCode("MyIntegrationObject")
													.build();

	}

	@Test
	public void testDecorator()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		final Map<String, Object> payload = Maps.newHashMap();

		when(sapPassportService.generate(any())).thenReturn("MY-PASSPORT");

		decorator.decorate(httpHeaders, payload, decoratorContext, execution);

		verify(sapPassportService).generate("MyIntegrationObject");
		verify(execution).createHttpEntity(httpHeaders, payload, decoratorContext);

		assertThat(httpHeaders).contains(entry("SAP-PASSPORT", Collections.singletonList("MY-PASSPORT")));
	}

	@Test
	public void testDecoratorDoesNotHandleExceptions()
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		doThrow(NullPointerException.class).when(sapPassportService).generate(any());

		assertThatThrownBy(() ->
				decorator.decorate(httpHeaders, Maps.newHashMap(), decoratorContext, execution))
				.isInstanceOf(NullPointerException.class);
	}
}
