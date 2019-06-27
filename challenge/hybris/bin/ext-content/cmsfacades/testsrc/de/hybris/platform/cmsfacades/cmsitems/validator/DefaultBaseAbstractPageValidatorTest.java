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


import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CONTAINS_INVALID_CHARS;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.NO_RESTRICTION_SET_FOR_VARIATION_PAGE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultBaseAbstractPageValidatorTest
{
	private static final String TEST_UID = "test-uid";
	private static final String TEST_NAME = "test-name";
	private static final String TEST_TITLE = "test-title";
	private static final String INVALID = "invalid";
	private static final String EN = "en";

	@InjectMocks
	private DefaultBaseAbstractPageValidator validator;

	@Mock
	private Predicate<String> onlyHasSupportedCharactersPredicate;
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private LanguageData enLanguage;
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;

	private ValidationErrors validationErrors = new DefaultValidationErrors();

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		when(onlyHasSupportedCharactersPredicate.test(TEST_UID)).thenReturn(true);
		when(onlyHasSupportedCharactersPredicate.test(INVALID)).thenReturn(false);
		
		when(languageFacade.getLanguages()).thenReturn(Arrays.asList(enLanguage));
		
		when(enLanguage.getIsocode()).thenReturn(EN);
		when(enLanguage.isRequired()).thenReturn(true);
		
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(Locale.US);
	}

	@Test
	public void testValidateWithoutRequiredAttributeUid()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setUid(null);
		pageModel.setName(TEST_NAME);
		pageModel.setDefaultPage(true);
		pageModel.setTitle(TEST_TITLE, Locale.US);
		
		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.UID));
		assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED));
	}

	@Test
	public void testValidateUidHasSupportedCharacters()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setUid(INVALID);
		pageModel.setName(TEST_NAME);
		pageModel.setDefaultPage(true);
		pageModel.setTitle(TEST_TITLE, Locale.US);
		
		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.UID));
		assertThat(errors.get(0).getErrorCode(), is(FIELD_CONTAINS_INVALID_CHARS));
	}

	@Test
	public void testValidateVariationPageWithoutRestrictions()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setUid(TEST_UID);
		pageModel.setName(TEST_NAME);
		pageModel.setDefaultPage(false);
		pageModel.setRestrictions(Collections.emptyList());
		pageModel.setTitle(TEST_TITLE, Locale.US);
		
		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.RESTRICTIONS));
		assertThat(errors.get(0).getErrorCode(), is(NO_RESTRICTION_SET_FOR_VARIATION_PAGE));
	}
	
	@Test
	public void testValidateWithoutRequiredAttributeTitle()
	{
		final AbstractPageModel pageModel = new AbstractPageModel();
		pageModel.setUid(TEST_UID);
		pageModel.setName(TEST_NAME);
		pageModel.setDefaultPage(true);
		
		validator.validate(pageModel);

		final List<ValidationError> errors = validationErrorsProvider.getCurrentValidationErrors().getValidationErrors();

		assertEquals(1, errors.size());
		assertThat(errors.get(0).getField(), is(AbstractPageModel.TITLE));
		assertThat(errors.get(0).getErrorCode(), is(FIELD_REQUIRED_L10N));
	}
}
