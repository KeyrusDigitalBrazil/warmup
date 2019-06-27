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
package de.hybris.platform.warehousing.allocation.decline.action;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.warehousing.allocation.decline.action.impl.DefaultOutOfStockDeclineStrategy;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOutOfStockDeclineStrategyTest
{
	@Mock
	private ConsignmentEntryModel consignmentEntryModel;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private OrderEntryModel orderEntryModel;
	@Mock
	private ProductModel productModel;
	@Mock
	private WarehouseModel warehouseModel;
	@Mock
	private StockLevelModel stockLevelModel;
	@Mock
	private DeclineEntry manualDeclineEntry;
	@Mock
	private DeclineEntry autoDeclineEntry;
	@Mock
	private InventoryEventService inventoryEventService;
	@Mock
	private DefaultWarehouseStockService warehouseStockService;
	@Mock
	private StockService stockService;
	@Mock
	private AdvancedShippingNoticeEntryModel asnEntryModel;
	@Mock
	private AdvancedShippingNoticeModel asnModel;
	@Mock
	private StockLevelModel stockLevelModel2;
	@Mock
	private AdvancedShippingNoticeEntryModel asnEntryModel1;
	@Mock
	private AdvancedShippingNoticeModel asnModel1;
	@Mock
	private StockLevelModel stockLevelModel3;


	@InjectMocks
	private DefaultOutOfStockDeclineStrategy defaultOutOfStockDeclineStrategy;
	final private Set<StockLevelModel> stockLevelModelList = new HashSet<>();

	@Before
	public void setup()
	{
		when(manualDeclineEntry.getConsignmentEntry()).thenReturn(consignmentEntryModel);
		when(autoDeclineEntry.getConsignmentEntry()).thenReturn(consignmentEntryModel);
		when(consignmentEntryModel.getOrderEntry()).thenReturn(orderEntryModel);
		when(orderEntryModel.getProduct()).thenReturn(productModel);
		when(consignmentEntryModel.getConsignment()).thenReturn(consignmentModel);
		when(consignmentModel.getWarehouse()).thenReturn(warehouseModel);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(productModel.getCode(), warehouseModel)).thenReturn(1L);
		when(stockService.getStockLevel(productModel, warehouseModel)).thenReturn(stockLevelModel);
	}


	@Test
	public void shouldExecute()
	{
		// when
		defaultOutOfStockDeclineStrategy.execute(manualDeclineEntry);

		// Then
		verify(warehouseStockService,times(1)).getStockLevelForProductCodeAndWarehouse(productModel.getCode(),warehouseModel);
		verify(inventoryEventService,times(1)).createShrinkageEvent(any());
	}

	@Test
	public void shouldExecute_QuantityAlready0()
	{
		// when
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(productModel.getCode(), warehouseModel)).thenReturn(0L);
		defaultOutOfStockDeclineStrategy.execute(manualDeclineEntry);

		// Then
		verify(warehouseStockService,times(1)).getStockLevelForProductCodeAndWarehouse(productModel.getCode(),warehouseModel);
		verify(inventoryEventService,times(0)).createShrinkageEvent(any());
	}


	@Test
	public void shouldExecuteEntries()
	{
		// when
		defaultOutOfStockDeclineStrategy.execute(Arrays.asList(manualDeclineEntry,autoDeclineEntry));

		// Then
		verify(warehouseStockService,times(2)).getStockLevelForProductCodeAndWarehouse(productModel.getCode(),warehouseModel);
		verify(inventoryEventService,times(2)).createShrinkageEvent(any());
	}

	@Test
	public void shouldExcludeAsn_WithStatusCreated()
	{
		//given
		stockLevelModelList.addAll(Arrays.asList(stockLevelModel ,stockLevelModel2));

		//create Asn stock
		when(stockLevelModel2.getAsnEntry()).thenReturn(asnEntryModel);
		when(asnEntryModel.getAsn()).thenReturn(asnModel);
		when(asnModel.getStatus()).thenReturn(AsnStatus.CREATED);

		//add Stock value
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(productModel.getCode(), warehouseModel)).thenReturn(-3L);
		when(stockLevelModel.getAvailable()).thenReturn(3);
		when(stockLevelModel2.getAvailable()).thenReturn(-4);

		when(warehouseModel.getStockLevels()).thenReturn(stockLevelModelList);

		// when
		defaultOutOfStockDeclineStrategy.execute(autoDeclineEntry);

		// Then
		//assertEquals(3L, argument.getValue());
		verify(warehouseStockService,times(1)).getStockLevelForProductCodeAndWarehouse(productModel.getCode(),warehouseModel);
		verify(inventoryEventService,times(1)).createShrinkageEvent(any());
	}

	@Test
	public void shouldExcludeAsn_WithStatusReceivedAndCancelled()
	{
		//given
		stockLevelModelList.addAll(Arrays.asList(stockLevelModel, stockLevelModel2));

		//create Asn stock received
		when(stockLevelModel2.getAsnEntry()).thenReturn(asnEntryModel);
		when(asnEntryModel.getAsn()).thenReturn(asnModel);
		when(asnModel.getStatus()).thenReturn(AsnStatus.RECEIVED);

		//create Asn stock received
		when(stockLevelModel3.getAsnEntry()).thenReturn(asnEntryModel);
		when(asnEntryModel1.getAsn()).thenReturn(asnModel1);
		when(asnModel1.getStatus()).thenReturn(AsnStatus.CANCELLED);

		//add Stock value
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(productModel.getCode(), warehouseModel)).thenReturn(-3L);
		when(stockLevelModel.getAvailable()).thenReturn(3);
		when(stockLevelModel2.getAvailable()).thenReturn(-4);
		when(stockLevelModel2.getAvailable()).thenReturn(-4);

		when(warehouseModel.getStockLevels()).thenReturn(stockLevelModelList);

		// when
		defaultOutOfStockDeclineStrategy.execute(autoDeclineEntry);

		// Then
		//assertEquals(3L, argument.getValue());
		verify(warehouseStockService, times(1)).getStockLevelForProductCodeAndWarehouse(productModel.getCode(), warehouseModel);
		verify(inventoryEventService, times(0)).createShrinkageEvent(any());
	}
}
