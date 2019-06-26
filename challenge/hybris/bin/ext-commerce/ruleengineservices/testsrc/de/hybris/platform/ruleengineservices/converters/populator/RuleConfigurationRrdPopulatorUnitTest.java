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
import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.constants.RuleEngineServicesConstants;
import de.hybris.platform.ruleengineservices.rrd.RuleConfigurationRRD;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleConfigurationRrdPopulatorUnitTest
{

	private static final String RULE_CODE = "RULE_CODE";
	private static final String RULE_GROUP_CODE = "RULE_GROUP_CODE";
	private static final int MAX_RUNS = 12;

	@InjectMocks
	private RuleConfigurationRrdPopulator populator;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private AbstractRuleEngineRuleModel source;

	@Before
	public void setUp() throws Exception
	{
		populator = new RuleConfigurationRrdPopulator();
		populator.setConfigurationService(configurationService);
		when(configurationService.getConfiguration()).thenReturn(configuration);

		when(source.getMaxAllowedRuns()).thenReturn(valueOf(MAX_RUNS));
		when(source.getCode()).thenReturn(RULE_CODE);
		when(source.getRuleGroupCode()).thenReturn(RULE_GROUP_CODE);
	}

	@Test
	public void testPopulate()
	{
		final RuleConfigurationRRD target = new RuleConfigurationRRD();
		populator.populate(source, target);

		assertEquals(RULE_CODE, target.getRuleCode());
		assertEquals(RULE_GROUP_CODE, target.getRuleGroupCode());
		assertEquals(valueOf(MAX_RUNS), valueOf(target.getMaxAllowedRuns()));
		assertEquals(valueOf(0), target.getCurrentRuns());
	}

	@Test
	public void testDefaultMaxRuns()
	{
		// force default value
		when(source.getMaxAllowedRuns()).thenReturn(null);

		final RuleConfigurationRRD target = new RuleConfigurationRRD();
		populator.populate(source, target);
		assertEquals(valueOf(RuleEngineServicesConstants.DEFAULT_MAX_ALLOWED_RUNS), target.getMaxAllowedRuns());
	}
}
