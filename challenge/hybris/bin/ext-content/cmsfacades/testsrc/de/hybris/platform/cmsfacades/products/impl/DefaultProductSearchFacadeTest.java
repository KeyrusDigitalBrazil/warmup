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
package de.hybris.platform.cmsfacades.products.impl;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cmsfacades.products.service.ProductCategorySearchService;
import de.hybris.platform.cmsfacades.products.service.ProductSearchService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductSearchFacadeTest
{

	private static final String TEXT_TO_NON_EMPTY = "textSearch_nonEmptyResults";
	private static final String PRODUCT_CODE = "productCode";
	private static final String CATEGORY_CODE = "categoryCode";
	@Mock
	private ProductService productService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private Converter<ProductModel, de.hybris.platform.cmsfacades.data.ProductData> productDataConverter;
	@Mock
	private Converter<CategoryModel, de.hybris.platform.cmsfacades.data.CategoryData> categoryDataConverter;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private ProductCategorySearchService productCategorySearchService;
	@Mock
	private ProductSearchService productSearchService;
	
	@InjectMocks
	private DefaultProductSearchFacade cmsProductSearchFacade; 
	
	// mocked objects
	private CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
	private PageableData pageableData = mock(PageableData.class);
	private ProductModel productModel = mock(ProductModel.class);
	private de.hybris.platform.cmsfacades.data.ProductData cmsProductData = mock(de.hybris.platform.cmsfacades.data.ProductData.class);
	private CategoryModel categoryModel = mock(CategoryModel.class);
	private de.hybris.platform.cmsfacades.data.CategoryData cmsCategoryData = mock(de.hybris.platform.cmsfacades.data.CategoryData.class);
	
	private SearchResult<de.hybris.platform.category.model.CategoryModel> categoryModelSearchResult = mock(SearchResult.class);
	private List<CategoryModel> categoryModelList = new ArrayList<>();

	private SearchResult<ProductModel> productModelSearchResult = mock(SearchResult.class);
	private List<ProductModel> productModelList = new ArrayList<>();

	@Before
	public void setup()
	{
		// catalogVersionService mocks
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Arrays.asList(catalogVersion));

		// productService mocks
		
		when(productService.getProductForCode(catalogVersion, PRODUCT_CODE)).thenReturn(productModel);
		
		// productDataConverter mocks
		when(productDataConverter.convert(productModel)).thenReturn(cmsProductData);
		
		// productCategorySearchService mocks
		SearchResult<CategoryModel> nonEmptySearchResult = prepareServiceNonEmptySearchResults();
		when(productCategorySearchService.findProductCategories(TEXT_TO_NON_EMPTY, pageableData, catalogVersion)).thenReturn(nonEmptySearchResult);
		
		// categoryService mocks
		when(categoryService.getCategoryForCode(catalogVersion, CATEGORY_CODE)).thenReturn(categoryModel);
		
		// categoryDataConverter mocks
		when(categoryDataConverter.convert(categoryModel)).thenReturn(cmsCategoryData);
		
		// productCategorySearchService mocks
		categoryModelList.add(categoryModel);
		when(categoryModelSearchResult.getResult()).thenReturn(categoryModelList);
		when(productCategorySearchService.findProductCategories(TEXT_TO_NON_EMPTY, pageableData, catalogVersion)).thenReturn(categoryModelSearchResult);
		
		// cmsProductSearchFacade mocks
		productModelList.add(productModel);
		when(productModelSearchResult.getResult()).thenReturn(productModelList);
		when(productSearchService.findProducts(TEXT_TO_NON_EMPTY, pageableData, catalogVersion)).thenReturn(productModelSearchResult);
	}


	protected SearchResult<CategoryModel> prepareServiceNonEmptySearchResults()
	{
		final SearchResult<CategoryModel> searchResult = mock(SearchResult.class);

		final CategoryModel categoryModel = getCategoryModel();

		when(searchResult.getResult()).thenReturn(Arrays.asList(categoryModel));
		return searchResult;
	}

	private CategoryModel getCategoryModel()
	{
		return mock(CategoryModel.class);
	}
	
	@Test
	public void testGetProductForCode()
	{
		final de.hybris.platform.cmsfacades.data.ProductData product = cmsProductSearchFacade.getProductByCode(
				PRODUCT_CODE);
		verify(productDataConverter).convert(productModel);
		verify(productService).getProductForCode(catalogVersion, PRODUCT_CODE);
		assertThat(product, is(cmsProductData));
	}
	
	@Test
	public void testFindProducts_shouldReturnOneElement()
	{
		final SearchResult<de.hybris.platform.cmsfacades.data.ProductData> products = cmsProductSearchFacade.findProducts(
				TEXT_TO_NON_EMPTY, pageableData);
		verify(productSearchService).findProducts(TEXT_TO_NON_EMPTY, pageableData, catalogVersion);
		assertThat(products.getResult(), hasItem(cmsProductData));
	}
	
	@Test
	public void testGetProductCategoryForCode()
	{
		final de.hybris.platform.cmsfacades.data.CategoryData category = cmsProductSearchFacade
				.getProductCategoryByCode(CATEGORY_CODE);
		verify(categoryService).getCategoryForCode(catalogVersion, CATEGORY_CODE);
		verify(categoryDataConverter).convert(categoryModel);
		assertThat(category, is(cmsCategoryData));
	}
	
	@Test
	public void testFindroductCategoriesByText()
	{
		final SearchResult<de.hybris.platform.cmsfacades.data.CategoryData> productCategories = cmsProductSearchFacade
				.findProductCategories(TEXT_TO_NON_EMPTY, pageableData);
		verify(categoryDataConverter).convert(categoryModel);
		verify(productCategorySearchService).findProductCategories(TEXT_TO_NON_EMPTY, pageableData, catalogVersion);
		assertThat(productCategories.getResult(), hasItem(cmsCategoryData));
	}
			
}
