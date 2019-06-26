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

import com.google.common.collect.Maps;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleOrderThresholdPerfectPartnerConditionTranslatorTest
{
	private static final String VAR_PLACEHOLDER = "var";

	@InjectMocks
	private RuleOrderThresholdPerfectPartnerConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;

	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleParameterData cartThresholdParam;
	@Mock
	private RuleParameterData isDiscountedPriceIncludedParam;
	@Mock
	private RuleParameterData partnerProductParam;

	@Before
	public void setUp()
	{
		when(cartThresholdParam.getValue()).thenReturn(Maps.asMap(Sets.newSet("USD", "JPY"), x -> BigDecimal.valueOf(100)));
		when(isDiscountedPriceIncludedParam.getValue()).thenReturn(Boolean.FALSE);
		when(partnerProductParam.getValue()).thenReturn("prod1");

		parameters = Maps.newHashMap();
		parameters.put(RuleOrderThresholdPerfectPartnerConditionTranslator.CART_THRESHOLD_PARAM,
				cartThresholdParam);
		parameters.put(RuleOrderThresholdPerfectPartnerConditionTranslator.IS_DISCOUNTED_PRICE_INCLUDED_PARAM,
				isDiscountedPriceIncludedParam);
		parameters.put(RuleOrderThresholdPerfectPartnerConditionTranslator.PARTNER_PRODUCT_PARAM,
				partnerProductParam);

		when(condition.getParameters()).thenReturn(parameters);

		when(context.generateVariable(any())).thenReturn(VAR_PLACEHOLDER);
	}

	@Test
	public void testWhenParamIsNullThenFalseCondition()
	{
		parameters.forEach((k,v) -> {
			final Map<String, RuleParameterData> cp = Maps.newHashMap(parameters);
			parameters.put(k, null);

			final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
			assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));

			parameters = cp;
		});
	}

	@Test
	public void testWhenParamValueIsNullThenFalseCondition()
	{
		parameters.forEach((k,v) -> {
			Object value = parameters.get(k).getValue();
			when(parameters.get(k).getValue()).thenReturn(null);

			final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
			assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));

			when(parameters.get(k).getValue()).thenReturn(value);
		});
	}

	@Test
	public void testWhenCartThresholdParamIsEmptyThenFalseCondition()
	{
		when(cartThresholdParam.getValue()).thenReturn(Maps.newHashMap());
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testWhenTranslateThenRespectAnyPossibleCurrency()
	{
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));
		assertEquals(RuleIrGroupOperator.OR, ((RuleIrGroupCondition) ruleIrCondition).getOperator());
		assertEquals(2, ((RuleIrGroupCondition) ruleIrCondition).getChildren().size());
	}
}
