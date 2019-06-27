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

import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.DocumentData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.SearchResultGroup;
import de.hybris.platform.solrfacetsearch.search.SearchResultGroupCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class SearchResponseResultsPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, SearchResult>, SearchPageData<ITEM>>
{
	private Converter<DocumentData, ITEM> searchResultConverter;

	protected Converter<DocumentData, ITEM> getSearchResultConverter()
	{
		return searchResultConverter;
	}

	@Required
	public void setSearchResultConverter(final Converter<DocumentData, ITEM> searchResultConverter)
	{
		this.searchResultConverter = searchResultConverter;
	}

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SearchQuery, INDEXED_TYPE_SORT_TYPE, de.hybris.platform.solrfacetsearch.search.SearchResult> source,
			final SearchPageData<ITEM> target)
	{
		target.setResults(buildResults(source.getSearchResult(), source.getRequest().getSearchQuery()));
	}

	protected List<ITEM> buildResults(final SearchResult searchResult, final SearchQuery searchQuery)
	{
		if (searchResult == null)
		{
			return Collections.emptyList();
		}

		final List<ITEM> result = new ArrayList<ITEM>(searchResult.getPageSize());

		if (CollectionUtils.isNotEmpty(searchResult.getGroupCommands()))
		{
			for (final SearchResultGroupCommand groupCommand : searchResult.getGroupCommands())
			{
				for (final SearchResultGroup group : groupCommand.getGroups())
				{
					result.add(convertGroupResultDocuments(searchQuery, group));
				}
			}
		}
		else
		{
			for (final Document document : searchResult.getDocuments())
			{
				result.add(convertResultDocument(searchQuery, document));
			}
		}

		return result;
	}

	protected ITEM convertResultDocument(final SearchQuery searchQuery, final Document document)
	{
		final DocumentData<SearchQuery, Document> documentData = createDocumentData();
		documentData.setSearchQuery(searchQuery);
		documentData.setDocument(document);
		return getSearchResultConverter().convert(documentData);
	}

	protected ITEM convertGroupResultDocuments(final SearchQuery searchQuery, final SearchResultGroup group)
	{
		final DocumentData<SearchQuery, Document> documentData = createDocumentData();
		documentData.setSearchQuery(searchQuery);
		int documentIndex = 0;
		final List<Document> variants = new ArrayList<>();

		for (final Document document : group.getDocuments())
		{
			if (documentIndex == 0)
			{
				documentData.setDocument(document);
			}
			else
			{
				variants.add(document);
			}
			documentIndex++;
		}

		documentData.setVariants(variants);
		return getSearchResultConverter().convert(documentData);
	}

	protected DocumentData<SearchQuery, Document> createDocumentData()
	{
		return new DocumentData<SearchQuery, Document>();
	}
}
