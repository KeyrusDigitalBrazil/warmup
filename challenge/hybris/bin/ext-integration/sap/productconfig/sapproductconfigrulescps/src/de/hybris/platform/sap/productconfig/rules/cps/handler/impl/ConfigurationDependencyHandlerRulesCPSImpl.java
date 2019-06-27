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
package de.hybris.platform.sap.productconfig.rules.cps.handler.impl;

import de.hybris.platform.sap.productconfig.rules.cps.handler.CharacteristicValueRulesResultHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDependencyHandler;

import org.springframework.beans.factory.annotation.Required;


public class ConfigurationDependencyHandlerRulesCPSImpl implements ConfigurationDependencyHandler
{

	CharacteristicValueRulesResultHandler rulesResultHandler;

	@Override
	public void copyProductConfigurationDependency(final String sourceConfigId, final String targetConfigId)
	{
		getRulesResultHandler().copyAndPersistRuleResults(sourceConfigId, targetConfigId);
	}

	protected CharacteristicValueRulesResultHandler getRulesResultHandler()
	{
		return rulesResultHandler;
	}

	@Required
	public void setRulesResultHandler(final CharacteristicValueRulesResultHandler rulesResultHandler)
	{
		this.rulesResultHandler = rulesResultHandler;
	}

}
