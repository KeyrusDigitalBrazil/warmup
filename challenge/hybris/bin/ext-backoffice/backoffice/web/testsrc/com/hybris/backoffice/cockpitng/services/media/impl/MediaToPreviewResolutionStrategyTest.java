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
package com.hybris.backoffice.cockpitng.services.media.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;



public class MediaToPreviewResolutionStrategyTest
{

	@InjectMocks
	private final MediaToPreviewResolutionStrategy strategy = new MediaToPreviewResolutionStrategy();

	@Mock
	MediaService mediaService;


	@Before
	public void init()
	{
		initMocks(this);

		//strategy.setMediaService(mediaService);
	}

	@Test
	public void testCanResolve()
	{
		assertThat(strategy.canResolve("String")).isFalse();
		assertThat(strategy.canResolve(new Object())).isFalse();
		assertThat(strategy.canResolve(new MediaModel())).isTrue();
		assertThat(strategy.canResolve(new MediaModel()
		{
			// class that extends Media should also be accepted
		})).isTrue();
	}

	@Test
	public void testResolvePreviewUrl()
	{
		// given
		final MediaModel media = Mockito.mock(MediaModel.class);

		when(media.getURL2()).thenReturn("/abc");
		when(mediaService.getUrlForMedia(media)).thenReturn("www.not.important.url.info");

		// when
		final String previewUrl = strategy.resolvePreviewUrl(media);

		// then
		assertThat(previewUrl).isNull();
	}

	@Test
	public void testResolveMimeType()
	{
		// given
		final MediaModel media = Mockito.mock(MediaModel.class);
		final String mime = String.format("application-x/%s", Long.toString(System.nanoTime(), 24));

		when(media.getMime()).thenReturn(mime);

		// when
		final String mimeType = strategy.resolveMimeType(media);

		// then
		assertThat(mimeType).isEqualTo(mime);
	}
}
