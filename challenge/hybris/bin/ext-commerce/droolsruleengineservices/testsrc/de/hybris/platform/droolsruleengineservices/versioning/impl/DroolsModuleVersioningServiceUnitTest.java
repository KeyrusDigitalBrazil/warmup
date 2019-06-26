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
package de.hybris.platform.droolsruleengineservices.versioning.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.versioning.impl.DroolsKieModuleVersionResolver;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DroolsModuleVersioningServiceUnitTest
{
	private static final String RULE_CODE = "RULE_CODE";
	private static final String MODULE_NAME = "MODULE_NAME";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private DroolsModuleVersioningService moduleVersioningService;
	@Mock
	private RuleEngineService ruleEngineService;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private ModelService modelService;
	@Mock
	private DroolsRuleModel rule;
	@Mock
	private DroolsKIEBaseModel kieBase;
	@Mock
	private DroolsKIEModuleModel kieModule;

	@InjectMocks
	private DroolsKieModuleVersionResolver moduleVersionResolver;

	@Before
	public void setUp()
	{
		when(engineRuleDao.getActiveRuleByCodeAndMaxVersion(RULE_CODE, MODULE_NAME, 1)).thenReturn(rule);
		when(engineRuleDao.getRuleByCode(RULE_CODE, MODULE_NAME)).thenReturn(rule);
		when(rule.getKieBase()).thenReturn(kieBase);
		when(kieBase.getKieModule()).thenReturn(kieModule);
		when(kieModule.getName()).thenReturn(MODULE_NAME);

		moduleVersioningService.setModuleVersionResolver(moduleVersionResolver);
	}

	@Test
	public void testGetDeployedModuleVersionOk() throws Exception
	{
		when(kieModule.getDeployedMvnVersion()).thenReturn("1.2.3.4");
		final Optional<Long> deployedModuleVersion =  moduleVersioningService.getDeployedModuleVersionForRule(RULE_CODE, MODULE_NAME);
		assertThat(deployedModuleVersion).isNotNull();
		assertThat(deployedModuleVersion.isPresent()).isTrue();
		assertThat(deployedModuleVersion.get()).isEqualTo(4);
	}

	@Test
	public void testGetDeployedModuleVersionIllegalVersion() throws Exception
	{
		when(kieModule.getDeployedMvnVersion()).thenReturn("1.2.3.a");
		expectedException.expectMessage("Error during the deployed version of module");
		moduleVersioningService.getDeployedModuleVersionForRule(RULE_CODE, MODULE_NAME);
	}

	@Test
	public void testGetDeployedModuleVersionNoKieModule() throws Exception
	{
		when(kieBase.getKieModule()).thenReturn(null);
		final Optional<Long> deployedModuleVersion =  moduleVersioningService.getDeployedModuleVersionForRule(RULE_CODE, MODULE_NAME);
		assertThat(deployedModuleVersion.isPresent()).isFalse();
	}

	@Test
	public void testGetDeployedModuleVersionNoKieBase() throws Exception
	{
		when(rule.getKieBase()).thenReturn(null);
		final Optional<Long> deployedModuleVersion =  moduleVersioningService.getDeployedModuleVersionForRule(RULE_CODE, MODULE_NAME);
		assertThat(deployedModuleVersion.isPresent()).isFalse();
	}

	@Test
	public void testGetModuleVersion() throws Exception
	{
		when(kieModule.getDeployedMvnVersion()).thenReturn("1.2.3.4");
		final Optional<Long> moduleVersion = moduleVersioningService.getModuleVersion(rule);
		assertThat(moduleVersion).isNotNull();
		assertThat(moduleVersion.isPresent()).isTrue();
		assertThat(moduleVersion.get()).isEqualTo(4);
	}

	@Test
	public void testGetModuleVersionInsupportedType() throws Exception
	{
		final Optional<Long> moduleVersion = moduleVersioningService.getModuleVersion(new AbstractRuleEngineRuleModel());
		assertThat(moduleVersion).isNotNull();
		assertThat(moduleVersion.isPresent()).isFalse();
	}

	@Test
	public void testGetModuleVersionKieBaseNotSet() throws Exception
	{
		when(kieModule.getDeployedMvnVersion()).thenReturn("1.2.3.4");
		when(rule.getKieBase()).thenReturn(null);
		final Optional<Long> moduleVersion = moduleVersioningService.getModuleVersion(rule);
		assertThat(moduleVersion).isNotNull();
		assertThat(moduleVersion.isPresent()).isFalse();
	}

	@Test
	public void testGetModuleVersionKieModuleNotSet() throws Exception
	{
		when(kieBase.getKieModule()).thenReturn(null);
		final Optional<Long> moduleVersion = moduleVersioningService.getModuleVersion(rule);
		assertThat(moduleVersion).isNotNull();
		assertThat(moduleVersion.isPresent()).isFalse();
	}

	@Test
	public void testAssertRuleModuleVersion() throws Exception
	{
		when(rule.getVersion()).thenReturn(1L);
		moduleVersioningService.assertRuleModuleVersion(rule, kieModule);
		verify(kieModule).setVersion(1L);
	}

	@Test
	public void testAssertRuleModuleVersionWhenModuleVersionIsBigger() throws Exception
	{
		when(kieModule.getVersion()).thenReturn(2L);
		when(rule.getVersion()).thenReturn(1L);
		moduleVersioningService.assertRuleModuleVersion(rule, kieModule);
		verify(kieModule, times(0)).setVersion(anyLong());
	}

	@Test
	public void testAssertRuleModuleVersionRuleVersionIsNull() throws Exception
	{
		when(rule.getVersion()).thenReturn(null);
		moduleVersioningService.assertRuleModuleVersion(rule, kieModule);
		assertThat(kieModule.getVersion()).isNotNull().isEqualTo(0);
	}

}
