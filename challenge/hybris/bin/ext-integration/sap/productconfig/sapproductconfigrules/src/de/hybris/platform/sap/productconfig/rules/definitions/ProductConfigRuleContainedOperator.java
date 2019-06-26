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
package de.hybris.platform.sap.productconfig.rules.definitions;

import de.hybris.platform.ruleengineservices.definitions.RuleParameterEnum;


/**
 * Enum representing possible operators used for customer condition definition in context of the product config rule
 * module. A typical rule condition may looks like:<br>
 * Current web shop customer <code>[IS_CONTAINED_IN|IS_NOT_CONTAINED_IN]</code> the following list of customers:
 * <code>(CUSTOMER_A, CUSTOMER_B, CUSTOMER_C)</code>
 */
public enum ProductConfigRuleContainedOperator implements RuleParameterEnum
{
	/**
	 * Example: Current web shop customer 'John Doe' <code>IS_CONTAINED_IN</code> the following list of customers:
	 * <code>('John Doe', 'Max Mueller', 'Cony Consumer')</code>
	 */
	IS_CONTAINED_IN,

	/**
	 * Example: Current web shop customer 'John Doe' <code>IS_NOT_CONTAINED_IN</code> the following list of customers:
	 * <code>('Max Mueller', 'Cony Consumer')</code>
	 */
	IS_NOT_CONTAINED_IN
}
