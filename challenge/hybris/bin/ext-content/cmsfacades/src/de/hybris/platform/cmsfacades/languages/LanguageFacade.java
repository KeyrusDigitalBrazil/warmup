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
package de.hybris.platform.cmsfacades.languages;

import de.hybris.platform.commercefacades.storesession.data.LanguageData;

import java.util.List;


/**
 * Language facade interface which deals with methods related to language operations.
 */
public interface LanguageFacade
{

	/**
	 * Get all languages for the current active site. The languages are ordered starting with the default language.<br>
	 * For example: Given a site supports [ "EN", "DE", "JP", "IT" ] and <i>German</i> is the default language, the
	 * resulted list would be ordered: [ "<b>DE</b>", "EN", "JP", "IT" ]
	 *
	 * @param siteId
	 *           the current cmsSite-id.
	 * @return list of languages.
	 */
	List<LanguageData> getLanguages();

}
