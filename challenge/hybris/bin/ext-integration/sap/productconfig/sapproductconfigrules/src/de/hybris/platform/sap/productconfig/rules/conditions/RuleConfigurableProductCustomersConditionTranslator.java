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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Creates the intermediate representation of the Configurable Product Customers condition
 */
public class RuleConfigurableProductCustomersConditionTranslator implements RuleConditionTranslator
{

	static final String CUSTOMERS_OPERATOR_PARAM = "customers_operator";
	static final String CUSTOMERS_PARAM = "customers";

	static final String USER_RAO_ID_ATTRIBUTE = "pk";
	static final String CART_RAO_USER_ATTRIBUTE = "user";


	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition)
	{
		final ProductConfigRuleContainedOperator customersOperator = retrieveCustomersOperator(condition);
		final List<String> customers = retrieveCustomers(condition);

		if (customersOperator == null || (CollectionUtils.isEmpty(customers)))
		{
			return new RuleIrFalseCondition();
		}


		final String userRaoVariable = context.generateVariable(UserRAO.class);
		final String cartRaoVariable = context.generateVariable(CartRAO.class);

		final List<RuleIrCondition> irConditions = new ArrayList<>();

		final RuleIrTypeCondition irUserCondition = new RuleIrTypeCondition();
		irUserCondition.setVariable(userRaoVariable);

		final RuleIrAttributeRelCondition irCartUserRel = new RuleIrAttributeRelCondition();
		irCartUserRel.setVariable(cartRaoVariable);
		irCartUserRel.setAttribute(CART_RAO_USER_ATTRIBUTE);
		irCartUserRel.setOperator(RuleIrAttributeOperator.EQUAL);
		irCartUserRel.setTargetVariable(userRaoVariable);

		final RuleIrAttributeCondition irCustomersCondition = new RuleIrAttributeCondition();
		irCustomersCondition.setVariable(userRaoVariable);
		irCustomersCondition.setAttribute(USER_RAO_ID_ATTRIBUTE);

		if (ProductConfigRuleContainedOperator.IS_CONTAINED_IN.equals(customersOperator))
		{
			irCustomersCondition.setOperator(RuleIrAttributeOperator.IN);
		}
		else
		{
			irCustomersCondition.setOperator(RuleIrAttributeOperator.NOT_IN);
		}

		irCustomersCondition.setValue(customers);

		irConditions.add(irUserCondition);
		irConditions.add(irCartUserRel);
		irConditions.add(irCustomersCondition);

		final RuleIrGroupCondition irResultCondition = new RuleIrGroupCondition();
		irResultCondition.setOperator(RuleIrGroupOperator.AND);
		irResultCondition.setChildren(irConditions);

		return irResultCondition;
	}

	protected ProductConfigRuleContainedOperator retrieveCustomersOperator(final RuleConditionData condition)
	{
		ProductConfigRuleContainedOperator customersOperator = null;
		final RuleParameterData customersOperatorParameter = condition.getParameters().get(CUSTOMERS_OPERATOR_PARAM);
		if (customersOperatorParameter != null)
		{
			customersOperator = customersOperatorParameter.getValue();
		}
		return customersOperator;
	}

	protected List<String> retrieveCustomers(final RuleConditionData condition)
	{
		List<String> customers = null;
		final RuleParameterData customersParameter = condition.getParameters().get(CUSTOMERS_PARAM);
		if (customersParameter != null)
		{
			customers = customersParameter.getValue();
		}
		return customers;
	}

}
