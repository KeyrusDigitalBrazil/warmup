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
package de.hybris.platform.sap.sapproductavailability.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.stock.strategies.CommerceAvailabilityCalculationStrategy;
import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;


@UnitTest
public class DefaultSapProductAvailabilityServiceTest
{

	private DefaultSapProductAvailabilityService service;

	@Mock
	private ProductModel product;

	@Mock
	private BaseStoreModel baseStore;

	@Mock
	private StockService stockService;

	@Mock
	private WarehouseSelectionStrategy warehouseSelectionStrategy;

	@Mock
	private ModuleConfigurationAccess moduleConfigurationAccess;
	
	@Mock
	private CommerceAvailabilityCalculationStrategy commerceStockLevelCalculationStrategy;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		service = new DefaultSapProductAvailabilityService();
		service.setStockService(stockService);
		service.setWarehouseSelectionStrategy(warehouseSelectionStrategy);
		service.setModuleConfigurationAccess(moduleConfigurationAccess);
		service.setCommerceStockLevelCalculationStrategy(commerceStockLevelCalculationStrategy);
	}

	@Test
	public void testStockConversion()
	{
		final List<WarehouseModel> warehouses = new ArrayList<>(0);
		Mockito.when(product.getSapBaseUnitConversion()).thenReturn(new Double("0.1"));
		Mockito.when(moduleConfigurationAccess.getProperty("sapproductavailability_atpActive")).thenReturn(Boolean.FALSE);
		Mockito.when(warehouseSelectionStrategy.getWarehousesForBaseStore(baseStore)).thenReturn(warehouses);
		Mockito.when(commerceStockLevelCalculationStrategy.calculateAvailability(Collections.emptyList())).thenReturn(60L);
		final Long amount = service.getStockLevelForProductAndBaseStore(product, baseStore);

		assertEquals(600, amount.longValue());
	}

	@Test
	public void testStockConversionRounding()
	{
		final List<WarehouseModel> warehouses = new ArrayList<>(0);
		Mockito.when(product.getSapBaseUnitConversion()).thenReturn(new Double("0.31"));
		Mockito.when(moduleConfigurationAccess.getProperty("sapproductavailability_atpActive")).thenReturn(Boolean.FALSE);
		Mockito.when(warehouseSelectionStrategy.getWarehousesForBaseStore(baseStore)).thenReturn(warehouses);
		Mockito.when(commerceStockLevelCalculationStrategy.calculateAvailability(Collections.emptyList())).thenReturn(60L);
		final Long amount = service.getStockLevelForProductAndBaseStore(product, baseStore);

		assertEquals(193, amount.longValue());
	}

	@Test
	public void testStockConversionOff()
	{
		final List<WarehouseModel> warehouses = new ArrayList<>(0);
		Mockito.when(product.getSapBaseUnitConversion()).thenReturn(null);
		Mockito.when(moduleConfigurationAccess.getProperty("sapproductavailability_atpActive")).thenReturn(Boolean.FALSE);
		Mockito.when(warehouseSelectionStrategy.getWarehousesForBaseStore(baseStore)).thenReturn(warehouses);
		Mockito.when(commerceStockLevelCalculationStrategy.calculateAvailability(Collections.emptyList())).thenReturn(60L);
		final Long amount = service.getStockLevelForProductAndBaseStore(product, baseStore);

		assertEquals(60, amount.longValue());
	}

	@Test
	public void testStockConversionZero()
	{
		final List<WarehouseModel> warehouses = new ArrayList<>(0);
		Mockito.when(product.getSapBaseUnitConversion()).thenReturn(new Double(0));
		Mockito.when(moduleConfigurationAccess.getProperty("sapproductavailability_atpActive")).thenReturn(Boolean.FALSE);
		Mockito.when(warehouseSelectionStrategy.getWarehousesForBaseStore(baseStore)).thenReturn(warehouses);
		Mockito.when(commerceStockLevelCalculationStrategy.calculateAvailability(Collections.emptyList())).thenReturn(60L);
		final Long amount = service.getStockLevelForProductAndBaseStore(product, baseStore);

		assertEquals(60, amount.longValue());
	}

}
