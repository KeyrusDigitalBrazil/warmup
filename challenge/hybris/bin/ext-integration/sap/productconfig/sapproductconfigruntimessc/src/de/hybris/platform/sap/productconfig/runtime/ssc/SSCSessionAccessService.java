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
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;


/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
public interface SSCSessionAccessService
{
	/**
	 * get the configuration provider for this session
	 *
	 * @return Configuration provider
	 */
	ConfigurationProvider getConfigurationProvider();

	/**
	 * cache the pricing provider in this session
	 *
	 * @param provider
	 *           provider to cache
	 */
	void setConfigurationProvider(ConfigurationProvider provider);
}
