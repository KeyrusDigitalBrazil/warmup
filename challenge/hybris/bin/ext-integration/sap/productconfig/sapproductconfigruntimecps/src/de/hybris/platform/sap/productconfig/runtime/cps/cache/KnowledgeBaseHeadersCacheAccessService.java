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

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.common.CPSMasterDataKBHeaderInfo;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;

import java.util.List;


/**
 * Facilitates cache access for KB Header Lists
 */
public interface KnowledgeBaseHeadersCacheAccessService
{
	/**
	 * Retrieves knowledgebase headers for a specified product
	 *
	 * @param product
	 *           product code
	 * @return list of KB Header Info for product
	 */
	List<CPSMasterDataKBHeaderInfo> getKnowledgeBases(final String product);

	/**
	 * Access the Cache Region object
	 *
	 * @return CacheRegion object
	 */
	ProductConfigurationCacheAccess<ProductConfigurationCacheKey, List<CPSMasterDataKBHeaderInfo>> getCache();

	/**
	 * Clears the cache
	 */
	void clearCache();

}
