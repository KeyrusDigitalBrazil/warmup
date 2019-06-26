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
 * Enum representing possible operators used for product config rule condition definition. A typical rule condition may
 * looks like:<br>
 * Cstic 'XY' <code>[HAS/DOES_NOT_HAVE]</code> value 'xyz'.
 */
public enum ProductConfigRuleValueOperator implements RuleParameterEnum
{
	/**
	 * Example: Cstic 'Color' <code>HAS</code> value 'blue'.
	 */
	HAS,
	/**
	 * Example: Cstic 'Color' <code>DOES_NOT_HAVE</code> value 'red'.
	 */
	DOES_NOT_HAVE
}
