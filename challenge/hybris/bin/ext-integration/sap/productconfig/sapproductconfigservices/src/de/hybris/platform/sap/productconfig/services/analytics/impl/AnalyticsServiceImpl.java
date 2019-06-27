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
package de.hybris.platform.sap.productconfig.services.analytics.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the{@link AnalyticsService}.<br>
 * While the atcual service call is delegated to the analytics provider, this service will cache the result within the
 * sesseion, to reduce number of actual service calls to a minimum.
 */
public class AnalyticsServiceImpl implements AnalyticsService
{
	private ProviderFactory providerFactory;
	private ProductConfigurationCacheAccessService productConfigurationCacheAccessService;
	private ConfigurationModelCacheStrategy configurationModelCacheStrategy;

	@Override
	public AnalyticsDocument getAnalyticData(final String configId)
	{
		final AnalyticsDocument analyticData = getProductConfigurationCacheAccessService().getAnalyticData(configId);
		if (null != analyticData)
		{
			return analyticData;
		}
		final ConfigModel configModel = getConfigurationModelCacheStrategy().getConfigurationModelEngineState(configId);
		final AnalyticsDocument analyticsDocument = getProviderFactory().getAnalyticsProvider().getPopularity(configModel);
		getProductConfigurationCacheAccessService().setAnalyticData(configId, analyticsDocument);
		return analyticsDocument;
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	@Override
	public boolean isActive()
	{
		return providerFactory.getAnalyticsProvider().isActive();
	}

	protected ConfigurationModelCacheStrategy getConfigurationModelCacheStrategy()
	{
		return configurationModelCacheStrategy;
	}

	@Required
	public void setConfigurationModelCacheStrategy(final ConfigurationModelCacheStrategy configurationModelCacheStrategy)
	{
		this.configurationModelCacheStrategy = configurationModelCacheStrategy;
	}

	protected ProductConfigurationCacheAccessService getProductConfigurationCacheAccessService()
	{
		return productConfigurationCacheAccessService;
	}

	@Required
	public void setProductConfigurationCacheAccessService(
			final ProductConfigurationCacheAccessService productConfigurationCacheAccessService)
	{
		this.productConfigurationCacheAccessService = productConfigurationCacheAccessService;
	}
}
