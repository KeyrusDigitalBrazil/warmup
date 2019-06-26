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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;

import java.util.Arrays;

import javax.annotation.Resource;

import org.apache.solr.common.params.FacetParams;
import org.junit.Test;


public class FacetParamsTest extends AbstractIntegrationTest
{
	private static final String MANUFACTURER_FIELD = "manufacturerName";
	private static final String MANUFACTURER_INTEL = "Intel";

	private static final int FACET_VALUES_SIZE = 18;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Test
	public void testFacetLimitParam1() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnline = catalogVersionService.getCatalogVersion(HW_CATALOG, ONLINE_CATALOG_VERSION);

		final int facetLimit = 10;

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Arrays.asList(hwOnline));
		query.addFacet(MANUFACTURER_FIELD);
		query.addRawParam(FacetParams.FACET_LIMIT, Integer.toString(facetLimit));

		final SearchResult result = facetSearchService.search(query);
		final Facet facet = result.getFacet(MANUFACTURER_FIELD);

		// then
		assertNotNull(facet);
		assertThat(facet.getFacetValues()).hasSize(facetLimit);
	}

	@Test
	public void testFacetLimitParam2() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnline = catalogVersionService.getCatalogVersion(HW_CATALOG, ONLINE_CATALOG_VERSION);

		final int facetLimit = 20;

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Arrays.asList(hwOnline));
		query.addFacet(MANUFACTURER_FIELD);
		query.addRawParam(FacetParams.FACET_LIMIT, Integer.toString(facetLimit));

		final SearchResult result = facetSearchService.search(query);
		final Facet facet = result.getFacet(MANUFACTURER_FIELD);

		// then
		assertNotNull(facet);
		assertThat(facet.getFacetValues()).hasSize(FACET_VALUES_SIZE);
	}

	@Test
	public void testFacetMinCountParam1() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnline = catalogVersionService.getCatalogVersion(HW_CATALOG, ONLINE_CATALOG_VERSION);

		final int facetMincount = 5;

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Arrays.asList(hwOnline));
		query.addFacet(MANUFACTURER_FIELD);
		query.addRawParam(FacetParams.FACET_MINCOUNT, Integer.toString(facetMincount));

		final SearchResult result = facetSearchService.search(query);
		final Facet facet = result.getFacet(MANUFACTURER_FIELD);

		// then
		assertNotNull(facet);
		assertThat(facet.getFacetValues()).hasSize(1).extracting("name").contains(MANUFACTURER_INTEL);
	}

	@Test
	public void testFacetMinCountParam2() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnline = catalogVersionService.getCatalogVersion(HW_CATALOG, ONLINE_CATALOG_VERSION);

		final int facetMincount = 10;

		// when
		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Arrays.asList(hwOnline));
		query.addFacet(MANUFACTURER_FIELD);
		query.addRawParam(FacetParams.FACET_MINCOUNT, Integer.toString(facetMincount));

		final SearchResult result = facetSearchService.search(query);
		final Facet facet = result.getFacet(MANUFACTURER_FIELD);

		// then
		assertNotNull(facet);
		assertThat(facet.getFacetValues()).isEmpty();
	}
}
