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
package de.hybris.platform.solrfacetsearch.solr.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrIndexNotFoundException;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultSolrIndexServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final String FACET_SEARCH_CONFIG1_NAME = "testFacetSearchConfig1";
	private static final String INDEXED_TYPE1_NAME = "testIndexedType1";

	private static final String FACET_SEARCH_CONFIG2_NAME = "testFacetSearchConfig2";
	private static final String INDEXED_TYPE2_NAME = "testIndexedType2";

	private static final String QUALIFIER_1 = "1";
	private static final String QUALIFIER_2 = "2";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private SolrIndexService solrIndexService;

	@Resource
	private FacetSearchConfigService facetSearchConfigService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/integration/DefaultSolrIndexServiceIntegrationTest.csv", DEFAULT_ENCODING);
	}

	@Test
	public void createIndex() throws Exception
	{
		// when
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// then
		assertNotNull(index);
		assertEquals(FACET_SEARCH_CONFIG1_NAME, index.getFacetSearchConfig().getName());
		assertEquals(INDEXED_TYPE1_NAME, index.getIndexedType().getIdentifier());
		assertEquals(QUALIFIER_1, index.getQualifier());
		assertFalse(index.isActive());
	}

	@Test
	public void createDuplicateIndex() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// expect
		expectedException.expect(SolrServiceException.class);

		// when
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
	}

	@Test
	public void getAllIndexesEmpty() throws Exception
	{
		// when
		final List<SolrIndexModel> indexes = solrIndexService.getAllIndexes();

		// then
		assertThat(indexes, empty());
	}

	@Test
	public void getAllIndexes() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_2);
		solrIndexService.createIndex(FACET_SEARCH_CONFIG2_NAME, INDEXED_TYPE2_NAME, QUALIFIER_1);
		solrIndexService.createIndex(FACET_SEARCH_CONFIG2_NAME, INDEXED_TYPE2_NAME, QUALIFIER_2);

		// when
		final List<SolrIndexModel> indexes = solrIndexService.getAllIndexes();

		// then
		assertThat(indexes, not(empty()));
		assertEquals(4, indexes.size());
	}

	@Test
	public void getIndexesForConfigAndTypeEmpty() throws Exception
	{
		// when
		final List<SolrIndexModel> indexes = solrIndexService.getIndexesForConfigAndType(FACET_SEARCH_CONFIG1_NAME,
				INDEXED_TYPE1_NAME);

		// then
		assertThat(indexes, empty());
	}

	@Test
	public void getAllIndexesForConfigAndType() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig1 = facetSearchConfigService.getConfiguration(FACET_SEARCH_CONFIG1_NAME);
		final IndexedType indexedType1 = facetSearchConfig1.getIndexConfig().getIndexedTypes().values().iterator().next();

		final FacetSearchConfig facetSearchConfig2 = facetSearchConfigService.getConfiguration(FACET_SEARCH_CONFIG2_NAME);
		final IndexedType indexedType2 = facetSearchConfig2.getIndexConfig().getIndexedTypes().values().iterator().next();

		solrIndexService.createIndex(facetSearchConfig1.getName(), indexedType1.getIdentifier(), QUALIFIER_2);
		solrIndexService.createIndex(facetSearchConfig2.getName(), indexedType2.getIdentifier(), QUALIFIER_1);
		solrIndexService.createIndex(facetSearchConfig2.getName(), indexedType2.getIdentifier(), QUALIFIER_2);

		// when
		final List<SolrIndexModel> indexes = solrIndexService.getIndexesForConfigAndType(FACET_SEARCH_CONFIG2_NAME,
				INDEXED_TYPE2_NAME);

		// then
		assertThat(indexes, not(empty()));
		assertEquals(2, indexes.size());
	}

	@Test
	public void getIndex() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// when
		final SolrIndexModel index = solrIndexService.getIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// then
		assertNotNull(index);
		assertEquals(FACET_SEARCH_CONFIG1_NAME, index.getFacetSearchConfig().getName());
		assertEquals(INDEXED_TYPE1_NAME, index.getIndexedType().getIdentifier());
		assertEquals(QUALIFIER_1, index.getQualifier());
	}

	@Test
	public void getNonExistingIndex() throws Exception
	{
		// expect
		expectedException.expect(SolrIndexNotFoundException.class);

		// when
		solrIndexService.getIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
	}

	@Test
	public void getIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(SolrServiceException.class);

		// when
		solrIndexService.getIndex("invalidFacetSearchConfig", INDEXED_TYPE1_NAME, QUALIFIER_1);
	}

	@Test
	public void getIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(SolrServiceException.class);

		// when
		solrIndexService.getIndex(FACET_SEARCH_CONFIG1_NAME, "invalidIndexedType", QUALIFIER_1);
	}

	@Test
	public void getOrCreateIndex() throws Exception
	{
		// when
		final SolrIndexModel index = solrIndexService.getOrCreateIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// then
		assertNotNull(index);
		assertEquals(FACET_SEARCH_CONFIG1_NAME, index.getFacetSearchConfig().getName());
		assertEquals(INDEXED_TYPE1_NAME, index.getIndexedType().getIdentifier());
		assertEquals(QUALIFIER_1, index.getQualifier());
		assertFalse(index.isActive());

	}

	@Test
	public void getOrCreateIndexExistingIndex() throws Exception
	{
		// given
		final SolrIndexModel previousIndex = solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME,
				QUALIFIER_1);

		// when
		final SolrIndexModel index = solrIndexService.getOrCreateIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// then
		assertNotNull(index);
		assertEquals(FACET_SEARCH_CONFIG1_NAME, index.getFacetSearchConfig().getName());
		assertEquals(INDEXED_TYPE1_NAME, index.getIndexedType().getIdentifier());
		assertEquals(QUALIFIER_1, index.getQualifier());
		assertFalse(index.isActive());
		assertNotNull(index.getPk());
		assertEquals(previousIndex.getPk(), index.getPk());
	}

	@Test
	public void activateIndex() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_2);

		// when
		solrIndexService.activateIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_2);
		final SolrIndexModel index1 = solrIndexService.getIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
		final SolrIndexModel index2 = solrIndexService.getIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_2);

		// the
		assertFalse(index1.isActive());
		assertTrue(index2.isActive());
	}

	@Test
	public void activateNonExistingIndex() throws Exception
	{
		// expect
		expectedException.expect(SolrIndexNotFoundException.class);

		// when
		solrIndexService.activateIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
	}

	@Test
	public void getActiveIndex() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// when
		solrIndexService.activateIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
		final SolrIndexModel index = solrIndexService.getIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// then
		assertNotNull(index);
		assertEquals(FACET_SEARCH_CONFIG1_NAME, index.getFacetSearchConfig().getName());
		assertEquals(INDEXED_TYPE1_NAME, index.getIndexedType().getIdentifier());
		assertEquals(QUALIFIER_1, index.getQualifier());
		assertTrue(index.isActive());
	}

	@Test
	public void getNonExistingActiveIndex() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// expect
		expectedException.expect(SolrIndexNotFoundException.class);

		// when
		solrIndexService.getActiveIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME);
	}

	@Test
	public void deleteIndex() throws Exception
	{
		// given
		solrIndexService.createIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);

		// when
		solrIndexService.deleteIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
		final List<SolrIndexModel> indexes = solrIndexService.getAllIndexes();

		// then
		assertThat(indexes, empty());
	}

	@Test
	public void deleteNonExistingIndex() throws Exception
	{
		// expect
		expectedException.expect(SolrIndexNotFoundException.class);

		// when
		solrIndexService.deleteIndex(FACET_SEARCH_CONFIG1_NAME, INDEXED_TYPE1_NAME, QUALIFIER_1);
	}
}
