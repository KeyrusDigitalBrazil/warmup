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
package de.hybris.platform.commerceservices.search.flexiblesearch;

import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.List;
import java.util.Map;


/**
 * The PagedFlexibleSearchService interface supports executing flexible search queries returning paginated results. The
 * current page, page size, and sort are specified using the {@link PageableData} parameter. The results are returned in
 * a parametrized {@link SearchPageData} result which includes the search results for the requested page, the pagination
 * data, and the available sort options.
 *
 * @deprecated Since 6.6. Use {@link de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService}
 *             instead
 */
@Deprecated
public interface PagedFlexibleSearchService
{
	/**
	 * Searches according to a flexible search query and pagination data
	 * 
	 * @param searchQuery
	 *           the flexible search query
	 * @param pageableData
	 *           the object containing pagination data
	 * @return an instance of {@link SearchPageData} containing the search results with sort and pagination data
	 */
	<T> SearchPageData<T> search(FlexibleSearchQuery searchQuery, PageableData pageableData);

	/**
	 * Searches according to a query string and pagination data
	 * 
	 * @param query
	 *           the query string
	 * @param queryParams
	 *           the query parameters
	 * @param pageableData
	 *           the object containing pagination data
	 * @return an instance of {@link SearchPageData} containing the search results with sort and pagination data
	 */
	<T> SearchPageData<T> search(String query, Map<String, ?> queryParams, PageableData pageableData);

	/**
	 * Searches according to a sort query and pagination data
	 * 
	 * @param sortQueries
	 *           the sort queries
	 * @param defaultSortCode
	 *           the default sort code
	 * @param queryParams
	 *           the query parameters
	 * @param pageableData
	 *           the object containing pagination data
	 * @return an instance of {@link SearchPageData} containing the search results with sort and pagination data
	 */
	<T> SearchPageData<T> search(List<SortQueryData> sortQueries, String defaultSortCode, Map<String, ?> queryParams,
			PageableData pageableData);
}
