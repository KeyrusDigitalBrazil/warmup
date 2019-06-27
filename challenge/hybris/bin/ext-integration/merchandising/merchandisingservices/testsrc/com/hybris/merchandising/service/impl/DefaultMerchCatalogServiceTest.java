/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.DefaultCategoryModelUrlResolver;
import de.hybris.platform.site.BaseSiteService;

/**
 * DefaultMerchCatalogServiceTest is a test suite for {@link DefaultMerchCatalogService}.
 *
 */
public class DefaultMerchCatalogServiceTest {
	DefaultMerchCatalogService merchCatalogService;
	BaseSiteModel baseSite;
	BaseSiteService baseSiteService;

	public static final String APPAREL_UK = "apparel-uk";
	public static final String CATALOG_ID = "123";
	public static final String VERSION = "live";
	public static final String BASE_CAT_URL = "https://hybris.com";

	@Before
	public void setUp() {
		baseSite = Mockito.mock(BaseSiteModel.class);
		baseSiteService = Mockito.mock(BaseSiteService.class);
		Mockito.when(baseSiteService.getBaseSiteForUID(APPAREL_UK)).thenReturn(baseSite);

		final List<CatalogModel> mockedCatalog = new ArrayList<>();
		mockedCatalog.add(getMockCatalogModel());
		Mockito.when(baseSiteService.getProductCatalogs(Mockito.any(BaseSiteModel.class))).thenReturn(mockedCatalog);

		final DefaultCategoryModelUrlResolver mockedUrlResolver = Mockito.mock(DefaultCategoryModelUrlResolver.class);

		Mockito.when(mockedUrlResolver.resolve(Mockito.any())).thenReturn("");
		merchCatalogService = new DefaultMerchCatalogService();
		merchCatalogService.setBaseSiteService(baseSiteService);
		merchCatalogService.setCategoryUrlResolver(mockedUrlResolver);
	}

	@Test
	public void testGetCategories() {
		final List<CategoryHierarchy> retrievedValue = merchCatalogService.getCategories(APPAREL_UK, CATALOG_ID, VERSION, BASE_CAT_URL);
		Assert.assertNotNull("Expected retrieved result to not be null", retrievedValue);
		Assert.assertEquals("Expected retrieved value to contain 1 root category", 1, retrievedValue.size());

		final CategoryHierarchy root = retrievedValue.get(0);
		Assert.assertEquals("Expected root category to contain 2 children", 2, root.getSubcategories().size());
		final CategoryHierarchy subCat1 = root.getSubcategories().get(0);
		Assert.assertEquals("Expected child category to contain 1 child", 1, subCat1.getSubcategories().size());
	}

	@Test
	public void testGetBaseSiteService() {
		final BaseSiteService service = merchCatalogService.getBaseSiteService();
		Assert.assertNotNull("Expected configured base site service to not be null", service);
		Assert.assertEquals("Expected baseSiteService to be the same as injected", baseSiteService, service);
	}

	private CatalogModel getMockCatalogModel() {
		final CatalogModel mockedCatalogModel = Mockito.mock(CatalogModel.class);
		final Set<CatalogVersionModel> catVersion = new HashSet<>();
		final CatalogVersionModel cvm = getMockCatalogVersionModel(Boolean.TRUE);
		catVersion.add(cvm);
		final CatalogVersionModel nonLive = getMockCatalogVersionModel(Boolean.FALSE);
		Mockito.when(mockedCatalogModel.getCatalogVersions()).thenReturn(catVersion);
		return mockedCatalogModel;
	}

	private CatalogVersionModel getMockCatalogVersionModel(final Boolean live) {
		final CatalogVersionModel mockedCatalogVersionModel = Mockito.mock(CatalogVersionModel.class);

		final CategoryModel subCatL2 = getCategoryModel("subCatL2", "subCatL2", null);
		final CategoryModel subCatL1 = getCategoryModel("subCatL1", "subCatL1", Arrays.asList(new CategoryModel[] {subCatL2}));
		final CategoryModel subCatL1_2 = getCategoryModel("subCatL1_2", "subCatL1_2", null);
		final CategoryModel rootCategory = getCategoryModel("root", "root", Arrays.asList(new CategoryModel[] {subCatL1, subCatL1_2}));
		Mockito.when(mockedCatalogVersionModel.getActive()).thenReturn(live);
		Mockito.when(mockedCatalogVersionModel.getRootCategories()).thenReturn(Arrays.asList(new CategoryModel[] {rootCategory}));
		return mockedCatalogVersionModel;
	}

	private CategoryModel getCategoryModel(final String code, final String name, final List<CategoryModel> categories) {
		final CategoryModel mockedCategoryModel = Mockito.mock(CategoryModel.class);
		Mockito.when(mockedCategoryModel.getCode()).thenReturn("code");
		Mockito.when(mockedCategoryModel.getName()).thenReturn("name");
		if(categories != null) {
			Mockito.when(mockedCategoryModel.getCategories()).thenReturn(categories);
		}
		return mockedCategoryModel;
	}
}
