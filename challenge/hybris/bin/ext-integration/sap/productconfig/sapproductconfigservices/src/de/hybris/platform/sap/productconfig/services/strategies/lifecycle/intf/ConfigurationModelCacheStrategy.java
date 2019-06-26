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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * This strategy manages the caching of the product configuration model itself.
 */
public interface ConfigurationModelCacheStrategy
{

	/**
	 * Retrieves the configuration model engine state
	 *
	 * @param configId
	 *           id of the configuration
	 * @return Configuration model
	 */
	ConfigModel getConfigurationModelEngineState(String configId);

	/**
	 * Puts the given config model into the engine state read cache
	 *
	 * @param configId
	 *           unique config id
	 * @param configModel
	 *           model to cache
	 */
	void setConfigurationModelEngineState(String configId, ConfigModel configModel);

	/**
	 * purges the caches
	 */
	void purge();

	/**
	 * Clears the read cache for the configuration engine state and price summary states for the whole user session
	 *
	 * @deprecated since 18.11.0 - this method is obsolete because the key under which the configuration engine state and
	 *             price summary states have been saved consists of configuration id an user session id
	 */
	@Deprecated
	void removeConfigAttributeStates();

	/**
	 * Removes the given configuration engine state and price summary model from read cache for engine state
	 *
	 * @param configId
	 *           unique config id
	 */
	void removeConfigAttributeState(String configId);

}
