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
package com.hybris.backoffice.labels.impl;

import static java.util.Currency.getInstance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@RunWith(MockitoJUnitRunner.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class PriceLabelHandlerTest extends AbstractCockpitngUnitTest<PriceLabelHandler>
{

	public static final String DONG_ISO_CODE = "VND";
	public static final String DONG_SYMBOL = "₫";

	@Spy
    @InjectMocks
	private PriceLabelHandler handler;

	@Mock
	private I18NService i18NService;

	@Mock
	private CurrencyModel dong;

	@Mock
	private CurrencyModel yen;

	@Test
	public void shouldReturnValidLabelsForDongIsoCode()
	{
		when(dong.getIsocode()).thenReturn(DONG_ISO_CODE);
		when(dong.getSymbol()).thenReturn(DONG_SYMBOL);
		when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
		when(i18NService.getBestMatchingJavaCurrency(DONG_ISO_CODE)).thenReturn(getInstance(DONG_ISO_CODE));

		assertThat(handler.getLabel(0d, dong)).isEqualTo("₫0");
		assertThat(handler.getLabel(22725.77d, dong)).isEqualTo("₫22,726");
		assertThat(handler.getLabel(0.123d, dong)).isEqualTo("₫0");
		assertThat(handler.getLabel(-1.1598d, dong)).isEqualTo("-₫1");
	}

	@Test
	public void shouldReturnValidLabelsForYenCurrencyForEnglishLocale()
	{
		when(yen.getIsocode()).thenReturn("JPY");
		when(yen.getSymbol()).thenReturn("¥");
		when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
		when(i18NService.getBestMatchingJavaCurrency("JPY")).thenReturn(getInstance("JPY"));
		when(yen.getDigits()).thenReturn(0);

		assertThat(handler.getLabel(0d, yen)).isEqualTo("¥0");
		assertThat(handler.getLabel(2272577d, yen)).isEqualTo("¥2,272,577");
		assertThat(handler.getLabel(0.123d, yen)).isEqualTo("¥0");
		assertThat(handler.getLabel(-1.1598d, yen)).isEqualTo("-¥1");
	}

	@Test
	public void shouldReturnValidLabelsForYenCurrencyForFrenchLocale()
	{
		when(yen.getIsocode()).thenReturn("JPY");
		when(yen.getSymbol()).thenReturn("¥");
		when(i18NService.getCurrentLocale()).thenReturn(Locale.FRENCH);
		when(i18NService.getBestMatchingJavaCurrency("JPY")).thenReturn(getInstance("JPY"));
		when(yen.getDigits()).thenReturn(0);

		assertThat(handler.getLabel(0d, yen)).isEqualTo("0 ¥");
		assertThat(handler.getLabel(2272577d, yen)).isEqualTo("2 272 577 ¥");
		assertThat(handler.getLabel(0.123d, yen)).isEqualTo("0 ¥");
		assertThat(handler.getLabel(-1.1598d, yen)).isEqualTo("-1 ¥");
	}

}
