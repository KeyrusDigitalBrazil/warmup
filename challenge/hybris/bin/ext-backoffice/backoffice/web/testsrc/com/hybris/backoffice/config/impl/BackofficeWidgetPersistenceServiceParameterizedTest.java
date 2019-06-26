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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.hybris.cockpitng.core.persistence.impl.jaxb.WidgetConnection;
import com.hybris.cockpitng.core.persistence.impl.jaxb.WidgetExtension;
import com.hybris.cockpitng.core.persistence.impl.jaxb.Widgets;
import com.hybris.cockpitng.modules.CockpitModuleConnector;


@RunWith(Parameterized.class)
public class BackofficeWidgetPersistenceServiceParameterizedTest
{

	@InjectMocks
	private BackofficeWidgetPersistenceServiceUnitTest.BackofficeWidgetPersistenceServiceStub service;
	@Mock
	private CockpitModuleConnector cockpitModuleConnector;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Parameterized.Parameters
	public static Collection<List[]> data() throws ParseException
	{
		return Arrays.asList(new List[][]
		{
				{
						// input
						Arrays.asList("b", "a", "b", null, "a"), //
						// returned by platform service
						Arrays.asList("a", "b", "c", "d"), //
						// expected
						Arrays.asList(null, "a", "a", "b", "b") },
				{
						// input
						Arrays.asList("a", "b", "c", null, "d"), //
						// returned by platform service
						Arrays.asList("a", "b", "c", "d"), //
						// expected
						Arrays.asList(null, "a", "b", "c", "d") },
				{
						// input
						Arrays.asList("c", "a", null, "a", null), //
						// returned by platform service
						Arrays.asList("a", "b", "c", "d"), //
						// expected
						Arrays.asList(null, null, "a", "a", "c") }

		});
	}

	@Parameterized.Parameter(0)
	public List<String> input;

	@Parameterized.Parameter(1)
	public List<String> returnedByPlatformService;

	@Parameterized.Parameter(2)
	public List<String> expected;

	@Test
	public void shouldExtensionsBeSortedCorrectly()
	{
		// given
		final Widgets root = mock(Widgets.class);
		final List<WidgetExtension> extensions = input.stream().map(this::mockWidgetExtension).collect(Collectors.toList());
		given(root.getWidgetExtension()).willReturn(extensions);
		given(cockpitModuleConnector.getCockpitModuleUrls()).willReturn(returnedByPlatformService);

		// when
		final List<WidgetExtension> extractedExtensions = service.extractWidgetExtensions(root);

		// then
		assertThat(extractedExtensions).extracting(WidgetExtension::getContextId).isEqualTo(expected);
	}

	@Test
	public void shouldConnectionsBeSortedCorrectly()
	{
		// given
		final Widgets root = mock(Widgets.class);
		final List<WidgetConnection> connections = input.stream().map(this::mockWidgetConnections).collect(Collectors.toList());
		given(root.getWidgetConnection()).willReturn(connections);
		given(cockpitModuleConnector.getCockpitModuleUrls()).willReturn(returnedByPlatformService);

		// when
		service.sortWidgetConnections(root);

		// then 
		assertThat(root.getWidgetConnection()).extracting(WidgetConnection::getModuleUrl).isEqualTo(expected);
	}

	protected WidgetExtension mockWidgetExtension(final String contextId)
	{
		final WidgetExtension extension = mock(WidgetExtension.class);
		when(extension.getContextId()).thenReturn(contextId);
		return extension;
	}

	protected WidgetConnection mockWidgetConnections(final String contextId)
	{
		final WidgetConnection connection = mock(WidgetConnection.class);
		when(connection.getModuleUrl()).thenReturn(contextId);
		when(connection.toString()).thenReturn(contextId);
		return connection;
	}

}
