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
package de.hybris.platform.sap.productconfig.rules.action.strategy.impl;

import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.sap.productconfig.rules.action.strategy.ProductConfigRuleActionStrategyChecker;
import de.hybris.platform.sap.productconfig.rules.rao.action.SetCsticValueRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Encapsulates logic of setting characteristic value as rule action.
 */
public class SetCsticValueRuleActionStrategyImpl extends ProductConfigAbstractRuleActionStrategy
{

	private static final Logger LOG = Logger.getLogger(SetCsticValueRuleActionStrategyImpl.class);

	private static final String MODE_SET = "Setting single value";
	private static final String MODE_ADD = "Adding ";
	private static final String ACTION_DESCRIPTION = "Setting characteristic value";
	private static final String MSG_DEBUG_VALUE_SET = "%s value %s to characteristic %s";


	@Override
	protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final CsticModel cstic = getCstic(model, action, csticMap);
		final String csticValueName = getValueToSet(action, cstic);

		String mode;
		if (cstic.isMultivalued())
		{
			cstic.addValue(csticValueName);
			mode = MODE_ADD;
		}
		else
		{
			cstic.setSingleValue(csticValueName);
			mode = MODE_SET;
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format(MSG_DEBUG_VALUE_SET, mode, csticValueName, cstic.getName()));
		}
		return cstic.isChangedByFrontend();
	}


	@Override
	protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final ProductConfigRuleActionStrategyChecker checker = getRuleActionChecker();

		boolean actionApplicable = checker.checkCsticPartOfModel(model, action, ACTION_DESCRIPTION, csticMap);
		if (actionApplicable)
		{
			final String value = getValueToSet(action, getCstic(model, action, csticMap));
			actionApplicable &= checker.checkValueAssignable(model, action, value, ACTION_DESCRIPTION, csticMap);
			actionApplicable &= checker.checkValueForamtable(model, action, value, getRulesFormator(), ACTION_DESCRIPTION, csticMap);
		}

		return actionApplicable;
	}

	protected String getValueToSet(final AbstractRuleActionRAO action, final CsticModel cstic)
	{
		String csticValueName = ((SetCsticValueRAO) action).getValueNameToSet().getCsticValueName();
		csticValueName = getRulesFormator().formatForService(cstic, csticValueName);
		return csticValueName;
	}

}
