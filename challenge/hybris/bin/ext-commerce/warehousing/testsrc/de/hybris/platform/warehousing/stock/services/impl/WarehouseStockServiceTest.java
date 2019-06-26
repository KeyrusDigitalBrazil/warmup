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
package de.hybris.platform.warehousing.stock.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.warehousing.atp.strategy.impl.WarehousingAvailabilityCalculationStrategy;
import de.hybris.platform.warehousing.stock.daos.WarehouseStockLevelDao;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehouseStockServiceTest
{
	@Spy
	@InjectMocks
	private final WarehouseStockService warehouseStockService = new DefaultWarehouseStockService();

	@Mock
	private WarehouseModel warehouse;
	@Mock
	private StockLevelDao stockLevelDao;
	@Mock
	private StockLevelModel stockLevelModel;
	@Mock
	private WarehousingAvailabilityCalculationStrategy commerceAvailabilityCalculationStrategy;
	@Mock
	private WarehouseStockLevelDao warehouseStockLevelDao;
	@Mock
	private ModelService modelService;

	private static final String PRODUCT_CODE = "productCode";
	private static final String WAREHOUSE_CODE = "warehouseCode";
	private static final Long AVAILABILITY = 8L;
	private static final int INITIALE_QTY = 5;
	private static final InStockStatus STOCK_STATUS = InStockStatus.NOTSPECIFIED;
	private static final Date RELEASE_DATE = new Date();
	private static final String BIN_CODE = "binCode";
	private static List<StockLevelModel> stockLevelModels;

	@Before
	public void setUp() throws Exception
	{
		stockLevelModels = new ArrayList<>();
		stockLevelModels.add(stockLevelModel);
		when(stockLevelModel.getAvailable()).thenReturn(AVAILABILITY.intValue());
		when(stockLevelDao.findStockLevels(PRODUCT_CODE, Collections.singleton(warehouse))).thenReturn(Collections.singleton(stockLevelModel));
		when(commerceAvailabilityCalculationStrategy.calculateAvailability(any())).thenReturn(AVAILABILITY);
		when(warehouseStockLevelDao.getStockLevels(PRODUCT_CODE, WAREHOUSE_CODE, null, null)).thenReturn(stockLevelModels);

		doNothing().when(modelService).save(any());
		when(modelService.create(StockLevelModel.class)).thenReturn(new StockLevelModel());
	}

	@Test
	public void shouldGetStockLevels()
	{
		//when
		StockLevelModel result = warehouseStockService.getUniqueStockLevel(PRODUCT_CODE, WAREHOUSE_CODE, null, null);

		//then
			assertNotNull(result);
	}

	@Test
	public void createStockLevel()
	{
		//when
		StockLevelModel result = warehouseStockService.createStockLevel(PRODUCT_CODE, warehouse, INITIALE_QTY, STOCK_STATUS, RELEASE_DATE, BIN_CODE);

		//then
		assertEquals(PRODUCT_CODE, result.getProductCode());
		assertEquals(warehouse, result.getWarehouse());
		assertEquals(INITIALE_QTY, result.getAvailable());
		assertEquals(STOCK_STATUS, result.getInStockStatus());
		assertEquals(RELEASE_DATE, result.getReleaseDate());
		assertEquals(BIN_CODE, result.getBin());
	}

	@Test
	public void shouldGetStockLevel_WithStock()
	{
		//when
		Long result = warehouseStockService.getStockLevelForProductCodeAndWarehouse(PRODUCT_CODE, warehouse);

		//then
		assertEquals(Long.valueOf(8), result);
	}

	@Test
	public void shouldGetStockLevel_NoStock()
	{
		when(stockLevelDao.findStockLevels(PRODUCT_CODE, Collections.singleton(warehouse))).thenReturn(new ArrayList<StockLevelModel>());

		//when
		Long result = warehouseStockService.getStockLevelForProductCodeAndWarehouse(PRODUCT_CODE, warehouse);

		//then
		assertEquals(Long.valueOf(0), result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowException_getStockLevelProductCodeNull()
	{
		warehouseStockService.getStockLevelForProductCodeAndWarehouse(null, warehouse);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowException_getStockLevelWarehouseNull()
	{
		warehouseStockService.getStockLevelForProductCodeAndWarehouse(PRODUCT_CODE, null);
	}
}
