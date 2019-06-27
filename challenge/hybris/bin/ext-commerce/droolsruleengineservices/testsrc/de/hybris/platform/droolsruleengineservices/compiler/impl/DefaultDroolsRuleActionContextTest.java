/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.droolsruleengineservices.compiler.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;


@UnitTest
public class DefaultDroolsRuleActionContextTest extends AbstractGeneratorTest
{
	@Test
	public void testGetAbsentValue() throws IOException
	{
		final HashMap<String, Object> variables = new HashMap<String, Object>();
		final String stringValue = "a string";
		final Object nullObject = null;
		variables.put(String.class.getName(), stringValue);
		variables.put(Object.class.getName(), nullObject);
		final DefaultDroolsRuleActionContext droolsRuleActionContext = new DefaultDroolsRuleActionContext(variables, null);

		assertEquals(stringValue, droolsRuleActionContext.getValue(String.class));
		assertEquals(nullObject, droolsRuleActionContext.getValue(Object.class));
		assertTrue(droolsRuleActionContext.getValues(Object.class).isEmpty());
	}
}
