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
package de.hybris.platform.solrfacetsearch.search;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@IntegrationTest
public class SearchByPropertiesWithExportIdTest extends AbstractIntegrationTest
{
	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	/**
	 * Test solr configuration builds index for products from test catalog for two index properties:
	 *
	 * 1.) propertyName : code , exportId : code_id <br/>
	 * 2.) propertyName : name, exportId : null
	 */

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchByPropertiesWithExportIdTest.csv");
	}

	@Test
	public void testSearchingByFirstExportId() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(facetSearchConfig);

		// when
		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		query.addQuery("code", "product1" + getTestId());

		final SearchResult result = facetSearchService.search(query);

		// then
		assertThat(result.getResultCodes()).containsOnly("product1" + getTestId());
	}


	@Test
	public void testSearchingByName() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		indexerService.performFullIndex(facetSearchConfig);

		// when
		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(hwOnlineCatalogVersion));
		query.addQuery("name", "deutches");

		final SearchResult result = facetSearchService.search(query);

		// then
		assertThat(result.getResultCodes()).contains("product1" + getTestId(), "product2" + getTestId());
	}

}
