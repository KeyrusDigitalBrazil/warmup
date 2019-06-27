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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link ConfigurationModelCacheStrategy}. It uses the hybris caches to store any data
 * and hence delegates to the {@link CacheAccessService}.
 */
public class DefaultConfigurationModelCacheStrategyImpl extends SessionServiceAware implements ConfigurationModelCacheStrategy
{
	private ProductConfigurationCacheAccessService cacheAccessService;

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return getCacheAccessService().getConfigurationModelEngineState(configId);
	}

	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		getCacheAccessService().setConfigurationModelEngineState(configId, configModel);
	}

	@Override
	public void purge()
	{
		getSessionAccessService().purge();
	}

	/**
	 *
	 * @deprecated since 18.11.0 - this method is obsolete because the key under which the configuration engine state and
	 *             price summary states have been saved consists of configuration id an user session id
	 *
	 */
	@Deprecated
	@Override
	public void removeConfigAttributeStates()
	{
		getSessionAccessService().removeConfigAttributeStates();
	}

	@Override
	public void removeConfigAttributeState(final String configId)
	{
		getCacheAccessService().removeConfigAttributeState(configId);
	}

	protected ProductConfigurationCacheAccessService getCacheAccessService()
	{
		return cacheAccessService;
	}

	@Required
	public void setCacheAccessService(final ProductConfigurationCacheAccessService cacheAccessService)
	{
		this.cacheAccessService = cacheAccessService;
	}
}
