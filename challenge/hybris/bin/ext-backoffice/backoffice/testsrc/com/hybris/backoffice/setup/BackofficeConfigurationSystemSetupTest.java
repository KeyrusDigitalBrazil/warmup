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
package com.hybris.backoffice.setup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.hybris.backoffice.model.BackofficeConfigurationMediaModel;

@IntegrationTest
public class BackofficeConfigurationSystemSetupTest extends ServicelayerTransactionalTest
{
	private static final String MEDIA_CODE = "test";
	public static final String MEDIA_DATA = "test data";

	@Resource
	private ModelService modelService;
	@Resource
	private MediaService mediaService;
	private BackofficeConfigurationSystemSetup systemSetup = new BackofficeConfigurationSystemSetup();

	@Before
	public void setUp()
	{
		mediaService = spy(mediaService);
		systemSetup.setMediaService(mediaService);
		systemSetup.setModelService(modelService);
	}

	@Test
	public void shouldMoveOldMedia()
	{
		final MediaFolderModel mediaFolder = modelService.create(MediaFolderModel.class);
		mediaFolder.setQualifier("folder");

		prepareOldMedia(mediaFolder, MEDIA_CODE, MEDIA_DATA.getBytes(), CatalogUnawareMediaModel.class);

		systemSetup.moveToNewTable(MEDIA_CODE);

		final MediaModel newMedia = mediaService.getMedia(MEDIA_CODE);
		assertThat(newMedia).isNotNull();
		assertThat(newMedia).isInstanceOf(BackofficeConfigurationMediaModel.class);
		assertThat(newMedia.getFolder()).isEqualTo(mediaFolder);

		final byte[] dataFromNewMedia = mediaService.getDataFromMedia(newMedia);
		assertThat(dataFromNewMedia).isNotNull();
		assertThat(new String(dataFromNewMedia)).isEqualTo(MEDIA_DATA);
	}

	@Test
	public void shouldNotMoveNewMedia()
	{
		final MediaFolderModel mediaFolder = modelService.create(MediaFolderModel.class);
		mediaFolder.setQualifier("folder");

		prepareOldMedia(mediaFolder, MEDIA_CODE, MEDIA_DATA.getBytes(), BackofficeConfigurationMediaModel.class);

		systemSetup.moveToNewTable(MEDIA_CODE);

		final MediaModel newMedia = mediaService.getMedia(MEDIA_CODE);
		assertThat(newMedia).isNotNull();
		assertThat(newMedia).isInstanceOf(BackofficeConfigurationMediaModel.class);
		assertThat(newMedia.getFolder()).isEqualTo(mediaFolder);

		final byte[] dataFromNewMedia = mediaService.getDataFromMedia(newMedia);
		assertThat(dataFromNewMedia).isNotNull();
		assertThat(new String(dataFromNewMedia)).isEqualTo(MEDIA_DATA);
	}

	protected void prepareOldMedia(final MediaFolderModel mediaFolder, final String mediaCode, final byte[] data, final Class<? extends MediaModel> mediaModelClass)
	{
		final MediaModel oldMedia = modelService.create(mediaModelClass);
		oldMedia.setCode(mediaCode);
		oldMedia.setFolder(mediaFolder);
		modelService.save(oldMedia);

		mediaService.setDataForMedia(oldMedia, data);
	}

	@Test
	public void shouldMoveBackofficeConfigs()
	{
		systemSetup.moveConfigurationToANewType1808();

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(mediaService, times(2)).getMedia(captor.capture());
		assertThat(captor.getAllValues()).containsExactly("cockpitng-config", "cockpitng-widgtes-config");
	}
}