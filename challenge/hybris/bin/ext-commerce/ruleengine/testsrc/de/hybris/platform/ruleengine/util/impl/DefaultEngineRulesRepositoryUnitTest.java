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
package de.hybris.platform.ruleengine.util.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.versioning.ModuleVersionResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultEngineRulesRepositoryUnitTest
{

	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String RULE_CODE = "RULE_CODE";

	@InjectMocks
	private DefaultEngineRulesRepository engineRulesRepository;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private ModuleVersionResolver<DroolsKIEModuleModel> moduleVersionResolver;

	@Test
	public void testCheckEngineRuleDeployedForModuleNoModule()
	{
		final AbstractRuleEngineRuleModel engineRule = mock(AbstractRuleEngineRuleModel.class);

		final boolean deploymentStatus = engineRulesRepository.checkEngineRuleDeployedForModule(engineRule, MODULE_NAME);
		assertThat(deploymentStatus).isFalse();
		verify(moduleVersionResolver, times(0)).getDeployedModuleVersion(any());
	}

	@Test
	public void testCheckEngineRuleDeployedForModuleNoDeployedModuleVersion()
	{
		final AbstractRuleEngineRuleModel engineRule = mock(AbstractRuleEngineRuleModel.class);
		createDroolsRulesModule(null);

		final boolean deploymentStatus = engineRulesRepository.checkEngineRuleDeployedForModule(engineRule, MODULE_NAME);
		assertThat(deploymentStatus).isFalse();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(0)).getRuleByCodeAndMaxVersion(anyString(), eq(MODULE_NAME), anyLong());
	}

	@Test
	public void testCheckEngineRuleDeployedForModuleNoRuleFound()
	{
		final AbstractRuleEngineRuleModel engineRule = createRuleEngineRule(RULE_CODE, 1L, false);
		createDroolsRulesModule(1L);
		when(engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1L)).thenReturn(null);

		final boolean deploymentStatus = engineRulesRepository.checkEngineRuleDeployedForModule(engineRule, MODULE_NAME);
		assertThat(deploymentStatus).isFalse();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(1)).getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1L);
	}

	@Test
	public void testCheckEngineRuleDeployedForModuleRuleNotActive()
	{
		final AbstractRuleEngineRuleModel engineRule = createRuleEngineRule(RULE_CODE, 1L, false);

		createDroolsRulesModule(1L);
		
		when(engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1L)).thenReturn(engineRule);

		final boolean deploymentStatus = engineRulesRepository.checkEngineRuleDeployedForModule(engineRule, MODULE_NAME);
		assertThat(deploymentStatus).isFalse();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(1)).getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1L);
		verify(engineRule, times(1)).getActive();
		verify(engineRule, times(0)).getVersion();
	}

	@Test
	public void testCheckEngineRuleDeployedForModuleRuleNotEqualTheLatestVersion()
	{
		final AbstractRuleEngineRuleModel engineRule = createRuleEngineRule(RULE_CODE, 2L, true);
		final AbstractRuleEngineRuleModel deployedEngineRule = createRuleEngineRule(RULE_CODE, 5L, true);
		createDroolsRulesModule(5L);
		when(engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 5L)).thenReturn(deployedEngineRule);
		//when
		final boolean deploymentStatus = engineRulesRepository.checkEngineRuleDeployedForModule(engineRule, MODULE_NAME);
		//then
		assertThat(deploymentStatus).isFalse();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(1)).getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 5L);
	}

	@Test
	public void testCheckEngineRuleDeployedForModuleOk()
	{
		final AbstractRuleEngineRuleModel engineRule = createRuleEngineRule(RULE_CODE, 1L, true);

		createDroolsRulesModule(1L);
		
		when(engineRuleDao.getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1L)).thenReturn(engineRule);

		final boolean deploymentStatus = engineRulesRepository.checkEngineRuleDeployedForModule(engineRule, MODULE_NAME);
		assertThat(deploymentStatus).isTrue();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(1)).getRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1L);
		verify(engineRule, times(1)).getActive();
	}

	@Test
	public void testGetDeployedEngineRulesForModuleNoModuleFound()
	{
		final Collection<AbstractRuleEngineRuleModel> deployedRulesForModule = engineRulesRepository
				.getDeployedEngineRulesForModule(MODULE_NAME);
		assertThat(deployedRulesForModule).isEmpty();
		verify(moduleVersionResolver, times(0)).getDeployedModuleVersion(any());
	}

	@Test
	public void testGetDeployedEngineRulesForModuleNoDeployedModuleVersion()
	{
		createDroolsRulesModule(null);

		final Collection<AbstractRuleEngineRuleModel> deployedRulesForModule = engineRulesRepository
				.getDeployedEngineRulesForModule(MODULE_NAME);
		assertThat(deployedRulesForModule).isEmpty();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
	}

	@Test
	public void testGetDeployedEngineRulesForModuleNoRuleFound()
	{
		createRuleEngineRule(RULE_CODE, 1L, false);
		createDroolsRulesModule(1L);

		when(engineRuleDao.getActiveRulesForVersion(MODULE_NAME, 1L)).thenReturn(Collections.emptyList());

		final Collection<AbstractRuleEngineRuleModel> deployedRulesForModule = engineRulesRepository
				.getDeployedEngineRulesForModule(MODULE_NAME);
		assertThat(deployedRulesForModule).isEmpty();
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(1)).getActiveRulesForVersion(MODULE_NAME, 1L);
	}

	@Test
	public void testGetDeployedEngineRulesForModuleOk()
	{
		final AbstractRuleEngineRuleModel engineRule = createRuleEngineRule(RULE_CODE, 1L, false);
		createDroolsRulesModule(1L);

		when(engineRuleDao.getActiveRulesForVersion(MODULE_NAME, 1L)).thenReturn(Collections.singletonList(engineRule));

		final Collection<AbstractRuleEngineRuleModel> deployedRulesForModule = engineRulesRepository
				.getDeployedEngineRulesForModule(MODULE_NAME);
		assertThat(deployedRulesForModule).hasSize(1).contains(engineRule);
		verify(moduleVersionResolver, times(1)).getDeployedModuleVersion(any());
		verify(engineRuleDao, times(1)).getActiveRulesForVersion(MODULE_NAME, 1L);
	}

	private AbstractRuleEngineRuleModel createRuleEngineRule(final String code, final long version, final boolean active)
	{
		final AbstractRuleEngineRuleModel engineRule = mock(AbstractRuleEngineRuleModel.class);
		when(engineRule.getCode()).thenReturn(code);
		when(engineRule.getVersion()).thenReturn(version);
		when(engineRule.getActive()).thenReturn(active);
		return engineRule;
	}

	private DroolsKIEModuleModel createDroolsRulesModule(final Long version)
	{
		final DroolsKIEModuleModel module = mock(DroolsKIEModuleModel.class);
		when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(module);
		when(moduleVersionResolver.getDeployedModuleVersion(module)).thenReturn(Optional.ofNullable(version));
		return module;
	}

}
