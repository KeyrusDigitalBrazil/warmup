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
package de.hybris.platform.sap.productconfig.services.cache.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.cache.CacheKeyGenerator;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Required;


public class ProductConfigurationCacheAccessServiceImpl implements ProductConfigurationCacheAccessService
{
	private CacheKeyGenerator keyGenerator;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, AnalyticsDocument> analyticsCache;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, PriceSummaryModel> priceSummaryCache;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, ConfigModel> configCache;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<String, ClassificationSystemCPQAttributesContainer>> classificationSystemCPQAttributesCache;

	@Override
	public void setAnalyticData(final String configId, final AnalyticsDocument analyticsDocument)
	{
		getAnalyticsCache().put(getKeyGenerator().createAnalyticsDataCacheKey(configId), analyticsDocument);
	}

	@Override
	public AnalyticsDocument getAnalyticData(final String configId)
	{
		return getAnalyticsCache().get(getKeyGenerator().createAnalyticsDataCacheKey(configId));
	}

	@Override
	public PriceSummaryModel getPriceSummaryState(final String configId)
	{
		return getPriceSummaryCache().get(getKeyGenerator().createPriceSummaryCacheKey(configId));
	}

	@Override
	public void setPriceSummaryState(final String configId, final PriceSummaryModel priceSummaryModel)
	{
		getPriceSummaryCache().put(getKeyGenerator().createPriceSummaryCacheKey(configId), priceSummaryModel);
	}

	@Override
	public ConfigModel getConfigurationModelEngineState(final String configId)
	{
		return getConfigCache().get(getKeyGenerator().createConfigCacheKey(configId));
	}

	@Override
	public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
	{
		getConfigCache().put(getKeyGenerator().createConfigCacheKey(configId), configModel);
	}

	@Override
	public void removeConfigAttributeState(final String configId)
	{
		getPriceSummaryCache().remove(getKeyGenerator().createPriceSummaryCacheKey(configId));
		getAnalyticsCache().remove(getKeyGenerator().createAnalyticsDataCacheKey(configId));
		getConfigCache().remove(getKeyGenerator().createConfigCacheKey(configId));
	}

	@Override
	public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(final String productCode)
	{
		final ProductConfigurationCacheKey cacheKey = getKeyGenerator()
				.createClassificationSystemCPQAttributesCacheKey(productCode);
		Map<String, ClassificationSystemCPQAttributesContainer> cachedValue = getClassificationSystemCPQAttributesCache()
				.get(cacheKey);
		if (null == cachedValue)
		{
			getClassificationSystemCPQAttributesCache().putIfAbsent(cacheKey, new ConcurrentHashMap<>());
			cachedValue = getClassificationSystemCPQAttributesCache().get(cacheKey);
		}
		return cachedValue;
	}

	protected CacheKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final CacheKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, AnalyticsDocument> getAnalyticsCache()
	{
		return analyticsCache;
	}

	@Required
	public void setAnalyticsCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, AnalyticsDocument> analyticsCache)
	{
		this.analyticsCache = analyticsCache;
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, PriceSummaryModel> getPriceSummaryCache()
	{
		return priceSummaryCache;
	}

	@Required
	public void setPriceSummaryCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, PriceSummaryModel> priceSummaryCache)
	{
		this.priceSummaryCache = priceSummaryCache;
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, ConfigModel> getConfigCache()
	{
		return configCache;
	}

	@Required
	public void setConfigCache(final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, ConfigModel> configCache)
	{
		this.configCache = configCache;
	}

	protected ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<String, ClassificationSystemCPQAttributesContainer>> getClassificationSystemCPQAttributesCache()
	{
		return classificationSystemCPQAttributesCache;
	}

	@Required
	public void setClassificationSystemCPQAttributesCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, Map<String, ClassificationSystemCPQAttributesContainer>> classificationSystemCPQAttributesCache)
	{
		this.classificationSystemCPQAttributesCache = classificationSystemCPQAttributesCache;
	}

}

