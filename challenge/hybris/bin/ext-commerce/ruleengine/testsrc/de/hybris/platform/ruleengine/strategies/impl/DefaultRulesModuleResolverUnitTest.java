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
package de.hybris.platform.ruleengine.strategies.impl;

import static de.hybris.platform.ruleengine.EngineRulesBuilder.newAbstractRule;
import static de.hybris.platform.ruleengine.EngineRulesBuilder.newDroolsRule;
import static de.hybris.platform.ruleengine.EngineRulesBuilder.newKieBase;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.util.EngineRulesRepository;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRulesModuleResolverUnitTest
{
	@InjectMocks
	private DefaultRulesModuleResolver resolver = new DefaultRulesModuleResolver();
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private AbstractRuleModel ruleModel;
	@Mock
	private EngineRulesRepository engineRulesRepository;

	@Test
	public void shouldReturnRulesModulesThatMapToDeployedDroolsRules()
	{
		final DroolsKIEBaseModel kieBase = newKieBase("module1");
		final DroolsRuleModel rule1 = newDroolsRule(kieBase, "rule1");

		final DroolsKIEBaseModel kieBase2 = newKieBase("module2");
		final DroolsRuleModel rule2 = newDroolsRule(kieBase2, "rule2");

		final AbstractRuleEngineRuleModel rule3 = newAbstractRule("rule3");

		when(engineRulesRepository.checkEngineRuleDeployedForModule(rule1, "module1")).thenReturn(true);
		when(engineRulesRepository.checkEngineRuleDeployedForModule(rule2, "module2")).thenReturn(true);


		final DroolsKIEBaseModel kieBase4 = newKieBase("module2");
		final DroolsRuleModel rule4 = newDroolsRule(kieBase4, "rule4");

		when(engineRulesRepository.checkEngineRuleDeployedForModule(rule4, "module2")).thenReturn(false);

		when(ruleModel.getEngineRules()).thenReturn(ImmutableSet.of(rule1, rule2, rule3));

		final List<AbstractRulesModuleModel> result = resolver.lookupForRulesModules(ruleModel);

		assertThat(result).hasSize(2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionOnNullRule()
	{
		resolver.lookupForRulesModules(null);
	}

}
