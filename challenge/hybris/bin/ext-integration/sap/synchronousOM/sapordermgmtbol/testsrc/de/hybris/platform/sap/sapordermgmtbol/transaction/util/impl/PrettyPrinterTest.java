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
package de.hybris.platform.sap.sapordermgmtbol.transaction.util.impl;

import de.hybris.platform.sap.sapcommonbol.transaction.util.impl.PrettyPrinter;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class PrettyPrinterTest extends TestCase
{


	private PrettyPrinter classUnderTest;

	@Override
	protected void setUp() throws Exception
	{
		classUnderTest = new PrettyPrinter("MyPrettyPrinter");
	}

	public void testToString()
	{
		assertEquals("MyPrettyPrinter", classUnderTest.toString());
	}

	public void testToAdd()
	{
		classUnderTest.add(classUnderTest, "added String");
		assertEquals("MyPrettyPrinter\nadded String=[MyPrettyPrinter]", classUnderTest.toString());
	}

}
