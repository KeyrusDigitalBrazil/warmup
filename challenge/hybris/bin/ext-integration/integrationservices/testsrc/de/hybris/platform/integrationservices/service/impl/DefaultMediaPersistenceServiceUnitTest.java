/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.integrationservices.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationApiMediaModel;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMediaPersistenceServiceUnitTest
{
	private static final InputStream INPUT_STREAM = mock(InputStream.class);
	private static final String MEDIA_NAME_PREFIX = "MyPrefix_";

	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private IntegrationServicesConfiguration integrationServicesConfiguration;

	@Mock
	private SearchResult<Object> searchResult;
	@InjectMocks
	private DefaultMediaPersistenceService mediaPersistenceService;

	@Before
	public void setup()
	{
		when(searchResult.getResult()).thenReturn(Collections.emptyList());
		when(modelService.create(eq(IntegrationApiMediaModel.class))).thenReturn(new IntegrationApiMediaModel());
		mockConfigurationService();
	}

	@Test
	public void testPersistMediasCreatesCorrectMedia()
	{
		final List<InputStream> payloads = Collections.singletonList(INPUT_STREAM);

		final List<IntegrationApiMediaModel> persisted = mediaPersistenceService.persistMedias(payloads, IntegrationApiMediaModel.class);

		assertThat(persisted).hasSameSizeAs(payloads);
		final IntegrationApiMediaModel integrationApiMediaModel = persisted.get(0);
		assertThat(integrationApiMediaModel.getCode()).startsWith(MEDIA_NAME_PREFIX);
	}

	@Test
	public void testPersistMediasSavesTheMediaWithMediaService()
	{
		final List<InputStream> payloads = Collections.singletonList(INPUT_STREAM);

		mediaPersistenceService.persistMedias(payloads, IntegrationApiMediaModel.class);

		verify(mediaService).setStreamForMedia(any(IntegrationApiMediaModel.class), same(INPUT_STREAM), anyString(), eq("text/plain"));
	}

	@Test
	public void testPersistSeveralMediasSuccessfully()
	{
		final InputStream body1 = mock(InputStream.class);
		final InputStream body2 = mock(InputStream.class);
		final List<InputStream> requestBodies = Arrays.asList(body1, body2);

		final List<IntegrationApiMediaModel> mediaModels = mediaPersistenceService.persistMedias(requestBodies, IntegrationApiMediaModel.class);

		assertThat(mediaModels).hasSameSizeAs(requestBodies);
		verify(mediaService).setStreamForMedia(any(IntegrationApiMediaModel.class), same(body1), anyString(), anyString());
		verify(mediaService).setStreamForMedia(any(IntegrationApiMediaModel.class), same(body2), anyString(), anyString());
	}

	@Test
	public void testPersistMediasOneMediaFailedToPersist()
	{
		final InputStream body1 = mock(InputStream.class);
		final InputStream body2 = mock(InputStream.class);
		final List<InputStream> requestBodies = Arrays.asList(body1, body2);

		doThrow(new MediaIOException("")).when(mediaService)
				.setStreamForMedia(any(IntegrationApiMediaModel.class), same(body1), anyString(), anyString());

		final List<IntegrationApiMediaModel> mediaModels = mediaPersistenceService.persistMedias(requestBodies, IntegrationApiMediaModel.class);

		assertThat(mediaModels).hasSameSizeAs(requestBodies);
		assertThat(mediaModels.get(0)).isNull();
		assertThat(mediaModels.get(1)).isNotNull();
	}

	@Test
	public void testPersistMediasWithEmptyListOfPayloads()
	{
		final List<IntegrationApiMediaModel> medias = mediaPersistenceService.persistMedias(Collections.emptyList(), IntegrationApiMediaModel.class);
		assertThat(medias).isEmpty();
	}

	@Test
	public void testPersistMediasWithNullPayload()
	{
		final InputStream body1 = null;
		final InputStream body2 = mock(InputStream.class);
		final List<InputStream> requestBodies = Arrays.asList(null, body2);

		final List<IntegrationApiMediaModel> mediaModels = mediaPersistenceService.persistMedias(requestBodies, IntegrationApiMediaModel.class);

		assertThat(mediaModels).hasSameSizeAs(requestBodies);
		assertThat(mediaModels.get(0)).isNull();
		assertThat(mediaModels.get(1)).isNotNull();

		verify(mediaService, never()).setStreamForMedia(any(IntegrationApiMediaModel.class), same(body1), anyString(), anyString());
		verify(mediaService).setStreamForMedia(any(IntegrationApiMediaModel.class), same(body2), anyString(), anyString());
	}

	private void mockConfigurationService()
	{
		when(integrationServicesConfiguration.getMediaPersistenceMediaNamePrefix()).thenReturn(MEDIA_NAME_PREFIX);
	}
}
