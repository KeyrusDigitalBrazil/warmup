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
package de.hybris.platform.commerceservices.search.solrfacetsearch.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.commerceservices.search.solrfacetsearch.data.FilterQueryOperator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;


public class SearchFiltersPopulatorTest
{
	private static final String KEY1 = "key1";
	private static final String KEY2 = "key2";
	private static final String KEY3 = "key3";
	private static final String KEY4 = "key4";
	private static final String VALUE1 = "value1";
	private static final String VALUE2 = "value2";
	private static final String VALUE3 = "value3";
	private static final String VALUE4 = "value4";

	private static final String CATEGORY_CODE = "categoryCode";

	private SearchFiltersPopulator<FacetSearchConfig, IndexedTypeSort> searchFiltersPopulator;

	@Before
	public void setup()
	{
		searchFiltersPopulator = new SearchFiltersPopulator<FacetSearchConfig, IndexedTypeSort>();
	}

	@Test
	public void shouldPopulateDifferentQueryTypes()
	{
		final SearchQueryPageableData<SolrSearchQueryData> source = null;
		final SolrSearchRequest<FacetSearchConfig, IndexedType, IndexedProperty, SearchQuery, IndexedTypeSort> target = populateTarget();

		assertNull("Indexed property should be null", target.getIndexedPropertyValues());
		assertTrue("Facet values should be empty", CollectionUtils.isEmpty(target.getSearchQuery().getFacetValues()));
		assertTrue("Filter queries should be empty", CollectionUtils.isEmpty(target.getSearchQuery().getFilterQueries()));

		searchFiltersPopulator.populate(source, target);

		assertTrue("Indexed property values should not be empty", CollectionUtils.isNotEmpty(target.getIndexedPropertyValues()));
		assertEquals("Indexed property values size should be 4", 4, target.getIndexedPropertyValues().size());
		assertTrue("Facet values should not be empty", CollectionUtils.isNotEmpty(target.getSearchQuery().getFacetValues()));
		assertEquals("Facet values size should be 4",4, target.getSearchQuery().getFacetValues().size());
		assertTrue("Filter queries should not be empty", CollectionUtils.isNotEmpty(target.getSearchQuery().getFilterQueries()));
		assertEquals("Filter queries size should be 3", 3, target.getSearchQuery().getFilterQueries().size());
	}

	protected SolrSearchRequest<FacetSearchConfig, IndexedType, IndexedProperty, SearchQuery, IndexedTypeSort> populateTarget()
	{
		final SolrSearchRequest<FacetSearchConfig, IndexedType, IndexedProperty, SearchQuery, IndexedTypeSort> solrSearchRequest = new SolrSearchRequest<>();

		final SolrSearchQueryTermData solrSearchQueryTermData1 = new SolrSearchQueryTermData();
		solrSearchQueryTermData1.setKey(KEY1);
		solrSearchQueryTermData1.setValue(VALUE1);

		final SolrSearchQueryTermData solrSearchQueryTermData2 = new SolrSearchQueryTermData();
		solrSearchQueryTermData2.setKey(KEY2);
		solrSearchQueryTermData2.setValue(VALUE2);

		final SolrSearchQueryTermData solrSearchQueryTermData3 = new SolrSearchQueryTermData();
		solrSearchQueryTermData3.setKey(KEY3);
		solrSearchQueryTermData3.setValue(VALUE3);

		final SolrSearchQueryTermData solrSearchQueryTermData4 = new SolrSearchQueryTermData();
		solrSearchQueryTermData4.setKey(KEY4);
		solrSearchQueryTermData4.setValue(VALUE4);

		List<SolrSearchQueryTermData> filterTerms = new ArrayList<>();
		filterTerms.add(solrSearchQueryTermData1);
		filterTerms.add(solrSearchQueryTermData2);
		filterTerms.add(solrSearchQueryTermData3);
		filterTerms.add(solrSearchQueryTermData4);

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		solrSearchQueryData.setFilterTerms(filterTerms);
		solrSearchQueryData.setCategoryCode(CATEGORY_CODE);

		final SolrSearchFilterQueryData solrSearchFilterQueryData3 = new SolrSearchFilterQueryData();
		solrSearchFilterQueryData3.setKey(KEY3);
		solrSearchFilterQueryData3.setOperator(FilterQueryOperator.AND);
		solrSearchFilterQueryData3.setValues(new HashSet<>());

		final SolrSearchFilterQueryData solrSearchFilterQueryData4 = new SolrSearchFilterQueryData();
		solrSearchFilterQueryData4.setKey(KEY4);
		solrSearchFilterQueryData4.setValues(new HashSet<>());

		final List<SolrSearchFilterQueryData> filterQueries = new ArrayList<>();
		filterQueries.add(solrSearchFilterQueryData3);
		filterQueries.add(solrSearchFilterQueryData4);

		solrSearchQueryData.setFilterQueries(filterQueries);

		solrSearchRequest.setSearchQueryData(solrSearchQueryData);

		final IndexedType indexedType = new IndexedType();
		indexedType.setIndexedProperties(populateIndexedProperties());

		final SearchQuery searchQuery = new SearchQuery(new FacetSearchConfig(), indexedType);

		solrSearchRequest.setSearchQuery(searchQuery);

		solrSearchRequest.setIndexedType(indexedType);

		return solrSearchRequest;
	}

	protected Map<String, IndexedProperty> populateIndexedProperties()
	{
		final HashMap<String, IndexedProperty> indexedProperties = new HashMap<>();

		final IndexedProperty indexedProperty1 = new IndexedProperty();
		indexedProperty1.setFacet(true);

		final IndexedProperty indexedProperty2 = new IndexedProperty();
		indexedProperty2.setFacet(true);

		final IndexedProperty indexedProperty3 = new IndexedProperty();
		indexedProperty3.setFacet(false);

		final IndexedProperty indexedProperty4 = new IndexedProperty();
		indexedProperty4.setFacet(false);

		indexedProperties.put(KEY1, indexedProperty1);
		indexedProperties.put(KEY2, indexedProperty2);
		indexedProperties.put(KEY3, indexedProperty3);
		indexedProperties.put(KEY4, indexedProperty4);

		return indexedProperties;
	}
}
