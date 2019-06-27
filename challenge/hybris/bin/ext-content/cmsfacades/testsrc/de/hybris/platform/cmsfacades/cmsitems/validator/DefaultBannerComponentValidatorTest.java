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
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBannerComponentValidatorTest
{
	@InjectMocks
	private DefaultBannerComponentValidator validator;

	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	private ValidationErrors validationErrors = new DefaultValidationErrors();

	@Before
	public void setup()
	{
		final LanguageData language = new LanguageData();
		language.setRequired(true);
		language.setIsocode(Locale.ENGLISH.toLanguageTag());
		when(languageFacade.getLanguages()).thenReturn(Arrays.asList(language));
		when(commonI18NService.getLocaleForIsoCode(Locale.ENGLISH.toLanguageTag())).thenReturn(Locale.ENGLISH);
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
	}

	@Test
	public void testValidateWithoutRequiredAttributeAddErrors()
	{
		final BannerComponentModel itemModel = new BannerComponentModel();
		validator.validate(itemModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(3, errors.size());
		
		assertThat(errors.stream().map(ValidationError::getField).collect(Collectors.toList()), 
				containsInAnyOrder(BannerComponentModel.CONTENT, BannerComponentModel.HEADLINE, BannerComponentModel.MEDIA));
	}

	@Test
	public void testValidateWithContentOnlyAddErrors()
	{
		final BannerComponentModel itemModel = new BannerComponentModel();
		itemModel.setContent("test", Locale.ENGLISH);
		validator.validate(itemModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(2, errors.size());
		assertThat(errors.stream().map(ValidationError::getField).collect(Collectors.toList()),
				containsInAnyOrder(BannerComponentModel.HEADLINE, BannerComponentModel.MEDIA));
	}


	@Test
	public void testValidateWithHeadlineOnlyAddErrors()
	{
		final BannerComponentModel itemModel = new BannerComponentModel();
		itemModel.setHeadline("test", Locale.ENGLISH);
		validator.validate(itemModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(2, errors.size());
		assertThat(errors.stream().map(ValidationError::getField).collect(Collectors.toList()),
				containsInAnyOrder(BannerComponentModel.CONTENT, BannerComponentModel.MEDIA));
	}

	@Test
	public void testValidateWithMediaOnlyAddErrors()
	{
		final BannerComponentModel itemModel = new BannerComponentModel();
		itemModel.setMedia(new MediaModel(), Locale.ENGLISH);
		validator.validate(itemModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(2, errors.size());
		assertThat(errors.stream().map(ValidationError::getField).collect(Collectors.toList()),
				containsInAnyOrder(BannerComponentModel.CONTENT, BannerComponentModel.HEADLINE));
	}

}
