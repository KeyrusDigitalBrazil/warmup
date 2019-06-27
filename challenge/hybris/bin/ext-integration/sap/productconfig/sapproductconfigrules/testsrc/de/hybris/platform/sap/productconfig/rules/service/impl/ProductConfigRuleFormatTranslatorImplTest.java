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
package de.hybris.platform.sap.productconfig.rules.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigRuleFormatTranslatorImplTest
{


	private ProductConfigRuleFormatTranslatorImpl classUnderTest;
	private CsticModel cstic;

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigRuleFormatTranslatorImpl();
		cstic = new CsticModelImpl();
	}

	@Test
	public void testIsNumericFalse()
	{
		cstic.setValueType(CsticModel.TYPE_BOOLEAN);
		final boolean isNumeric = classUnderTest.isNumericValue(cstic);
		assertFalse("a boolean cstic is not numeric", isNumeric);
	}

	@Test
	public void testIsNumericButDomainConstrained()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		cstic.setConstrained(true);
		final boolean isNumeric = classUnderTest.isNumericValue(cstic);
		assertFalse("domain constrained cstic should never be considered numeric", isNumeric);
	}

	@Test
	public void testIsNumericIntegerTrue()
	{
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		final boolean isNumeric = classUnderTest.isNumericValue(cstic);
		assertTrue("a integer cstic is  numeric", isNumeric);
	}

	@Test
	public void testIsNumericFloatTrue()
	{

		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final boolean isNumeric = classUnderTest.isNumericValue(cstic);
		assertTrue("a float cstic is  numeric", isNumeric);
	}

	@Test
	public void testFormatForRulesNoValues()
	{
		final String value = classUnderTest.formatForRules(cstic, null);
		assertEquals(classUnderTest.getNoValueIndicator(), value);
	}

	@Test
	public void testFormatForRulesNumeric()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final String value = classUnderTest.formatForRules(cstic, "1500.0");
		assertEquals("1,500", value);
	}

	@Test
	public void testFormatForRulesNumericErr()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final String value = classUnderTest.formatForRules(cstic, "aaa");
		assertEquals("aaa", value);
	}

	@Test
	public void testFormatForRulesString()
	{
		cstic.setValueType(CsticModel.TYPE_STRING);
		final String value = classUnderTest.formatForRules(cstic, "1500.0");
		assertEquals("1500.0", value);
	}


	@Test
	public void testFormatForServiceNumeric()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final String value = classUnderTest.formatForService(cstic, "1,500");
		assertEquals("1500.0", value);
	}

	@Test
	public void testFormatForServiceNumericErr()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final String value = classUnderTest.formatForService(cstic, "aaa");
		assertEquals("aaa", value);
	}

	@Test
	public void testFormatForServiceString()
	{
		cstic.setValueType(CsticModel.TYPE_STRING);
		final String value = classUnderTest.formatForService(cstic, "1500.0");
		assertEquals("1500.0", value);
	}


	@Test
	public void testFormatableForServiceNumeric()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final boolean formatable = classUnderTest.canBeFormattedForService(cstic, "1,500");
		assertTrue(formatable);
	}

	@Test
	public void testFormatableForServiceNumericErr()
	{
		cstic.setValueType(CsticModel.TYPE_FLOAT);
		final boolean formatable = classUnderTest.canBeFormattedForService(cstic, "aaa");
		assertFalse(formatable);
	}

	@Test
	public void testFormatableForServiceString()
	{
		cstic.setValueType(CsticModel.TYPE_STRING);
		final boolean formatable = classUnderTest.canBeFormattedForService(cstic, "aaa");
		assertTrue(formatable);
	}

	@Test
	public void testFormatableForServiceNumericEmpty()
	{
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		final boolean formatable = classUnderTest.canBeFormattedForService(cstic, "");
		assertTrue(formatable);
	}

	@Test
	public void testFormatableForServiceNumericNull()
	{
		cstic.setValueType(CsticModel.TYPE_INTEGER);
		final boolean formatable = classUnderTest.canBeFormattedForService(cstic, null);
		assertTrue(formatable);
	}


}
