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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


/**
 * Tests the full text search function in the given catalog version.
 */
public class SearchFulltextTest extends AbstractIntegrationTest
{
	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchFulltextTest.csv");
	}

	/**
	 * Creates the index and searches for "canon" in Online catalog version. There should be 4 products that can be
	 * found, and each contains the keyword "canon"
	 *
	 * @throws Exception
	 */

	@Test
	public void testFulltextSearchEn() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(getFacetSearchConfig());

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.addQuery("name", "canon");

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(4, searchResult.getNumberOfResults());
	}

	@Test
	public void testFulltextSearchDe() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();

		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		// when
		indexerService.performFullIndex(getFacetSearchConfig());

		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("de"));
		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.addQuery("name", "canon");

		final SearchResult searchResult = facetSearchService.search(query);

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}
}
