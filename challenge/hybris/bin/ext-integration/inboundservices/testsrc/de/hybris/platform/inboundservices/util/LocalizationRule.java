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
package de.hybris.platform.inboundservices.util;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.junit.rules.ExternalResource;

/**
 * A rule for controlling the Platform session language in tests
 */
public class LocalizationRule extends ExternalResource
{

	private final LanguageModel originalLanguage;
	private final CommonI18NService i18NService;

	public static LocalizationRule initialize()
	{
		return new LocalizationRule();
	}

	private LocalizationRule()
	{
		i18NService = getI18Service();
		originalLanguage = getI18Service().getCurrentLanguage();
	}

	/**
	 * Sets the current language to language identified by {@code isoCode}
	 * @param isoCode the isoCode of the Language to be set
	 */
	public void setSessionLanguage(final String isoCode)
	{
		final LanguageModel language = i18NService.getLanguage(isoCode);
		i18NService.setCurrentLanguage(language);
	}

	@Override
	protected void after()
	{
		i18NService.setCurrentLanguage(originalLanguage);
	}

	private CommonI18NService getI18Service()
	{
		return Registry.getApplicationContext()
				.getBean("commonI18NService", CommonI18NService.class);
	}
}
