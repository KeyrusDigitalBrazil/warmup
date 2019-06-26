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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.Test;


public class DeleteFromIndexTest extends AbstractIntegrationTest
{
	private static final String PRODUCT1_CODE = "product1";

	@Resource
	private DefaultIndexerService indexerService;
	@Resource
	private ProductService productService;
	@Resource
	private ModelService modelService;
	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/DeleteFromIndexTest.csv");
	}

	@Test
	public void testDeleteItemsFromDatabaseFirst() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		indexerService.performFullIndex(facetSearchConfig);

		final ProductModel product = productService.getProductForCode(getProductCode(PRODUCT1_CODE));

		final PK productPk = product.getPk();

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.addQuery("pk", productPk.getLong().toString());
		final SearchResult resultBefore = facetSearchService.search(query);

		assertTrue(resultBefore.getResultPKs().contains(productPk));

		// when
		modelService.remove(product);

		// then
		final SearchResult resultAfterDatabaseDelete = facetSearchService.search(query);

		assertTrue(resultAfterDatabaseDelete.getResultPKs().contains(productPk));

		// when
		indexerService.deleteTypeIndex(facetSearchConfig, indexedType, Arrays.asList(productPk));

		// then
		final SearchResult resultAfter = facetSearchService.search(query);

		assertFalse(resultAfter.getResultPKs().contains(productPk));
	}

	@Test
	public void testDeleteItemsFromIndexFirst() throws Exception
	{
		// given
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		indexerService.performFullIndex(facetSearchConfig);

		final ProductModel product = productService.getProductForCode(getProductCode(PRODUCT1_CODE));

		final PK productPk = product.getPk();

		final SearchQuery query = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query.addQuery("pk", productPk.getLong().toString());
		final SearchResult resultBefore = facetSearchService.search(query);

		assertTrue(resultBefore.getResultPKs().contains(productPk));

		// when
		indexerService.deleteTypeIndex(facetSearchConfig, indexedType, Arrays.asList(productPk));

		modelService.remove(product);

		// then
		final SearchResult resultAfter = facetSearchService.search(query);

		assertFalse(resultAfter.getResultPKs().contains(productPk));
	}

	protected String getProductCode(final String productId)
	{
		return productId + getTestId();
	}
}
