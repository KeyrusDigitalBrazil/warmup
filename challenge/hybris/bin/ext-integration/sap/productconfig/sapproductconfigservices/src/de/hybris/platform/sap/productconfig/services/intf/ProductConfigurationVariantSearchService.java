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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;

import java.util.List;


/**
 * Service for searching variants.
 */
public interface ProductConfigurationVariantSearchService
{
	/**
	 * Searches variants that are similar to the currently configured product identified by the given config id.
	 *
	 * @param configId
	 *           configuration id of current configuration session
	 * @param productCode
	 *           product code of the currently configured product
	 * @return A List of variants that are similar to current configured product
	 */
	List<VariantSearchResult> getVariantsForConfiguration(String configId, String productCode);

	/**
	 * Searches variants that are similar to the currently configured product identified by the given config id.
	 *
	 * @param searchQueryData
	 *           search query to be used
	 * @return A List of variants that are similar to current configured product
	 */
	List<VariantSearchResult> getVariantsForCustomQuery(SolrSearchQueryData searchQueryData);
}
