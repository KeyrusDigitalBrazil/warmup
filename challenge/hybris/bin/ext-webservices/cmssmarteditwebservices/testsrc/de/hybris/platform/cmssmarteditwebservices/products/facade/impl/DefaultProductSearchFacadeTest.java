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
package de.hybris.platform.cmssmarteditwebservices.products.facade.impl;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.products.ProductSearchFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmssmarteditwebservices.data.CategoryData;
import de.hybris.platform.cmssmarteditwebservices.data.ProductData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductSearchFacadeTest
{

	private static final String TEXT_TO_NON_EMPTY = "textSearch_nonEmptyResults";
	private static final String PRODUCT_CODE = "productCode";
	private static final String CATEGORY_CODE = "categoryCode";
	private static final java.lang.String CATALOG_VERSION = "CATALOG_VERSION";
	private static final java.lang.String CATALOG_ID = "CATALOG_ID";
	
	@Mock
	private ProductSearchFacade productSearchFacade;
	@Mock
	private Converter<de.hybris.platform.cmsfacades.data.ProductData, ProductData> productDataConverter;
	@Mock
	private Converter<de.hybris.platform.cmsfacades.data.CategoryData, CategoryData> categoryDataConverter;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CMSAdminSiteService cmsAdminSiteService;
	@Mock
	private CatalogVersionService catalogVersionService;
	
	@InjectMocks
	@Spy
	private DefaultProductSearchFacade cmsSeProductSearchFacade; 
	
	// mocked objects
	private PageableData pageableData = mock(PageableData.class);
	private de.hybris.platform.cmsfacades.data.ProductData cmsProductData = mock(de.hybris.platform.cmsfacades.data.ProductData.class);
	private de.hybris.platform.cmsfacades.data.CategoryData cmsCategoryData = mock(de.hybris.platform.cmsfacades.data.CategoryData.class);
	
	private ProductData cmsSeProductData = mock(ProductData.class);
	private CategoryData cmsSeCategoryData = mock(CategoryData.class);
	private SearchResult<de.hybris.platform.cmsfacades.data.ProductData> productResult = mock(SearchResult.class);
	private SearchResult<de.hybris.platform.cmsfacades.data.CategoryData> categoryResult = mock(SearchResult.class);
	
	private ItemData categoryItemData = mock(ItemData.class);
	private CategoryModel categoryModel = mock(CategoryModel.class);
	private Optional<ItemModel> categoryModelOptional = Optional.of(categoryModel);
	private CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
	
	private ItemData productItemData = mock(ItemData.class);
	private ProductModel productModel = mock(ProductModel.class);
	private Optional<ItemModel> productModelOptional = Optional.of(productModel);
	
	@Before
	public void setup()
	{
		// productResult mock
		when(productResult.getResult()).thenReturn(Arrays.asList(cmsProductData));
		// categoryResult mock
		when(categoryResult.getResult()).thenReturn(Arrays.asList(cmsCategoryData));
		
		// productSearchFacade mocks
		when(productSearchFacade.getProductByCode(PRODUCT_CODE)).thenReturn(cmsProductData);
		when(productSearchFacade.findProducts(TEXT_TO_NON_EMPTY, pageableData)).thenReturn(productResult);
		
		when(productSearchFacade.getProductCategoryByCode(CATEGORY_CODE)).thenReturn(cmsCategoryData);
		when(productSearchFacade.findProductCategories(TEXT_TO_NON_EMPTY, pageableData)).thenReturn(categoryResult);
		
		// productDataConverter mock
		when(productDataConverter.convert(cmsProductData)).thenReturn(cmsSeProductData);
		
		// categoryDataConverter mock
		when(categoryDataConverter.convert(cmsCategoryData)).thenReturn(cmsSeCategoryData);
		
		// uniqueItemIdentifierService mocks
		when(uniqueItemIdentifierService.getItemModel(categoryItemData)).thenReturn(categoryModelOptional);
		when(uniqueItemIdentifierService.getItemModel(productItemData)).thenReturn(productModelOptional);
				
		// cmsSeProductSearchFacade mocls
		when(cmsSeProductSearchFacade.buildItemData(CATEGORY_CODE, CategoryModel._TYPECODE)).thenReturn(categoryItemData);
		when(cmsSeProductSearchFacade.buildItemData(PRODUCT_CODE, ProductModel._TYPECODE)).thenReturn(productItemData);
		
		// catalogVersion mocls
		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		final CatalogModel catalog = mock(CatalogModel.class);
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalog.getId()).thenReturn(CATALOG_ID);
		
	}

	@Test
	public void testGetProductForUid()
	{
		// productModel mocks
		when(productModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		
		final ProductData product = cmsSeProductSearchFacade.getProductByUid(PRODUCT_CODE);
		verify(productSearchFacade).getProductByCode(PRODUCT_CODE);
		verify(productDataConverter).convert(cmsProductData);
		assertThat(product, is(cmsSeProductData));
	}
	
	@Test
	public void testFindProducts_shouldReturnOneElement()
	{
		final SearchResult<ProductData> products = cmsSeProductSearchFacade.findProducts(TEXT_TO_NON_EMPTY, pageableData);
		verify(productSearchFacade).findProducts(TEXT_TO_NON_EMPTY, pageableData);
		verify(productDataConverter).convert(cmsProductData);
		assertThat(products.getResult(), hasItem(cmsSeProductData));
	}
	
	@Test
	public void testGetProductCategoryForCode()
	{
		// categoryModel mocks
		when(categoryModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(categoryModel.getCode()).thenReturn(CATEGORY_CODE);
		
		final CategoryData category = cmsSeProductSearchFacade.getProductCategoryByUid(CATEGORY_CODE);
		verify(productSearchFacade).getProductCategoryByCode(CATEGORY_CODE);
		verify(categoryDataConverter).convert(cmsCategoryData);
		assertThat(category, is(cmsSeCategoryData));
	}
	
	@Test
	public void testFindProductCategoriesByText()
	{
		final SearchResult<CategoryData> categories = cmsSeProductSearchFacade.findProductCategories(TEXT_TO_NON_EMPTY,
				pageableData);
		verify(productSearchFacade).findProductCategories(TEXT_TO_NON_EMPTY, pageableData);
		verify(categoryDataConverter).convert(cmsCategoryData);
		assertThat(categories.getResult(), hasItem(cmsSeCategoryData));
	}
			
}
