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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ZeroPriceModelImplTest
{

	private ZeroPriceModelImpl classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = new ZeroPriceModelImpl();
	}

	@Test
	public void testClone()
	{
		final PriceModel clone = classUnderTest.clone();
		assertSame("clone should always return same instance, as ZeroPriceModelImpl is immutable", classUnderTest, clone);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPrice()
	{
		classUnderTest.setPriceValue(BigDecimal.ONE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetStrikeThroughPrice()
	{
		classUnderTest.setObsoletePriceValue(BigDecimal.ONE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCurrency()
	{
		classUnderTest.setCurrency("EUR");
	}

	@Test
	public void testGetPrice()
	{
		final BigDecimal price = classUnderTest.getPriceValue();
		assertEquals(BigDecimal.ZERO, price);
	}

	@Test
	public void testGetStrikeThroughPrice()
	{
		final BigDecimal price = classUnderTest.getObsoletePriceValue();
		assertEquals(BigDecimal.ZERO, price);
	}

	@Test
	public void testGetCurrency()
	{
		final String currency = classUnderTest.getCurrency();
		assertTrue(currency.isEmpty());
	}

	@Test
	public void testEquals()
	{
		assertEquals(PriceModel.NO_PRICE, PriceModel.PRICE_NA);
	}

	@Test
	public void testHasVaildPriceNoPrice()
	{
		assertFalse(PriceModel.NO_PRICE.hasValidPrice());
	}
}
