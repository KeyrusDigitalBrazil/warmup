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
import static org.fest.assertions.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ResourceLoader;

import com.hybris.cockpitng.core.config.impl.cache.ContextQuery;
import com.hybris.cockpitng.core.config.impl.cache.DefaultConfigurationCache;
import com.hybris.cockpitng.core.config.impl.jaxb.Config;
import com.hybris.cockpitng.core.config.impl.jaxb.Context;


@RunWith(MockitoJUnitRunner.class)
public class TestingBackofficeCockpitConfigurationServiceTest
{
	private static final String CUSTOM_CONFIGURATION_SNIPPET = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><config xmlns=\"http://www.hybris.com/cockpit/config\"><context component=\"test-component\">\n</context></config>";
	private static final String TEST_MODULE = "test-module";

	@Spy
	@InjectMocks
	private TestingBackofficeCockpitConfigurationService configurationService;

	@Mock
	private DefaultConfigurationCache configurationCache;

	@Mock
	private Config rootConfig;

	@Mock
	private Config customConfigurationMock;

	@Mock
	private ResourceLoader resourceLoader;


	@Test
	public void shouldApplyCustomConfigurationWhenCustomConfigurationSnippetProvided() throws Exception
	{
		//given
		configurationService.setCustomConfiguration(CUSTOM_CONFIGURATION_SNIPPET, TEST_MODULE);

		//when
		configurationService.cacheRootConfiguration(rootConfig, 1);

		//then
		verify(configurationService).updateMainConfig(eq(rootConfig), any(Config.class));
	}

	@Test
	public void shouldLoadCustomConfigurationWhenCustomConfigurationSnippetProvided() throws Exception
	{
		//given
		configurationService.setCustomConfiguration(CUSTOM_CONFIGURATION_SNIPPET, TEST_MODULE);

		//when

		configurationService.cacheRootConfiguration(rootConfig, 1);

		//then
		verify(configurationService).getCustomConfiguration();
	}

	@Test
	public void shouldApplyCustomConfigurationWhenCustomConfigurationProvided() throws Exception
	{
		//given
		configurationService.setCustomConfiguration(customConfigurationMock, TEST_MODULE);

		//when
		configurationService.cacheRootConfiguration(rootConfig, 1);

		//then
		verify(configurationService).updateMainConfig(eq(rootConfig), any(Config.class));
	}

	@Test
	public void shouldLoadCustomConfigurationWhenCustomConfigurationProvided() throws Exception
	{
		//given
		configurationService.setCustomConfiguration(customConfigurationMock, TEST_MODULE);

		//when

		configurationService.cacheRootConfiguration(rootConfig, 1);

		//then
		verify(configurationService).getCustomConfiguration();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenSetCustomConfigurationSetIsNull() throws Exception
	{
		//given
		final Config customConfig = null;
		configurationService.setCustomConfiguration(customConfig, TEST_MODULE);

		//then
		fail("Should do not reach this code");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenSetCustomConfigurationSnippetSetIsNull() throws Exception
	{
		//given
		final String customConfig = null;
		configurationService.setCustomConfiguration(customConfig, TEST_MODULE);

		//then
		fail("Should do not reach this code");
	}

	@Test
	public void shouldUpdateRootConfigWithContextsFromProvidedConfig() throws Exception
	{
		//given
		final Config rootConfig = mock(Config.class);
		final Config providedConfig = mock(Config.class);
		final Context context1 = mock(Context.class);
		final Context context2 = mock(Context.class);
		when(providedConfig.getContext()).thenReturn(Arrays.asList(context1, context2));

		//when
		configurationService.updateMainConfig(rootConfig, providedConfig);

		//then
		verify(configurationService, times(1)).updateMainConfig(eq(rootConfig), eq(context1));
		verify(configurationService, times(1)).updateMainConfig(eq(rootConfig), eq(context2));
	}

	@Test
	public void shouldAddProvidedContextToRootConfigWhenRootConfigNotContainsCorrespondingContexts() throws Exception
	{
		//given
		final Config rootConfig = mock(Config.class);
		final List<Context> rootContexts = mock(List.class);
		when(rootConfig.getContext()).thenReturn(rootContexts);
		final Context providedContext = mock(Context.class);
		final Object anyMock = mock(Object.class);
		when(providedContext.getAny()).thenReturn(anyMock);
		when(configurationCache.createContextQuery(eq(rootConfig), any())).thenReturn(mock(ContextQuery.class));

		//when
		configurationService.updateMainConfig(rootConfig, providedContext);

		//then
		final ArgumentCaptor<Context> addedContext = ArgumentCaptor.forClass(Context.class);
		verify(rootContexts).add(addedContext.capture());
		assertThat(addedContext.getValue().getAny()).isEqualTo(anyMock);
	}

	@Test
	public void shouldClearCustomConfigurationAndInvalidateCacheWhenMethodCalled() throws Exception
	{
		configurationService.setCustomConfiguration(CUSTOM_CONFIGURATION_SNIPPET, TEST_MODULE);

		//when
		configurationService.clearCustomConfiguration();

		//then
		assertThat(configurationService.getCustomConfiguration().isPresent()).isFalse();
		verify(configurationCache, times(2)).clear();
	}
}
