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

import static org.fest.assertions.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContextFactory;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.ExporterException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Exporter;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexOperationIdGenerator;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import com.hybris.backoffice.solrsearch.daos.SolrModifiedItemDAO;
import com.hybris.backoffice.solrsearch.enums.SolrItemModificationType;
import com.hybris.backoffice.solrsearch.indexer.cron.BackofficeSolrIndexerDeleteJob;
import com.hybris.backoffice.solrsearch.model.SolrModifiedItemModel;
import com.hybris.backoffice.solrsearch.services.BackofficeFacetSearchConfigService;
import com.hybris.backoffice.solrsearch.utils.SolrPlatformUtils;


public class BackofficeSolrIndexerDeleteJobTest
{

	private static final Logger LOG = LoggerFactory.getLogger(BackofficeSolrIndexerDeleteJobTest.class);

	private static final String MODIFIED_TYPE_CODE = "Product";
	private static final Long DELETED_PK = Long.valueOf(1L);
	private static final String SEARCH_CONFIG_NAME = "Product Index";
	private static final SolrServerMode SOLR_SERVER_MODE = SolrServerMode.EMBEDDED;

	@Mock
	private ModelService modelService;

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private SolrModifiedItemDAO solrModifiedItemDAO;

	@Mock
	private FacetSearchConfigService facetSearchConfigService;

	@Mock
	private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;

	@Mock
	private Exporter exporterService;

	@Mock
	private SolrIndexService solrIndexService;

	@Mock
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Mock
	private IndexOperationIdGenerator indexOperationIdGenerator;

	@Mock
	private IndexerBatchContextFactory indexerBatchContextFactory;

	@InjectMocks
	private BackofficeSolrIndexerDeleteJob indexerDeleteJob;

	private SolrModifiedItemModel deletedItem;
	private SolrFacetSearchConfigModel searchConfig;
	private FacetSearchConfig facetSearchConfig;
	private IndexedType indexedType;


	@Before
	public void init()
	{
		initMocks(this);

		deletedItem = new SolrModifiedItemModel();
		deletedItem.setModifiedTypeCode(MODIFIED_TYPE_CODE);
		deletedItem.setModifiedPk(DELETED_PK);
		deletedItem.setModificationType(SolrItemModificationType.DELETE);

		final SolrServerConfigModel serverConfig = new SolrServerConfigModel();

		searchConfig = new SolrFacetSearchConfigModel();
		searchConfig.setName(SEARCH_CONFIG_NAME);
		searchConfig.setSolrServerConfig(serverConfig);


		final Map<String, IndexedType> indexedTypes = new HashMap<>();
		indexedType = new IndexedType();
		indexedType.setCode(MODIFIED_TYPE_CODE);
		indexedTypes.put(MODIFIED_TYPE_CODE, indexedType);

		final IndexConfig indexConfig = new IndexConfig();
		indexConfig.setIndexedTypes(indexedTypes);

		final SolrConfig solrConfig = new SolrConfig();
		solrConfig.setMode(SolrServerMode.EMBEDDED);

		facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setIndexConfig(indexConfig);
		facetSearchConfig.setSolrConfig(solrConfig);


		when(beanFactory.getBean(SolrPlatformUtils.createSolrExporterBeanName(SOLR_SERVER_MODE), Exporter.class))
				.thenReturn(exporterService);

		when(indexOperationIdGenerator.generate(any(), any(), any())).thenReturn(1L);
		when(indexerBatchContextFactory.createContext(anyLong(), any(), anyBoolean(), any(), any(), any()))
				.thenReturn(mock(IndexerBatchContext.class));

	}

	@Test
	public void shouldDeleteSingleItemFromSolrIndex() throws ExporterException
	{
		final List<SolrModifiedItemModel> modifiedItems = Collections.singletonList(deletedItem);
		when(solrModifiedItemDAO.findByModificationType(SolrItemModificationType.DELETE)).thenReturn(modifiedItems);

		try
		{
			when(backofficeFacetSearchConfigService.getSolrFacetSearchConfigModel(MODIFIED_TYPE_CODE)).thenReturn(searchConfig);
			when(facetSearchConfigService.getConfiguration(SEARCH_CONFIG_NAME)).thenReturn(facetSearchConfig);
		}
		catch (final FacetConfigServiceException e)
		{
			LOG.error("Facet configuration error", e);
			fail(e.getMessage());
		}

		final SolrSearchProvider solrSearchProvider = mock(SolrSearchProvider.class);
		final SolrIndexModel activeIndex = mock(SolrIndexModel.class);
		when(activeIndex.getQualifier()).thenReturn("qualifier");
		try
		{
			when(solrIndexService.getActiveIndex(facetSearchConfig.getName(), indexedType.getIdentifier())).thenReturn(activeIndex);
			when(solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType)).thenReturn(solrSearchProvider);
		}
		catch (final SolrServiceException e)
		{
			LOG.error("Facet configuration error", e);
			fail(e.getMessage());
		}

		indexerDeleteJob.performIndexingJob(new CronJobModel());

		final List<String> pks = Stream.of(deletedItem).map(i -> i.getModifiedPk().toString())
				.collect(Collectors.toList());

		verify(exporterService).exportToDeleteFromIndex(pks, facetSearchConfig, indexedType);
		verify(modelService).removeAll(modifiedItems);
	}
}
