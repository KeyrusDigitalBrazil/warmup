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
package de.hybris.platform.warehousing.atp.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousingAvailabilityCalculationStrategyTest
{
	private static final String RETURN_BIN = "ReturnBin";
	private static final String STOCK_LEVELS = "stockLevels";

	@Mock
	private StockLevelModel externalStock, returnStock, availableStock;
	@Mock
	private WarehouseModel externalWarehouse, internalWarehouse;
	@Mock
	private BaseStoreModel baseStore;
	@Mock
	private AtpFormulaModel atpFormula;
	@Mock
	private RestockConfigService restockConfigService;
	@Mock
	private BaseStoreService baseStoreService;

	@InjectMocks
	private WarehousingAvailabilityCalculationStrategy warehousingAvailabilityCalculationStrategy;

	@Before
	public void setUp() throws Exception
	{
		when(externalStock.getWarehouse()).thenReturn(externalWarehouse);
		when(returnStock.getWarehouse()).thenReturn(internalWarehouse);
		when(availableStock.getWarehouse()).thenReturn(internalWarehouse);

		when(externalWarehouse.isExternal()).thenReturn(true);
		when(internalWarehouse.isExternal()).thenReturn(false);

		when(restockConfigService.getReturnedBinCode()).thenReturn(RETURN_BIN);
		when(returnStock.getBin()).thenReturn(RETURN_BIN);

		when(externalStock.getAvailable()).thenReturn(3);
		when(returnStock.getAvailable()).thenReturn(1);
		when(availableStock.getAvailable()).thenReturn(100);

		when(atpFormula.getAvailability()).thenReturn(true);
		when(atpFormula.getExternal()).thenReturn(true);
		when(atpFormula.getReturned()).thenReturn(true);
	}

	@Test
	public void filterStocksWithExternalAndReturn()
	{
		//When
		final Map<String, Object> params = warehousingAvailabilityCalculationStrategy
				.filterStocks(Arrays.asList(externalStock, returnStock, availableStock), atpFormula);
		//Then
		assertEquals(1, params.entrySet().size());
		assertEquals(3, ((Collection<StockLevelModel>) params.get(STOCK_LEVELS)).size());
	}

	@Test
	public void filterStocksWithExternalNoReturn()
	{
		//Given
		when(atpFormula.getReturned()).thenReturn(false);
		//When
		final Map<String, Object> params = warehousingAvailabilityCalculationStrategy
				.filterStocks(Arrays.asList(externalStock, returnStock, availableStock), atpFormula);
		//Then
		assertEquals(1, params.entrySet().size());
		assertEquals(2, ((Collection<StockLevelModel>) params.get(STOCK_LEVELS)).size());
	}

	@Test
	public void filterStocksWithNoExternalAndReturn()
	{
		//Given
		when(atpFormula.getExternal()).thenReturn(false);
		//When
		final Map<String, Object> params = warehousingAvailabilityCalculationStrategy
				.filterStocks(Arrays.asList(externalStock, returnStock, availableStock), atpFormula);
		//Then
		assertEquals(1, params.entrySet().size());
		assertEquals(2, ((Collection<StockLevelModel>) params.get(STOCK_LEVELS)).size());
	}

	@Test
	public void filterStocksWithNoAvailable()
	{
		//Given
		when(atpFormula.getAvailability()).thenReturn(false);
		when(atpFormula.getExternal()).thenReturn(false);
		when(atpFormula.getReturned()).thenReturn(false);
		//When
		final Map<String, Object> params = warehousingAvailabilityCalculationStrategy
				.filterStocks(Arrays.asList(externalStock, returnStock, availableStock), atpFormula);
		//Then
		assertEquals(1, params.entrySet().size());
		assertEquals(1, ((Collection<StockLevelModel>) params.get(STOCK_LEVELS)).size());
	}

	@Test
	public void calculateAvailabilityForceInStock()
	{
		//Given
		when(availableStock.getInStockStatus()).thenReturn(InStockStatus.FORCEINSTOCK);
		//When
		final Long result = warehousingAvailabilityCalculationStrategy
				.calculateAvailability(Arrays.asList(externalStock, returnStock, availableStock));
		//Then
		assertNull(result);
	}

	@Test
	public void calculateAvailabilityNoStocks()
	{
		//When
		final Long result = warehousingAvailabilityCalculationStrategy.calculateAvailability(Collections.EMPTY_LIST);
		//Then
		assertEquals(Long.valueOf(0), result);
	}

	@Test
	public void calculateAvailabilityNoFormula()
	{
		//Given
		final Collection<StockLevelModel> stocks = Arrays.asList(externalStock, returnStock, availableStock);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		//When
		final Long result = warehousingAvailabilityCalculationStrategy.calculateAvailability(stocks);
		//Then
		assertEquals(Long.valueOf(0), result);
	}

	@Test
	public void getDefaultAtpFormulaWithNullBaseStoreInSession()
	{
		//Given
		final Collection<StockLevelModel> stocks = Arrays.asList(externalStock, returnStock, availableStock);

		when(baseStoreService.getCurrentBaseStore()).thenReturn(null);
		when(internalWarehouse.getBaseStores()).thenReturn(Collections.singletonList(baseStore));
		when(externalWarehouse.getBaseStores()).thenReturn(Collections.singletonList(baseStore));
		when(baseStore.getDefaultAtpFormula()).thenReturn(atpFormula);

		//When
		final AtpFormulaModel result = warehousingAvailabilityCalculationStrategy.getDefaultAtpFormula(stocks);
		//Then
		assertEquals(atpFormula,result);
	}
}
