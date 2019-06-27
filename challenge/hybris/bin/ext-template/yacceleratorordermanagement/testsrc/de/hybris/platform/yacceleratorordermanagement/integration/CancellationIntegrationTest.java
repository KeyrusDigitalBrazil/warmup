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
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordercancel.OrderCancelDeniedException;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.model.OrderCancelConfigModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@IntegrationTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CancellationIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	private final VerifyOrderAndConsignment verifyOrderAndConsignment = new VerifyOrderAndConsignment();
	private Map<AbstractOrderEntryModel, Long> cancellationEntryInfo;
	private Map<AbstractOrderEntryModel, Long> cancellationEntryInfo2;
	private OrderCancelConfigModel configuration;
	private Map<ConsignmentEntryModel, Long> declineEntryInfo;
	private static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	private static final String RE_SOURCE_CHOICE = "reSource";

	@Before
	public void setUp() throws Exception
	{
		if (order != null)
		{
			modelService.remove(order);
		}
		cancellationEntryInfo = new HashMap<>();
		cancellationEntryInfo2 = new HashMap<>();

		configuration = new OrderCancelConfigModel();
		configuration.setOrderCancelAllowed(true);
		modelService.save(configuration);
		declineEntryInfo = new HashMap<>();
		cancellationUtil.setOrderCancelConfig();
	}

	@After
	public void cleanUp()
	{
		cleanUpData();
	}

	/**
	 * Given an order with 1 entries, and cancel:<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * consignment should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancellation result<br>
	 */
	@Test
	public void shouldCancelEntireOrderSuccess_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when cancel the order
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), CAMERA_QTY);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, Long.valueOf(0L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);

		//then verify the ATP
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	@Test
	public void shouldCancelEntireOrderSuccess_SingleEntry_MultiStockLevel() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		IntStream.rangeClosed(1, 6).forEach(e -> stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), 1, null));
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when cancel the order
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), CAMERA_QTY);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, Long.valueOf(0L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);

		//then verify the ATP
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	/**
	 * Given an order with 1 entries, and cancel more:<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * cancellation:{quantity:30, product: product1)}
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelMore_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when cancel the order
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(30L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
	}

	/**
	 * Given an order with 2 entries, and cancel all:<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * entry 2 : {quantity: 3, product: product2}<br>
	 * cancellation:{all}
	 * <p>
	 * Result:<br>
	 * consignment should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancellation result<br>
	 */
	@Test
	public void shouldCancelEntireOrderSuccess_MultiEntry_SingleConsignment() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//when cancel the order
		order.getEntries().forEach(e -> cancellationEntryInfo.put(e, e.getQuantity()));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);

		for (final ConsignmentModel consignment : order.getConsignments())
		{
			sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignment, timeOut);
		}
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, Long.valueOf(0L),
						Long.valueOf(0L), MEMORY_CARD_QTY, Long.valueOf(0L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);

		//then verify the ATP
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Montreal_Downtown()));
	}

	/**
	 * Given an order with 1 entries sourced from 2 POS, and cancel all:<br>
	 * entry 1 : {quantity: 3, product: Camera}<br>
	 * cancellation:{all}
	 * <p>
	 * Result:<br>
	 * consignment should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancellation result<br>
	 */
	@Test
	public void shouldCancelEntireOrderSuccess_singleEntry_MultiConsignment() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 2);
		stockLevels.Camera(warehouses.Boston(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL)).findFirst().get();
		final ConsignmentModel consignmentResult_Boston = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_BOSTON)).findFirst().get();

		//when cancel first consignment
		order.getEntries().forEach(e -> cancellationEntryInfo.put(e, e.getQuantity()));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Boston, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 2);
		assertEquals(3L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(0L)).booleanValue());

		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 0, 0);

		//then verify the ATP and consignment
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
	}

	/**
	 * Given an order with 1 entries, and cancel partially then create shipment<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * cancellation : {quantity: 2, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * 1 shipment should be created with quantity 1<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the shipment result<br>
	 */
	@Test
	public void shouldCancelPartiallyOrderSuccess_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream().findFirst().get();

		//when cancel first consignment
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(2L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(2, order.getConsignments().size());
		assertEquals(2L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//then verify the ATP
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));

		//confirm all the consignment
		order.getConsignments().stream().filter(consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED))
				.forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getConsignments().stream().allMatch(
				result -> result.getStatus().equals(ConsignmentStatus.SHIPPED) || result.getStatus()
						.equals(ConsignmentStatus.CANCELLED)));
		assertEquals(1L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityShipped().longValue());
		assertEquals(OrderStatus.COMPLETED, order.getStatus());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}

	/**
	 * Given an order with 1 entry and 2 consigments, then cancel partially then create shipment<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * cancellation : {quantity: 2, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * generate a new optimized consignment<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the shipment result<br>
	 */
	@Test
	public void shouldCancelPartiallyOrderSuccess_SingleEntry_MultiConsignments() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 2);
		stockLevels.Camera(warehouses.Boston(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL)).findFirst().get();
		final ConsignmentModel consignmentResult_Boston = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_BOSTON)).findFirst().get();

		//when cancel first consignment
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(1L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Boston, timeOut);

		final ConsignmentModel consignmentResult_Montreal_new = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL) || !e.getStatus().equals(ConsignmentStatus.CANCELLED))
				.findFirst().get();
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal_new, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 3);
		assertEquals(1L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(2L)).booleanValue());

		assertTrue(order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL))
				.anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL))
				.anyMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_BOSTON))
				.allMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//then verify the ATP and consignment
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//confirm all the consignment
		order.getConsignments().stream().filter(consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED))
				.forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getConsignments().stream().allMatch(
				result -> result.getStatus().equals(ConsignmentStatus.SHIPPED) || result.getStatus()
						.equals(ConsignmentStatus.CANCELLED)));
		assertEquals(2L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityShipped().longValue());
		assertEquals(OrderStatus.COMPLETED, order.getStatus());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 0, 0);
	}

	/**
	 * Given an order with 1 entries, and cancel partially then create shipment<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * cancellation : {quantity: 2, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * 1 shipment should be created with quantity 1<br>
	 * <p>
	 * Assert:<br>
	 * It verifies a new consignment create from better location<br>
	 */
	@Test
	public void shouldCancelPartiallyOrderAndResourceSuccess_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Boston = order.getConsignments().stream().findFirst().get();
		stockLevels.Camera(warehouses.Montreal(), 2);

		//when cancel first consignment
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(2L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Boston, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(2, order.getConsignments().size());
		assertEquals(2L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//then verify the ATP
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));

		//confirm all the consignment
		order.getConsignments().stream().filter(consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED))
				.forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getConsignments().stream().allMatch(
				result -> result.getStatus().equals(ConsignmentStatus.SHIPPED) || result.getStatus()
						.equals(ConsignmentStatus.CANCELLED)));
		assertEquals(1L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityShipped().longValue());
		assertEquals(OrderStatus.COMPLETED, order.getStatus());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}


	/**
	 * Given an order with 1 entries, partially sourced, and cancel partially then create shipment<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * cancellation : {quantity: 2, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * 1 shipment should be created with quantity 1<br>
	 * <p>
	 * Assert:<br>
	 * It cancel unallocated quantity without modify consignment<br>
	 */
	@Test
	public void shouldCancelUnallocatedQuantityFirst_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream().findFirst().get();

		//when cancel first consignment
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(2L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(1, order.getConsignments().size());
		assertEquals(2L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));

		//confirm the consignment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.SHIPPED)));
		assertEquals(1L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityShipped().longValue());
		assertEquals(OrderStatus.COMPLETED, order.getStatus());
		sourcingUtil.refreshOrder(order);
		assertFalse(cancellationUtil.isCancelPossible(order, true, true));

		//then verify the dynamic attribute
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertEquals(Long.valueOf(2), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityCancelled());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityShipped());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityPending());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantity());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityAllocated());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 0, 0);
	}

	@Test(expected = OrderCancelDeniedException.class)
	public void shouldCancelFail_alreadyShipped() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream().findFirst().get();

		//confirm all the consignment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.SHIPPED)));
		assertEquals(3L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityShipped().longValue());
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED) || order.getStatus().equals(OrderStatus.COMPLETED));

		//when cancel first consignment
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(2L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}

	/**
	 * Given an order with 1 entries, cancel entire order which already has been partially cancelled<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * cancellation : {quantity: 1, product: product1}<br>
	 * cancellation : {quantity: 2, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * order should be cancel complete<br>
	 * <p>
	 * Assert:<br>
	 * order status in cancel<br>
	 */
	@Test
	public void shouldCancelEntirePartiallyCancelledOrder_singleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL)).findFirst().get();

		//when cancel first time
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(1L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		final ConsignmentModel consignmentResult_Montreal_new = order.getConsignments().stream()
				.filter(e -> !e.getStatus().equals(ConsignmentStatus.CANCELLED)).findFirst().get();
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal_new, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 2);
		assertEquals(1L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(2L)).booleanValue());

		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//when cancel second time
		sourcingUtil.refreshOrder(order);
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo2.put(order.getEntries().stream().findFirst().get(), Long.valueOf(2L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo2, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal_new, timeOut);

		//then verify the order

		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(0L)).booleanValue());

		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal_new, timeOut);
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));

		//then verify the ATP and consignment
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 0, 0);
	}

	@Test
	public void shouldCancelOrderSuccess_MultiOrderEntry_OMSE_1446() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 3);
		stockLevels.MemoryCard(warehouses.Montreal(), 2);
		stockLevels.MemoryCard(warehouses.Boston(), 1);
		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream().findFirst().get();

		//when cancel memoryCard with quantity 1
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo
				.put(order.getEntries().stream().filter(entry -> entry.getProduct().getCode().equals(MEMORY_CARD_CODE)).findFirst()
						.get(), Long.valueOf(1L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		final ConsignmentModel consignmentResult_Montreal_New = order.getConsignments().stream()
				.filter(e -> !e.getStatus().equals(ConsignmentStatus.CANCELLED)).findFirst().get();

		//when decline memoryCard with quantity 1
		declineEntryInfo.put(consignmentResult_Montreal_New.getConsignmentEntries().stream()
				.filter(c -> c.getOrderEntry().getProduct().getCode().equals(MEMORY_CARD_CODE)).findFirst().get(), 1L);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult_Montreal_New, declineEntryInfo, orderProcessModel,
				DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//when confirm consignment for Camera
		order.getConsignments().stream().filter(
				consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED) && consignment.getConsignmentEntries()
						.stream().anyMatch(ce -> ce.getOrderEntry().getProduct().getCode().equals(CAMERA_CODE)))
				.forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);

		//when cancel consignment
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo2
				.put(order.getEntries().stream().filter(o -> o.getProduct().getCode().equals(CODE_MEMORY_CARD)).findFirst().get(),
						Long.valueOf(1L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo2, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);


		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertFalse(cancellationUtil.isCancelPossible(order, true, true));
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertEquals(3, order.getConsignments().size());
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.SHIPPED)));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertFalse(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getStatus().equals(OrderStatus.COMPLETED));

		//then verify the ATP
		// Camera: Global ATP should be 0, since Montreal Warehouse is banned.
		// MemoryCard: Global ATP should be 1, since Montreal Warehouse is banned and Boston's availability is 1.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndBaseStore(products.MemoryCard(), baseStores.NorthAmerica()));

		//then verify the dynamic attribute
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityCancelled());
		assertEquals(Long.valueOf(3), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityShipped());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityPending());
		assertEquals(Long.valueOf(3), cancellationUtil.getOrderEntryModel_Camera(order).getQuantity());
		assertEquals(Long.valueOf(3), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityAllocated());

		assertEquals(Long.valueOf(2), cancellationUtil.getOrderEntryModel_MemoryCard(order).getQuantityCancelled());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_MemoryCard(order).getQuantityShipped());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_MemoryCard(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_MemoryCard(order).getQuantityPending());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_MemoryCard(order).getQuantity());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_MemoryCard(order).getQuantityAllocated());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 0, 0);
	}

	@Test
	public void shouldCancelOrderSuccess_PartiallySourced_OMSE_1440() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream().findFirst().get();

		//when confirm consignment for Camera
		order.getConsignments().stream().filter(
				consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED) && consignment.getConsignmentEntries()
						.stream().anyMatch(ce -> ce.getOrderEntry().getProduct().getCode().equals(CAMERA_CODE)))
				.forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);

		//when add inventory to another location
		stockLevels.Camera(warehouses.Boston(), 2);
		modelService.saveAll();
		sourcingUtil.getOrderBusinessProcessService().triggerChoiceEvent(order, ORDER_ACTION_EVENT_NAME, RE_SOURCE_CHOICE);
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		modelService.refresh(order);
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//when cancel consignment
		sourcingUtil.refreshOrder(order);
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(2L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertEquals(2, order.getConsignments().size());
		assertEquals(2L, ((OrderEntryModel) order.getEntries().stream().findFirst().get()).getQuantityCancelled().longValue());
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.COMPLETED));

		//then verify the ATP
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//then verify the dynamic attribute
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertEquals(Long.valueOf(2), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityCancelled());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityShipped());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityPending());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantity());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityAllocated());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}

	@Test
	public void shouldCancelOrderSuccess_NothingToSource() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 0);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		//when cancel order
		sourcingUtil.refreshOrder(order);
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(3L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//then verify the dynamic attribute
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));

		assertEquals(Long.valueOf(3), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityCancelled());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityShipped());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityPending());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantity());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityAllocated());
		sourcingUtil.assertProcessState(orderProcessModel, ProcessState.SUCCEEDED);
	}

	@Test
	public void shouldCancelOrderSuccess_PartiallySourcedANdPartiallyConfirmed_OMSE_1454()
			throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		//when confirm consignment for Camera
		order.getConsignments().stream().filter(
				consignment -> !consignment.getStatus().equals(ConsignmentStatus.CANCELLED) && consignment.getConsignmentEntries()
						.stream().anyMatch(ce -> ce.getOrderEntry().getProduct().getCode().equals(CAMERA_CODE)))
				.forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);

		//when partially cancel unallocated quantity
		sourcingUtil.refreshOrder(order);
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(1L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getStatus().equals(OrderStatus.SUSPENDED));

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//then verify the dynamic attribute
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityCancelled());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityShipped());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityPending());
		assertEquals(Long.valueOf(2), cancellationUtil.getOrderEntryModel_Camera(order).getQuantity());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityAllocated());

		//when cancel all the unallocated quantity
		sourcingUtil.refreshOrder(order);
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));

		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), Long.valueOf(1L));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getStatus().equals(OrderStatus.COMPLETED));

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));

		//then verify the dynamic attribute
		order.getConsignments().stream().forEach(e -> sourcingUtil.getModelService().refresh(e));
		assertEquals(Long.valueOf(2), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityCancelled());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityShipped());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityUnallocated());
		assertEquals(Long.valueOf(0), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityPending());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantity());
		assertEquals(Long.valueOf(1), cancellationUtil.getOrderEntryModel_Camera(order).getQuantityAllocated());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 0, 0);
	}

	/**
	 * Given an order with 1 entry {quantity: 23, product: product1} and status WAIT_FRAUD_MANUAL_CHECK:<br>
	 * Partial Cancel : {quantity: 3, product: product1}<br>
	 * <p>
	 * Result:<br>
	 * Order status should still be WAIT_FRAUD_MANUAL_CHECK, while the order entry's quantity get reduced by 3<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancellation result<br>
	 */
	@Test
	public void shouldPartialCancelPotentialFraudOrderSuccess_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 23);
		order = sourcingUtil.createOrder(orders.Camera_Shipped(23L));
		order.setTotalPrice(Double.valueOf("2400"));
		modelService.save(order);
		final OrderProcessModel orderProcessModel = sourcingUtil
				.runOrderProcessForOrderBasedPriority(order, OrderStatus.WAIT_FRAUD_MANUAL_CHECK);

		//when cancel the order
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), CAMERA_QTY);
		assertTrue(cancellationUtil.isCancelPossible(order, true, true));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.CUSTOMERREQUEST);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify order and consignment and process state
		sourcingUtil.refreshOrder(order);
		assertEquals(0, order.getConsignments().size());
		assertTrue(order.getStatus().equals(OrderStatus.WAIT_FRAUD_MANUAL_CHECK));
		sourcingUtil.assertProcessState(orderProcessModel, ProcessState.RUNNING);
	}

	/**
	 * Given an order with 1 entry {quantity: 23, product: product1} and status WAIT_FRAUD_MANUAL_CHECK:<br>
	 * Cancel the order completely
	 * <p>
	 * Result:<br>
	 * Order status should be Cancelled, while the order process moves to end state<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancellation result<br>
	 */
	@Test
	public void shouldCompleteCancelPotentialFraudOrderSuccess_SingleEntry() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 23);
		order = sourcingUtil.createOrder(orders.Camera_Shipped(23L));
		order.setTotalPrice(Double.valueOf("2400"));
		modelService.save(order);
		final OrderProcessModel orderProcessModel = sourcingUtil
				.runOrderProcessForOrderBasedPriority(order, OrderStatus.WAIT_FRAUD_MANUAL_CHECK);

		//when cancel the order
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), 23L);
		assertTrue(cancellationUtil.isCancelPossible(order, false, false));
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.CUSTOMERREQUEST);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify order and consignment and process state
		sourcingUtil.refreshOrder(order);
		assertEquals(0, order.getConsignments().size());
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));
		sourcingUtil.assertProcessState(orderProcessModel, ProcessState.SUCCEEDED);
	}

	@Test
	public void shouldNotCreateEventWhenCancelWholeOrderFromExternalWarehouse() throws InterruptedException, OrderCancelException
	{
		// Given
		final WarehouseModel externalMontrealWarehouse = warehouses.Montreal_External();
		stockLevels.Camera(warehouses.Boston(), 3);
		stockLevels.Camera(externalMontrealWarehouse, 6);

		//Verify ATP
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// Verify consignment is allocated from the external warehouse
		assertEquals(1, order.getConsignments().size());
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		assertEquals(externalMontrealWarehouse.getCode(), consignmentResult.getWarehouse().getCode());

		//then verify the ATP (ATP should remain same as NO allocation event were created for the external warehouse)
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		//when the order is cancelled
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), CAMERA_QTY);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the ATP (ATP should remain same as NO allocation event were created for the external warehouse)
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		assertEquals(1, order.getConsignments().size());
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}

	@Test
	public void shouldNotCreateEventWhenCancelPartialOrderFromExternalWarehouse() throws InterruptedException, OrderCancelException
	{
		// Given
		final WarehouseModel externalMontrealWarehouse = warehouses.Montreal_External();
		stockLevels.Camera(warehouses.Boston(), 3);
		stockLevels.Camera(externalMontrealWarehouse, 6);

		//Verify ATP
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// Verify consignment is allocated from the external warehouse
		assertEquals(1, order.getConsignments().size());
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		assertEquals(externalMontrealWarehouse.getCode(), consignmentResult.getWarehouse().getCode());

		//then verify the ATP (ATP should remain same as NO allocation event were created for the external warehouse)
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		//when the order is cancelled
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), 1L);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the ATP (ATP should remain same as NO allocation event were created for the external warehouse)
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		assertEquals(2, order.getConsignments().size());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);
	}

}
