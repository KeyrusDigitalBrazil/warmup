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
package com.hybris.backoffice.cockpitng.modules;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.CockpitApplicationException;
import com.hybris.cockpitng.core.modules.ModuleInfo;
import com.hybris.cockpitng.modules.CockpitModuleDeploymentException;
import com.hybris.cockpitng.modules.server.ws.jaxb.CockpitModuleInfo;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeLibraryFetcherTest
{

	@Spy
	private BackofficeLibraryFetcher fetcher;

	@Test
	public void shouldCreateExtensionURIForModuleWithBackofficePackage()
	{
		// given
		final String MODULE_NAME = "module";
		doReturn(true).when(fetcher).canFetchLibrary(any(), any());

		// when
		final URI moduleUrl = fetcher.getExtensionModuleUrl(MODULE_NAME);

		// then
		assertThat(moduleUrl.getScheme()).isEqualTo("extension");
		assertThat(moduleUrl.getHost()).isEqualTo(MODULE_NAME);
	}

	@Test
	public void shouldCreateSimplifiedExtensionURIForModuleWithBackofficePackage()
	{
		// given
		final String MODULE_NAME = "module";
		doReturn(false).when(fetcher).canFetchLibrary(any(), any());

		// when
		final URI moduleUrl = fetcher.getExtensionModuleUrl(MODULE_NAME);

		// then
		assertThat(moduleUrl.getScheme()).isEqualTo("simplified-extension");
		assertThat(moduleUrl.getHost()).isEqualTo(MODULE_NAME);
	}

	@Test
	public void shouldBeAbleToFetchModuleWithProperPackage() throws MalformedURLException
	{
		// given
		doReturn(new URL("http://localhost")).when(fetcher).findWidgetPackage(anyString(), any());
		final ModuleInfo MODULE_INFO = createModuleInfoWithPackage();

		// when
		final boolean canFetchLibrary = fetcher.canFetchLibrary(MODULE_INFO);

		// then
		verify(fetcher).findWidgetPackage(eq(MODULE_INFO.getId()), eq(MODULE_INFO.getWidgetsPackage()));
		assertThat(canFetchLibrary).isTrue();
	}


	@Test
	public void shouldNotBeAbleToFetchModuleWithoutPackage()
	{
		// given
		doReturn(null).when(fetcher).findWidgetPackage(anyString(), any());
		final ModuleInfo MODULE_INFO = createModuleInfoWithoutPackage();

		// when
		final boolean canFetchLibrary = fetcher.canFetchLibrary(MODULE_INFO);

		// then
		verify(fetcher).findWidgetPackage(eq(MODULE_INFO.getId()), eq(MODULE_INFO.getWidgetsPackage()));
		assertThat(canFetchLibrary).isFalse();
	}

	@Test
	public void shouldCopyPackageWhenFetching() throws MalformedURLException, CockpitApplicationException
	{
		// given
		final URL PACKAGE_URL = new URL("http://localhost");
		doReturn(PACKAGE_URL).when(fetcher).findWidgetPackage(anyString(), any());
		doNothing().when(fetcher).copyURLToFile(any(), any(), any());
		final ModuleInfo MODULE_INFO = createModuleInfoWithPackage();
		final File ARCHIVE = new File("/");

		// when
		fetcher.fetchLibrary(MODULE_INFO, ARCHIVE);

		// then
		verify(fetcher).copyURLToFile(same(MODULE_INFO), eq(PACKAGE_URL), eq(ARCHIVE));
	}

	@Test(expected = CockpitModuleDeploymentException.class)
	public void shouldFailIfRequestedToFetchModuleWithoutPackage() throws CockpitApplicationException
	{
		// given
		doReturn(null).when(fetcher).findWidgetPackage(anyString(), any());
		doNothing().when(fetcher).copyURLToFile(any(), any(), any());
		final ModuleInfo MODULE_INFO = createModuleInfoWithoutPackage();
		final File ARCHIVE = new File("/");

		// when
		fetcher.fetchLibrary(MODULE_INFO, ARCHIVE);

		// then
	}

	private CockpitModuleInfo createModuleInfoWithoutPackage()
	{
		final CockpitModuleInfo ret = new CockpitModuleInfo();
		ret.setId(UUID.randomUUID().toString());
		return ret;
	}

	private CockpitModuleInfo createModuleInfoWithPackage()
	{
		final CockpitModuleInfo ret = createModuleInfoWithoutPackage();
		ret.setWidgetsPackage(UUID.randomUUID().toString());
		return ret;
	}

}
