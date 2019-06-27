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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.cockpitng.modules.BackofficeLibraryFetcher;
import com.hybris.cockpitng.core.CockpitApplicationException;
import com.hybris.cockpitng.core.modules.LibraryFetcher;
import com.hybris.cockpitng.core.persistence.packaging.WidgetClassLoader;
import com.hybris.cockpitng.core.spring.ModuleContentProvider;
import com.hybris.cockpitng.modules.ModulesEnumeration;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeApplicationContextTest
{

	@Spy
	private BackofficeApplicationContext applicationContext;

	@Mock
	private WidgetClassLoader classLoader;

	@Mock
	private BackofficeModulesManager manager;

	@Before
	public void setUp()
	{
		doReturn(manager).when(applicationContext).createModulesManager();
		doReturn(classLoader).when(applicationContext).createWidgetClassLoader(any());
	}

	@Test
	public void shouldWrapWithWidgetClassLoaderIfClassLoaderSet()
	{
		// given
		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();

		// when
		applicationContext.setClassLoader(currentClassLoader);

		// then
		verify(applicationContext).revertWidgetClassLoader();
		verify(applicationContext).initializeWidgetClassLoader();
		verify(applicationContext).createWidgetClassLoader(currentClassLoader);
	}

	@Test
	public void shouldSetWidgetClassLoaderOnFirstRefresh()
	{
		// given

		// when
		applicationContext.prepareRefresh();

		// then
		verify(applicationContext).initializeWidgetClassLoader();
		verify(applicationContext).createWidgetClassLoader(any());
	}

	@Test
	public void shouldClosePreviousWidgetClassLoaderOnRefresh() throws IOException
	{
		// given

		// when
		applicationContext.prepareRefresh();
		applicationContext.prepareRefresh();

		// then
		verify(applicationContext, times(2)).initializeWidgetClassLoader();
		verify(classLoader).close();
	}

	@Test
	public void shouldCreateFetcherAndEnumerationWhileCreatingManager()
	{
		// given
		doCallRealMethod().when(applicationContext).createModulesManager();
		doReturn(BackofficeLibraryFetcher.class.getName()).when(applicationContext).getLibraryFetcherClassName();
		doReturn(BackofficeModulesEnumeration.class.getName()).when(applicationContext).getModulesEnumerationClassName();

		// when
		final BackofficeModulesManager modulesManager = applicationContext.createModulesManager();

		// then
		verify(applicationContext).createModulesEnumeration();
		verify(applicationContext).createModulesFetcher(any());
		assertThat(modulesManager.getLibraryFetcher()).isInstanceOf(BackofficeLibraryFetcher.class);
		assertThat(modulesManager.getModulesEnumeration()).isInstanceOf(BackofficeModulesEnumeration.class);
	}

	@Test
	public void shouldNotCreateFetcherIfEnumerationIsFetcher()
	{
		// given
		final ModulesEnumeration enumeration = mock(ModulesEnumeration.class, withSettings().extraInterfaces(LibraryFetcher.class));
		doReturn(null).when(applicationContext).getLibraryFetcherClassName();

		// when
		final LibraryFetcher modulesFetcher = applicationContext.createModulesFetcher(enumeration);

		// then
		assertThat(modulesFetcher).isSameAs(enumeration);
	}

	@Test
	public void shouldCreateFetcherIfConfiguredEvenThoughEnumerationIsFetcher()
	{
		// given
		final ModulesEnumeration enumeration = mock(ModulesEnumeration.class, withSettings().extraInterfaces(LibraryFetcher.class));
		doReturn(BackofficeLibraryFetcher.class.getName()).when(applicationContext).getLibraryFetcherClassName();

		// when
		final LibraryFetcher modulesFetcher = applicationContext.createModulesFetcher(enumeration);

		// then
		assertThat(modulesFetcher).isInstanceOf(BackofficeLibraryFetcher.class);
	}

	@Test
	public void shouldRegisterNewJarAndFillItWithContentsWhenRegisteringNewModule() throws IOException, CockpitApplicationException
	{
		// given
		final String MODULE_NAME = "someModule";
		final ModuleContentProvider CONTENT_PROVIDER = mock(ModuleContentProvider.class);
		doReturn(mock(OutputStream.class)).when(applicationContext).createDefaultModuleJarStream(any());
		doNothing().when(applicationContext).refresh();

		// when
		applicationContext.registerNewModule(MODULE_NAME, CONTENT_PROVIDER);

		// then
		final InOrder methodOrder = inOrder(CONTENT_PROVIDER, manager, applicationContext);
		methodOrder.verify(manager).registerNewModuleJar(eq(MODULE_NAME));
		methodOrder.verify(CONTENT_PROVIDER).prepareStream(any());
		methodOrder.verify(CONTENT_PROVIDER).writeContent(any());
		methodOrder.verify(CONTENT_PROVIDER).finalizeStream(any());
		methodOrder.verify(applicationContext).refresh();
	}

	@Test
	public void shouldUnregisterModuleJarIfRegisteringNewModuleFails() throws IOException, CockpitApplicationException
	{
		// given
		final String MODULE_NAME = "someModule";
		final ModuleContentProvider CONTENT_PROVIDER = mock(ModuleContentProvider.class);
		doThrow(IOException.class).when(CONTENT_PROVIDER).writeContent(any());
		doReturn(mock(OutputStream.class)).when(applicationContext).createDefaultModuleJarStream(any());

		Exception error = null;
		// when
		try
		{
			applicationContext.registerNewModule(MODULE_NAME, CONTENT_PROVIDER);
		}
		catch (final CockpitApplicationException ex)
		{
			error = ex;
		}

		// then
		assertThat(error).isNotNull();
		verify(manager).unregisterModuleJar(eq(MODULE_NAME));
	}

	@Test(expected = CockpitApplicationException.class)
	public void shouldFailWhenRegisteringExistingModule() throws CockpitApplicationException
	{
		// given
		final String MODULE_NAME = "someModule";
		when(manager.isModuleRegistered(eq(MODULE_NAME))).thenReturn(Boolean.TRUE);

		// when
		applicationContext.registerNewModule(MODULE_NAME, mock(ModuleContentProvider.class));

		// then
	}

	@Test
	public void shouldUnregisterJarAndReloadClassLoaderOnWhenUnregisteringModule() throws CockpitApplicationException, IOException
	{
		// given
		final String MODULE_NAME = "someModule";

		// when
		applicationContext.unregisterModule(MODULE_NAME);

		// then
		final InOrder methodOrder = inOrder(applicationContext, manager);
		methodOrder.verify(applicationContext).revertWidgetClassLoader();
		methodOrder.verify(manager).unregisterModuleJar(eq(MODULE_NAME));
		methodOrder.verify(applicationContext).initializeWidgetClassLoader();

	}
}
