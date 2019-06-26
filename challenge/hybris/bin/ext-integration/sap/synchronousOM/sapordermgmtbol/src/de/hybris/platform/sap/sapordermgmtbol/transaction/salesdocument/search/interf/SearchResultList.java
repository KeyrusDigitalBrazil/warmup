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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;

import java.util.List;


/**
 * 
 */
public interface SearchResultList
{

	/**
	 * Adds a search result item
	 * 
	 * @param result
	 *           Search result
	 */
	void add(SearchResult result);

	/**
	 * Returns total number of search results
	 * 
	 * @return Total number of search results
	 */
	int size();

	/**
	 * Clears search result list
	 */
	void clear();

	/**
	 * Sets paging data that we need for accessing the result list
	 * 
	 * @param pageableData
	 */
	void setPageableData(PageableData pageableData);

	/**
	 * Returns the result list, taking the paging data into account. In case no paging data is provided, a runtime
	 * exception is thrown.
	 * 
	 * @return Search result list
	 */
	List<SearchResult> getSearchResult();

}
