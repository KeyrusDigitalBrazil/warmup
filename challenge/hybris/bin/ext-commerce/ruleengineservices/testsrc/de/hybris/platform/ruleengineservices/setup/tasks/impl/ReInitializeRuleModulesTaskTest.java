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
package de.hybris.platform.ruleengineservices.setup.tasks.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengineservices.constants.RuleEngineServicesConstants;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.ruleengineservices.rule.services.RuleService;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReInitializeRuleModulesTaskTest
{
	private static final String LIVE_MODULE = "live module";
	@InjectMocks
	private ReInitializeRuleModulesTask reInitializeRuleModulesTask;
	@Mock
	private RuleService ruleService;
	@Mock
	private RuleEngineService ruleEngineService;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private RuleMaintenanceService ruleMaintenanceService;
	@Mock
	private RuleDao ruleDao;
	@Mock
	private AbstractRulesModuleModel activeModule;
	@Mock
	private SystemSetupContext context;
	@Mock
	private AbstractRuleModel abstractRule;

	@Before
	public void setUp() throws Exception
	{
		given(ruleService.getEngineRuleTypeForRuleType(SourceRuleModel.class)).willReturn(RuleType.DEFAULT);
		given(activeModule.getActive()).willReturn(Boolean.TRUE);
		given(activeModule.getName()).willReturn(LIVE_MODULE);
	}

	@Test
	public void shouldCompileAndPublishAllInactiveRules() throws Exception
	{
		final SourceRuleModel sourceRule = new SourceRuleModel();
		final SourceRuleModel sourceRule2 = new SourceRuleModel();
		//given
		given(rulesModuleDao.findActiveRulesModulesByRuleType(RuleType.DEFAULT)).willReturn(
				newArrayList(activeModule));
		given(ruleDao.findByVersionAndStatuses(RuleEngineServicesConstants.DEFAULT_RULE_VERSION, RuleStatus.INACTIVE))
				.willReturn(newArrayList(sourceRule, sourceRule2, abstractRule));
		//when
		reInitializeRuleModulesTask.execute(context);
		//then
		final ArgumentCaptor<List> qualifiedModules = ArgumentCaptor.forClass(List.class);
		verify(ruleMaintenanceService).compileAndPublishRulesWithBlocking(qualifiedModules.capture(), eq(LIVE_MODULE), eq(true));
		Assertions.assertThat(qualifiedModules.getValue()).containsExactly(sourceRule, sourceRule2);
	}

	@Test
	public void shouldNotFireInitializeWhenNoneOfModulesQualifiesForInitialization() throws Exception
	{
		//given
		given(rulesModuleDao.findActiveRulesModulesByRuleType(RuleType.DEFAULT)).willReturn(Collections.emptyList());
		//when
		reInitializeRuleModulesTask.execute(context);
		//then
		verify(ruleMaintenanceService, never()).compileAndPublishRulesWithBlocking(anyList(), anyString(), eq(true));
	}
}
