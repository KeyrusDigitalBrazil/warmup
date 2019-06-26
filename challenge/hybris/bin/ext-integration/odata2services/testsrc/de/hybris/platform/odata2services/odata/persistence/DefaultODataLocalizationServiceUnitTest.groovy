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
package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultODataLocalizationServiceUnitTest extends Specification {
	def commonI18NService = Mock(CommonI18NService)

	def oDataLocalizationService = new DefaultODataLocalizationService()

	def setup() {
		oDataLocalizationService.setCommonI18NService(commonI18NService)
	}

	@Test
	def "successfully get the locale for the language iso code"() {
		given:
		def locale = Locale.ENGLISH
		commonI18NService.getLocaleForIsoCode("en") >> locale

		when:
		def actualLocale = oDataLocalizationService.getLocaleForLanguage("en")

		then:
		locale == actualLocale
		1 * commonI18NService.getLanguage("en")
	}

	@Test
	def "exception is thrown when isocode is = 'not a language code'"() {
		given:
		final String isoCode = "not a language code"
		commonI18NService.getLocaleForIsoCode(isoCode) >> new Locale(isoCode)
		commonI18NService.getLanguage(isoCode) >> { throw Mock(UnknownIdentifierException) }

		when:
		oDataLocalizationService.getLocaleForLanguage(isoCode)

		then:
		def e = thrown LanguageNotSupportedException
		e.getCause() instanceof UnknownIdentifierException
	}

	@Test
	def "exception is thrown when isocode is null"() {
		when:
		oDataLocalizationService.getLocaleForLanguage(null)

		then:
		thrown IllegalArgumentException
	}

	@Test
	def "get all supported locales when languages exist"()
	{
		given:
		def lang1 = Mock(LanguageModel)
		def lang2 = Mock(LanguageModel)
		commonI18NService.getAllLanguages() >> [lang1, lang2]

		commonI18NService.getLocaleForLanguage(lang1) >> Locale.ENGLISH
		commonI18NService.getLocaleForLanguage(lang2) >> Locale.FRENCH

		when:
		def locales = oDataLocalizationService.getSupportedLocales()

		then:
		locales == [Locale.ENGLISH, Locale.FRENCH].toArray()
	}

	@Test
	def "no supported languages"()
	{
		given:
		commonI18NService.getAllLanguages() >> []

		when:
		def locales = oDataLocalizationService.getSupportedLocales()

		then:
		locales == new Locale[0]
	}

	@Test
	@Unroll
	def "default locale is '#expectedLocale' when the Commerce Suite's default language is '#csIsoCode'"()
	{
		given:
		commonI18NService.getCurrentLanguage() >> language(csIsoCode)
		commonI18NService.getLocaleForIsoCode(_ as String) >> expectedLocale

		when:
		def actualLocale = oDataLocalizationService.getCommerceSuiteLocale()

		then:
		expectedLocale == actualLocale

		where:
		csIsoCode 	| expectedLocale
		"de" 		| Locale.GERMAN
		null		| Locale.ENGLISH
	}

	def language(def isoCode)
	{
		if (isoCode != null)
		{
			Mock(LanguageModel) {
				getIsocode() >> isoCode
			}
		}
	}
}
