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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.Operator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.testframework.Assert;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Test;


public class SearchWithRawQueryTest extends AbstractIntegrationTest
{
	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Test
	public void testRawSolrQueryShortcut() throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnlineCatalogVersion = catalogVersionService.getCatalogVersion(HW_CATALOG,
				ONLINE_CATALOG_VERSION);

		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query1 = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query1.setCatalogVersions(Arrays.asList(hwOnlineCatalogVersion));
		query1.setDefaultOperator(Operator.AND);
		query1.addQuery("code", "HW2300-2356");
		final SearchResult result = facetSearchService.search(query1);
		final Collection<ProductModel> products1 = (Collection<ProductModel>) result.getResults();

		final SearchQuery query2 = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query2.setDefaultOperator(Operator.AND);
		query2.setCatalogVersions(Arrays.asList(hwOnlineCatalogVersion));
		query2.addRawQuery("code_string:HW2300-2356");
		final SearchResult result2 = facetSearchService.search(query2);
		final Collection<ProductModel> products2 = (Collection<ProductModel>) result2.getResults();

		Assert.assertCollection(products1, products2);

		final SearchQuery query3 = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query3.setDefaultOperator(Operator.AND);
		query3.setCatalogVersions(Arrays.asList(hwOnlineCatalogVersion));
		query3.setLanguage("en");
		query3.addQuery("name", "Intel Desktop Board");
		query3.addQuery("code", Operator.OR, "HW2200-0623", "HW2200-0812");

		final SearchResult result3 = facetSearchService.search(query3);
		final Collection<ProductModel> products3 = (Collection<ProductModel>) result3.getResults();

		final SearchQuery query4 = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query4.setDefaultOperator(Operator.AND);
		query4.setCatalogVersions(Arrays.asList(hwOnlineCatalogVersion));
		query4.addRawQuery("(name_text_en:Intel\\ Desktop\\ Board) AND ((code_string:HW2200-0623) OR (code_string:HW2200-0812))");

		final SearchResult result4 = facetSearchService.search(query4);
		final Collection<ProductModel> products4 = (Collection<ProductModel>) result4.getResults();

		Assert.assertCollection(products3, products4);

		final SearchQuery query5 = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		query5.setDefaultOperator(Operator.AND);
		query5.setCatalogVersions(Arrays.asList(hwOnlineCatalogVersion));
		query5.addQuery("name", "Intel Desktop Board");
		query5.addRawQuery("((code_string:HW2200-0623) OR (code_string:HW2200-0812))");

		final SearchResult result5 = facetSearchService.search(query5);
		final Collection<ProductModel> products5 = (Collection<ProductModel>) result5.getResults();

		Assert.assertCollection(products3, products5);
	}
}
