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
package de.hybris.platform.platformbackoffice.taxdiscountvalueparser;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DiscountValueParserTest extends ServicelayerBaseTest
{
	@Resource
	DiscountValueParser discountValueParser;

	@Before
	public void setUp()
	{
		getOrCreateCurrency("EUR");
	}

	@Test
	public void parseValues() throws ParserException
	{
		//given
		final String stringValue = "test : 19.00% = 1.92";

		//when
		final TaxOrDiscount parsedValue = discountValueParser.parseTaxOrDiscount(stringValue);

		//then
		assertThat(parsedValue.getCode()).isEqualTo("test");
		assertThat(parsedValue.getValue()).isEqualTo(19.0);
		assertThat(parsedValue.getAppliedValue()).isEqualTo(1.92);
		assertThat(parsedValue.getCurrencyIsoCode()).isEqualTo("%");
	}

	@Test
	public void parseValuesWithoutSpaces() throws ParserException
	{
		//given
		final String stringValue = "test:19.00%=1.92";

		//when
		final TaxOrDiscount parsedValue = discountValueParser.parseTaxOrDiscount(stringValue);

		//then
		assertThat(parsedValue.getCode()).isEqualTo("test");
		assertThat(parsedValue.getValue()).isEqualTo(19.0);
		assertThat(parsedValue.getAppliedValue()).isEqualTo(1.92);
		assertThat(parsedValue.getCurrencyIsoCode()).isEqualTo("%");
	}

	@Test
	public void parseValuesWithAdditionalSpaces() throws ParserException
	{
		//given
		final String stringValue = "test   :     19.00 %   =        1.92";

		//when
		final TaxOrDiscount parsedValue = discountValueParser.parseTaxOrDiscount(stringValue);

		//then
		assertThat(parsedValue.getCode()).isEqualTo("test");
		assertThat(parsedValue.getValue()).isEqualTo(19.0);
		assertThat(parsedValue.getAppliedValue()).isEqualTo(1.92);
		assertThat(parsedValue.getCurrencyIsoCode()).isEqualTo("%");
	}

	@Test
	public void parseCurrency() throws ParserException
	{
		//given
		final String stringValue = "test : 19.00 EUR = 1.92";

		//when
		final TaxOrDiscount parsedValue = discountValueParser.parseTaxOrDiscount(stringValue);

		//then
		assertThat(parsedValue.getCode()).isEqualTo("test");
		assertThat(parsedValue.getValue()).isEqualTo(19.0);
		assertThat(parsedValue.getAppliedValue()).isEqualTo(1.92);
		assertThat(parsedValue.getCurrencyIsoCode()).isEqualTo("EUR");
	}

	@Test
	public void parseCurrencyWithoutAppliedValue() throws ParserException
	{
		//given
		final String stringValue = "test : 19.00 EUR";

		//when
		final TaxOrDiscount parsedValue = discountValueParser.parseTaxOrDiscount(stringValue);

		//then
		assertThat(parsedValue.getCode()).isEqualTo("test");
		assertThat(parsedValue.getValue()).isEqualTo(19.0);
		assertThat(parsedValue.getAppliedValue()).isEqualTo(0.0);
		assertThat(parsedValue.getCurrencyIsoCode()).isEqualTo("EUR");
	}

	@Test
	public void shouldZeroAppliedValueWhenNotGiven() throws ParserException
	{
		//given
		final String stringValue = "test : 19.00%";

		//when
		final TaxOrDiscount parsedValue = discountValueParser.parseTaxOrDiscount(stringValue);

		//then
		assertThat(parsedValue.getCode()).isEqualTo("test");
		assertThat(parsedValue.getValue()).isEqualTo(19.0);
		assertThat(parsedValue.getAppliedValue()).isEqualTo(0.0);
		assertThat(parsedValue.getCurrencyIsoCode()).isEqualTo("%");
	}

	@Test(expected = ParserException.class)
	public void shouldFailOnNonExistingCurrency() throws ParserException
	{
		//given
		final String stringValue = "test : 19.00 PLN = 1.92";

		//when
		discountValueParser.parseTaxOrDiscount(stringValue);
	}

	@Test(expected = ParserException.class)
	public void shouldFailOnInvalidFormat() throws ParserException
	{
		//given
		final String stringValue = "test @ 19.00 PLN = 1.92";

		//when
		discountValueParser.parseTaxOrDiscount(stringValue);
	}

	@Test(expected = ParserException.class)
	public void shouldFailOnInvalidNumberFormat() throws ParserException
	{
		//given
		final String stringValue = "test : 19.af PLN = 1.92";

		//when
		discountValueParser.parseTaxOrDiscount(stringValue);
	}
}
