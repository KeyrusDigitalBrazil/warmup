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
package de.hybris.platform.warehousing.atp;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.atp.formula.services.AtpFormulaService;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.model.IncreaseEventModel;
import de.hybris.platform.warehousing.returns.service.RestockConfigService;
import de.hybris.platform.warehousing.stock.services.impl.DefaultWarehouseStockService;
import de.hybris.platform.warehousing.util.BaseWarehousingIntegrationTest;
import de.hybris.platform.warehousing.util.models.AllocationEvents;
import de.hybris.platform.warehousing.util.models.BaseStores;
import de.hybris.platform.warehousing.util.models.CancellationEvents;
import de.hybris.platform.warehousing.util.models.PointsOfService;
import de.hybris.platform.warehousing.util.models.Products;
import de.hybris.platform.warehousing.util.models.StockLevels;
import de.hybris.platform.warehousing.util.models.Warehouses;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class ATPBasicIntegrationTest extends BaseWarehousingIntegrationTest
{
	private static final Long MONTREAL_CAMERA_QTY = Long.valueOf(50);
	private static final Long BOSTON_CAMERA_QTY = Long.valueOf(25);
	private static final Long ALLOCATED_CAMERA_QTY = Long.valueOf(10);
	private static final String ATP_FORMULA_CODE = "test formula";

	@Resource
	private CommerceStockService commerceStockService;

	@Resource
	private Products products;
	@Resource
	private BaseStores baseStores;
	@Resource
	private PointsOfService pointsOfService;
	@Resource
	private StockLevels stockLevels;
	@Resource
	private Warehouses warehouses;
	@Resource
	private AllocationEvents allocationEvents;
	@Resource
	private CancellationEvents cancellationEvents;
	@Resource
	private DefaultWarehouseStockService warehouseStockService;
	@Resource
	private StockService stockService;
	@Resource
	private ModelService modelService;
	@Resource
	private AtpFormulaService atpFormulaService;
	@Resource
	private InventoryEventService inventoryEventService;
	@Resource
	protected RestockConfigService restockConfigService;

	private BaseStoreModel northAmericaStore;
	private StockLevelModel montrealStockLevel;
	private StockLevelModel bostonStockLevel;

	private IncreaseEventModel increaseEventModel = new IncreaseEventModel();


	@Before
	public void setUp()
	{
		northAmericaStore = baseStores.NorthAmerica();

		final WarehouseModel warehouseMontreal = warehouses.Montreal();
		pointsOfService.Montreal_Downtown();
		final WarehouseModel warehouseBoston = warehouses.Boston();
		pointsOfService.Boston();

		montrealStockLevel = stockLevels.Camera(warehouseMontreal, MONTREAL_CAMERA_QTY.intValue());
		bostonStockLevel = stockLevels.Camera(warehouseBoston, BOSTON_CAMERA_QTY.intValue());
	}

	@Test
	public void createDefault_AtpFormula()
	{
		// When
		AtpFormulaModel atpFormulaModel = new AtpFormulaModel();
		atpFormulaModel.setCode(ATP_FORMULA_CODE);
		modelService.save(atpFormulaModel);
		modelService.refresh(atpFormulaModel);
		final Collection<AtpFormulaModel> atpFormulas = atpFormulaService.getAllAtpFormula();
		//then
		assertEquals(2, atpFormulas.size());
		assertTrue(atpFormulas.stream().anyMatch(atpFormula -> ATP_FORMULA_CODE.equals(atpFormula.getCode())));

	}

	@Test
	public void getDefault_Atp()
	{
		// When
		final Long globalAtp = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);
		final Long posAtp = commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(),
				pointsOfService.Montreal_Downtown());
		final Long warehouseAtp = warehouseStockService.getStockLevelForProductCodeAndWarehouse(
				products.Camera().getCode(), warehouses.Montreal());

		// Then
		assertGlobalAtp(globalAtp, Long.valueOf(0));
		assertEquals(MONTREAL_CAMERA_QTY, posAtp);
		assertEquals(MONTREAL_CAMERA_QTY, warehouseAtp);
	}

	@Test
	public void shouldGetATPForBaseStoreWithThreshold()
	{
		// Given
		final StockLevelModel stockLevel = warehouses.Montreal().getStockLevels().iterator().next();
		stockLevel.setReserved(-9);
		modelService.save(stockLevel);

		// When
		final Long globalAtp = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);

		// Then
		assertGlobalAtp(globalAtp, Long.valueOf(-9));
	}

	@Test
	public void shouldDecreaseStockLevel_AllocateCameraFrom1Pos1Warehouse()
	{
		// Given
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);

		// When
		final Long globalAtp = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);
		final Long localAtp = commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(),
				pointsOfService.Montreal_Downtown());

		// Then
		assertGlobalAtp(globalAtp, ALLOCATED_CAMERA_QTY);
		assertEquals(Long.valueOf(MONTREAL_CAMERA_QTY - ALLOCATED_CAMERA_QTY), localAtp);
	}

	@Test
	public void shouldDecreaseStockLevel_AllocateCameraFrom2Pos2Warehouse()
	{
		// Given
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);
		allocationEvents.Camera_ShippedFromBostonToMontrealNancyHome(ALLOCATED_CAMERA_QTY, bostonStockLevel);

		// When
		final Long globalAtp = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);
		final Long localMontrealAtp = commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(),
				pointsOfService.Montreal_Downtown());
		final Long localBostonAtp = commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(),
				pointsOfService.Boston());

		// Then
		assertGlobalAtp(globalAtp, ALLOCATED_CAMERA_QTY * 2);
		assertEquals(Long.valueOf(MONTREAL_CAMERA_QTY - ALLOCATED_CAMERA_QTY), localMontrealAtp);
		assertEquals(Long.valueOf(BOSTON_CAMERA_QTY - ALLOCATED_CAMERA_QTY), localBostonAtp);
	}

	@Test
	public void shouldGetDifferentATPForBaseStore_NewCancellationEvent()
	{
		// Given
		final Long globalAtp = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);
		final Long globalAtpAfterAllocation = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);

		assertGlobalAtp(globalAtpAfterAllocation, Long.valueOf(ALLOCATED_CAMERA_QTY));

		// When
		cancellationEvents.Camera_Cancellation(ALLOCATED_CAMERA_QTY, CancelReason.CUSTOMERREQUEST, montrealStockLevel);

		// Then
		assertGlobalAtp(globalAtp, Long.valueOf(0));
	}

	@Test
	public void shouldGetLessATP_StockLevelForcedOutOfStock()
	{
		// When
		final Long localBostonAtp_BeforeForcingOutOfStock = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());
		bostonStockLevel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		final Long localBostonAtp_AfterForcingOutOfStock = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());

		// Then
		assertEquals(Long.valueOf(25), localBostonAtp_BeforeForcingOutOfStock);
		assertEquals(Long.valueOf(0), localBostonAtp_AfterForcingOutOfStock);
		assertTrue(localBostonAtp_AfterForcingOutOfStock < localBostonAtp_BeforeForcingOutOfStock);
	}

	@Test
	public void shouldGetLessATP_StockLevelWithReturnedBin()
	{
		// When
		final Long localBostonAtp_BeforeReturnedBin = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());
		bostonStockLevel.setBin(restockConfigService.getReturnedBinCode());
		final Long localBostonAtp_AfterReturnedBin = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());

		// Then
		assertEquals(Long.valueOf(25), localBostonAtp_BeforeReturnedBin);
		assertEquals(Long.valueOf(0), localBostonAtp_AfterReturnedBin);
		assertTrue(localBostonAtp_BeforeReturnedBin > localBostonAtp_AfterReturnedBin);
	}

	@Test
	public void shouldGetNewATP_StockLevelForcedOutOfStock_UpdateStockLevel()
	{
		// When
		final Long localBostonAtp_BeforeForcingOutOfStock = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());
		bostonStockLevel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		stockService.updateActualStockLevel(products.Camera(), warehouses.Boston(), 5, "");
		final Long localBostonAtp_AfterForcingOutOfStock = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());

		// Then
		assertEquals(Long.valueOf(25), localBostonAtp_BeforeForcingOutOfStock);
		//force out of stock is based on the stock level, warehouse can have multi stock level for same product
		assertEquals(Long.valueOf(5), localBostonAtp_AfterForcingOutOfStock);
	}


	@Test
	public void shouldGetATPNull_StockLevelForcedInStock()
	{
		// When
		final Long localBostonAtp_BeforeForcingInOfStock = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());
		bostonStockLevel.setInStockStatus(InStockStatus.FORCEINSTOCK);
		final Long globalBostonAtp_AfterForcingInStock = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);
		final Long localBostonWarehouseAtp_AfterForcingInStock = warehouseStockService.getStockLevelForProductCodeAndWarehouse(
				products.Camera().getCode(), warehouses.Boston());
		final Long localBostonPosAtp_AfterForcingInStock = commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston());

		// Then
		assertEquals(Long.valueOf(25), localBostonAtp_BeforeForcingInOfStock);
		assertEquals(null, globalBostonAtp_AfterForcingInStock);
		assertEquals(null, localBostonWarehouseAtp_AfterForcingInStock);
		assertEquals(null, localBostonPosAtp_AfterForcingInStock);
	}

	@Test
	public void shouldGetMoreATP_StockLevelIncreaseEvent()
	{
		// Given
		final StockLevelModel stockLevel = warehouses.Montreal().getStockLevels().iterator().next();

		increaseEventModel.setQuantity(11);
		increaseEventModel.setStockLevel(stockLevel);
		inventoryEventService.createIncreaseEvent(increaseEventModel);

		// When
		final Long globalAtp = commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(),
				northAmericaStore);
		final Long localMontrealAtp = commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(),
				pointsOfService.Montreal_Downtown());

		// Then
		assertGlobalAtp(globalAtp, Long.valueOf(-11L));
		assertEquals(Long.valueOf(61), localMontrealAtp);
	}

	@Test
	public void shouldReturnInStockStatusWhenPositiveAtp()
	{
		//When
		final StockLevelStatus stockLevelStatus = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.INSTOCK,stockLevelStatus);
	}

	@Test
	public void shouldReturnInStockStatusWhenStockLevelIsForceInStock()
	{
		//Given
		montrealStockLevel.setInStockStatus(InStockStatus.FORCEINSTOCK);
		modelService.save(montrealStockLevel);

		//When
		final StockLevelStatus stockLevelStatus = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.INSTOCK,stockLevelStatus);
	}

	@Test
	public void shouldReturnInStockStatusWhenMixOfAllStockLevelStatus()
	{
		//Given
		montrealStockLevel.setInStockStatus(InStockStatus.FORCEINSTOCK);
		bostonStockLevel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		modelService.save(montrealStockLevel);
		modelService.save(bostonStockLevel);

		//When
		final StockLevelStatus result =commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.INSTOCK, result);
	}

	@Test
	public void shouldReturnInStockStatusWhenForceOutOfStockAndNoStockLevelStatus()
	{
		//Given
		montrealStockLevel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		bostonStockLevel.setInStockStatus(null);
		modelService.save(montrealStockLevel);
		modelService.save(bostonStockLevel);

		//When
		final StockLevelStatus result =commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.INSTOCK, result);
	}

	@Test
	public void shouldReturnLowStockWhenAvailabilityIsBiggerThan0AndLessOrEqualDefaultLowStockThreshold()
	{
		//Given
		montrealStockLevel.setAvailable(12);
		bostonStockLevel.setAvailable(12);
		modelService.save(montrealStockLevel);
		modelService.save(bostonStockLevel);
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);
		allocationEvents.Camera_ShippedFromBostonToMontrealNancyHome(ALLOCATED_CAMERA_QTY, bostonStockLevel);

		//When
		final StockLevelStatus result = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);
		//Then
		assertEquals(StockLevelStatus.LOWSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenStockLevelIsForceOutOfStock()
	{
		//Given
		montrealStockLevel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		bostonStockLevel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		modelService.save(montrealStockLevel);
		modelService.save(bostonStockLevel);

		//When
		final StockLevelStatus result = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityIsLessThan0()
	{
		//Given
		montrealStockLevel.setAvailable(0);
		bostonStockLevel.setAvailable(0);
		modelService.save(montrealStockLevel);
		modelService.save(bostonStockLevel);
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);
		allocationEvents.Camera_ShippedFromBostonToMontrealNancyHome(ALLOCATED_CAMERA_QTY, bostonStockLevel);

		//When
		final StockLevelStatus result = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityIsEqual0()
	{
		//Given
		montrealStockLevel.setAvailable(ALLOCATED_CAMERA_QTY.intValue());
		bostonStockLevel.setAvailable(ALLOCATED_CAMERA_QTY.intValue());
		modelService.save(montrealStockLevel);
		modelService.save(bostonStockLevel);
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);
		allocationEvents.Camera_ShippedFromBostonToMontrealNancyHome(ALLOCATED_CAMERA_QTY, bostonStockLevel);

		//When
		final StockLevelStatus result = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenNoStockLevelPassed()
	{
		//Given
		northAmericaStore.setWarehouses(Collections.emptyList());

		//When
		final StockLevelStatus result = commerceStockService
				.getStockLevelStatusForProductAndBaseStore(products.Camera(), northAmericaStore);

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);
	}

	@Test
	public void shouldReturnOutOfStockWhenAvailabilityForPoSIsLessThan0()
	{
		//Given
		montrealStockLevel.setAvailable(0);
		modelService.save(montrealStockLevel);
		allocationEvents.Camera_ShippedFromMontrealToMontrealNancyHome(ALLOCATED_CAMERA_QTY, montrealStockLevel);

		//When
		final StockLevelStatus result = commerceStockService
				.getStockLevelStatusForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown());

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, result);

		//Given Add a new stocklevel but still keeping atp < 0
		stockLevels.NewStockLevel(products.Camera(), pointsOfService.Montreal_Downtown().getWarehouses().get(0), 5, null);

		//When
		final StockLevelStatus updatedResult = commerceStockService
				.getStockLevelStatusForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown());

		//Then
		assertEquals(StockLevelStatus.OUTOFSTOCK, updatedResult);
	}


	private void assertGlobalAtp(final Long globalAtp, final Long adjust)
	{
		assertEquals(Long.valueOf(MONTREAL_CAMERA_QTY + BOSTON_CAMERA_QTY - adjust), globalAtp);
	}
}
