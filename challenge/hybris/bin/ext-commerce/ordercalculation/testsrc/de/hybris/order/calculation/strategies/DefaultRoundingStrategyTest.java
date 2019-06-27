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
package de.hybris.order.calculation.strategies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import de.hybris.order.calculation.money.Currency;
import de.hybris.order.calculation.money.Money;
import de.hybris.order.calculation.money.Percentage;
import de.hybris.order.calculation.strategies.impl.DefaultRoundingStrategy;

import java.math.BigDecimal;

import org.junit.Test;


/**
 *
 */
public class DefaultRoundingStrategyTest
{
	private final RoundingStrategy roundingStrategy = new DefaultRoundingStrategy();
	private final Currency euro = new Currency("EUR", 2);
	private final Currency fourDigits = new Currency("fourDigits", 4);

	@Test
	public void testMultiply()
	{
		final Money result1 = roundingStrategy.multiply(new Money(BigDecimal.ONE, euro), BigDecimal.ONE);
		assertEquals(new Money(BigDecimal.ONE, euro), result1);
		assertNotSame(new Money(BigDecimal.ONE, fourDigits), result1);

		final Money result2 = roundingStrategy.multiply(new Money(BigDecimal.ONE, euro), new BigDecimal("0.5"));
		assertEquals(new Money(new BigDecimal("0.5"), euro), result2);

		final Money result3 = roundingStrategy.multiply(new Money(BigDecimal.ONE, euro), new BigDecimal("0.333333333333"));
		assertEquals(new Money(new BigDecimal("0.33"), euro), result3);
		final Money result4 = roundingStrategy.multiply(new Money(BigDecimal.ONE, fourDigits), new BigDecimal("0.333333333333"));
		assertEquals(new Money(new BigDecimal("0.3333"), fourDigits), result4);

		final Money result5 = roundingStrategy.multiply(new Money("23.34", euro), new BigDecimal("1.00"));
		assertEquals(new Money(new BigDecimal("23.34"), euro), result5);

	}

	@Test
	public void testDividide()
	{
		final Money oneEuro = new Money(BigDecimal.ONE, euro);
		final Money twoEuro = new Money(BigDecimal.valueOf(2), euro);
		assertEquals(new Money("0.33", euro), roundingStrategy.divide(oneEuro, BigDecimal.valueOf(3)));
		assertEquals(new Money("0.50", euro), roundingStrategy.divide(oneEuro, BigDecimal.valueOf(2)));
		assertEquals(oneEuro, roundingStrategy.divide(oneEuro, BigDecimal.ONE));
		assertEquals(new Money("0.67", euro), roundingStrategy.divide(twoEuro, BigDecimal.valueOf(3)));
	}

	@Test
	public void testPercentageValue()
	{
		final Money oneEuro = new Money(BigDecimal.ONE, euro);
		assertEquals(new Money("0.33", euro), roundingStrategy.getPercentValue(oneEuro, new Percentage(33)));
		assertEquals(new Money("0.66", euro), roundingStrategy.getPercentValue(oneEuro, new Percentage(66)));
		assertEquals(new Money("0.67", euro),
				roundingStrategy.getPercentValue(oneEuro, new Percentage(new BigDecimal("66.666666666"))));
		assertEquals(new Money("0.99", euro), roundingStrategy.getPercentValue(oneEuro, new Percentage(99)));
		assertEquals(new Money("0.99", euro), roundingStrategy.getPercentValue(oneEuro, new Percentage(new BigDecimal("99.1"))));
		assertEquals(oneEuro, roundingStrategy.getPercentValue(oneEuro, new Percentage(new BigDecimal("99.6"))));
		assertEquals(new Money("0", euro), roundingStrategy.getPercentValue(oneEuro, new Percentage(0)));
	}
}
