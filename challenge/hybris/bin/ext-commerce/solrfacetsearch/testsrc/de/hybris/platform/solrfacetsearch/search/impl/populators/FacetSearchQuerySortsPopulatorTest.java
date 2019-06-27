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
package de.hybris.platform.solrfacetsearch.search.impl.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeSortField;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.OrderField;
import de.hybris.platform.solrfacetsearch.search.OrderField.SortOrder;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.context.FacetSearchContext;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class FacetSearchQuerySortsPopulatorTest
{
	public static final String FIELD1 = "field1";
	public static final String TRANSLATED_FIELD1 = "translatedField1";

	public static final String FIELD2 = "field2";
	public static final String TRANSLATED_FIELD2 = "translatedField2";

	public static final String NAMED_SORT_CODE = "namedSortCode";

	public static final String PROMOTED_ITEM_PK_STR = "1";

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	@Mock
	private FacetSearchContext facetSearchContext;

	private FacetSearchQuerySortsPopulator facetSearchQuerySortsPopulator;
	private SearchQueryConverterData searchQueryConverterData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);

		facetSearchQuerySortsPopulator = new FacetSearchQuerySortsPopulator();
		facetSearchQuerySortsPopulator.setFieldNameTranslator(fieldNameTranslator);

		searchQueryConverterData = new SearchQueryConverterData();
		searchQueryConverterData.setSearchQuery(searchQuery);
		searchQueryConverterData.setFacetSearchContext(facetSearchContext);

		given(fieldNameTranslator.translate(searchQuery, FIELD1, FieldType.SORT)).willReturn(TRANSLATED_FIELD1);
		given(fieldNameTranslator.translate(searchQuery, FIELD2, FieldType.SORT)).willReturn(TRANSLATED_FIELD2);
	}

	@Test
	public void populateWithEmptySorts()
	{
		// given
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQuerySortsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		final List<SortClause> sorts = solrQuery.getSorts();

		final SortClause scoreClause = new SortClause(OrderField.SCORE, ORDER.desc);
		assertThat(sorts, Matchers.hasSize(1));
		assertThat(sorts, CoreMatchers.hasItem(scoreClause));
	}

	@Test
	public void populateWithSingleSort()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addSort(FIELD1, SortOrder.ASCENDING);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQuerySortsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		final SortClause sortClause = new SortClause(TRANSLATED_FIELD1, ORDER.asc);
		assertThat(solrQuery.getSorts(), CoreMatchers.hasItem(sortClause));
	}

	@Test
	public void populateWithMultipleSorts()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addSort(FIELD1, SortOrder.ASCENDING);
		searchQuery.addSort(FIELD2, SortOrder.DESCENDING);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQuerySortsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		final SortClause sortClause1 = new SortClause(TRANSLATED_FIELD1, ORDER.asc);
		final SortClause sortClause2 = new SortClause(TRANSLATED_FIELD2, ORDER.desc);

		final List<SortClause> sorts = solrQuery.getSorts();
		assertThat(sorts, Matchers.hasSize(2));
		assertEquals(sorts.get(0), sortClause1);
		assertEquals(sorts.get(1), sortClause2);
	}

	@Test
	public void populateWithPromotedSort()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();

		final IndexedTypeSort namedSort = new IndexedTypeSort();
		namedSort.setCode(NAMED_SORT_CODE);
		namedSort.setApplyPromotedItems(true);
		final IndexedTypeSortField namedSortField = new IndexedTypeSortField();
		namedSortField.setFieldName(FIELD1);
		namedSortField.setAscending(false);
		namedSort.setFields(Arrays.asList(namedSortField));
		given(facetSearchContext.getNamedSort()).willReturn(namedSort);

		searchQuery.addPromotedItem(PK.parse(PROMOTED_ITEM_PK_STR));

		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQuerySortsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		final String promotedItemSortClauseItem = "query({!v='pk:" + PROMOTED_ITEM_PK_STR + "^=" + FacetSearchQuerySortsPopulator.MAX_PROMOTED_RESULT_SCORE + "'},1)";

		final SortClause sortClause1 = new SortClause(TRANSLATED_FIELD1, ORDER.desc);
		final SortClause promotedItemClause = new SortClause(promotedItemSortClauseItem, ORDER.desc);

		final List<SortClause> sorts = solrQuery.getSorts();
		assertThat(sorts, Matchers.hasSize(2));
		assertEquals(sorts.get(0), promotedItemClause);
		assertEquals(sorts.get(1), sortClause1);
	}

	@Test
	public void populateWithoutPromotedSort()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();

		final IndexedTypeSort namedSort = new IndexedTypeSort();
		namedSort.setCode(NAMED_SORT_CODE);
		namedSort.setApplyPromotedItems(false);

		final IndexedTypeSortField namedSortField = new IndexedTypeSortField();
		namedSortField.setFieldName(FIELD1);
		namedSortField.setAscending(false);
		namedSort.setFields(Arrays.asList(namedSortField));

		given(facetSearchContext.getNamedSort()).willReturn(namedSort);

		searchQuery.addPromotedItem(PK.parse(PROMOTED_ITEM_PK_STR));

		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQuerySortsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		final SortClause sortClause1 = new SortClause(TRANSLATED_FIELD1, ORDER.desc);

		final List<SortClause> sorts = solrQuery.getSorts();
		assertThat(sorts, Matchers.hasSize(1));
		assertEquals(sorts.get(0), sortClause1);
	}
}