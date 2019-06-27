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
package de.hybris.platform.solrfacetsearch.indexer.strategies.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexOperationIdGenerator;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultIndexOperationIdGeneratorTest extends AbstractIntegrationTest
{
	private static final String QUALIFIER = "qualifier";

	@Resource
	private IndexOperationIdGenerator indexOperationIdGenerator;

	@Resource
	private SolrIndexService solrIndexService;

	@Resource
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Test
	public void testNextIdIsBiggerThanPrevious() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SolrIndexModel index = solrIndexService.createIndex(facetSearchConfig.getName(), indexedType.getIdentifier(),
				QUALIFIER);
		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
		final Index solrIndex = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, index.getQualifier());

		// when
		final long indexOperationId1 = indexOperationIdGenerator.generate(facetSearchConfig, indexedType, solrIndex);
		final long indexOperationId2 = indexOperationIdGenerator.generate(facetSearchConfig, indexedType, solrIndex);

		// then
		assertTrue(indexOperationId1 > 0);
		assertTrue(indexOperationId2 > 0);
		assertTrue(indexOperationId2 > indexOperationId1);
	}
}
