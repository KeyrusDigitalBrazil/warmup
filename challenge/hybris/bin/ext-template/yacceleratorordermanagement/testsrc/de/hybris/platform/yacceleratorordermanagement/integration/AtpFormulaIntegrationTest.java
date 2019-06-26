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
package de.hybris.platform.yacceleratorordermanagement.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.atp.services.impl.WarehousingCommerceStockService;
import de.hybris.platform.warehousing.cancellation.ConsignmentCancellationService;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.model.IncreaseEventModel;
import de.hybris.platform.warehousing.model.ShrinkageEventModel;
import de.hybris.platform.warehousing.model.WastageEventModel;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;
import de.hybris.platform.warehousing.util.models.AtpFormulas;
import de.hybris.platform.warehousing.util.models.Orders;
import de.hybris.platform.yacceleratorordermanagement.integration.util.CancellationUtil;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;


@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class AtpFormulaIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	private static final int MONTREAL_STOCK_QTY = 50;
	private static final int BOSTON_STOCK_QTY = 25;
	private static final int MONTREAL_EXTERNAL_STOCK_QTY = 100;
	private static final int GRIFFINTOWN_STOCK_QTY = 3;
	private static final int NEW_STOCK_QUANTITY = 500;

	private static final Long CANCELLED_QTY = Long.valueOf(1);
	private static final Long SHRINKED_QTY = Long.valueOf(2);
	private static final Long WASTED_QTY = Long.valueOf(3);
	private static final Long INCREASED_QTY = Long.valueOf(4);

	private Map<AbstractOrderEntryModel, Long> cancellationEntryInfo;

	@Resource
	protected Orders orders;
	@Resource
	protected SourcingService sourcingService;
	@Resource
	protected AllocationService allocationService;
	@Resource
	protected ConsignmentCancellationService consignmentCancellationService;
	@Resource
	protected InventoryEventService inventoryEventService;
	@Resource
	protected DefaultWarehouseStockService warehouseStockService;
	@Resource
	protected WarehousingCommerceStockService commerceStockService;
	@Resource
	protected CancellationUtil cancellationUtil;
	@Resource
	protected AtpFormulas atpFormulas;
	protected OrderModel order;
	protected Long initialOrderQty;

	private static final String CODE_RETURNED_BIN = "returned_bin";

	@Before
	public void setUp() throws InterruptedException, OrderCancelException
	{
		cancellationUtil.setOrderCancelConfig();

		stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), MONTREAL_STOCK_QTY, null);
		stockLevels.NewStockLevel(products.Camera(), warehouses.Boston(), BOSTON_STOCK_QTY, null);
		stockLevels.NewStockLevel(products.Camera(), warehouses.Griffintown(), GRIFFINTOWN_STOCK_QTY, null);

		order = sourcingUtil.createCameraShippedOrder();
		initialOrderQty = order.getEntries().iterator().next().getQuantity();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream().findFirst().get();

		cancellationEntryInfo = new HashMap<AbstractOrderEntryModel, Long>();
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), CANCELLED_QTY);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		final StockLevelModel stockLevel = warehouses.Montreal().getStockLevels().iterator().next();

		IncreaseEventModel increaseEventModel = new IncreaseEventModel();
		increaseEventModel.setQuantity(INCREASED_QTY);
		increaseEventModel.setStockLevel(stockLevel);
		inventoryEventService.createIncreaseEvent(increaseEventModel);

		sourcingUtil.refreshOrder(order);

		ShrinkageEventModel shrinkageEventModel = new ShrinkageEventModel();
		shrinkageEventModel.setQuantity(SHRINKED_QTY);
		shrinkageEventModel.setStockLevel(stockLevel);
		inventoryEventService.createShrinkageEvent(shrinkageEventModel);

		WastageEventModel wastageEventModel = new WastageEventModel();
		wastageEventModel.setQuantity(WASTED_QTY);
		wastageEventModel.setStockLevel(stockLevel);
		inventoryEventService.createWastageEvent(wastageEventModel);
	}

	@After
	public void cleanUp()
	{
		cleanUpData();
	}

	@Test
	public void getDefault_Atp()
	{
		updateFormula(atpFormulas.Hybris());
		checkGlobalAndLocalAtp(
				Long.valueOf(MONTREAL_STOCK_QTY - getAllocatedQuantity()) + getCancelQuantity() - SHRINKED_QTY - WASTED_QTY
						+ INCREASED_QTY);
	}

	@Test
	public void getAtp_Null_Formula()
	{
		updateFormula(null);

		final Long globalAtp = commerceStockService
				.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica());
		final Long posAtp = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown());
		final Long warehouseAtp = warehouseStockService
				.getStockLevelForProductCodeAndWarehouse(products.Camera().getCode(), warehouses.Montreal());

		Long zero = 0L;

		assertEquals(zero, globalAtp);
		assertEquals(zero, posAtp);
		assertEquals(zero, warehouseAtp);
	}

	/**
	 * Availability is not present in the formula
	 */
   @Test
	public void getAtp_Empty_Formula()
	{
		updateFormula(atpFormulas.customFormula(false, false, false, false, false, false, false, false, false));
		checkGlobalAndLocalAtp(Long.valueOf(0L));
	}

	@Test
	public void getAtp_Available_Only()
	{
		updateFormula(atpFormulas.customFormula(true, false, false, false, false, false, false, false, false));
		checkGlobalAndLocalAtp(Long.valueOf(MONTREAL_STOCK_QTY));
	}

	@Test
	public void getAtp_Available_Allocated()
	{
		updateFormula(atpFormulas.customFormula(true, true, false, false, false, false, false, false, false));
		checkGlobalAndLocalAtp(Long.valueOf(MONTREAL_STOCK_QTY - getAllocatedQuantity()));
	}

	@Test
	public void getAtp_Available_Allocated_Cancelled()
	{
		updateFormula(atpFormulas.customFormula(true, true, true, false, false, false, false, false, false));
		checkGlobalAndLocalAtp(Long.valueOf(MONTREAL_STOCK_QTY - getAllocatedQuantity() + getCancelQuantity()));
	}

	@Test
	public void getAtp_Available_Allocated_Cancelled_Shrinked()
	{
		updateFormula(atpFormulas.customFormula(true, true, true, false, false, true, false, false, false));
		checkGlobalAndLocalAtp(Long.valueOf(MONTREAL_STOCK_QTY - getAllocatedQuantity() + getCancelQuantity() - SHRINKED_QTY));
	}

	@Test
	public void getAtp_Available_Allocated_Cancelled_Shrinked_Wasted()
	{
		updateFormula(atpFormulas.customFormula(true, true, true, false, false, true, true, false, false));
		checkGlobalAndLocalAtp(
				Long.valueOf(MONTREAL_STOCK_QTY - getAllocatedQuantity() + getCancelQuantity() - SHRINKED_QTY - WASTED_QTY));
	}

	@Test
	public void getAtp_Available_Allocated_Cancelled_Shrinked_Wasted_Increase()
	{
		updateFormula(atpFormulas.customFormula(true, true, true, true, false, true, true, false, false));
		checkGlobalAndLocalAtp(Long.valueOf(
				MONTREAL_STOCK_QTY - getAllocatedQuantity() + getCancelQuantity() - SHRINKED_QTY - WASTED_QTY + INCREASED_QTY));
	}

	@Test
	public void getAtp_Shrinked_Wasted()
	{
		final AtpFormulaModel atpFormula = atpFormulas.customFormula(true, false, false, false, false, true, true, false, false);
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(MONTREAL_STOCK_QTY + BOSTON_STOCK_QTY - SHRINKED_QTY - WASTED_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		atpFormula.setShrinkage(Boolean.FALSE);
		atpFormula.setWastage(Boolean.FALSE);
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(MONTREAL_STOCK_QTY + BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));


	}

	@Test
	public void getAtp_WithReturnedBinCode_WithoutReturned()
	{
		final StockLevelModel stockLevel = warehouses.Montreal().getStockLevels().iterator().next();
		stockLevel.setBin(CODE_RETURNED_BIN);
		modelService.save(stockLevel);
		stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), NEW_STOCK_QUANTITY, null);
		updateFormula(atpFormulas.customFormula(true, false, false, false, false, false, false, false, false));
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + GRIFFINTOWN_STOCK_QTY)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals((Long.valueOf(BOSTON_STOCK_QTY + NEW_STOCK_QUANTITY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
	}

	@Test
	public void getAtp_WithReturned()
	{
		final StockLevelModel stockLevel = warehouses.Montreal().getStockLevels().iterator().next();
		stockLevel.setBin(CODE_RETURNED_BIN);
		modelService.save(stockLevel);
		stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), NEW_STOCK_QUANTITY, null);

		final AtpFormulaModel atpFormula = atpFormulas.customFormula(true, false, false, false, false, false, false, true, false);
		//Checking ATP with Returned
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + MONTREAL_STOCK_QTY + GRIFFINTOWN_STOCK_QTY)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + MONTREAL_STOCK_QTY + BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//Checking ATP without returned
		atpFormula.setReturned(null);
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + GRIFFINTOWN_STOCK_QTY)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		atpFormula.setReturned(Boolean.FALSE);
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + GRIFFINTOWN_STOCK_QTY)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals((Long.valueOf(NEW_STOCK_QUANTITY + BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
	}

	@Test
	public void getAtp_WithExternal()
	{
		//setup external warehouse
		final WarehouseModel externalWarehouseMontreal = warehouses.Montreal_External();
		externalWarehouseMontreal.setPointsOfService(Arrays.asList(pointsOfService.Montreal_External()));
		externalWarehouseMontreal.setBaseStores(Arrays.asList(baseStores.NorthAmerica()));
		externalWarehouseMontreal.setExternal(true);
		modelService.save(externalWarehouseMontreal);
		//setup external stock Level
		final StockLevelModel externalStockLevelMontreal = stockLevels
				.Camera(externalWarehouseMontreal, MONTREAL_EXTERNAL_STOCK_QTY);
		modelService.save(externalStockLevelMontreal);
		//setup formula with only external
		final AtpFormulaModel atpFormula = atpFormulas.customFormula(true, false, false, false, false, false, false, false, true);
		updateFormula(atpFormula);
		//check local and global atp
		assertEquals((Long.valueOf(MONTREAL_EXTERNAL_STOCK_QTY)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));
		assertEquals((Long.valueOf(MONTREAL_EXTERNAL_STOCK_QTY+MONTREAL_STOCK_QTY+BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//Checking ATP with External-Null
		atpFormula.setExternal(null);
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(0)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));
		assertEquals((Long.valueOf(MONTREAL_STOCK_QTY+BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//Checking ATP with External-False
		atpFormula.setExternal(Boolean.FALSE);
		updateFormula(atpFormula);
		assertEquals((Long.valueOf(0)), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));
		assertEquals((Long.valueOf(MONTREAL_STOCK_QTY+BOSTON_STOCK_QTY)),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

	}

	protected void checkGlobalAndLocalAtp(final Long expectedLocal)
	{
		final Long globalAtp = commerceStockService
				.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica());
		final Long posAtp = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown());
		final Long warehouseAtp = warehouseStockService
				.getStockLevelForProductCodeAndWarehouse(products.Camera().getCode(), warehouses.Montreal());

		Long expectedGlobal = 0L;
		if (expectedLocal != 0L)
			expectedGlobal = expectedLocal + Long.valueOf(BOSTON_STOCK_QTY);

		Long expectedPosAtp = expectedLocal;
		if (baseStores.NorthAmerica().getDefaultAtpFormula().getFormulaString().contains("Availability"))
		{
			expectedPosAtp += Long.valueOf(GRIFFINTOWN_STOCK_QTY);
		}

		assertEquals(expectedGlobal, globalAtp);
		assertEquals(expectedPosAtp, posAtp);
		assertEquals(expectedLocal, warehouseAtp);
	}

	protected void updateFormula(final AtpFormulaModel atpFormulaModel)
	{
		BaseStoreModel baseStoreModel = baseStores.NorthAmerica();
		baseStoreModel.setDefaultAtpFormula(atpFormulaModel);
		modelService.save(baseStoreModel);
	}

	/**
	 * Because of the cancellation, the entire order is first allocated and sourced again for its original quantity minus the quantity cancelled.
	 *
	 * @return the allocated quantity
	 */
	protected Long getAllocatedQuantity()
	{
		return (initialOrderQty * 2) - CANCELLED_QTY;
	}

	/**
	 * When cancelled, even partially, an order is cancelled integrally and if there is some quantity left, it is source again.
	 *
	 * @return the cancelled quantity
	 */
	protected Long getCancelQuantity()
	{
		return initialOrderQty;
	}
}
