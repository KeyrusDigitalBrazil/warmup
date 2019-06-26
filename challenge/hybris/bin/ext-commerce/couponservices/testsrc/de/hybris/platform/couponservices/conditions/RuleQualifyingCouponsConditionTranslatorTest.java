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
package de.hybris.platform.couponservices.conditions;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.rao.CouponRAO;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleQualifyingCouponsConditionTranslatorTest
{
	private static final String COUPON_ID = "couponId";

	private static final String COUPON_RAO_VARIABLE = "couponRaoVariable";

	@InjectMocks
	private RuleQualifyingCouponsConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;
	@Mock
	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleParameterData couponsParameter;

	@Before
	public void setUp()
	{
		when(condition.getParameters()).thenReturn(parameters);
		when(parameters.get(RuleQualifyingCouponsConditionTranslator.COUPONS_PARAM)).thenReturn(couponsParameter);
		when(context.generateVariable(CouponRAO.class)).thenReturn(COUPON_RAO_VARIABLE);
		when(couponsParameter.getValue()).thenReturn(Collections.singletonList(COUPON_ID));
	}

	@Test
	public void testTranslateOperatorParamNull()
	{
		when(parameters.get(RuleQualifyingCouponsConditionTranslator.COUPONS_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateParamValueNull()
	{
		when(couponsParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslate()
	{
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrAttributeCondition.class));
		assertEquals(RuleIrAttributeOperator.IN, ((RuleIrAttributeCondition) ruleIrCondition).getOperator());
		assertEquals(RuleQualifyingCouponsConditionTranslator.COUPON_RAO_COUPON_ID_ATTRIBUTE,
				((RuleIrAttributeCondition) ruleIrCondition).getAttribute());
		assertEquals(COUPON_ID, ((List<String>) ((RuleIrAttributeCondition) ruleIrCondition).getValue()).get(0));
		assertEquals(COUPON_RAO_VARIABLE, ((RuleIrAttributeCondition) ruleIrCondition).getVariable());
	}

	@Test
	public void testTranslateAlternative()
	{
		when(couponsParameter.getValue()).thenReturn(Arrays.asList("ttt", COUPON_ID));
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrAttributeCondition.class));
		assertEquals(RuleIrAttributeOperator.IN, ((RuleIrAttributeCondition) ruleIrCondition).getOperator());
		assertEquals(RuleQualifyingCouponsConditionTranslator.COUPON_RAO_COUPON_ID_ATTRIBUTE,
				((RuleIrAttributeCondition) ruleIrCondition).getAttribute());
		assertTrue(((List<String>) ((RuleIrAttributeCondition) ruleIrCondition).getValue()).contains("ttt"));
		assertTrue(((List<String>) ((RuleIrAttributeCondition) ruleIrCondition).getValue()).contains(COUPON_ID));
		assertEquals(COUPON_RAO_VARIABLE, ((RuleIrAttributeCondition) ruleIrCondition).getVariable());
	}
}
