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

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionTranslator;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Creates the intermediate representation of the Configurable Product Customer Groups condition
 */
public class RuleConfigurableProductCustomerGroupsConditionTranslator implements RuleConditionTranslator
{
	static final String CUSTOMER_GROUPS_OPERATOR_PARAM = "customer_groups_operator";
	static final String CUSTOMER_GROUPS_PARAM = "customer_groups";

	static final String USER_RAO_GROUPS_ATTRIBUTE = "groups";
	static final String USER_GROUP_RAO_ID_ATTRIBUTE = "id";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition)
	{
		final ProductConfigRuleContainedDeepOperator customerGroupsOperator = retrieveCustomerGroupsOperator(condition);
		final List<String> customerGroups = retrieveCustomerGroups(condition);

		if (customerGroupsOperator == null || (CollectionUtils.isEmpty(customerGroups)))
		{
			return new RuleIrFalseCondition();
		}

		final String userRaoVariable = context.generateVariable(UserRAO.class);
		final String cartRaoVariable = context.generateVariable(CartRAO.class);

		final List<RuleIrCondition> irConditions = new ArrayList<RuleIrCondition>();

		final RuleIrTypeCondition irUserCondition = new RuleIrTypeCondition();
		irUserCondition.setVariable(userRaoVariable);

		final RuleIrAttributeRelCondition irCartUserRel = new RuleIrAttributeRelCondition();
		irCartUserRel.setVariable(cartRaoVariable);
		irCartUserRel.setAttribute(RuleConfigurableProductCustomersConditionTranslator.CART_RAO_USER_ATTRIBUTE);
		irCartUserRel.setOperator(RuleIrAttributeOperator.EQUAL);
		irCartUserRel.setTargetVariable(userRaoVariable);

		irConditions.add(irUserCondition);
		irConditions.add(irCartUserRel);

		RuleIrCondition irCustomerGroupsResultCondition = null;

		final RuleIrGroupCondition irResultCondition = new RuleIrGroupCondition();
		irResultCondition.setOperator(RuleIrGroupOperator.AND);

		switch (customerGroupsOperator)
		{
			case IS_CONTAINED_IN_ALL:
				irCustomerGroupsResultCondition = prepareContainsAllCustomerGroupConditions(context, customerGroups);
				irConditions.add(irCustomerGroupsResultCondition);
				irResultCondition.setChildren(irConditions);
				break;
			case IS_NOT_CONTAINED_IN_ANY:
				irCustomerGroupsResultCondition = prepareCustomerInAnyGroupConditions(context, customerGroups);
				irConditions.add(irCustomerGroupsResultCondition);
				addNotCondition(irConditions, irResultCondition);
				break;
			case IS_CONTAINED_IN_ANY:
				irCustomerGroupsResultCondition = prepareCustomerInAnyGroupConditions(context, customerGroups);
				irConditions.add(irCustomerGroupsResultCondition);
				irResultCondition.setChildren(irConditions);
				break;
			default:
				throw new IllegalStateException("Unsupported customer group operator: " + customerGroupsOperator.name());
		}

		return irResultCondition;
	}

	protected void addNotCondition(final List<RuleIrCondition> irConditions, final RuleIrGroupCondition irResultCondition)
	{
		final RuleIrNotCondition irNotCondition = new RuleIrNotCondition();
		irNotCondition.setChildren(irConditions);
		irResultCondition.setChildren(Arrays.asList(irNotCondition));
	}

	protected ProductConfigRuleContainedDeepOperator retrieveCustomerGroupsOperator(final RuleConditionData condition)
	{
		ProductConfigRuleContainedDeepOperator customerGroupsOperator = null;
		final RuleParameterData customerGroupsOperatorParameter = condition.getParameters().get(CUSTOMER_GROUPS_OPERATOR_PARAM);
		if (customerGroupsOperatorParameter != null)
		{
			customerGroupsOperator = customerGroupsOperatorParameter.getValue();
		}
		return customerGroupsOperator;
	}

	protected List<String> retrieveCustomerGroups(final RuleConditionData condition)
	{
		List<String> customerGroups = null;
		final RuleParameterData customerGroupsParameter = condition.getParameters().get(CUSTOMER_GROUPS_PARAM);
		if (customerGroupsParameter != null)
		{
			customerGroups = customerGroupsParameter.getValue();
		}
		return customerGroups;
	}

	protected RuleIrExistsCondition prepareCustomerInAnyGroupConditions(final RuleCompilerContext context,
			final List<String> customerGroups)
	{
		final String userRaoVariable = context.generateVariable(UserRAO.class);

		final RuleIrLocalVariablesContainer variablesContainer = context.createLocalContainer();
		final String userGroupRaoVariable = context.generateLocalVariable(variablesContainer, UserGroupRAO.class);

		final List<RuleIrCondition> irCustomerGroupsConditionList = new ArrayList<>();

		final RuleIrAttributeCondition irUserGroupCondition = new RuleIrAttributeCondition();
		irUserGroupCondition.setVariable(userGroupRaoVariable);
		irUserGroupCondition.setAttribute(USER_GROUP_RAO_ID_ATTRIBUTE);
		irUserGroupCondition.setOperator(RuleIrAttributeOperator.IN);
		irUserGroupCondition.setValue(customerGroups);

		final RuleIrAttributeRelCondition irUserUserGroupRel = new RuleIrAttributeRelCondition();
		irUserUserGroupRel.setVariable(userRaoVariable);
		irUserUserGroupRel.setAttribute(USER_RAO_GROUPS_ATTRIBUTE);
		irUserUserGroupRel.setOperator(RuleIrAttributeOperator.CONTAINS);
		irUserUserGroupRel.setTargetVariable(userGroupRaoVariable);

		irCustomerGroupsConditionList.add(irUserGroupCondition);
		irCustomerGroupsConditionList.add(irUserUserGroupRel);

		final RuleIrExistsCondition irCustomerGroupsResultCondition = new RuleIrExistsCondition();
		irCustomerGroupsResultCondition.setVariablesContainer(variablesContainer);
		irCustomerGroupsResultCondition.setChildren(irCustomerGroupsConditionList);

		return irCustomerGroupsResultCondition;

	}

	protected RuleIrGroupCondition prepareContainsAllCustomerGroupConditions(final RuleCompilerContext context,
			final List<String> customerGroups)
	{
		final List<RuleIrCondition> irCustomerGroupsConditionList = new ArrayList<>();

		final String userRaoVariable = context.generateVariable(UserRAO.class);

		for (final String customerGroup : customerGroups)
		{
			final RuleIrLocalVariablesContainer variablesContainer = context.createLocalContainer();
			final String containsUserGroupRaoVariable = context.generateLocalVariable(variablesContainer, UserGroupRAO.class);

			final RuleIrAttributeCondition irContainsUserGroupCondition = new RuleIrAttributeCondition();
			irContainsUserGroupCondition.setVariable(containsUserGroupRaoVariable);
			irContainsUserGroupCondition.setAttribute(USER_GROUP_RAO_ID_ATTRIBUTE);
			irContainsUserGroupCondition.setOperator(RuleIrAttributeOperator.EQUAL);
			irContainsUserGroupCondition.setValue(customerGroup);

			final RuleIrAttributeRelCondition irContainsUserUserGroupRel = new RuleIrAttributeRelCondition();
			irContainsUserUserGroupRel.setVariable(userRaoVariable);
			irContainsUserUserGroupRel.setAttribute(USER_RAO_GROUPS_ATTRIBUTE);
			irContainsUserUserGroupRel.setOperator(RuleIrAttributeOperator.CONTAINS);
			irContainsUserUserGroupRel.setTargetVariable(containsUserGroupRaoVariable);

			final RuleIrExistsCondition irContainsCustomerGroupCondition = new RuleIrExistsCondition();
			irContainsCustomerGroupCondition.setVariablesContainer(variablesContainer);
			irContainsCustomerGroupCondition.setChildren(Arrays.asList(irContainsUserGroupCondition, irContainsUserUserGroupRel));

			irCustomerGroupsConditionList.add(irContainsCustomerGroupCondition);
		}

		final RuleIrGroupCondition irCustomerGroupsResultCondition = new RuleIrGroupCondition();
		irCustomerGroupsResultCondition.setOperator(RuleIrGroupOperator.AND);
		irCustomerGroupsResultCondition.setChildren(irCustomerGroupsConditionList);

		return irCustomerGroupsResultCondition;
	}

}
