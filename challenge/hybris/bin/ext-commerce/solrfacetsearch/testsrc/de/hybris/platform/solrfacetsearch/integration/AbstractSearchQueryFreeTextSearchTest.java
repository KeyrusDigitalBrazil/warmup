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

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.solrfacetsearch.config.WildcardType;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.FreeTextFuzzyQueryField;
import de.hybris.platform.solrfacetsearch.search.OrderField.SortOrder;
import de.hybris.platform.solrfacetsearch.search.SearchResult;

import org.junit.Test;


@ManualTest
public abstract class AbstractSearchQueryFreeTextSearchTest extends AbstractSearchQueryTest
{
	protected abstract String getFreeTextQueryBuilder();

	@Override
	protected void loadData() throws Exception
	{
		importConfig("/test/integration/SearchQueryFreeTextSearchTest.csv");
	}

	@Test
	public void addFreeTextQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("prod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_NAME_FIELD, null, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextQueryWithExactMatch1() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"prod 2\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document);
	}

	@Test
	public void addFreeTextQueryWithExactMatch2() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("aaaa \"prod 2\" aaaa");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document);
	}

	@Test
	public void addFreeTextQueryWithFieldEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("prod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, null, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextQueryWithQueryEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("manufacturer1,'\"name");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_MANUFACTURER_NAME_FIELD, null, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document);
	}

	@Test
	public void addFreeTextQueryWithNoResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pprod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextFuzzyQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pprod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextFuzzyQuery(new FreeTextFuzzyQueryField(PRODUCT_NAME_FIELD));
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextFuzzyQueryWithExactMatch() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"prod\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextFuzzyQuery(new FreeTextFuzzyQueryField(PRODUCT_NAME_FIELD));
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextFuzzyQueryWithFieldEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pprod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextFuzzyQuery(new FreeTextFuzzyQueryField(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD));
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextFuzzyQueryWithQueryEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("maufacturer2,'\"name");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextFuzzyQuery(new FreeTextFuzzyQueryField(PRODUCT_MANUFACTURER_NAME_FIELD));
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextFuzzyQueryWithNoResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pppprod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextFuzzyQuery(new FreeTextFuzzyQueryField(PRODUCT_NAME_FIELD));
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextWildcardPrefixQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("rod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.PREFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPrefixQueryWithExactMatch() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"prod\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.PREFIX, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextWildcardPrefixWithFieldEscapingQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("rod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, null, WildcardType.PREFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPrefixWithQueryEscapingQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("nufacturer1,'\"name");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_MANUFACTURER_NAME_FIELD, null, WildcardType.PREFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
	}

	@Test
	public void addFreeTextWildcardPrefixQueryWithNoResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pro");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.PREFIX, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextWildcardPostfixQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("prod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.POSTFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPostfixQueryWithExactMatch() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"prod\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.POSTFIX, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextWildcardPostfixWithFieldEscapingQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pro");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, null, WildcardType.POSTFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPostfixWithQueryEscapingQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("manufacturer1,'\"na");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_MANUFACTURER_NAME_FIELD, null, WildcardType.POSTFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);
	}

	@Test
	public void addFreeTextWildcardPostfixQueryWithNoResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("rod");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.POSTFIX, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextWildcardPrefixAndPostfixQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("ro");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.PREFIX_AND_POSTFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPrefixAndPostfixQueryWithExactMatch() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"prod\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.PREFIX_AND_POSTFIX, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextWildcardPrefixAndPostfixQueryWithFieldEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("ro");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, null, WildcardType.PREFIX_AND_POSTFIX,
					null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(2, searchResult.getNumberOfResults());

		final Document document1 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document1);

		final Document document2 = searchResult.getDocuments().get(1);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPrefixAndPostfixQueryWithQueryEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("nufacturer2,'\"na");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_MANUFACTURER_NAME_FIELD, null, WildcardType.PREFIX_AND_POSTFIX, null);
			searchQuery.addSort(PRODUCT_CODE_FIELD, SortOrder.ASCENDING);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document2 = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document2);
	}

	@Test
	public void addFreeTextWildcardPrefixAndPostfixQueryWithNoResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pprodd");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextWildcardQuery(PRODUCT_NAME_FIELD, null, WildcardType.PREFIX_AND_POSTFIX, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextPhraseQuery() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("prod 1");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextPhraseQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document);
	}

	@Test
	public void addFreeTextPhraseQueryWithExactMatch() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"prod\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextPhraseQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextPhraseQueryWithFieldEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("prod 1");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextPhraseQuery(PRODUCT_NAME_WITH_RESERVED_CHARS_FIELD, null, null);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT1_CODE, document);
	}

	@Test
	public void addFreeTextPhraseQueryWithQueryEscaping() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("manufacturer2,'\"name");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextPhraseQuery(PRODUCT_MANUFACTURER_NAME_FIELD, null, null);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());

		final Document document = searchResult.getDocuments().get(0);
		assertProductCode(PRODUCT2_CODE, document);
	}

	@Test
	public void addFreeTextPhraseQueryWithNoResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("pprod 1");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextPhraseQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextQueryWithReservedWords() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("AND OR NOT");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_CODE_FIELD, null, null);
			searchQuery.addFreeTextFuzzyQuery(PRODUCT_CODE_FIELD, null, Integer.valueOf(0), null);
			searchQuery.addFreeTextWildcardQuery(PRODUCT_CODE_FIELD, null, null, null);
			searchQuery.addFreeTextPhraseQuery(PRODUCT_CODE_FIELD, null, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextQueryWithReservedWordsAndExactMatch() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("\"AND\" \"OR\" \"NOT\"");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_CODE_FIELD, null, null);
			searchQuery.addFreeTextFuzzyQuery(PRODUCT_CODE_FIELD, null, Integer.valueOf(0), null);
			searchQuery.addFreeTextWildcardQuery(PRODUCT_CODE_FIELD, null, null, null);
			searchQuery.addFreeTextPhraseQuery(PRODUCT_CODE_FIELD, null, null);
		});

		// then
		assertEquals(0, searchResult.getNumberOfResults());
	}

	@Test
	public void addFreeTextQueryWithReservedWordsWithResults() throws Exception
	{
		// when
		final SearchResult searchResult = executeSearchQuery(searchQuery -> {
			searchQuery.setUserQuery("Reserved word OR");
			searchQuery.setFreeTextQueryBuilder(getFreeTextQueryBuilder());
			searchQuery.addFreeTextQuery(PRODUCT_NAME_FIELD, null, null);
		});

		// then
		assertEquals(1, searchResult.getNumberOfResults());
	}

	protected void assertProductCode(final String expectedProductCode, final Document document)
	{
		assertEquals(expectedProductCode, document.getFields().get(PRODUCT_CODE_FIELD));
	}
}
