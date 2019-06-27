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
package de.hybris.platform.acceleratorcms.productcarousel.impl;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

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
public class DefaultProductCarouselRendererServiceTest
{

	@InjectMocks
	private DefaultProductCarouselRendererService defaultProductCarouselPreviewRendererService;

	@Mock
	private ProductService productService;

	@Mock
	private SearchRestrictionService searchRestrictionService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private ModelService modelService;

	@Mock
	private CatalogVersionModel activeCatalogVersionModel;

	@Mock
	private CatalogVersionModel stagedCatalogVersionModel;

	@Mock
	private CatalogVersionModel sessionCatalogVersionModel;

	private ProductCarouselComponentModel carouselModel;
	private CategoryModel categoryModel;

	private ProductModel productModel01;
	private ProductModel productModel02;
	private ProductModel productModel03;

	private ProductModel productModel03FromSession;

	private CategoryModel categoryModel01;
	private CategoryModel categoryModel02;

	private List<ProductModel> allProducts;
	private List<CategoryModel> allCategories;

	private CatalogModel catalogModel;

	final String PRODUCT_MODEL_01_CODE = "productModel01_code", PRODUCT_MODEL_02_CODE = "productModel02_code",
			PRODUCT_MODEL_03_CODE = "productModel03_code";

	@Before
	public void setUp()
	{

		carouselModel = spy(new ProductCarouselComponentModel());
		when(carouselModel.getPk()).thenReturn(PK.BIG_PK);

		categoryModel = spy(new CategoryModel());
		when(categoryModel.getPk()).thenReturn(PK.BIG_PK);

		catalogModel = new CatalogModel();
		catalogModel.setId("id");

		setProductsAndCategoriesData();

	}

	@Test
	public void testGetDisplayableProducts_noProducts_for_productcarousel()
	{

		carouselModel.setProducts(Arrays.asList());
		when(modelService.get(PK.BIG_PK)).thenReturn(carouselModel);

		final List<ProductModel> products = defaultProductCarouselPreviewRendererService.getDisplayableProducts(carouselModel);
		assertTrue(products.isEmpty());

	}

	@Test
	public void testGetDisplayableProducts_hasProducts_for_productcarousel()
	{
		carouselModel.setProducts(allProducts);
		when(modelService.get(PK.BIG_PK)).thenReturn(carouselModel);

		when(stagedCatalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(activeCatalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogVersionService.getSessionCatalogVersionForCatalog("id")).thenReturn(sessionCatalogVersionModel);

		when(productService.getProductForCode(sessionCatalogVersionModel, PRODUCT_MODEL_01_CODE)).thenReturn(productModel01);
		when(productService.getProductForCode(sessionCatalogVersionModel, PRODUCT_MODEL_02_CODE))
				.thenThrow(UnknownIdentifierException.class);
		when(productService.getProductForCode(sessionCatalogVersionModel, PRODUCT_MODEL_03_CODE))
				.thenReturn(productModel03FromSession);

		final List<ProductModel> products = defaultProductCarouselPreviewRendererService.getDisplayableProducts(carouselModel);
		assertThat(products, containsInAnyOrder(productModel01, productModel02, productModel03FromSession));

	}

	@Test
	public void testGetDisplayableProducts_noProducts_for_category()
	{

		categoryModel.setProducts(Arrays.asList());
		when(modelService.get(PK.BIG_PK)).thenReturn(categoryModel);

		final List<ProductModel> products = defaultProductCarouselPreviewRendererService.getDisplayableProducts(categoryModel);
		assertTrue(products.isEmpty());

	}

	@Test
	public void testGetDisplayableProducts_hasProducts_for_category()
	{

		categoryModel.setProducts(allProducts);
		when(modelService.get(PK.BIG_PK)).thenReturn(categoryModel);

		when(stagedCatalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(activeCatalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogVersionService.getSessionCatalogVersionForCatalog("id")).thenReturn(sessionCatalogVersionModel);

		when(productService.getProductForCode(sessionCatalogVersionModel, PRODUCT_MODEL_01_CODE)).thenReturn(productModel01);
		when(productService.getProductForCode(sessionCatalogVersionModel, PRODUCT_MODEL_02_CODE))
				.thenThrow(UnknownIdentifierException.class);
		when(productService.getProductForCode(sessionCatalogVersionModel, PRODUCT_MODEL_03_CODE))
				.thenReturn(productModel03FromSession);


		final List<ProductModel> products = defaultProductCarouselPreviewRendererService.getDisplayableProducts(categoryModel);
		assertThat(products, containsInAnyOrder(productModel01, productModel02, productModel03FromSession));

	}

	@Test
	public void testGetListOfCategories_noCategories_for_productcarousel()
	{

		carouselModel.setCategories(Arrays.asList());
		when(modelService.get(PK.BIG_PK)).thenReturn(carouselModel);

		final List<CategoryModel> categories = defaultProductCarouselPreviewRendererService.getListOfCategories(carouselModel);
		assertTrue(categories.isEmpty());

	}

	@Test
	public void testGetListOfCategories_hasCategories_for_productcarousel()
	{

		carouselModel.setCategories(allCategories);
		when(modelService.get(PK.BIG_PK)).thenReturn(carouselModel);

		final List<CategoryModel> categories = defaultProductCarouselPreviewRendererService.getListOfCategories(carouselModel);
		assertThat(categories, containsInAnyOrder(categoryModel01, categoryModel02));

	}

	private void setProductsAndCategoriesData()
	{

		final Boolean staged = Boolean.valueOf(false);
		final Boolean active = Boolean.valueOf(true);

		activeCatalogVersionModel.setActive(active);
		stagedCatalogVersionModel.setActive(staged);

		productModel01 = new ProductModel();
		productModel01.setCode(PRODUCT_MODEL_01_CODE);
		productModel01.setCatalogVersion(activeCatalogVersionModel);

		productModel02 = new ProductModel();
		productModel02.setCode(PRODUCT_MODEL_02_CODE);
		productModel02.setCatalogVersion(stagedCatalogVersionModel);

		productModel03 = new ProductModel();
		productModel03.setCode(PRODUCT_MODEL_03_CODE);
		productModel03.setCatalogVersion(stagedCatalogVersionModel);

		allProducts = Arrays.asList(productModel01, productModel02, productModel03);
		allCategories = Arrays.asList(categoryModel01, categoryModel02);

		productModel03FromSession = new ProductModel();
	}

}
