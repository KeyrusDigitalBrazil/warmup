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
package de.hybris.platform.solrfacetsearch.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SearchConfig;
import de.hybris.platform.solrfacetsearch.config.SearchQueryProperty;
import de.hybris.platform.solrfacetsearch.config.SearchQuerySort;
import de.hybris.platform.solrfacetsearch.config.SearchQueryTemplate;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FacetSearchStrategyFactory;
import de.hybris.platform.solrfacetsearch.search.OrderField;
import de.hybris.platform.solrfacetsearch.search.OrderField.SortOrder;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.BeanFactory;


@UnitTest
public class DefaultFacetSearchServiceTest
{
	protected static final String SOME_TEMPLATE = "SOME_TEMPLATE";
	protected static final String GROUP_INDEXED_PROPERTY_NAME = "groupIndexedPropertyName";
	protected static final String DEFAULT_FACET_PROPERTY = "DEFAULT_FACET_PROPERTY";
	protected static final String NOT_INCLUDED_INDEXED_PROPERTY = "NOT_INCLUDED_INDEXED_PROPERTY";
	protected static final String FREE_TEXT_INDEXED_PROPERTY = "FREE_TEXT_INDEXED_PROPERTY";
	protected static final String SEARCH_QUERY_SORT_FIELD_1 = "sortField1";
	protected static final String SEARCH_QUERY_SORT_FIELD_2 = "sortField2";
	protected static final String INDEXED_PROPERTY_3 = "indexedProperty3";
	protected static final String INDEXED_PROPERTY_1 = "indexedProperty1";
	protected static final String INDEXED_PROPERTY_2 = "indexedProperty2";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private FacetSearchStrategyFactory facetSearchStrategyFactory;

	@Mock
	private BeanFactory beanFactory;

	@InjectMocks
	private final DefaultFacetSearchService facetSearchService = new DefaultFacetSearchService();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testFreeTextSearchQueryFromTemplateNulls()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQueryFromTemplate(null, null, null, null);

	}

	@Test
	public void testSearchQueryFromTemplateTemplateNotFoundFallback()
	{
		//given
		final IndexedType indexedType = new IndexedType();
		indexedType.setSearchQueryTemplates(Collections.EMPTY_MAP);
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();

		//when
		final SearchQuery searchQuery = facetSearchService.createSearchQueryFromTemplate(facetSearchConfig, indexedType,
				"not found");

		//then
		assertNotNull(searchQuery);
	}

	@Test
	public void testFreeTextSearchQueryFromTemplateTemplateNotFoundFallback()
	{
		//given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();

		final IndexedType indexedType = new IndexedType();
		indexedType.setSearchQueryTemplates(Collections.EMPTY_MAP);
		indexedType.setGroup(true);
		indexedType.setGroupLimit(20);
		indexedType.setGroupFacets(true);
		indexedType.setGroupFieldName(INDEXED_PROPERTY_1);

		final IndexedProperty indexedProperty = new IndexedProperty();
		indexedProperty.setName(INDEXED_PROPERTY_1);
		indexedProperty.setIncludeInResponse(true);
		indexedProperty.setFtsFuzzyQuery(true);
		indexedProperty.setFtsPhraseQuery(true);
		indexedProperty.setFtsQuery(true);
		indexedProperty.setFtsWildcardQuery(true);

		final IndexedProperty facetIndexedProperty = new IndexedProperty();
		facetIndexedProperty.setName(INDEXED_PROPERTY_2);
		facetIndexedProperty.setFacet(true);
		facetIndexedProperty.setFacetType(FacetType.MULTISELECTAND);
		facetIndexedProperty.setIncludeInResponse(true);

		final Map<String, IndexedProperty> searchQueryProperties = new HashMap<>();
		searchQueryProperties.put(INDEXED_PROPERTY_1, indexedProperty);
		searchQueryProperties.put(INDEXED_PROPERTY_2, facetIndexedProperty);
		indexedType.setIndexedProperties(searchQueryProperties);

		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setPageSize(50);
		searchConfig.setLegacyMode(false);
		searchConfig.setDefaultSortOrder(Arrays.asList(OrderField.SCORE));
		searchConfig.setRestrictFieldsInResponse(true);

		facetSearchConfig.setSearchConfig(searchConfig);

		//when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQueryFromTemplate(facetSearchConfig, indexedType,
				"not found", StringUtils.EMPTY);

		//then
		assertEquals(searchQuery.isGroupFacets(), indexedType.isGroupFacets());
		assertEquals(searchQuery.getGroupCommands().get(0).getField(), indexedProperty.getName());
		assertEquals(searchQuery.getFacets().get(0).getField(), facetIndexedProperty.getName());
		assertTrue(searchQuery.getFields().contains(indexedProperty.getName()));
		assertTrue(searchQuery.getFields().contains(facetIndexedProperty.getName()));
	}

	@Test
	public void testSearchQueryFromTemplate()
	{
		//given
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setPageSize(50);
		searchConfig.setLegacyMode(false);
		searchConfig.setDefaultSortOrder(Arrays.asList(OrderField.SCORE));

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSearchConfig(searchConfig);

		final IndexedProperty groupIndexedProperty = new IndexedProperty();
		groupIndexedProperty.setName(GROUP_INDEXED_PROPERTY_NAME);

		final SearchQueryTemplate defaultSearchQueryTemplate = new SearchQueryTemplate();

		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setGroup(true);
		searchQueryTemplate.setGroupProperty(groupIndexedProperty);
		searchQueryTemplate.setShowFacets(true);
		searchQueryTemplate.setRestrictFieldsInResponse(true);

		final Map<String, SearchQueryTemplate> templatesMap = new HashMap<>();
		templatesMap.put(DefaultFacetSearchService.DEFAULT_QUERY_TEMPLATE_NAME, defaultSearchQueryTemplate);
		templatesMap.put(SOME_TEMPLATE, searchQueryTemplate);

		final IndexedType indexedType = new IndexedType();
		indexedType.setSearchQueryTemplates(templatesMap);
		indexedType.setFieldsValuesProvider(StringUtils.EMPTY);

		final SearchQueryProperty facetSearchQueryProperty = new SearchQueryProperty();
		facetSearchQueryProperty.setFacet(true);
		facetSearchQueryProperty.setIncludeInResponse(true);
		facetSearchQueryProperty.setIndexedProperty(DEFAULT_FACET_PROPERTY);

		final SearchQueryProperty notIncludedInResponseSearchQueryProperty = new SearchQueryProperty();
		notIncludedInResponseSearchQueryProperty.setIncludeInResponse(false);
		notIncludedInResponseSearchQueryProperty.setIndexedProperty(NOT_INCLUDED_INDEXED_PROPERTY);

		final Map<String, SearchQueryProperty> searchQueriesProperties = new HashMap<>();
		searchQueriesProperties.put(DEFAULT_FACET_PROPERTY, facetSearchQueryProperty);
		searchQueriesProperties.put(NOT_INCLUDED_INDEXED_PROPERTY, notIncludedInResponseSearchQueryProperty);

		searchQueryTemplate.setSearchQueryProperties(searchQueriesProperties);

		//when
		final SearchQuery searchQuery = facetSearchService.createSearchQueryFromTemplate(facetSearchConfig, indexedType,
				SOME_TEMPLATE);

		//then
		assertEquals(searchQuery.isGroupFacets(), searchQueryTemplate.isGroupFacets());
		assertEquals(searchQuery.getGroupCommands().get(0).getField(), groupIndexedProperty.getName());
		assertEquals(searchQuery.getFacets().get(0).getField(), facetSearchQueryProperty.getIndexedProperty());
		assertTrue(searchQuery.getFields().contains(facetSearchQueryProperty.getIndexedProperty()));
		assertFalse(searchQuery.getFields().contains(notIncludedInResponseSearchQueryProperty.getIndexedProperty()));
	}

	@Test
	public void testFreeTextSearchQueryFromTemplate()
	{
		//given
		final SearchConfig searchConfig = new SearchConfig();
		searchConfig.setPageSize(50);
		searchConfig.setLegacyMode(false);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		facetSearchConfig.setSearchConfig(searchConfig);

		final IndexedProperty groupIndexedProperty = new IndexedProperty();
		groupIndexedProperty.setName(GROUP_INDEXED_PROPERTY_NAME);

		final SearchQueryTemplate defaultSearchQueryTemplate = new SearchQueryTemplate();

		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setGroup(true);
		searchQueryTemplate.setGroupProperty(groupIndexedProperty);
		searchQueryTemplate.setShowFacets(true);
		searchQueryTemplate.setRestrictFieldsInResponse(true);

		final Map<String, SearchQueryTemplate> templatesMap = new HashMap<>();
		templatesMap.put(DefaultFacetSearchService.DEFAULT_QUERY_TEMPLATE_NAME, defaultSearchQueryTemplate);
		templatesMap.put(SOME_TEMPLATE, searchQueryTemplate);

		final IndexedType indexedType = new IndexedType();
		indexedType.setSearchQueryTemplates(templatesMap);
		indexedType.setFieldsValuesProvider(StringUtils.EMPTY);

		final SearchQueryProperty facetSearchQueryProperty = new SearchQueryProperty();
		facetSearchQueryProperty.setFacet(true);
		facetSearchQueryProperty.setIncludeInResponse(true);
		facetSearchQueryProperty.setIndexedProperty(DEFAULT_FACET_PROPERTY);

		final SearchQueryProperty notIncludedInResponseSearchQueryProperty = new SearchQueryProperty();
		notIncludedInResponseSearchQueryProperty.setIncludeInResponse(false);
		notIncludedInResponseSearchQueryProperty.setIndexedProperty(NOT_INCLUDED_INDEXED_PROPERTY);

		final SearchQueryProperty freeTextSearchQueryProperty = new SearchQueryProperty();
		freeTextSearchQueryProperty.setIncludeInResponse(true);
		freeTextSearchQueryProperty.setIndexedProperty(FREE_TEXT_INDEXED_PROPERTY);
		freeTextSearchQueryProperty.setFtsFuzzyQuery(true);
		freeTextSearchQueryProperty.setFtsPhraseQuery(true);
		freeTextSearchQueryProperty.setFtsQuery(true);
		freeTextSearchQueryProperty.setFtsWildcardQuery(true);

		final Map<String, SearchQueryProperty> searchQueriesProperties = new HashMap<>();
		searchQueriesProperties.put(DEFAULT_FACET_PROPERTY, facetSearchQueryProperty);
		searchQueriesProperties.put(NOT_INCLUDED_INDEXED_PROPERTY, notIncludedInResponseSearchQueryProperty);
		searchQueriesProperties.put(FREE_TEXT_INDEXED_PROPERTY, freeTextSearchQueryProperty);

		searchQueryTemplate.setSearchQueryProperties(searchQueriesProperties);

		//when
		final SearchQuery searchQuery = facetSearchService.createFreeTextSearchQueryFromTemplate(facetSearchConfig, indexedType,
				SOME_TEMPLATE, StringUtils.EMPTY);

		//then
		assertEquals(searchQuery.isGroupFacets(), searchQueryTemplate.isGroupFacets());
		assertEquals(searchQuery.getGroupCommands().get(0).getField(), groupIndexedProperty.getName());
		assertEquals(searchQuery.getFacets().get(0).getField(), facetSearchQueryProperty.getIndexedProperty());
		assertTrue(searchQuery.getFields().contains(facetSearchQueryProperty.getIndexedProperty()));
		assertFalse(searchQuery.getFields().contains(notIncludedInResponseSearchQueryProperty.getIndexedProperty()));
	}

	@Test
	public void testPopulateEmptySortFields()
	{
		// given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		// when
		facetSearchService.populateSortFields(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		// then
		assertEquals(searchQuery.getSorts().size(), 0);
	}

	@Test
	public void testPopulateSortFields()
	{
		// given
		final Collection<SearchQuerySort> searchQuerySorts = new ArrayList<>();
		final SearchQuerySort searchQuerySort1 = new SearchQuerySort();
		searchQuerySort1.setAscending(true);
		searchQuerySort1.setField(SEARCH_QUERY_SORT_FIELD_1);
		searchQuerySorts.add(searchQuerySort1);

		final SearchQuerySort searchQuerySort2 = new SearchQuerySort();
		searchQuerySort2.setAscending(false);
		searchQuerySort2.setField(SEARCH_QUERY_SORT_FIELD_2);
		searchQuerySorts.add(searchQuerySort2);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		searchQueryTemplate.setSearchQuerySorts(searchQuerySorts);

		// when
		facetSearchService.populateSortFields(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		// then
		assertEquals(searchQuery.getSorts().size(), 2);
		assertEquals(searchQuery.getSorts().get(0).getField(), SEARCH_QUERY_SORT_FIELD_1);
		assertEquals(searchQuery.getSorts().get(0).getSortOrder(), SortOrder.ASCENDING);

		assertEquals(searchQuery.getSorts().get(1).getField(), SEARCH_QUERY_SORT_FIELD_2);
		assertEquals(searchQuery.getSorts().get(1).getSortOrder(), SortOrder.DESCENDING);
	}

	@Test
	public void testPopulateFacetFieldsWhenHidden()
	{
		// given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setShowFacets(false);

		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		// when
		facetSearchService.populateFacetFields(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		// then
		assertEquals(searchQuery.getFacets().size(), 0);
	}

	@Test
	public void testPopulateFacetFields()
	{
		// given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setShowFacets(true);

		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		final Map<String, SearchQueryProperty> searchQueryProperties = new HashMap<>();
		final SearchQueryProperty sqProperty1 = new SearchQueryProperty();
		sqProperty1.setFacet(true);
		sqProperty1.setIndexedProperty(INDEXED_PROPERTY_1);
		searchQueryProperties.put(INDEXED_PROPERTY_1, sqProperty1);

		final SearchQueryProperty sqProperty2 = new SearchQueryProperty();
		sqProperty2.setFacet(false);
		sqProperty2.setIndexedProperty(INDEXED_PROPERTY_2);
		searchQueryProperties.put(INDEXED_PROPERTY_2, sqProperty2);

		final SearchQueryProperty sqProperty3 = new SearchQueryProperty();
		sqProperty3.setFacet(true);
		sqProperty3.setIndexedProperty(INDEXED_PROPERTY_3);
		searchQueryProperties.put(INDEXED_PROPERTY_3, sqProperty3);

		searchQueryTemplate.setSearchQueryProperties(searchQueryProperties);

		// when
		facetSearchService.populateFacetFields(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		// then
		assertEquals(searchQuery.getFacets().size(), 2);
		for (final FacetField facet : searchQuery.getFacets())
		{
			assertThat(facet.getField(), CoreMatchers.anyOf(CoreMatchers.containsString(INDEXED_PROPERTY_1),
					CoreMatchers.containsString(INDEXED_PROPERTY_3)));
		}
	}

	@Test
	public void testPopulateFieldsWhenRestricted()
	{
		// given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setRestrictFieldsInResponse(true);

		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		final Map<String, SearchQueryProperty> searchQueryProperties = new HashMap<>();
		final SearchQueryProperty sqProperty1 = new SearchQueryProperty();
		sqProperty1.setIncludeInResponse(true);
		sqProperty1.setIndexedProperty(INDEXED_PROPERTY_1);
		searchQueryProperties.put(INDEXED_PROPERTY_1, sqProperty1);

		final SearchQueryProperty sqProperty2 = new SearchQueryProperty();
		sqProperty2.setIncludeInResponse(false);
		sqProperty2.setIndexedProperty(INDEXED_PROPERTY_2);
		searchQueryProperties.put(INDEXED_PROPERTY_2, sqProperty2);

		final SearchQueryProperty sqProperty3 = new SearchQueryProperty();
		sqProperty3.setIncludeInResponse(true);
		sqProperty3.setIndexedProperty(INDEXED_PROPERTY_3);
		searchQueryProperties.put(INDEXED_PROPERTY_3, sqProperty3);

		searchQueryTemplate.setSearchQueryProperties(searchQueryProperties);

		// when
		facetSearchService.populateFields(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		// then
		assertEquals(searchQuery.getFields().size(), 2);
		for (final String field : searchQuery.getFields())
		{
			assertThat(field, CoreMatchers.anyOf(CoreMatchers.containsString(INDEXED_PROPERTY_1),
					CoreMatchers.containsString(INDEXED_PROPERTY_2), CoreMatchers.containsString(INDEXED_PROPERTY_3)));
		}
	}

	@Test
	public void testPopulateFieldsWhenNotRestricted()
	{
		// given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setRestrictFieldsInResponse(false);

		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);
		final Map<String, SearchQueryProperty> searchQueryProperties = new HashMap<>();
		final SearchQueryProperty sqProperty1 = new SearchQueryProperty();
		sqProperty1.setIncludeInResponse(true);
		sqProperty1.setIndexedProperty(INDEXED_PROPERTY_1);
		searchQueryProperties.put(INDEXED_PROPERTY_1, sqProperty1);

		final SearchQueryProperty sqProperty2 = new SearchQueryProperty();
		sqProperty2.setIncludeInResponse(false);
		sqProperty2.setIndexedProperty(INDEXED_PROPERTY_2);
		searchQueryProperties.put(INDEXED_PROPERTY_2, sqProperty2);

		final SearchQueryProperty sqProperty3 = new SearchQueryProperty();
		sqProperty3.setIncludeInResponse(true);
		sqProperty3.setIndexedProperty(INDEXED_PROPERTY_3);
		searchQueryProperties.put(INDEXED_PROPERTY_3, sqProperty3);

		searchQueryTemplate.setSearchQueryProperties(searchQueryProperties);

		// when
		facetSearchService.populateFields(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		// then
		assertEquals(searchQuery.getFields().size(), 0);
	}

	@Test
	public void testPagination()
	{
		//given
		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		final SearchQueryTemplate searchQueryTemplate = new SearchQueryTemplate();
		searchQueryTemplate.setPageSize(2);

		//when
		facetSearchService.populatePagination(facetSearchConfig, indexedType, searchQueryTemplate, searchQuery);

		//then
		assertEquals(searchQuery.getPageSize(), searchQuery.getPageSize());
	}
}
