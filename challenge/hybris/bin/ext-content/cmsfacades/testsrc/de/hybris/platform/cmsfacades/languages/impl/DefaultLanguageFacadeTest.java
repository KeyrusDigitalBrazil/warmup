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
package de.hybris.platform.cmsfacades.languages.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultLanguageFacadeTest
{
	private static final String ENGLISH = "EN";
	private static final String GERMAN = "DE";
	private static final String GERMAN_SWISS = "de_CH";

	@InjectMocks
	private DefaultLanguageFacade languageFacade;

	@Mock
	private StoreSessionFacade storeSessionFacade;

	private LanguageData languageEN;
	private LanguageData languageDE;
	private LanguageData languageDE_CH;

	private List<LanguageData> languages;

	@Before
	public void setUp()
	{
		languageEN = new LanguageData();
		languageEN.setIsocode(ENGLISH);
		languageDE = new LanguageData();
		languageDE.setIsocode(GERMAN);
		languageDE_CH = new LanguageData();
		languageDE_CH.setIsocode(GERMAN_SWISS);

		languages = Arrays.asList(languageEN, languageDE, languageDE_CH);

		when(storeSessionFacade.getAllLanguages()).thenReturn(languages);
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageEN);
	}

	@Test
	public void getLanguagesDefaultEnglish()
	{
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageEN);
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		assertEquals(ENGLISH, languagesFound.get(0).getIsocode());
		assertTrue(languagesFound.get(0).isRequired());
		assertEquals(GERMAN, languagesFound.get(1).getIsocode());
		assertFalse(languagesFound.get(1).isRequired());
	}

	@Test
	public void getLanguagesDefaultGerman()
	{
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageDE);
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		assertEquals(GERMAN, languagesFound.get(0).getIsocode());
		assertTrue(languagesFound.get(0).isRequired());
		assertEquals(ENGLISH, languagesFound.get(1).getIsocode());
		assertFalse(languagesFound.get(1).isRequired());
	}

	@Test
	public void getLanguagesWithUnderscoreDelimiter()
	{
		// GIVEN
		when(storeSessionFacade.getDefaultLanguage()).thenReturn(languageDE_CH);

		// WHEN
		final List<LanguageData> languagesFound = languageFacade.getLanguages();

		// THEN
		assertThat(languagesFound.get(0).getIsocode(), is("de_CH"));
	}
}
