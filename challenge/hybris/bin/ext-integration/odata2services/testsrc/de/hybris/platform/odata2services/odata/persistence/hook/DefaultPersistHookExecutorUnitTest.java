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
package de.hybris.platform.odata2services.odata.persistence.hook;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.impl.DefaultPersistHookExecutor;
import de.hybris.platform.odata2services.odata.persistence.hook.impl.PersistenceHookRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPersistHookExecutorUnitTest
{

	@InjectMocks
	private final DefaultPersistHookExecutor executor = new DefaultPersistHookExecutor();
	@Mock
	private PersistenceHookRegistry registry;
	@Mock
	private ItemModel item;
	@Mock
	private PrePersistHook prePersistHook;
	@Mock
	private PostPersistHook postPersistHook;
	private static final String PRE_PERSIST_HOOK_NAME = "BEFORE_SAVE";
	private static final String POST_PERSIST_HOOK_NAME = "AFTER_SAVE";
	private static final String INTEGRATION_KEY = "integratinonKey|Value";

	@Test
	public void testRunPrePersistHookExecutes()
	{
		givenPrePersistHook(PRE_PERSIST_HOOK_NAME);

		executor.runPrePersistHook(PRE_PERSIST_HOOK_NAME, item, INTEGRATION_KEY);

		verify(prePersistHook).execute(item);
	}

	@Test
	public void testPrePersistHooksExceptionIncludesHookNameInMessage()
	{
		final PrePersistHookException prePersistHookException = new PrePersistHookException("message", new RuntimeException("Expected test exception"), INTEGRATION_KEY);

		doThrow(prePersistHookException).when(prePersistHook).execute(item);
		givenPrePersistHook(PRE_PERSIST_HOOK_NAME);

		assertThatThrownBy(() -> executor.runPrePersistHook(PRE_PERSIST_HOOK_NAME, item, INTEGRATION_KEY))
				.isInstanceOf(PrePersistHookException.class)
				.hasMessageContaining(PRE_PERSIST_HOOK_NAME)
				.hasCause(prePersistHookException);
	}

	@Test
	public void testRunPostPersistHook()
	{
		givenPostPersistHook(POST_PERSIST_HOOK_NAME);

		executor.runPostPersistHook(POST_PERSIST_HOOK_NAME, item, INTEGRATION_KEY);

		verify(postPersistHook).execute(item);
	}

	@Test
	public void testExceptionThrownInsidePostPersistHook()
	{
		givenPostPersistHook(POST_PERSIST_HOOK_NAME);
		final PostPersistHookException e = new PostPersistHookException("message", new RuntimeException("Expected test exception"), INTEGRATION_KEY);

		doThrow(e).when(postPersistHook).execute(item);
		givenPostPersistHook(POST_PERSIST_HOOK_NAME);

		assertThatThrownBy(() -> executor.runPostPersistHook(POST_PERSIST_HOOK_NAME, item, INTEGRATION_KEY))
				.isInstanceOf(PostPersistHookException.class)
				.hasMessageContaining(POST_PERSIST_HOOK_NAME)
				.hasCause(e);
	}

	@Test
	public void testIgnoresNullPrePersistHookName()
	{
		givenPostPersistHook(POST_PERSIST_HOOK_NAME);
		
		executor.runPrePersistHook(PRE_PERSIST_HOOK_NAME, item, INTEGRATION_KEY);

		verifyZeroInteractions(prePersistHook);
	}

	@Test
	public void testIgnoresEmptyPrePersistHookName()
	{
		givenPostPersistHook(POST_PERSIST_HOOK_NAME);

		executor.runPrePersistHook(PRE_PERSIST_HOOK_NAME, item, INTEGRATION_KEY);

		verifyZeroInteractions(prePersistHook);
	}

	@Test
	public void testIgnoresNullPostPersistHookName()
	{
		givenPrePersistHook(PRE_PERSIST_HOOK_NAME);

		executor.runPostPersistHook(POST_PERSIST_HOOK_NAME, item, INTEGRATION_KEY);

		verifyZeroInteractions(prePersistHook);
	}

	@Test
	public void testIgnoresEmptyPostPersistHookName()
	{
		givenPrePersistHook(PRE_PERSIST_HOOK_NAME);

		executor.runPostPersistHook(POST_PERSIST_HOOK_NAME, item, INTEGRATION_KEY);

		verifyZeroInteractions(prePersistHook);
	}

	private void givenPostPersistHook(final String hookName)
	{
		when(registry.getPostPersistHook(hookName, INTEGRATION_KEY)).thenReturn(postPersistHook);
	}

	private void givenPrePersistHook(final String hookName)
	{
		when(registry.getPrePersistHook(hookName, INTEGRATION_KEY)).thenReturn(prePersistHook);
	}
}