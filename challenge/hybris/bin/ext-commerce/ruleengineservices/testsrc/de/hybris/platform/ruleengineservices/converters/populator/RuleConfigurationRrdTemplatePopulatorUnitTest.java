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

import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rrd.RuleConfigurationRRD;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleConfigurationRrdTemplatePopulatorUnitTest
{
	private static final String RULE_CODE = "RULE_CODE";
	private static final String RULE_GROUP_CODE = "RULE_GROUP_CODE";
	private static final int MAX_RUNS = 12;
	private static final int CURRENT_RUNS = 5;

	@Mock
	private RuleConfigurationRRD source;

	private RuleConfigurationRrdTemplatePopulator populator;

	@Before
	public void setUp() throws Exception
	{
		populator = new RuleConfigurationRrdTemplatePopulator();

		when(source.getMaxAllowedRuns()).thenReturn(valueOf(MAX_RUNS));
		when(source.getRuleCode()).thenReturn(RULE_CODE);
		when(source.getRuleGroupCode()).thenReturn(RULE_GROUP_CODE);
		when(source.getCurrentRuns()).thenReturn(CURRENT_RUNS);
	}

	@Test
	public void testPopulate()
	{
		final RuleConfigurationRRD target = new RuleConfigurationRRD();
		populator.populate(source, target);

		assertEquals(RULE_CODE, target.getRuleCode());
		assertEquals(RULE_GROUP_CODE, target.getRuleGroupCode());
		assertEquals(valueOf(MAX_RUNS), valueOf(target.getMaxAllowedRuns()));
		assertEquals(valueOf(CURRENT_RUNS), target.getCurrentRuns());
	}
}
