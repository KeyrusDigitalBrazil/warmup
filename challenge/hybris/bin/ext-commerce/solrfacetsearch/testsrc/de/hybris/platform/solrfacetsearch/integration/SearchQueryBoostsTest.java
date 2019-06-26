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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerService;
import de.hybris.platform.solrfacetsearch.search.BoostField.BoostType;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQuery.QueryOperator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;


@IntegrationTest
public class SearchQueryBoostsTest extends AbstractIntegrationTest
{
	@Resource
	private DefaultIndexerService indexerService;

	@Resource
	private FacetSearchService facetSearchService;

	private static final String PRODUCT_CODE_FIELD = "code";
	private static final String SCORE_FIELD = "score";

	private static final Float DEFAULT_SCORE = Float.valueOf(1);

	private static final String PRODUCT1_CODE = "product1";
	private static final String PRODUCT2_CODE = "product2";

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SearchQueryBoostsTest.csv");
	}

	@Test
	public void defaultScore() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			// NOOP
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertScore(DEFAULT_SCORE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void singleMultiplicativeBoost() throws Exception
	{
		// given
		final Float boost = 2f;
		final Float expectedScore = DEFAULT_SCORE * boost;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost, BoostType.MULTIPLICATIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
		assertScore(expectedScore, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void multipleMultiplicativeBoosts() throws Exception
	{
		// given
		final Float boost1 = 2f;
		final Float boost2 = 3f;
		final Float expectedScore = DEFAULT_SCORE * boost1 * boost2;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.MULTIPLICATIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost2, BoostType.MULTIPLICATIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
		assertScore(expectedScore, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void singleAdditiveBoost() throws Exception
	{
		// given
		final Float boost = 2f;
		final Float expectedScore = DEFAULT_SCORE + boost;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost, BoostType.ADDITIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
		assertScore(expectedScore, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void multipleAdditiveBoosts() throws Exception
	{
		// given
		final Float boost1 = 2f;
		final Float boost2 = 3f;
		final Float expectedScore = DEFAULT_SCORE + boost1 + boost2;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.ADDITIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost2, BoostType.ADDITIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
		assertScore(expectedScore, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void multipleBoostsSameDocument1() throws Exception
	{
		// given
		final Float boost1 = 2f;
		final Float boost2 = 5f;
		final Float expectedScore = (DEFAULT_SCORE + boost1) * boost2;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.ADDITIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost2, BoostType.MULTIPLICATIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
		assertScore(expectedScore, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void multipleBoostsSameDocument2() throws Exception
	{
		// given
		final Float boost1 = 2f;
		final Float boost2 = 5f;
		final Float expectedScore = (DEFAULT_SCORE + boost1) * boost2;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost2, BoostType.MULTIPLICATIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.ADDITIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
		assertScore(expectedScore, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
		assertScore(DEFAULT_SCORE, document2);
	}

	@Test
	public void multipleBoostsDifferentDocuments() throws Exception
	{
		// given
		final Float boost1 = 3f;
		final Float expectedScore1 = (DEFAULT_SCORE + boost1) * boost1;

		final Float boost2 = 4f;
		final Float expectedScore2 = (DEFAULT_SCORE + boost2) * boost2;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.MULTIPLICATIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.ADDITIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT2_CODE, boost2, BoostType.MULTIPLICATIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT2_CODE, boost2, BoostType.ADDITIVE);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document1);
		assertScore(expectedScore2, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT1_CODE, document2);
		assertScore(expectedScore1, document2);
	}

	@Test
	public void multipleBoostsDifferentDocumentsWithFilterQuery() throws Exception
	{
		// given
		final Float boost1 = 3f;

		final Float boost2 = 4f;
		final Float expectedScore2 = (DEFAULT_SCORE + boost2) * boost2;

		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.MULTIPLICATIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT1_CODE, boost1, BoostType.ADDITIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT2_CODE, boost2, BoostType.MULTIPLICATIVE);
			searchQuery.addBoost(PRODUCT_CODE_FIELD, QueryOperator.EQUAL_TO, PRODUCT2_CODE, boost2, BoostType.ADDITIVE);
			searchQuery.addFilterQuery(PRODUCT_CODE_FIELD, PRODUCT2_CODE);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document1);
		assertScore(expectedScore2, document1);
	}

	protected SearchResult executeSearchQuery(final Consumer<SearchQuery> action) throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		indexerService.performFullIndex(facetSearchConfig);

		final SearchQuery searchQuery = facetSearchService.createPopulatedSearchQuery(facetSearchConfig, indexedType);
		searchQuery.getFields().addAll(Arrays.asList(SCORE_FIELD, PRODUCT_CODE_FIELD));

		action.accept(searchQuery);

		return facetSearchService.search(searchQuery);
	}

	protected void assertProductCode(final String expectedProductCode, final Document document)
	{
		assertEquals(expectedProductCode, document.getFields().get(PRODUCT_CODE_FIELD));
	}

	protected void assertScore(final float expectedScore, final Document document)
	{
		assertEquals(Float.valueOf(expectedScore), document.getFields().get(SCORE_FIELD));
	}
}
