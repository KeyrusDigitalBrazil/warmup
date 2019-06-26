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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.solrsearch.events.DirectSolrIndexSynchronizationStrategy;
import com.hybris.backoffice.solrsearch.model.BackofficeIndexedTypeToSolrFacetSearchConfigModel;
import com.hybris.backoffice.solrsearch.services.BackofficeFacetSearchConfigService;


public class DirectSolrIndexSynchronizationStrategyTest
{
	public static final String PRODUCT_TYPECODE = "Product";
	public static final long PK_ = 1L;
	public static final String CONFIG_NAME = "backoffice_product";

	@Mock
	private ComposedTypeModel typeModel;

	@Mock
	private BackofficeIndexedTypeToSolrFacetSearchConfigModel searchConfig;

	@Mock
	private SolrFacetSearchConfigModel solrFacetSearchConfigModel;

	@Mock
	private FacetSearchConfigService facetSearchConfigService;

	@Mock
	private IndexerService indexerService;

	@Mock
	private ModelService modelService;

	@Mock
	private BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;

	@Mock
	private TypeService typeService;

	@Mock
	private FacetSearchConfig facetSearchConfig;


	private IndexedType indexedType = new IndexedType();

	@InjectMocks
	private DirectSolrIndexSynchronizationStrategy strategy = new DirectSolrIndexSynchronizationStrategy();

	@Before
	public void init() throws FacetConfigServiceException
	{
		MockitoAnnotations.initMocks(this);

		final SolrConfig config = new SolrConfig();
		config.setMode(SolrServerMode.EMBEDDED);

		indexedType.setCode(PRODUCT_TYPECODE);

		final Map<String,IndexedType> indexedTypes = new HashMap<>();
		indexedTypes.put("bo_product", indexedType);

		IndexConfig indexConfig = new IndexConfig();
		indexConfig.setIndexedTypes(indexedTypes);

		when(searchConfig.getSolrFacetSearchConfig()).thenReturn(solrFacetSearchConfigModel);
		when(searchConfig.getSolrFacetSearchConfig().getName()).thenReturn(CONFIG_NAME);
		when(typeService.getTypeForCode(PRODUCT_TYPECODE)).thenReturn(typeModel);
		when(backofficeFacetSearchConfigService.getSolrFacetSearchConfigModel(PRODUCT_TYPECODE)).thenReturn(solrFacetSearchConfigModel);
		when(facetSearchConfigService.getConfiguration(CONFIG_NAME)).thenReturn(facetSearchConfig);
		when(facetSearchConfig.getSolrConfig()).thenReturn(config);
		when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);


	}

	@Test
	public void testRemoveItem() throws FacetConfigServiceException, IndexerException
	{
		strategy.removeItem(PRODUCT_TYPECODE, PK_);

		verify(facetSearchConfigService).getConfiguration(CONFIG_NAME);
		final ArgumentCaptor<List> pkList = ArgumentCaptor.forClass(List.class);
		verify(indexerService).deleteTypeIndex(eq(facetSearchConfig), eq(indexedType), pkList.capture());
		assertThat(pkList.getValue()).hasSize(1);
		assertThat(pkList.getValue()).containsExactly(PK.fromLong(PK_));
	}


	@Test
	public void testUpdateItem() throws FacetConfigServiceException, IndexerException
	{
		strategy.updateItem(PRODUCT_TYPECODE, PK_);

		verify(facetSearchConfigService).getConfiguration(CONFIG_NAME);
		final ArgumentCaptor<List> pkList = ArgumentCaptor.forClass(List.class);
		verify(indexerService).updateTypeIndex(
				eq(facetSearchConfig),
				eq(indexedType),
				pkList.capture()
		);
		assertThat(pkList.getValue()).hasSize(1);
		assertThat(pkList.getValue()).containsExactly(PK.fromLong(PK_));
	}

}
