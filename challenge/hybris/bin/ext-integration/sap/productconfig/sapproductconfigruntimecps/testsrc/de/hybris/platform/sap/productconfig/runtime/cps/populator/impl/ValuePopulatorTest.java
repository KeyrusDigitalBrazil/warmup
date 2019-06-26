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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class ValuePopulatorTest
{
	ValuePopulator classUnderTest = new ValuePopulator();
	private CPSValue source;
	private CsticValueModel target;
	private static final String valueString = "VALUE";
	private static final String author = "8";
	private CPSValue sourceNumeric;
	private CsticValueModel targetNumeric;
	private static final String numericValue = "5";

	@Before
	public void initialize()
	{
		source = new CPSValue();
		target = new CsticValueModelImpl();
		source.setValue(valueString);
		source.setAuthor(author);

		sourceNumeric = new CPSValue();
		targetNumeric = new CsticValueModelImpl();
		targetNumeric.setNumeric(true);
		sourceNumeric.setValue(numericValue);
	}

	@Test
	public void testPopulatorCoreAttributesAuthorUnknown()
	{
		source.setAuthor("XXXX");
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals("X", target.getAuthor());
		assertEquals("X", target.getAuthorExternal());
	}

	@Test
	public void testPopulatorCoreAttributesAuthorUser()
	{
		source.setAuthor("User");
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(CsticValueModel.AUTHOR_USER, target.getAuthor());
		assertEquals(CsticValueModel.AUTHOR_EXTERNAL_USER, target.getAuthorExternal());
	}

	@Test
	public void testPopulatorCoreAttributesAuthorDefault()
	{
		source.setAuthor("Default");
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(CsticValueModel.AUTHOR_USER, target.getAuthor());
		assertEquals(CsticValueModel.AUTHOR_EXTERNAL_DEFAULT, target.getAuthorExternal());

	}

	@Test
	public void testPopulatorCoreAttributesAuthorSystem()
	{
		source.setAuthor("System");
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(CsticValueModel.AUTHOR_SYSTEM, target.getAuthor());
		assertEquals(CsticModel.AUTHOR_SYSTEM, target.getAuthorExternal());
	}

	@Test
	public void testPopulatorCoreAttributesValueName()
	{
		classUnderTest.populateCoreAttributes(source, target);
		assertEquals(valueString, target.getName());
		assertNull(target.getLanguageDependentName());
	}

	@Test
	public void testPopulatorCoreAttributesNumeric()
	{
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
		assertEquals(String.valueOf(numericValue), targetNumeric.getName());
		assertNull(target.getLanguageDependentName());
	}

	@Test(expected = IllegalStateException.class)
	public void testPopulatorCoreAttributesWithNullValue()
	{
		sourceNumeric.setValue(null);
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
	}

	@Test(expected = IllegalStateException.class)
	public void testPopulatorCoreAttributesWithEmptyValue()
	{
		sourceNumeric.setValue("");
		classUnderTest.populateCoreAttributes(sourceNumeric, targetNumeric);
	}
}
