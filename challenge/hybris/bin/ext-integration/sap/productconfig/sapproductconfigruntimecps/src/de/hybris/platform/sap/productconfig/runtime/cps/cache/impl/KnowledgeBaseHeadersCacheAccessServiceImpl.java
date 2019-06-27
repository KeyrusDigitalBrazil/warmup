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
package de.hybris.platform.sap.productconfig.runtime.cps.cache.impl;

import de.hybris.platform.regioncache.CacheValueLoader;
import de.hybris.platform.regioncache.region.impl.EHCacheRegion;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.KnowledgeBaseHeadersCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link KnowledgeBaseHeadersCacheAccessService}. Uses {@link EHCacheRegion} for caching.
 * Caches list of KB Headers data per product, which means that KB existence checks are cached.
 */
public class KnowledgeBaseHeadersCacheAccessServiceImpl implements KnowledgeBaseHeadersCacheAccessService
{
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<CPSMasterDataKBHeaderInfo>> cache;
	private CacheValueLoader<List<CPSMasterDataKBHeaderInfo>> loader;
	private CPSCacheKeyGenerator keyGenerator;

	@Override
	public List<CPSMasterDataKBHeaderInfo> getKnowledgeBases(final String product)
	{
		final ProductConfigurationCacheKey cacheKey = getKeyGenerator().createKnowledgeBaseHeadersCacheKey(product);
		return getCache().getWithLoader(cacheKey, getLoader());
	}

	@Override
	public void clearCache()
	{
		getCache().clearCache();
	}

	@Override
	public ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<CPSMasterDataKBHeaderInfo>> getCache()
	{
		return cache;
	}

	/**
	 * @param cache
	 *           Cache region
	 */
	@Required
	public void setCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<CPSMasterDataKBHeaderInfo>> cache)
	{
		this.cache = cache;
	}

	protected CacheValueLoader<List<CPSMasterDataKBHeaderInfo>> getLoader()
	{
		return loader;
	}

	/**
	 * @param loader
	 *           Loader for reading KB data via REST
	 */
	@Required
	public void setLoader(final CacheValueLoader<List<CPSMasterDataKBHeaderInfo>> loader)
	{
		this.loader = loader;
	}

	protected CPSCacheKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	/**
	 * @param keyGenerator
	 *           the keyGenerator to set
	 */
	@Required
	public void setKeyGenerator(final CPSCacheKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}
}
