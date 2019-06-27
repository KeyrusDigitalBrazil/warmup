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
package de.hybris.platform.adaptivesearchsolr.integration;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class SolrAsSearchTest extends AbstractIntegrationTest
{
	private static final String PRODUCT_CODE_FIELD = "code";

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/adaptivesearchsolr/test/integration/solrAsSearchTest.impex");
	}

	@Test
	public void searchWithProfile1() throws Exception
	{
		// given
		importConfig("/adaptivesearchsolr/test/integration/solrAsSearchTest_withProfile1.impex");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void searchWithProfile2() throws Exception
	{
		// given
		importConfig("/adaptivesearchsolr/test/integration/solrAsSearchTest_withProfile2.impex");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT1_CODE, document2);
	}

	protected void assertProductCode(final String expectedProductCode, final Document document)
	{
		assertEquals(expectedProductCode, document.getFields().get(PRODUCT_CODE_FIELD));
	}
}
