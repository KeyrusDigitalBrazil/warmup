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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CPSBaseSiteProviderTest
{
	private CPSBaseSiteProvider classUnderTest;
	private BaseSiteModel baseSite;

	@Mock
	private FlexibleSearchService flexibleSearch;
	@Mock
	private SearchResult<BaseSiteModel> searchResult;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new CPSBaseSiteProvider();
		classUnderTest.setFlexibleSearchService(flexibleSearch);

		Mockito.<SearchResult<? extends BaseSiteModel>> when(flexibleSearch.search(anyString())).thenReturn(searchResult);

		baseSite = new BaseSiteModel();
	}

	@Test
	public void testOneBaseSite()
	{
		final List<BaseSiteModel> resultList = new ArrayList<>();
		resultList.add(baseSite);
		when(searchResult.getCount()).thenReturn(1);
		when(searchResult.getResult()).thenReturn(resultList);

		final BaseSiteModel result = classUnderTest.getConfiguredBaseSite();

		assertEquals(baseSite, result);
	}

	@Test
	public void testTwoBaseSite()
	{
		final List<BaseSiteModel> resultList = new ArrayList<>();
		resultList.add(baseSite);
		resultList.add(new BaseSiteModel());
		when(searchResult.getCount()).thenReturn(2);
		when(searchResult.getResult()).thenReturn(resultList);

		final BaseSiteModel result = classUnderTest.getConfiguredBaseSite();

		assertEquals(baseSite, result);
	}

	@Test(expected = IllegalStateException.class)
	public void testNoBaseSite()
	{
		classUnderTest.getConfiguredBaseSite();
	}
}
