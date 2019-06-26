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
package de.hybris.platform.sap.productconfig.frontend.validator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class MaxLengthValidatorImplTest
{

	private MaxLengthValidatorImpl classUnderTest;
	private CsticData cstic;

	@Before
	public void setUp()
	{
		classUnderTest = new MaxLengthValidatorImpl();
		cstic = new CsticData();
		cstic.setMaxlength(5);
	}

	@Test
	public void testAppliesTo()
	{
		assertTrue(classUnderTest.appliesTo(cstic));
	}

	@Test
	public void testAppliesToValues()
	{
		assertTrue(classUnderTest.appliesToValues());
	}

	@Test
	public void testAppliesToFormattedValues()
	{
		assertTrue(classUnderTest.appliesToFormattedValues());
	}

	@Test
	public void testAppliesToAdditionalValues()
	{
		assertTrue(classUnderTest.appliesToAdditionalValues());
	}


	@Test
	public void testValidateOk()
	{
		final String newValue = classUnderTest.validate(cstic, null, "abcde");
		assertEquals("abcde", newValue);
	}


	@Test
	public void testValidateTooLong()
	{
		final String newValue = classUnderTest.validate(cstic, null, "abcdef");
		assertEquals("abcde", newValue);
	}

	@Test
	public void testAlwaysAcceptNullValue()
	{
		final String newValue = classUnderTest.validate(cstic, null, "NULL_VALUE");
		assertEquals("NULL_VALUE", newValue);
	}

	@Test
	public void testValidateNoMaxValue()
	{
		cstic.setMaxlength(0);
		final String newValue = classUnderTest
				.validate(cstic, null,
						"123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
		assertEquals("123456789012345678901234567890123456789012345678901234567890", newValue);
	}


}
