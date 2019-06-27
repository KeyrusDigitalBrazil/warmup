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

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;


/**
 * Resolves suitable search query templates names to be used in a specific context.
 */
public interface SearchQueryTemplateNameResolver
{
	/**
	 * Returns the search query template name to be used for a given {@link SearchQueryContext}.
	 *
	 * @param facetSearchConfig
	 *           - the facet search configuration
	 * @param indexedType
	 *           - the indexed type
	 * @param searchQueryContext
	 *           - the search query context
	 *
	 * @return the search query template name
	 */
	String resolveTemplateName(FacetSearchConfig facetSearchConfig, IndexedType indexedType,
			SearchQueryContext searchQueryContext);
}
