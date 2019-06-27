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
package de.hybris.platform.ruleengineservices.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.order.calculation.money.Currency;
import de.hybris.platform.ruleengineservices.calculation.AbstractRuleEngineTest;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractOrderRaoToCurrencyConverterTest extends AbstractRuleEngineTest
{
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionOnNullCurrencyIso()
	{
		getAbstractOrderRaoToCurrencyConverter().convert(createCartRAO("cart_code", null));
	}

	@Test
	public void testCartToCurrencyConversionForUSD()
	{
		final CartRAO cart = createCartRAO("cart_code", USD);
		final Currency conversionResult = getAbstractOrderRaoToCurrencyConverter().convert(cart);
		Assert.assertEquals(USD, conversionResult.getIsoCode());
		Assert.assertEquals(2, conversionResult.getDigits());
	}

	@Test
	public void testCartToCurrencyConversionForEUR()
	{
		final CartRAO cart = createCartRAO("cart_code", EUR);
		final Currency conversionResult = getAbstractOrderRaoToCurrencyConverter().convert(cart);
		Assert.assertEquals(EUR, conversionResult.getIsoCode());
		Assert.assertEquals(3, conversionResult.getDigits());
	}
}
