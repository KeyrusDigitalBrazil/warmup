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
package de.hybris.platform.ruleengine;

import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Utility class that exposes mock rule related creation methods
 */
public class EngineRulesBuilder
{
	private EngineRulesBuilder()
	{

	}

	public static DroolsRuleModel newDroolsRule(final DroolsKIEBaseModel kieBase, final String code)
	{
		final DroolsRuleModel droolsRule = mock(DroolsRuleModel.class);
		when(droolsRule.getKieBase()).thenReturn(kieBase);
		when(droolsRule.getRuleType()).thenReturn(RuleType.DEFAULT);
		when(droolsRule.getCode()).thenReturn(code);
		when(droolsRule.getActive()).thenReturn(Boolean.TRUE);
		when(droolsRule.getCurrentVersion()).thenReturn(Boolean.TRUE);

		return droolsRule;
	}

	public static AbstractRuleEngineRuleModel newAbstractRule(final String code)
	{
		final AbstractRuleEngineRuleModel rule = mock(AbstractRuleEngineRuleModel.class);
		when(rule.getCode()).thenReturn(code);
		when(rule.getRuleType()).thenReturn(RuleType.DEFAULT);
		when(rule.getActive()).thenReturn(Boolean.TRUE);
		when(rule.getCurrentVersion()).thenReturn(Boolean.TRUE);

		return rule;
	}

	public static DroolsKIEBaseModel newKieBase(final String moduleName)
	{
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		final DroolsKIEModuleModel kieModule = mock(DroolsKIEModuleModel.class);
		when(kieModule.getName()).thenReturn(moduleName);
		when(kieBase.getKieModule()).thenReturn(kieModule);

		return kieBase;
	}

}
