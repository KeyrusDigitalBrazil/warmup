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
import de.hybris.platform.ruledefinitions.AmountOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleProductPriceConditionTranslatorTest
{
	private static final String ORDER_ENTRY_RAO_VAR = "orderEntryRaoVariable";
	private static final String CART_RAO_VAR = "cartRaoVariable";
	private static final String AVAILABLE_QUANTITY_VAR = "availableQuantity";

	@InjectMocks
	private RuleProductPriceConditionTranslator translator;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleConditionDefinitionData conditionDefinition;
	@Mock
	private Map<String, RuleParameterData> parameters;
	@Mock
	private RuleParameterData operatorParameter;
	@Mock
	private RuleParameterData valueParameter;

	@Before
	public void setUp()
	{
		when(condition.getParameters()).thenReturn(parameters);
		when(parameters.get(RuleProductPriceConditionTranslator.OPERATOR_PARAM)).thenReturn(operatorParameter);
		when(parameters.get(RuleProductPriceConditionTranslator.VALUE_PARAM)).thenReturn(valueParameter);
		when(context.generateVariable(OrderEntryRAO.class)).thenReturn(ORDER_ENTRY_RAO_VAR);
		when(context.generateVariable(CartRAO.class)).thenReturn(CART_RAO_VAR);
		when(operatorParameter.getValue()).thenReturn(AmountOperator.GREATER_THAN);

	}

	@Test
	public void testTranslateOperatorParamNull()
	{
		when(parameters.get(RuleProductPriceConditionTranslator.OPERATOR_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateValueParamNull()
	{
		when(parameters.get(RuleProductPriceConditionTranslator.VALUE_PARAM)).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateOperatorNull()
	{
		when(operatorParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateValueNull()
	{
		when(valueParameter.getValue()).thenReturn(null);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslateValueEmpty()
	{
		when(valueParameter.getValue()).thenReturn(MapUtils.EMPTY_MAP);
		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);

		assertThat(ruleIrCondition, instanceOf(RuleIrFalseCondition.class));
	}

	@Test
	public void testTranslate()
	{
		final Map<String, BigDecimal> value = new HashMap<String, BigDecimal>();
		value.put("USD", new BigDecimal(600));
		value.put("EUR", new BigDecimal(500));

		when(valueParameter.getValue()).thenReturn(value);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(2, irGroupCondition.getChildren().size());
		assertEquals(RuleIrGroupOperator.OR, irGroupCondition.getOperator());

		checkConditionChild(irGroupCondition.getChildren().get(0));
		checkConditionChild(irGroupCondition.getChildren().get(1));
	}

	protected void checkConditionChild(final RuleIrCondition condition)
	{
		assertThat(condition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irCurrencyGroupCondition = (RuleIrGroupCondition) condition;
		assertEquals(RuleIrGroupOperator.AND, irCurrencyGroupCondition.getOperator());
		final List<RuleIrCondition> irCurrencyGroupConditionChildren = irCurrencyGroupCondition.getChildren();
		assertEquals(5, irCurrencyGroupConditionChildren.size());

		assertThat(irCurrencyGroupConditionChildren.get(3), instanceOf(RuleIrAttributeRelCondition.class));
		final RuleIrAttributeRelCondition ruleIrCartConsumedCondition = (RuleIrAttributeRelCondition) irCurrencyGroupConditionChildren.get(3);
		assertEquals(RuleIrAttributeOperator.EQUAL, ruleIrCartConsumedCondition.getOperator());
		assertEquals(ORDER_ENTRY_RAO_VAR, ruleIrCartConsumedCondition.getTargetVariable());

		assertThat(irCurrencyGroupConditionChildren.get(4), instanceOf(RuleIrAttributeCondition.class));
		final RuleIrAttributeCondition ruleIrAvailableQtyCondition = (RuleIrAttributeCondition) irCurrencyGroupConditionChildren.get(4);
		assertEquals(RuleIrAttributeOperator.GREATER_THAN_OR_EQUAL, ruleIrAvailableQtyCondition.getOperator());
		assertEquals(AVAILABLE_QUANTITY_VAR, ruleIrAvailableQtyCondition.getAttribute());
	}

	@Test
	public void testTranslateNulleKeyInValue()
	{
		final Map<String, BigDecimal> value = new HashMap<String, BigDecimal>();
		value.put(null, new BigDecimal(600));
		value.put("EUR", new BigDecimal(500));

		when(valueParameter.getValue()).thenReturn(value);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(1, irGroupCondition.getChildren().size());
		assertEquals(RuleIrGroupOperator.OR, irGroupCondition.getOperator());

		checkConditionChild(irGroupCondition.getChildren().get(0));
	}

	@Test
	public void testTranslateNulleValueInValue()
	{
		final Map<String, BigDecimal> value = new HashMap<String, BigDecimal>();
		value.put("USD", null);
		value.put("EUR", new BigDecimal(500));

		when(valueParameter.getValue()).thenReturn(value);

		final RuleIrCondition ruleIrCondition = translator.translate(context, condition, conditionDefinition);
		assertThat(ruleIrCondition, instanceOf(RuleIrGroupCondition.class));

		final RuleIrGroupCondition irGroupCondition = (RuleIrGroupCondition) ruleIrCondition;
		assertEquals(1, irGroupCondition.getChildren().size());
		assertEquals(RuleIrGroupOperator.OR, irGroupCondition.getOperator());

		checkConditionChild(irGroupCondition.getChildren().get(0));
	}
}
