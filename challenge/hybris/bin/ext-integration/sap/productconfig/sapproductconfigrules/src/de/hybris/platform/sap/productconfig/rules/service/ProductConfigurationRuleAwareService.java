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
package de.hybris.platform.sap.productconfig.rules.service;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;


/**
 * ProductConfigurationRuleAwareService provides access to the rule based specific functionality of the configuration
 * engine implementation.
 *
 */
public interface ProductConfigurationRuleAwareService extends ProductConfigurationService
{

	/**
	 * Retrieve the actual configuration model for the requested <code>configId</code> in the <code>ConfigModel</code>
	 * format bypassing rule evaluation.
	 *
	 * @param configId
	 *           Unique configuration ID
	 * @return The actual configuration
	 */
	ConfigModel retrieveConfigurationModelBypassRules(String configId);

	/**
	 * Creates the <code>ConfigModel</code> from the given <code>externalConfiguration</code> bypassing rule evaluation.
	 *
	 * @param kbKey
	 *           Key attributes needed to create a model
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @return The actual configuration
	 */
	ConfigModel createConfigurationFromExternalBypassRules(final KBKey kbKey, String externalConfiguration);
}
