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
package com.hybris.backoffice.cockpitng;

import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.cockpitng.dataaccess.facades.object.validation.BackofficeLocalizationAwareValidationService;
import com.hybris.cockpitng.validation.LocalizedQualifier;
import com.hybris.cockpitng.validation.ValidationContext;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeLocalizationAwareValidationHandlerTest
{
	private static final ValidationContext VALIDATION_CONTEXT = mock(ValidationContext.class);

	@Mock
	BackofficeLocalizationAwareValidationService service;
	@InjectMocks
	BackofficeLocalizationAwareValidationHandler handler;

	@Test
	public void shouldDelegateValidateToValidationService()
	{
		// given
		final Object objectToValidate = mock(Object.class);

		// when

		handler.validate(objectToValidate, VALIDATION_CONTEXT);

		// then
		then(service).should().validate(same(objectToValidate), same(VALIDATION_CONTEXT));
	}

	@Test
	public void shouldDelegateValidateWithQualifiersToValidationService()
	{
		// given
		final Object objectToValidate = mock(Object.class);
		final LinkedList<String> qualifiers = new LinkedList<>();

		// when
		handler.validate(objectToValidate, qualifiers, VALIDATION_CONTEXT);

		// then
		then(service).should().validate(same(objectToValidate), same(qualifiers), same(VALIDATION_CONTEXT));
	}

	@Test
	public void shouldDelegateValidateWithQualifiersWithLocalesToValidationService()
	{
		// given
		final Object objectToValidate = mock(Object.class);
		final Collection<LocalizedQualifier> qualifiersWithLocales = new LinkedList<>();

		// when
		handler.validate(objectToValidate, qualifiersWithLocales, VALIDATION_CONTEXT);

		// then
		then(service).should().validate(same(objectToValidate), same(qualifiersWithLocales), same(VALIDATION_CONTEXT));
	}
}
