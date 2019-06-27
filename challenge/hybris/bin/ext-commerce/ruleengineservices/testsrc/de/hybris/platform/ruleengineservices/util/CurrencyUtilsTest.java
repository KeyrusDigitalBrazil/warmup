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
package de.hybris.platform.ruleengineservices.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CurrencyUtilsTest
{
	private static final String USD = "USD";
	private static final String EUR = "EUR";

	@Mock
	private CommonI18NService commonI18NService;

	private CurrencyUtils currencyUtils;
	private CurrencyModel currencyModel;
	private CurrencyModel currencyModelEUR;

	@Before
	public void setUp()
	{
		currencyUtils = new CurrencyUtils();
		currencyUtils.setCommonI18NService(commonI18NService);

		currencyModel = new CurrencyModel();
		currencyModel.setIsocode(USD);
		currencyModel.setDigits(2);
		when(commonI18NService.getCurrency(USD)).thenReturn(currencyModel);

		currencyModelEUR = new CurrencyModel();
		currencyModelEUR.setIsocode(EUR);
		currencyModelEUR.setDigits(2);
		when(commonI18NService.getCurrency(EUR)).thenReturn(currencyModelEUR);
	}

	@Test
	public void testApplyRounding()
	{
		final BigDecimal value = new BigDecimal("100.4456");
		final BigDecimal roundedValue = currencyUtils.applyRounding(value, USD);
		assertThat(roundedValue).isEqualByComparingTo(new BigDecimal("100.45"));
	}

	@Test
	public void testApplyRoundingNoDigits()
	{
		currencyModel.setDigits(null);

		final BigDecimal value = new BigDecimal("100.5456");
		final BigDecimal roundedValue = currencyUtils.applyRounding(value, USD);
		assertThat(roundedValue).isEqualByComparingTo(new BigDecimal("101"));
	}

	@Test
	public void testApplyRoundingNoPrice()
	{
		final BigDecimal roundedValue = currencyUtils.applyRounding(null, USD);
		assertThat(roundedValue).isNull();
	}

	@Test
	public void testConvertCurrencyNoConverter()
	{
		final BigDecimal value = new BigDecimal("100");
		final BigDecimal convertedValue = currencyUtils.convertCurrency(USD, EUR, value);
		assertThat(convertedValue).isEqualByComparingTo(new BigDecimal("0"));
	}

	@Test
	public void testConvertCurrencyWithConverter()
	{
		final double conversionRateUS = 1d;
		final double conversionRateEUR = 1.12345678d;

		currencyModel.setConversion(Double.valueOf(conversionRateUS));
		currencyModelEUR.setConversion(Double.valueOf(conversionRateEUR));

		final BigDecimal value = new BigDecimal("100");
		when(commonI18NService.convertAndRoundCurrency(Mockito.eq(conversionRateUS), Mockito.eq(conversionRateEUR),
				Mockito.eq(currencyModelEUR.getDigits()), Mockito.anyDouble())).thenReturn(value.doubleValue() * conversionRateEUR);

		final BigDecimal convertedValue = currencyUtils.convertCurrency(USD, EUR, value);
		assertThat(convertedValue).isEqualByComparingTo(new BigDecimal("112.35"));
	}

}
