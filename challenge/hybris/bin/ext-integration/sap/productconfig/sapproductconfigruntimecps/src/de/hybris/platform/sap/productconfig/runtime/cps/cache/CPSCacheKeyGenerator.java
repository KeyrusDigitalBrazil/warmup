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
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;


/**
 * Generates cache keys to be used for hybris cache regions in CPS context
 */
public interface CPSCacheKeyGenerator
{
	/**
	 * Creates a cache key for the master data cache region
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param lang
	 *           language
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createMasterDataCacheKey(final String kbId, final String lang);

	/**
	 * Creates a cache key for the knowledgebase headers cache region
	 *
	 * @param product
	 *           product for which knowledgebases are looked up
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createKnowledgeBaseHeadersCacheKey(final String product);

	/**
	 * Creates a cache key for the cookie cache region
	 *
	 * @param configId
	 *           configuration runtime id
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createCookieCacheKey(final String configId);

	/**
	 * Creates a cache key for the value prices cache region
	 *
	 * @param kbId
	 *           knowledgebase id for which all value prices are calculated
	 * @param pricingProduct
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createValuePricesCacheKey(final String kbId, String pricingProduct);

	/**
	 * Creates a cache key for the configuration cache region
	 *
	 * @param configId
	 *           configuration id
	 * @return the created cache key
	 */
	ProductConfigurationCacheKey createConfigurationCacheKey(String configId);
}
