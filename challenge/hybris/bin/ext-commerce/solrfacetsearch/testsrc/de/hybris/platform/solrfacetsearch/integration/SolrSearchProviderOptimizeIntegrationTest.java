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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;

import java.util.List;

import javax.annotation.Resource;
import org.junit.Test;


@IntegrationTest
public class SolrSearchProviderOptimizeIntegrationTest extends AbstractIntegrationTest
{
	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private SolrIndexService solrIndexService;

	@Resource
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Test
	public void shouldOptimizeAfterFullIndex() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		indexerService.performFullIndex(facetSearchConfig);

		for (final IndexedType indexedType : facetSearchConfig.getIndexConfig().getIndexedTypes().values())
		{
			final List<SolrIndexModel> indexes = solrIndexService.getIndexesForConfigAndType(facetSearchConfig.getName(),
					indexedType.getIdentifier());
			for (final SolrIndexModel index : indexes)
			{
				final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
				final Index solrIndex = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, index.getQualifier());

				// when
				solrSearchProvider.optimize(solrIndex);
			}
		}
		// then only need to make sure no exceptions are thrown
	}
}
