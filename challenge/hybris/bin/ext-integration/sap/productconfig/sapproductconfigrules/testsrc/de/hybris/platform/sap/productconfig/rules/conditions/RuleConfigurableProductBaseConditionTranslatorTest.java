/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.rules.conditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;



@UnitTest
public class RuleConfigurableProductBaseConditionTranslatorTest
{

	private RuleConfigurableProductBaseConditionTranslator classUnderTest;

	private RuleConditionData condition;

	private Map<String, RuleParameterData> parameters;
	private RuleParameterData csticParam;
	private RuleParameterData csticValueParam;

	@Before
	public void setUp()
	{
		classUnderTest = new RuleConfigurableProductConditionTranslator();
		condition = new RuleConditionData();
		parameters = new HashMap();
		condition.setParameters(parameters);
		csticParam = new RuleParameterData();
		parameters.put(RuleConfigurableProductBaseConditionTranslator.CSTIC_PARAM, csticParam);
		csticValueParam = new RuleParameterData();
		parameters.put(RuleConfigurableProductBaseConditionTranslator.CSTIC_VALUE_PARAM, csticValueParam);
	}

	@Test
	public void testGetCstic()
	{
		csticParam.setValue("CSTIC_NAME");
		final String cstic = classUnderTest.getCstic(condition);
		assertEquals("CSTIC_NAME", cstic);
	}

	@Test
	public void testGetCsticIgnoreCase()
	{
		csticParam.setValue("CstIC_naME");
		final String cstic = classUnderTest.getCstic(condition);
		assertEquals("CSTIC_NAME", cstic);
	}

	@Test
	public void testGetCsticNull()
	{
		csticParam.setValue(null);
		final String cstic = classUnderTest.getCstic(condition);
		assertNull(cstic);
	}

	@Test
	public void testGetCsticValue()
	{
		csticValueParam.setValue("CSTIC_VALUE_NAME");
		final String csticValue = classUnderTest.getCsticValue(condition, "CSTIC_NAME");
		assertEquals("CSTIC_VALUE_NAME", csticValue);
	}

	@Test
	public void testGetCsticValueConsideringCase()
	{
		csticValueParam.setValue("CstIC_Value_naME");
		final String csticValue = classUnderTest.getCsticValue(condition, "CSTIC_NAME");
		assertEquals("CstIC_Value_naME", csticValue);
	}

}
