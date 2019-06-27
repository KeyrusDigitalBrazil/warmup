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

import static java.util.stream.Collectors.toList;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.processing.enums.BatchType;
import de.hybris.platform.processing.model.BatchModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.DefaultFlexibleSearchService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.model.SolrIndexerBatchModel;
import de.hybris.platform.solrfacetsearch.model.SolrIndexerDistributedProcessModel;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;


public class DistributedIndexerProcessTest extends AbstractIntegrationTest
{
	@Resource
	private DefaultFlexibleSearchService flexibleSearchService;

	@Resource
	private DefaultIndexerService indexerService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/DistributedIndexerProcessTest.csv");
	}

	@Test
	public void testDistributedProcess() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();

		// when
		indexerService.performFullIndex(facetSearchConfig);

		// then
		final SolrIndexerDistributedProcessModel distributedIndexerProcess = getDistributedIndexerProcess();

		assertThat(distributedIndexerProcess).isNotNull();
		final List<SolrIndexerBatchModel> inputBatches = getInputBatches(distributedIndexerProcess);
		assertNotNull(inputBatches);

		assertNotNull(inputBatches.get(0).getContext());
		final List<PK> inputBatchContext = (List<PK>) inputBatches.get(0).getContext();
		assertThat(inputBatchContext).hasSize(
				getNumberOfIndexingItems() < distributedIndexerProcess.getBatchSize() ? getNumberOfIndexingItems()
						: distributedIndexerProcess.getBatchSize());
	}

	private List<SolrIndexerBatchModel> getInputBatches(final SolrIndexerDistributedProcessModel process)
	{
		return process.getBatches().stream().filter(b -> b.getType() == BatchType.INITIAL).map(this::asSolrIndexerBatchModel)
				.collect(toList());
	}

	private SolrIndexerBatchModel asSolrIndexerBatchModel(final BatchModel batch)
	{
		assertThat(batch).isInstanceOf(SolrIndexerBatchModel.class);
		return (SolrIndexerBatchModel) batch;
	}

	private SolrIndexerDistributedProcessModel getDistributedIndexerProcess()
	{
		final StringBuilder query = new StringBuilder("SELECT {ssidp." + SolrIndexerDistributedProcessModel.PK + "} ");
		query.append("FROM {" + SolrIndexerDistributedProcessModel._TYPECODE + " AS ssidp} ");
		query.append("ORDER BY {" + SolrIndexerDistributedProcessModel.MODIFIEDTIME + "} DESC");

		final SearchResult<SolrIndexerDistributedProcessModel> searchRes = flexibleSearchService.search(query.toString());
		return searchRes.getResult().get(0);
	}

	private int getNumberOfIndexingItems()
	{
		final StringBuilder query = new StringBuilder("SELECT {p." + ProductModel.PK + "} ");
		query.append("FROM {" + ProductModel._TYPECODE + " AS p} ");

		final SearchResult<ProductModel> searchRes = flexibleSearchService.search(query.toString());
		return searchRes.getResult().size();
	}
}
