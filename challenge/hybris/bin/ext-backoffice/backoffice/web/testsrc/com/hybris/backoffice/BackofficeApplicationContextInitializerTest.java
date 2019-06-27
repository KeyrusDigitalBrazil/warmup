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
package com.hybris.backoffice;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.hybris.cockpitng.modules.core.impl.CockpitModulesApplicationContextInitializer;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeApplicationContextInitializerTest
{

	@Spy
	private final BackofficeApplicationContextInitializer initializer = new BackofficeApplicationContextInitializer();

	@Test
	public void shouldRefreshContextIfInitializerNotInitialized()
	{
		// given
		final ConfigurableApplicationContext APPLICATION_CONTEXT = mock(ConfigurableApplicationContext.class);
		final CockpitModulesApplicationContextInitializer COCKPIT_INITIALIZER = mock(
				CockpitModulesApplicationContextInitializer.class);
		when(APPLICATION_CONTEXT.getBean(anyString(), eq(CockpitModulesApplicationContextInitializer.class)))
				.thenReturn(COCKPIT_INITIALIZER);
		final HttpSessionEvent EVENT = mockSessionEvent();
		when(EVENT.getSession().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(APPLICATION_CONTEXT);

		// when
		initializer.sessionCreated(EVENT);

		// then
		verify(APPLICATION_CONTEXT).refresh();
	}

	@Test
	public void shouldNotRefreshContextIfInitializerInitialized()
	{
		// given
		final ConfigurableApplicationContext APPLICATION_CONTEXT = mock(ConfigurableApplicationContext.class);
		final CockpitModulesApplicationContextInitializer COCKPIT_INITIALIZER = mock(
				CockpitModulesApplicationContextInitializer.class);
		when(COCKPIT_INITIALIZER.isInitialized()).thenReturn(Boolean.TRUE);
		when(APPLICATION_CONTEXT.getBean(anyString(), eq(CockpitModulesApplicationContextInitializer.class)))
				.thenReturn(COCKPIT_INITIALIZER);
		final HttpSessionEvent EVENT = mockSessionEvent();
		when(EVENT.getSession().getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(APPLICATION_CONTEXT);

		// when
		initializer.sessionCreated(EVENT);

		// then
		verify(APPLICATION_CONTEXT, never()).refresh();
	}

	protected HttpSessionEvent mockSessionEvent()
	{
		final HttpSession SESSION = mock(HttpSession.class);
		final ServletContext SERVLET_CONTEXT = mock(ServletContext.class);
		when(SESSION.getServletContext()).thenReturn(SERVLET_CONTEXT);
		return new HttpSessionEvent(SESSION);
	}

	@Test
	public void shouldCreateConfiguredContext()
	{
		// given
		final ServletContext SERVLET_CONTEXT = mock(ServletContext.class);
		when(SERVLET_CONTEXT.getInitParameter("contextClass")).thenReturn(BackofficeApplicationContext.class.getName());

		// when
		final WebApplicationContext applicationContext = initializer.createWebApplicationContext(SERVLET_CONTEXT);

		// then
		assertThat(applicationContext).isInstanceOf(BackofficeApplicationContext.class);
		verify(initializer, never()).createDefaultWebApplicationContext(any());
	}

	@Test
	public void shouldCreateDefaultContextIfNotConfigured()
	{
		// given
		final ServletContext SERVLET_CONTEXT = mock(ServletContext.class);
		when(SERVLET_CONTEXT.getAttribute("contextClass")).thenReturn(null);

		// when
		initializer.createWebApplicationContext(SERVLET_CONTEXT);

		// then
		verify(initializer).createDefaultWebApplicationContext(any());
	}
}
