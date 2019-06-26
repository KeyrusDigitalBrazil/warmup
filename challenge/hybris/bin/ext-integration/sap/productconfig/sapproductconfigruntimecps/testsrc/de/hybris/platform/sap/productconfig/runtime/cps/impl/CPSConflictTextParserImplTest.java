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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class CPSConflictTextParserImplTest
{
	private static final String EXPLANATION = "&EXPLANATION&";
	private static final String RAW_CONFLICT_TEXT = "Conflict for Color: &EXPLANATION& Object Type 1 cannot be satisfied by the remaining available colors\n\n &DOCUMENTATION& Make your selections in a way so that they are not mutual exclusive.";
	private static final String PARSED_CONFLICT_TEXT = "Conflict for Color:  Object Type 1 cannot be satisfied by the remaining available colors\n\n  Make your selections in a way so that they are not mutual exclusive.";

	private CPSConflictTextParserImpl classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new CPSConflictTextParserImpl();
	}

	@Test
	public void testParseConflictText()
	{
		final String result = classUnderTest.parseConflictText(RAW_CONFLICT_TEXT);
		assertNotNull(result);
		assertFalse(result.contains(EXPLANATION));
		assertFalse(result.contains("&DOCUMENTATION&"));
		assertEquals(PARSED_CONFLICT_TEXT, result);

	}

	@Test
	public void testParseConflictWithNewlines()
	{
		final String result = classUnderTest.parseConflictText("&EXPLANATION&\nThis is an explanation.");
		assertNotNull(result);
		assertFalse(result.contains(EXPLANATION));
		assertEquals("\nThis is an explanation.", result);
	}
}
