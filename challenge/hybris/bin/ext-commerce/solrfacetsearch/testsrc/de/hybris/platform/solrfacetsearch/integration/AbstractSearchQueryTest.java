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
package de.hybris.platform.solrfacetsearch.integration;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.function.Consumer;

import javax.annotation.Resource;


public abstract class AbstractSearchQueryTest extends AbstractIntegrationTest
{
	protected static final String PRODUCT_CODE_FIELD = "code";
	protected static final String PRODUCT_NAME_FIELD = "name";
	protected static final String PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD = "name, 1766";
	protected static final String PRODUCT_MANUFACTURER_NAME_FIELD = "manufacturerName";
	protected static final String PRODUCT_APPROVAL_STATUS_FIELD = "approvalStatus";

	protected static final String PRODUCT1_CODE = "product1";
	protected static final String PRODUCT1_NAME = "product 1 name";

	protected static final String PRODUCT2_CODE = "product2";
	protected static final String PRODUCT2_NAME = "product 2 name";

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	protected SearchResult executeSearchQuery() throws Exception
	{
		return executeSearchQuery(searchQuery -> {
			// NOOP
		});
	}

	protected SearchResult executeSearchQuery(final Consumer<SearchQuery> action) throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		action.accept(searchQuery);

		return facetSearchService.search(searchQuery);
	}

	protected void enabledSearchLegacyMode() throws Exception
	{
		importConfig("/test/solrSearchConfig_enableLegacyMode.csv");
	}

	protected void assertDocumentField(final Object expectedValue, final Document document, final String field)
	{
		assertEquals(expectedValue, document.getFields().get(field));
	}
}
