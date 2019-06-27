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

package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehousing.model.RestockConfigModel;
import de.hybris.platform.warehousing.returns.RestockException;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;
import de.hybris.platform.warehousing.returns.strategy.RestockWarehouseSelectionStrategy;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class InventoryUpdateActionTest
{
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String RETURN_BIN = "RETURN_BIN";

	@InjectMocks
	private InventoryUpdateAction action;

	@Mock
	private ReturnProcessModel returnProcessModel;
	@Mock
	private ModelService modelService;
	@Mock
	private TimeService timeService;
	@Mock
	private RestockConfigService restockConfigService;
	@Mock
	private WarehouseStockService warehouseStockService;
	@Mock
	private RestockWarehouseSelectionStrategy restockWarehouseSelectionStrategy;
	@Mock
	private ReturnRequestModel onlineReturn;
	@Mock
	private ReturnEntryModel returnEntryModel;
	@Mock
	private OrderEntryModel orderEntryModel;
	@Mock
	private ProductModel productModel;
	@Mock
	private WarehouseModel returnWarehouse;
	@Mock
	private RestockConfigModel restockConfigModel;


	@Before
	public void setup() throws RestockException
	{
		when(returnProcessModel.getReturnRequest()).thenReturn(onlineReturn);
		when(onlineReturn.getReturnEntries()).thenReturn(Collections.singletonList(returnEntryModel));
		when(returnEntryModel.getOrderEntry()).thenReturn(orderEntryModel);
		when(orderEntryModel.getProduct()).thenReturn(productModel);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(returnEntryModel.getReceivedQuantity()).thenReturn(10L);
		when(timeService.getCurrentTime()).thenReturn(new Date());
		when(restockConfigService.getRestockConfig()).thenReturn(restockConfigModel);
		when(restockConfigModel.getReturnedBinCode()).thenReturn(RETURN_BIN);
		when(restockConfigModel.getDelayDaysBeforeRestock()).thenReturn(1);
		when(restockConfigModel.getIsUpdateStockAfterReturn()).thenReturn(Boolean.TRUE);
	}

	@Test
	public void shouldUpdateInventoryWarehouseSet() throws Exception
	{
		//Given
		when(onlineReturn.getReturnWarehouse()).thenReturn(returnWarehouse);

		//When
		action.execute(returnProcessModel);

		//Then
		verify(restockWarehouseSelectionStrategy, never()).performStrategy(onlineReturn);
		verify(warehouseStockService).createStockLevel(anyString(), any(WarehouseModel.class), anyInt(), any(), any(), anyString());
	}

	@Test
	public void shouldUpdateInventoryNoWarehouseSetWarehouseAvl() throws Exception
	{
		//Given
		when(onlineReturn.getReturnWarehouse()).thenReturn(null);
		when(restockWarehouseSelectionStrategy.performStrategy(onlineReturn)).thenReturn(returnWarehouse);

		//When
		action.execute(returnProcessModel);

		//Then
		verify(restockWarehouseSelectionStrategy, times(1)).performStrategy(onlineReturn);
		verify(warehouseStockService).createStockLevel(anyString(), any(WarehouseModel.class), anyInt(), any(), any(), anyString());
	}

	@Test
	public void shouldUpdateInventoryNoWarehouseSetNoWarehouseAvl() throws Exception
	{
		//Given
		when(onlineReturn.getReturnWarehouse()).thenReturn(null);

		//When
		action.execute(returnProcessModel);

		//Then
		verify(restockWarehouseSelectionStrategy, times(1)).performStrategy(onlineReturn);
		verify(warehouseStockService, never())
				.createStockLevel(anyString(), any(WarehouseModel.class), anyInt(), any(), any(), anyString());
	}

}
