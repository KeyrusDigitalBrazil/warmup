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
package com.hybris.backoffice.solrsearch.indexing;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.impl.DefaultIndexerStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.impl.DefaultIndexerStrategyFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import com.hybris.backoffice.solrsearch.indexer.impl.BackofficeIndexerStrategy;
import com.hybris.backoffice.solrsearch.indexer.impl.BackofficeIndexerStrategyFactory;
import com.hybris.backoffice.solrsearch.services.BackofficeFacetSearchConfigService;


public class BackofficeIndexerStrategyFactoryTest
{
	private static final String BACKOFFICE_CONFIG_NAME = "BackofficeFacetSearchConfig1";
	private static final String OTHER_CONFIG_NAME = "someOtherConfig";
	private static final String INDEXER_STRATEGY_BEAN_NAME = "indexerStrategy";
	private static final String BACKOFFICE_INDEXER_STRATEGY = "backofficeIndexerStrategy";
	@InjectMocks
	@Rule
	public ExpectedException expectedException = ExpectedException.none(); // NOPMD

	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private FacetSearchConfig facetSearchConfig;
	@Mock
	private BackofficeFacetSearchConfigService facetSearchConfigService;

	private BackofficeIndexerStrategyFactory backofficeIndexerStrategyFactory;
	private DefaultIndexerStrategyFactory defaultIndexerStrategyFactory;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		backofficeIndexerStrategyFactory = new BackofficeIndexerStrategyFactory();
		defaultIndexerStrategyFactory = new DefaultIndexerStrategyFactory();
		defaultIndexerStrategyFactory.setIndexerStrategyBeanId(INDEXER_STRATEGY_BEAN_NAME);

		backofficeIndexerStrategyFactory.setApplicationContext(applicationContext);
		backofficeIndexerStrategyFactory.setBackofficeFacetSearchConfigService(facetSearchConfigService);
		defaultIndexerStrategyFactory.setApplicationContext(applicationContext);
		backofficeIndexerStrategyFactory.setDefaultIndexerStrategyFactory(defaultIndexerStrategyFactory);
	}

	@Test
	public void shouldCreateBackofficeIndexerStrategy() throws Exception
	{
		// given
		final DefaultIndexerStrategy expectedIndexerStrategy = mock(BackofficeIndexerStrategy.class);
		backofficeIndexerStrategyFactory.setIndexerStrategyBeanName(BACKOFFICE_INDEXER_STRATEGY);
		when(facetSearchConfig.getName()).thenReturn(BACKOFFICE_CONFIG_NAME);
		when(applicationContext.getBean(BACKOFFICE_INDEXER_STRATEGY, IndexerStrategy.class)).thenReturn(expectedIndexerStrategy);
		when(Boolean.valueOf(facetSearchConfigService.isBackofficeSolrSearchConfiguredForName(BACKOFFICE_CONFIG_NAME)))
				.thenReturn(Boolean.TRUE);

		// when
		final IndexerStrategy indexerStrategy = backofficeIndexerStrategyFactory.createIndexerStrategy(facetSearchConfig);

		// then
		assertSame(expectedIndexerStrategy, indexerStrategy);
	}

	@Test
	public void shouldCreateDefaultIndexerStrategy() throws Exception
	{
		//given
		final DefaultIndexerStrategy expectedIndexerStrategy = mock(DefaultIndexerStrategy.class);
		backofficeIndexerStrategyFactory.setIndexerStrategyBeanName(BACKOFFICE_INDEXER_STRATEGY);
		when(facetSearchConfig.getName()).thenReturn(OTHER_CONFIG_NAME);
		when(applicationContext.getBean(INDEXER_STRATEGY_BEAN_NAME, IndexerStrategy.class)).thenReturn(expectedIndexerStrategy);
		when(Boolean.valueOf(facetSearchConfigService.isBackofficeSolrSearchConfiguredForName(OTHER_CONFIG_NAME)))
				.thenReturn(Boolean.FALSE);
		when(facetSearchConfig.getIndexConfig()).thenReturn(new IndexConfig());

		// when
		final IndexerStrategy indexerStrategy = backofficeIndexerStrategyFactory.createIndexerStrategy(facetSearchConfig);

		// then
		assertSame(expectedIndexerStrategy, indexerStrategy);
	}
}
