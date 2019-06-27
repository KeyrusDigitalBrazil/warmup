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
package de.hybris.platform.ruleengine.init.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.util.EngineRulesRepository;
import de.hybris.platform.ruleengine.versioning.ModuleVersionResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultContentMatchRulesFilterUnitTest
{

	@InjectMocks
	private DefaultContentMatchRulesFilter contentMatchRulesFilter;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private ModuleVersionResolver<DroolsKIEModuleModel> moduleVersionResolver;
	@Mock
	private EngineRulesRepository engineRulesRepository;

	@Test
	public void testVerifyRulesModuleIsSameNullList()
	{
		final Optional<DroolsKIEModuleModel> droolsKIEModuleModel = contentMatchRulesFilter.verifyTheRulesModuleIsSame(null);
		assertThat(droolsKIEModuleModel).isNotPresent();
	}

	@Test
	public void testVerifyRulesModuleIsSameEmptyList()
	{
		final Optional<DroolsKIEModuleModel> droolsKIEModuleModel = contentMatchRulesFilter
				.verifyTheRulesModuleIsSame(Lists.emptyList());
		assertThat(droolsKIEModuleModel).isNotPresent();
	}

	@Test
	public void testVerifyRulesModuleIsSameNoKieBase()
	{
		assertThatThrownBy(() -> contentMatchRulesFilter
				.verifyTheRulesModuleIsSame(Collections.singletonList(newEngineRule(null, "engineRule"))))
				.isInstanceOf(IllegalStateException.class).hasMessage("Rule [engineRule] has no KieBase assigned to it");
	}

	@Test
	public void testVerifyRulesModuleIsSameNoKieModule()
	{
		assertThatThrownBy(() -> contentMatchRulesFilter
				.verifyTheRulesModuleIsSame(Collections.singletonList(newEngineRule(newKieBase(null), "engineRule"))))
				.isInstanceOf(IllegalStateException.class).hasMessage("Rule [engineRule] has no KieModule assigned to it");
	}

	@Test
	public void testVerifyRulesModuleIsSameDifferentModules()
	{
		final DroolsKIEBaseModel kieBase1 = newKieBase(newKieModule("MODULE_NAME1"));
		final DroolsKIEBaseModel kieBase2 = newKieBase(newKieModule("MODULE_NAME2"));
		final Collection<DroolsRuleModel> droolsRules = Arrays.asList(newEngineRule(kieBase1, "rule1"),
				newEngineRule(kieBase2, "rule2"));

		assertThatThrownBy(() -> contentMatchRulesFilter.verifyTheRulesModuleIsSame(droolsRules))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("All the rules in the collection should have the same DroolsKIEModuleModel [MODULE_NAME1]");
	}

	@Test
	public void testVerifyRulesModuleIsSameOK()
	{
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final Collection<DroolsRuleModel> droolsRules = Arrays.asList(newEngineRule(kieBase, "rule1"),
				newEngineRule(kieBase, "rule2"));

		final Optional<DroolsKIEModuleModel> droolsKIEModuleModel = contentMatchRulesFilter.verifyTheRulesModuleIsSame(droolsRules);
		assertThat(droolsKIEModuleModel).isPresent();
		assertThat(droolsKIEModuleModel.get()).isEqualTo(module);
	}

	@Test
	public void testApplyNullArg()
	{
		assertThatThrownBy(() -> contentMatchRulesFilter.apply(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The provided rule UUIDs collections shouldn't be NULL or empty");
	}

	@Test
	public void testApplyEmptyUuidCollection()
	{
		assertThatThrownBy(() -> contentMatchRulesFilter.apply(null)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("The provided rule UUIDs collections shouldn't be NULL or empty");
	}

	@Test
	public void testApplyDifferentModules()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2");
		final DroolsKIEBaseModel kieBase1 = newKieBase(newKieModule("MODULE_NAME1"));
		final DroolsKIEBaseModel kieBase2 = newKieBase(newKieModule("MODULE_NAME2"));
		final Collection<DroolsRuleModel> droolsRules = Arrays.asList(newEngineRule(kieBase1, "rule1"),
				newEngineRule(kieBase2, "rule2"));

		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);
		assertThatThrownBy(() -> contentMatchRulesFilter.apply(ruleUuids)).isInstanceOf(IllegalStateException.class)
				.hasMessage("All the rules in the collection should have the same DroolsKIEModuleModel [MODULE_NAME1]");
	}

	@Test
	public void testApplyNoDeployedVersion()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2");
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final Collection<DroolsRuleModel> droolsRules = Arrays.asList(newEngineRule(kieBase, "rule1"),
				newEngineRule(kieBase, "rule2"));

		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenReturn(Optional.empty());
		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);

		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> filteredRules = contentMatchRulesFilter
				.apply(ruleUuids);
		assertThat(filteredRules.getLeft()).isEqualTo(droolsRules);
		assertThat(filteredRules.getRight()).isEmpty();
	}

	@Test
	public void testApplyValidDeployedVersionWrongFormat()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2");
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		when(module.getDeployedMvnVersion()).thenReturn("basic_module_version_1");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final Collection<DroolsRuleModel> droolsRules = Arrays.asList(newEngineRule(kieBase, "rule1"),
				newEngineRule(kieBase, "rule2"));

		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenThrow(IllegalArgumentException.class);
		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);

		assertThatThrownBy(() -> contentMatchRulesFilter.apply(ruleUuids)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testApplyValidDeployedVersionWrongFormatNonNumeric()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2");
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		when(module.getDeployedMvnVersion()).thenReturn("basic_module_version.N");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final Collection<DroolsRuleModel> droolsRules = Arrays.asList(newEngineRule(kieBase, "rule1"),
				newEngineRule(kieBase, "rule2"));

		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenThrow(IllegalArgumentException.class);
		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);

		assertThatThrownBy(() -> contentMatchRulesFilter.apply(ruleUuids)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testApplyOnlyRemove()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2");
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		when(module.getDeployedMvnVersion()).thenReturn("basic_module_version.1");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final DroolsRuleModel rule1 = newEngineRule(kieBase, "rule1");
		final DroolsRuleModel rule2 = newEngineRule(kieBase, "rule2");
		final List<DroolsRuleModel> droolsRules = Arrays.asList(rule1, rule2);

		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenReturn(Optional.of(Long.valueOf(1)));
		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);
		final DroolsRuleModel droolsRuleToRemove = newEngineRule(kieBase, "rule3");
		final List<DroolsRuleModel> deployedDroolsRules = Arrays.asList(rule1,
				rule2, droolsRuleToRemove);
		when(engineRulesRepository.<DroolsRuleModel>getDeployedEngineRulesForModule("MODULE_NAME")).thenReturn(deployedDroolsRules);

		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> filteredRules = contentMatchRulesFilter
				.apply(ruleUuids);
		assertThat(filteredRules.getLeft()).isEmpty();
		assertThat(filteredRules.getRight()).isNotEmpty().hasSize(1).contains(droolsRuleToRemove);
	}

	@Test
	public void testApplyOnlyAdd()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2");
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		when(module.getDeployedMvnVersion()).thenReturn("basic_module_version.1");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final DroolsRuleModel rule3 = newEngineRule(kieBase, "rule3");
		final DroolsRuleModel droolsRuleToAdd = rule3;
		when(droolsRuleToAdd.getVersion()).thenReturn(2L);
		final DroolsRuleModel rule1 = newEngineRule(kieBase, "rule1");
		final DroolsRuleModel rule2 = newEngineRule(kieBase, "rule2");

		final List<DroolsRuleModel> droolsRules = Arrays.asList(rule1, rule2,
				droolsRuleToAdd);

		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenReturn(Optional.of(Long.valueOf(1)));

		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);
		final DroolsRuleModel droolsRuleToRemove = newEngineRule(kieBase, "rule4");
		final Collection<DroolsRuleModel> deployedDroolsRules = Arrays.asList(rule1, rule2, droolsRuleToRemove);
		when(engineRulesRepository.<DroolsRuleModel>getDeployedEngineRulesForModule("MODULE_NAME")).thenReturn(deployedDroolsRules);

		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> filteredRules = contentMatchRulesFilter
				.apply(ruleUuids);
		assertThat(filteredRules.getLeft()).isNotEmpty().hasSize(1).contains(droolsRuleToAdd);
		assertThat(filteredRules.getRight()).isNotEmpty().hasSize(1).contains(droolsRuleToRemove);
	}

	@Test
	public void testApplyOK()
	{
		final Collection<String> ruleUuids = Lists.newArrayList("uuid1", "uuid2", "uuid3");
		final DroolsKIEModuleModel module = newKieModule("MODULE_NAME");
		when(module.getDeployedMvnVersion()).thenReturn("basic_module_version.1");
		final DroolsKIEBaseModel kieBase = newKieBase(module);
		final DroolsRuleModel droolsRuleToAdd = newEngineRule(kieBase, "rule3");
		when(droolsRuleToAdd.getVersion()).thenReturn(2L);
		final DroolsRuleModel rule1 = newEngineRule(kieBase, "rule1");
		final DroolsRuleModel rule2 = newEngineRule(kieBase, "rule2");

		final List<DroolsRuleModel> droolsRules = Arrays.asList(rule1, rule2,
				droolsRuleToAdd);

		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenReturn(Optional.of(Long.valueOf(1)));

		when(engineRuleDao.<DroolsRuleModel> getRulesByUuids(ruleUuids)).thenReturn(droolsRules);

		final List<DroolsRuleModel> deployedDroolsRules = Arrays.asList(rule1, rule2);
		when(engineRulesRepository.<DroolsRuleModel>getDeployedEngineRulesForModule("MODULE_NAME")).thenReturn(deployedDroolsRules);

		final Pair<Collection<DroolsRuleModel>, Collection<DroolsRuleModel>> filteredRules = contentMatchRulesFilter
				.apply(ruleUuids);
		assertThat(filteredRules.getLeft()).isNotEmpty().hasSize(1).contains(droolsRuleToAdd);
		assertThat(filteredRules.getRight()).isEmpty();
	}

	private DroolsRuleModel newEngineRule(final DroolsKIEBaseModel kieBase, final String ruleCode)
	{
		final DroolsRuleModel engineRule = mock(DroolsRuleModel.class);
		when(engineRule.getCode()).thenReturn(ruleCode);
		when(engineRule.getKieBase()).thenReturn(kieBase);
		when(engineRule.getActive()).thenReturn(Boolean.TRUE);
		when(engineRule.getCurrentVersion()).thenReturn(Boolean.TRUE);
		return engineRule;
	}

	private DroolsKIEBaseModel newKieBase(final DroolsKIEModuleModel module)
	{
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		when(kieBase.getKieModule()).thenReturn(module);
		return kieBase;
	}

	private DroolsKIEModuleModel newKieModule(final String moduleName)
	{
		final DroolsKIEModuleModel kieModule = mock(DroolsKIEModuleModel.class);
		when(kieModule.getName()).thenReturn(moduleName);
		return kieModule;
	}

}
