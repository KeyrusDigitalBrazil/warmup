/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.dao;

import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Collection;
import java.util.Optional;

import com.hybris.merchandising.model.MerchIndexingConfigModel;

/**
 * The {@link MerchIndexingConfigModel} DAO.
 */
public interface MerchIndexingConfigDao
{
	/**
	 * Finds all merchandising listener configurations.
	 *
	 * @return list of merchandising configurations or empty list if no configuration is found
	 */
	Collection<MerchIndexingConfigModel> findAllMerchIndexingConfigs();

	/**
	 * Finds the merchandising listener configuration for a specific indexed type.
	 *
	 * @param indexedType
	 *           - the indexed type
	 *
	 * @return the search configuration
	 */
	Optional<MerchIndexingConfigModel> findMerchIndexingConfigByIndexedType(final SolrIndexedTypeModel indexedType);
}
