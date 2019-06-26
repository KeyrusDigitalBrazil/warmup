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
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCacheKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link MasterDataCacheAccessService}. Uses {@link ProductConfigurationCacheAccess} for
 * caching. Caches KB data per id and language, which means that master data attributes like characteristic types e.g.
 * are cached redundantly.
 */
public class MasterDataCacheAccessServiceImpl implements MasterDataCacheAccessService
{
	private CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> loader;
	private CPSCacheKeyGenerator keyGenerator;
	private ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> cache;

	@Override
	public CPSMasterDataKnowledgeBaseContainer getKbContainer(final String kbId, final String language)
	{
		final ProductConfigurationCacheKey cacheKey = getKeyGenerator().createMasterDataCacheKey(kbId, language);

		return getCache().getWithLoader(cacheKey, getLoader());
	}

	@Override
	public void clearCache()
	{
		getCache().clearCache();
	}

	protected CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> getLoader()
	{
		return loader;
	}

	/**
	 * @param loader
	 *           Loader for reading KB data via REST
	 */
	@Required
	public void setLoader(final CacheValueLoader<CPSMasterDataKnowledgeBaseContainer> loader)
	{
		this.loader = loader;
	}

	protected CPSCacheKeyGenerator getKeyGenerator()
	{
		return keyGenerator;
	}

	@Required
	public void setKeyGenerator(final CPSCacheKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	@Override
	public ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> getCache()
	{
		return cache;
	}

	@Required
	public void setCache(
			final ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> cache)
	{
		this.cache = cache;
	}

	@Override
	public void removeKbContainer(final String kbId, final String language)
	{
		getCache().remove(getKeyGenerator().createMasterDataCacheKey(kbId, language));
	}

}
