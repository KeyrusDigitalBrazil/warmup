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

import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link LanguageFacade}.
 */
public class DefaultLanguageFacade implements LanguageFacade
{
	private StoreSessionFacade storeSessionFacade;
	private CommonI18NService commonI18NService;

	@Override
	public List<LanguageData> getLanguages()
	{
		final List<LanguageData> languageDataList = new ArrayList<>();

		final LanguageData defaultLanguage = getStoreSessionFacade().getDefaultLanguage();

		for (final LanguageData language : getStoreSessionFacade().getAllLanguages())
		{
			if (defaultLanguage.getIsocode().equalsIgnoreCase(language.getIsocode()))
			{
				language.setRequired(true);
				languageDataList.add(0, language);
			}
			else
			{
				languageDataList.add(language);
			}
		}

		return languageDataList;
	}

	/**
	 * Converts a languageData.isocode to hybris format (i.e. de-CH)
	 *
	 * @param languageDataList List of language Data
	 * @deprecated since 1808, the conversion should be made on a frontend side.
	 */
	@Deprecated
	protected void convertLocalToIsoCodes(final List<LanguageData> languageDataList)
	{
		languageDataList.stream()
				.forEach(languageData ->
				{
					final Locale locale = getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode());
					languageData.setIsocode(locale.toLanguageTag());
				});
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	/**
	 * @deprecated since 1808
	 */
	@Deprecated
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @deprecated since 1808
	 */
	@Deprecated
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
