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
package de.hybris.platform.ruleengine.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.ruleengine.EngineRulesBuilder.newDroolsRule;
import static de.hybris.platform.ruleengine.EngineRulesBuilder.newKieBase;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EngineRulePreconditionsUnitTest
{

	private final String MODULE_NAME = "MODULE_NAME";

	@Test
	public void testCheckRulesHaveSameTypeNotSame()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		final DroolsRuleModel rule1 = newDroolsRule(kieBase, "rule1");
		final DroolsRuleModel rule2 = newDroolsRule(kieBase, "rule2");
		when(rule2.getRuleType()).thenReturn(null);

		assertThatThrownBy(() -> EngineRulePreconditions.checkRulesHaveSameType(asList(rule1, rule2)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("One or more rules in the collection are having different rule types");
	}

	@Test
	public void testCheckRulesHaveSameTypeFirstHasNullType()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		final DroolsRuleModel rule1 = newDroolsRule(kieBase, "rule1");
		final DroolsRuleModel rule2 = newDroolsRule(kieBase, "rule2");
		when(rule1.getRuleType()).thenReturn(null);

		assertThatThrownBy(() -> EngineRulePreconditions.checkRulesHaveSameType(asList(rule1, rule2)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("RuleType of engine rule [rule1] is NULL");
	}

	@Test
	public void testCheckRulesHaveSameTypeOK()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		final DroolsRuleModel rule1 = newDroolsRule(kieBase, "rule1");
		final DroolsRuleModel rule2 = newDroolsRule(kieBase, "rule2");

		EngineRulePreconditions.checkRulesHaveSameType(asList(rule1, rule2));
	}


	@Test
	public void testCheckRuleHasKieModuleNoKieBase()
	{
		final DroolsRuleModel rule = newDroolsRule(null, "rule");

		assertThatThrownBy(() -> EngineRulePreconditions.checkRuleHasKieModule(rule))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Rule [rule] has no KieBase assigned to it");
	}

	@Test
	public void testCheckRuleHasKieModuleNoModule()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		when(kieBase.getKieModule()).thenReturn(null);
		final DroolsRuleModel rule = newDroolsRule(kieBase, "rule");

		assertThatThrownBy(() -> EngineRulePreconditions.checkRuleHasKieModule(rule))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Rule [rule] has no KieModule assigned to it");
	}

	@Test
	public void testCheckRuleHasKieModuleOK()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		final DroolsRuleModel rule = newDroolsRule(kieBase, "rule");

		EngineRulePreconditions.checkRuleHasKieModule(rule);
	}
	

}
