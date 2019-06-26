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
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.UserGroupRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.sap.productconfig.rules.definitions.ProductConfigRuleContainedDeepOperator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class RuleConfigurableProductCustomerGroupsConditionTranslatorTest
{
	public static final String V_USER = "$v_User";
	public static final String V_CART = "$v_Cart";
	public static final String V_USER_GROUP = "$v_UserGroup";
	private RuleConfigurableProductCustomerGroupsConditionTranslator classUnderTest;

	@Mock
	private RuleCompilerContext context;
	@Mock
	private RuleConditionData condition;
	@Mock
	private RuleIrLocalVariablesContainer variablesContainer;

	private RuleConditionDefinitionData conditionDefinition;

	Map<String, RuleParameterData> ruleParameters;

	final List<String> customerGroupList = Arrays.asList("customerGroup1", "customerGroup2");


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new RuleConfigurableProductCustomerGroupsConditionTranslator();

		conditionDefinition = new RuleConditionDefinitionData();

		ruleParameters = new HashMap<String, RuleParameterData>();

		final RuleParameterData customerGroupsParameter = new RuleParameterData();
		customerGroupsParameter.setValue(customerGroupList);
		ruleParameters.put(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_PARAM, customerGroupsParameter);

		given(condition.getParameters()).willReturn(ruleParameters);

		given(context.generateVariable(UserRAO.class)).willReturn(V_USER);
		given(context.generateVariable(CartRAO.class)).willReturn(V_CART);
		given(context.createLocalContainer()).willReturn(variablesContainer);
		given(context.generateLocalVariable(variablesContainer, UserGroupRAO.class)).willReturn(V_USER_GROUP);
	}

	@Test
	public void testPrepareCustomerInAnyGroupConditions()
	{
		final RuleIrExistsCondition irCustomerGroupsResultCondition = classUnderTest.prepareCustomerInAnyGroupConditions(context,
				customerGroupList);
		assertNotNull(irCustomerGroupsResultCondition);

		final List<RuleIrCondition> children = irCustomerGroupsResultCondition.getChildren();
		assertEquals(2, children.size());

		final RuleIrAttributeCondition irUserGroupCondition = (RuleIrAttributeCondition) children.get(0);
		verifyAttributeCondition(irUserGroupCondition, V_USER_GROUP,
				RuleConfigurableProductCustomerGroupsConditionTranslator.USER_GROUP_RAO_ID_ATTRIBUTE, RuleIrAttributeOperator.IN,
				customerGroupList);

		final RuleIrAttributeRelCondition irUserUserGroupRel = (RuleIrAttributeRelCondition) children.get(1);
		verifyAttributeRelCondition(irUserUserGroupRel, V_USER,
				RuleConfigurableProductCustomerGroupsConditionTranslator.USER_RAO_GROUPS_ATTRIBUTE, RuleIrAttributeOperator.CONTAINS,
				V_USER_GROUP);
	}

	@Test
	public void testPrepareCustomerInAllGroupConditions()
	{
		final RuleIrGroupCondition irCustomerGroupsResultCondition = classUnderTest
				.prepareContainsAllCustomerGroupConditions(context, customerGroupList);
		assertNotNull(irCustomerGroupsResultCondition);

		final List<RuleIrCondition> children = irCustomerGroupsResultCondition.getChildren();
		assertEquals(2, children.size());
		assertEquals(RuleIrGroupOperator.AND, irCustomerGroupsResultCondition.getOperator());

		final RuleIrExistsCondition irContainsCustomerGroupCondition1 = (RuleIrExistsCondition) children.get(0);
		validateiContainsCustomerGroupCondition(irContainsCustomerGroupCondition1, "customerGroup1");

		final RuleIrExistsCondition irContainsCustomerGroupCondition2 = (RuleIrExistsCondition) children.get(1);
		validateiContainsCustomerGroupCondition(irContainsCustomerGroupCondition2, "customerGroup2");

	}

	private void validateiContainsCustomerGroupCondition(final RuleIrExistsCondition irContainsCustomerGroupCondition,
			final String group)
	{
		assertNotNull(irContainsCustomerGroupCondition);
		final List<RuleIrCondition> children = irContainsCustomerGroupCondition.getChildren();
		assertEquals(2, children.size());

		final RuleIrAttributeCondition irContainsUserGroupCondition = (RuleIrAttributeCondition) children.get(0);
		verifyAttributeCondition(irContainsUserGroupCondition, V_USER_GROUP,
				RuleConfigurableProductCustomerGroupsConditionTranslator.USER_GROUP_RAO_ID_ATTRIBUTE, RuleIrAttributeOperator.EQUAL,
				group);

		final RuleIrAttributeRelCondition irContainsUserUserGroupRel = (RuleIrAttributeRelCondition) children.get(1);
		verifyAttributeRelCondition(irContainsUserUserGroupRel, V_USER,
				RuleConfigurableProductCustomerGroupsConditionTranslator.USER_RAO_GROUPS_ATTRIBUTE, RuleIrAttributeOperator.CONTAINS,
				V_USER_GROUP);
	}

	@Test
	public void testTranslateContainedInAny() throws RuleCompilerException
	{
		final RuleParameterData customerGroupsOperatorParameter = new RuleParameterData();
		customerGroupsOperatorParameter.setValue(ProductConfigRuleContainedDeepOperator.IS_CONTAINED_IN_ANY);
		ruleParameters.put(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_OPERATOR_PARAM,
				customerGroupsOperatorParameter);

		final RuleIrGroupCondition irResultCondition = (RuleIrGroupCondition) classUnderTest.translate(context, condition,
				conditionDefinition);

		assertNotNull(irResultCondition);
		final List<RuleIrCondition> children = irResultCondition.getChildren();

		verifyTranslateIn(children);
	}

	@Test
	public void testTranslateContainedInAll() throws RuleCompilerException
	{
		final RuleParameterData customerGroupsOperatorParameter = new RuleParameterData();
		customerGroupsOperatorParameter.setValue(ProductConfigRuleContainedDeepOperator.IS_CONTAINED_IN_ALL);
		ruleParameters.put(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_OPERATOR_PARAM,
				customerGroupsOperatorParameter);

		final RuleIrGroupCondition irResultCondition = (RuleIrGroupCondition) classUnderTest.translate(context, condition,
				conditionDefinition);

		assertNotNull(irResultCondition);
		final List<RuleIrCondition> children = irResultCondition.getChildren();

		verifyTranslateIn(children);
	}

	@Test
	public void testTranslateNotContainedInAny() throws RuleCompilerException
	{
		final RuleParameterData customerGroupsOperatorParameter = new RuleParameterData();
		customerGroupsOperatorParameter.setValue(ProductConfigRuleContainedDeepOperator.IS_NOT_CONTAINED_IN_ANY);
		ruleParameters.put(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_OPERATOR_PARAM,
				customerGroupsOperatorParameter);

		final RuleIrGroupCondition irResultCondition = (RuleIrGroupCondition) classUnderTest.translate(context, condition,
				conditionDefinition);

		assertNotNull(irResultCondition);
		List<RuleIrCondition> children = irResultCondition.getChildren();
		assertEquals(1, children.size());

		final RuleIrNotCondition irNotCondition = (RuleIrNotCondition) children.get(0);
		assertNotNull(irNotCondition);
		children = irNotCondition.getChildren();

		verifyTranslateIn(children);
	}

	protected void verifyTranslateIn(final List<RuleIrCondition> children) throws RuleCompilerException
	{
		assertEquals(3, children.size());

		final RuleIrTypeCondition irUserCondition = (RuleIrTypeCondition) children.get(0);
		verifyTypeCondition(irUserCondition, V_USER);

		final RuleIrAttributeRelCondition irCartUserRel = (RuleIrAttributeRelCondition) children.get(1);
		verifyAttributeRelCondition(irCartUserRel, V_CART,
				RuleConfigurableProductCustomersConditionTranslator.CART_RAO_USER_ATTRIBUTE, RuleIrAttributeOperator.EQUAL, V_USER);

		final RuleIrCondition irCustomerGroupsResultCondition = children.get(2);
		assertNotNull(irCustomerGroupsResultCondition);
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

	@Test
	public void testTranslateWithoutCustomerGroupParam()
	{
		ruleParameters.remove(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_OPERATOR_PARAM);

		final RuleIrCondition result = classUnderTest.translate(context, condition, conditionDefinition);
		assertEquals(RuleIrFalseCondition.class, result.getClass());
	}

	@Test
	public void testTranslateWithEmptyCustomerGroups()
	{
		final RuleParameterData customerGroupsParameter = new RuleParameterData();
		customerGroupsParameter.setValue(emptyList());
		ruleParameters.put(RuleConfigurableProductCustomerGroupsConditionTranslator.CUSTOMER_GROUPS_PARAM, customerGroupsParameter);

		final RuleIrCondition result = classUnderTest.translate(context, condition, conditionDefinition);
		assertEquals(RuleIrFalseCondition.class, result.getClass());
	}
}
