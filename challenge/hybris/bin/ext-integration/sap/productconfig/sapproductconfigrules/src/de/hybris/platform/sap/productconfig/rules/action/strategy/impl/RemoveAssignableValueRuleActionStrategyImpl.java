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
import de.hybris.platform.sap.productconfig.rules.rao.action.RemoveAssignableValueRAO;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;

import java.util.Map;


/**
 * Encapsulates logic of removing of characteristic assignable value as rule action.
 */
public class RemoveAssignableValueRuleActionStrategyImpl extends ProductConfigAbstractRuleActionStrategy
{
	private static final String ACTION_DESCRIPTION = "Remove assignable characteristic value";


	@Override
	protected boolean isActionPossible(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final ProductConfigRuleActionStrategyChecker checker = getRuleActionChecker();

		boolean actionApplicable = checker.checkCsticPartOfModel(model, action, ACTION_DESCRIPTION, csticMap);
		if (actionApplicable)
		{
			actionApplicable = checker.checkValueUnassigned(model, action, getValueName(action), ACTION_DESCRIPTION, csticMap);
		}
		return actionApplicable;
	}

	@Override
	protected boolean executeAction(final ConfigModel model, final AbstractRuleActionRAO action,
			final Map<String, CsticModel> csticMap)
	{
		final String valueName = ((RemoveAssignableValueRAO) action).getValueNameToRemoveFromAssignable().getCsticValueName();
		getCstic(model, action, csticMap).removeAssignableValue(valueName);
		return false;
	}

	protected String getValueName(final AbstractRuleActionRAO action)
	{
		final RemoveAssignableValueRAO removeAssignableValueAction = (RemoveAssignableValueRAO) action;
		return removeAssignableValueAction.getValueNameToRemoveFromAssignable().getCsticValueName();
	}
}
