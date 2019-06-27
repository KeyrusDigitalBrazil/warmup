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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rrd.RuleGroupExecutionRRD;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleGroupExecutionRrdTemplatePopulatorUnitTest
{

	private static final String RULE_GROUP_CODE = "RULE_GROUP_CODE";
	private static final boolean RULE_GROUP_IS_EXCLUSIVE = true;

	@Mock
	private RuleGroupExecutionRRD source;

	private RuleGroupExecutionRrdTemplatePopulator populator;

	@Before
	public void setUp() throws Exception
	{
		populator = new RuleGroupExecutionRrdTemplatePopulator();
		when(source.getCode()).thenReturn(RULE_GROUP_CODE);
		when(source.isExclusive()).thenReturn(RULE_GROUP_IS_EXCLUSIVE);
	}

	@Test
	public void testPopulate()
	{
		final RuleGroupExecutionRRD target = new RuleGroupExecutionRRD();
		populator.populate(source, target);
		assertEquals(RULE_GROUP_CODE, target.getCode());
		assertEquals(RULE_GROUP_IS_EXCLUSIVE, target.isExclusive());
	}
}
