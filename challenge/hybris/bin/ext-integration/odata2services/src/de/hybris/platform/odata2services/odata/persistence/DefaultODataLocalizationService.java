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

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

/**
 * Default implementation of the {@link ODataLocalizationService}
 */
public class DefaultODataLocalizationService implements ODataLocalizationService
{
	private CommonI18NService commonI18NService;

	private void verifyLanguageExists(final String language)
	{
		try
		{
			getCommonI18NService().getLanguage(language);
		}
		catch (final UnknownIdentifierException | IllegalArgumentException e)
		{
			throw new LanguageNotSupportedException(language, e);
		}
	}
	
	@Override
	public Locale getLocaleForLanguage(final String isoCode)
	{
		Preconditions.checkArgument(isoCode != null,
				"Cannot have a null isCode for a Language.");
		final Locale locale = getCommonI18NService().getLocaleForIsoCode(isoCode);
		verifyLanguageExists(locale.getLanguage());
		return locale;
	}

	@Override
	public Locale[] getSupportedLocales()
	{
		return getCommonI18NService().getAllLanguages().stream().map(getCommonI18NService()::getLocaleForLanguage).toArray(Locale[]::new);
	}

	@Override
	public Locale getCommerceSuiteLocale()
	{
		final LanguageModel languageModel = getCommonI18NService().getCurrentLanguage();
		return languageModel != null ? getLocaleForLanguage(languageModel.getIsocode()) : Locale.ENGLISH;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
