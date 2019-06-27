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
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;

import java.util.List;


/**
 * Creates the intermediate representation of the Configurable Product in the Cart condition
 */
public class RuleConfigurableProductInCartConditionTranslator extends RuleConfigurableProductBaseConditionTranslator
{

	static final String CART_RAO_ENTRIES_ATTRIBUTE = "entries";
	static final String ORDER_ENTRY_RAO_PRODUCT_CONFIGURATION_ATTRIBUTE = "productConfiguration";

	@Override
	public RuleIrCondition translate(final RuleCompilerContext context, final RuleConditionData condition,
			final RuleConditionDefinitionData conditionDefinition)
	{
		// Parameters
		final String product = getProduct(condition);
		if (product == null || product.trim().isEmpty())
		{
			return new RuleIrFalseCondition();
		}

		// Variables
		final RuleIrLocalVariablesContainer variablesContainer = context.createLocalContainer();
		final String productConfigurationRaoVariable = context.generateLocalVariable(variablesContainer, ProductConfigRAO.class);
		final String csticValueRaoVariable = context.generateLocalVariable(variablesContainer, CsticValueRAO.class);
		final String csticRaoVariable = context.generateLocalVariable(variablesContainer, CsticRAO.class);
		final String orderEntryRaoVariable = context.generateLocalVariable(variablesContainer, OrderEntryRAO.class);
		final String cartRaoVariable = context.generateVariable(CartRAO.class);

		// Prepare Product Configuration relevant conditions
		final List<RuleIrCondition> irConditions = prepareProductConfigurationConditions(condition, product, Boolean.TRUE,
				productConfigurationRaoVariable, csticRaoVariable, csticValueRaoVariable);

		// Order Entry
		final RuleIrAttributeRelCondition irOrderEntryProductConfigRel = new RuleIrAttributeRelCondition();
		irOrderEntryProductConfigRel.setVariable(orderEntryRaoVariable);
		irOrderEntryProductConfigRel.setAttribute(ORDER_ENTRY_RAO_PRODUCT_CONFIGURATION_ATTRIBUTE);
		irOrderEntryProductConfigRel.setOperator(RuleIrAttributeOperator.EQUAL);
		irOrderEntryProductConfigRel.setTargetVariable(productConfigurationRaoVariable);

		// Cart
		final RuleIrAttributeRelCondition irCartOrderEntryRel = new RuleIrAttributeRelCondition();
		irCartOrderEntryRel.setVariable(cartRaoVariable);
		irCartOrderEntryRel.setAttribute(CART_RAO_ENTRIES_ATTRIBUTE);
		irCartOrderEntryRel.setOperator(RuleIrAttributeOperator.CONTAINS);
		irCartOrderEntryRel.setTargetVariable(orderEntryRaoVariable);

		irConditions.add(irOrderEntryProductConfigRel);
		irConditions.add(irCartOrderEntryRel);

		// Result condition
		final RuleIrExistsCondition irResultCondition = new RuleIrExistsCondition();
		irResultCondition.setVariablesContainer(variablesContainer);
		irResultCondition.setChildren(irConditions);

		return irResultCondition;
	}

}
