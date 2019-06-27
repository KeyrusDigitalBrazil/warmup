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
package de.hybris.platform.commercefacades.search.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchQueryDecoderPopulator;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.FilterQueryOperator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for SolrSearchQueryDecoderPopulator
 */
@UnitTest
public class SolrSearchQueryDecoderPopulatorTest
{
	protected static final String FREE_TEXT_SEARCH = "free text search";
	protected static final String SORT = "sort";
	protected static final String KEY1 = "key1";
	protected static final String KEY2 = "key2";
	protected static final String VALUE1 = "value1";
	protected static final String VALUE2 = "value2";

	private final AbstractPopulatingConverter<SearchQueryData, SolrSearchQueryData> solrSearchQueryDecoder = new ConverterFactory<SearchQueryData, SolrSearchQueryData, SolrSearchQueryDecoderPopulator>()
			.create(SolrSearchQueryData.class, new SolrSearchQueryDecoderPopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvertNull()
	{
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(null);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertNull(result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertNull(result.getFilterTerms());
	}

	@Test
	public void testConvertEmpty()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertNull(result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertNull(result.getFilterTerms());
	}

	@Test
	public void testConvertWord()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH);
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertWord2()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertNull(result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertSort()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(":" + SORT);
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertWordSort()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":" + SORT);
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertTrue(result.getFilterTerms().isEmpty());
	}

	@Test
	public void testConvertFilterTerm()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("::key1:value1");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals("", result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(1, result.getFilterTerms().size());
		Assert.assertEquals(KEY1, result.getFilterTerms().get(0).getKey());
		Assert.assertEquals(VALUE1, result.getFilterTerms().get(0).getValue());
	}

	@Test
	public void testConvertFilterTerm2()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("::key1:value1:key2:value2");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals("", result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(2, result.getFilterTerms().size());
		Assert.assertEquals(KEY1, result.getFilterTerms().get(0).getKey());
		Assert.assertEquals(VALUE1, result.getFilterTerms().get(0).getValue());
		Assert.assertEquals(KEY2, result.getFilterTerms().get(1).getKey());
		Assert.assertEquals(VALUE2, result.getFilterTerms().get(1).getValue());
	}

	@Test
	public void testConvertFilterTerm3()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("::key1:5%3A%27text%27");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals("", result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(1, result.getFilterTerms().size());
		// Verifying special characters decoding for : '
		Assert.assertEquals(KEY1, result.getFilterTerms().get(0).getKey());
		Assert.assertEquals("5:'text'", result.getFilterTerms().get(0).getValue());
	}

	@Test
	public void testConvertFilterTerm4()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue("::key1:7%2B-%26%26%7C%7C%21%28%29%7B%7Dtext:key2:8%5B%5D%5E%22%7E*%3F%3A%5C%2Ftext");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals("", result.getFreeTextSearch());
		Assert.assertEquals("", result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(2, result.getFilterTerms().size());
		Assert.assertEquals(KEY1, result.getFilterTerms().get(0).getKey());
		// Verifying special characters decoding for + - && || ! ( ) { }
		Assert.assertEquals("7+-&&||!(){}text", result.getFilterTerms().get(0).getValue());
		Assert.assertEquals(KEY2, result.getFilterTerms().get(1).getKey());
		// Verifying special characters decoding for [ ] ^ " ~ * ? : \
		Assert.assertEquals("8[]^\"~*?:\\/text", result.getFilterTerms().get(1).getValue());
	}

	@Test
	public void testConvertAll()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(2, result.getFilterTerms().size());
		Assert.assertEquals(KEY1, result.getFilterTerms().get(0).getKey());
		Assert.assertEquals(VALUE1, result.getFilterTerms().get(0).getValue());
		Assert.assertEquals(KEY2, result.getFilterTerms().get(1).getKey());
		Assert.assertEquals(VALUE2, result.getFilterTerms().get(1).getValue());
	}

	@Test
	public void testConvertAllWithSpecialCharacters()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":" + SORT
				+ ":key1:7%2B-%26%26%7C%7C%21%28%29%7B%7Dtext:key2:8%5B%5D%5E%22%7E*%3F%3A%5C%2Ftext");
		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull(result.getCategoryCode());
		Assert.assertEquals(FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertEquals(SORT, result.getSort());
		Assert.assertNotNull(result.getFilterTerms());
		Assert.assertEquals(2, result.getFilterTerms().size());
		Assert.assertEquals(KEY1, result.getFilterTerms().get(0).getKey());
		// Verifying special characters decoding for + - && || ! ( ) { } with free text search and sort
		Assert.assertEquals("7+-&&||!(){}text", result.getFilterTerms().get(0).getValue());
		Assert.assertEquals(KEY2, result.getFilterTerms().get(1).getKey());
		// Verifying special characters decoding for [ ] ^ " ~ * ? : \ with free text search and sort
		Assert.assertEquals("8[]^\"~*?:\\/text", result.getFilterTerms().get(1).getValue());
	}

	@Test
	public void testConvertAllWithFilterQueries()
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2");

		final SearchFilterQueryData searchFilterQueryData1 = new SearchFilterQueryData();
		searchFilterQueryData1.setKey("filterQuery1");
		searchFilterQueryData1.setValues(new HashSet<String>(Arrays.asList("filterQuery1Value")));

		final SearchFilterQueryData searchFilterQueryData2 = new SearchFilterQueryData();
		searchFilterQueryData2.setKey("filterQuery2");
		searchFilterQueryData2.setOperator(FilterQueryOperator.AND);
		searchFilterQueryData2.setValues(new HashSet<String>(Arrays.asList("filterQuery2Value1", "filterQuery2Value2")));

		searchQueryData.setFilterQueries(Arrays.asList(searchFilterQueryData1, searchFilterQueryData2));

		searchQueryData.setSearchQueryContext(SearchQueryContext.DEFAULT);

		final SolrSearchQueryData result = solrSearchQueryDecoder.convert(searchQueryData);
		Assert.assertNull("Category code in result should be null", result.getCategoryCode());
		Assert.assertEquals("Search type of the result should be free text search", FREE_TEXT_SEARCH, result.getFreeTextSearch());
		Assert.assertEquals("Result.sort should be '" + SORT + "'", SORT, result.getSort());
		Assert.assertNotNull("Filter terms in the result should not be null", result.getFilterTerms());
		Assert.assertEquals("Result should have 2 filter terms", 2, result.getFilterTerms().size());
		Assert.assertEquals("First filter term should have key='" + KEY1 + "'", KEY1, result.getFilterTerms().get(0).getKey());
		Assert.assertEquals("First filter term should have value='" + VALUE1 + "'", VALUE1, result.getFilterTerms().get(0)
				.getValue());
		Assert.assertEquals("Second filter term should have key='" + KEY2 + "'", KEY2, result.getFilterTerms().get(1).getKey());
		Assert.assertEquals("Second filter term should have value='" + VALUE2 + "'", VALUE2, result.getFilterTerms().get(1)
				.getValue());

		Assert.assertEquals("Result should have 2 filter queries", 2, result.getFilterQueries().size());
		Assert.assertEquals("First filter query should have key='filterQuery1'", "filterQuery1", result.getFilterQueries().get(0)
				.getKey());
		Assert.assertNull("Operator for first filter query should be null", result.getFilterQueries().get(0).getOperator());
		Assert.assertEquals("First filter query shuld have 1 value", 1, result.getFilterQueries().get(0).getValues().size());
		Assert.assertTrue("First filter query shuld value='filterQuery1Value'", result.getFilterQueries().get(0).getValues()
				.contains("filterQuery1Value"));

		Assert.assertEquals("Second filter query should have key='filterQuery2'", "filterQuery2", result.getFilterQueries().get(1)
				.getKey());
		Assert.assertEquals("Operator for second filter query should be AND", FilterQueryOperator.AND, result.getFilterQueries()
				.get(1).getOperator());
		Assert.assertEquals("Second filter query shuld have 2 values", 2, result.getFilterQueries().get(1).getValues().size());
		Assert.assertTrue("First value for second filter query should be filterQuery2Value1", result.getFilterQueries().get(1)
				.getValues().contains("filterQuery2Value1"));
		Assert.assertTrue("Second value for second filter query should be filterQuery2Value2", result.getFilterQueries().get(1)
				.getValues().contains("filterQuery2Value2"));
		Assert.assertEquals("SearchQueryContext is Default", SearchQueryContext.DEFAULT, result.getSearchQueryContext());
	}
}
