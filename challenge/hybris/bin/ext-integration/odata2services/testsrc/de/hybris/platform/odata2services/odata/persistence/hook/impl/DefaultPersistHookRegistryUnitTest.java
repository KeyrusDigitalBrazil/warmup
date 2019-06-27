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

package de.hybris.platform.odata2services.odata.persistence.hook.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.odata2services.odata.persistence.hook.PersistHookNotFoundException;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPersistHookRegistryUnitTest
{
	@InjectMocks
	private DefaultPersistenceHookRegistry registry;
	@Mock
	private ApplicationContext context;

	@Before
	public void setUp()
	{
		registry = new DefaultPersistenceHookRegistry();
	}
	private static final String INTEGRATION_KEY = "integratinonKey|Value";

	@Test
	public void testSetApplicationContext()
	{
		doReturn(ImmutableMap.of("hook", mock(PrePersistHook.class))).when(context).getBeansOfType(PrePersistHook.class);
		doReturn(ImmutableMap.of("hook", mock(PostPersistHook.class))).when(context).getBeansOfType(PostPersistHook.class);

		registry.setApplicationContext(context);

		assertThat(registry.getPrePersistHook("hook", INTEGRATION_KEY))
				.isNotNull()
				.isInstanceOf(PrePersistHook.class);
		assertThat(registry.getPostPersistHook("hook", INTEGRATION_KEY))
				.isNotNull()
				.isInstanceOf(PostPersistHook.class);
	}

	@Test
	public void testGetPrePersistHookIgnoresNullNames()
	{
		assertThat(registry.getPrePersistHook(null, INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPrePersistHookIgnoresEmptyNames()
	{
		assertThat(registry.getPrePersistHook("", INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPostPersistHookIgnoresNullNames()
	{
		assertThat(registry.getPostPersistHook(null, INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testGetPostPersistHookIgnoresEmptyNames()
	{
		assertThat(registry.getPostPersistHook("", INTEGRATION_KEY)).isNull();
	}

	@Test
	public void testPrePersistHookDoesNotExist()
	{
		assertThatThrownBy(() -> registry.getPrePersistHook("preHookName", INTEGRATION_KEY))
				.isInstanceOf(PersistHookNotFoundException.class)
				.hasMessageContaining("preHookName");
	}

	@Test
	public void testPostPersistHookDoesNotExist()
	{
		assertThatThrownBy(() -> registry.getPostPersistHook("postHookName", INTEGRATION_KEY))
				.isInstanceOf(PersistHookNotFoundException.class)
				.hasMessageContaining("postHookName");
	}

	@Test
	public void testAddPerPersistHook()
	{
		final PrePersistHook hook = mock(PrePersistHook.class);
		registry.addHook("preHook", hook);
		assertThat(registry.getPrePersistHook("preHook", INTEGRATION_KEY)).isSameAs(hook);
	}

	@Test
	public void testAddPostPersistHook()
	{
		final PostPersistHook hook = mock(PostPersistHook.class);
		registry.addHook("postHook", hook);
		assertThat(registry.getPostPersistHook("postHook", INTEGRATION_KEY)).isSameAs(hook);
	}
}