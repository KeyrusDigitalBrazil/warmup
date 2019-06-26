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
package de.hybris.platform.ruleengineservices.rrd.equals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rrd.RuleConfigurationRRD;
import de.hybris.platform.ruleengineservices.rrd.RuleGroupExecutionRRD;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class RRDEqualsUnitTest
{

	private static final String RULECODE_1 = "code1";
	private static final String RULECODE_2 = "code2";
	private static final String RULEGROUPCODE_1 = "group1";
	private static final String RULEGROUPCODE_2 = "group2";


	private RuleConfigurationRRD config1;
	private RuleConfigurationRRD config2;

	private RuleGroupExecutionRRD exec1;
	private RuleGroupExecutionRRD exec2;

	private RuleConfigurationRRD config1WithOtherGroup;


	@Before
	public void setup()
	{
		config1 = new RuleConfigurationRRD();
		config1.setRuleCode(RULECODE_1);
		config1.setRuleGroupCode(RULEGROUPCODE_1);

		config2 = new RuleConfigurationRRD();
		config2.setRuleCode(RULECODE_2);
		config2.setRuleGroupCode(RULEGROUPCODE_2);

		config1WithOtherGroup = new RuleConfigurationRRD();
		config1WithOtherGroup.setRuleCode(RULECODE_1);
		config1WithOtherGroup.setRuleGroupCode(RULEGROUPCODE_2);


		exec1 = new RuleGroupExecutionRRD();
		exec1.setCode(RULEGROUPCODE_1);

		exec2 = new RuleGroupExecutionRRD();
		exec1.setCode(RULEGROUPCODE_2);

	}

	@Test
	public void testRRDsAreNotEqual()
	{
		// tests that the equals method check the type / don't fail if different types are checked for equality
		assertNotEquals(config1, config2);
		assertNotEquals(exec1, exec2);
		assertNotEquals(config1, exec1);
		assertNotEquals(exec1, config1);

	}

	@Test
	public void testRRDsAreEqual()
	{
		// only the code is tested in equals, not the rule group code
		assertEquals(config1, config1WithOtherGroup);
	}
}
