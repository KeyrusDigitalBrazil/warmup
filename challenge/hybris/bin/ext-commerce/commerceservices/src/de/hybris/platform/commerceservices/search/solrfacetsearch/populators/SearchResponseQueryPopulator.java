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

import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.IndexedPropertyValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.List;


/**
 */
public class SearchResponseQueryPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, SEARCH_QUERY_TYPE, SEARCH_RESULT_TYPE, ITEM> implements Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SEARCH_QUERY_TYPE, IndexedTypeSort, SEARCH_RESULT_TYPE>, FacetSearchPageData<SolrSearchQueryData, ITEM>>
{
	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, IndexedProperty, SEARCH_QUERY_TYPE, IndexedTypeSort, SEARCH_RESULT_TYPE> source,
			final FacetSearchPageData<SolrSearchQueryData, ITEM> target)
	{
		final SearchResult searchResult = (SearchResult) source.getSearchResult();
		final String currentNamedSortCode = retrieveCurrentNamedSortCode(searchResult);

		final SolrSearchQueryData solrSearchQueryData = buildSearchQueryData(source.getRequest().getSearchText(),
				source.getRequest().getSearchQueryData().getCategoryCode(),
				currentNamedSortCode, source.getRequest().getIndexedPropertyValues(),
				source.getRequest().getSearchQueryData().getSearchQueryContext());

		target.setCurrentQuery(solrSearchQueryData);
	}

	protected SolrSearchQueryData buildSearchQueryData(final String searchText, final String categoryCode,
			final String currentSortCode, final List<IndexedPropertyValueData<IndexedProperty>> indexedPropertyValues,
			final SearchQueryContext searchQueryContext)
	{
		final SolrSearchQueryData result = createSearchQueryData();
		result.setFreeTextSearch(searchText);
		result.setCategoryCode(categoryCode);
		result.setSearchQueryContext(searchQueryContext);
		result.setSort(currentSortCode);

		final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();

		if (indexedPropertyValues != null && !indexedPropertyValues.isEmpty())
		{
			for (final IndexedPropertyValueData<IndexedProperty> indexedPropertyValue : indexedPropertyValues)
			{
				final SolrSearchQueryTermData term = createSearchQueryTermData();
				term.setKey(indexedPropertyValue.getIndexedProperty().getName());
				term.setValue(indexedPropertyValue.getValue());
				terms.add(term);
			}
		}

		result.setFilterTerms(terms);

		return result;
	}

	protected SolrSearchQueryData createSearchQueryData()
	{
		return new SolrSearchQueryData();
	}

	protected String retrieveCurrentNamedSortCode(final SearchResult searchResult){
		return searchResult != null ?
				(searchResult.getCurrentNamedSort() != null ? searchResult.getCurrentNamedSort().getCode() : null)
				: null;
	}

	protected SolrSearchQueryTermData createSearchQueryTermData()
	{
		return new SolrSearchQueryTermData();
	}
}
