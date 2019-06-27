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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.media.MediaModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaToDataContentConverterTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String MEDIA_URL = "some media url";
	private final String MIME_TYPE = "some mime type";
	private final String MEDIA_CODE = "some media code";

	private MediaData mediaData;

	@Mock
	private de.hybris.platform.servicelayer.dto.converter.Converter<MediaModel, MediaData> mediaModelConverter;

	@Mock
	private MediaModel mediaModel;

	@InjectMocks
	private MediaToDataContentConverter converter;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		mediaData = new MediaData();
		mediaData.setUuid("some uuid");
		mediaData.setCatalogId("some catalog id");
		mediaData.setCatalogVersion("some catalog version");
		mediaData.setCode(MEDIA_CODE);
		mediaData.setUrl(MEDIA_URL);
		mediaData.setMime(MIME_TYPE);

		when(mediaModelConverter.convert(mediaModel)).thenReturn(mediaData);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void givenNullMediaModel_WhenConvertIsCalled_ThenItReturnsNull()
	{
		// WHEN
		MediaData result = converter.convert(null);

		// THEN
		assertThat(result, nullValue());
	}

	@Test
	public void givenMediaModel_WhenConvertIsCalled_ThenItReturnsTheRightValue()
	{
		// WHEN
		MediaData result = converter.convert(mediaModel);

		// THEN
		assertThat(result, is(mediaData));
		assertThat(result.getUuid(), nullValue());
		assertThat(result.getCatalogId(), nullValue());
		assertThat(result.getCatalogVersion(), nullValue());
		assertThat(result.getCode(), is(MEDIA_CODE));
		assertThat(result.getMime(), is(MIME_TYPE));
		assertThat(result.getUrl(), is(MEDIA_URL));
	}
}
