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
package de.hybris.platform.sap.productconfig.rules.cps.compiler.processors;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerException;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrProcessor;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;


/**
 * {@link RuleIrProcessor} for product configuration with CPS. Ensures CPS specific validation.
 */
public class ProductConfigRuleCPSIrProcessor implements RuleIrProcessor
{
	protected static final String CONDITION_CONFIGURABLE_PRODUCT_IN_CART = "y_configurable_product_in_cart";
	protected static final String ACTION_DISCOUNT_FOR_OPTION = "y_configurable_product_percentage_discount_for_option";
	protected static final String ACTION_PROMO_MESSAGE = "y_configurable_product_display_promo_message";
	protected static final String ACTION_PROMO_OPPORTUNITY_MESSAGE = "y_configurable_product_display_promo_opportunity_message";

	@Override
	public void process(final RuleCompilerContext context, final RuleIr ruleIr)
	{
		final AbstractRuleModel sourceRule = context.getRule();

		if (sourceRule instanceof ProductConfigSourceRuleModel)
		{
			final ProductConfigSourceRuleModel productConfigSourceRule = (ProductConfigSourceRuleModel) sourceRule;
			final String conditions = productConfigSourceRule.getConditions();
			final String actions = productConfigSourceRule.getActions();

			if (conditions.contains(CONDITION_CONFIGURABLE_PRODUCT_IN_CART) && (actions.contains(ACTION_DISCOUNT_FOR_OPTION)
					|| actions.contains(ACTION_PROMO_MESSAGE) || actions.contains(ACTION_PROMO_OPPORTUNITY_MESSAGE)))
			{
				throw new RuleCompilerException(
						"The combination of the \"Configurable product in the cart\" condition with promo actions is not supported");
			}
		}
	}
}
