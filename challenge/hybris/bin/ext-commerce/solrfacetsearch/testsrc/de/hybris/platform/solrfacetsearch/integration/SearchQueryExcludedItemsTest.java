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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@IntegrationTest
public class SearchQueryExcludedItemsTest extends AbstractIntegrationTest
{
	@Resource
	private ProductService productService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	private static final String PRODUCT_CODE_FIELD = "code";

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchQueryExcludedItemsTest.csv");
	}

	@Test
	public void excludeResult1() throws Exception
	{
		// given
		final ProductModel product1 = productService.getProductForCode(PRODUCT1_CODE);

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addExcludedItem(product1.getPk());
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document);
	}

	@Test
	public void excludeResult2() throws Exception
	{
		// given
		final ProductModel product2 = productService.getProductForCode(PRODUCT2_CODE);

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addExcludedItem(product2.getPk());
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document);
	}

	@Test
	public void excludeMultipleResults() throws Exception
	{
		// given
		final ProductModel product1 = productService.getProductForCode(PRODUCT1_CODE);
		final ProductModel product2 = productService.getProductForCode(PRODUCT2_CODE);

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addExcludedItem(product1.getPk());
			searchQuery.addExcludedItem(product2.getPk());
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	protected SearchResult executeSearchQuery(final Consumer<SearchQuery> action) throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.getFields().addAll(Arrays.asList(PRODUCT_CODE_FIELD));

		action.accept(searchQuery);

		return facetSearchService.search(searchQuery);
	}

	protected void assertProductCode(final String expectedProductCode, final Document document)
	{
		assertEquals(expectedProductCode, document.getFields().get(PRODUCT_CODE_FIELD));
	}
}
