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
package de.hybris.platform.ruleengineservices.converters.populator;

import static de.hybris.platform.ruleengineservices.constants.RuleEngineServicesConstants.DEFAULT_RULEGROUP_CODE_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.model.RuleGroupModel;
import de.hybris.platform.ruleengineservices.rrd.RuleGroupExecutionRRD;
import de.hybris.platform.ruleengineservices.rule.dao.RuleGroupDao;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Optional;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleGroupExecutionRrdPopulatorUnitTest
{
	private static final boolean RULE_GROUP_IS_EXCLUSIVE = true;
	private static final String RULE_GROUP_CODE = "RULE_GROUP_CODE";
	private static final boolean DEFAULT_RULE_GROUP_IS_EXCLUSIVE = true;

	@InjectMocks
	private RuleGroupExecutionRrdPopulator populator;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private RuleGroupDao ruleGroupDao;

	@Mock
	private AbstractRuleEngineRuleModel source;

	@Mock
	private RuleGroupModel ruleGroupModel;

	@Mock
	private RuleGroupModel defaultRuleGroupModel;

	@Before
	public void setUp() throws Exception
	{
		populator = new RuleGroupExecutionRrdPopulator();
		populator.setConfigurationService(configurationService);
		populator.setRuleGroupDao(ruleGroupDao);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(source.getRuleGroupCode()).thenReturn(RULE_GROUP_CODE);
		when(ruleGroupDao.findRuleGroupByCode(RULE_GROUP_CODE)).thenReturn(Optional.of(ruleGroupModel));
		when(ruleGroupModel.isExclusive()).thenReturn(RULE_GROUP_IS_EXCLUSIVE);
		when(defaultRuleGroupModel.isExclusive()).thenReturn(DEFAULT_RULE_GROUP_IS_EXCLUSIVE);
	}

	@Test
	public void testPopulate()
	{
		final RuleGroupExecutionRRD target = new RuleGroupExecutionRRD();
		populator.populate(source, target);
		assertEquals(RULE_GROUP_CODE, target.getCode());
		assertTrue(MapUtils.isEmpty(target.getExecutedRules()));
		assertEquals(RULE_GROUP_IS_EXCLUSIVE, target.isExclusive());
	}
}
