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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleActionConditionTranslatorTest
{
	private static final String RULE_CODE = "ruleCode";

	private static final String RULE_ACTION_RAO_VARIABLE = "ruleActionRaoVariable";

	@InjectMocks
	private DefaultRuleActionConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;
	@Mock
	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleParameterData ruleParameter;
	@Mock
	private RuleParameterData allowedParameter;

	@Before
	public void setUp()
	{
		when(condition.getParameters()).thenReturn(parameters);
		when(parameters.get(DefaultRuleActionConditionTranslator.RULE_PARAM)).thenReturn(ruleParameter);
		when(parameters.get(DefaultRuleActionConditionTranslator.ALLOWED_PARAM)).thenReturn(allowedParameter);
		when(context.generateVariable(AbstractRuleActionRAO.class)).thenReturn(RULE_ACTION_RAO_VARIABLE);
		when(ruleParameter.getValue()).thenReturn(RULE_CODE);
		when(allowedParameter.getValue()).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testTranslateRuleParamNull()
	{
		when(parameters.get(DefaultRuleActionConditionTranslator.RULE_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateAllowedParamsNull()
	{
		when(parameters.get(DefaultRuleActionConditionTranslator.ALLOWED_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateRuleParamValueNull()
	{
		when(ruleParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateAllowedParamValueNull()
	{
		when(allowedParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslate()
	{
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition attrCondition = (RuleIrAttributeCondition) ruleIrCondition;
		assertEquals(RuleIrAttributeOperator.EQUAL, attrCondition.getOperator());
		assertEquals(DefaultRuleActionConditionTranslator.FIRED_RULE_CODE_ATTRIBUTE, attrCondition.getAttribute());
		assertEquals(RULE_CODE, attrCondition.getValue());
		assertEquals(RULE_ACTION_RAO_VARIABLE, attrCondition.getVariable());
	}

	@Test
	public void testTranslateAlternative()
	{
		when(context.generateLocalVariable(Mockito.any(RuleIrLocalVariablesContainer.class), Mockito.anyObject())).thenReturn(
				RULE_ACTION_RAO_VARIABLE);
		when(allowedParameter.getValue()).thenReturn(Boolean.FALSE);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrNotCondition.class));
		final RuleIrAttributeCondition attrCondition = ((RuleIrAttributeCondition) ((RuleIrNotCondition) ruleIrCondition)
				.getChildren().get(0));
		assertEquals(RuleIrAttributeOperator.EQUAL, (attrCondition.getOperator()));
		assertEquals(DefaultRuleActionConditionTranslator.FIRED_RULE_CODE_ATTRIBUTE, attrCondition.getAttribute());
		assertEquals(RULE_CODE, attrCondition.getValue());
		assertEquals(RULE_ACTION_RAO_VARIABLE, attrCondition.getVariable());
	}

}
