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
package de.hybris.platform.ruleengineservices.definitions.conditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExecutableCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class RuleExecutableConditionTranslatorTest
{
	public static final String CONDITION_ID = "condition";
	public static final String TEST_PARAM_ID = "test";
	public static final String TEST_PARAM_VALUE = "testvalue";

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); //NOPMD

	@Mock
	private RuleCompilerContext context;

	private RuleConditionData condition;
	private RuleConditionDefinitionData conditionDefinition;

	private RuleExecutableConditionTranslator ruleExecutableConditionTranslator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		condition = new RuleConditionData();
		condition.setParameters(Collections.emptyMap());

		conditionDefinition = new RuleConditionDefinitionData();
		conditionDefinition.setTranslatorParameters(Collections.emptyMap());

		ruleExecutableConditionTranslator = new RuleExecutableConditionTranslator();
	}

	@Test
	public void translate() throws Exception
	{
		// given
		conditionDefinition.setTranslatorParameters(
				Collections.singletonMap(RuleExecutableConditionTranslator.CONDITION_ID_PARAM, CONDITION_ID));

		// when
		final RuleIrCondition irCondition = ruleExecutableConditionTranslator.translate(context, condition, conditionDefinition);

		// then
		assertNotNull(irCondition);
		assertTrue(irCondition instanceof RuleIrExecutableCondition);
		assertEquals(CONDITION_ID, ((RuleIrExecutableCondition) irCondition).getConditionId());
	}

	@Test
	public void translateWithParameter() throws Exception
	{
		// given
		final RuleParameterData testParam = new RuleParameterData();
		testParam.setValue(TEST_PARAM_VALUE);

		condition.setParameters(Collections.singletonMap(TEST_PARAM_ID, testParam));
		conditionDefinition.setTranslatorParameters(
				Collections.singletonMap(RuleExecutableConditionTranslator.CONDITION_ID_PARAM, CONDITION_ID));

		// when
		final RuleIrCondition irCondition = ruleExecutableConditionTranslator.translate(context, condition, conditionDefinition);

		// then
		assertNotNull(irCondition);
		assertTrue(irCondition instanceof RuleIrExecutableCondition);
		assertEquals(CONDITION_ID, ((RuleIrExecutableCondition) irCondition).getConditionId());
		assertEquals(TEST_PARAM_VALUE, ((RuleIrExecutableCondition) irCondition).getConditionParameters().get(TEST_PARAM_ID));
	}

	@Test
	public void failToTranslateNoConditionId() throws Exception
	{
		// when
		final RuleIrCondition irCondition = ruleExecutableConditionTranslator.translate(context, condition, conditionDefinition);

		// then
		assertTrue(irCondition instanceof RuleIrFalseCondition);
	}

}
