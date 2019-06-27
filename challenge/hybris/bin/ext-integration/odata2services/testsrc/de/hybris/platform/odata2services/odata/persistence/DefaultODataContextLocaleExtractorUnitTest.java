/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultODataContextLocaleExtractorUnitTest
{
	@Mock
	private ODataContext oDataContext;
	@Mock
	private ODataRequest oDataRequest;
	@Mock
	private ODataLocalizationService localizationService;
	@InjectMocks
	private DefaultODataContextLocaleExtractor localeExtractor;

	@Before
	public void setUp()
	{
		when(localizationService.getCommerceSuiteLocale()).thenReturn(Locale.ENGLISH);
	}

	@Test
	public void testExtractFromInvalidHeaderName()
	{
		assertThatThrownBy(() -> localeExtractor.extractFrom(oDataContext, "invalidHeaderName"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("not supported");
	}

	@Test
	public void testExtractFromMissingContentLanguage()
	{
		assertThat(localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE))
				.isEqualTo(Locale.ENGLISH);
	}

	@Test
	public void testExtractFromMissingAcceptLanguage()
	{
		assertThat(localeExtractor.extractFrom(oDataContext, HttpHeaders.ACCEPT_LANGUAGE))
				.isEqualTo(Locale.ENGLISH);
	}

	@Test
	public void testExtractFromEmptyContentLanguage()
	{
		stubODataRequestContentLanguageHeader(StringUtils.EMPTY);
		stubLocalizationService(StringUtils.EMPTY);

		assertThat(localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE))
				.isEqualTo(Locale.ENGLISH);
	}

	@Test
	public void testExtractFromEmptyAcceptLanguage()
	{
		stubODataRequestAcceptLanguagesHeader(StringUtils.EMPTY);

		assertThat(localeExtractor.extractFrom(oDataContext, HttpHeaders.ACCEPT_LANGUAGE))
				.isEqualTo(Locale.ENGLISH);
	}
	
	@Test
	public void testExtractFromGermanContentLanguage()
	{
		stubODataRequestContentLanguageHeader("de");
		stubLocalizationService("de");

		assertThat(localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE))
				.isEqualTo(Locale.GERMAN);
	}

	@Test
	public void testExtractFromGermanAcceptLanguage()
	{
		stubODataRequestAcceptLanguagesHeader(Locale.GERMAN.getLanguage());
		stubLocalizationService("de");

		assertThat(localeExtractor.extractFrom(oDataContext, HttpHeaders.ACCEPT_LANGUAGE))
				.isEqualTo(Locale.GERMAN);
	}

	@Test
	public void testExtractFromInvalidContentLanguage()
	{
		stubODataRequestContentLanguageHeader("nonExistingLanguageIsoCode");
		stubLocalizationService("nonExistingLanguageIsoCode");
		stubLocalizationServiceGetLanguageThrowsError();

		assertThatThrownBy(() -> localeExtractor.extractFrom(oDataContext, HttpHeaders.CONTENT_LANGUAGE ))
				.isInstanceOf(LanguageNotSupportedException.class)
				.hasFieldOrPropertyWithValue("httpStatus", HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testExtractFromInvalidAcceptLanguage()
	{
		stubODataRequestAcceptLanguagesHeader("nonExistingLanguageIsoCode");
		stubLocalizationService("nonExistingLanguageIsoCode");
		stubLocalizationServiceGetLanguageThrowsError();

		assertThatThrownBy(() -> localeExtractor.extractFrom(oDataContext, HttpHeaders.ACCEPT_LANGUAGE ))
				.isInstanceOf(LanguageNotSupportedException.class)
				.hasFieldOrPropertyWithValue("httpStatus", HttpStatusCodes.BAD_REQUEST);
	}

	@Test
	public void testExtractAcceptLanguageEmptyAcceptLanguage()
	{
		stubODataRequestAcceptLanguagesHeader(StringUtils.EMPTY);

		assertThat(localeExtractor.getAcceptLanguage(oDataContext))
				.isEqualTo(Optional.empty());
	}

	@Test
	public void testExtractAcceptLanguageInvalidAcceptLanguage()
	{
		stubODataRequestAcceptLanguagesHeader("nonExistingLanguageIsoCode");
		stubLocalizationServiceGetLanguageThrowsError();

		assertThatThrownBy(() -> localeExtractor.getAcceptLanguage(oDataContext))
				.isInstanceOf(LanguageNotSupportedException.class)
				.hasFieldOrPropertyWithValue("httpStatus", HttpStatusCodes.BAD_REQUEST);
	}

	private void stubODataRequestAcceptLanguagesHeader(final String language)
	{
		doReturn(Collections.singletonList(new Locale(language))).when(oDataContext).getAcceptableLanguages();
	}

	private void stubODataRequestContentLanguageHeader(final String contentLanguage)
	{
		doReturn(contentLanguage).when(oDataContext).getRequestHeader(any(String.class));
	}

	private void stubLocalizationService(final String languageTrueCode)
	{
		final Locale locale = new Locale(languageTrueCode);
		doReturn(locale).when(localizationService).getLocaleForLanguage(languageTrueCode);
	}
	
	private void stubLocalizationServiceGetLanguageThrowsError()
	{
		doThrow(new LanguageNotSupportedException("", new SystemException(""))).when(localizationService).getLocaleForLanguage(any(String.class));
	}
}