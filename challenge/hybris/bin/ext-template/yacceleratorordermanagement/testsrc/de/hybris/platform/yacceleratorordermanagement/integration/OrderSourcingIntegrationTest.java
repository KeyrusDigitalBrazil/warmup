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
package de.hybris.platform.yacceleratorordermanagement.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.model.SourcingConfigModel;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.exceptions.WorkflowActionDecideException;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.yacceleratorordermanagement.integration.util.WorkflowUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class OrderSourcingIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	private final VerifyOrderAndConsignment verifyOrderAndConsignment = new VerifyOrderAndConsignment();
	private static final Logger LOG = LoggerFactory.getLogger(OrderSourcingIntegrationTest.class);

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

	/**
	 * Given that a consignment is Packed the Task Assignment Workflow must also be updated.
	 * Result:
	 * The task assignment workflow is in the Shipping step
	 */
	@Test
	public void updateTaskAssignmentWorkflowWithPackShippingConsignment() throws InterruptedException
	{
		//Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		modelService.refresh(order);

		//When packing a consignment
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PICKING_TEMPLATE_CODE));
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PACKING_TEMPLATE_CODE));

		modelService.refresh(order);
		//Verify the consignment status and workflow status
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("READY_FOR_SHIPPING")));
		assertTrue(order.getConsignments().stream().anyMatch(result ->
		{
			final WorkflowModel workflowModel = sourcingUtil.getNewestWorkflowService()
					.getWorkflowForCode(result.getTaskAssignmentWorkflow());
			return workflowModel.getActions().stream()
					.anyMatch(action -> action.getName().equals("Shipping") && action.getStatus().getCode().equals("in_progress"));
		}));
	}

	/**
	 * Given that a consignment is Packed the Task Assignment Workflow must also be updated.
	 * Result:
	 * The task assignment workflow is in the Ready for Pick up step
	 */
	@Test
	public void updateTaskAssignmentWorkflowWithPackPickUpConsignment() throws InterruptedException
	{
		//Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraPickUpOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		modelService.refresh(order);

		//When packing a consignment
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PICKING_TEMPLATE_CODE));
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PACKING_TEMPLATE_CODE));

		//Verify the consignment status and workflow status
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("READY_FOR_PICKUP")));
		assertTrue(order.getConsignments().stream().anyMatch(result ->
		{
			final WorkflowModel workflowModel = sourcingUtil.getNewestWorkflowService()
					.getWorkflowForCode(result.getTaskAssignmentWorkflow());
			return workflowModel.getActions().stream()
					.anyMatch(action -> action.getName().equals("Pick up") && action.getStatus().getCode().equals("in_progress"));
		}));
	}

	/**
	 * Given that a consignment is Shipped the Task Assignment Workflow must also be updated.
	 * Result:
	 * The task assignment workflow is terminated
	 */
	@Test
	public void updateTaskAssignmentWorkflowWithShipConsignment() throws InterruptedException
	{
		//Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		modelService.refresh(order);

		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PICKING_TEMPLATE_CODE));
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PACKING_TEMPLATE_CODE));
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.SHIPPING_TEMPLATE_CODE));

		//Verify the consignment status and workflow status
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("SHIPPED")));
		assertTrue(order.getConsignments().stream().anyMatch(result ->
		{
			final WorkflowModel workflowModel = sourcingUtil.getNewestWorkflowService()
					.getWorkflowForCode(result.getTaskAssignmentWorkflow());
			return workflowModel.getActions().stream().anyMatch(
					action -> action.getName().equals("Shipping") && action.getStatus().equals(WorkflowActionStatus.COMPLETED));
		}));
	}

	/**
	 * Given that a pickup consignment is Shipped the Task Assignment Workflow must also be updated.
	 * Result:
	 * The task assignment workflow is terminated
	 */
	@Test
	public void updateTaskAssignmentWorkflowWithPickupConsignment() throws InterruptedException
	{
		//Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraPickUpOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		modelService.refresh(order);

		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PICKING_TEMPLATE_CODE));
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PACKING_TEMPLATE_CODE));
		order.getConsignments().forEach(
				consignment -> workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignment, WorkflowUtil.PICKUP_TEMPLATE_CODE));

		//Verify the consignment status and workflow status
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("PICKUP_COMPLETE")));
		assertTrue(order.getConsignments().stream().anyMatch(result ->
		{
			final WorkflowModel workflowModel = sourcingUtil.getNewestWorkflowService()
					.getWorkflowForCode(result.getTaskAssignmentWorkflow());
			return workflowModel.getActions().stream().anyMatch(
					action -> action.getName().equals("Pick up") && action.getStatus().equals(WorkflowActionStatus.COMPLETED));
		}));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify the order status and consignment result<br>
	 */
	@Test
	public void shouldSourcingSuccess_SingleEntry() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

		//when confirm shipment
		order.getConsignments().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));

		//verify the confirmed shipment
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("SHIPPED")));
		assertTrue(order.getStatus().getCode().equals("COMPLETED"));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity Pending: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated());
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityPending().equals(Long.valueOf(0L)));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * The {@link de.hybris.platform.store.BaseStoreModel} for the Order does not have {@link SourcingConfigModel}. <br>
	 * So the sourcing fails and order status changes to Suspended.<br>
	 * <br>
	 * Later a {@link SourcingConfigModel} is provided for the {@link BaseStoreModel}, and as a result:<br>
	 * 1). Sourcing is successful and consignment is created<br>
	 * 2). The order status changes to Ready<br>
	 * 3). The ATP is reduced by the ordered Qty<br>
	 */
	@Test
	public void shouldNotSourceSourcingConfigNull() throws InterruptedException
	{
		// Given: Order placed with no sourcing config assigned to the baseStore.
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		final SourcingConfigModel sourcingConfig = order.getStore().getSourcingConfig();
		order.getStore().setSourcingConfig(null);

		//When
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		//Then: verify the ATP remains unchanged and order status is suspended
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(OrderStatus.SUSPENDED, order.getStatus());

		//Given: Assign SourcingConfiguration to the baseStore and run sourcing again
		order.getStore().setSourcingConfig(sourcingConfig);
		modelService.save(order.getStore());
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 0, 100, 0);
		orderBusinessProcessService.triggerEvent(
				BusinessProcessEvent.builder(orderProcessModel.getCode() + "_" + ORDER_ACTION_EVENT_NAME).withChoice(RE_SOURCE_CHOICE)
						.withEventTriggeringInTheFutureDisabled().build());
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		//Then: verify the ATP is now reduced by ordered quantity and order status is Ready
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(OrderStatus.READY, order.getStatus());

		//Then: verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));
	}

	/**
	 * Given an pickup order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify the order status and consignment result<br>
	 */
	@Test
	public void shouldSourcingSuccess_SingleEntry_PickUp() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraPickUpOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

		//when confirm shipment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));

		//verify the confirmed shipment
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("PICKUP_COMPLETE")));
		assertTrue(order.getStatus().getCode().equals("COMPLETED"));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity unallocated: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated());
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityPending().equals(Long.valueOf(0L)));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify the order sourcing failed<br>
	 */
	@Test
	public void shouldSourcingFail_SingleEntry() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 0);

		order = sourcingUtil.createCameraShippedOrder();
		sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 0);
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify the order status and consignment result<br>
	 */
	@Test
	public void shouldSourcingSuccess_Priority() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 6);

		//when create order
		order = sourcingUtil.createCameraShippedOrder();
		LOG.info("Sourcing from priority sourcing factor only");
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 0, 100, 0);
		warehouses.Montreal().setPriority(Integer.valueOf(1));
		warehouses.Boston().setPriority(Integer.valueOf(50));
		sourcingUtil.runDefaultOrderProcessForOrder(order, OrderStatus.READY);

		//then verify the result
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Second location should be picked, even if the first one has higher score.<br>
	 * That's because of fitness strategy - second warehouse is able to make whole order at one time.<br>
	 */
	@Test
	public void shouldPickLocationWithLowerScore() throws InterruptedException
	{
		//given
		stockLevels.Camera(warehouses.Montreal(), 2);
		stockLevels.Camera(warehouses.Boston(), 4);

		//when create order
		order = sourcingUtil.createCameraShippedOrder();
		LOG.info("Sourcing from score sourcing factor only");
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 0, 0, 100);
		warehouses.Montreal().setScore(25d);
		warehouses.Boston().setScore(5d);
		sourcingUtil.runDefaultOrderProcessForOrder(order, OrderStatus.READY);

		//verify the result
		assertEquals(Long.valueOf(2), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * In this case, both locations cannot send order at one time.
	 * All products will be taken from first location - because of the higher score.<br>
	 * To make whole order - the one product which is left will be taken from second location.<br>
	 */
	@Test
	public void shouldTakeAllFromFirstLocationAndTheRestFromSecond() throws InterruptedException
	{
		//given
		stockLevels.Camera(warehouses.Montreal(), 2);
		stockLevels.Camera(warehouses.Boston(), 2);

		//when create order
		order = sourcingUtil.createCameraShippedOrder();
		LOG.info("Sourcing from score sourcing factor only");
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 0, 0, 100);
		warehouses.Montreal().setScore(25d);
		warehouses.Boston().setScore(5d);
		sourcingUtil.runDefaultOrderProcessForOrder(order, OrderStatus.READY);

		//verify the result
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Whole order will be delivered from first location because score is higher.
	 */
	@Test
	public void shouldTakeAllFromLocationWithHigherScore() throws InterruptedException
	{
		//given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 6);

		//when create order
		order = sourcingUtil.createCameraShippedOrder();
		LOG.info("Sourcing from score sourcing factor only");
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 0, 0, 100);
		warehouses.Montreal().setScore(25d);
		warehouses.Boston().setScore(5d);
		sourcingUtil.runDefaultOrderProcessForOrder(order, OrderStatus.READY);

		//verify the result
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify the location where it is assigned<br>
	 */
	@Test
	public void shouldSourcingSuccess_DistanceMontreal() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 6);

		//when create order
		order = sourcingUtil.createCameraShippedOrder();
		LOG.info("Sourcing from distance sourcing factor only");
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		sourcingUtil.runDefaultOrderProcessForOrder(order, OrderStatus.READY);

		//then verify the result
		assertEquals(warehouses.Montreal().getName(), order.getConsignments().iterator().next().getWarehouse().getName());
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify the location where it is assigned<br>
	 */
	@Test
	public void shouldSourcingSuccess_DistanceBoston() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 6);

		//when create order
		order = sourcingUtil.createCameraShippedOrder();
		order.setDeliveryAddress(sourcingUtil.getOrders().getAddresses().Boston());
		modelService.save(order);
		LOG.info("Sourcing from distance sourcing factor only");
		sourcingUtil.setSourcingFactors(order.getStore(), 0, 100, 0, 0);
		sourcingUtil.runDefaultOrderProcessForOrder(order, OrderStatus.READY);

		//then verify the result
		assertEquals(warehouses.Boston().getName(), order.getConsignments().iterator().next().getWarehouse().getName());
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 2, product: memoryCard}<br>
	 * Result:<br>
	 * Verify the order status and consignment result<br>
	 */
	@Test
	public void shouldSourcingSuccess_MultiEntry_SingleConsignment() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(4), commerceStockService
				.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Montreal_Downtown()));

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 1);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY, Long.valueOf(0L),
						MEMORY_CARD_QTY, MEMORY_CARD_QTY));

		//when confirm shipment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));

		//verify the confirmed shipment
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("SHIPPED")));
		assertTrue(order.getStatus().getCode().equals("COMPLETED"));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity Pending: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityPending());
		assertTrue(order.getEntries().stream().allMatch(e -> ((OrderEntryModel) e).getQuantityPending().equals(Long.valueOf(0L))));
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 2, product: memoryCard}<br>
	 * Result:<br>
	 * Verify the order status and consignment result<br>
	 */
	@Test
	public void shouldSourcingSuccess_MultiEntries_MultiConsignments() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Boston(), 6);

		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Boston()));

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment(order, CODE_MEMORY_CARD, CODE_BOSTON, Long.valueOf(0L), MEMORY_CARD_QTY, MEMORY_CARD_QTY));

		//when confirm first shipment
		sourcingUtil.confirmDefaultConsignment(orderProcessModel,
				order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL)).findFirst().get());

		//verify the order status
		assertTrue(order.getStatus().getCode().equals("READY"));

		//when confirm second consignment
		sourcingUtil.confirmDefaultConsignment(orderProcessModel,
				order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_BOSTON)).findFirst().get());

		//verify the order
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("SHIPPED")));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity Pending: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityPending());
		assertTrue(order.getEntries().stream().allMatch(e -> ((OrderEntryModel) e).getQuantityPending().equals(Long.valueOf(0L))));
		assertTrue(order.getStatus().getCode().equals("COMPLETED"));
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 2, product: memoryCard}<br>
	 * Result:<br>
	 * Verify the order status and consignment result<br>
	 */
	@Test
	public void shouldSourcingSuccess_MultiEntries_MultiConsignments_SplitOrderEntries() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Boston(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 1);

		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the ATP
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(5),
				commerceStockService.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Boston()));

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY, Long.valueOf(0L),
						Long.valueOf(1L), Long.valueOf(1L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment(order, CODE_MEMORY_CARD, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));

		//when confirm first shipment
		sourcingUtil.confirmDefaultConsignment(orderProcessModel,
				order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_MONTREAL)).findFirst().get());

		//verify the order status
		assertTrue(order.getStatus().getCode().equals("READY"));

		//when confirm second consignment
		sourcingUtil.confirmDefaultConsignment(orderProcessModel,
				order.getConsignments().stream().filter(e -> e.getWarehouse().getCode().equals(CODE_BOSTON)).findFirst().get());

		//verify the order
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().getCode().equals("SHIPPED")));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity Pending: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityPending());
		assertTrue(order.getEntries().stream().allMatch(e -> ((OrderEntryModel) e).getQuantityPending().equals(Long.valueOf(0L))));
		assertTrue(order.getStatus().getCode().equals("COMPLETED"));
	}

	/**
	 * Given an order with 3 entries:<br>
	 * entry 1 : {quantity: 1, product: product1} <br>
	 * entry 2 : {quantity: 1, product: product2} <br>
	 * entry 3 : {quantity: 7, product: product3} <br>
	 * POS Montreal ->Montreal product1 quantity=6, product2 quantity =6, product3 quantity = 6 POS Boston ->Boston
	 * product1 quantity=6, product2 quantity =6, product3 quantity = 0 POS Toronto ->Toronto product1 quantity=0,
	 * product2 quantity =0, product3 quantity = 6
	 * Result:<br>
	 * It should source complete from 2 location, 6 of product3 from Montreal, and 1 from the Toronto<br>
	 */
	@Test
	public void shouldSourcingSuccess_OMSE_640() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Lens(warehouses.Montreal(), 6);

		stockLevels.Camera(warehouses.Toronto(), 0);
		stockLevels.MemoryCard(warehouses.Toronto(), 0);
		stockLevels.Lens(warehouses.Toronto(), 6);

		stockLevels.Camera(warehouses.Boston(), 6);
		stockLevels.MemoryCard(warehouses.Boston(), 3);
		stockLevels.Lens(warehouses.Boston(), 0);


		order = sourcingUtil
				.createOrder(orders.CameraAndMemoryCardAndLens_Shipped(Long.valueOf(1L), Long.valueOf(1L), Long.valueOf(7L)));
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("READY")));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L),
						Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment(order, LENS_CODE, CODE_TORONTO, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));
		//when confirm shipment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));

		//verify the confirmed shipment
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().getCode().equals("SHIPPED")));
		assertTrue(order.getStatus().getCode().equals("COMPLETED"));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity Pending: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated());
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityPending().equals(Long.valueOf(0L)));

		//then verify the ATP
		assertEquals(Long.valueOf(5), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(5), commerceStockService
				.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Lens(), pointsOfService.Montreal_Downtown()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Verify cannot confirm order twice<br>
	 */
	@Test(expected = WorkflowActionDecideException.class)
	public void shouldNotConfirmTwice() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//when confirm shipment twice
		order.getConsignments().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		sourcingUtil.refreshOrder(order);
		order.getConsignments().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
	}

	@Test
	public void shouldSourcingSuccess_ExternalWarehouse() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Paris(), 6);

		order = sourcingUtil.createCameraShippedOrder();
		sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		//then verify the consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 1);
		assertEquals(order.getConsignments().iterator().next().getWarehouse(), warehouses.Paris());
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_PARIS, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));
	}

	@Test
	public void shouldFailSourcing_ExternalWarehouse() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Paris(), 6);
		BaseStoreModel mybasestore = baseStores.NorthAmerica();
		mybasestore.setDefaultAtpFormula(atpFormulas.customFormula(true, true, true, true, true, true, true, true, false));

		order = sourcingUtil.createCameraShippedOrder();
		sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);
		modelService.refresh(order);
		assertEquals(order.getStatus(), OrderStatus.SUSPENDED);
	}
}
