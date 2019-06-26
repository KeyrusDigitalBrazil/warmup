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
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.OrderField;
import de.hybris.platform.solrfacetsearch.search.OrderField.SortOrder;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.search.impl.populators.FacetSearchQueryHighlightingFieldsPopulator;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@IntegrationTest
public class SearchHighlightingFieldsTest extends AbstractIntegrationTest
{
	public static final String SEARCH_TERM_SHIRT = "shirt";
	public static final String SEARCH_TERM_TROUSERS = "trousers";
	public static final String SYNONYM_TERM_TROUSERS = "pants";
	public static final String SEARCH_TERM_RED = "red";
	public static final String LANGUAGE_EN = "en";
	public static final String NAME_FIELD = "name";
	public static final String DESCRIPTION_FIELD = "description";
	public static final String PRODUCT_CODE_FIELD = "code";

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private ConfigurationService configurationService;

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchHighlightingFieldsTest.csv");
	}

	@Test
	public void testWithoutHighlighting() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType,
				SEARCH_TERM_SHIRT);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.setLanguage(LANGUAGE_EN);
		searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		final List<Document> products = searchResult.getDocuments();
		assertEquals(2, products.size());
		assertEquals(products.get(0).getFieldValue(NAME_FIELD), "red shirt");
		assertEquals(products.get(1).getFieldValue(NAME_FIELD), "blue shirt");
	}

	@Test
	public void testEnableHighlightingDisableProperties() throws Exception
	{
		// given
		importConfig("/test/integration/SearchHighlightingFieldsTest_enableHighlightingDisableProperties.csv");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType,
				SEARCH_TERM_SHIRT);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.setLanguage(LANGUAGE_EN);
		searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		final List<Document> products = searchResult.getDocuments();
		assertEquals(2, products.size());
		assertEquals(products.get(0).getFieldValue(NAME_FIELD), "red shirt");
		assertEquals(products.get(1).getFieldValue(NAME_FIELD), "blue shirt");
	}

	@Test
	public void testDisableHighlightingEnableProperties() throws Exception
	{
		// given
		importConfig("/test/integration/SearchHighlightingFieldsTest_disableHighlightingEnableProperties.csv");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType,
				SEARCH_TERM_SHIRT);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.setLanguage(LANGUAGE_EN);
		searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		final List<Document> products = searchResult.getDocuments();
		assertEquals(2, products.size());
		assertEquals(products.get(0).getFieldValue(NAME_FIELD), "red shirt");
		assertEquals(products.get(1).getFieldValue(NAME_FIELD), "blue shirt");
	}

	@Test
	public void testHighlightingFreeTextSearch() throws Exception
	{
		// given
		importConfig("/test/integration/SearchHighlightingFieldsTest_enableHighlighting.csv");

		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType,
				SEARCH_TERM_SHIRT);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.setLanguage(LANGUAGE_EN);
		searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		final String expectedHighlightedTerm = getExpectedHighlightedSearchTerm(SEARCH_TERM_SHIRT);

		final List<Document> products = searchResult.getDocuments();
		assertEquals(2, products.size());
		assertEquals(products.get(0).getFieldValue(NAME_FIELD), "red " + expectedHighlightedTerm);
		assertEquals(products.get(0).getFieldValue(DESCRIPTION_FIELD), "red " + SEARCH_TERM_SHIRT);
		assertEquals(products.get(1).getFieldValue(NAME_FIELD), "blue " + expectedHighlightedTerm);
		assertEquals(products.get(1).getFieldValue(DESCRIPTION_FIELD), "blue " + SEARCH_TERM_SHIRT);
	}

	@Test
	public void testHighlightingFreeTextSearchWithSynonym() throws Exception
	{
		// given
		importConfig("/test/integration/SearchHighlightingFieldsTest_enableHighlighting.csv");

		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQuery(facetSearchConfig, indexedType,
				SEARCH_TERM_TROUSERS);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.setLanguage(LANGUAGE_EN);
		searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		final String expectedHighlightedTerm = getExpectedHighlightedSearchTerm(SYNONYM_TERM_TROUSERS);

		final List<Document> products = searchResult.getDocuments();
		assertEquals(3, products.size());
		assertEquals(products.get(0).getFieldValue(NAME_FIELD), expectedHighlightedTerm);
		assertEquals(products.get(0).getFieldValue(DESCRIPTION_FIELD), SYNONYM_TERM_TROUSERS);
		assertEquals(products.get(1).getFieldValue(NAME_FIELD), "red " + expectedHighlightedTerm);
		assertEquals(products.get(1).getFieldValue(DESCRIPTION_FIELD), "red " + SYNONYM_TERM_TROUSERS);
		assertEquals(products.get(2).getFieldValue(NAME_FIELD), "blue " + expectedHighlightedTerm);
		assertEquals(products.get(2).getFieldValue(DESCRIPTION_FIELD), "blue " + SYNONYM_TERM_TROUSERS);
	}

	@Test
	public void testHighlightingForQuery() throws Exception
	{
		// given
		importConfig("/test/integration/SearchHighlightingFieldsTest_enableHighlighting.csv");

		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(getFacetSearchConfig());

		// when
		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		searchQuery.setLanguage(LANGUAGE_EN);
		searchQuery.addQuery(NAME_FIELD, SEARCH_TERM_RED);
		searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);

		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		final String expectedHighlightedTerm = getExpectedHighlightedSearchTerm(SEARCH_TERM_RED);

		final List<Document> products = searchResult.getDocuments();
		assertEquals(2, products.size());
		assertEquals(products.get(0).getFieldValue(NAME_FIELD), expectedHighlightedTerm + " shirt");
		assertEquals(products.get(0).getFieldValue(DESCRIPTION_FIELD), SEARCH_TERM_RED + " shirt");
		assertEquals(products.get(1).getFieldValue(NAME_FIELD), expectedHighlightedTerm + " pants");
		assertEquals(products.get(1).getFieldValue(DESCRIPTION_FIELD), SEARCH_TERM_RED + " pants");
	}

	private String getExpectedHighlightedSearchTerm(final String searchTerm)
	{
		final Configuration configuration = configurationService.getConfiguration();
		final String preTag = configuration.getString(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_PRE);
		final String postTag = configuration.getString(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_POST);

		return preTag + searchTerm + postTag;
	}
}