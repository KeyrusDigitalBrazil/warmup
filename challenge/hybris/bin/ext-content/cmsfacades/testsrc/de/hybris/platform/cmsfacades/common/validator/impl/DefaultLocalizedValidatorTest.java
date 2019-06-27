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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings(
		{ "rawtypes", "unchecked" })
public class DefaultLocalizedValidatorTest
{
	private static final String CONTENT = "content";
	private static final String FRENCH = "fr";
	private static final String ENGLISH = "en";
	private static final String SPANISH = "es";

	@InjectMocks
	private DefaultLocalizedValidator validator;

	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private BiConsumer consumer;
	@Mock
	private Function function;
	@Mock
	private Errors errors;

	@Mock
	private LanguageData english;
	@Mock
	private LanguageData french;
	@Mock
	private LanguageData spanish;

	@Before
	public void setUp()
	{
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(english, french, spanish));

		when(english.getIsocode()).thenReturn(ENGLISH);
		when(english.isRequired()).thenReturn(Boolean.TRUE);
		when(french.getIsocode()).thenReturn(FRENCH);
		when(french.isRequired()).thenReturn(Boolean.FALSE);
		when(spanish.getIsocode()).thenReturn(SPANISH);
		when(spanish.isRequired()).thenReturn(Boolean.FALSE);

		when(function.apply(ENGLISH)).thenReturn(CONTENT);
		when(function.apply(FRENCH)).thenReturn(CONTENT);
		when(function.apply(SPANISH)).thenReturn(CONTENT);
	}

	@Test
	public void shouldNotValidateAll_NoLanguages()
	{
		when(languageFacade.getLanguages()).thenReturn(Collections.emptyList());
		validator.validateAllLanguages(consumer, function, errors);
		verifyZeroInteractions(consumer, function, errors);
	}

	@Test
	public void shouldValidateAll()
	{
		validator.validateAllLanguages(consumer, function, errors);

		verify(consumer, times(1)).accept(ENGLISH, CONTENT);
		verify(function, times(1)).apply(ENGLISH);
		verify(consumer, times(1)).accept(FRENCH, CONTENT);
		verify(function, times(1)).apply(FRENCH);
		verify(consumer, times(1)).accept(SPANISH, CONTENT);
		verify(function, times(1)).apply(SPANISH);
	}

	@Test
	public void shouldNotValidateRequired_NoRequiredLanguages()
	{
		when(english.isRequired()).thenReturn(Boolean.FALSE);
		validator.validateRequiredLanguages(consumer, function, errors);
		verifyZeroInteractions(consumer, function, errors);
	}

	@Test
	public void shouldValidateRequiredOnly()
	{
		validator.validateRequiredLanguages(consumer, function, errors);

		verify(consumer, times(1)).accept(ENGLISH, CONTENT);
		verify(function, times(1)).apply(ENGLISH);
		verify(consumer, times(0)).accept(FRENCH, CONTENT);
		verify(function, times(0)).apply(FRENCH);
		verify(consumer, times(0)).accept(SPANISH, CONTENT);
		verify(function, times(0)).apply(SPANISH);
	}
}
