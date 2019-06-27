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
package de.hybris.platform.warehousing.atp.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.atp.services.impl.WarehousingCommerceStockService;
import de.hybris.platform.warehousing.atp.strategy.PickupWarehouseSelectionStrategy;
import de.hybris.platform.warehousing.atp.strategy.impl.WarehousingAvailabilityCalculationStrategy;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class WarehousingCommerceStockServiceTest
{
	private static final Long ZERO = Long.valueOf(0);
	private static final Long TEN = Long.valueOf(10);

	@InjectMocks
	private final WarehousingCommerceStockService warehousingCommerceStockService = new WarehousingCommerceStockService();
	@Mock
	private DefaultStockService stockService;
	@Mock
	private WarehousingAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;
	@Mock
	private PickupWarehouseSelectionStrategy pickupWarehouseSelectionStrategy;

	private PointOfServiceModel pointOfService;
	private ProductModel product;
	private WarehouseModel warehouse;
	private List<WarehouseModel> warehouses;
	private StockLevelModel stockLevel;
	private List<StockLevelModel> stockLevels;
	private BaseStoreModel baseStore;
	private AtpFormulaModel atpFormula;

	@Before
	public void setUp()
	{
		baseStore = new BaseStoreModel();
		warehouse = new WarehouseModel();
		warehouses = Arrays.asList(warehouse);
		atpFormula = new AtpFormulaModel();
		baseStore.setDefaultAtpFormula(atpFormula);

		pointOfService = new PointOfServiceModel();
		pointOfService.setWarehouses(warehouses);
		pointOfService.setBaseStore(baseStore);

		product = new ProductModel();
		product.setCode("TEST");

		stockLevel = new StockLevelModel();
		stockLevel.setAvailable(TEN.intValue());
		stockLevel.setProduct(product);
		stockLevel.setWarehouse(warehouse);
		stockLevels = Arrays.asList(stockLevel);
	}


	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_GetStockLevelForProductAndPointOfService_NullProduct()
	{
		warehousingCommerceStockService.getStockLevelForProductAndPointOfService(null, pointOfService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_GetStockLevelForProductAndPointOfService_NullPos()
	{
		warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, null);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NoWarehouses()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(Collections.emptyList());

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(ZERO, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NullStock()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(null);

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(ZERO, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NoStock()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(Collections.emptyList());

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(ZERO, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_NullAvailability()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(stockLevels);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels)).thenReturn(null);

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(null, value);
	}

	@Test
	public void shouldGetStockLevelForProductAndPointOfService_Valid()
	{
		when(pickupWarehouseSelectionStrategy.getWarehouses(pointOfService)).thenReturn(warehouses);
		when(stockService.getStockLevels(product, warehouses)).thenReturn(stockLevels);
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(stockLevels)).thenReturn(
				Long.valueOf(stockLevel.getAvailable()));

		final Long value = warehousingCommerceStockService.getStockLevelForProductAndPointOfService(product, pointOfService);

		assertEquals(TEN, value);
	}
}
