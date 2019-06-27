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
package de.hybris.platform.ordersplitting;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class DefaultWarehouseDaoTest extends ServicelayerTest
{

	@Resource
	private DefaultWarehouseDao warehouseDao;

	@Resource
	private ModelService modelService;

	private WarehouseModel warehouse1;
	private static final String WAREHOUSE1_CODE = "w1";
	private WarehouseModel warehouse2;
	private static final String WAREHOUSE2_CODE = "w2";
	private WarehouseModel warehouse3;
	private static final String WAREHOUSE3_CODE = "w3";
	private WarehouseModel warehouse4;


	private VendorModel vendor1;
	private static final String VENDOR1CODE = "v1";
	private VendorModel vendor2;
	private static final String VENDOR2CODE = "v2";
	private VendorModel vendor3; //NOPMD
	private static final String VENDOR3CODE = "v3";

	private CatalogModel catalog; //NOPMD
	private CatalogVersionModel catalogVersion; //NOPMD

	private ProductModel product1; //NOPMD
	private static final String PRODUCT1CODE = "P1";
	private ProductModel product2; //NOPMD
	private static final String PRODUCT2CODE = "P2";
	private ProductModel product3; //NOPMD
	private static final String PRODUCT3CODE = "P3";

	/**
	 *
	 */
	@Before
	public void setUp() throws Exception
	{
		vendor1 = createVendor(VENDOR1CODE);
		vendor2 = createVendor(VENDOR2CODE);
		vendor3 = createVendor(VENDOR3CODE);
		warehouse1 = createWarehouse(WAREHOUSE1_CODE, vendor1, Boolean.TRUE);
		warehouse2 = createWarehouse(WAREHOUSE2_CODE, vendor2, Boolean.FALSE);
		warehouse3 = createWarehouse(WAREHOUSE3_CODE, vendor3, Boolean.FALSE);
		warehouse4 = createWarehouse(WAREHOUSE3_CODE, vendor3, Boolean.TRUE);

		catalog = modelService.create(CatalogModel.class);
		catalog.setId("id1");
		modelService.save(catalog);

		catalogVersion = modelService.create(CatalogVersionModel.class);
		catalogVersion.setVersion("v1");
		catalogVersion.setCatalog(catalog);
		modelService.save(catalogVersion);

		product1 = createProduct(PRODUCT1CODE, catalogVersion);
		product2 = createProduct(PRODUCT2CODE, catalogVersion);
		product3 = createProduct(PRODUCT3CODE, catalogVersion);

		createStockLevel(product1, 10, warehouse1);
		createStockLevel(product2, 10, warehouse1);
		createStockLevel(product3, 10, warehouse1);

		createStockLevel(product1, 1, warehouse2);
		createStockLevel(product2, 3, warehouse2);
		createStockLevel(product3, 5, warehouse2);

		createStockLevel(product1, 1, warehouse3);
		createStockLevel(product2, 1, warehouse3);

	}

	private StockLevelModel createStockLevel(final ProductModel product, final int quantity, final WarehouseModel warehouse)
	{
		final StockLevelModel res = modelService.create(StockLevelModel.class);
		res.setProductCode(product.getCode());
		res.setAvailable(quantity);
		res.setWarehouse(warehouse);
		modelService.save(res);
		return res;
	}

	private ProductModel createProduct(final String code, final CatalogVersionModel version)
	{
		final ProductModel res = modelService.create(ProductModel.class);
		res.setCode(code);
		res.setCatalogVersion(version);
		modelService.save(res);
		return res;
	}

	private WarehouseModel createWarehouse(final String code, final VendorModel vendor, final Boolean def)
	{
		final WarehouseModel res = modelService.create(WarehouseModel.class);
		res.setCode(code);
		res.setVendor(vendor);
		res.setDefault(def);
		modelService.save(res);
		return res;
	}

	private VendorModel createVendor(final String code)
	{
		final VendorModel res = modelService.create(VendorModel.class);
		res.setCode(code);
		modelService.save(res);
		return res;
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getWarehouseForCode(java.lang.String)}.
	 */
	@Test
	public void testGetWarehouseForCode()
	{
		Assertions.assertThat(warehouseDao.getWarehouseForCode(WAREHOUSE1_CODE)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehouseForCode(WAREHOUSE2_CODE)).containsOnly(warehouse2);
		Assertions.assertThat(warehouseDao.getWarehouseForCode(WAREHOUSE3_CODE)).containsOnly(warehouse3, warehouse4);
	}

	/**
	 * Test method for {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getDefWarehouse()}.
	 */
	@Test
	public void testGetDefWarehouse()
	{
		Assertions.assertThat(warehouseDao.getDefWarehouse()).containsOnly(warehouse1, warehouse4);
	}

	/**
	 * Test method for {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getWarehouses(String)}.
	 */
	@Test
	public void testGetWarehouses()
	{
		Assertions.assertThat(warehouseDao.getWarehouses(PRODUCT1CODE)).containsOnly(warehouse1, warehouse2, warehouse3);
		Assertions.assertThat(warehouseDao.getWarehouses(PRODUCT2CODE)).containsOnly(warehouse1, warehouse2, warehouse3);
		Assertions.assertThat(warehouseDao.getWarehouses(PRODUCT3CODE)).containsOnly(warehouse1, warehouse2);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.ordersplitting.daos.impl.DefaultWarehouseDao#getWarehousesWithProductsInStock(String, long, VendorModel)}.
	 */
	@Test
	public void testGetWarehousesWithProductsInStock()
	{
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT1CODE, 2, null)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT2CODE, 3, null)).containsOnly(warehouse1,
				warehouse2);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT3CODE, 4, null)).containsOnly(warehouse1,
				warehouse2);

		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT1CODE, 2, vendor1)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT1CODE, 2, vendor2)).isEmpty();

		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT2CODE, 3, vendor1)).containsOnly(warehouse1);
		Assertions.assertThat(warehouseDao.getWarehousesWithProductsInStock(PRODUCT2CODE, 3, vendor2)).containsOnly(warehouse2);
	}

}
