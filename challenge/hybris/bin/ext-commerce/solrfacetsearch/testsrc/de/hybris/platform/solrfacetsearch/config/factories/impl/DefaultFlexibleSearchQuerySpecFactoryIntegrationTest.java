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
package de.hybris.platform.solrfacetsearch.config.factories.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FlexibleSearchQuerySpec;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.factories.FlexibleSearchQuerySpecFactory;
import de.hybris.platform.solrfacetsearch.indexer.IndexerService;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;


@IntegrationTest
public class DefaultFlexibleSearchQuerySpecFactoryIntegrationTest extends AbstractIntegrationTest
{
	protected static final Date DEFAULT_DATE = new Date(0);

	@Resource
	private FlexibleSearchQuerySpecFactory flexibleSearchQuerySpecFactory;

	@Resource
	private IndexerService indexerService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/DefaultFlexibleSearchQuerySpecFactoryIntegrationTest.csv");
	}

	@Test
	public void lastIndexTimeWithNoActiveIndex() throws Exception
	{
		//given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		//when
		final FlexibleSearchQuerySpec query = flexibleSearchQuerySpecFactory
				.createIndexQuery(indexedType.getFlexibleSearchQueries().get(IndexOperation.UPDATE), indexedType, facetSearchConfig);
		final Date lastIndexTime = (Date) query.createParameters().get(DefaultFlexibleSearchQuerySpecFactory.LASTINDEXTIME);

		//then
		assertEquals(DEFAULT_DATE, lastIndexTime);
	}

	@Test
	public void lastIndexTimeForFullOperation() throws Exception
	{
		//given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final Date startTime = new Date();

		//when
		indexerService.performFullIndex(facetSearchConfig);
		final FlexibleSearchQuerySpec query = flexibleSearchQuerySpecFactory
				.createIndexQuery(indexedType.getFlexibleSearchQueries().get(IndexOperation.FULL), indexedType, facetSearchConfig);
		final Date lastIndexTime = (Date) query.createParameters().get(DefaultFlexibleSearchQuerySpecFactory.LASTINDEXTIME);

		//then
		assertTrue(lastIndexTime.after(startTime));
	}

	@Test
	public void lastIndexTimeForUpdateOperation() throws Exception
	{
		//given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final Date startTime = new Date();

		//when
		indexerService.performFullIndex(facetSearchConfig);
		final FlexibleSearchQuerySpec query = flexibleSearchQuerySpecFactory
				.createIndexQuery(indexedType.getFlexibleSearchQueries().get(IndexOperation.UPDATE), indexedType, facetSearchConfig);
		final Date lastIndexTime = (Date) query.createParameters().get(DefaultFlexibleSearchQuerySpecFactory.LASTINDEXTIME);

		//then
		assertTrue(lastIndexTime.after(startTime));
	}
}
