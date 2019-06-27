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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.core.PK;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerService;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;

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

import com.hybris.backoffice.solrsearch.daos.SolrModifiedItemDAO;
import com.hybris.backoffice.solrsearch.enums.SolrItemModificationType;
import com.hybris.backoffice.solrsearch.indexer.cron.BackofficeSolrIndexerUpdateJob;
import com.hybris.backoffice.solrsearch.model.SolrModifiedItemModel;
import com.hybris.backoffice.solrsearch.services.BackofficeFacetSearchConfigService;


public class BackofficeSolrIndexerUpdateJobTest
{

	private static final String MODIFIED_TYPE_CODE = "Product";
	private static final Long MODIFIED_PK = Long.valueOf(1L);
	private static final String SEARCH_CONFIG_NAME = "Product Index";
	private static final Logger LOG = LoggerFactory.getLogger(BackofficeSolrIndexerUpdateJobTest.class);
	@Mock
	private ModelService modelService;

	@Mock
	private SolrModifiedItemDAO solrModifiedItemDAO;

	@Mock
	private FacetSearchConfigService facetSearchConfigService;

	@Mock
	private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;

	@Mock
	private IndexerService indexerService;

	@InjectMocks
	private BackofficeSolrIndexerUpdateJob indexerUpdateJob;

	private SolrModifiedItemModel updatedItem;
	private SolrFacetSearchConfigModel searchConfig;
	private FacetSearchConfig facetSearchConfig;
	private IndexedType indexedType;


	@Before
	public void init()
	{
		initMocks(this);

		updatedItem = new SolrModifiedItemModel();
		updatedItem.setModifiedTypeCode(MODIFIED_TYPE_CODE);
		updatedItem.setModifiedPk(MODIFIED_PK);
		updatedItem.setModificationType(SolrItemModificationType.UPDATE);

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
	}

	@Test
	public void shouldUpdateSingleItemInSolrIndex() throws IndexerException
	{
		final List<SolrModifiedItemModel> modifiedItems = Collections.singletonList(updatedItem);

		when(solrModifiedItemDAO.findByModificationType(SolrItemModificationType.UPDATE)).thenReturn(modifiedItems);
		when(modelService.get(PK.fromLong(updatedItem.getModifiedPk().longValue()))).thenReturn(updatedItem);
		try
		{
			when(backofficeFacetSearchConfigService.getSolrFacetSearchConfigModel(MODIFIED_TYPE_CODE)).thenReturn(searchConfig);
			when(facetSearchConfigService.getConfiguration(SEARCH_CONFIG_NAME)).thenReturn(facetSearchConfig);
		}
		catch (final FacetConfigServiceException e)
		{
			LOG.error("Facet configuration error", e);
			fail("Facet configuration error");
		}

		indexerUpdateJob.performIndexingJob(new CronJobModel());

		final List<PK> pks = Stream.of(updatedItem).map(i -> PK.fromLong(i.getModifiedPk().longValue()))
				.collect(Collectors.toList());

		verify(indexerService).updateTypeIndex(facetSearchConfig, indexedType, pks);
		verify(modelService).removeAll(modifiedItems);
	}
}
