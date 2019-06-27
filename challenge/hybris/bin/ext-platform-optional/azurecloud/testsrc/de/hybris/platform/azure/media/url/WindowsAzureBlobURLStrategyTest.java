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
package de.hybris.platform.azure.media.url;

import static de.hybris.platform.azure.media.storage.WindowsAzureBlobStorageStrategy.CONTAINER_ADDRESS_KEY;
import static de.hybris.platform.azure.media.url.WindowsAzureBlobURLStrategy.PUBLIC_BASE_URL_KEY;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.azure.media.storage.WindowsAzureBlobStorageStrategy;
import de.hybris.platform.media.MediaSource;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WindowsAzureBlobURLStrategyTest
{
	@Mock
	private MediaFolderConfig folderConfig;
	@Mock
	private MediaSource media;
	private WindowsAzureBlobURLStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
		strategy = new WindowsAzureBlobURLStrategy()
		{
			@Override
			String computeContainerAddress(final MediaFolderConfig config)
			{
                return config.getParameter(CONTAINER_ADDRESS_KEY);
			}
		};
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNull()
	{
		// given
		final MediaFolderConfig folderConfig = null;

		try
		{
			// when
			strategy.getUrlForMedia(folderConfig, media);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("media folder config is required to perform this operation");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMediaSourceIsNull()
	{
		// given
		final MediaSource media = null;

		try
		{
			// when
			strategy.getUrlForMedia(folderConfig, media);
			fail("MediaSource is required to perform this operation");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("MediaSource is required to perform this operation");
		}
	}


	@Test
	public void shouldReturnBlankUrlWhenMediaLocationIsBlankOrNull()
	{
		// given
		given(media.getLocation()).willReturn(null);

		// when
		final String url = strategy.getUrlForMedia(folderConfig, media);

		// then
		assertThat(url).isNotNull().isEqualTo("");
	}


	@Test
	public void shouldReturnMediaUrlBasedOnLocation()
	{
		// given
		given(folderConfig.getParameter(PUBLIC_BASE_URL_KEY)).willReturn("http://foo.windowsazure.com");
		given(folderConfig.getParameter(CONTAINER_ADDRESS_KEY)).willReturn("xyz");
		given(media.getLocation()).willReturn("foo/bar/baz.jpg");

		// when
		final String url = strategy.getUrlForMedia(folderConfig, media);

		// then
		assertThat(url).isNotNull().isEqualTo("http://foo.windowsazure.com/xyz/foo/bar/baz.jpg");
	}
}
