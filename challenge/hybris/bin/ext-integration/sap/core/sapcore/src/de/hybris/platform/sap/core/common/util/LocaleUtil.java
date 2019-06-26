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
package de.hybris.platform.sap.core.common.util;

import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;


/**
 * Helper class to access the <code>Locale</code> attached with the executing thread.
 * 
 */
public final class LocaleUtil
{

	private LocaleUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final ThreadLocal<Locale> locales = new ThreadLocal<Locale>()
	{
		@Override
		protected Locale initialValue()
		{
			return null;
		}
	};

	/**
	 * Get the current locale.<br>
	 * 
	 * @return locale the current locale
	 */
	public static Locale getLocale()
	{
		if (locales.get() == null)
		{
			return ((I18NService) GenericFactoryProvider.getInstance().getBean("i18NService", I18NService.class)).getCurrentLocale();
		}
		else
		{
			return locales.get();
		}
	}

	/**
	 * Set the locale (for test purposes).<br>
	 * 
	 * @param locale
	 *           the default locale to be used in tests
	 */
	public static void setLocale(final Locale locale)
	{
		if (locale == null)
		{
			locales.remove();
		}
		else
		{
			locales.set(locale);
		}
	}
}
