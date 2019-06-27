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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.WildcardType;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider.FieldType;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.Keyword;
import de.hybris.platform.solrfacetsearch.search.KeywordModifier;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MultiFieldFreeTextQueryBuilderTest
{
	public static final String EAN_FIELD = "ean";
	public static final String EAN_TRANSLATED_FIELD = "ean_string";

	public static final String EAN1_FIELD = "ean1";
	public static final String EAN1_TRANSLATED_FIELD = "ean1_string";

	public static final String EAN2_FIELD = "ean2";
	public static final String EAN2_TRANSLATED_FIELD = "ean2_string";

	public static final String EAN3_FIELD = "ean3";
	public static final String EAN3_TRANSLATED_FIELD = "ean3_string";

	public static final String EAN4_FIELD = "ean4";
	public static final String EAN4_TRANSLATED_FIELD = "ean4_string";

	@Mock
	private FieldNameTranslator fieldNameTranslator;

	private MultiFieldFreeTextQueryBuilder multiFieldFreeTextQueryBuilder;
	private SearchQuery searchQuery;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		multiFieldFreeTextQueryBuilder = new MultiFieldFreeTextQueryBuilder();
		multiFieldFreeTextQueryBuilder.setFieldNameTranslator(fieldNameTranslator);

		final FacetSearchConfig facetSearchConfig = new FacetSearchConfig();
		final IndexedType indexedType = new IndexedType();
		searchQuery = new SearchQuery(facetSearchConfig, indexedType);

		given(fieldNameTranslator.translate(searchQuery, EAN_FIELD, FieldType.INDEX)).willReturn(EAN_TRANSLATED_FIELD);
		given(fieldNameTranslator.translate(searchQuery, EAN1_FIELD, FieldType.INDEX)).willReturn(EAN1_TRANSLATED_FIELD);
		given(fieldNameTranslator.translate(searchQuery, EAN2_FIELD, FieldType.INDEX)).willReturn(EAN2_TRANSLATED_FIELD);
		given(fieldNameTranslator.translate(searchQuery, EAN3_FIELD, FieldType.INDEX)).willReturn(EAN3_TRANSLATED_FIELD);
		given(fieldNameTranslator.translate(searchQuery, EAN4_FIELD, FieldType.INDEX)).willReturn(EAN4_TRANSLATED_FIELD);
	}

	@Test
	public void testMultiFreeTextBuilder() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextQuery(EAN_FIELD, 0, Float.valueOf(100));
		searchQuery.addFreeTextFuzzyQuery(EAN_FIELD, 0, Integer.valueOf(10), Float.valueOf(50));
		searchQuery.addFreeTextWildcardQuery(EAN_FIELD, 0, WildcardType.POSTFIX, Float.valueOf(25));
		searchQuery.addFreeTextPhraseQuery(EAN_FIELD, Float.valueOf(10), Float.valueOf(200));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(test^100.0 OR test1^100.0 OR test2^100.0 OR "
				+ "test~10^50.0 OR test1~10^50.0 OR test2~10^50.0 OR test*^25.0 OR test1*^25.0 OR test2*^25.0 OR "
				+ "\"test test1 test2\"~10.0^200.0))";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testMultiFieldsFreeTextBuilder() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextQuery(EAN2_FIELD, 0, Float.valueOf(100));
		searchQuery.addFreeTextFuzzyQuery(EAN3_FIELD, 0, Integer.valueOf(10), Float.valueOf(50));
		searchQuery.addFreeTextWildcardQuery(EAN4_FIELD, 0, WildcardType.POSTFIX, Float.valueOf(25));
		searchQuery.addFreeTextPhraseQuery(EAN1_FIELD, Float.valueOf(10), Float.valueOf(200));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean2_string:(test^100.0 OR test1^100.0 OR "
				+ "test2^100.0)) OR (ean3_string:(test~10^50.0 OR test1~10^50.0 OR test2~10^50.0)) OR (ean4_string:(test*^25"
				+ ".0 OR test1*^25.0 OR test2*^25.0)) OR (ean1_string:\"test test1 test2\"~10.0^200.0)";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTextQueryWithQuotes() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextQuery(EAN_FIELD, 0, Float.valueOf(100));

		searchQuery.setUserQuery("\"test test1\" test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test test1", KeywordModifier.EXACT_MATCH), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(\"test test1\"^100.0 OR test2^100.0))";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTextQueryWithQuotesAndBackslashes() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextQuery(EAN_FIELD, 0, Float.valueOf(100));

		searchQuery.setUserQuery("\"test\\ test\\ test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test\\ test1\\", KeywordModifier.EXACT_MATCH), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(\"test\\\\ test1\\\\\"^100.0 OR test2^100.0))";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTextQuery() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextQuery(EAN_FIELD, 0, Float.valueOf(100));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(test^100.0 OR test1^100.0 OR test2^100.0))";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTexFuzzyQuery() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextFuzzyQuery(EAN_FIELD, 0, Integer.valueOf(10), Float.valueOf(50));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(test~10^50.0 OR test1~10^50.0 OR test2~10^50.0))";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTextWildcardQuery() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextWildcardQuery(EAN_FIELD, 0, WildcardType.POSTFIX, Float.valueOf(25));
		searchQuery.addFreeTextWildcardQuery(EAN1_FIELD, 0, WildcardType.PREFIX, Float.valueOf(26));
		searchQuery.addFreeTextWildcardQuery(EAN2_FIELD, 0, WildcardType.PREFIX_AND_POSTFIX, Float.valueOf(27));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(test*^25.0 OR test1*^25.0 OR test2*^25.0)) OR "
				+ "(ean1_string:(*test^26.0 OR *test1^26.0 OR *test2^26.0)) OR "
				+ "(ean2_string:(*test*^27.0 OR *test1*^27.0 OR *test2*^27.0))";

		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTextPhraseQuery() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextPhraseQuery(EAN_FIELD, Float.valueOf(10), Float.valueOf(200));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:\"test test1 test2\"~10.0^200.0)";
		Assert.assertEquals(expectedQuery, query);
	}

	@Test
	public void testFreeTextMinTermLength() throws FacetSearchException
	{
		// given
		searchQuery.addFreeTextWildcardQuery(EAN_FIELD, 5, WildcardType.POSTFIX, Float.valueOf(25));

		searchQuery.setUserQuery("test test1 test2");
		searchQuery.setKeywords(Arrays.asList(new Keyword("test"), new Keyword("test1"), new Keyword("test2")));

		// when
		final String query = multiFieldFreeTextQueryBuilder.buildQuery(searchQuery);

		// then
		final String expectedQuery = "(ean_string:(test1*^25.0 OR test2*^25.0))";
		Assert.assertEquals(expectedQuery, query);
	}
}
