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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class CPSIntervalTypeTest
{

	@Test
	public void testIsInterval()
	{
		assertFalse("Expected FALSE for empty Interval type, but was TRUE", CPSIntervalType.isInterval(null));
		assertFalse("Expected FALSE for Interval type 0, but was TRUE", CPSIntervalType.isInterval("0"));
		assertFalse("Expected FALSE for Interval type 1, but was TRUE", CPSIntervalType.isInterval("1"));

		for (int i = 2; i <= 9; i++)
		{
			assertTrue("Expected TRUE for Interval type " + i + ", but was FALSE", CPSIntervalType.isInterval("" + i));
		}
	}

	@Test
	public void testFromString()
	{
		assertEquals(CPSIntervalType.UNCONSTRAINED_DOMAIN, CPSIntervalType.fromString("0"));
		assertEquals(CPSIntervalType.SINGLE_VALUE, CPSIntervalType.fromString("1"));
		assertEquals(CPSIntervalType.HALF_OPEN_RIGHT_INTERVAL, CPSIntervalType.fromString("2"));
		assertEquals(CPSIntervalType.CLOSED_INTERVAL, CPSIntervalType.fromString("3"));
		assertEquals(CPSIntervalType.HALF_OPEN_LEFT_INTERVAL, CPSIntervalType.fromString("4"));
		assertEquals(CPSIntervalType.OPEN_INTERVAL, CPSIntervalType.fromString("5"));
		assertEquals(CPSIntervalType.INFINITY_TO_HIGH_OPEN_INTERVAL, CPSIntervalType.fromString("6"));
		assertEquals(CPSIntervalType.INFINITY_TO_HIGH_CLOSED_INTERVAL, CPSIntervalType.fromString("7"));
		assertEquals(CPSIntervalType.LOW_TO_INFINITY_OPEN_INTERVAL, CPSIntervalType.fromString("8"));
		assertEquals(CPSIntervalType.LOW_TO_INFINITY_CLOSED_INTERVAL, CPSIntervalType.fromString("9"));
	}
}
