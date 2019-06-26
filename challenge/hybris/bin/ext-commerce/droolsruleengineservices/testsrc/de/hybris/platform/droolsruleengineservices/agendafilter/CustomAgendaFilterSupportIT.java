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
/**
 *
 */
package de.hybris.platform.droolsruleengineservices.agendafilter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.droolsruleengineservices.impl.AbstractRuleEngineServicesTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * CustomAgendaFilterSupportIT tests the support for custom AgendaFilters
 */
@IntegrationTest
public class CustomAgendaFilterSupportIT extends AbstractRuleEngineServicesTest
{

	@Resource
	List<AgendaFilterCreationStrategy> defaultAgendaFilterStrategies;

	@Resource
	AgendaFilterCreationStrategy testCustomAgendaFilterCreationStrategy;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/droolsruleengineservices/test/ruleenginesetup.impex", "utf-8");
		defaultAgendaFilterStrategies.add(testCustomAgendaFilterCreationStrategy);
	}

	@After
	public void tearDown()
	{
		final boolean removed = defaultAgendaFilterStrategies.remove(testCustomAgendaFilterCreationStrategy);
		if (!removed)
		{
			fail("tearDown: removing testCustomAgendaFilterCreationStrategy failed! This might impact other tests as well!");
		}
	}

	@Test
	public void testCustomAgendaFilter() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("customAgendaFilterTest.drl",
				"/droolsruleengineservices/test/rules/evaluation/", "de.hybris.platform.promotionengineservices.test", null);
		initializeRuleEngine(rule1);

		final Map<String, String> mapFact = new HashMap<>();
		final HashSet<Object> facts = new HashSet<>();
		facts.add(mapFact);

		final RuleEvaluationContext context = prepareContext(facts);
		getCommerceRuleEngineService().evaluate(context);

		assertTrue("rule should have added map entry with key 'addedByRule'", mapFact.containsKey("addedByRule"));
		assertTrue("custom agendaFilter should have added map entry with key 'agendaFilter'",
				mapFact.containsKey("addedByAgendaFilter"));

	}

}
