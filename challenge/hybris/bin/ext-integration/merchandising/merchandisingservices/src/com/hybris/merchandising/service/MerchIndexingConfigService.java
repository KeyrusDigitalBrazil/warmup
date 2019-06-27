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
package com.hybris.merchandising.service;

import java.util.Collection;
import java.util.Optional;

import com.hybris.merchandising.model.MerchIndexingConfigModel;

/**
 * Service that provides basic functionality for merchandising listener configurations.
 */
public interface MerchIndexingConfigService
{
	/**
	 * Returns all merchandising listener configurations.
	 *
	 * @return list of merchandising configurations or empty list if no configuration is found
	 */
	Collection<MerchIndexingConfigModel> getAllMerchIndexingConfigs();

	/**
	 * Returns the merchandising listener configuration for a specific indexed type.
	 *
	 * @param indexedType
	 *           - the indexed type identifier
	 *
	 * @return the search configuration
	 */
	Optional<MerchIndexingConfigModel> getMerchIndexingConfigForIndexedType(final String indexedType);
}
