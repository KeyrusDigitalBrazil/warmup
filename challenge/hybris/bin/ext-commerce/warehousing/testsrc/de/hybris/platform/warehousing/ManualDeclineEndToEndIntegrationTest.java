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
package de.hybris.platform.warehousing;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.warehousing.allocation.AllocationException;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.util.BaseSourcingIntegrationTest;
import de.hybris.platform.warehousing.util.DeclineEntryBuilder;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class ManualDeclineEndToEndIntegrationTest extends BaseSourcingIntegrationTest
{
	@Resource
	private AllocationService allocationService;
	@Resource
	private CommerceStockService commerceStockService;
	@Resource
	private StockService stockService;
	@Resource
	private WarehouseService warehouseService;

	private static final Long CAMERA_QTY = new Long(3);
	private static final Long MEMORYCARD_QTY = new Long(4);
	public static final String CODE_MONTREAL = "montreal";
	public static final String CODE_BOSTON = "boston";
	public static final String NAME_MONTREAL_DOWNTOWN = "montreal-downtown";
	public Map<ConsignmentEntryModel, Long> declineEntryInfo;
	private VerifyOrderAndConsignment verifyOrderAndConsignmentUtil = new VerifyOrderAndConsignment();

	@Before
	public void setup()
	{
		declineEntryInfo = new HashMap<ConsignmentEntryModel, Long>();
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should SUCCESS without insufficient stock<br>
	 * <p>
	 */
	@Test
	public void shouldManualDecline_SuccessReSourced()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 1);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), CAMERA_QTY);
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(-2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(5),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify new consignment
		refreshOrder(order);
		assertTrue(order.getConsignments().size() == 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));

		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY).booleanValue());
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should SUCCESS without sufficient stock twice<br>
	 * <p>
	 */
	@Test
	public void shouldManualDecline_SuccessReSourcedTwice()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 5);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), CAMERA_QTY);
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));
		refreshOrder(order);
		declineEntryInfo.put(order.getConsignments().stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(1), Long.valueOf(1L));
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_MONTREAL)));

		//then verify the ATP
		assertEquals(Long.valueOf(7),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Partially manual Decline should SUCCESS without insufficient stock<br>
	 * <p>
	 */
	@Test
	public void shouldPartiallyManualDecline_SuccessReSourced()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 1);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), Long.valueOf(1L));
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify new consignment
		refreshOrder(order);
		assertTrue(order.getConsignments().size() == 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));

		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(1L), Long.valueOf(2L), Long.valueOf(2L))
				.booleanValue());
		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L))
				.booleanValue());
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should fail,should at least have 1 stock in new location<br>
	 * <p>
	 */
	@Test(expected = AllocationException.class)
	public void shouldManualDecline_FailedNoStock()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), Long.valueOf(1L));
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should fail,should at least have 1 stock in new location<br>
	 * <p>
	 */
	@Test(expected = AllocationException.class)
	public void shouldManualDecline_FailedOverQuantity()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), Long.valueOf(4L));
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 3, product: memoryCard}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should SUCCESS without insufficient stock<br>
	 * <p>
	 */
	@Test
	public void shouldManualDecline2Entries_SuccessReSourced()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 1);
		stockLevels.MemoryCard(warehouses.Montreal(), 5);
		stockLevels.MemoryCard(warehouses.Boston(), 1);

		// When create consignment
		final OrderModel order = orders.CameraAndMemoryCard_Shipped(CAMERA_QTY, MEMORYCARD_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), CAMERA_QTY);
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(1), CAMERA_QTY);
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(-2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(5),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(
				Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndPointOfService(products.MemoryCard(),
						pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.MemoryCard(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(-2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Boston()));

		//then verify new consignment
		refreshOrder(order);
		assertTrue(order.getConsignments().size() == 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));
		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L), CAMERA_QTY, Long.valueOf(1L),Long.valueOf(1L))
				.booleanValue());
		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera_MemoryCard(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY, Long.valueOf(0L), CAMERA_QTY,
				CAMERA_QTY).booleanValue());
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 3, product: memoryCard}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should failed<br>
	 * <p>
	 */
	@Test(expected = AllocationException.class)
	public void shouldManualDecline2Entries_FailedReSourced()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 1);
		stockLevels.MemoryCard(warehouses.Montreal(), 5);

		// When create consignment
		final OrderModel order = orders.CameraAndMemoryCard_Shipped(CAMERA_QTY, MEMORYCARD_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when manual decline the order
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), CAMERA_QTY);
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(1), CAMERA_QTY);
		allocationService.manualReallocate(DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo,
				warehouseService.getWarehouseForCode(CODE_BOSTON)));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should SUCCESS without insufficient stock<br>
	 * <p>
	 */
	@Test
	public void shouldManualDecline_ForceOutOfStock()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 1);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when set stock level force out of stock and manual decline the order
		stockService.setInStockStatus(products.Camera(), Collections.singleton(warehouses.Montreal()), InStockStatus.FORCEOUTOFSTOCK);
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), CAMERA_QTY);
		allocationService
				.manualReallocate(
						DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo, warehouseService.getWarehouseForCode(CODE_BOSTON)));

		//then verify the ATP
		assertEquals(Long.valueOf(-2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(-2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0),
				commerceStockService
						.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify new consignment
		refreshOrder(order);
		assertTrue(order.getConsignments().size() == 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));

		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY).booleanValue());
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * <p>
	 * Result:<br>
	 * Manual Decline should SUCCESS without insufficient stock<br>
	 * <p>
	 */
	@Test
	public void shouldManualDecline_ForceInStock()
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 1);

		// When create consignment
		final OrderModel order = orders.Camera_Shipped(CAMERA_QTY);
		final SourcingResults results = sourcingService.sourceOrder(order);
		final Collection<ConsignmentModel> consignmentResult = allocationService.createConsignments(order, "con", results);

		assertTrue(CollectionUtils.isNotEmpty(consignmentResult));
		assertNotNull(consignmentResult.iterator().next().getConsignmentEntries().iterator().next().getOrderEntry().getProduct());

		//then sourcing result is not null and sourcing is complete
		assertTrue(results.isComplete());

		//when set stock level force out of stock and manual decline the order
		stockService.setInStockStatus(products.Camera(), Collections.singleton(warehouses.Boston()), InStockStatus.FORCEINSTOCK);
		declineEntryInfo.put(consignmentResult.stream().flatMap(consignment -> consignment.getConsignmentEntries().stream())
				.collect(Collectors.toList()).get(0), CAMERA_QTY);
		allocationService
				.manualReallocate(
						DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfo, warehouseService.getWarehouseForCode(CODE_BOSTON)));

		//then verify the ATP
		assertEquals(null,
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(null,
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(5),
				commerceStockService
						.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify new consignment
		refreshOrder(order);
		assertTrue(order.getConsignments().size() == 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));

		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignmentUtil.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY).booleanValue());
	}
}
