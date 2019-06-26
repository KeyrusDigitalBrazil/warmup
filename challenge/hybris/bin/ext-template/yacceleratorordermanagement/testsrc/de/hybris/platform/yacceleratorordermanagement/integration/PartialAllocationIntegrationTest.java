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
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class PartialAllocationIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	private static final Logger LOG = LoggerFactory.getLogger(PartialAllocationIntegrationTest.class);
	private static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	private static final String RE_SOURCE_CHOICE = "reSource";
	private static final Long INITIAL_CAMERA_STOCK = Long.valueOf(2L);
	private static final Long CAMERA_QTY = Long.valueOf(3L);
	private final static int timeOut = 40; //seconds
	private VerifyOrderAndConsignment verifyOrderAndConsignment = new VerifyOrderAndConsignment();

	@Before
	public void setUp() throws Exception
	{
		if (order != null)
		{
			modelService.remove(order);
		}
	}

	@After
	public void cleanUp()
	{
		cleanUpData();
	}

	@Test
	public void ShouldFulfillPartially_Shipping() throws InterruptedException
	{
		//Given
		stockLevels.Camera(warehouses.Montreal(), INITIAL_CAMERA_STOCK.intValue());
		order = sourcingUtil.createCameraShippedOrder();

		// When
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		assertEquals(OrderStatus.SUSPENDED, orderProcessModel.getOrder().getStatus());
		LOG.info("Order process is in state " + orderProcessModel.getOrder().getStatus());

		LOG.info("Number of consignments: " + order.getConsignments().size());
		assertEquals(1, order.getConsignments().size());
		assertEquals(INITIAL_CAMERA_STOCK, ((OrderEntryModel) order.getEntries().get(0)).getQuantityAllocated());

		LOG.info("Stock added in boston warehouse for the camera");
		stockLevels.Camera(warehouses.Boston(), INITIAL_CAMERA_STOCK.intValue());
		modelService.saveAll();

		LOG.info("Second sourcing processing");
		sourcingUtil.getOrderBusinessProcessService().triggerChoiceEvent(order, ORDER_ACTION_EVENT_NAME, RE_SOURCE_CHOICE);

		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		modelService.refresh(order);
		assertEquals(2, order.getConsignments().size());
		assertTrue(order.getStatus().equals(OrderStatus.READY));
	}

	@Test
	public void ShouldFulfillPartially_PickUp() throws InterruptedException
	{
		//Given create order
		stockLevels.Camera(warehouses.Montreal(), INITIAL_CAMERA_STOCK.intValue());
		order = sourcingUtil.createCameraPickUpOrder();

		//When
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);
		//then verify the order has been partially fullfilled
		assertEquals(OrderStatus.SUSPENDED, orderProcessModel.getOrder().getStatus());
		LOG.info("Order process is in state " + orderProcessModel.getOrder().getStatus());

		LOG.info("Number of consignments: " + order.getConsignments().size());
		assertEquals(1, order.getConsignments().size());
		assertEquals(INITIAL_CAMERA_STOCK, ((OrderEntryModel) order.getEntries().get(0)).getQuantityAllocated());

		//when add inventory to another location
		LOG.info("Stock added in boston warehouse for the camera");
		stockLevels.Camera(warehouses.Boston(), INITIAL_CAMERA_STOCK.intValue());
		modelService.saveAll();

		LOG.info("Second sourcing processing");
		sourcingUtil.getOrderBusinessProcessService().triggerChoiceEvent(order, ORDER_ACTION_EVENT_NAME, RE_SOURCE_CHOICE);

		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		//then verify the order has been partially fullfilled
		modelService.refresh(order);
		assertEquals(1, order.getConsignments().size());
		assertTrue(order.getStatus().equals(OrderStatus.SUSPENDED));

		//when add inventory to same location
		LOG.info("Stock added in Montreal warehouse for the camera");

		stockService.updateActualStockLevel(products.Camera(), warehouses.Montreal(), 5, "");

		modelService.saveAll();

		LOG.info("Second sourcing processing");
		sourcingUtil.getOrderBusinessProcessService().triggerChoiceEvent(order, ORDER_ACTION_EVENT_NAME, RE_SOURCE_CHOICE);

		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		//then verify the order has been fulfilled
		modelService.refresh(order);
		assertEquals(2, order.getConsignments().size());

		sourcingUtil.refreshOrder(order);
		assertTrue(order.getStatus().equals(OrderStatus.READY));
		order.getConsignments().stream()
				.anyMatch(e -> e.getConsignmentEntries().stream().anyMatch(a -> a.getQuantity().longValue() == 1L));
	}

	@Test
	public void shouldOrderOnHold_ProductNull_PartiallyFailSourcing() throws InterruptedException
	{
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		stockLevels.MemoryCard(warehouses.Boston(), 6);
		modelService.saveAll();
		LOG.info("Second sourcing processing");
		sourcingUtil.getOrderBusinessProcessService().triggerChoiceEvent(order, ORDER_ACTION_EVENT_NAME, RE_SOURCE_CHOICE);
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getStatus().equals(OrderStatus.READY));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment
						.verifyConsignment(order, CODE_MEMORY_CARD, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(2L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment
						.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(3L), Long.valueOf(3L)));
	}

	@Test
	public void shouldOrderOnHold_ProductNull_FailSourcing() throws InterruptedException
	{
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		stockLevels.Camera(warehouses.Boston(), 6);
		modelService.saveAll();
		LOG.info("Second sourcing processing");
		sourcingUtil.getOrderBusinessProcessService().triggerChoiceEvent(order, ORDER_ACTION_EVENT_NAME, RE_SOURCE_CHOICE);
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);
		sourcingUtil.refreshOrder(order);
		assertTrue(order.getStatus().equals(OrderStatus.READY));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment
						.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(3L), Long.valueOf(3L)));
	}
}
