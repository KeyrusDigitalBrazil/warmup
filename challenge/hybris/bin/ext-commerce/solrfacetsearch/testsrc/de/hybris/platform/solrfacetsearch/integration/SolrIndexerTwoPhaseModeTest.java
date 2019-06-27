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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrIndexNotFoundException;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class SolrIndexerTwoPhaseModeTest extends AbstractIntegrationTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Resource
	private SolrIndexService solrIndexService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SolrIndexerTwoPhaseModeTest.csv");
	}

	@Test
	public void fullIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(2, searchResult.getNumberOfResults());
	}

	@Test
	public void fullIndexAndUpdate() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(facetSearchConfig);
		importConfig("/test/integration/SolrIndexerTwoPhaseModeTest_addProduct1.csv");
		indexerService.updateIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(3, searchResult.getNumberOfResults());
	}

	@Test
	public void fullIndexTwice() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(facetSearchConfig);
		importConfig("/test/integration/SolrIndexerTwoPhaseModeTest_addProduct1.csv");
		indexerService.updateIndex(facetSearchConfig);
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(3, searchResult.getNumberOfResults());
	}

	@Test
	public void fullIndexTwiceAndUpdate() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(facetSearchConfig);
		importConfig("/test/integration/SolrIndexerTwoPhaseModeTest_addProduct1.csv");
		indexerService.updateIndex(facetSearchConfig);
		indexerService.performFullIndex(facetSearchConfig);
		importConfig("/test/integration/SolrIndexerTwoPhaseModeTest_addProduct2.csv");
		indexerService.updateIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(4, searchResult.getNumberOfResults());
	}

	@Test
	public void noActiveIndexBeforeFullIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		// expect
		expectedException.expect(SolrIndexNotFoundException.class);

		// when
		solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());
	}

	@Test
	public void activeIndexFoundAfterFullIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		// when
		indexerService.performFullIndex(facetSearchConfig);
		final SolrIndexModel index = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());

		// then
		assertNotNull(index);
		assertNotNull(index.getFacetSearchConfig());
		assertEquals(facetSearchConfig.getName(), index.getFacetSearchConfig().getName());
		assertNotNull(index.getIndexedType());
		assertEquals(indexedType.getIdentifier(), index.getIndexedType().getIdentifier());
	}

	@Test
	public void activeIndexChangesAfterFullIndexTwice() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		// when
		indexerService.performFullIndex(facetSearchConfig);
		final SolrIndexModel index1 = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());
		indexerService.performFullIndex(facetSearchConfig);
		final SolrIndexModel index2 = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());

		// then
		assertNotNull(index1);
		assertNotNull(index1.getFacetSearchConfig());
		assertEquals(facetSearchConfig.getName(), index1.getFacetSearchConfig().getName());
		assertNotNull(index1.getIndexedType());
		assertEquals(indexedType.getIdentifier(), index1.getIndexedType().getIdentifier());

		assertNotNull(index2);
		assertNotNull(index2.getFacetSearchConfig());
		assertEquals(facetSearchConfig.getName(), index2.getFacetSearchConfig().getName());
		assertNotNull(index2.getIndexedType());
		assertEquals(indexedType.getIdentifier(), index2.getIndexedType().getIdentifier());

		assertNotEquals(index1.getQualifier(), index2.getQualifier());
	}

	@Test
	public void activeIndexChangesAfterFullIndexThreeTimes() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		// when
		indexerService.performFullIndex(facetSearchConfig);
		final SolrIndexModel index1 = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());
		indexerService.performFullIndex(facetSearchConfig);
		final SolrIndexModel index2 = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());
		indexerService.performFullIndex(facetSearchConfig);
		final SolrIndexModel index3 = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());

		// then
		assertNotNull(index1);
		assertNotNull(index1.getFacetSearchConfig());
		assertEquals(facetSearchConfig.getName(), index1.getFacetSearchConfig().getName());
		assertNotNull(index1.getIndexedType());
		assertEquals(indexedType.getIdentifier(), index1.getIndexedType().getIdentifier());

		assertNotNull(index2);
		assertNotNull(index2.getFacetSearchConfig());
		assertEquals(facetSearchConfig.getName(), index2.getFacetSearchConfig().getName());
		assertNotNull(index2.getIndexedType());
		assertEquals(indexedType.getIdentifier(), index2.getIndexedType().getIdentifier());

		assertNotNull(index3);
		assertNotNull(index3.getFacetSearchConfig());
		assertEquals(facetSearchConfig.getName(), index3.getFacetSearchConfig().getName());
		assertNotNull(index3.getIndexedType());
		assertEquals(indexedType.getIdentifier(), index3.getIndexedType().getIdentifier());


		assertNotEquals(index1.getQualifier(), index2.getQualifier());
		assertNotEquals(index2.getQualifier(), index3.getQualifier());
		assertEquals(index1.getQualifier(), index3.getQualifier());
	}
}
