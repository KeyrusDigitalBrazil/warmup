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
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator.SolrSearchQueryEncoderPopulator;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.FilterQueryOperator;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for SolrSearchQueryEncoderPopulator
 */
@UnitTest
public class SolrSearchQueryEncoderPopulatorTest
{
	protected static final String FREE_TEXT_SEARCH = "free text search";
	protected static final String SORT = "sort";
	protected static final String KEY1 = "key1";
	protected static final String KEY2 = "key2";
	protected static final String VALUE1 = "value1";
	protected static final String VALUE2 = "value2";

	private final AbstractPopulatingConverter<SolrSearchQueryData, SearchQueryData> solrSearchQueryEncoder = new ConverterFactory<SolrSearchQueryData, SearchQueryData, SolrSearchQueryEncoderPopulator>()
			.create(SearchQueryData.class, new SolrSearchQueryEncoderPopulator());

	@Before
	public void setUp()
	{
		//Do Nothing
	}

	@Test
	public void testConvertNull()
	{
		final SearchQueryData result = solrSearchQueryEncoder.convert(null);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertEmpty()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertEmptyTerms()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFilterTerms(Collections.<SolrSearchQueryTermData> emptyList());
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertEmptyTerms2()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
		searchQueryData.setFilterTerms(Collections.singletonList(searchQueryTermData));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("", result.getValue());
	}

	@Test
	public void testConvertFreeText()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFreeTextSearch(FREE_TEXT_SEARCH);
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals(FREE_TEXT_SEARCH + ":", result.getValue());
	}

	@Test
	public void testConvertSort()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setSort(SORT);
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals(":" + SORT, result.getValue());
	}

	@Test
	public void testConvertTerms1()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
		searchQueryTermData.setKey(KEY1);
		searchQueryTermData.setValue(VALUE1);
		searchQueryData.setFilterTerms(Collections.singletonList(searchQueryTermData));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("::key1:value1", result.getValue());
	}

	@Test
	public void testConvertTerms2()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey(KEY1);
		searchQueryTermData1.setValue(VALUE1);
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey(KEY2);
		searchQueryTermData2.setValue(VALUE2);

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("::key1:value1:key2:value2", result.getValue());
	}

	@Test
	public void testConvertTerms3()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData = new SolrSearchQueryTermData();
		searchQueryTermData.setKey(KEY1);
		searchQueryTermData.setValue("5:'text'");
		searchQueryData.setFilterTerms(Collections.singletonList(searchQueryTermData));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		// Verifying special characters encoding for : '
		Assert.assertEquals("::key1:5%3A%27text%27", result.getValue());
	}

	@Test
	public void testConvertTerms4()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey(KEY1);
		searchQueryTermData1.setValue("7+-&&||!(){}text");
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey(KEY2);
		searchQueryTermData2.setValue("8[]^\"~*?:\\/text");

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		// Verifying special characters encoding for + - && || ! ( ) { } [ ] ^ " ~ * ? : \
		Assert.assertEquals("::key1:7%2B-%26%26%7C%7C%21%28%29%7B%7Dtext:key2:8%5B%5D%5E%22%7E*%3F%3A%5C%2Ftext", result.getValue());
	}

	@Test
	public void testConvertAll()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFreeTextSearch(FREE_TEXT_SEARCH);
		searchQueryData.setSort(SORT);
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey(KEY1);
		searchQueryTermData1.setValue(VALUE1);
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey(KEY2);
		searchQueryTermData2.setValue(VALUE2);

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals(FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2", result.getValue());
	}

	@Test
	public void testConvertAllWithSpecialCharacters()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFreeTextSearch(FREE_TEXT_SEARCH);
		searchQueryData.setSort(SORT);
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey(KEY1);
		searchQueryTermData1.setValue("7+-&&||!(){}text");
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey(KEY2);
		searchQueryTermData2.setValue("8[]^\"~*?:\\/text");

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));
		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		// Verifying special characters encoding for + - && || ! ( ) { } [ ] ^ " ~ * ? : \ with free text search and sort
		Assert.assertEquals(FREE_TEXT_SEARCH + ":" + SORT
				+ ":key1:7%2B-%26%26%7C%7C%21%28%29%7B%7Dtext:key2:8%5B%5D%5E%22%7E*%3F%3A%5C%2Ftext", result.getValue());
	}

	@Test
	public void testConvertAllWithFilterQueries()
	{
		final SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
		searchQueryData.setFreeTextSearch(FREE_TEXT_SEARCH);
		searchQueryData.setSort(SORT);
		final SolrSearchQueryTermData searchQueryTermData1 = new SolrSearchQueryTermData();
		searchQueryTermData1.setKey(KEY1);
		searchQueryTermData1.setValue(VALUE1);
		final SolrSearchQueryTermData searchQueryTermData2 = new SolrSearchQueryTermData();
		searchQueryTermData2.setKey(KEY2);
		searchQueryTermData2.setValue(VALUE2);

		searchQueryData.setFilterTerms(Arrays.asList(searchQueryTermData1, searchQueryTermData2));

		final SolrSearchFilterQueryData solrSearchFilterQueryData1 = new SolrSearchFilterQueryData();
		solrSearchFilterQueryData1.setKey("filterQuery1");
		solrSearchFilterQueryData1.setValues(new HashSet<String>(Arrays.asList("filterQuery1Value")));

		final SolrSearchFilterQueryData solrSearchFilterQueryData2 = new SolrSearchFilterQueryData();
		solrSearchFilterQueryData2.setKey("filterQuery2");
		solrSearchFilterQueryData2.setOperator(FilterQueryOperator.AND);
		solrSearchFilterQueryData2.setValues(new HashSet<String>(Arrays.asList("filterQuery2Value1", "filterQuery2Value2")));

		searchQueryData.setFilterQueries(Arrays.asList(solrSearchFilterQueryData1, solrSearchFilterQueryData2));

		searchQueryData.setSearchQueryContext(SearchQueryContext.DEFAULT);

		final SearchQueryData result = solrSearchQueryEncoder.convert(searchQueryData);
		Assert.assertEquals("Result value should be " + FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2",
				FREE_TEXT_SEARCH + ":" + SORT + ":key1:value1:key2:value2", result.getValue());

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
