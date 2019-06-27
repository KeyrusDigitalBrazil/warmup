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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCategoryModelUniqueIdentifierConverterTest
{
	private static final String CATEGORY_ID = "abc-1234";
	private static final String CATEGORY_NAME = "abc-name";
	private static final String CATALOG_VERSION = "catalogversion-1234";
	private static final String CATALOG_ID = "catalog-1234";

	// this is the encoding used

	@Mock
	private CategoryService categoryService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private Converter<CategoryModel, ItemData> categoryModelItemDataConverter;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
	@InjectMocks
	private DefaultCategoryModelUniqueIdentifierConverter converter;
	
	
	private ItemData itemData = new ItemData();
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CategoryModel categoryModel;
	@Mock
	private CatalogModel catalog;
	

	@Before
	public void setup()
	{
		EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setItemId(CATEGORY_ID);
		itemComposedKey.setCatalogVersion(CATALOG_VERSION);
		itemComposedKey.setCatalogId(CATALOG_ID);
		
		
		String composedKey = itemComposedKey.toEncoded();
		itemData.setItemId(composedKey);
		itemData.setName(CATEGORY_NAME);
		itemData.setItemType(CategoryModel._TYPECODE);

		// services
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION)).thenReturn(catalogVersion);
		when(categoryService.getCategoryForCode(catalogVersion, CATEGORY_ID)).thenReturn(categoryModel);

		// restrictions disabler
		when(sessionSearchRestrictionsDisabler.execute(anyObject())).thenReturn(categoryModel);

		// categoryModel
		when(categoryModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(categoryModel.getCode()).thenReturn(CATEGORY_ID);
		// catalog version
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		//catalog
		when(catalog.getId()).thenReturn(CATALOG_ID);
		
		// categoryModelItemDataConverter mocks
		
		when(categoryModelItemDataConverter.convert(categoryModel)).thenReturn(itemData);
	}
	
	@Test
	public void testConvertCategoryFromItemData()
	{
		final CategoryModel category = converter.convert(itemData);
		assertThat(category, is(categoryModel));
	}

	@Test
	public void testConvertCategoryFromCategoryModel()
	{
		final ItemData itemDataConverted = converter.convert(categoryModel);
		assertThat(itemDataConverted.getItemId(), is(itemData.getItemId()));
		assertThat(itemDataConverted.getItemType(), is(CategoryModel._TYPECODE));
		assertThat(itemDataConverted.getName(), is(CATEGORY_NAME));
	}
}
