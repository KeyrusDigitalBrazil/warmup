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
package de.hybris.platform.commerceservices.search.solrfacetsearch.querybuilder;

import de.hybris.platform.solrfacetsearch.search.SearchQuery;


/**
 * Interface used by the DefaultSearchFacade to allow the free text query to be built from a number of beans.
 *
 * @deprecated Since 6.4, default search mode (instead of legacy) should be used.
 */
@Deprecated
public interface FreeTextQueryBuilder
{
	/**
	 * Add a free text query to the search query.
	 *
	 * @param searchQuery
	 *           The search query to add search terms to
	 * @param fullText
	 *           The full text of the query
	 * @param textWords
	 *           The full text query split into words
	 */
	void addFreeTextQuery(final SearchQuery searchQuery, final String fullText, final String[] textWords);
}
