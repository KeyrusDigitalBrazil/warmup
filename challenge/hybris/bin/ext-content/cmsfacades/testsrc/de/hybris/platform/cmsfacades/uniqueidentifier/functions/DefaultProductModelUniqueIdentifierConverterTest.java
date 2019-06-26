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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductModelUniqueIdentifierConverterTest
{
	private static final String PRODUCT_ID = "abc-1234";
	private static final String PRODUCT_NAME = "abc-name";
	private static final String CATALOG_VERSION = "catalogversion-1234";
	private static final String CATALOG_ID = "catalog-1234";

	// this is the encoding used

	@Mock
	private ProductService productService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private Converter<ProductModel, ItemData> productModelItemDataConverter;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
	@InjectMocks
	private DefaultProductModelUniqueIdentifierConverter converter;
	
	
	private ItemData itemData = new ItemData();
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private ProductModel productModel;
	@Mock
	private CatalogModel catalog;

	@Before
	public void setup()
	{
		EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
		itemComposedKey.setItemId(PRODUCT_ID);
		itemComposedKey.setCatalogVersion(CATALOG_VERSION);
		itemComposedKey.setCatalogId(CATALOG_ID);
		
		
		String composedKey = itemComposedKey.toEncoded();
		itemData.setItemId(composedKey);
		itemData.setName(PRODUCT_NAME);
		itemData.setItemType(ProductModel._TYPECODE);

		// services
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION)).thenReturn(catalogVersion);
		when(productService.getProductForCode(catalogVersion, PRODUCT_ID)).thenReturn(productModel);
		
		// productModel
		when(productModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(productModel.getCode()).thenReturn(PRODUCT_ID);

		// restrictions disabler
		when(sessionSearchRestrictionsDisabler.execute(anyObject())).thenReturn(productModel);

		// catalog version
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		//catalog
		when(catalog.getId()).thenReturn(CATALOG_ID);

		// productModelItemDataConverter mocks
		when(productModelItemDataConverter.convert(productModel)).thenReturn(itemData);
	}
	
	@Test
	public void testConvertProductFromItemData()
	{
		final ProductModel product = converter.convert(itemData);
		assertThat(product, is(productModel));
	}

	@Test
	public void testConvertProductFromProductModel()
	{
		final ItemData itemDataConverted = converter.convert(productModel);
		assertThat(itemDataConverted.getItemId(), is(itemData.getItemId()));
		assertThat(itemDataConverted.getItemType(), is(ProductModel._TYPECODE));
		assertThat(itemDataConverted.getName(), is(PRODUCT_NAME));
	}
}
