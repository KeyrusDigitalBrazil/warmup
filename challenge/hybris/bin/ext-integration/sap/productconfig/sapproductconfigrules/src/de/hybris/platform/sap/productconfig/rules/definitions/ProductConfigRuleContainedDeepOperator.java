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
 * Enum representing possible operators used for customer group condition definition in context of the product config
 * rule module. A typical rule condition may looks like:<br>
 * Current web shop customer <code>[IS_CONTAINED_IN_ANY|IS_CONTAINED_IN_ALL|IS_NOT_CONTAINED_IN_ANY]</code> of the
 * following customer groups: <code>(GROUP_A, GROUP_B, GROUP_C)</code>
 */
public enum ProductConfigRuleContainedDeepOperator implements RuleParameterEnum
{
	/**
	 * Example: Current web shop customer 'Cony Consumer' <code>IS_CONTAINED_IN_ANY</code> of the following customer
	 * groups: <code>('Customer', 'Sales Agents', 'Administrators')</code>
	 */
	IS_CONTAINED_IN_ANY,
	/**
	 * Example: Current web shop customer 'Cony Consumer' <code>IS_CONTAINED_IN_ALL</code> of the following customer
	 * groups: <code>('Customer', 'Gold Status', 'Premium Delivery')</code>
	 */
	IS_CONTAINED_IN_ALL,
	/**
	 * Example: Current web shop customer 'Cony Consumer' <code>IS_CONTAINED_IN_ANY</code> of the following customer
	 * groups: <code>('Platinum Status', 'Premium Delivery')</code>
	 */
	IS_NOT_CONTAINED_IN_ANY
}
