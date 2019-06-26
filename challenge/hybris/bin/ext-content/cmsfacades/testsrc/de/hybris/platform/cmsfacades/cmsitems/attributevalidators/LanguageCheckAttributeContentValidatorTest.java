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
package de.hybris.platform.cmsfacades.cmsitems.attributevalidators;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LanguageCheckAttributeContentValidatorTest
{
	private static final String EN = "en";
	private static final String DE = "de";
	@Mock
	private ValidationErrorsProvider validationErrorsProvider;
	@Mock
	private LanguageFacade languageFacade;
	@InjectMocks
	private LanguageCheckAttributeContentValidator validator;
	

	@Mock
	private LanguageData enLanguage;
	@Mock
	private LanguageData deLanguage;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private ValidationErrors validationErrors;

	@Before
	public void setup()
	{
		when(validationErrorsProvider.getCurrentValidationErrors()).thenReturn(validationErrors);
		when(languageFacade.getLanguages()).thenReturn(Arrays.asList(enLanguage, deLanguage));
		
		when(enLanguage.getIsocode()).thenReturn(EN);
		when(enLanguage.isRequired()).thenReturn(true);
		when(deLanguage.getIsocode()).thenReturn(DE);
		when(deLanguage.isRequired()).thenReturn(false);
	}
	
	@Test
	public void testValidLocalizedContent_shouldNotAddError()
	{
		final Map<String, Object> value = new HashMap<>();
		value.put(EN, "");
		value.put(DE, "");
		
		validator.validate(value, attributeDescriptor);
		verifyZeroInteractions(validationErrorsProvider);
	}

	@Test
	public void testInvalidLocalizedContent_shouldAddError()
	{
		final Map<String, Object> value = new HashMap<>();
		value.put(DE, "");

		final List<ValidationError> errors = validator.validate(value, attributeDescriptor);
		assertThat(errors, not(empty()));
	}

}
