/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.common.service.impl;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.InvalidTypeException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSessionCachedContextProviderTest
{
	private static final String CACHED_KEY = "test-key";
	private static final String ITEM_VALUE = "item-value";

	@Spy
	@InjectMocks
	private DefaultSessionCachedContextProvider sessionCachedContextProvider;

	@Mock
	private SessionService sessionService;
	@Mock
	private AtomicReference atomicReference;

	@Test
	public void shouldCreateSetInCache()
	{
		sessionCachedContextProvider.getOrCreateCollectionInCache(CACHED_KEY, HashSet.class);
		verify(sessionService).setAttribute(anyString(), any());
	}

	@Test
	public void shouldGetExistingSetFromCache()
	{
		when(sessionService.getAttribute(CACHED_KEY)).thenReturn(atomicReference);

		sessionCachedContextProvider.getOrCreateCollectionInCache(CACHED_KEY, HashSet.class);

		verify(sessionService, never()).setAttribute(anyString(), any());
	}

	@Test(expected = InvalidTypeException.class)
	public void shouldFailToInstantiateCollectionInCache()
	{
		sessionCachedContextProvider.getOrCreateCollectionInCache(CACHED_KEY, Set.class);
	}

	@Test
	public void shouldCreateSetInCacheAndAddItemToSet()
	{
		sessionCachedContextProvider.addItemToSetCache(CACHED_KEY, ITEM_VALUE);

		verify(sessionService).setAttribute(anyString(), any());
		verify(sessionCachedContextProvider).getWrappedObject(any());
	}

	@Test
	public void shouldAddItemToExistingSetInCache()
	{
		when(sessionService.getAttribute(CACHED_KEY)).thenReturn(atomicReference);
		when(sessionCachedContextProvider.getWrappedObject(atomicReference)).thenReturn(new HashSet<>());
		sessionCachedContextProvider.addItemToSetCache(CACHED_KEY, ITEM_VALUE);

		verify(sessionService, never()).setAttribute(anyString(), any());
		verify(sessionCachedContextProvider).getWrappedObject(any());
	}

	@Test
	public void shouldGetWrappedObjectByCacheKey()
	{
		when(sessionService.getAttribute(CACHED_KEY)).thenReturn(new AtomicReference<List>(Arrays.asList("foo", "bar")));

		final List<String> wrappedList = sessionCachedContextProvider.getAllItemsFromListCache(CACHED_KEY);

		assertThat(wrappedList, contains("foo", "bar"));
	}

	@Test
	public void givenKeyIsNotCached_WhenHasCacheKeyIsCalled_ThenItMustReturnFalse()
	{
		// GIVEN
		when(sessionService.getAttribute(CACHED_KEY)).thenReturn(null);

		// WHEN
		final boolean result = sessionCachedContextProvider.hasCacheKey(CACHED_KEY);

		// THEN
		assertFalse(result);
	}

	@Test
	public void givenKeyIsCached_WhenHasCacheKeyIsCalled_ThenItMustReturnTrue()
	{
		// GIVEN
		when(sessionService.getAttribute(CACHED_KEY)).thenReturn(Collections.emptyList());

		// WHEN
		final boolean result = sessionCachedContextProvider.hasCacheKey(CACHED_KEY);

		// THEN
		assertTrue(result);
	}

	@Test
	public void whenCreateEmptyListCacheIsCalled_ThenItMustCreateAnEmptyListCache()
	{
		// WHEN
		sessionCachedContextProvider.createEmptyListCache(CACHED_KEY);

		// THEN
		verify(sessionCachedContextProvider).getOrCreateListCollectionInCache(CACHED_KEY);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailToGetWrappedObjectInvalidInputType()
	{
		sessionCachedContextProvider.getWrappedObject(null);
	}

}
