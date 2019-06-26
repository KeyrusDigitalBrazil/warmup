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
package de.hybris.platform.ruledefinitions.conditions;

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
import de.hybris.platform.ruleengineservices.rao.CustomerSupportRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleCustomerSupportConditionTranslatorTest
{
	private static final String CUSTOMER_SUPPORT_RAO_VARIABLE = "customerSupportRaoVariable";

	@InjectMocks
	private RuleCustomerSupportConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;
	@Mock
	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleParameterData sessionActiveParameter;

	@Before
	public void setUp()
	{
		when(condition.getParameters()).thenReturn(parameters);
		when(parameters.get(RuleCustomerSupportConditionTranslator.ASSISTED_SERVICE_SESSION_ACTIVE_PARAM)).thenReturn(
				sessionActiveParameter);
		when(context.generateVariable(CustomerSupportRAO.class)).thenReturn(CUSTOMER_SUPPORT_RAO_VARIABLE);
		when(sessionActiveParameter.getValue()).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testTranslateOperatorParamNull()
	{
		when(parameters.get(RuleCustomerSupportConditionTranslator.ASSISTED_SERVICE_SESSION_ACTIVE_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateParamValueNull()
	{
		when(sessionActiveParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslate()
	{
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrAttributeCondition.class));
		assertEquals(RuleIrAttributeOperator.EQUAL, ((RuleIrAttributeCondition) ruleIrCondition).getOperator());
		assertEquals(RuleCustomerSupportConditionTranslator.CUSTOMER_SUPPORT_RAO_CUSTOMER_EMULATION_ACTIVE_ATTRIBUTE,
				((RuleIrAttributeCondition) ruleIrCondition).getAttribute());
		assertEquals(Boolean.TRUE, ((RuleIrAttributeCondition) ruleIrCondition).getValue());
		assertEquals(CUSTOMER_SUPPORT_RAO_VARIABLE, ((RuleIrAttributeCondition) ruleIrCondition).getVariable());
	}

	@Test
	public void testTranslateAlternative()
	{
		when(sessionActiveParameter.getValue()).thenReturn(Boolean.FALSE);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrAttributeCondition.class));
		assertEquals(RuleIrAttributeOperator.EQUAL, ((RuleIrAttributeCondition) ruleIrCondition).getOperator());
		assertEquals(RuleCustomerSupportConditionTranslator.CUSTOMER_SUPPORT_RAO_CUSTOMER_EMULATION_ACTIVE_ATTRIBUTE,
				((RuleIrAttributeCondition) ruleIrCondition).getAttribute());
		assertEquals(Boolean.FALSE, ((RuleIrAttributeCondition) ruleIrCondition).getValue());
		assertEquals(CUSTOMER_SUPPORT_RAO_VARIABLE, ((RuleIrAttributeCondition) ruleIrCondition).getVariable());
	}
}
