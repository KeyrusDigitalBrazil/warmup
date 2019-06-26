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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.hybris.backoffice.model.BackofficeConfigurationMediaModel;

@IntegrationTest
public class BackofficeWidgetPersistenceServiceIntegrationTest extends ServicelayerTest
{
	public static final String MEDIA_CODE = "code";
	public static final String MEDIA_MIME = "mime";
	@Resource
	@Spy
	private ModelService modelService;
	@Resource
	@Spy
	private MediaService mediaService;
	@Resource
	private SessionService sessionService;
	@Resource
	private UserService userService;
	@Resource
	private SearchRestrictionService searchRestrictionService;
	private BackofficeWidgetPersistenceService service = new BackofficeWidgetPersistenceService();
	private DefaultBackofficeConfigurationMediaHelper backofficeConfigurationMediaHelper = new DefaultBackofficeConfigurationMediaHelper();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		service.setMediaService(mediaService);

		backofficeConfigurationMediaHelper.setMediaService(mediaService);
		backofficeConfigurationMediaHelper.setModelService(modelService);
		backofficeConfigurationMediaHelper.setSessionService(sessionService);
		backofficeConfigurationMediaHelper.setUserService(userService);
		backofficeConfigurationMediaHelper.setSearchRestrictionService(searchRestrictionService);

	}

	@Test
	public void shouldStoreOnlyOneMediaWithGivenCode()
	{
		final MediaFolderModel folder = modelService.create(MediaFolderModel.class);

		folder.setQualifier(DefaultBackofficeConfigurationMediaHelper.PROPERTY_BACKOFFICE_CONFIGURATION_SECURE_MEDIA_FOLDER);
		folder.setPath(DefaultBackofficeConfigurationMediaHelper.PROPERTY_BACKOFFICE_CONFIGURATION_SECURE_MEDIA_FOLDER);

		modelService.save(folder);

		final MediaModel firstMedia = backofficeConfigurationMediaHelper.createWidgetsConfigMedia(MEDIA_CODE, MEDIA_MIME);
		final MediaModel secondMedia = backofficeConfigurationMediaHelper.createWidgetsConfigMedia(MEDIA_CODE, MEDIA_MIME);

		assertThat(firstMedia).isNotNull();
		assertThat(secondMedia).isEqualTo(firstMedia);
		assertThat(firstMedia).isInstanceOf(BackofficeConfigurationMediaModel.class);

		verify(modelService, times(2)).create(BackofficeConfigurationMediaModel.class);
		verify(modelService, times(3)).save(any(BackofficeConfigurationMediaModel.class));
		verify(mediaService).getMedia(MEDIA_CODE);
	}
}