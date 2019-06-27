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
package de.hybris.platform.droolsruleengineservices.impl;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.event.RuleEngineInitializedEvent;
import de.hybris.platform.ruleengine.event.RuleEngineModuleSwapCompletedEvent;
import de.hybris.platform.ruleengine.exception.DroolsRuleLoopException;
import de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.servicelayer.event.EventService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.SingletonMap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@IntegrationTest
public class DefaultDroolsEngineServiceTest extends AbstractRuleEngineServicesTest
{
	@Resource
	private EventService eventService;

	@Mock
	private EventService mockedEventService;

	@Before
	public void setUp() throws ImpExException
	{
		init();
		importCsv("/droolsruleengineservices/test/ruleenginesetup.impex", "utf-8");
		MockitoAnnotations.initMocks(this);
		if (getPlatformRuleEngineService() instanceof DefaultPlatformRuleEngineService)
		{
			((DefaultPlatformRuleEngineService) getPlatformRuleEngineService()).setEventService(mockedEventService);
		}
	}

	@After
	public void restore()
	{
		if (getPlatformRuleEngineService() instanceof DefaultPlatformRuleEngineService)
		{
			((DefaultPlatformRuleEngineService) getPlatformRuleEngineService()).setEventService(eventService);
		}
	}

	@Test
	public void testRuleEngineInitializedEvent() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("raoRule01.drl", "/droolsruleengineservices/test/rules/rao/",
				"de.hybris.platform.ruleengineservices.test",
				new SingletonMap("ruleOrderFixedDiscountAction", "ruleOrderFixedDiscountAction"));
		final DroolsKIEModuleModel testRulesModule = getTestRulesModule(Collections.singleton(rule1));
		getPlatformRuleEngineService().initialize(singletonList(testRulesModule), false, false).waitForInitializationToFinish();
		final Matcher<RuleEngineInitializedEvent> matcher = new BaseMatcher()
		{

			@Override
			public boolean matches(final Object compareTo)
			{
				if (compareTo instanceof RuleEngineModuleSwapCompletedEvent)
				{
					final RuleEngineModuleSwapCompletedEvent event = (RuleEngineModuleSwapCompletedEvent) compareTo;

					return event.getRulesModuleName().equals(testRulesModule.getName());
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText(
						String.format("RulesModuleName of RuleEngineInitializedEvent should be '%s'", testRulesModule.getName()));

			}
		};

		Mockito.verify(mockedEventService).publishEvent(Mockito.argThat(matcher));
	}

	@Test
	public void testRuleInitializationError() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("defective-raoRule01.drl", "/droolsruleengineservices/test/rules/rao/",
				"de.hybris.platform.ruleengineservices.test", null);
		final List<RuleEngineActionResult> results = getPlatformRuleEngineService()
				.initialize(singletonList(getTestRulesModule(Collections.singleton(rule1))), false, false).waitForInitializationToFinish().getResults();
		if(CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		Assert.assertTrue(result.isActionFailed());
	}


	@Test
	public void testSingleRuleLoopDetection() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("looping-rule01.drl", "/droolsruleengineservices/test/rules/loopdetection/",
				"de.hybris.platform.ruleengineservices.test", null);
		initializeRuleEngine(rule1);
		final RuleEvaluationContext context = prepareContext(Collections.singleton(buildRAOsForCartWithCode("ABC")));
		// make sure ruleFiringLimit is set before running the test
		assertNotNull(context.getRuleEngineContext());
		assertTrue(context.getRuleEngineContext() instanceof DroolsRuleEngineContextModel);
		final DroolsRuleEngineContextModel droolsContext = (DroolsRuleEngineContextModel) context.getRuleEngineContext();
		droolsContext.getKieSession().getKieBase();
		droolsContext.setRuleFiringLimit(Long.valueOf(200L));
		getModelService().save(droolsContext);

		try
		{
			getCommerceRuleEngineService().evaluate(context);
			fail("should have thrown a DroolsRuleLoopException");
		}
		catch (final DroolsRuleLoopException e)
		{
			final List<String> allRuleFirings = e.getAllRuleFirings();
			assertEquals(200L, e.getLimit());
			assertEquals(1, allRuleFirings.size());
		}
	}

	@Test
	public void testTwoRulesLoopDetection() throws IOException
	{
		final DroolsRuleModel rule2 = getRuleForFile("looping-rule02.drl", "/droolsruleengineservices/test/rules/loopdetection/",
				"de.hybris.platform.ruleengineservices.test", null);
		final DroolsRuleModel rule3 = getRuleForFile("looping-rule03.drl", "/droolsruleengineservices/test/rules/loopdetection/",
				"de.hybris.platform.ruleengineservices.test", null);
		initializeRuleEngine(rule2, rule3);
		final RuleEvaluationContext context = prepareContext(Collections.singleton(buildRAOsForCartWithCode("ABC")));
		// make sure ruleFiringLimit is set before running the test
		assertNotNull(context.getRuleEngineContext());
		assertTrue(context.getRuleEngineContext() instanceof DroolsRuleEngineContextModel);
		final DroolsRuleEngineContextModel droolsContext = (DroolsRuleEngineContextModel) context.getRuleEngineContext();
		droolsContext.getKieSession().getKieBase();
		droolsContext.setRuleFiringLimit(Long.valueOf(100L));
		getModelService().save(droolsContext);

		try
		{
			getCommerceRuleEngineService().evaluate(context);
			fail("should have thrown a DroolsRuleLoopException");
		}
		catch (final DroolsRuleLoopException e)
		{
			final List<String> allRuleFirings = e.getAllRuleFirings();
			assertEquals(100L, e.getLimit());
			assertEquals(2, allRuleFirings.size());
		}
	}

	@Test
	public void testRuleEvaluation() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("raoRule01.drl", "/droolsruleengineservices/test/rules/rao/",
				"de.hybris.platform.ruleengineservices.test",
				new SingletonMap("ruleOrderFixedDiscountAction", "ruleOrderFixedDiscountAction"));
		final DroolsRuleModel rule2 = getRuleForFile("raoRule02.drl", "/droolsruleengineservices/test/rules/rao/",
				"de.hybris.platform.ruleengineservices.test",
				new SingletonMap("ruleOrderFixedDiscountAction", "ruleOrderFixedDiscountAction"));
		initializeRuleEngine(rule1, rule2);

		doEvaluationAndAssertion("XYZ", 20L);
		doEvaluationAndAssertion("ABC", 10L);
	}

	private void doEvaluationAndAssertion(final String cartCode, final long expectedDiscount)
	{
		final RuleEvaluationContext context = prepareContext(buildRAOsForNotEmptyCartWithCode(cartCode));

		final RuleEvaluationResult result = getCommerceRuleEngineService().evaluate(context);
		final RuleEngineResultRAO resultRAO = result.getResult();
		Assert.assertNotNull(resultRAO);
		Assert.assertEquals("should have one action", 1, resultRAO.getActions().size());
		final AbstractRuleActionRAO resultAction = resultRAO.getActions().iterator().next();
		Assert.assertTrue("should be DiscountRAO", resultAction instanceof DiscountRAO);
		final DiscountRAO discount = (DiscountRAO) resultAction;
		Assert.assertEquals(0, BigDecimal.valueOf(expectedDiscount).compareTo(discount.getValue()));
	}
}
