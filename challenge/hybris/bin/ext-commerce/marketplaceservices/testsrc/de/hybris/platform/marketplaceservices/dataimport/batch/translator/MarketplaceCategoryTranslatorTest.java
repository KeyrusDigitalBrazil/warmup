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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.DescriptorParams;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;


@UnitTest
public class MarketplaceCategoryTranslatorTest
{
	private static final String VENDOR_CODE = "Canon";
	private static final String CATALOG_ID = "globalMarketplaceProductCatalog";
	private static final String CATALOG_VERSION = "Online";

	private static final String CATEGORY_CODE_1 = "Category1";
	private static final String CATEGORY_CODE_2 = "Category2";
	private static final String PARENT_CATEGORY_CODE = "Category3";
	private static final String IMPORT_CATEGORY = String.join(",", CATEGORY_CODE_1, CATEGORY_CODE_2);

	private MarketplaceCategoryTranslator translator;
	private TestDescriptorParams params;
	private Item item;
	private CatalogVersionModel catalogVersion;
	private CategoryModel category1, category2, parentCategory;

	@Mock
	private ModelService modelService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private VendorService vendorService;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private VendorModel vendor;

	@Mock
	private StandardColumnDescriptor descriptor;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new MarketplaceCategoryTranslator();
		translator.init(descriptor);
		translator.setCategoryService(categoryService);
		translator.setCatalogVersionService(catalogVersionService);
		translator.setVendorService(vendorService);

		params = new TestDescriptorParams(ImmutableMap.of("vendor", VENDOR_CODE, "globalCatalogId", CATALOG_ID,
				"globalCatalogVersion", CATALOG_VERSION));

		category1 = new CategoryModel();
		category1.setCode(CATEGORY_CODE_1);

		category2 = new CategoryModel();
		category2.setCode(CATEGORY_CODE_2);

		parentCategory = new CategoryModel();
		parentCategory.setCode(PARENT_CATEGORY_CODE);

		Mockito.when(descriptor.getDescriptorData()).thenReturn(params);
		Mockito.when(vendorService.getVendorByCode(VENDOR_CODE)).thenReturn(Optional.of(vendor));
		Mockito.when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION)).thenReturn(catalogVersion);
		Mockito.when(categoryService.getCategoryForCode(catalogVersion, CATEGORY_CODE_1)).thenReturn(category1);
		Mockito.when(categoryService.getCategoryForCode(catalogVersion, CATEGORY_CODE_2)).thenReturn(category2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoCategory()
	{
		translator.importValue("", item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoVendor()
	{
		Mockito.when(vendorService.getVendorByCode(VENDOR_CODE)).thenReturn(Optional.empty());
		translator.importValue(IMPORT_CATEGORY, item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVendorCategoryIsNull()
	{
		Mockito.when(vendor.getCategories()).thenReturn(null);
		translator.importValue(IMPORT_CATEGORY, item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVendorCategoryNoMatch()
	{
		Mockito.when(vendor.getCategories()).thenReturn(new HashSet<>());
		translator.importValue(IMPORT_CATEGORY, item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVendorCategoryPartialMatch()
	{
		Mockito.when(vendor.getCategories()).thenReturn(new HashSet<>(Arrays.asList(category1)));
		translator.importValue(IMPORT_CATEGORY, item);
	}

	@Test
	public void testVendorCategoryAllMatch()
	{
		final Set<CategoryModel> result = new HashSet<>(Arrays.asList(category1, category2));
		Mockito.when(vendor.getCategories()).thenReturn(result);
		assertEquals(result, translator.importValue(IMPORT_CATEGORY, item));
	}

	@Test
	public void testVendorCategoryParentMatch()
	{
		final Set<CategoryModel> result = new HashSet<>(Arrays.asList(category1, category2));
		Mockito.when(categoryService.getAllSupercategoriesForCategory(category1)).thenReturn(Arrays.asList(parentCategory));
		Mockito.when(categoryService.getAllSupercategoriesForCategory(category2)).thenReturn(Arrays.asList(parentCategory));
		Mockito.when(vendor.getCategories()).thenReturn(Arrays.asList(parentCategory));
		assertEquals(result, translator.importValue(IMPORT_CATEGORY, item));
	}

	private static final class TestDescriptorParams extends DescriptorParams
	{
		public TestDescriptorParams(Map<String, String> m)
		{
			this.addAllModifier(m);
		}
	}
}
