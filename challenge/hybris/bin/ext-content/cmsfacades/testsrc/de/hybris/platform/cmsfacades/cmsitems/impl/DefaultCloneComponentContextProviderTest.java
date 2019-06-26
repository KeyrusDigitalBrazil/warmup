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
package de.hybris.platform.cmsfacades.cmsitems.impl;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_CONTEXT;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCloneComponentContextProviderTest
{
	@InjectMocks
	private DefaultCloneComponentContextProvider cloneComponentContextProvider;

	@Mock
	private SessionService sessionService;

	@Test
	public void shouldFindValueInSession()
	{
		when(sessionService.getAttribute(SESSION_CLONE_COMPONENT_CONTEXT)).thenReturn(new Object());

		final Object value = cloneComponentContextProvider.getValueFromSession(true);

		assertThat(value, notNullValue());
		verify(sessionService).getAttribute(SESSION_CLONE_COMPONENT_CONTEXT);
	}

	@Test
	public void shouldNotFindValueInSessionWithNoException()
	{
		final Object value = cloneComponentContextProvider.getValueFromSession(false);

		assertThat(value, nullValue());
		verify(sessionService).getAttribute(SESSION_CLONE_COMPONENT_CONTEXT);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotFindValueInSessionThrowException()
	{
		cloneComponentContextProvider.getValueFromSession(true);
	}

	@Test
	public void shouldInitializeSessionValueWithNewReference()
	{
		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "value"));

		verify(sessionService).setAttribute(anyString(), anyObject());
	}

	@Test
	public void shouldInitializeSessionValueWithExistingReference()
	{
		final AtomicReference<LinkedList<Object>> sessionDataReference = new AtomicReference<>(new LinkedList<>());
		when(sessionService.getAttribute(SESSION_CLONE_COMPONENT_CONTEXT)).thenReturn(sessionDataReference);

		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "value"));

		assertThat(sessionDataReference.get(), hasSize(1));
		verify(sessionService, times(0)).setAttribute(anyString(), anyObject());
	}

	@Test
	public void shouldNotFindItemForKeyNonIntializedSession()
	{
		final Object value = cloneComponentContextProvider.findItemForKey("test");

		assertThat(value, nullValue());
	}

	@Test
	public void shouldNotFindItemForKeyNoMatch()
	{
		when(sessionService.getAttribute(SESSION_CLONE_COMPONENT_CONTEXT)).thenReturn(new AtomicReference<>(new LinkedList<>()));

		final Object value = cloneComponentContextProvider.findItemForKey("test");

		assertThat(value, nullValue());
	}

	@Test
	public void shouldFindItemForKey()
	{
		when(sessionService.getAttribute(SESSION_CLONE_COMPONENT_CONTEXT)).thenReturn(new AtomicReference<>(new LinkedList<>()));
		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "value"));

		final Object value = cloneComponentContextProvider.findItemForKey("test");

		assertThat(value, equalTo("value"));
	}

	@Test
	public void shouldFindTopMostItemForKey()
	{
		when(sessionService.getAttribute(SESSION_CLONE_COMPONENT_CONTEXT)).thenReturn(new AtomicReference<>(new LinkedList<>()));
		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "first")); // first item added to the stack
		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "second"));
		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "third"));
		cloneComponentContextProvider.initializeItem(new SimpleImmutableEntry<>("test", "fourth")); // last item added to the stack

		final Object value = cloneComponentContextProvider.findItemForKey("test");

		assertThat(value, equalTo("fourth"));
	}
}
