/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cms2.namedquery.service.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.NamedQueryConversionDto;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FlexibleSearchNamedQueryConverterTest
{

	private static final String QUERY = "QUERY";
	private static final String QUERY_NAME = "QUERY_NAME";
	private static final Integer DEFAULT_PAGE_SIZE = 100;


	FlexibleSearchNamedQueryConverter flexibleSearchNamedQueryConverter = new FlexibleSearchNamedQueryConverter();

	@Before
	public void setup()
	{
		flexibleSearchNamedQueryConverter.setDefaultPageSize(DEFAULT_PAGE_SIZE);
	}

	@Test
	public void testWithoutSortingAndWithoutParamsAndWithDefaultPagingOptions()
	{
		final NamedQueryConversionDto namedQuery = new NamedQueryConversionDto().withQuery(QUERY)
				.withNamedQuery(new NamedQuery().withQueryName(QUERY_NAME));
		final FlexibleSearchQuery flexibleSearchQuery = flexibleSearchNamedQueryConverter.convert(namedQuery);
		Assert.assertEquals(QUERY, flexibleSearchQuery.getQuery());
		Assert.assertEquals(DEFAULT_PAGE_SIZE.intValue(), flexibleSearchQuery.getCount());
		Assert.assertEquals(0, flexibleSearchQuery.getStart());
	}

	@Test
	public void testWithoutSortingAndWithoutParamsRequestingPagingOptions()
	{
		final NamedQueryConversionDto namedQueryConversion = new NamedQueryConversionDto().withQuery(QUERY).withNamedQuery( //
				new NamedQuery().withQueryName(QUERY_NAME) //
						.withPageSize(10) //
						.withCurrentPage(2));
		final FlexibleSearchQuery flexibleSearchQuery = flexibleSearchNamedQueryConverter.convert(namedQueryConversion);
		Assert.assertEquals(QUERY, flexibleSearchQuery.getQuery());
		Assert.assertEquals(namedQueryConversion.getNamedQuery().getPageSize(), (Integer) flexibleSearchQuery.getCount());
		Assert.assertEquals(
				(namedQueryConversion.getNamedQuery().getCurrentPage()) * namedQueryConversion.getNamedQuery().getPageSize(),
				flexibleSearchQuery.getStart());
	}

	@Test
	public void testWithoutSortingAndWithoutParamsRequestingCurrentPageOnly()
	{

		final NamedQueryConversionDto namedQueryConversion = new NamedQueryConversionDto().withQuery(QUERY).withNamedQuery( //
				new NamedQuery().withQueryName(QUERY_NAME) //
						.withCurrentPage(2));

		final FlexibleSearchQuery flexibleSearchQuery = flexibleSearchNamedQueryConverter.convert(namedQueryConversion);
		Assert.assertEquals(QUERY, flexibleSearchQuery.getQuery());
		Assert.assertEquals(DEFAULT_PAGE_SIZE, (Integer) flexibleSearchQuery.getCount());
		Assert.assertEquals((namedQueryConversion.getNamedQuery().getCurrentPage()) * DEFAULT_PAGE_SIZE,
				flexibleSearchQuery.getStart());
	}

	@Test
	public void testWithSortingAndWithoutParamsWithDefaultPagingOptions()
	{
		final NamedQueryConversionDto namedQueryConversion = new NamedQueryConversionDto().withQuery(QUERY).withNamedQuery( //
				new NamedQuery().withQueryName(QUERY_NAME));

		final Sort sort1 = new Sort().withParameter("param1").withDirection(SortDirection.ASC);
		final Sort sort2 = new Sort().withParameter("param2").withDirection(SortDirection.DESC);
		namedQueryConversion //
				.getNamedQuery()//
				.withQueryName(QUERY_NAME).withSort(Arrays.asList(sort1, sort2));
		final FlexibleSearchQuery flexibleSearchQuery = flexibleSearchNamedQueryConverter.convert(namedQueryConversion);

		final String expectedQuery = QUERY + FlexibleSearchNamedQueryConverter.ORDER_BY
				+ FlexibleSearchNamedQueryConverter.OPEN_BRACKET + sort1.getParameter()
				+ FlexibleSearchNamedQueryConverter.CLOSE_BRACKET + FlexibleSearchNamedQueryConverter.SPACE
				+ sort1.getDirection().name() + FlexibleSearchNamedQueryConverter.COMMA
				+ FlexibleSearchNamedQueryConverter.OPEN_BRACKET + sort2.getParameter()
				+ FlexibleSearchNamedQueryConverter.CLOSE_BRACKET + FlexibleSearchNamedQueryConverter.SPACE
				+ sort2.getDirection().name();
		Assert.assertEquals(expectedQuery, flexibleSearchQuery.getQuery());
	}


	@Test
	public void testWithoutSortingAndWithParams()
	{
		final NamedQueryConversionDto namedQueryConversion = new NamedQueryConversionDto().withQuery(QUERY).withNamedQuery( //
				new NamedQuery().withQueryName(QUERY_NAME));
		final Map<String, Object> params = new HashMap<>();
		params.put("param1", Arrays.asList("value1", "value 2"));
		namedQueryConversion.getNamedQuery().setParameters(params);
		final FlexibleSearchQuery flexibleSearchQuery = flexibleSearchNamedQueryConverter.convert(namedQueryConversion);

		Assert.assertEquals(params, flexibleSearchQuery.getQueryParameters());
	}
}
