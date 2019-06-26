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
package de.hybris.platform.ruleengineservices.cache.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.rrd.RuleConfigurationRRD;
import de.hybris.platform.ruleengineservices.rrd.RuleGroupExecutionRRD;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleAddProductPercentageDiscountRAOAction;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleOrderEntryFixedDiscountRAOAction;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleOrderEntryFixedPriceRAOAction;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleOrderEntryPercentageDiscountRAOAction;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleOrderFixedDiscountRAOAction;
import de.hybris.platform.ruleengineservices.rule.evaluation.actions.impl.RuleOrderPercentageDiscountRAOAction;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultCommerceRuleEngineCacheServiceIT extends ServicelayerTest
{

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Resource(name = "defaultCommerceRuleEngineCacheService")
	private DefaultCommerceRuleEngineCacheService cacheService;

	@Resource
	private RulesModuleDao rulesModuleDao;

	@Resource
	private RuleEngineContextDao ruleEngineContextDao;

	@Resource
	private ModelService modelService;

	@Before
	public void setup() throws ImpExException
	{
		importCsv("/ruleengineservices/test/rule/defaultCommerceRuleEngineCacheServiceIT.impex", "utf-8");
	}

	@Test
	public void testAddToCacheWithNullCache()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		cacheService.addToCache(null);
	}

	@Test
	public void testAddToCacheWithWrongCacheType()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		cacheService.addToCache(new KIEModuleCacheBuilder()
		{
			@Override
			public <T extends AbstractRuleEngineRuleModel> void processRule(final T rule)
			{
				// do nothing
			}

		});
	}

	@Test
	public void testCreateKIEModuleCacheWithNullValue()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		cacheService.createKIEModuleCacheBuilder(null);
	}

	@Test
	public void testCreateKIEModuleCacheWithNullPK()
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		cacheService.createKIEModuleCacheBuilder(new DroolsKIEModuleModel());
	}

	@Test
	public void testProvideCachedEntitiesWithOutPriorAddToCache()
	{
		final AbstractRuleEngineContextModel context = ruleEngineContextDao
				.findRuleEngineContextByName("ruleEngineCacheTest-junit-context");

		final RuleEvaluationContext evaluationContext = new RuleEvaluationContext();
		evaluationContext.setRuleEngineContext(context);
		cacheService.provideCachedEntities(evaluationContext);
		// context should not contain any facts
		final Set<Object> facts = evaluationContext.getFacts();
		assertNotNull(facts);
		assertEquals(0, facts.size());
		// context should not contain any globals
		final Map<String, Object> globals = evaluationContext.getGlobals();
		assertNotNull(globals);
		assertEquals(0, globals.size());

	}

	@Test
	public void testProvideCachedEntitiesWithPriorAddToCache() throws ImpExException
	{
		importCsv("/ruleengineservices/test/rule/defaultCommerceRuleEngineCacheServiceIT_testAddToCache.impex", "utf-8");

		modelService.detachAll();
		final AbstractRulesModuleModel abstractModule = rulesModuleDao.findByName("ruleEngineCacheTest-module-junit");
		assertTrue(abstractModule instanceof DroolsKIEModuleModel);
		final DroolsKIEModuleModel module = (DroolsKIEModuleModel) abstractModule;

		final KIEModuleCacheBuilder cache = cacheService.createKIEModuleCacheBuilder(module);
		final Set<DroolsRuleModel> rules = module.getKieBases().stream().findFirst().get().getRules();
		assertEquals(4, rules.size());
		rules.stream().forEach(rule -> cache.processRule(rule));

		cacheService.addToCache(cache);

		final AbstractRuleEngineContextModel context = ruleEngineContextDao
				.findRuleEngineContextByName("ruleEngineCacheTest-junit-context");

		final RuleEvaluationContext evaluationContext = new RuleEvaluationContext();
		evaluationContext.setRuleEngineContext(context);
		cacheService.provideCachedEntities(evaluationContext);

		// context should contain 4 RuleConfigurationRRDs and 3 RuleGroupExectionRRDs
		final Set<Object> facts = evaluationContext.getFacts();
		assertNotNull(facts);
		assertEquals(7, facts.size());
		evaluationContext.getFacts();
		assertTrue(facts.contains(createRuleConfigurationRRD("1111", null)));
		assertTrue(facts.contains(createRuleConfigurationRRD("2222", null)));
		assertTrue(facts.contains(createRuleConfigurationRRD("3333", null)));
		assertTrue(facts.contains(createRuleConfigurationRRD("4444", null)));
		assertTrue(facts.contains(createRuleGroupExecutionRRD("ruleGroup1")));
		assertTrue(facts.contains(createRuleGroupExecutionRRD("ruleGroup2")));
		assertTrue(facts.contains(createRuleGroupExecutionRRD("ruleGroup3")));

		// context should contain 6 globals
		final Map<String, Object> globals = evaluationContext.getGlobals();
		assertNotNull(globals);
		assertEquals(6, globals.size());
		assertTrue(globals.containsKey("ruleOrderEntryFixedPriceRAOAction"));
		assertTrue(globals.containsKey("ruleOrderEntryFixedDiscountRAOAction"));
		assertTrue(globals.containsKey("ruleOrderEntryPercentageDiscountRAOAction"));
		assertTrue(globals.containsKey("ruleOrderPercentageDiscountRAOAction"));
		assertTrue(globals.containsKey("ruleOrderFixedDiscountRAOAction"));
		assertTrue(globals.containsKey("ruleAddProductPercentageDiscountRAOAction"));

		assertTrue(globals.get("ruleOrderEntryFixedPriceRAOAction") instanceof RuleOrderEntryFixedPriceRAOAction);
		assertTrue(globals.get("ruleOrderEntryFixedDiscountRAOAction") instanceof RuleOrderEntryFixedDiscountRAOAction);
		assertTrue(globals.get("ruleOrderEntryPercentageDiscountRAOAction") instanceof RuleOrderEntryPercentageDiscountRAOAction);
		assertTrue(globals.get("ruleOrderPercentageDiscountRAOAction") instanceof RuleOrderPercentageDiscountRAOAction);
		assertTrue(globals.get("ruleOrderFixedDiscountRAOAction") instanceof RuleOrderFixedDiscountRAOAction);
		assertTrue(globals.get("ruleAddProductPercentageDiscountRAOAction") instanceof RuleAddProductPercentageDiscountRAOAction);
	}

	protected RuleConfigurationRRD createRuleConfigurationRRD(final String ruleCode, final String ruleGroupCode)
	{
		final RuleConfigurationRRD rrd = new RuleConfigurationRRD();
		rrd.setCurrentRuns(Integer.valueOf(0));
		rrd.setRuleGroupCode(ruleGroupCode);
		rrd.setRuleCode(ruleCode);
		return rrd;
	}

	protected RuleGroupExecutionRRD createRuleGroupExecutionRRD(final String ruleGroupCode)
	{
		final RuleGroupExecutionRRD rrd = new RuleGroupExecutionRRD();
		rrd.setCode(ruleGroupCode);
		return rrd;
	}
}
