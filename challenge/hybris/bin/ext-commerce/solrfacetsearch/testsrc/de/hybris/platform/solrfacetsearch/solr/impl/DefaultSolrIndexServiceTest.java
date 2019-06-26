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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.solrfacetsearch.daos.SolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.daos.SolrIndexDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSolrIndexServiceTest
{
	private static final String FACET_SEARCH_CONFIG_NAME = "facetSearchConfig";
	private static final String INDEXED_TYPE_NAME = "indexedType";
	private static final String QUALIFIER = "qualifier";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SolrIndexDao solrIndexDao;

	@Mock
	private SolrFacetSearchConfigDao solrFacetSearchConfigDao;

	@Mock
	private ModelService modelService;

	@Mock
	private TimeService timeService;

	private DefaultSolrIndexService solrIndexService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		solrIndexService = new DefaultSolrIndexService();
		solrIndexService.setSolrIndexDao(solrIndexDao);
		solrIndexService.setSolrFacetSearchConfigDao(solrFacetSearchConfigDao);
		solrIndexService.setModelService(modelService);
		solrIndexService.setTimeService(timeService);
	}

	@Test
	public void createIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.createIndex(null, INDEXED_TYPE_NAME, QUALIFIER);
	}

	@Test
	public void createIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, null, QUALIFIER);
	}

	@Test
	public void createIndexWithInvalidQualifier() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, null);
	}

	@Test
	public void getIndexesForConfigAndTypeWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getIndexesForConfigAndType(null, INDEXED_TYPE_NAME);
	}

	@Test
	public void getIndexesForConfigAndTypeWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getIndexesForConfigAndType(FACET_SEARCH_CONFIG_NAME, null);
	}

	@Test
	public void getIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getIndex(null, INDEXED_TYPE_NAME, QUALIFIER);
	}

	@Test
	public void getIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getIndex(FACET_SEARCH_CONFIG_NAME, null, QUALIFIER);
	}

	@Test
	public void getIndexWithInvalidQualifier() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, null);
	}

	@Test
	public void getOrCreateIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getOrCreateIndex(null, INDEXED_TYPE_NAME, QUALIFIER);
	}

	@Test
	public void getOrCreateIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getOrCreateIndex(FACET_SEARCH_CONFIG_NAME, null, QUALIFIER);
	}

	@Test
	public void getOrCreateIndexWithInvalidQualifier() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getOrCreateIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, null);
	}

	@Test
	public void deleteIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.deleteIndex(null, INDEXED_TYPE_NAME, QUALIFIER);
	}

	@Test
	public void deleteIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.deleteIndex(FACET_SEARCH_CONFIG_NAME, null, QUALIFIER);
	}

	@Test
	public void deleteIndexWithInvalidQualifier() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.deleteIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, null);
	}

	@Test
	public void activateIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.activateIndex(null, INDEXED_TYPE_NAME, QUALIFIER);
	}

	@Test
	public void activateIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.activateIndex(FACET_SEARCH_CONFIG_NAME, null, QUALIFIER);
	}

	@Test
	public void activateIndexWithInvalidQualifier() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.activateIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, null);
	}

	@Test
	public void getActiveIndexWithInvalidFacetSearchConfig() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getActiveIndex(null, INDEXED_TYPE_NAME);
	}

	@Test
	public void getActiveIndexWithInvalidIndexedType() throws Exception
	{
		// expect
		expectedException.expect(IllegalArgumentException.class);

		// when
		solrIndexService.getActiveIndex(FACET_SEARCH_CONFIG_NAME, null);
	}

}
