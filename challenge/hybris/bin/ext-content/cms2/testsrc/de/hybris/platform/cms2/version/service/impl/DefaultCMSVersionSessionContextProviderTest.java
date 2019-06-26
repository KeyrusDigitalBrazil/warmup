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
package de.hybris.platform.cms2.version.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.service.SessionCachedContextProvider;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

import static de.hybris.platform.cms2.constants.Cms2Constants.CACHED_PAGE_VERSIONED_IN_TRANSACTION;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionSessionContextProviderTest
{
	@Mock
	private AbstractPageModel pageModel;

	@Mock
	private SessionCachedContextProvider sessionCachedContextProvider;

	@InjectMocks
	private DefaultCMSVersionSessionContextProvider cmsVersionSessionContextProvider;

	@Test
	public void givenEmptyOptional_WhenAddPageVersionedInTransactionToCacheIsCalled_ThenItCreatesAnEmptyCachedList()
	{
		// WHEN
		cmsVersionSessionContextProvider.addPageVersionedInTransactionToCache(Optional.empty());

		// THEN
		verify(sessionCachedContextProvider).createEmptyListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION);
	}

	@Test
	public void givenOptionalWithAValue_WhenAddPageVersionedInTransactionToCacheIsCalled_ThenItAddsTheItemToACachedList()
	{
		// WHEN
		cmsVersionSessionContextProvider.addPageVersionedInTransactionToCache(Optional.of(pageModel));

		// THEN
		verify(sessionCachedContextProvider, never()).createEmptyListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION);
		verify(sessionCachedContextProvider).addItemToListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION, pageModel);
	}

	@Test
	public void givenEmptyCachedList_WhenGetPageVersionedInTransactionFromCacheIsCalled_ThenItReturnsAnEmptyOptional()
	{
		// GIVEN
		when(sessionCachedContextProvider.getAllItemsFromListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION)).thenReturn(
				Collections.emptyList());

		// WHEN
		Optional<AbstractPageModel> result = cmsVersionSessionContextProvider.getPageVersionedInTransactionFromCache();

		// THEN
		verify(sessionCachedContextProvider).getAllItemsFromListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION);
		assertFalse(result.isPresent());
	}

	@Test
	public void givenPageIsPresentInCachedList_WhenGetPageVersionedInTransactionFromCacheIsCalled_ThenItReturnsThePage()
	{
		// GIVEN
		when(sessionCachedContextProvider.getAllItemsFromListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION)).thenReturn(
				Collections.singletonList(pageModel));

		// WHEN
		Optional<AbstractPageModel> result = cmsVersionSessionContextProvider.getPageVersionedInTransactionFromCache();

		// THEN
		verify(sessionCachedContextProvider).getAllItemsFromListCache(CACHED_PAGE_VERSIONED_IN_TRANSACTION);
		assertTrue(result.isPresent());
		assertThat(result.get(), is(pageModel));
	}
}
