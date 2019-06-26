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
package com.hybris.backoffice.cockpitng.dataaccess.facades.object.validation.impl;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.services.impl.LocalizedHybrisConstraintViolation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.cockpitng.validation.LocalizedQualifier;
import com.hybris.cockpitng.validation.ValidationContext;
import com.hybris.cockpitng.validation.model.ValidationInfo;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeLocalizationAwareValidationServiceTest
{
	private static final Object NULL_OBJECT_TO_VALIDATE = null;

	@InjectMocks
	@Spy
	DefaultBackofficeLocalizationAwareValidationService backofficeValidationService;

	@Test
	public void shouldExtendBackofficeValidationService()
	{
		assertThat(backofficeValidationService).isInstanceOf(DefaultBackofficeValidationService.class);
	}

	@Test
	public void shouldReturnEmptyListWhenObjectToValidateIsNull()
	{
		// given
		final Collection<LocalizedQualifier> localizedQualifiers = new LinkedList<>();
		final ValidationContext validationContext = mock(ValidationContext.class);

		// when
		final List<ValidationInfo> validationInfos = backofficeValidationService.validate(NULL_OBJECT_TO_VALIDATE,
				localizedQualifiers, validationContext);

		// then
		assertThat(validationInfos).isEmpty();
	}

	@Test
	public void shouldCallValidatePropertiesWithLocalizedQualifierNames()
	{
		// given
		final Object objectToValidate = mock(Object.class);
		final ValidationContext validationContext = mock(ValidationContext.class);

		final LinkedList<LocalizedQualifier> localizedQualifiers = new LinkedList<>();
		localizedQualifiers.add(new LocalizedQualifier("name", Arrays.asList(Locale.CANADA, Locale.JAPAN)));
		localizedQualifiers.add(new LocalizedQualifier("description", Arrays.asList(Locale.ENGLISH, Locale.GERMAN)));
		localizedQualifiers.add(new LocalizedQualifier("ean"));

		// when
		backofficeValidationService.validate(objectToValidate, localizedQualifiers, validationContext);

		// then
		then(backofficeValidationService).should().validateProperties(objectToValidate, Arrays.asList("name", "description", "ean"),
				validationContext);
	}

	@Test
	public void shouldTranslateHybrisConstraintViolations()
	{
		// given
		final Object objectToValidate = mock(Object.class);
		final ValidationContext validationContext = mock(ValidationContext.class);

		final LinkedList<LocalizedQualifier> localizedQualifiers = new LinkedList<>();
		localizedQualifiers.add(new LocalizedQualifier("name", Arrays.asList(Locale.CANADA, Locale.JAPAN)));
		localizedQualifiers.add(new LocalizedQualifier("description", Arrays.asList(Locale.ENGLISH, Locale.GERMAN)));
		localizedQualifiers.add(new LocalizedQualifier("ean"));

		final Set<HybrisConstraintViolation> constraintViolations = new HashSet<>();
		constraintViolations.add(mock(HybrisConstraintViolation.class));
		willReturn(constraintViolations).given(backofficeValidationService).validateProperties(any(), any(), any());
		final List<ValidationInfo> expectedValidationInfos = new ArrayList<>();
		expectedValidationInfos.add(mock(ValidationInfo.class));
		willReturn(expectedValidationInfos).given(backofficeValidationService).translatePlatformViolations(any(), any(), any());

		// when
		final List<ValidationInfo> validationInfos = backofficeValidationService.validate(objectToValidate, localizedQualifiers,
				validationContext);

		// then
		assertThat(validationInfos).isSameAs(expectedValidationInfos);
		then(backofficeValidationService).should().translatePlatformViolations(objectToValidate, validationContext,
				constraintViolations);
	}

	@Test
	public void shouldFilterOutInvalidLocalizedConstraintForPassedLocalizedQualifiers()
	{
		// given
		final Object objectToValidate = mock(Object.class);
		final ValidationContext validationContext = mock(ValidationContext.class);

		final LocalizedQualifier localizedQualifierForCanadaAndJapan = new LocalizedQualifier("name",
				Arrays.asList(Locale.CANADA, Locale.JAPAN));

		final LinkedList<LocalizedQualifier> localizedQualifiers = new LinkedList<>();
		localizedQualifiers.add(localizedQualifierForCanadaAndJapan);


		final Set<HybrisConstraintViolation> constraintViolations = new HashSet<>();
		final LocalizedHybrisConstraintViolation englishNameViolation = createLocalizedConstraintViolation("name", Locale.ENGLISH);
		constraintViolations.add(englishNameViolation);
		final LocalizedHybrisConstraintViolation japanNameViolation = createLocalizedConstraintViolation("name", Locale.JAPAN);
		constraintViolations.add(japanNameViolation);
		final LocalizedHybrisConstraintViolation germanNameViolation = createLocalizedConstraintViolation("name", Locale.GERMAN);
		constraintViolations.add(germanNameViolation);
		willReturn(constraintViolations).given(backofficeValidationService).validateProperties(any(), any(), any());

		willReturn(emptyList()).given(backofficeValidationService).translatePlatformViolations(any(), any(), any());

		// when
		backofficeValidationService.validate(objectToValidate, localizedQualifiers, validationContext);

		// then
		then(backofficeValidationService).should().translatePlatformViolations(objectToValidate, validationContext,
				Sets.newHashSet(japanNameViolation));
	}

	protected LocalizedHybrisConstraintViolation createLocalizedConstraintViolation(final String property, final Locale locale)
	{
		final LocalizedHybrisConstraintViolation constraintViolation = mock(LocalizedHybrisConstraintViolation.class);
		given(constraintViolation.getViolationLanguage()).willReturn(locale);
		given(constraintViolation.getProperty()).willReturn(property);
		return constraintViolation;
	}
}
