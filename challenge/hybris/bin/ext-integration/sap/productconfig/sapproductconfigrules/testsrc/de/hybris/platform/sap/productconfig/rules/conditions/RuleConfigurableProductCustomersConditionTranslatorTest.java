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

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleContainedOperator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class RuleConfigurableProductCustomersConditionTranslatorTest
{
	public static final String V_USER = "$v_User";
	public static final String V_CART = "$v_Cart";
	private RuleConfigurableProductCustomersConditionTranslator classUnderTest;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;

	private RuleConditionDefinitionData conditionDefinition;

	Map<String, RuleParameterData> ruleParameters;

	final List<String> customerList = Arrays.asList("customer1", "customer2");


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new RuleConfigurableProductCustomersConditionTranslator();

		conditionDefinition = new RuleConditionDefinitionData();

		ruleParameters = new HashMap<String, RuleParameterData>();

		final RuleParameterData customersParameter = new RuleParameterData();
		customersParameter.setValue(customerList);
		ruleParameters.put(RuleConfigurableProductCustomersConditionTranslator.CUSTOMERS_PARAM, customersParameter);

		given(condition.getParameters()).willReturn(ruleParameters);

		given(context.generateVariable(UserRAO.class)).willReturn(V_USER);
		given(context.generateVariable(CartRAO.class)).willReturn(V_CART);

	}

	@Test
	public void testTranslateIn() throws RuleCompilerException
	{
		final RuleParameterData customersOperatorParameter = new RuleParameterData();
		customersOperatorParameter.setValue(ProductConfigRuleContainedOperator.IS_CONTAINED_IN);
		ruleParameters.put(RuleConfigurableProductCustomersConditionTranslator.CUSTOMERS_OPERATOR_PARAM,
				customersOperatorParameter);

		verifyTranslate(RuleIrAttributeOperator.IN);
	}

	@Test
	public void testTranslateNotIn() throws RuleCompilerException
	{
		final RuleParameterData customersOperatorParameter = new RuleParameterData();
		customersOperatorParameter.setValue(ProductConfigRuleContainedOperator.IS_NOT_CONTAINED_IN);
		ruleParameters.put(RuleConfigurableProductCustomersConditionTranslator.CUSTOMERS_OPERATOR_PARAM,
				customersOperatorParameter);

		verifyTranslate(RuleIrAttributeOperator.NOT_IN);
	}


	@Test
	public void testTranslateWithoutCustomerGroupParam()
	{
		ruleParameters.remove(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_OPERATOR_PARAM);

		RuleIrCondition result = classUnderTest.translate(context,
				condition, conditionDefinition);
		assertEquals(RuleIrFalseCondition.class, result.getClass());
	}

	@Test
	public void testTranslateWithEmptyCustomerGroups()
	{
		final RuleParameterData customerGroupsParameter = new RuleParameterData();
		customerGroupsParameter.setValue(emptyList());
		ruleParameters.put(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_PARAM, customerGroupsParameter);

		RuleIrCondition result = classUnderTest.translate(context,
				condition, conditionDefinition);
		assertEquals(RuleIrFalseCondition.class, result.getClass());
	}


	protected void verifyTranslate(final RuleIrAttributeOperator attributeOperator) throws RuleCompilerException
	{
		final RuleIrGroupCondition irResultCondition = (RuleIrGroupCondition) classUnderTest.translate(context, condition,
				conditionDefinition);

		assertNotNull(irResultCondition);

		final List<RuleIrCondition> children = irResultCondition.getChildren();
		assertEquals(3, children.size());
		assertEquals(RuleIrGroupOperator.AND, irResultCondition.getOperator());

		final RuleIrTypeCondition irUserCondition = (RuleIrTypeCondition) children.get(0);
		verifyTypeCondition(irUserCondition, V_USER);

		final RuleIrAttributeRelCondition irCartUserRel = (RuleIrAttributeRelCondition) children.get(1);
		verifyAttributeRelCondition(irCartUserRel, V_CART,
				RuleConfigurableProductCustomersConditionTranslator.CART_RAO_USER_ATTRIBUTE, RuleIrAttributeOperator.EQUAL, V_USER);

		final RuleIrAttributeCondition irCustomersCondition = (RuleIrAttributeCondition) children.get(2);
		verifyAttributeCondition(irCustomersCondition, V_USER,
				RuleConfigurableProductCustomersConditionTranslator.USER_RAO_ID_ATTRIBUTE, attributeOperator, customerList);
	}

	private void verifyTypeCondition(final RuleIrTypeCondition irUserCondition, final String variable)
	{
		assertEquals(variable, irUserCondition.getVariable());
	}

	private void verifyAttributeCondition(final RuleIrAttributeCondition ruleIrCondition, final String variable,
			final String attribute, final RuleIrAttributeOperator operator, final Object value)
	{
		assertEquals(variable, ruleIrCondition.getVariable());
		assertEquals(attribute, ruleIrCondition.getAttribute());
		assertEquals(operator, ruleIrCondition.getOperator());
		assertEquals(value, ruleIrCondition.getValue());
	}

	private void verifyAttributeRelCondition(final RuleIrAttributeRelCondition ruleIrCondition, final String variable,
			final String attribute, final RuleIrAttributeOperator operator, final String targetVariable)
	{
		assertEquals(variable, ruleIrCondition.getVariable());
		assertEquals(attribute, ruleIrCondition.getAttribute());
		assertEquals(operator, ruleIrCondition.getOperator());
		assertEquals(targetVariable, ruleIrCondition.getTargetVariable());
	}

}
