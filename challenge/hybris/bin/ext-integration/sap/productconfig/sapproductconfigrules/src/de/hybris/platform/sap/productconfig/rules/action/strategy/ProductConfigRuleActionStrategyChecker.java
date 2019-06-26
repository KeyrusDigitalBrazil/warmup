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
import de.hybris.platform.sap.productconfig.rules.service.ProductConfigRuleFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Map;


/**
 * helper class for rule based action strategies. provides common used functionality
 */
public interface ProductConfigRuleActionStrategyChecker
{

	/**
	 * extracts the cstic from action raos
	 *
	 * @param model
	 *           config model
	 * @param action
	 *           rao action
	 * @param csticMap
	 *           cached cstics
	 * @return cstic model referenced by the rao action
	 */
	CsticModel getCstic(ConfigModel model, AbstractRuleActionRAO action, Map<String, CsticModel> csticMap);

	/**
	 * checks whether the provided cstic is valid in context of the given model
	 *
	 * @param model
	 *           config model
	 * @param action
	 *           rao action
	 * @param actionDescription
	 *           text describing the action, used for logging
	 * @param csticMap
	 *           cached cstics
	 * @return <code>true</code>, only if the cstic the action refers to is still part of the config model
	 */
	boolean checkCsticPartOfModel(ConfigModel model, AbstractRuleActionRAO action, String actionDescription,
			Map<String, CsticModel> csticMap);

	/**
	 * checks whether the given value is already assigned
	 *
	 * @param model
	 *           config model
	 * @param action
	 *           rao actio
	 * @param value
	 *           cstic value name
	 * @param actionDescription
	 *           text describing the action, used for logging
	 * @param csticMap
	 *           cached cstics
	 * @return <code>true</code>, only if the given cstic value is already assigned to the cstic referenced by the action
	 */
	boolean checkValueUnassigned(ConfigModel model, AbstractRuleActionRAO action, String value, String actionDescription,
			Map<String, CsticModel> csticMap);

	/**
	 * checks whether the given value can be assigned
	 *
	 * @param model
	 *           config model
	 * @param action
	 *           rao action
	 * @param value
	 *           cstic value name
	 * @param actionDescription
	 *           text describing the action, used for logging
	 * @param csticMap
	 *           cached cstics
	 * @return <code>true</code>, only if the given cstic value can be assigned to the cstic referenced by the action
	 */
	boolean checkValueAssignable(ConfigModel model, AbstractRuleActionRAO action, String value, String actionDescription,
			Map<String, CsticModel> csticMap);

	/**
	 * checks whether the value can be parsed/formatted
	 *
	 * @param model
	 *           config model
	 * @param action
	 *           rao action
	 * @param valueToSet
	 *           ctsic value name
	 * @param rulesFormator
	 *           formator instance to be used
	 * @param actionDescription
	 *           text describing the action, used for logging
	 * @param csticMap
	 *           cached cstics
	 * @return <code>true</code>, only if the given cstic value has the expected format as defined by the cstic
	 *         referenced by the action
	 */
	boolean checkValueForamtable(ConfigModel model, AbstractRuleActionRAO action, String valueToSet,
			ProductConfigRuleFormatTranslator rulesFormator, String actionDescription, Map<String, CsticModel> csticMap);

}
