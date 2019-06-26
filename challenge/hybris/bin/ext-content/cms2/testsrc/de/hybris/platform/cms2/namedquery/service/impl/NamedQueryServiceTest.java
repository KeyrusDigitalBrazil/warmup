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
import de.hybris.platform.cms2.exceptions.InvalidNamedQueryException;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.NamedQueryConversionDto;
import de.hybris.platform.cms2.namedquery.service.NamedQueryFactory;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NamedQueryServiceTest
{

	private static final String QUERY = "QUERY";
	private static final String QUERY_NAME = "QUERY_NAME";

	@Mock
	FlexibleSearchService flexibleSearchService;

	@Mock
	NamedQueryFactory mediaSearchNamedQueryFactory;

	@Mock
	FlexibleSearchNamedQueryConverter flexibleSearchNamedQueryConverter;

	@InjectMocks
	FlexibleSearchNamedQueryService namedQueryService;

	SearchResult<Object> searchResult = new SearchResultImpl<>(Arrays.asList("result 1"), 1, 100, 0);

	@Before
	public void setup() throws InvalidNamedQueryException
	{
		Mockito.when(mediaSearchNamedQueryFactory.getNamedQuery(QUERY_NAME)).thenReturn(QUERY);
	}

	@Test
	public void testNamedQueryServiceSearchCoordination() throws InvalidNamedQueryException
	{
		final FlexibleSearchQuery flexibleSearchQuery = Mockito.mock(FlexibleSearchQuery.class);
		Mockito.when(flexibleSearchNamedQueryConverter.convert(Mockito.any())).thenReturn(flexibleSearchQuery);

		Mockito.when(flexibleSearchService.search(flexibleSearchQuery)).thenReturn(searchResult);

		final List<Object> search = namedQueryService.search(new NamedQuery() //
				.withQueryName(QUERY_NAME) //
				.withCurrentPage(1) //
				.withPageSize(10));

		Assert.assertEquals(searchResult.getResult(), search);
	}

	@Test
	public void testInternalNamedQueryAssignment()
	{
		final NamedQuery namedQuery = new NamedQuery().withSort(new ArrayList<>()).withParameters(new HashMap<>()).withPageSize(100)
				.withCurrentPage(1).withQueryName(QUERY_NAME);
		final NamedQueryConversionDto namedQueryConversionDto = namedQueryService.getInternalNamedQuery(namedQuery, QUERY);
		Assert.assertEquals(namedQuery.getCurrentPage(), namedQueryConversionDto.getNamedQuery().getCurrentPage());
		Assert.assertEquals(namedQuery.getSort(), namedQueryConversionDto.getNamedQuery().getSort());
		Assert.assertEquals(namedQuery.getParameters(), namedQueryConversionDto.getNamedQuery().getParameters());
		Assert.assertEquals(namedQuery.getPageSize(), namedQueryConversionDto.getNamedQuery().getPageSize());
		Assert.assertEquals(namedQuery.getQueryName(), namedQueryConversionDto.getNamedQuery().getQueryName());
		Assert.assertEquals(QUERY, namedQueryConversionDto.getQuery());
	}

	@Test(expected = InvalidNamedQueryException.class)
	public void testInvalidNamedQueryExceptionAfterSearch() throws InvalidNamedQueryException
	{
		Mockito.when(mediaSearchNamedQueryFactory.getNamedQuery(Mockito.anyString())).thenThrow(InvalidNamedQueryException.class);

		final NamedQuery namedQuery = new NamedQuery().withQueryName(QUERY_NAME);

		namedQueryService.search(namedQuery);
	}
}
