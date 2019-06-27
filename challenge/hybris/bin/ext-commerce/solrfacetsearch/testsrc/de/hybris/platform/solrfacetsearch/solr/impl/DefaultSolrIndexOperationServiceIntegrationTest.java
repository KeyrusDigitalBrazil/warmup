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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationStatus;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.model.SolrIndexOperationModel;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexOperationService;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrIndexOperationNotFoundException;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@IntegrationTest
public class DefaultSolrIndexOperationServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final String FACET_SEARCH_CONFIG_NAME = "testFacetSearchConfig";
	private static final String INDEXED_TYPE_NAME = "testIndexedType";
	private static final String QUALIFIER = "qualifier";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Resource
	private SolrIndexOperationService solrIndexOperationService;

	@Resource
	private SolrIndexService solrIndexService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/integration/DefaultSolrIndexOperationServiceIntegrationTest.csv", DEFAULT_ENCODING);
	}

	@Test
	public void getOperationForId() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);
		solrIndexOperationService.startOperation(index, 4, IndexOperation.UPDATE, true);

		// when
		final SolrIndexOperationModel indexOperation = solrIndexOperationService.getOperationForId(4);

		// then
		assertNotNull(indexOperation);
		assertEquals(index, indexOperation.getIndex());
		assertEquals(4, indexOperation.getId());
		assertEquals(IndexerOperationValues.UPDATE, indexOperation.getOperation());
		assertEquals(true, indexOperation.isExternal());
	}

	@Test
	public void getNonExistingOperationForId() throws Exception
	{
		// expect
		expectedException.expect(SolrIndexOperationNotFoundException.class);

		// when
		solrIndexOperationService.getOperationForId(2);
	}

	@Test
	public void startOperation() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);

		// when
		final SolrIndexOperationModel indexOperation = solrIndexOperationService.startOperation(index, 2, IndexOperation.FULL,
				true);

		// then
		assertNotNull(indexOperation);
		assertEquals(index, indexOperation.getIndex());
		assertEquals(2, indexOperation.getId());
		assertEquals(IndexerOperationValues.FULL, indexOperation.getOperation());
		assertEquals(true, indexOperation.isExternal());
		assertEquals(IndexerOperationStatus.RUNNING, indexOperation.getStatus());
		assertNotNull(indexOperation.getStartTime());
		assertEquals(null, indexOperation.getEndTime());
	}

	@Test
	public void startOperationWithSameId() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);

		// expect
		expectedException.expect(SolrServiceException.class);

		// when
		solrIndexOperationService.startOperation(index, 2, IndexOperation.FULL, false);
		solrIndexOperationService.startOperation(index, 2, IndexOperation.UPDATE, false);
	}

	@Test
	public void endOperationWithSuccess() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);
		solrIndexOperationService.startOperation(index, 3, IndexOperation.FULL, true);

		// when
		final SolrIndexOperationModel indexOperation = solrIndexOperationService.endOperation(3, false);

		// then
		assertNotNull(indexOperation);
		assertEquals(index, indexOperation.getIndex());
		assertEquals(3, indexOperation.getId());
		assertEquals(IndexerOperationValues.FULL, indexOperation.getOperation());
		assertEquals(true, indexOperation.isExternal());
		assertEquals(IndexerOperationStatus.SUCCESS, indexOperation.getStatus());
		assertNotNull(indexOperation.getStartTime());
		assertNotNull(indexOperation.getEndTime());
	}

	@Test
	public void endOperationWithError() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);
		solrIndexOperationService.startOperation(index, 3, IndexOperation.FULL, true);

		// when
		final SolrIndexOperationModel indexOperation = solrIndexOperationService.endOperation(3, true);

		// then
		assertNotNull(indexOperation);
		assertEquals(index, indexOperation.getIndex());
		assertEquals(3, indexOperation.getId());
		assertEquals(IndexerOperationValues.FULL, indexOperation.getOperation());
		assertEquals(true, indexOperation.isExternal());
		assertEquals(IndexerOperationStatus.FAILED, indexOperation.getStatus());
		assertNotNull(indexOperation.getStartTime());
		assertNotNull(indexOperation.getEndTime());
	}

	@Test
	public void endNonExistingOperation() throws Exception
	{
		// expect
		expectedException.expect(SolrIndexOperationNotFoundException.class);

		// when
		solrIndexOperationService.cancelOperation(23);
	}

	@Test
	public void cancelOperation() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);
		solrIndexOperationService.startOperation(index, 3, IndexOperation.FULL, true);

		// when
		final SolrIndexOperationModel indexOperation = solrIndexOperationService.cancelOperation(3);

		// then
		assertNotNull(indexOperation);
		assertEquals(index, indexOperation.getIndex());
		assertEquals(3, indexOperation.getId());
		assertEquals(IndexerOperationValues.FULL, indexOperation.getOperation());
		assertEquals(true, indexOperation.isExternal());
		assertEquals(IndexerOperationStatus.ABORTED, indexOperation.getStatus());
		assertNotNull(indexOperation.getStartTime());
		assertNotNull(indexOperation.getEndTime());
	}

	@Test
	public void cancelNonExistingOperation() throws Exception
	{
		// expect
		expectedException.expect(SolrIndexOperationNotFoundException.class);

		// when
		solrIndexOperationService.cancelOperation(23);
	}

	@Test
	public void getLastIndexOperationTime() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);

		solrIndexOperationService.startOperation(index, 2, IndexOperation.FULL, false);
		solrIndexOperationService.endOperation(2, false);

		Thread.sleep(500);

		solrIndexOperationService.startOperation(index, 3, IndexOperation.UPDATE, false);
		solrIndexOperationService.endOperation(3, false);

		final SolrIndexOperationModel lastIndexOperation = solrIndexOperationService.getOperationForId(3);

		// when
		final Date lastIndexOperationTime = solrIndexOperationService.getLastIndexOperationTime(index);

		// then
		assertEquals(lastIndexOperation.getStartTime(), lastIndexOperationTime);
	}

	@Test
	public void getLastIndexOperationTimeNoOperation() throws Exception
	{
		// given
		final SolrIndexModel index = solrIndexService.createIndex(FACET_SEARCH_CONFIG_NAME, INDEXED_TYPE_NAME, QUALIFIER);

		// when
		final Date lastIndexOperationTime = solrIndexOperationService.getLastIndexOperationTime(index);

		// then
		assertEquals(new Date(0), lastIndexOperationTime);
	}
}
