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
package com.hybris.backoffice.cockpitng.util.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.cockpitng.core.persistence.packaging.WidgetJarLibInfo;


public class BackofficeResourceLoaderTest
{
	private static final String PATH = "path";
	private static final String MODULE = "module";

	@Test
	public void testGetViewResourceAsStringWhenResourceDoesNotExists()
	{
		verifyGetViewResourceAsStringByJarAndPath(false);
	}

	@Test
	public void testGetViewResourceAsStringWhenResourceExists()
	{
		verifyGetViewResourceAsStringByJarAndPath(true);
	}

	private static void verifyGetViewResourceAsStringByJarAndPath(final boolean resourceExists)
	{
		// given
		final BackofficeResourceLoader resourceLoader = Mockito.spy(new BackofficeResourceLoader());

        final String VALUE = "resource";
		final InputStream stream = resourceExists ? new ByteArrayInputStream(VALUE.getBytes()) : null;
		final WidgetJarLibInfo jarLibInfo = Mockito.mock(WidgetJarLibInfo.class);
		Mockito.doReturn(stream).when(resourceLoader).getResourceAsStream(jarLibInfo, PATH);

		// test
		final String resource = resourceLoader.getViewResourceAsString(jarLibInfo, PATH);

		// then
		if (resourceExists)
		{
			Assertions.assertThat(resource).isEqualTo("resource");
		}
		else
		{
			Assertions.assertThat(resource).isNull();
		}
		Mockito.verify(resourceLoader).getViewResourceAsString(jarLibInfo, PATH);
		Mockito.verify(resourceLoader).getResourceAsStream(jarLibInfo, PATH);
		Mockito.verifyNoMoreInteractions(resourceLoader);
	}

	@Test
	public void testGetResourceByPathAndModuleWhenCacheIsEnabledAndResourceExists()
	{
		verifyGetResourceByPathAndModule(true, true);
	}

	@Test
	public void testGetResourceByPathAndModuleWhenCacheIsEnabledAndResourceDoesNotExist()
	{
		verifyGetResourceByPathAndModule(true, false);
	}

	@Test
	public void testGetResourceByPathAndModuleWhenCacheIsDisabledAndResourceExists()
	{
		verifyGetResourceByPathAndModule(false, true);
	}

	@Test
	public void testGetResourceByPathAndModuleWhenCacheIsDisabledAndResourceDoesNotExist()
	{
		verifyGetResourceByPathAndModule(false, false);
	}

	private static void verifyGetResourceByPathAndModule(final boolean cacheEnabled, final boolean resourceExists)
	{
		// given
		final BackofficeResourceLoader resourceLoader = Mockito.spy(new BackofficeResourceLoader());
		Mockito.doReturn(Boolean.valueOf(!cacheEnabled)).when(resourceLoader).isEnabled();
		final InputStream stream = resourceExists ? Mockito.mock(InputStream.class) : null;
		Mockito.doReturn(stream).when(resourceLoader).fetchResource(PATH, MODULE);

		// test
		final InputStream resource = resourceLoader.getResourceAsStream(PATH, MODULE);

		// then
		if (cacheEnabled)
		{
			Assertions.assertThat(resource).isNull();
		}
		else
		{
			Assertions.assertThat(resource).isEqualTo(stream);
		}
		Mockito.verify(resourceLoader).getResourceAsStream(PATH, MODULE);
		Mockito.verify(resourceLoader).isEnabled();
		if (!cacheEnabled)
		{
			Mockito.verify(resourceLoader).fetchResource(PATH, MODULE);
		}
		Mockito.verifyNoMoreInteractions(resourceLoader);
	}


	@Test
	public void testHasResourceByPathWhenCacheIsEnabledAndResourceExists()
	{
		verifyHasResourceByPath(true, true);
	}

	@Test
	public void testHasResourceByPathWhenCacheIsEnabledAndResourceDoesNotExist()
	{
		verifyHasResourceByPath(true, false);
	}

	@Test
	public void testHasResourceByPathWhenCacheIsDisabledAndResourceExists()
	{
		verifyHasResourceByPath(false, true);
	}

	@Test
	public void testHasResourceByPathWhenCacheIsDisabledAndResourceDoesNotExist()
	{
		verifyHasResourceByPath(false, false);
	}

	private static void verifyHasResourceByPath(final boolean cacheEnabled, final boolean resourceExists)
	{
		// given
		final BackofficeResourceLoader resourceLoader = Mockito.spy(new BackofficeResourceLoader());
		Mockito.doReturn(Boolean.valueOf(!cacheEnabled)).when(resourceLoader).isEnabled();
		final File file = resourceExists ? Mockito.mock(File.class) : null;
		Mockito.doReturn(file).when(resourceLoader).resolveFile(PATH);

		// test
		final boolean resource = resourceLoader.hasResource(PATH);

		// then
		if (cacheEnabled)
		{
			Assertions.assertThat(resource).isFalse();
		}
		else
		{
			Assertions.assertThat(resource).isEqualTo(resourceExists);
		}
		Mockito.verify(resourceLoader).hasResource(PATH);
		Mockito.verify(resourceLoader).isEnabled();
		if (!cacheEnabled)
		{
			Mockito.verify(resourceLoader).resolveFile(PATH);
		}
		Mockito.verifyNoMoreInteractions(resourceLoader);
	}
}
