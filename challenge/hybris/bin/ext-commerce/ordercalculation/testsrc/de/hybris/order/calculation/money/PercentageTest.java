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
package de.hybris.order.calculation.money;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;

import java.math.BigDecimal;

import org.junit.Test;


@UnitTest
public class PercentageTest
{
	@Test(expected = IllegalArgumentException.class)
	public void testNullParam()
	{
		new Percentage((String) null);
	}

	@Test
	public void testNegativeAndZeroValue()
	{
		final Percentage val1 = new Percentage(-10);
		final Percentage val3 = new Percentage(new BigDecimal("-35"));
		assertEquals(Percentage.TWENTYFIVE, val1.subtract(val3));

		final Percentage val2 = new Percentage(new BigDecimal("0.0000000"));
		assertEquals(new Percentage(BigDecimal.ZERO), val2);
	}

	@Test
	public void testComplexAdd()
	{
		final Percentage val1 = new Percentage(0);
		final Percentage val3 = new Percentage(new BigDecimal("35.333"));

		final Percentage result = val1.add(Percentage.TEN).add(val3);
		assertEquals(new Percentage(new BigDecimal("45.333")), result);
		assertEquals("45.333%", result.toString());

	}

	@Test
	public void testAdd()
	{
		final Percentage add1 = new Percentage(new BigDecimal(2));
		final Percentage add2 = new Percentage(new BigDecimal(3));
		final Percentage res = new Percentage(new BigDecimal(5));

		assertEquals(res, add1.add(add2));
	}

	@Test
	public void testSubtract()
	{
		final Percentage add1 = new Percentage(new BigDecimal(5));
		final Percentage add2 = new Percentage(new BigDecimal(2));
		final Percentage res = new Percentage(new BigDecimal(3));

		assertEquals(res, add1.subtract(add2));
	}

	@Test
	public void testEquality()
	{
		final Percentage value1 = new Percentage(0);
		final Percentage value2 = new Percentage(BigDecimal.ZERO);
		final Percentage value3 = new Percentage(new BigDecimal("0.0000000000"));

		assertEquals(value1, value2);
		assertEquals(value1, value3);
		assertEquals(value2, value3);
		assertEquals(value2, value1);
		assertEquals(value3, value1);
		assertEquals(value3, value2);

		final Percentage val3 = new Percentage(new BigDecimal("1.000000000000001"));
		final Percentage val4 = new Percentage(new BigDecimal("1.0000000000000001"));
		final Percentage val5 = new Percentage(new BigDecimal("0.0000000000000001"));

		assertFalse(val3.equals(val4));
		assertFalse(val4.equals(val5));
		assertFalse(val3.equals(val5));
		assertFalse(val5.equals(val3));
		assertFalse(val5.equals(val4));
		assertFalse(val4.equals(val3));

		assertFalse(val5.equals(value3));
		assertFalse(val5.equals(value2));
		assertFalse(val5.equals(value1));

		assertFalse(val5.equals(new Object()));
	}

	@Test
	public void testHastCode()
	{
		final Percentage value1 = new Percentage(0);
		final Percentage value2 = new Percentage(BigDecimal.ZERO);
		assertEquals(value1.hashCode(), value2.hashCode());
	}
}
