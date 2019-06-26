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
package de.hybris.platform.smarteditwebservices.i18n.facade;

import de.hybris.platform.smarteditwebservices.data.SmarteditLanguageData;

import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Facade to retrieve internationalization data
 */
public interface SmarteditI18nFacade
{
	/**
	 * Retrieve the translation keys and the translated values for the given locale.
	 *
	 * @param locale locale
	 * @return Map of attribute key and translated values
	 */
	Map<String, String> getTranslationMap(Locale locale);

	/**
	 * Retrieve the translation keys and the translated values for the given language tag.
	 *
	 * @param languageTag language tag
	 * @return Map of attribute key and translated values
	 * @deprecated since 1808, the backend should accept only languages with underscore (in java format)
	 */
	@Deprecated
	Map<String, String> getTranslationMap(String languageTag);

	/**
	 * Retrieve the supported smartedit languages
	 *
	 * @return a {@link List} of {@link SmarteditLanguageData}
	 */
	List<SmarteditLanguageData> getSupportedLanguages();
}
