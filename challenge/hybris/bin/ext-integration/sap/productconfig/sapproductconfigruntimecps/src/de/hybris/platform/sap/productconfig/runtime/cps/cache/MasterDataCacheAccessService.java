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

import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationCacheAccess;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.impl.ProductConfigurationCacheKey;


/**
 * Facilitates cache access for master data
 */
public interface MasterDataCacheAccessService
{
	/**
	 * Retrieves the knowledgebase container for a given id and language
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param language
	 *           language code
	 * @return knowledgebase for id and language code
	 */
	CPSMasterDataKnowledgeBaseContainer getKbContainer(final String kbId, final String language);

	/**
	 * Access the Cache Region object
	 *
	 * @return ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> object
	 */
	ProductConfigurationCacheAccess<ProductConfigurationCacheKey, CPSMasterDataKnowledgeBaseContainer> getCache();

	/**
	 * Clears the cache
	 */
	void clearCache();

	/**
	 * Removes knowledgebase from cache
	 *
	 * @param kbId
	 *           knowledgebase id
	 * @param language
	 *           language code
	 */
	void removeKbContainer(String kbId, final String language);

}
