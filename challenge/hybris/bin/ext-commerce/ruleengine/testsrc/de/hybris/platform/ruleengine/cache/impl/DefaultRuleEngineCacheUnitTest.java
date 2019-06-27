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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.cache.RuleGlobalsBeanProvider;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineCacheUnitTest
{
	private static final String MODULE_NAME = "MODULE_NAME";

	private DefaultRuleEngineCache cache;

	@Mock
	private DroolsKIEModuleModel kieModule;

	@Mock
	private DroolsKIEBaseModel kieBase;

	@Mock
	private DroolsKIESessionModel kieSession;

	@Mock
	private RuleGlobalsBeanProvider ruleGlobalsBeanProvider;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private DroolsRuleModel rule;

	private final Object global1 = new Object();
	private final Object global2 = new Object();
	private final String global1Ref = "global1";
	private final String global2Ref = "global2";
	private final String global1BeanName = "bean1";
	private final String global2BeanName = "bean2";


	private final PK kieModulePk = PK.fromLong(12345L);
	private final PK kieBasePk = PK.fromLong(34567L);
	private final PK kieSessionPk = PK.fromLong(45678L);
	private final PK rulePk = PK.fromLong(23456L);

	@Before
	public void setup()
	{
		cache = new DefaultRuleEngineCache();
		cache.setRuleGlobalsBeanProvider(ruleGlobalsBeanProvider);
		cache.setConfigurationService(configurationService);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(kieModule.getPk()).thenReturn(kieModulePk);
		when(kieBase.getPk()).thenReturn(kieBasePk);
		when(kieBase.getKieModule()).thenReturn(kieModule);
		when(kieSession.getPk()).thenReturn(kieSessionPk);
		when(kieSession.getKieBase()).thenReturn(kieBase);
		when(rule.getPk()).thenReturn(rulePk);
		when(rule.getKieBase()).thenReturn(kieBase);

		final Map<String, String> rule1Globals = new HashMap<>();
		rule1Globals.put(global1Ref, global1BeanName);
		rule1Globals.put(global2Ref, global2BeanName);
		when(rule.getGlobals()).thenReturn(rule1Globals);
		when(ruleGlobalsBeanProvider.getRuleGlobals(global1BeanName)).thenReturn(global1);
		when(ruleGlobalsBeanProvider.getRuleGlobals(global2BeanName)).thenReturn(global2);

		when(kieModule.getName()).thenReturn(MODULE_NAME);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddKIEModuleCacheWithNullCache()
	{
		cache.addKIEModuleCache(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddKIEModuleCacheWithWrongCacheType()
	{
		cache.addKIEModuleCache(new KIEModuleCacheBuilder()
		{

			@Override
			public <T extends AbstractRuleEngineRuleModel> void processRule(final T rule)
			{
				// do nothing
			}

		});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKIEModuleCacheWithNullValue()
	{
		cache.createKIEModuleCacheBuilder(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateKIEModuleCacheWithNullPK()
	{
		when(kieModule.getPk()).thenReturn(null);
		cache.createKIEModuleCacheBuilder(kieModule);
	}

	@Test
	public void testCreateKIEModuleCacheContainer()
	{
		final KIEModuleCacheBuilder cacheBuilder = cache.createKIEModuleCacheBuilder(kieModule);
		assertTrue(cacheBuilder instanceof DefaultKIEModuleCacheBuilder);
	}

	@Test
	public void testAddToCacheAndProvideGlobals()
	{
		final KIEModuleCacheBuilder cacheBuilder = cache.createKIEModuleCacheBuilder(kieModule);
		cacheBuilder.processRule(rule);
		cache.addKIEModuleCache(cacheBuilder);

		final RuleEvaluationContext context = new RuleEvaluationContext();
		final DroolsRuleEngineContextModel engineContext = mock(DroolsRuleEngineContextModel.class);
		context.setRuleEngineContext(engineContext);
		when(engineContext.getKieSession()).thenReturn(kieSession);
		final Map<String, Object> globals = cache.getGlobalsForKIEBase(kieBase);

		assertNotNull(globals);
		assertEquals(2, globals.size());
		assertTrue(globals.containsKey(global1Ref));
		assertTrue(globals.containsKey(global2Ref));
		assertTrue(globals.containsValue(global1));
		assertTrue(globals.containsValue(global2));
	}

}
