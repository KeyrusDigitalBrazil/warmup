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
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;

import java.util.List;


/**
 * Creates the intermediate representation of the current Configurable Product condition
 */
public class RuleConfigurableProductConditionTranslator extends RuleConfigurableProductBaseConditionTranslator
{
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

		// Prepare Product Configuration relevant conditions
		final List<RuleIrCondition> irConditions = prepareProductConfigurationConditions(condition, product, Boolean.FALSE,
				productConfigurationRaoVariable, csticRaoVariable, csticValueRaoVariable);

		// Result condition
		final RuleIrExistsCondition irResultCondition = new RuleIrExistsCondition();
		irResultCondition.setVariablesContainer(variablesContainer);
		irResultCondition.setChildren(irConditions);

		return irResultCondition;
	}

}
