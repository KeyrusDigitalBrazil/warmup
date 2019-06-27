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
package de.hybris.platform.cmsfacades.common.validator.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LocalizedMediaValidatorTest
{
	private static final String ENGLISH = Locale.ENGLISH.toString();
	private static final String MEDIA = "media";
	private static final String MEDIA_CODE = "test-media-code";
	private static final String INVALID = "invalid";

	@Mock
	private MediaService mediaService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private MediaModel media;

	@InjectMocks
	private LocalizedMediaValidator validator;

	private BannerComponentData data;
	private Errors errors;

	@Before
	public void setUp()
	{
		data = new BannerComponentData();
		errors = new BeanPropertyBindingResult(data, data.getClass().getSimpleName());

		when(cmsAdminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersion);
		when(mediaService.getMedia(catalogVersion, MEDIA_CODE)).thenReturn(media);
	}

	@Test
	public void shouldValidateMediaCode()
	{
		validator.validate(ENGLISH, MEDIA, MEDIA_CODE, errors);

		assertThat(errors.getFieldErrorCount(), is(0));
	}

	@Test
	public void shouldNotValidateMediaCode_MediaCodeNull()
	{
		validator.validate(ENGLISH, MEDIA, null, errors);

		assertThat(errors.getFieldErrorCount(), is(0));
		verifyZeroInteractions(cmsAdminSiteService, mediaService);
	}

	@Test
	public void shouldNotValidateMediaCode_MediaCodeInvalid()
	{
		when(mediaService.getMedia(catalogVersion, INVALID)).thenReturn(null);

		validator.validate(ENGLISH, MEDIA, INVALID, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError(MEDIA).getCode(), is(CmsfacadesConstants.INVALID_MEDIA_CODE_L10N));
		assertThat(errors.getFieldError(MEDIA).getArguments()[0], is(ENGLISH));
	}

	@Test
	public void shouldNotValidateMediaCode_MediaCodeAmbiguous()
	{
		when(mediaService.getMedia(catalogVersion, INVALID)).thenThrow(new AmbiguousIdentifierException("multiple entries found"));

		validator.validate(ENGLISH, MEDIA, INVALID, errors);

		assertThat(errors.getFieldErrorCount(), is(1));
		assertThat(errors.getFieldError(MEDIA).getCode(), is(CmsfacadesConstants.INVALID_MEDIA_CODE_L10N));
		assertThat(errors.getFieldError(MEDIA).getArguments()[0], is(ENGLISH));
	}
}

class BannerComponentData
{
	private Map<String, String> media;

	public Map<String, String> getMedia()
	{
		return this.media;
	}

	public void setMedia(final Map<String, String> media)
	{
		this.media = media;
	}
}
