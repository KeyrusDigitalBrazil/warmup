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
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class OrderOnHoldIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	private final VerifyOrderAndConsignment verifyOrderAndConsignment = new VerifyOrderAndConsignment();
	private Map<AbstractOrderEntryModel, Long> cancellationEntryInfo;
	private Map<ConsignmentEntryModel, Long> declineEntryInfo;
	private static final String PUT_ON_HOLD_CHOICE = "putOnHold";

	@Before
	public void setUp() throws Exception
	{
		if (order != null)
		{
			modelService.remove(order);
		}
		cancellationEntryInfo = new HashMap<>();
		cancellationUtil.setOrderCancelConfig();
		declineEntryInfo = new HashMap<>();
	}

	@After
	public void cleanUp()
	{
		cleanUpData();
	}

	/**
	 * Given an order with 1 entries and put this order on hold:<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * Consignment 1 : {quantity: 3, product: product1, Ready}<br>
	 * <p>
	 * Result:<br>
	 * 1. Consignment 1 should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancelled consignment<br>
	 */
	@Test
	public void shouldPutOrderOnHold() throws InterruptedException, OrderCancelException
	{
		// Given create the order
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//When put order on hold
		sourcingUtil.moveOrderProcess(order, orderProcessModel, PUT_ON_HOLD_CHOICE);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.ON_HOLD));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, Long.valueOf(0L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}

	/**
	 * Given a partially declined order with 1 entries and put this order on hold:<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * Consignment 1 : {quantity: 2, quantityDeclined: 1, product: product1, Ready}<br>
	 * Consignment 2 : {quantity: 1, product: product1, Ready}<br>
	 * <p>
	 * Result:<br>
	 * 1. Consignment 1 should be cancelled<br>
	 * 2. Consignment 2 should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancelled consignments<br>
	 */
	@Test
	public void shouldPutPartiallyDeclinedOrderOnHold() throws InterruptedException, OrderCancelException
	{
		// Given create the order and decline it partially
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 4);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), 1L);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//When put order on hold
		sourcingUtil.moveOrderProcess(order, orderProcessModel, PUT_ON_HOLD_CHOICE);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.ON_HOLD));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(1L), Long.valueOf(2L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(0L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 0, 0);

		//then verify the ATP
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
	}

	/**
	 * Given a partially cancelled order with 1 entries and put this order on hold:<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * Consignment 1 : {quantity: 3, product: product1, Cancelled}<br>
	 * Consignment 2 : {quantity: 2, product: product1, Ready}<br>
	 * <p>
	 * Result:<br>
	 * 1. Consignment 1 should be cancelled<br>
	 * 2. Consignment 2 should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancelled consignments<br>
	 */
	@Test
	public void shouldPutPartiallyCancelledOrderOnHoldAndCancelPartiallyAgain() throws InterruptedException, OrderCancelException
	{
		// Given create the order and cancel it partially
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), 1L);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, order.getConsignments().iterator().next(), timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//When put order on hold
		sourcingUtil.moveOrderProcess(order, orderProcessModel, PUT_ON_HOLD_CHOICE);

		//then verify the consignment
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 2);

		//When cancel the order partially again
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), 1L);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, order.getConsignments().iterator().next(), timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//Then verify order still is on hold and do not go to sourcing again
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.ON_HOLD));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(0L)).booleanValue());

		//Then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 0, 0);
	}



	/**
	 * Given an on hold order with 1 entries and re-source this order<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * Consignment 1 : {quantity: 3, product: product1, Cancelled}<br>
	 * <p>
	 * Result:<br>
	 * 1. Consignment 1 should be cancelled<br>
	 * 2. Consignment 2 should be Ready<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the consignments<br>
	 */
	@Test
	public void shouldReSourceOnHoldOrder() throws InterruptedException, OrderCancelException
	{
		// Given create the order and put it on hold
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		sourcingUtil.moveOrderProcess(order, orderProcessModel, PUT_ON_HOLD_CHOICE);

		//When resource the on hold order
		sourcingUtil.moveOrderProcess(order, orderProcessModel, RE_SOURCE_CHOICE);

		//then verify the order and consignments
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 2);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.READY)));
		assertTrue(order.getStatus().equals(OrderStatus.READY));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, Long.valueOf(3L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);
	}

	/**
	 * Given an on hold order with 1 entries and cancel this order<br>
	 * entry 1 : {quantity: 3, product: product1}<br>
	 * Consignment 1 : {quantity: 3, product: product1, Cancelled}<br>
	 * <p>
	 * Result:<br>
	 * 1. Consignment 1 should be cancelled<br>
	 * 2. Order should be cancelled<br>
	 * <p>
	 * Assert:<br>
	 * It verifies the cancelled order and consignment<br>
	 */
	@Test
	public void shouldCancelOnHoldOrder() throws InterruptedException, OrderCancelException
	{
		// Given create the order and put it on hold
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		sourcingUtil.moveOrderProcess(order, orderProcessModel, PUT_ON_HOLD_CHOICE);

		//When cancel the on hold order
		cancellationEntryInfo.put(order.getEntries().stream().findFirst().get(), CAMERA_QTY);
		cancellationUtil.cancelOrder(order, cancellationEntryInfo, CancelReason.LATEDELIVERY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, order.getConsignments().iterator().next(), timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);

		//then verify the order and consignments
		sourcingUtil.refreshOrder(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.CANCELLED)));
		assertTrue(order.getStatus().equals(OrderStatus.CANCELLED));
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, Long.valueOf(0L)).booleanValue());

		//then verify that the consignment workflow has been terminated
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 0, 0);
	}

}
