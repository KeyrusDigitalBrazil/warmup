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
package de.hybris.platform.ruleengine.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.ruleengine.cache.RuleGlobalsBeanProvider;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultKIEModuleCacheBuilderUnitTest
{

	private static final String MODULE_NAME = "MODULE_NAME";

	@Mock
	private DroolsKIEModuleModel kieModule1;

	@Mock
	private DroolsRuleModel rule1;
	@Mock
	private DroolsRuleModel rule2;
	@Mock
	private DroolsKIEBaseModel kieBase1;
	@Mock
	private RuleGlobalsBeanProvider ruleGlobalsBeanProvider;


	private PK rule1Pk;
	private PK rule2Pk;

	private PK kieBase1Pk;
	private PK kieModule1Pk;

	private DefaultKIEModuleCacheBuilder cacheBuilder;

	private final Map<String, String> rule1Globals = new HashMap<>();
	private final Map<String, String> rule2Globals = new HashMap<>();

	@Before
	public void setup()
	{
		rule1Pk = PK.fromLong(1234L);
		rule2Pk = PK.fromLong(2345L);
		kieBase1Pk = PK.fromLong(3456L);
		kieModule1Pk = PK.fromLong(4567L);
		when(rule1.getKieBase()).thenReturn(kieBase1);
		when(rule1.getPk()).thenReturn(rule1Pk);
		when(rule1.getGlobals()).thenReturn(rule1Globals);
		when(rule1.getCode()).thenReturn("rule1");
		addGlobals(3, "rule1-", rule1Globals);
		setupBeanLookups(rule1Globals);

		when(rule2.getKieBase()).thenReturn(kieBase1);
		when(rule2.getPk()).thenReturn(rule2Pk);
		when(rule2.getGlobals()).thenReturn(rule2Globals);
		when(kieBase1.getPk()).thenReturn(kieBase1Pk);
		when(kieModule1.getPk()).thenReturn(kieModule1Pk);
		when(rule2.getCode()).thenReturn("rule2");

		when(kieModule1.getName()).thenReturn(MODULE_NAME);

		when(kieBase1.getKieModule()).thenReturn(kieModule1);

		cacheBuilder = new DefaultKIEModuleCacheBuilder(ruleGlobalsBeanProvider, kieModule1, kb -> kb.getPk().getLongValueAsString(),
				true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCacheKey()
	{
		new DefaultKIEModuleCacheBuilder(ruleGlobalsBeanProvider, null, kb -> kb.getPk().getLongValueAsString(), true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullCacheKeyGenerator()
	{
		new DefaultKIEModuleCacheBuilder(ruleGlobalsBeanProvider, kieModule1, null, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessNullRule()
	{
		cacheBuilder.processRule(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testProcessRuleWithNullKieBase()
	{
		when(rule1.getKieBase()).thenReturn(null);
		cacheBuilder.processRule(rule1);
	}

	@Test
	public void testProcessRule()
	{
		// adding the rules globals to the cache
		cacheBuilder.processRule(rule1);
		final Map<Object, Map<String, Object>> globalsCache = cacheBuilder.getGlobalsCache();
		assertNotNull(globalsCache);
		assertEquals(1, globalsCache.size());
		final Map<String, Object> kieBaseGlobals = cacheBuilder.getCachedGlobalsForKieBase(kieBase1);
		assertEquals(3, kieBaseGlobals.size());
		assertTrue(kieBaseGlobals.containsKey("rule1-1"));
		assertTrue(kieBaseGlobals.containsKey("rule1-2"));
		assertTrue(kieBaseGlobals.containsKey("rule1-3"));
		assertTrue(kieBaseGlobals.containsValue("rule1-1"));
		assertTrue(kieBaseGlobals.containsValue("rule1-2"));
		assertTrue(kieBaseGlobals.containsValue("rule1-3"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProcessRuleWithMismatchingGlobals()
	{
		// adding the rules globals to the cache
		cacheBuilder.processRule(rule1);

		// rule2 has a different type for the same key rule1-1
		rule2Globals.put("rule1-1", "rule2mismatch");
		when(ruleGlobalsBeanProvider.getRuleGlobals("rule2mismatch")).thenReturn(Integer.MIN_VALUE);

		cacheBuilder.processRule(rule2);

	}

	/**
	 * adds {@code count} "global" definitions following the pattern {@code rule}-{@code count}
	 */
	protected void addGlobals(final int count, final String rule, final Map<String, String> globals)
	{
		for (int i = 1; i <= count; i++)
		{
			globals.put(rule + i, rule + i);
		}
	}

	/**
	 * mocks the bean lookup for the globals
	 */
	protected void setupBeanLookups(final Map<String, String> globals)
	{
		globals.forEach((k, v) ->
		{
			when(ruleGlobalsBeanProvider.getRuleGlobals(v)).thenReturn(v);
		});
	}
}
