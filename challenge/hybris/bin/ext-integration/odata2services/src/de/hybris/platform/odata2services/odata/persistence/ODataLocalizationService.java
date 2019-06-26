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

import java.util.Locale;

/**
 * Defines functionality to handle localization
 */
public interface ODataLocalizationService
{
	/**
	 * Looks up a locale supported by the Commerce Suite by the standard ISO code, i.e "en"
	 * @param isoCode The isoCode used to search for an existing LanguageModel
	 * @return The locale
	 */
	Locale getLocaleForLanguage(final String isoCode);

	/**
	 * Looks up a all locales supported by this Commerce Suite installation.
	 *
	 * @return an array of locales. May return empty if no locales are supported. Will never return null.
	 */
	Locale[] getSupportedLocales();

	/**
	 * Gets the locale of the Commerce Suite
	 * @return The locale
	 */
	Locale getCommerceSuiteLocale();
}
