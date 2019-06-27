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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexOperationService;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.util.Collections;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;


public class SolrIndexHotUpdateTest extends AbstractIntegrationTest
{
	private static final String PRODUCT_CODE = "product1";
	private static final String PRODUCT_NAME = "test product";

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private SolrIndexService solrIndexService;

	@Resource
	private SolrIndexOperationService solrIndexOperationService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SolrIndexHotUpdateTest.csv");
	}

	@Test
	public void shouldChangeLastIndexTimeWhenFullIndex()
			throws FacetConfigServiceException, InterruptedException, IndexerException, SolrServiceException
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		// when
		indexerService.performFullIndex(getFacetSearchConfig());
		final SolrIndexModel activeIndex1 = solrIndexService.getActiveIndex(facetSearchConfig.getName(),
				indexedType.getIdentifier());
		final Date lastIndexTime1 = solrIndexOperationService.getLastIndexOperationTime(activeIndex1);

		// wait for one second since MySQL doesn't store milliseconds !
		Thread.sleep(1000);

		indexerService.performFullIndex(getFacetSearchConfig());
		final SolrIndexModel activeIndex2 = solrIndexService.getActiveIndex(facetSearchConfig.getName(),
				indexedType.getIdentifier());
		final Date lastIndexTime2 = solrIndexOperationService.getLastIndexOperationTime(activeIndex2);

		// then
		assertNotNull(lastIndexTime2);
		assertTrue(lastIndexTime2.after(lastIndexTime1));
	}

	@Test
	public void shouldNotChangeLastIndexTimeWhenPartialUpdate()
			throws FacetConfigServiceException, InterruptedException, IndexerException, SolrServiceException
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		// when
		indexerService.performFullIndex(getFacetSearchConfig());
		final SolrIndexModel activeIndex1 = solrIndexService.getActiveIndex(facetSearchConfig.getName(),
				indexedType.getIdentifier());
		final Date lastIndexTime1 = solrIndexOperationService.getLastIndexOperationTime(activeIndex1);

		// wait for one second since MySQL doesn't store milliseconds !
		Thread.sleep(1000);

		// update product
		final ProductModel product = productService.getProductForCode(getProductCode());
		product.setName(PRODUCT_NAME);
		modelService.save(product);

		indexerService.updatePartialTypeIndex(facetSearchConfig, indexedType, Collections.emptyList(),
				Collections.singletonList(product.getPk()), Collections.emptyMap());
		final SolrIndexModel activeIndex2 = solrIndexService.getActiveIndex(facetSearchConfig.getName(),
				indexedType.getIdentifier());
		final Date lastIndexTime2 = solrIndexOperationService.getLastIndexOperationTime(activeIndex2);

		// then
		assertNotNull(lastIndexTime2);
		//date should change
		assertEquals(lastIndexTime2, lastIndexTime1);
	}

	@Override
	protected String getProductCode()
	{
		return PRODUCT_CODE + getTestId();
	}
}
