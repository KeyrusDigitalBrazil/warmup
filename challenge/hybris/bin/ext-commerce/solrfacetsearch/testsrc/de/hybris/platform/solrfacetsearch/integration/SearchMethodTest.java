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
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.QueryMethod;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.BoostField.BoostType;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.QueryOperator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@IntegrationTest
public class SearchMethodTest extends AbstractIntegrationTest
{
	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	private static final String PRODUCT_CODE_FIELD = "code";
	private static final String SCORE_FIELD = "score";

	@Override
	protected void loadData() throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchMethodTest.csv");
	}

	@Test
	public void emptyMethod() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		facetSearchConfig.getSolrConfig().setQueryMethod(null);

		// when
		final SearchResult searchResult = executeSearchQuery(facetSearchConfig);

		// then
		assertNotNull(searchResult);
		assertEquals(2, searchResult.getNumberOfResults());
	}

	@Test
	public void testGetMethod() throws Exception
	{// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		facetSearchConfig.getSolrConfig().setQueryMethod(QueryMethod.GET);

		// when
		final SearchResult searchResult = executeSearchQuery(facetSearchConfig);

		// then
		assertNotNull(searchResult);
		assertEquals(2, searchResult.getNumberOfResults());
	}

	@Test
	public void testPostMethod() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		facetSearchConfig.getSolrConfig().setQueryMethod(QueryMethod.POST);

		// when
		final SearchResult searchResult = executeSearchQuery(facetSearchConfig);

		// then
		assertNotNull(searchResult);
		assertEquals(2, searchResult.getNumberOfResults());
	}

	protected SearchResult executeSearchQuery(final FacetSearchConfig facetSearchConfig) throws Exception
	{
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.getFields().addAll(Arrays.asList(SCORE_FIELD, PRODUCT_CODE_FIELD));

		return facetSearchService.search(searchQuery);
	}
}
