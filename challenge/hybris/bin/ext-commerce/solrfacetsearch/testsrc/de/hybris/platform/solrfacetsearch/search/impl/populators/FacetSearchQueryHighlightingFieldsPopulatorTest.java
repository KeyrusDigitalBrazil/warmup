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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.config.impl.HybrisConfiguration;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.impl.DefaultFacetSearchService;
import de.hybris.platform.solrfacetsearch.search.impl.SearchQueryConverterData;

import org.apache.commons.configuration.Configuration;
import org.apache.solr.client.solrj.SolrQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class FacetSearchQueryHighlightingFieldsPopulatorTest
{
	public static final String FIELD1 = "field1";
	public static final String TRANSLATED_FIELD1 = "translatedField1";

	public static final String FIELD2 = "field2";
	public static final String TRANSLATED_FIELD2 = "translatedField2";

	public static final String HIGHLIGHTING_METHOD_DEFAULT = "default";

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	@Mock
	private ConfigurationService configurationService;

	private FacetSearchService facetSearchService;
	private FacetSearchQueryHighlightingFieldsPopulator facetSearchQueryHighlightingFieldsPopulator;
	private SearchQueryConverterData searchQueryConverterData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		facetSearchService = new DefaultFacetSearchService();
		final SearchQuery searchQuery = facetSearchService.createSearchQuery(facetSearchConfig, indexedType);

		facetSearchQueryHighlightingFieldsPopulator = new FacetSearchQueryHighlightingFieldsPopulator();
		facetSearchQueryHighlightingFieldsPopulator.setFieldNameTranslator(fieldNameTranslator);
		facetSearchQueryHighlightingFieldsPopulator.setConfigurationService(configurationService);

		searchQueryConverterData = new SearchQueryConverterData();
		searchQueryConverterData.setSearchQuery(searchQuery);

		when(fieldNameTranslator.translate(searchQuery, FIELD1, FieldType.INDEX)).thenReturn(TRANSLATED_FIELD1);
		when(fieldNameTranslator.translate(searchQuery, FIELD2, FieldType.INDEX)).thenReturn(TRANSLATED_FIELD2);

		final Configuration configuration = mock(HybrisConfiguration.class);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_METHOD,
				FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_METHOD_UNIFIED)).thenReturn(HIGHLIGHTING_METHOD_DEFAULT);
		when(configuration.getString(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_PRE,
				FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_PRE_EM))
						.thenReturn(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_PRE_EM);
		when(configuration.getString(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_POST,
				FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_POST_EM))
						.thenReturn(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_POST_EM);
		when(configuration.getBoolean(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_REQUIRE_FIELD_MATCH, true))
				.thenReturn(false);
		when(configuration.getInt(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_SNIPPETS,
				FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_SNIPPETS_DEFAULT)).thenReturn(1);

	}

	@Test
	public void populateWithEmptyHighlights()
	{
		// given
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryHighlightingFieldsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertNull(solrQuery.getHighlightFields());
	}

	@Test
	public void populateWithSingleHighlightField()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addHighlightingField(FIELD1);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryHighlightingFieldsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertEquals(solrQuery.getHighlight(), true);
		assertEquals(solrQuery.getHighlightFields().length, 1);
		assertEquals(solrQuery.getHighlightFields()[0], TRANSLATED_FIELD1);
		assertEquals(solrQuery.getHighlightSnippets(), 1);
		assertEquals(solrQuery.getHighlightRequireFieldMatch(), false);
		assertTrue(solrQuery.get(FacetSearchQueryHighlightingFieldsPopulator.SOLR_HIGHLIGHTING_METHOD_PARAM)
				.equals(HIGHLIGHTING_METHOD_DEFAULT));
		assertTrue(solrQuery.get(FacetSearchQueryHighlightingFieldsPopulator.SOLR_HIGHLIGHTING_TAG_PRE)
				.equals(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_PRE_EM));
		assertTrue(solrQuery.get(FacetSearchQueryHighlightingFieldsPopulator.SOLR_HIGHLIGHTING_TAG_POST)
				.equals(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_POST_EM));
	}

	@Test
	public void populateWithMultipleHighlightFields()
	{
		// given
		final SearchQuery searchQuery = searchQueryConverterData.getSearchQuery();
		searchQuery.addHighlightingField(FIELD1);
		searchQuery.addHighlightingField(FIELD2);
		final SolrQuery solrQuery = new SolrQuery();

		// when
		facetSearchQueryHighlightingFieldsPopulator.populate(searchQueryConverterData, solrQuery);

		// then
		assertEquals(solrQuery.getHighlight(), true);
		assertEquals(solrQuery.getHighlightFields().length, 2);
		assertEquals(solrQuery.getHighlightFields()[0], TRANSLATED_FIELD1);
		assertEquals(solrQuery.getHighlightFields()[1], TRANSLATED_FIELD2);
		assertEquals(solrQuery.getHighlightSnippets(), 1);
		assertEquals(solrQuery.getHighlightRequireFieldMatch(), false);
		assertTrue(solrQuery.get(FacetSearchQueryHighlightingFieldsPopulator.SOLR_HIGHLIGHTING_METHOD_PARAM)
				.equals(HIGHLIGHTING_METHOD_DEFAULT));
		assertTrue(solrQuery.get(FacetSearchQueryHighlightingFieldsPopulator.SOLR_HIGHLIGHTING_TAG_PRE)
				.equals(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_PRE_EM));
		assertTrue(solrQuery.get(FacetSearchQueryHighlightingFieldsPopulator.SOLR_HIGHLIGHTING_TAG_POST)
				.equals(FacetSearchQueryHighlightingFieldsPopulator.HIGHLIGHTING_TAG_POST_EM));
	}
}
