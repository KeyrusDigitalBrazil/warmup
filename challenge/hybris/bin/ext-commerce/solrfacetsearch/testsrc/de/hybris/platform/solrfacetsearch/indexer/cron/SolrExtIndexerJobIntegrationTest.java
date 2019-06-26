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
package de.hybris.platform.solrfacetsearch.indexer.cron;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexOperationService;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class SolrExtIndexerJobIntegrationTest extends AbstractIntegrationTest
{
	private static final String DEFAULT_SOLR_EXT_INDEXER_JOB_CODE = "testSolrExtIndexerJob";

	private static final String PRODUCT_CODE = "00001";
	private static final String PRODUCT_MAN_NAME = "hybris";
	private static final String PRODUCT_MAN_NAME_UPDATED = "updated_hybris";

	@Resource
	private CronJobService cronJobService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Resource
	private SolrIndexService solrIndexService;

	@Resource
	private SolrIndexOperationService solrIndexOperationService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SolrExtIndexerJobIntegrationTest.csv");
		indexerService.performFullIndex(getFacetSearchConfig());
	}

	@Test
	public void testSearchBeforeUpdate() throws IndexerException, FacetConfigServiceException, FacetSearchException
	{
		//given
		final CronJobModel cronJob = cronJobService.getCronJob(getCronJobCode());

		//when
		cronJobService.performCronJob(cronJob, true);

		final SearchResult searchResult = facetSearchService.search(getQuery());

		//then
		assertEquals(1, searchResult.getNumberOfResults());
	}

	@Test
	public void testDeleteProduct() throws FacetSearchException, FacetConfigServiceException, ImpExException, IOException
	{
		//given
		importConfig("/test/integration/SolrExtIndexerJobIntegrationTest_createDeleteCronJob.csv");
		final CronJobModel cronJob = cronJobService.getCronJob(getCronJobCode());

		//when
		cronJobService.performCronJob(cronJob, true);

		final SearchResult searchResult = facetSearchService.search(getQuery());

		//then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void testUpdateProduct() throws FacetSearchException, FacetConfigServiceException, ImpExException, IOException
	{
		//given
		importConfig("/test/integration/SolrExtIndexerJobIntegrationTest_updateProduct.csv");

		final CronJobModel cronJob = cronJobService.getCronJob(getCronJobCode());

		//when
		cronJobService.performCronJob(cronJob, true);

		final SearchResult searchResult = facetSearchService.search(getQuery());
		final SearchResult searchResultUpdated = facetSearchService.search(getQueryUpdated());

		//then
		assertEquals(1, searchResultUpdated.getNumberOfResults());
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void testUpdateLastIndexTimeNotChange()
			throws FacetSearchException, FacetConfigServiceException, ImpExException, IOException, SolrServiceException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final SolrIndexModel index = solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier());

		//given
		importConfig("/test/integration/SolrExtIndexerJobIntegrationTest_updateProduct.csv");

		final CronJobModel cronJob = cronJobService.getCronJob(getCronJobCode());

		//when
		final Date lastIndexTime1 = solrIndexOperationService.getLastIndexOperationTime(index);
		cronJobService.performCronJob(cronJob, true);
		final Date lastIndexTime2 = solrIndexOperationService.getLastIndexOperationTime(index);


		//then
		assertNotNull(lastIndexTime2);
		assertEquals(lastIndexTime2, lastIndexTime1);
	}

	private SearchQuery getQuery() throws FacetConfigServiceException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");
		query.setDefaultOperator(Operator.AND);
		query.addQuery("manufacturerName", PRODUCT_MAN_NAME);
		query.addQuery("code", PRODUCT_CODE);

		return query;
	}

	private SearchQuery getQueryUpdated() throws FacetConfigServiceException
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel catalogVersionOnline = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION + getTestId());

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(catalogVersionOnline));
		query.setLanguage("en");
		query.setDefaultOperator(Operator.AND);
		query.addQuery("manufacturerName", PRODUCT_MAN_NAME_UPDATED);
		query.addQuery("code", PRODUCT_CODE);

		return query;
	}

	private String getCronJobCode()
	{
		return DEFAULT_SOLR_EXT_INDEXER_JOB_CODE + getTestId();
	}
}
