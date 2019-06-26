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

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrClientPool;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@PerformanceTest
public class DefaultSolrClientPoolPerformanceTest extends AbstractIntegrationTest
{
	private static final Logger LOG = Logger.getLogger(DefaultSolrClientPoolPerformanceTest.class);

	@Resource
	private SolrClientPool solrClientPool;

	@Resource
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Test
	public void testPoolPerformance() throws FacetConfigServiceException, SolrServiceException, IOException, SolrServerException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
		final Index index = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, "1");

		final long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
		{
			final SolrClient solrClient = solrSearchProvider.getClientForIndexing(index);
			solrClient.close();
		}
		final long endTime = System.currentTimeMillis();

		LOG.info("Time taken: " + (endTime - startTime));
	}

	@Test
	public void testPoolPerformanceWithInvalidation()
			throws FacetConfigServiceException, SolrServiceException, IOException, SolrServerException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
		final Index index = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, "1");

		final long startTime = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++)
		{
			if (forceCreateClient(i))
			{
				solrClientPool.invalidateAll();
			}
			final SolrClient solrClient = solrSearchProvider.getClientForIndexing(index);
			solrClient.close();
		}
		final long endTime = System.currentTimeMillis();

		LOG.info("Time taken: " + (endTime - startTime));
	}

	private boolean forceCreateClient(final int index)
	{
		return index % 10000 == 0;
	}
}
