/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.atp.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.warehousing.atp.strategy.impl.WarehousingAvailabilityCalculationStrategy;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehouseStockServiceTest
{
	private static final Long ZERO = Long.valueOf(0);
	private static final Long TEN = Long.valueOf(10);

	@InjectMocks
	private final DefaultWarehouseStockService warehouseStockService = new DefaultWarehouseStockService();

	@Mock
	private StockService stockService;
	@Mock
	private WarehousingAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;

	@Mock
	private StockLevelModel stockLevel;
	@Mock
	private AtpFormulaModel atpFormula;
	@Mock
	private StockLevelDao stockLevelDao;

	private ProductModel product;
	private WarehouseModel warehouse;

	@Before
	public void setUp()
	{
		product = new ProductModel();
		product.setCode("product1");
		warehouse = new WarehouseModel();

		when(stockLevelDao.findStockLevels(product.getCode(), Collections.singleton(warehouse))).thenReturn(Arrays.asList(stockLevel));
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Mockito.anyCollection())).thenReturn(TEN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_ProductCodeNull()
	{
		warehouseStockService.getStockLevelForProductCodeAndWarehouse(null, warehouse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_WarehouseNull()
	{
		warehouse = null;
		warehouseStockService.getStockLevelForProductCodeAndWarehouse(product.getCode(), warehouse);
	}

	@Test
	public void shouldNotBeAvailable_NoStockLevelFound()
	{
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Mockito.anyCollection())).thenReturn(ZERO);
		final Long availability = warehouseStockService.getStockLevelForProductCodeAndWarehouse(product.getCode(), warehouse);
		assertEquals(ZERO, availability);
	}

	@Test
	public void shouldNotBeAvailable_FailToCalculate()
	{
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(Mockito.anyCollection())).thenReturn(null);
		final Long availability = warehouseStockService.getStockLevelForProductCodeAndWarehouse(product.getCode(), warehouse);
		assertNull(availability);
	}

	@Test
	public void shouldGetAvailability()
	{
		final Long availability = warehouseStockService.getStockLevelForProductCodeAndWarehouse(product.getCode(), warehouse);
		assertEquals(TEN, availability);
	}
}
