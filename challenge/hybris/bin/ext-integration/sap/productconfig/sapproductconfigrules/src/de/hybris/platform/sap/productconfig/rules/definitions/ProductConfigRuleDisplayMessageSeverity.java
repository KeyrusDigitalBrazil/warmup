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
 * Enum representing possible severities used for product config rule messages.
 */
public enum ProductConfigRuleDisplayMessageSeverity implements RuleParameterEnum
{
	/**
	 * Related message will be shown as a warning
	 */
	WARNING,
	/**
	 * Related message will be shown as an info message
	 */
	INFO
}
