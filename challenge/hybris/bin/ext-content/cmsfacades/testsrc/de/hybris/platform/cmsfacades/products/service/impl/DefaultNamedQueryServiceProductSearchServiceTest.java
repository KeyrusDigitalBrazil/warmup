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
package de.hybris.platform.cmsfacades.products.service.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.service.NamedQueryService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNamedQueryServiceProductSearchServiceTest
{

	private static final String TEXT = "text";
	@Mock
	private NamedQueryService namedQueryService;

	@InjectMocks
	private DefaultNamedQueryServiceProductSearchService productSearchService;

	private final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
	private final PageableData pageableData = mock(PageableData.class);
	private final SearchResult searchResult = mock(SearchResult.class);
	private final ProductModel productModel = mock(ProductModel.class);

	@Before
	public void setup()
	{
		when(searchResult.getResult()).thenReturn(Arrays.asList(productModel));
		when(namedQueryService.getSearchResult(Mockito.any())).thenReturn(searchResult);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedQueryForProductSearchWithNullValues()
	{
		productSearchService.getNamedQueryForProductSearch(null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetNamedQueryForProductSearchWithNullatalogVersion()
	{
		productSearchService.getNamedQueryForProductSearch(null, new PageableData(), null);
	}

	@Test
	public void testGetNamedQueryForProductSearchWithNullTextAndNullSort()
	{
		final NamedQuery namedQuery = productSearchService
				.getNamedQueryForProductSearch(
						null, new PageableData(), catalogVersion);
		assertThat(namedQuery.getSort().get(0).getParameter(), is(ProductModel.NAME));
		assertThat(namedQuery.getSort().get(0).getDirection(), is(SortDirection.ASC));
		assertThat(namedQuery.getParameters().get(ProductModel.NAME), is("%%"));
		assertThat(namedQuery.getParameters().get(ProductModel.DESCRIPTION), is("%%"));
		assertThat(namedQuery.getParameters().get(ProductModel.CODE), is("%%"));
	}

	@Test
	public void testFindProductCategories()
	{
		final SearchResult<ProductModel> productCategories = productSearchService.findProducts(TEXT,
				pageableData, catalogVersion);
		verify(namedQueryService).getSearchResult(Mockito.any());
		assertThat(productCategories.getResult(), hasItem(productModel));
	}

}
