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
package de.hybris.platform.droolsruleengineservices.compiler.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleGeneratorContext;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleValueFormatter;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExecutableAction;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariable;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDroolsRuleActionsGeneratorTest extends AbstractGeneratorTest
{
	private static final String RULE_EXECUTION_CONTEXT_RRD_CLASS_NAME = "RuleExecutionContextRRD";
	public static final String INDENTATION = "  ";
	public static final String VARIABLE_PREFIX = "$";

	public static final String CART_VARIABLE_NAME = "cart";
	public static final String CART_VARIABLE_CLASS_NAME = "CartRAO";

	public static final String RESULT_VARIABLE_NAME = "result";
	public static final String RESULT_VARIABLE_CLASS_NAME = "RuleEngineResultRAO";

	public static final String MAP_CLASS_NAME = "Map";
	public static final String ACTION_CONTEXT_CLASS_NAME = "DefaultDroolsRuleActionContext";

	@Mock
	private DroolsRuleGeneratorContext droolsRuleGeneratorContext;

	@Mock
	private DroolsRuleValueFormatter droolsRuleValueFormatter;

	private RuleIrVariable cartVariable;
	private RuleIrVariable resultVariable;

	@Mock
	private DroolsRuleModel droolsRule;
	@Mock
	private AbstractRuleModel rule;

	private RuleIr ruleIr;
	private Map<String, RuleIrVariable> ruleIrVariables;

	private DefaultDroolsRuleActionsGenerator actionsGenerator;

	@Before
	public void init()
	{
		cartVariable = new RuleIrVariable();
		cartVariable.setName(CART_VARIABLE_NAME);
		cartVariable.setType(CartRAO.class);

		resultVariable = new RuleIrVariable();
		resultVariable.setName(RESULT_VARIABLE_NAME);
		resultVariable.setType(RuleEngineResultRAO.class);

		ruleIr = new RuleIr();
		ruleIrVariables = new LinkedHashMap<>();

		when(droolsRuleGeneratorContext.getIndentationSize()).thenReturn(INDENTATION);
		when(droolsRuleGeneratorContext.getVariablePrefix()).thenReturn(VARIABLE_PREFIX);
		when(droolsRuleGeneratorContext.getRuleIr()).thenReturn(ruleIr);
		when(droolsRuleGeneratorContext.getVariables()).thenReturn(ruleIrVariables);
		when(droolsRuleGeneratorContext.generateClassName(CartRAO.class)).thenReturn(CART_VARIABLE_CLASS_NAME);
		when(droolsRuleGeneratorContext.generateClassName(RuleEngineResultRAO.class)).thenReturn(RESULT_VARIABLE_CLASS_NAME);
		when(droolsRuleGeneratorContext.generateClassName(Map.class)).thenReturn(MAP_CLASS_NAME);
		when(droolsRuleGeneratorContext.generateClassName(DefaultDroolsRuleActionContext.class)).thenReturn(
				ACTION_CONTEXT_CLASS_NAME);

		when(droolsRuleGeneratorContext.getDroolsRule()).thenReturn(droolsRule);
		when(droolsRule.getSourceRule()).thenReturn(rule);

		actionsGenerator = new DefaultDroolsRuleActionsGenerator();
		actionsGenerator.setDroolsRuleValueFormatter(droolsRuleValueFormatter);
	}

	@Test
	public void testGenerateCode() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString("/droolsruleengineservices/test/compiler/generatedActions.bin");

		final BigDecimal totalValue = BigDecimal.valueOf(20);
		final Map<String, Object> actionParameters = Collections.singletonMap("total", totalValue);

		final RuleIrExecutableAction ruleIrAction = new RuleIrExecutableAction();
		ruleIrAction.setActionId("testBeanID");
		ruleIrAction.setActionParameters(actionParameters);

		ruleIr.setActions(Collections.singletonList(ruleIrAction));
		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);

		when(droolsRuleValueFormatter.formatValue(droolsRuleGeneratorContext, actionParameters)).thenReturn(
				"[\"value\":new BigDecimal(20)]");

		// when
		final String generatedDroolsCode = actionsGenerator.generateActions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(removeAllWhitespaces(expectedDroolsCode), removeAllWhitespaces(generatedDroolsCode));
	}
}
