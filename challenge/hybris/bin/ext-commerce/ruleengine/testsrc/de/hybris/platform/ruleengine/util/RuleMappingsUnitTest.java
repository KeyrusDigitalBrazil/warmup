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
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.hybris.platform.ruleengine.EngineRulesBuilder.newDroolsRule;
import static de.hybris.platform.ruleengine.EngineRulesBuilder.newKieBase;
import static org.assertj.core.api.Assertions.assertThat;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleMappingsUnitTest
{
	private final String MODULE_NAME = "MODULE_NAME";

	@Test
	public void testShouldReturnModuleName()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		final DroolsRuleModel rule = newDroolsRule(kieBase, "rule");

		assertThat(RuleMappings.moduleName(rule)).isEqualTo(MODULE_NAME);
	}

	@Test
	public void testShouldReturnModule()
	{
		final DroolsKIEBaseModel kieBase = newKieBase(MODULE_NAME);
		final DroolsRuleModel rule = newDroolsRule(kieBase, "rule");

		assertThat((DroolsKIEModuleModel) RuleMappings.module(rule)).isEqualTo(kieBase.getKieModule());
	}

	@Test(expected = IllegalStateException.class)
	public void testShouldThrowIllegalStateExceptionOnMissingKiaBase()
	{
		final DroolsRuleModel rule = newDroolsRule(null, "rule");

		RuleMappings.module(rule);
	}
}
