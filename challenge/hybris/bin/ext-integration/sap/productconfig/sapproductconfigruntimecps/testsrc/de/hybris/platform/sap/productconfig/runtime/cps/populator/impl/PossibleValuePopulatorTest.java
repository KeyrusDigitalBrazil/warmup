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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class PossibleValuePopulatorTest
{
	PossibleValuePopulator classUnderTest = new PossibleValuePopulator();
	private CPSPossibleValue source;
	private CsticValueModel target;
	private static final String valueString = "VALUE";
	private CPSPossibleValue sourceNumeric;
	private CsticValueModel targetNumeric;
	private static final String numericValue = "5";
	private static final String numericValueHigh = "10";


	@Before
	public void initialize()
	{
		source = new CPSPossibleValue();
		target = new CsticValueModelImpl();
		source.setValueLow(valueString);
		source.setSelectable(true);

		sourceNumeric = new CPSPossibleValue();
		targetNumeric = new CsticValueModelImpl();
		targetNumeric.setNumeric(true);
		sourceNumeric.setValueLow(numericValue);

	}

	@Test
	public void testPopulatorCoreAttributesSelectable()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue(target.isSelectable());
	}

	@Test
	public void testPopulatorCoreAttributesValueNameWithNullValueHigh()
	{
		source.setValueHigh(null);
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(valueString, target.getName());
	}

	@Test
	public void testPopulatorCoreAttributesValueNameWithEmptyValueHigh()
	{
		source.setValueHigh("");
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(valueString, target.getName());
	}

	@Test
	public void testPopulatorCoreAttributesDomainValue()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertTrue(target.isDomainValue());
	}

	@Test
	public void testPopulatorCoreAttributesValues()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(source.getValueLow(), target.getName());
		assertNull(target.getLanguageDependentName());
	}

	@Test
	public void testPopulateNumericalAttributesInterval()
	{
		sourceNumeric.setIntervalType("3");
		sourceNumeric.setValueHigh(numericValueHigh);
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
		final String result = targetNumeric.getName();
		final String[] boundaries = result.split("-");
		assertEquals(2, boundaries.length);
		assertEquals(numericValue, boundaries[0].trim());
		assertEquals(numericValueHigh, boundaries[1].trim());
		assertEquals(result, targetNumeric.getLanguageDependentName());
	}

	@Test
	public void testPopulateInterval()
	{
		for (int i = 2; i <= 5; i++)
		{
			sourceNumeric.setIntervalType("" + i);
			sourceNumeric.setValueHigh(numericValueHigh);
			classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
			final String result = targetNumeric.getName();
			final String[] boundaries = result.split("-");
			assertEquals(2, boundaries.length);
			assertEquals(numericValue, boundaries[0].trim());
			assertEquals(numericValueHigh, boundaries[1].trim());
			assertEquals(result, targetNumeric.getLanguageDependentName());
		}
	}

	@Test
	public void testIntervalInfinityToBOpen()
	{
		sourceNumeric.setIntervalType("6");
		sourceNumeric.setValueHigh(numericValueHigh);
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
		final String result = targetNumeric.getName();
		assertEquals("< " + numericValueHigh, result);
		assertEquals(result, targetNumeric.getLanguageDependentName());
	}

	@Test
	public void testIntervalInfinityToBClosed()
	{
		sourceNumeric.setIntervalType("7");
		sourceNumeric.setValueHigh(numericValueHigh);
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
		final String result = targetNumeric.getName();
		assertEquals("≤ " + numericValueHigh, result);
		assertEquals(result, targetNumeric.getLanguageDependentName());
	}

	@Test
	public void testIntervalAOpenToInfinity()
	{
		sourceNumeric.setIntervalType("8");
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
		final String result = targetNumeric.getName();
		assertEquals("> " + numericValue, result);
		assertEquals(result, targetNumeric.getLanguageDependentName());
	}

	@Test
	public void testIntervalAClosedToInfinity()
	{
		sourceNumeric.setIntervalType("9");
		sourceNumeric.setValueHigh(numericValueHigh);
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
		final String result = targetNumeric.getName();
		assertEquals("≥ " + numericValue, result);
		assertEquals(result, targetNumeric.getLanguageDependentName());
	}

}
