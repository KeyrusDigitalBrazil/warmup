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
package de.hybris.platform.azure.media.storage;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.media.services.MediaLocationHashService;
import de.hybris.platform.media.storage.MediaMetaData;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WindowsAzureBlobStorageStrategyTest
{
	private static final String MEDIA_ID = "123456";
	private static final Long MEDIA_SIZE = Long.valueOf(123456);

	@Mock
	private MediaLocationHashService locationHashService;
	@Mock
	private MediaFolderConfig folderConfig;
	@Mock
	private MediaStorageConfigService configService;
	@Mock
	private InputStream dataStream;
	private WindowsAzureBlobStorageStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
		strategy = new WindowsAzureBlobStorageStrategy();
		strategy.setLocationHashService(locationHashService);
		strategy.setStorageConfigService(configService);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnStoringMedia()
	{
		// given
		final MediaFolderConfig folderConfig = null;

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, Collections.EMPTY_MAP, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("folder config is required!");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMediaIdIsNullOnStoringMedia()
	{
		// given
		final String mediaId = null;

		try
		{
			// when
			strategy.store(folderConfig, mediaId, Collections.EMPTY_MAP, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("mediaId is required!");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMetaDataIsNullOnStoringMedia()
	{
		// given
		final Map<String, Object> metaData = null;

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, metaData, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("metaData is required!");
		}
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenDataStreamIsNullOnStoringMedia()
	{
		// given
		final InputStream dataStream = null;

		try
		{
			// when
			strategy.store(folderConfig, MEDIA_ID, Collections.EMPTY_MAP, dataStream);
			fail("should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("dataStream is required!");
		}
	}

	@Test
	public void shouldThrowMediaNotFoundExceptionWhenAskingForSizeForNonExistentObject() throws Exception
	{
		// given
		final String mediaLocation = "NON_EXISISTENT";

		try
		{
			// when
			strategy.getSize(folderConfig, mediaLocation);
			fail("Should throw MediaNotFoundException");

		}
		catch (final MediaNotFoundException e)
		{
			// then fine
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldThrowUnsupportedOperationExceptionOnGettingMediaAsFile()
	{
		strategy.getAsFile(folderConfig, MEDIA_ID);
	}

}
