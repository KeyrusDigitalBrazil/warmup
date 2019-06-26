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
package de.hybris.platform.ruleengineservices.action.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.GenericTestItemModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengineservices.action.RuleActionStrategy;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * test for {@link DefaultRuleActionService}
 *
 */

@UnitTest
public class DefaultRuleActionServiceTest
{

	private DefaultRuleActionService ruleActionService;

	@Mock
	private RuleActionStrategy ruleActionStrategy;

	@Mock
	private DiscountRAO discountAction;

	@Before
	public void setUp() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);

		final Map<String, RuleActionStrategy> strategyMap = new HashMap<>();
		strategyMap.put("actionRAO", ruleActionStrategy);
		ruleActionService = new DefaultRuleActionService();
		ruleActionService.setActionStrategiesMapping(strategyMap);

		final List<GenericTestItemModel> results = new ArrayList<GenericTestItemModel>();
		results.add(new GenericTestItemModel());
		Mockito.when(ruleActionStrategy.getStrategyId()).thenReturn("testAction");
		Mockito.doReturn(results).when(ruleActionStrategy).apply(discountAction);
		Mockito.doReturn("actionRAO").when(discountAction).getActionStrategyKey();

	}

	@Test
	public void testApplyAllActions()
	{
		final RuleEngineResultRAO ruleEngineResult = new RuleEngineResultRAO();

		ruleEngineResult.setActions(new LinkedHashSet<AbstractRuleActionRAO>(Arrays.asList(discountAction)));
		final List<ItemModel> results = ruleActionService.applyAllActions(ruleEngineResult);
		assertEquals(1, results.size());
	}

}
