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
package de.hybris.platform.sap.productconfig.rules.action.strategy;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Defines a strategy that encapsulates the logic of a rule action.
 */
public interface ProductConfigRuleActionStrategy
{
	/**
	 * Applies the action described by the given {@link AbstractRuleActionRAO}.
	 *
	 * @param model
	 *           product configuration model to be adjusted
	 * @param action
	 *           the action to apply
	 * @return true if model is adjusted
	 */
	boolean apply(ConfigModel model, AbstractRuleActionRAO action);
}
