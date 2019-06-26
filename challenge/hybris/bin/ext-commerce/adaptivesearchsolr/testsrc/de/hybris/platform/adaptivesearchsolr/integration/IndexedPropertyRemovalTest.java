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
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.daos.SolrIndexedPropertyDao;
import de.hybris.platform.solrfacetsearch.daos.SolrIndexedTypeDao;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class IndexedPropertyRemovalTest extends AbstractIntegrationTest
{
	private static final String INDEXED_TYPE_IDENTIFIER = "testIndexedType";

	private static final String CATEGORY_CODE_FIELD = "categoryCode";
	private static final String MANUFACTURER_NAME_FIELD = "manufacturerName";
	private static final String PRICE_FIELD = "price";
	private static final String PRODUCT_CODE_FIELD = "code";
	private static final String PRICE_VALUE_FIELD = "priceValue";

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

	@Resource
	private SolrIndexedTypeDao solrIndexedTypeDao;

	@Resource
	private SolrIndexedPropertyDao solrIndexedPropertyDao;

	@Resource
	private ModelService modelService;

	@Resource
	private FacetSearchConfigService facetSearchConfigService;

	private CatalogVersionModel catalogVersion;

	@Override
	public void loadData() throws Exception
	{
		importConfig("/adaptivesearchsolr/test/integration/indexedPropertyRemovalTest.impex");

		catalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG, ONLINE_CATALOG_VERSION + getTestId());

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		indexerService.performFullIndex(facetSearchConfig);
	}

	@Test
	public void calculateSearchProfile() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));

		// when
		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertNotNull(searchResult.getFacets());
		assertEquals(3, searchResult.getFacets().size());

		final Iterator<Facet> facetsIterator = searchResult.getFacets().iterator();

		final Facet facet1 = facetsIterator.next();
		assertEquals(CATEGORY_CODE_FIELD, facet1.getName());

		final Facet facet2 = facetsIterator.next();
		assertEquals(MANUFACTURER_NAME_FIELD, facet2.getName());

		final Facet facet3 = facetsIterator.next();
		assertEquals(PRICE_FIELD, facet3.getName());

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertEquals(PRODUCT2_CODE, document1.getFields().get(PRODUCT_CODE_FIELD));

		final Document document2 = searchResult.getDocuments().get(1);
		assertEquals(PRODUCT1_CODE, document2.getFields().get(PRODUCT_CODE_FIELD));
	}

	@Test
	public void calculateSearchProfileAfterRemovingPromotedFacet() throws Exception
	{
		// given
		removeIndexedProperty(CATEGORY_CODE_FIELD);

		// get non-cached facet search config
		final FacetSearchConfig facetSearchConfig = facetSearchConfigService.getConfiguration(getFacetSearchConfigName());
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));

		// when
		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertNotNull(searchResult.getFacets());
		assertEquals(2, searchResult.getFacets().size());

		final Iterator<Facet> facetsIterator = searchResult.getFacets().iterator();

		final Facet facet1 = facetsIterator.next();
		assertEquals(MANUFACTURER_NAME_FIELD, facet1.getName());

		final Facet facet2 = facetsIterator.next();
		assertEquals(PRICE_FIELD, facet2.getName());

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertEquals("product2", document1.getFields().get("code"));

		final Document document2 = searchResult.getDocuments().get(1);
		assertEquals("product1", document2.getFields().get("code"));
	}

	@Test
	public void calculateSearchProfileAfterRemovingFacet() throws Exception
	{
		// given
		removeIndexedProperty(PRICE_FIELD);

		final FacetSearchConfig facetSearchConfig = facetSearchConfigService.getConfiguration(getFacetSearchConfigName());
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));

		// when
		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertNotNull(searchResult.getFacets());
		assertEquals(2, searchResult.getFacets().size());

		final Iterator<Facet> facetsIterator = searchResult.getFacets().iterator();

		final Facet facet1 = facetsIterator.next();
		assertEquals(CATEGORY_CODE_FIELD, facet1.getName());

		final Facet facet2 = facetsIterator.next();
		assertEquals(MANUFACTURER_NAME_FIELD, facet2.getName());

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertEquals(PRODUCT2_CODE, document1.getFields().get(PRODUCT_CODE_FIELD));

		final Document document2 = searchResult.getDocuments().get(1);
		assertEquals(PRODUCT1_CODE, document2.getFields().get(PRODUCT_CODE_FIELD));
	}

	@Test
	public void calculateSearchProfileAfterRemovingBoostRule() throws Exception
	{
		// given
		removeIndexedProperty(PRICE_VALUE_FIELD);

		final FacetSearchConfig facetSearchConfig = facetSearchConfigService.getConfiguration(getFacetSearchConfigName());
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.setCatalogVersions(Collections.singletonList(catalogVersion));

		// when
		final SearchResult searchResult = facetSearchService.search(searchQuery);

		// then
		assertNotNull(searchResult.getFacets());
		assertEquals(3, searchResult.getFacets().size());

		final Iterator<Facet> facetsIterator = searchResult.getFacets().iterator();

		final Facet facet1 = facetsIterator.next();
		assertEquals(CATEGORY_CODE_FIELD, facet1.getName());

		final Facet facet2 = facetsIterator.next();
		assertEquals(MANUFACTURER_NAME_FIELD, facet2.getName());

		final Facet facet3 = facetsIterator.next();
		assertEquals(PRICE_FIELD, facet3.getName());

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertEquals(PRODUCT1_CODE, document1.getFields().get(PRODUCT_CODE_FIELD));

		final Document document2 = searchResult.getDocuments().get(1);
		assertEquals(PRODUCT2_CODE, document2.getFields().get(PRODUCT_CODE_FIELD));
	}

	protected void removeIndexedProperty(final String indexedProperty)
	{
		final SolrIndexedTypeModel solrIndexType = solrIndexedTypeDao.findIndexedTypeByIdentifier(getIndexedTypeIdentifier());
		final SolrIndexedPropertyModel solrIndexProperty = solrIndexedPropertyDao.findIndexedPropertyByName(solrIndexType,
				indexedProperty);

		final List<SolrIndexedPropertyModel> indexedProperties = new ArrayList<>(solrIndexType.getSolrIndexedProperties());
		indexedProperties.remove(solrIndexProperty);
		solrIndexType.setSolrIndexedProperties(indexedProperties);
		modelService.save(solrIndexType);
		modelService.remove(solrIndexProperty);
	}

	protected String getIndexedTypeIdentifier()
	{
		return INDEXED_TYPE_IDENTIFIER + getTestId();
	}
}
