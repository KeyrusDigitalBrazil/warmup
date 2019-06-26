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
package de.hybris.platform.ruleengine.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.rule.Rule;


/**
 * Unit tests for DroolsRuleLoopException
 */
@UnitTest
public class DroolsRuleLoopExceptionTest
{

	private Map<Rule, Long> ruleFirings;

	@Before
	public void setUp()
	{
		ruleFirings = new HashMap();
		for (long counter = 1; counter <= 200; counter++)
		{
			ruleFirings.put(createRule(counter), Long.valueOf(counter));
		}
		// make sure to have some rules with same firing count
		final Rule rule201 = createRule(201);
		final Rule rule202 = createRule(202);
		final Rule rule203 = createRule(203);
		ruleFirings.put(rule201, Long.valueOf(250l));
		ruleFirings.put(rule202, Long.valueOf(250l));
		ruleFirings.put(rule203, Long.valueOf(250l));
	}

	@Test
	public void testDroolsRuleLoopExceptionAllFirings()
	{
		final DroolsRuleLoopException exception = new DroolsRuleLoopException(250, ruleFirings);

		final List<String> allRuleFirings = exception.getAllRuleFirings();
		assertNotNull(allRuleFirings);
		assertEquals("expected 203 entries", 203, allRuleFirings.size());
		assertTrue("first rule should have count 250", allRuleFirings.get(0).startsWith("250:"));
		assertTrue("second rule should have count 250", allRuleFirings.get(1).startsWith("250:"));
		assertTrue("third rule should have count 250", allRuleFirings.get(2).startsWith("250:"));
		assertTrue("fourth rule should have count 200", allRuleFirings.get(3).startsWith("200:"));
	}

	@Test
	public void testDroolsRuleLoopExceptionLimitedFirings()
	{
		final DroolsRuleLoopException exception = new DroolsRuleLoopException(250, ruleFirings);

		final List<String> allRuleFirings = exception.getRuleFirings(10);
		assertNotNull(allRuleFirings);
		assertEquals("expected 10 entries", 10, allRuleFirings.size());
		assertTrue("first rule should have count 250", allRuleFirings.get(0).startsWith("250:"));
		assertTrue("second rule should have count 250", allRuleFirings.get(1).startsWith("250:"));
		assertTrue("third rule should have count 250", allRuleFirings.get(2).startsWith("250:"));
		assertTrue("fourth rule should have count 200", allRuleFirings.get(3).startsWith("200:"));
	}

	@Test
	public void testDroolsRuleLoopExceptionHigherLimitThanFirings()
	{
		final DroolsRuleLoopException exception = new DroolsRuleLoopException(250, ruleFirings);

		final List<String> allRuleFirings = exception.getRuleFirings(1000);
		assertNotNull(allRuleFirings);
		assertEquals("expected 203 entries", 203, allRuleFirings.size());
		assertTrue("first rule should have count 250", allRuleFirings.get(0).startsWith("250:"));
		assertTrue("second rule should have count 250", allRuleFirings.get(1).startsWith("250:"));
		assertTrue("third rule should have count 250", allRuleFirings.get(2).startsWith("250:"));
		assertTrue("fourth rule should have count 200", allRuleFirings.get(3).startsWith("200:"));
	}

	@Test
	public void testDroolsRuleLoopExceptionMessage()
	{
		final DroolsRuleLoopException exception = new DroolsRuleLoopException(345, ruleFirings);

		final String message = exception.getMessage();
		assertNotNull(message);
		assertTrue(message.contains("Current Limit:345"));
		assertEquals(345, exception.getLimit());
	}

	private Rule createRule(final long counter)
	{
		final Rule rule = mock(Rule.class);
		when(rule.getId()).thenReturn(createRuleId(counter));
		return rule;
	}

	private String createRuleId(final long counter)
	{
		return counter + ".TestRule";
	}
}