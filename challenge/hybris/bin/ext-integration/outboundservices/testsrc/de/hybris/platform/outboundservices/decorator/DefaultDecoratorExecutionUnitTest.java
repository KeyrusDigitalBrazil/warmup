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
package de.hybris.platform.outboundservices.decorator;

import static de.hybris.platform.outboundservices.decorator.DecoratorContext.decoratorContextBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDecoratorExecutionUnitTest
{
	private static final String MY_HEADER = "MY_HEADER";
	private static final String MY_ATTRIBUTE = "MY_ATTRIBUTE";

	private DefaultDecoratorExecution decoratorExecution;
	private List<OutboundRequestDecorator> decorators = Lists.newArrayList();
	private HttpHeaders httpHeaders = new HttpHeaders();
	private Map<String, Object> payload = Maps.newHashMap();
	private DecoratorContext context = decoratorContextBuilder().withDestinationModel(mock(ConsumedDestinationModel.class))
																.withItemModel(mock(ItemModel.class))
																.withIntegrationObjectCode("IntegrationObjectCode")
																.build();

	@Test
	public void testExecutionWithEmptyIterator()
	{
		httpHeaders.add(MY_HEADER, "my-value");
		payload.put(MY_ATTRIBUTE, "my-attribute");

		decoratorExecution = new DefaultDecoratorExecution(decorators.iterator());
		final HttpEntity<Map<String, Object>> httpEntity = decoratorExecution.createHttpEntity(httpHeaders, payload, context);

		assertThat(httpEntity).isNotNull();
		assertThat(httpEntity.getHeaders()).contains(entry(MY_HEADER, Collections.singletonList("my-value")));
		assertThat(httpEntity.getBody()).contains(entry(MY_ATTRIBUTE, "my-attribute"));
	}

	@Test
	public void testExecutionWithOneDecorator()
	{
		httpHeaders.add(MY_HEADER, "my-value");
		payload.put(MY_ATTRIBUTE, "my-attribute");

		final OutboundRequestDecorator decorator = mockDecorator("d1");
		decorators.add(decorator);
		decoratorExecution = new DefaultDecoratorExecution(decorators.iterator());
		final HttpEntity<Map<String, Object>> httpEntity = decoratorExecution.createHttpEntity(httpHeaders, payload, context);

		assertThat(httpEntity).isNotNull();
		assertThat(httpEntity.getHeaders()).contains(
				entry(MY_HEADER, Collections.singletonList("my-value")),
				entry("DECORATOR-HEADER", Collections.singletonList("d1")));
		assertThat(httpEntity.getBody()).contains(entry(MY_ATTRIBUTE, "my-attribute"), entry("DECORATOR-ATTR", "d1"));
	}

	@Test
	public void testExecutionWithMultipleDecorators()
	{
		httpHeaders.add(MY_HEADER, "my-value");
		payload.put(MY_ATTRIBUTE, "my-attribute");

		final OutboundRequestDecorator decorator1 = mockDecorator("d1");
		final OutboundRequestDecorator decorator2 = mockDecorator("d2");
		final OutboundRequestDecorator decorator3 = mockDecorator("d3");

		decorators.add(decorator1);
		decorators.add(decorator2);
		decorators.add(decorator3);
		decoratorExecution = new DefaultDecoratorExecution(decorators.iterator());
		final HttpEntity<Map<String, Object>> httpEntity = decoratorExecution.createHttpEntity(httpHeaders, payload, context);

		assertThat(httpEntity).isNotNull();
		assertThat(httpEntity.getHeaders()).contains(
				entry(MY_HEADER, Collections.singletonList("my-value")),
				entry("DECORATOR-HEADER", Arrays.asList("d1", "d2", "d3")));
		assertThat(httpEntity.getBody()).contains(entry(MY_ATTRIBUTE, "my-attribute"), entry("DECORATOR-ATTR", "d3"));
	}

	@Test
	public void testExecutionWithMultipleDecorators_havingAnStopDecorator()
	{
		httpHeaders.add(MY_HEADER, "my-value");
		payload.put(MY_ATTRIBUTE, "my-attribute");

		final OutboundRequestDecorator decorator1 = mockDecorator("d1");
		final OutboundRequestDecorator decorator2 = mockStopDecorator("d2");
		final OutboundRequestDecorator decorator3 = mockDecorator("d3");

		decorators.add(decorator1);
		decorators.add(decorator2);
		decorators.add(decorator3);
		decoratorExecution = new DefaultDecoratorExecution(decorators.iterator());
		final HttpEntity<Map<String, Object>> httpEntity = decoratorExecution.createHttpEntity(httpHeaders, payload, context);

		assertThat(httpEntity).isNotNull();
		assertThat(httpEntity.getHeaders()).contains(
				entry(MY_HEADER, Collections.singletonList("my-value")),
				entry("DECORATOR-HEADER", Arrays.asList("d1", "d2")));
		assertThat(httpEntity.getBody()).contains(entry(MY_ATTRIBUTE, "my-attribute"), entry("DECORATOR-ATTR", "d2"));
	}

	private OutboundRequestDecorator mockDecorator(final String name)
	{
		final OutboundRequestDecorator decorator = mock(OutboundRequestDecorator.class);

		when(decorator.decorate(eq(httpHeaders), eq(payload), eq(context), any())).then(call -> {
			final HttpHeaders h = call.getArgumentAt(0, HttpHeaders.class);
			final Map<String, Object> p = call.getArgumentAt(1, Map.class);
			final DecoratorContext c = call.getArgumentAt(2, DecoratorContext.class);
			final DecoratorExecution e = call.getArgumentAt(3, DecoratorExecution.class);
			h.add("DECORATOR-HEADER", name);
			p.put("DECORATOR-ATTR", name);
			return e.createHttpEntity(h, p, c);
		});

		return decorator;
	}

	private OutboundRequestDecorator mockStopDecorator(final String name)
	{
		final OutboundRequestDecorator decorator = mock(OutboundRequestDecorator.class);

		when(decorator.decorate(eq(httpHeaders), eq(payload), eq(context), any())).then(call -> {
			final HttpHeaders h = call.getArgumentAt(0, HttpHeaders.class);
			final Map<String, Object> p = call.getArgumentAt(1, Map.class);
			h.add("DECORATOR-HEADER", name);
			p.put("DECORATOR-ATTR", name);
			return new HttpEntity<>(p, h);
		});

		return decorator;
	}
}
