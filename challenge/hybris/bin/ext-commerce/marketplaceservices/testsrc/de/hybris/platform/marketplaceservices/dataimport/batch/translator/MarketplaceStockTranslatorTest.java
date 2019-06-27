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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockImportAdapter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;


@UnitTest
public class MarketplaceStockTranslatorTest
{
	private static final String PRODUCT_CODE = "Canon_123456";
	private static final String WAREHOUSE_CODE_1 = "warehouse1";

	private MarketplaceStockTranslator translator;
	private Item item;
	private ProductModel product;
	private WarehouseModel warehouse1;

	@Mock
	private StockImportAdapter stockImportAdapter;

	@Mock
	private ModelService modelService;

	@Mock
	private VendorService vendorService;

	@Mock
	private VendorModel vendor;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		translator = new MarketplaceStockTranslator();
		translator.setStockImportAdapter(stockImportAdapter);
		translator.setModelService(modelService);
		translator.setStockImportAdapter(stockImportAdapter);
		translator.setVendorService(vendorService);
		product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		warehouse1 = new WarehouseModel();
		warehouse1.setCode(WAREHOUSE_CODE_1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProductWithoutVendor()
	{
		Mockito.when(modelService.get(item)).thenReturn(product);
		Mockito.when(vendorService.getVendorByProduct(product)).thenReturn(Optional.empty());
		translator.performImport("66", item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testVendorWithoutWarehouse()
	{
		Mockito.when(modelService.get(item)).thenReturn(product);
		Mockito.when(vendorService.getVendorByProduct(product)).thenReturn(Optional.of(vendor));
		Mockito.when(vendor.getWarehouses()).thenReturn(new HashSet<WarehouseModel>());
		translator.performImport("66", item);
	}

	@Test
	public void testValidProduct()
	{
		Mockito.when(modelService.get(item)).thenReturn(product);
		Mockito.when(vendorService.getVendorByProduct(product)).thenReturn(Optional.of(vendor));
		Mockito.when(vendor.getWarehouses()).thenReturn(new HashSet<WarehouseModel>(Arrays.asList(warehouse1)));
		translator.performImport("66", item);
		Mockito.verify(stockImportAdapter).performImport("66:warehouse1", item);
	}
}
