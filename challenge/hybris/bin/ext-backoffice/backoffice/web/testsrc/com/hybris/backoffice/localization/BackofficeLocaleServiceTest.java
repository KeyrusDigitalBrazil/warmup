/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.localization;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.zkoss.zk.ui.Session;


@UnitTest
public class BackofficeLocaleServiceTest
{
	@Mock
	private I18NService i18nService;

	@Mock
	private Session session;


	private PlatformFallbackLocaleProvider platformFallbackLocaleProvider;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		platformFallbackLocaleProvider = new PlatformFallbackLocaleProvider();
		platformFallbackLocaleProvider.setI18NService(i18nService);
	}

	@Test
	public void testConnection()
	{
		final Locale baselocaleEN = Locale.ENGLISH;
		final Locale fallbackLocale = Locale.CANADA_FRENCH;

		Mockito.when(i18nService.getFallbackLocales(baselocaleEN)).thenReturn(new Locale[]
		{ fallbackLocale });

		final List<Locale> fallbackLocales = platformFallbackLocaleProvider.getFallbackLocales(baselocaleEN);
		Assert.assertEquals(fallbackLocales.get(0), fallbackLocale);
	}
}
