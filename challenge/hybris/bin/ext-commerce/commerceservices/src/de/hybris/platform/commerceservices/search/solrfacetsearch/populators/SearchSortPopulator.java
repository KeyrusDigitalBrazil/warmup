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
package de.hybris.platform.commerceservices.search.solrfacetsearch.populators;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import org.apache.commons.lang.StringUtils;


/**
 */
public class SearchSortPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_PROPERTY_TYPE> implements
		Populator<SearchQueryPageableData<SolrSearchQueryData>, SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort>>
{
	@Override
	public void populate(final SearchQueryPageableData<SolrSearchQueryData> source,
			final SolrSearchRequest<FACET_SEARCH_CONFIG_TYPE, IndexedType, INDEXED_PROPERTY_TYPE, SearchQuery, IndexedTypeSort> target)
	{
		// Try to get the sort from the pageableData
		if (target.getPageableData() != null && StringUtils.isNotBlank(target.getPageableData().getSort()))
		{
			target.getSearchQuery().setNamedSort(target.getPageableData().getSort());
		}

		// Fall-back to the last sort used in the searchQueryData
		if (StringUtils.isBlank(target.getSearchQuery().getNamedSort())
				&& StringUtils.isNotBlank(target.getSearchQueryData().getSort()))
		{
			target.getSearchQuery().setNamedSort(target.getSearchQueryData().getSort());
		}
	}
}
