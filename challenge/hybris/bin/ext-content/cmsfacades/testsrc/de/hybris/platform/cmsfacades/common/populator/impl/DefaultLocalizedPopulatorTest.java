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
package de.hybris.platform.cmsfacades.common.populator.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultLocalizedPopulatorTest
{

	private static final String ENGLISH_ISOCODE = Locale.ENGLISH.toString();
	private static final String GERMAN_ISOCODE = Locale.GERMAN.toString();
	private static final String FRENCH_CA_ISOCODE = Locale.CANADA_FRENCH.toString();

	private static final String FRENCH_VALUE = "Some French Value";
	private static final String ENGLISH_VALUE = "Some English Value";
	private static final String GERMAN_VALUE = "Some German Value";

	@Mock
	private LanguageFacade languageFacade;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private CMSUserService cmsUserService;

	@InjectMocks
	private DefaultLocalizedPopulator defaultLocalizedPopulator;

	@Mock
	private BiConsumer<Locale, String> setter;

	@Mock
	private Function<Locale, String> getter;

	@Before
	public void setUp()
	{
		setUserReadableLanguages(GERMAN_ISOCODE, FRENCH_CA_ISOCODE, ENGLISH_ISOCODE);

		when(getter.apply(Locale.GERMAN)).thenReturn(GERMAN_VALUE);
		when(getter.apply(Locale.ENGLISH)).thenReturn(ENGLISH_VALUE);
		when(getter.apply(Locale.CANADA_FRENCH)).thenReturn(FRENCH_VALUE);
	}

	@Test
	public void givenLocaleWithNoUnderscore_WhenPopulateIsCalled_ThenLocalizedValueIsPopulated()
	{
		// GIVEN
		setUpLanguages(Locale.ENGLISH);

		// WHEN
		defaultLocalizedPopulator.populate(setter, getter);

		// THEN
		verify(languageFacade).getLanguages();
		assertValueForLanguageWasPopulated(Locale.ENGLISH, ENGLISH_VALUE);
	}

	@Test
	public void testPopulatorWithLocalContainingUnderscore()
	{
		// GIVEN
		setUpLanguages(Locale.CANADA_FRENCH);

		// WHEN
		defaultLocalizedPopulator.populate(setter, getter);

		// THEN
		verify(languageFacade).getLanguages();
		assertValueForLanguageWasPopulated(Locale.CANADA_FRENCH, FRENCH_VALUE);
	}

	@Test
	public void givenUserHasReadAccessToSomeLanguages_WhenPopulateIsCalled_ThenItPopulatesOnlyTheLanguagesHeCanRead()
	{
		// GIVEN
		setUpLanguages(Locale.CANADA_FRENCH, Locale.GERMAN, Locale.ENGLISH);
		setUserReadableLanguages(GERMAN_ISOCODE, FRENCH_CA_ISOCODE);

		// WHEN
		defaultLocalizedPopulator.populate(setter, getter);

		// THEN
		assertValueForLanguageWasPopulated(Locale.GERMAN, GERMAN_VALUE);
		assertValueForLanguageWasPopulated(Locale.CANADA_FRENCH, FRENCH_VALUE);
		assertValueForLanguageWasNotPopulated(Locale.ENGLISH, ENGLISH_VALUE);
	}

	// ---------------------------------------------------------------------------------------------------------------------
	// Helper Methods
	// ---------------------------------------------------------------------------------------------------------------------
	protected void setUpLanguages(final Locale ...locales)
	{
		when(languageFacade.getLanguages()).then(answer ->
				Arrays.stream(locales)
					.map(this::buildAndConfigureLanguageData)
					.collect(Collectors.toList())
		);
	}

	protected void setUserReadableLanguages(final String ...readableLanguages)
	{
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(
				new HashSet<>(Arrays.asList(readableLanguages)));
	}

	protected LanguageData buildAndConfigureLanguageData(final Locale locale)
	{
		String isoCode = locale.toString();
		final LanguageData languageData = new LanguageData();
		languageData.setIsocode(isoCode);

		when(commonI18NService.getLocaleForIsoCode(isoCode)).thenReturn(locale);

		return languageData;
	}

	protected void assertValueForLanguageWasPopulated(final Locale locale, final String localizedValue)
	{
		verify(commonI18NService).getLocaleForIsoCode(locale.toString());
		verify(getter).apply(locale);
		verify(setter).accept(locale, localizedValue);
	}

	protected void assertValueForLanguageWasNotPopulated(final Locale locale, final String localizedValue)
	{
		verify(commonI18NService, never()).getLocaleForIsoCode(locale.toString());
		verify(getter, never()).apply(locale);
		verify(setter, never()).accept(locale, localizedValue);
	}
}
