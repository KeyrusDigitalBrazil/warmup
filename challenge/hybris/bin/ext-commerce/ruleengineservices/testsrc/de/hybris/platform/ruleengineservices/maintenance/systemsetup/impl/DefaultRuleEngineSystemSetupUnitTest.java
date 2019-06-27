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
package de.hybris.platform.ruleengineservices.maintenance.systemsetup.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.jalo.DroolsKIEModule;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengineservices.jalo.SourceRule;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleEngineSystemSetupUnitTest
{
	private static final String MODULE1_NAME = "module1";
	private static final String MODULE2_NAME = "module2";

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	private final PK sourceRule1Pk = PK.fromLong(1234L);
	private final PK sourceRule2Pk = PK.fromLong(1235L);
	private final PK ruleModulePK = PK.fromLong(1236L);
	@InjectMocks
	private DefaultRuleEngineSystemSetup defaultRuleEngineSystemSetup;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private ModelService modelService;
	@Mock
	private RuleMaintenanceService ruleMaintenanceService;
	@Mock
	private RuleEngineService ruleEngineService;
	@Mock
	private SourceRuleModel sourceRule1;
	@Mock
	private SourceRuleModel sourceRule2;
	@Mock
	private SourceRule jaloSourceRule1;
	@Mock
	private DroolsKIEModule jaloRuleModule;
	@Mock
	private DroolsKIEModuleModel ruleModule;

	@Before
	public void setup()
	{
		when(sourceRule1.getPk()).thenReturn(sourceRule1Pk);
		when(sourceRule2.getPk()).thenReturn(sourceRule2Pk);
		when(jaloSourceRule1.getPK()).thenReturn(sourceRule1Pk);
		when(jaloRuleModule.getPK()).thenReturn(ruleModulePK);
		when(modelService.get(sourceRule1Pk)).thenReturn(sourceRule1);
		when(modelService.get(sourceRule2Pk)).thenReturn(sourceRule2);
		when(modelService.get(ruleModulePK)).thenReturn(ruleModule);
	}

	@Test
	public void testRegisterJaloSourceRule()
	{
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(jaloSourceRule1, MODULE1_NAME);
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap()).containsKey(MODULE1_NAME);
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE1_NAME)).contains(jaloSourceRule1.getPK());
	}

	@Test
	public void testRegisterJaloSourceRules()
	{
		//when
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(jaloSourceRule1, new String[]
		{ MODULE1_NAME, MODULE2_NAME });
		//then
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap()).containsKey(MODULE1_NAME);
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap()).containsKey(MODULE2_NAME);
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE1_NAME)).contains(jaloSourceRule1.getPK());
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE2_NAME)).contains(jaloSourceRule1.getPK());
	}

	@Test
	public void testRegisterSourceRuleModels()
	{
		//when
		defaultRuleEngineSystemSetup.registerSourceRulesForDeployment(Arrays.asList(new SourceRuleModel[]
		{ sourceRule1, sourceRule2 }), Arrays.asList(new String[]
		{ MODULE1_NAME, MODULE2_NAME }));
		//then
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap()).containsKey(MODULE1_NAME);
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap()).containsKey(MODULE2_NAME);
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE1_NAME)).contains(sourceRule1.getPk());
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE1_NAME)).contains(sourceRule2.getPk());
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE2_NAME)).contains(sourceRule1.getPk());
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap().get(MODULE2_NAME)).contains(sourceRule2.getPk());
	}

	@Test
	public void testDoNotFailOnErrorDuringRuleRegistration()
	{
		//given
		final Configuration configuration = Mockito.mock(Configuration.class);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getBoolean("ruleengineservices.system.setup.failOnError", true)).thenReturn(false);
		when(jaloSourceRule1.getPK()).thenReturn(null);
		//when
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(jaloSourceRule1, MODULE1_NAME);
		//then
		assertThat(defaultRuleEngineSystemSetup.getInitializationMap()).doesNotContainKey(MODULE1_NAME);
	}

	@Test
	public void testFailOnNullValueForRuleModels()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);
		//when
		defaultRuleEngineSystemSetup.registerSourceRulesForDeployment((Collection<SourceRuleModel>) null,
				Collections.singleton(MODULE1_NAME));
	}

	@Test
	public void testFailOnNullValueForModuleNames()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);
		//when
		defaultRuleEngineSystemSetup.registerSourceRulesForDeployment(Collections.singleton(sourceRule1), null);
	}

	@Test
	public void testFailOnNullValueForJaloSourceRuleOneModuleName()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);
		//when
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(null, MODULE1_NAME);
	}

	@Test
	public void testFailOnNullValueForJaloSourceRuleMultipleModuleName()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);
		//when
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(null, new String[]
		{ MODULE1_NAME });
	}

	@Test
	public void testFailOnNullValueForJaloModuleName()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);
		//when
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(jaloSourceRule1, (String) null);
	}

	@Test
	public void testFailOnNullValueForJaloModuleNames()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);
		//when
		defaultRuleEngineSystemSetup.registerSourceRuleForDeployment(jaloSourceRule1, (String[]) null);
	}

	@Test
	public void testShouldInvokeModuleInitialization()
	{
		defaultRuleEngineSystemSetup.initializeModule(jaloRuleModule);

		Mockito.verify(ruleEngineService).initialize(eq(Collections.singletonList(ruleModule)), eq(Boolean.TRUE), eq(Boolean.TRUE));
	}
}
