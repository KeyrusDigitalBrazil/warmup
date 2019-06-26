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
package de.hybris.platform.sap.productconfig.services.ssc.strategies.lifecycle.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationDeepCopyHandler;

import org.springframework.beans.factory.annotation.Required;


public class DefaultConfigurationCopyStrategyImpl implements ConfigurationCopyStrategy
{

	private ConfigurationDeepCopyHandler configDeepCopyHandler;

	@Override
	public String deepCopyConfiguration(final String configId, final String productCode, final String externalConfiguration,
			final boolean force)
	{
		return getConfigDeepCopyHandler().deepCopyConfiguration(configId, productCode, externalConfiguration, force, null);
	}

	@Override
	public void finalizeClone(final AbstractOrderModel source, final AbstractOrderModel target)
	{
		//For SSC no finalize clone steps necessary
	}

	protected ConfigurationDeepCopyHandler getConfigDeepCopyHandler()
	{
		return configDeepCopyHandler;
	}

	@Required
	public void setConfigDeepCopyHandler(final ConfigurationDeepCopyHandler configDeepCopyHandler)
	{
		this.configDeepCopyHandler = configDeepCopyHandler;
	}


}
