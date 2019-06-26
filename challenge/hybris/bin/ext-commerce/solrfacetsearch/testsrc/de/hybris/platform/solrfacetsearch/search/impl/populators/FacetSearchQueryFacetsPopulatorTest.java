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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetType;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;

import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class FacetSearchQueryFacetsPopulatorTest
{
	public static final String FIELD = "field";
	public static final String TRANSLATED_FIELD = "translatedField";

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	private FacetSearchQueryFacetsPopulator facetSearchQueryFacetsPopulator;
	private SearchQueryConverterData searchQueryConverterData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		final SearchQuery searchQuery = new SearchQuery(facetSearchConfig, indexedType);

		facetSearchQueryFacetsPopulator = new FacetSearchQueryFacetsPopulator();
		facetSearchQueryFacetsPopulator.setFieldNameTranslator(fieldNameTranslator);

		searchQueryConverterData = new SearchQueryConverterData();
		searchQueryConverterData.setSearchQuery(searchQuery);

		given(fieldNameTranslator.translate(searchQuery, FIELD, FieldType.INDEX)).willReturn(TRANSLATED_FIELD);
	}

	@Test
	public void populateWithEmptyFacets()
	{
		// given
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getFacetFields()).isNull();
	}

	@Test
	public void populateWithRefineFacet()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addFacet(FIELD, FacetType.REFINE);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getFacetFields()).hasSize(1).contains(TRANSLATED_FIELD);
	}

	@Test
	public void populateWithMultiSelectAndFacet()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addFacet(FIELD, FacetType.MULTISELECTAND);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getFacetFields()).hasSize(1).contains(TRANSLATED_FIELD);
	}

	@Test
	public void populateWithMultiSelectOrFacet()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addFacet(FIELD, FacetType.MULTISELECTOR);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryFacetsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertThat(solrQuery.getFacetFields()).hasSize(1).contains(TRANSLATED_FIELD);
	}
}
