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
package de.hybris.platform.solrfacetsearch.config;

public final class FacetSearchConfigs
{
	// Suppresses default constructor, ensuring non-instantiability.
	private FacetSearchConfigs()
	{
	}

	public static FacetSearchConfig createFacetSearchConfig(final String name, final String description,
			final IndexConfig indexConfig, final SearchConfig searchConfig, final SolrConfig solrConfig)
	{
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setName(name);
		facetSearchConfig.setDescription(description);
		facetSearchConfig.setIndexConfig(indexConfig);
		facetSearchConfig.setSearchConfig(searchConfig);
		facetSearchConfig.setSolrConfig(solrConfig);
		return facetSearchConfig;
	}
}
