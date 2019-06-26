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

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.Facet;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FacetValue;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;


/**
 * Tests the Solr search with name converter support.
 */
public class CategoryFacetDisplayNameProviderTest extends AbstractIntegrationTest
{
	private final static String CATALOG_ID = "hwcatalog";
	private final static String VERSION_ONLINE = "Online";

	private final static String FACET_FIELD = "categoryCode";

	@Resource
	private CatalogVersionService catalogVersionService;

	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	@Override
	protected void loadData() throws Exception
	{
		importCsv("/test/solrClassificationSystemOnline.csv", "utf-8");
		importCsv("/test/solrClassificationSystemStaged.csv", "utf-8");
	}

	/**
	 * Creates the index for the hwcatalog_online, searches for the product with code "HW2300-2356", and tests if the
	 * category names are correctly converted.
	 *
	 * @throws Exception
	 */
	@Test
	public void testCategoryFacetDisplayNameProvider() throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();
		final CatalogVersionModel hwOnline = catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_ONLINE);

		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery query = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		query.setCatalogVersions(Collections.singletonList(hwOnline));

		final FacetField facetField = new FacetField(FACET_FIELD);
		facetField.setDisplayNameProvider("categoryFacetDisplayNameProvider");
		query.addFacet(facetField);

		query.setLanguage("de");
		SearchResult result = facetSearchService.search(query);
		Facet facet = result.getFacet(FACET_FIELD);
		Map<String, String> categoryNames = createGermanNames();
		for (final FacetValue fv : facet.getFacetValues())
		{
			final String key = fv.getName();
			final String displayName = categoryNames.get(key);

			if (displayName != null)
			{
				assertEquals(displayName, fv.getDisplayName());
			}
		}

		query.setLanguage("en");
		result = facetSearchService.search(query);
		facet = result.getFacet(FACET_FIELD);
		categoryNames = createEnglishNames();
		for (final FacetValue fv : facet.getFacetValues())
		{
			final String key = fv.getName();
			final String displayName = categoryNames.get(key);

			if (displayName != null)
			{
				assertEquals(displayName, fv.getDisplayName());
			}
		}
	}

	private Map<String, String> createGermanNames()
	{
		final Map<String, String> result = new HashMap<String, String>();
		result.put("HW2000", "Systemkomponenten_online_de");
		result.put("HW2300", "Grafikkarten_online_de");
		result.put("electronics", "Elektronische Geräte");
		result.put("graphics", "Grafikkarten");
		result.put("hardware", "Hardware");
		result.put("specialoffers", "Sonderangebote_online_de");
		return result;
	}

	private Map<String, String> createEnglishNames()
	{
		final Map<String, String> result = new HashMap<String, String>();
		result.put("HW2000", "System components_online_en");
		result.put("HW2300", "Graphic cards_online_en");
		result.put("electronics", "Electronical Goods");
		result.put("graphics", "Graphic cards");
		result.put("hardware", "Hardware");
		result.put("specialoffers", "Special offers_online_en");
		return result;
	}
}
