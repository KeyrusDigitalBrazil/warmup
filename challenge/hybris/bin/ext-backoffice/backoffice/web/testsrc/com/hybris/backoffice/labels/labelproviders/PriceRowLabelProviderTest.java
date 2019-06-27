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
package com.hybris.backoffice.labels.labelproviders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.labels.impl.PriceLabelHandler;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@RunWith(MockitoJUnitRunner.class)
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class PriceRowLabelProviderTest extends AbstractCockpitngUnitTest<PriceRowLabelProvider>
{
	public static final int MILLION = 1000000;
	public static final String DONG_ISO_CODE = "VND";
	public static final String DONG_SYMBOL = "₫";
	private double USD_TO_VIETNAMESE_DONG = 22725.77d;

	@Spy
	@InjectMocks
	private PriceRowLabelProvider provider;

	@Spy
	@InjectMocks
	private PriceLabelHandler priceLabelHandler;

	@Mock
	private I18NService i18NService;


	@Test
	public void shouldReturnValidLabelForVietnameseDong()
	{
		assertThat(provider.getLabel(null)).isEqualTo("");
		final CurrencyModel dong = mock(CurrencyModel.class);
		when(dong.getIsocode()).thenReturn(DONG_ISO_CODE);
		when(dong.getSymbol()).thenReturn(DONG_SYMBOL);

		final PriceRowModel priceRow = mock(PriceRowModel.class);
		when(priceRow.getCurrency()).thenReturn(dong);
		when(priceRow.getPrice()).thenReturn(MILLION * USD_TO_VIETNAMESE_DONG);
		when(i18NService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
		when(i18NService.getBestMatchingJavaCurrency(DONG_ISO_CODE)).thenReturn(Currency.getInstance(DONG_ISO_CODE));
		assertThat(provider.getLabel(priceRow)).contains("₫22,725,770,000");
	}

}
