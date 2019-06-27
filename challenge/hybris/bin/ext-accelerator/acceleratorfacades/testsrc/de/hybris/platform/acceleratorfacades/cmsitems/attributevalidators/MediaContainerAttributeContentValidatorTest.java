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
package de.hybris.platform.acceleratorfacades.cmsitems.attributevalidators;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaContainerAttributeContentValidatorTest
{

	private static final String VALID_MEDIA_CODE = "valid-media-code";
	private static final String INVALID_MEDIA_CODE = "invalid-media-code";
	private static final String WIDESCREEN = "widescreen";
	private static final String MOBILE = "mobile";
	private static final String EN = "en";

	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private LanguageFacade languageFacade;

	private final List<String> cmsRequiredMediaFormatQualifiers = new ArrayList<>();

	@InjectMocks
	private MediaContainerAttributeContentValidator validator;

	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private MediaModel mediaModel;
	@Mock
	private ValidationErrors validationErrors;
	@Mock
	private LanguageData languageData;

	@Before
	public void setup()
	{
		cmsRequiredMediaFormatQualifiers.add(WIDESCREEN);

		validator.setCmsRequiredMediaFormatQualifiers(cmsRequiredMediaFormatQualifiers);

		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);

		when(uniqueItemIdentifierService.getItemModel(VALID_MEDIA_CODE, MediaModel.class)).thenReturn(Optional.of(mediaModel));
		when(uniqueItemIdentifierService.getItemModel(INVALID_MEDIA_CODE, MediaModel.class)).thenReturn(Optional.empty());
		when(Boolean.valueOf(languageData.isRequired())).thenReturn(Boolean.TRUE);
		when(languageData.getIsocode()).thenReturn(EN);
		when(languageFacade.getLanguages()).thenReturn(Arrays.asList(languageData));
	}

	@Test
	public void testWhenMediaFormatIsPresentWithValidMediaCode_shouldNotAddError()
	{
		final Map<String, String> formatMap = new HashMap<>();
		formatMap.put(WIDESCREEN, VALID_MEDIA_CODE);

		final Map<String, Map<String, String>> map = new HashMap<>();
		map.put(EN, formatMap);

		validator.validate(map, attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}


	@Test
	public void testWhenOnlyOptionalMediaFormatIsPresentWithValidMediaCode_shouldAddError()
	{
		final Map<String, String> formatMap = new HashMap<>();
		formatMap.put(MOBILE, VALID_MEDIA_CODE);

		final Map<String, Map<String, String>> map = new HashMap<>();
		map.put(EN, formatMap);

		final List<ValidationError> errors = validator.validate(map, attributeDescriptor);
		assertThat(errors, not(empty()));
	}

	@Test
	public void testWhenMediaFormatIsPresentWithInValidMediaCode_shouldAddError()
	{
		final Map<String, String> formatMap = new HashMap<>();
		formatMap.put(WIDESCREEN, INVALID_MEDIA_CODE);

		final Map<String, Map<String, String>> map = new HashMap<>();
		map.put(EN, formatMap);

		final List<ValidationError> errors = validator.validate(map, attributeDescriptor);
		assertThat(errors, not(empty()));
	}

}
