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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class PriceModelImplTest
{
	private final PriceModel model = new PriceModelImpl();
	private static final String USD = "USD";

	@Before
	public void setUp()
	{
		model.setCurrency(USD);
		model.setPriceValue(BigDecimal.ONE);
		model.setObsoletePriceValue(BigDecimal.TEN);

	}

	@Test
	public void testGetter()
	{
		assertEquals(USD, model.getCurrency());
		assertEquals(BigDecimal.ONE, model.getPriceValue());
		assertEquals(BigDecimal.TEN, model.getObsoletePriceValue());
	}

	@Test
	public void testEquals()
	{
		final PriceModel anotherModel = new PriceModelImpl();
		anotherModel.setCurrency(USD);
		anotherModel.setPriceValue(BigDecimal.ONE);
		anotherModel.setObsoletePriceValue(BigDecimal.TEN);

		assertEquals(model.hashCode(), anotherModel.hashCode());
		assertEquals(model, anotherModel);
	}

	@Test
	public void testEqualsSame()
	{
		assertEquals(model, model);
	}

	@Test
	public void testEqualsnoData()
	{
		final PriceModel emptyModel = new PriceModelImpl();
		final PriceModel anotherEmptyModel = new PriceModelImpl();

		assertEquals(emptyModel.hashCode(), anotherEmptyModel.hashCode());
		assertEquals(emptyModel, anotherEmptyModel);
	}

	@Test
	public void testNotEqualsCurrency()
	{
		final PriceModel anotherModel = new PriceModelImpl();
		anotherModel.setCurrency("EUR");
		anotherModel.setPriceValue(BigDecimal.ONE);
		anotherModel.setObsoletePriceValue(BigDecimal.TEN);

		assertNotEquals(model.hashCode(), anotherModel.hashCode());
		assertNotEquals(model, anotherModel);
	}

	@Test
	public void testNotEqualsPrice()
	{
		final PriceModel anotherModel = new PriceModelImpl();
		anotherModel.setCurrency(USD);
		anotherModel.setPriceValue(BigDecimal.TEN);
		anotherModel.setObsoletePriceValue(BigDecimal.TEN);

		assertNotEquals(model.hashCode(), anotherModel.hashCode());
		assertNotEquals(model, anotherModel);
	}

	@Test
	public void testNotEqualsStrikeThrough()
	{
		final PriceModel anotherModel = new PriceModelImpl();
		anotherModel.setCurrency(USD);
		anotherModel.setPriceValue(BigDecimal.ONE);
		anotherModel.setObsoletePriceValue(BigDecimal.ONE);

		assertNotEquals(model.hashCode(), anotherModel.hashCode());
		assertNotEquals(model, anotherModel);
	}

	@Test
	public void testNotEqualsNull()
	{
		assertNotEquals(model, null);
	}

	@Test
	public void testToString()
	{
		final String modelString = model.toString();
		assertTrue(modelString, modelString.contains(USD));
		assertTrue(modelString, modelString.contains(BigDecimal.ONE.toString()));
		assertTrue(modelString, modelString.contains(BigDecimal.TEN.toString()));
	}

	@Test
	public void testHasVaildPrice()
	{
		assertTrue(model.hasValidPrice());
	}

	@Test
	public void testHasVaildPriceNoCurrency()
	{
		model.setCurrency(null);
		assertFalse(model.hasValidPrice());
	}

	@Test
	public void testHasVaildPriceNoPrice()
	{
		model.setPriceValue(null);
		assertFalse(model.hasValidPrice());
	}

}
