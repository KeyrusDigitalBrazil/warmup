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
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


/**
 * Tests
 */
@UnitTest
public class ConflictingAssumptionModelImplTest
{
	ConflictingAssumptionModelImpl classUnderTest = new ConflictingAssumptionModelImpl();
	private static final String text = "A long text";

	@Test
	public void testCsticName()
	{
		final String csticName = "cstic";
		classUnderTest.setCsticName(csticName);
		assertEquals(csticName, classUnderTest.getCsticName());
	}

	@Test
	public void testValueName()
	{
		final String valueName = "value";
		classUnderTest.setValueName(valueName);
		assertEquals(valueName, classUnderTest.getValueName());
	}

	@Test
	public void testInstanceId()
	{
		final String instanceId = "1";
		classUnderTest.setInstanceId(instanceId);
		assertEquals(instanceId, classUnderTest.getInstanceId());
	}

	@Test
	public void testToStringCsticName()
	{

		final String csticName = text;
		classUnderTest.setCsticName(csticName);
		assertTrue("We expect the cstic name printed in toString", classUnderTest.toString().indexOf(csticName) > -1);
	}

	@Test
	public void testToStringValueName()
	{
		final String valueName = text;
		classUnderTest.setValueName(valueName);
		assertTrue("We expect the value name printed in toString", classUnderTest.toString().indexOf(valueName) > -1);
	}

	@Test
	public void testToStringInstId()
	{
		final String instanceId = text;
		classUnderTest.setInstanceId(instanceId);
		assertTrue("We expect the instance Id printed in toString", classUnderTest.toString().indexOf(instanceId) > -1);
	}

	@Test
	public void testAssumptionId()
	{
		final String assumptionId = "1";
		classUnderTest.setId(assumptionId);
		assertEquals("We expect assumption ID", assumptionId, classUnderTest.getId());
	}
}
