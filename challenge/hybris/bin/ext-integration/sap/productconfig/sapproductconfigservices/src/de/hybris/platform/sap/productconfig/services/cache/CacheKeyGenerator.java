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
package de.hybris.platform.sap.productconfig.services.cache;

import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;


/**
 * Generates cache keys to be used for hybris cache regions
 */
public interface CacheKeyGenerator
{
	/**
	 * Creates a cache key for the analytics data cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createAnalyticsDataCacheKey(final String configId);

	/**
	 * Creates a cache key for the price summary cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createPriceSummaryCacheKey(final String configId);

	/**
	 * Creates a cache key for the configuration cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createConfigCacheKey(final String configId);

	/**
	 * Creates a cache key for the classification system CPQ attributes cache region
	 *
	 * @param configId
	 *           configuration id
	 *
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createClassificationSystemCPQAttributesCacheKey(final String productCode);
}
