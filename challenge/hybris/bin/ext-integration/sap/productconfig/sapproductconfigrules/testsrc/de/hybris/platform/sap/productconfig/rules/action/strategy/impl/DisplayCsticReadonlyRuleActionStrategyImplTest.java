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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.ConfigurationRulesTestData;
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleUtil;
import de.hybris.platform.sap.productconfig.rules.service.impl.ProductConfigRuleUtilImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DisplayCsticReadonlyRuleActionStrategyImplTest
{

	private DisplayCsticReadonlyRuleActionStrategyImpl classUnderTest;
	private AbstractRuleActionRAO action;
	private ConfigModel model;
	private Map<String, CsticModel> csticMap;
	private CsticModel cstic;
	private ProductConfigRuleUtil ruleUtil;

	@Before
	public void setUp()
	{
		classUnderTest = new DisplayCsticReadonlyRuleActionStrategyImpl();
		ConfigurationRulesTestData.initDependenciesOfActionStrategy(classUnderTest);

		model = ConfigurationRulesTestData.createConfigModelWithCstic();
		ruleUtil = new ProductConfigRuleUtilImpl();
		csticMap = ruleUtil.getCsticMap(model);
		cstic = model.getRootInstance().getCstics().get(0);

		action = new AbstractRuleActionRAO();
		ConfigurationRulesTestData.setCsticAsActionTarget(action, cstic.getName());

		cstic.setReadonly(false);
	}

	@Test
	public void testEsxecuteAction()
	{
		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		assertFalse(configChanged);
		assertTrue(cstic.isReadonly());
	}

	@Test
	public void testEsxecuteAction_alreadyReadOnly()
	{
		cstic.setReadonly(true);
		final boolean configChanged = classUnderTest.executeAction(model, action, csticMap);
		assertFalse(configChanged);
		assertTrue(cstic.isReadonly());
	}

	@Test
	public void testIsActionPossible()
	{
		final boolean actionPossible = classUnderTest.isActionPossible(model, action, csticMap);
		assertTrue(actionPossible);
	}

	@Test
	public void testIsActionPossible_csticDoesNotExist()
	{
		cstic.setName("anotherName");
		csticMap = ruleUtil.getCsticMap(model);
		final boolean actionPossible = classUnderTest.isActionPossible(model, action, csticMap);
		assertFalse(actionPossible);
	}
}
