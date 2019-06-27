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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.constants.RuleEngineServicesConstants;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.model.ModelService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class LinkSourceRulesTaskTest
{
	private static final String UUID = "uuid";
	@InjectMocks
	private LinkSourceRulesTask task;
	@Mock
	private RuleDao ruleDao;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private ModelService modelService;
	@Mock
	private SourceRuleModel sourceRule;
	@Mock
	private AbstractRuleEngineRuleModel ruleEngineRule;
	@Mock
	private SystemSetupContext context;
	@Before
	public void setUp() throws Exception
	{
		given(ruleDao.findByVersionAndStatuses(RuleEngineServicesConstants.DEFAULT_RULE_VERSION,RuleStatus.INACTIVE, RuleStatus.ARCHIVED)).willReturn(Lists.newArrayList(
					 sourceRule));
	}

	@Test
	public void shouldUpdateMappingWhenCorrespondingRuleEngineRuleFound() throws Exception
	{
		//given
		given(sourceRule.getUuid()).willReturn(UUID);
		given(engineRuleDao.getRuleByUuid(UUID)).willReturn(ruleEngineRule);
		//when
		task.execute(context);
		//then
		verify(ruleEngineRule).setSourceRule(sourceRule);
	}

	@Test
	public void shouldSkipUpdatingMappingWhenCorrespondingRuleEngineRuleNotFound() throws Exception
	{
		//given
		given(sourceRule.getUuid()).willReturn(UUID);
		given(engineRuleDao.getRuleByUuid(UUID)).willReturn(null);
		//when
		task.execute(context);
		//then
		final ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(modelService).saveAll(captor.capture());
		assertThat(captor.getValue()).isEmpty();
	}
}
