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
package com.hybris.backoffice.i18n;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zkoss.util.Locales;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Session;


@UnitTest
public class BackofficeLocaleServiceTest
{
	@Mock
	private I18NService i18nService;

	@Mock
	private Session session;

	private BackofficeLocaleService localeService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		localeService = new BackofficeLocaleService()
		{
			@Override
			protected Optional<Session> lookupZkSession() {
				return Optional.of(session);
			}
		};
		localeService.setI18nService(i18nService);
	}

	@Test
	public void testChangeCurrentLocale()
	{
		final Locale localeEN = Locale.ENGLISH;
		localeService.setCurrentLocale(localeEN);
		verify(session, times(1)).setAttribute(Attributes.PREFERRED_LOCALE, localeEN);
		verify(i18nService, times(1)).setCurrentLocale(localeEN);
		assertEquals(Locales.getThreadLocal(), localeEN);
		assertEquals(localeService.getCurrentLocale(), localeEN);

		final Locale localeDE = Locale.GERMAN;
		localeService.setCurrentLocale(localeDE);
		verify(session, times(1)).setAttribute(Attributes.PREFERRED_LOCALE, localeDE);
		verify(i18nService, times(1)).setCurrentLocale(localeDE);
		assertEquals(Locales.getThreadLocal(), localeDE);
		assertEquals(localeService.getCurrentLocale(), localeDE);
	}
}
