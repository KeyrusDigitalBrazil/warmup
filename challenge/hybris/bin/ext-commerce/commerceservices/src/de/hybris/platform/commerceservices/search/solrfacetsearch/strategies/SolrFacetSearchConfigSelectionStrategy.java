/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.search.solrfacetsearch.strategies;

import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;



/**
 * Resolves suitable {@link SolrFacetSearchConfigModel} that should be used for searching in the current session
 * context.<br>
 * 
 *
 * 
 */
public interface SolrFacetSearchConfigSelectionStrategy
{

	/**
	 * Resolves suitable {@link SolrFacetSearchConfigModel} that should be used for searching in the current session
	 * 
	 * @return {@link SolrFacetSearchConfigModel}
	 * @throws NoValidSolrConfigException
	 */
	SolrFacetSearchConfigModel getCurrentSolrFacetSearchConfig() throws NoValidSolrConfigException;

}
