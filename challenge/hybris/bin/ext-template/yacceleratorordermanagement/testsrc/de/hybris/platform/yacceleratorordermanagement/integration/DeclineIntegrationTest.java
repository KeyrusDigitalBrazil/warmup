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
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.warehousing.data.allocation.DeclineEntries;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.model.SourcingBanModel;
import de.hybris.platform.warehousing.util.DeclineEntryBuilder;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;
import de.hybris.platform.yacceleratorordermanagement.integration.util.WorkflowUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This integration test creates a process and tests the order decline after the consignment is created. Please make
 * sure that you have initialized and update junit tenant before running this test.
 */
@IntegrationTest
public class DeclineIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	private OrderModel order2;
	private Map<ConsignmentEntryModel, Long> declineEntryInfo;
	private Map<ConsignmentEntryModel, Long> declineEntryInfo_2;
	private Map<ConsignmentEntryModel, Long> declineEntryInfoManual;
	private VerifyOrderAndConsignment verifyOrderAndConsignment = new VerifyOrderAndConsignment();
	private static final Logger LOG = LoggerFactory.getLogger(DeclineIntegrationTest.class);

	@Before
	public void setUp() throws Exception
	{
		if (order != null)
		{
			modelService.remove(order);
		}
		declineEntryInfo = new HashMap<>();
		declineEntryInfo_2 = new HashMap<>();
		declineEntryInfoManual = new HashMap<>();
	}

	@After
	public void cleanUp()
	{
		cleanUpData();
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Decline should SUCCESS with new location when to busy and ban first location<br>
	 */

	@Test
	public void shouldAutoReallocate_SingleEntry_SuccessReSourced__ToBusy_BanLocation() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP for Camera
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 0 after fulfilling the previously declined order)
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

		//And verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		//when place another order
		order2 = orders.MemoryCard_Shipped(MEMORY_CARD_QTY);
		order2.setDeliveryMode(deliveryMode);
		modelService.saveAll();

		//verify the location get banned
		sourcingUtil.runOrderProcessForOrderBasedPriority(order2, OrderStatus.SUSPENDED);
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Decline should SUCCESS with new location when to busy and ban first location<br>
	 */

	@Test
	public void shouldManualReallocate_SingleEntry() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.manualDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, warehouses.Boston(),
				DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 0 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);

		//And verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

		//when place another order
		order2 = orders.MemoryCard_Shipped(MEMORY_CARD_QTY);
		order2.setDeliveryMode(deliveryMode);
		modelService.saveAll();

		//verify the location get banned
		sourcingUtil.runOrderProcessForOrderBasedPriority(order2, OrderStatus.SUSPENDED);
	}

	/**
	 * Given an shipping order with 1 entry:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Perform a pack and then perform complete manual-Reallocation on this consignment<br>
	 * Result:<br>
	 * 1) A new Consignment is created, while the old consignment is cancelled<br>
	 * 2) Task Assignment workflow for the older consingment is terminated and a new workflow is assigned to the new consignment<br>
	 */
	@Test
	public void shouldManualReallocateAndReassignNewTaskAssignmentWorkFlowAfterPacking() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PICKING_TEMPLATE_CODE);
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PACKING_TEMPLATE_CODE);
		assertEquals(ConsignmentStatus.READY_FOR_SHIPPING, consignmentResult.getStatus());

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.manualDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, warehouses.Boston(),
				DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 0 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment and the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));
	}

	/**
	 * Given an shipping order with 1 entry:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Perform a pack and then perform partial manual-Reallocation on this consignment<br>
	 * Result:<br>
	 * 1) A new Consignment is created, while the old consignment status is moved back to {@link ConsignmentStatus#READY} from {@link ConsignmentStatus#READY_FOR_SHIPPING}<br>
	 * 2) Task Assignment workflow for the older consingment is terminated and a new workflow is assigned to it.<br>
	 * 2) Also a new Task Assignment workflow is assigned to the new consignment.<br>
	 */
	@Test
	public void shouldManualReallocatePartiallyAndReassignNewTaskAssignmentWorkFlowAfterPacking() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PICKING_TEMPLATE_CODE);
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PACKING_TEMPLATE_CODE);
		assertEquals(ConsignmentStatus.READY_FOR_SHIPPING, consignmentResult.getStatus());

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.manualDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, warehouses.Boston(),
				DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 2 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment and the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 2, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(1L), Long.valueOf(2L), Long.valueOf(2L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));
	}

	/**
	 * Given an shipping order with 1 entry:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Perform a pack and perform complete auto-Reallocation on this consignment<br>
	 * Result:<br>
	 * 1) A new Consignment is created, while the old consignment is cancelled<br>
	 * 2) Task Assignment workflow for the older consingment is terminated and a new workflow is assigned to the new consignment<br>
	 */
	@Test
	public void shouldAutoReallocateAndReassignNewTaskAssignmentWorkFlowAfterPacking() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PICKING_TEMPLATE_CODE);
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PACKING_TEMPLATE_CODE);
		assertEquals(ConsignmentStatus.READY_FOR_SHIPPING, consignmentResult.getStatus());

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 0 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment and the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));
	}

	/**
	 * Given an shipping order with 1 entry:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Perform a pack and then perform partial auto-Reallocation on this consignment<br>
	 * Result:<br>
	 * 1) A new Consignment is created, while the old consignment status is moved back to {@link ConsignmentStatus#READY} from {@link ConsignmentStatus#READY_FOR_SHIPPING}<br>
	 * 2) Task Assignment workflow for the older consingment is terminated and a new workflow is assigned to it.<br>
	 * 2) Also a new Task Assignment workflow is assigned to the new consignment.<br>
	 */
	@Test
	public void shouldAutoReallocatePartiallyAndReassignNewTaskAssignmentWorkFlowAfterPacking() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PICKING_TEMPLATE_CODE);
		workflowUtil.moveConsignmentWorkflow(orderProcessModel, consignmentResult, WorkflowUtil.PACKING_TEMPLATE_CODE);
		assertEquals(ConsignmentStatus.READY_FOR_SHIPPING, consignmentResult.getStatus());

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 2 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment and the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 2, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(1L), Long.valueOf(2L), Long.valueOf(2L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Decline should fail, and confirm the consignment<br>
	 */
	@Test
	public void shouldAutoReallocate_SingleEntry_PartiallyFailReSourced() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP - Global and pickup ATP should be 0, since Montreal Warehouse is banned
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 1);

		//And verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(1L), Long.valueOf(2L), Long.valueOf(2L)));

		//confirm all the consignment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		assertTrue(order.getConsignments().stream().anyMatch(result -> result.getStatus().equals(ConsignmentStatus.SHIPPED)));
		assertTrue(order.getStatus().equals(OrderStatus.SUSPENDED));
		sourcingUtil.refreshOrder(order);
		LOG.info("Quantity unallocated: " + ((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated());
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated().equals(Long.valueOf(1L)));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Decline should SUCCESS with new location when to out of stock and reset inventory<br>
	 */
	@Test
	public void shouldAutoReallocate_SingleEntry_FailReSourced__OutOfStock_ResetInventory() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the new consignment
		modelService.refresh(order);
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated().equals(Long.valueOf(2L)));
		assertEquals(order.getConsignments().size(), 2);

		//And verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.SUSPENDED, timeOut);

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));

		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Decline should SUCCESS with new location when partially reallocated<br>
	 */
	@Test
	public void shouldAutoReallocate_SingleEntry_PartiallyReSourcingSuccess() throws InterruptedException
	{
		//Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		Collection<ConsignmentModel> consignmentModels = order.getConsignments();
		LOG.info("Number of consignments: " + consignmentModels.size());
		assertEquals(1, consignmentModels.size());

		// When create consignment
		ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		LOG.info("Stock added in boston warehouse for the camera");
		stockLevels.Camera(warehouses.Boston(), CAMERA_QTY.intValue());
		modelService.saveAll();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(2L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);

		//And verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 2, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(2L), Long.valueOf(1L), Long.valueOf(1L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(2L)));
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		//then verify the ATP
		// Global ATP should be 1, since Montreal Warehouse is banned and Boston's availability is 1 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	/**
	 * Given an shipping order with 3 entries:<br>
	 * DeclineEntry 1 : auto<br>
	 * DeclineEntry 2 : manual<br>
	 * Result:<br>
	 * Decline should SUCCESS<br>
	 */
	@Test
	public void shouldAutoReallocate_MixManualAuto_ReSourcingFailed() throws InterruptedException
	{
		//Given
		LOG.info("Stock added in Montreal warehouse for the camera, and Toronto Warehouse for memoryCard and Lens");
		stockLevels.Camera(warehouses.Toronto(), CAMERA_QTY.intValue());
		stockLevels.MemoryCard(warehouses.Toronto(), MEMORY_CARD_QTY.intValue());
		stockLevels.Lens(warehouses.Toronto(), LENS_QTY.intValue());

		order = orders.CameraAndMemoryCardAndLens_Shipped(CAMERA_QTY, MEMORY_CARD_QTY, LENS_QTY);
		order.setDeliveryMode(deliveryMode);
		modelService.saveAll();

		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		Collection<ConsignmentModel> consignmentModels = order.getConsignments();
		LOG.info("Number of consignments: " + consignmentModels.size());
		assertEquals(1, consignmentModels.size());

		Collection<ConsignmentEntryModel> consignmentEntries = consignmentModels.stream()
				.flatMap(cons -> cons.getConsignmentEntries().stream()).collect(Collectors.toList());
		LOG.info("Number of consignment entries: " + consignmentEntries.size());

		LOG.info("Stock added in boston warehouse for the camera, memoryCard and Lens");
		stockLevels.Camera(warehouses.Boston(), CAMERA_QTY.intValue());
		stockLevels.MemoryCard(warehouses.Boston(), MEMORY_CARD_QTY.intValue());
		stockLevels.Lens(warehouses.Boston(), LENS_QTY.intValue());
		modelService.saveAll();

		ConsignmentEntryModel cameraConsEntryModel = consignmentEntries.stream()
				.filter(consEntry -> consEntry.getOrderEntry().getProduct().getCode().equals(CAMERA_CODE)).findFirst().get();
		ConsignmentEntryModel memoryCardConsEntryModel = consignmentEntries.stream()
				.filter(consEntry -> consEntry.getOrderEntry().getProduct().getCode().equals(MEMORY_CARD_CODE)).findFirst().get();
		ConsignmentEntryModel lensConsEntryModel = consignmentEntries.stream()
				.filter(consEntry -> consEntry.getOrderEntry().getProduct().getCode().equals(LENS_CODE)).findFirst().get();

		declineEntryInfoManual.put(cameraConsEntryModel, CAMERA_QTY);
		declineEntryInfo.put(memoryCardConsEntryModel, MEMORY_CARD_QTY);
		declineEntryInfo.put(lensConsEntryModel, LENS_QTY);

		DeclineEntries manualEntries = DeclineEntryBuilder.aDecline().build_Manual(declineEntryInfoManual, warehouses.Boston());
		DeclineEntries autoEntries = DeclineEntryBuilder.aDecline().build_Auto(declineEntryInfo);

		DeclineEntries manualAndAuto = new DeclineEntries();
		manualAndAuto.setEntries(
				Stream.concat(manualEntries.getEntries().stream(), autoEntries.getEntries().stream()).collect(Collectors.toList()));

		ConsignmentModel cons = consignmentModels.iterator().next();

		ConsignmentProcessModel consignmentProcess = cons.getConsignmentProcesses().stream()
				.filter(process -> process.getConsignment().equals(cons)).findAny().get();
		BusinessProcessParameterModel declineParam = new BusinessProcessParameterModel();
		declineParam.setName(DECLINE_ENTRIES);
		//declineParam.setValue(DeclineEntryBuilder.aDecline().build_Auto(declineEntryInfo));
		declineParam.setValue(manualAndAuto);
		declineParam.setProcess(consignmentProcess);
		consignmentProcess.setContextParameters(Collections.singleton(declineParam));
		modelService.save(consignmentProcess);

		//when decline the order
		sourcingUtil.getConsignmentBusinessProcessService()
				.triggerChoiceEvent(cons, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.READY, timeOut);

		//Then Verify Order
		assertEquals(3, order.getConsignments().size());
		assertTrue(order.getStatus().equals(OrderStatus.READY));

		//Verify Consignments and verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 2, 1);
		modelService.remove(order);
	}


	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * Decline should SUCCESS without sufficient stock twice<br>
	 */
	@Test
	public void shouldAutoDecline_SuccessReSourcedTwice() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		Collection<ConsignmentModel> consignmentResult = order.getConsignments();
		final ConsignmentModel oldCons = consignmentResult.iterator().next();
		assertEquals(CODE_MONTREAL, oldCons.getWarehouse().getCode());

		//Then verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);

		//When decline the order
		declineEntryInfo.put(oldCons.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(oldCons, declineEntryInfo, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);


		//Then Verify Consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		// When decline second time
		Collection<ConsignmentModel> consignmentResult2 = order.getConsignments();
		ConsignmentModel newCons = consignmentResult2.stream().filter(cons -> !cons.equals(oldCons)).findFirst().get();
		LOG.info("Stock added in Montreal warehouse for the camera");
		//this will only update the stock level table, use 11 to balance ATP to 5
		stockService.updateActualStockLevel(products.Camera(), warehouses.Montreal(), 11, "");
		declineEntryInfo_2.put(newCons.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(newCons, declineEntryInfo_2, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(2), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 3);

		//Then Verify Consignment
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 2, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * Result:<br>
	 * partially decline same consignment twice<br>
	 */
	@Test
	public void shouldAutoDecline_PartiallyDeclineTwice() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		Collection<ConsignmentModel> consignmentResult = order.getConsignments();
		ConsignmentModel oldCons = consignmentResult.iterator().next();

		//when decline the order
		declineEntryInfo.put(oldCons.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(oldCons, declineEntryInfo, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//Then Verify Consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);

		// When decline second time
		Collection<ConsignmentModel> consignmentResult2 = order.getConsignments();
		ConsignmentModel newCons = consignmentResult2.stream().findFirst().get();
		LOG.info("Stock added in Montreal warehouse for the camera");
		//this will only update the stock level table, use 11 to balance ATP to 5
		stockService.updateActualStockLevel(products.Camera(), warehouses.Montreal(), 11, "");
		declineEntryInfo_2.put(newCons.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(newCons, declineEntryInfo_2, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//And verify the consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(2L), Long.valueOf(1L), Long.valueOf(1L)));

		//confirm all the consignment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.SHIPPED)));
		assertTrue(order.getStatus().equals(OrderStatus.SUSPENDED));
		sourcingUtil.refreshOrder(order);
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated().equals(Long.valueOf(2L)));
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 2, product: memoryCard}<br>
	 * Result:<br>
	 * Decline should SUCCESS<br>
	 */
	@Test
	public void shouldAutoDecline2Entries_PartiallySuccessReSourced() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 4);
		stockLevels.MemoryCard(warehouses.Montreal(), 5);
		stockLevels.MemoryCard(warehouses.Boston(), 4);

		// And placing order
		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		Collection<ConsignmentModel> consignmentResult = order.getConsignments();
		ConsignmentModel cons = consignmentResult.iterator().next();

		//When decline the order
		declineEntryInfo.put(cons.getConsignmentEntries().stream().collect(Collectors.toList()).get(0), Long.valueOf(2L));
		declineEntryInfo.put(cons.getConsignmentEntries().stream().collect(Collectors.toList()).get(1), Long.valueOf(2L));

		declineUtil.autoDeclineDefaultConsignment(cons, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//Then Verify Consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 2, 1);

		//then verify the ATP
		// Camera: Global ATP should be 2, since Montreal Warehouse is banned and Boston's availability is 1 after fulfilling the previously declined order.
		// MemoryCard: Global ATP should be 2, since Montreal Warehouse is banned and Boston's availability is 2.
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.MemoryCard(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Boston()));


		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(2L), Long.valueOf(1L), Long.valueOf(1L),
						Long.valueOf(2L), Long.valueOf(0L), Long.valueOf(0L)).booleanValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(2L),
						Long.valueOf(0L), Long.valueOf(2L), Long.valueOf(2L)).booleanValue());
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 2, product: memoryCard}<br>
	 * Result:<br>
	 * Decline should SUCCESS, and confirm order<br>
	 */
	@Test
	public void shouldAutoDecline1Entry_MultiEntries_SuccessReSourced() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.Camera(warehouses.Boston(), 4);
		stockLevels.MemoryCard(warehouses.Montreal(), 5);
		stockLevels.MemoryCard(warehouses.Boston(), 4);

		// When create consignment
		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult_Montreal = order.getConsignments().stream()
				.filter(e -> CODE_MONTREAL.equals(e.getWarehouse().getCode())).findFirst().get();

		final ConsignmentModel cons = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo
				.put(cons.getConsignmentEntries().stream().filter(e -> e.getQuantity().equals(Long.valueOf(3L))).findFirst().get(),
						CAMERA_QTY);


		declineUtil.autoDeclineDefaultConsignment(cons, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Montreal, timeOut);
		sourcingUtil.waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
		modelService.refresh(order);
		final ConsignmentModel consignmentResult_Boston = order.getConsignments().stream()
				.filter(e -> e.getWarehouse().getCode().equals(CODE_BOSTON)).findFirst().get();
		sourcingUtil.waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult_Boston, timeOut);

		//then verify the ATP
		// Camera: Global ATP should be 1, since Montreal Warehouse is banned and Boston's availability is 1 after fulfilling the previously declined order.
		// MemoryCard: Global ATP should be 4, since Montreal Warehouse is banned and Boston's availability is 4.
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(1),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndBaseStore(products.MemoryCard(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(4),
				commerceStockService.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Boston()));

		//then verify new consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 2, 1);
		order.getConsignments().forEach(this::refreshConsignmentAndEntries);

		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(3L), Long.valueOf(0L), Long.valueOf(0L),
						Long.valueOf(0L), MEMORY_CARD_QTY, MEMORY_CARD_QTY).booleanValue());
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(3L), Long.valueOf(3L)).booleanValue());

		//confirm all the consignment
		order.getConsignments().stream().forEach(e -> sourcingUtil.confirmDefaultConsignment(orderProcessModel, e));
		assertTrue(order.getConsignments().stream().allMatch(result -> result.getStatus().equals(ConsignmentStatus.SHIPPED)));
		assertTrue(order.getStatus().equals(OrderStatus.COMPLETED));
	}

	/**
	 * Given an shipping order with 2 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * entry 2 : {quantity: 2, product: memoryCard}<br>
	 * Result:<br>
	 * Decline should Fail<br>
	 */
	@Test
	public void shouldAutoDecline1Entry_MultiEntries_FailReSourced() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 5);
		stockLevels.MemoryCard(warehouses.Montreal(), 5);

		// When create consignment
		order = sourcingUtil.createCameraAndMemoryCardShippingOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		Collection<ConsignmentModel> consignmentResult = order.getConsignments();
		ConsignmentModel cons = consignmentResult.iterator().next();

		//when decline the order
		declineEntryInfo
				.put(cons.getConsignmentEntries().stream().filter(e -> e.getQuantity().equals(Long.valueOf(3L))).findFirst().get(),
						CAMERA_QTY);

		declineUtil.autoDeclineDefaultConsignment(cons, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		// Camera: Global ATP should be 0, since Montreal Warehouse is banned.
		// MemoryCard: Global ATP should be 0, since Montreal Warehouse is banned.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.MemoryCard(), pointsOfService.Montreal_Downtown()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.MemoryCard(), baseStores.NorthAmerica()));

		//then verify new consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);
		order.getConsignments().forEach(this::refreshConsignmentAndEntries);
		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera_MemoryCard(order, CODE_MONTREAL, Long.valueOf(3L), Long.valueOf(0L), Long.valueOf(0L),
						Long.valueOf(0L), MEMORY_CARD_QTY, MEMORY_CARD_QTY).booleanValue());
		assertTrue(order.getStatus().equals(OrderStatus.SUSPENDED));
		modelService.refresh(order.getConsignments().iterator().next().getConsignmentEntries().iterator().next());
		modelService
				.refresh(order.getEntries().stream().filter(e -> e.getProduct().getCode().equals(CAMERA_CODE)).findFirst().get());
		assertTrue(
				((OrderEntryModel) order.getEntries().stream().filter(e -> e.getProduct().getCode().equals(CAMERA_CODE)).findFirst()
						.get()).getQuantityUnallocated().equals(Long.valueOf(3L)));
	}

	@Test
	public void shouldAutoReallocate_SingleEntry_FailReSourced__Damaged_ResetInventory() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 1);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.DAMAGED);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the new consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);
		assertTrue(((OrderEntryModel) order.getEntries().iterator().next()).getQuantityUnallocated().equals(Long.valueOf(2L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), Long.valueOf(1L), Long.valueOf(1L)));
		sourcingUtil.waitForOrderStatus(orderProcessModel, order, OrderStatus.SUSPENDED, timeOut);

		//then verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));

		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

	}

	/**
	 * Given an shipping order from USA with entry {quantity: 3, product: camera}<br>
	 * StockLevels :
	 * Montreal Warehouse(External) : {quantity: 6, product: camera}<br>
	 * Boston Warehouse(Internal) : {quantity: 3, product: camera}<br>
	 * The order gets sourced from Montreal Warehouse and now the consignment is declined with reason DAMAGED
	 * Result:<br>
	 * The order gets resourced from Boston and the Montreal warehouse is banned for 1 day<br>
	 */
	@Test
	public void shouldManualReallocate_DamagedEntry_BanExternalWarehouse() throws InterruptedException
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
		modelService.refresh(order);

		// Verify consignment is allocated from the external warehouse
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();
		assertEquals(externalMontrealWarehouse.getCode(), consignmentResult.getWarehouse().getCode());

		//then verify the ATP
		//ATP should remain same as NO allocation event were created for the external warehouse
		assertEquals(Long.valueOf(9),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(3),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.manualDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, warehouses.Boston(),
				DeclineReason.DAMAGED);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//then verify the ATP
		// Global should be 0, since External Montreal Warehouse is banned and Boston's availability is 0 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_External()));

		//then verify the new consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 1, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL_EXTERNAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

		//verify the location get banned
		final Collection<SourcingBanModel> sourcingBans = sourcingBanService
				.getSourcingBan(Collections.singletonList(externalMontrealWarehouse));
		assertEquals(Boolean.TRUE, CollectionUtils.isNotEmpty(sourcingBans));
	}

	/**
	 * Given a shipping order from USA with entry {quantity: 3, product: camera}<br>
	 * StockLevels :
	 * Montreal Warehouse: {quantity: 6, product: camera}<br>
	 * The order gets sourced from Montreal Warehouse and now the consignment is declined with reason TOOBUSY
	 * Result:<br>
	 * The Montreal warehouse is banned and the ATP value become to 0<br>
	 */
	@Test
	public void shouldExcludeBanWarehouseDuringATPCalculation() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);

		//Verify ATP
		assertEquals(Long.valueOf(6),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(6), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//create consignment
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		assertEquals(1, order.getConsignments().size());
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//When decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		modelService.refresh(order);

		//Then Verify consignment and its task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 0, 1, 1);

		//And verify the location get banned
		final Collection<SourcingBanModel> sourcingBans = sourcingBanService
				.getSourcingBan(Collections.singletonList(warehouses.Montreal()));
		assertEquals(1, sourcingBans.size());
		assertEquals(warehouses.Montreal(), sourcingBans.iterator().next().getWarehouse());

		//And verify the ATP
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	/**
	 * Given an shipping order with 1 entries:<br>
	 * entry 1 : {quantity: 3, product: camera}<br>
	 * The consignment is auto reallocated automatically with partial quantity 2
	 * Then consignment is auto reallocated again with left over quantity 1
	 * Result:<br>
	 * Decline should SUCCESS for both auto reallocation actions<br>
	 */
	@Test
	public void shouldSuccessfullyAutoReallocateTwice() throws InterruptedException
	{
		// Given
		stockLevels.Camera(warehouses.Montreal(), 6);
		stockLevels.MemoryCard(warehouses.Montreal(), 6);
		stockLevels.Camera(warehouses.Boston(), 3);
		order = sourcingUtil.createCameraShippedOrder();
		OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);

		// When create consignment
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when auto reallocate partially the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY - LEFTOVER_QTY);
		//AutoDecline with 2 params in the BPP
		declineUtil
				.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY, true);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//Then perform another auto reallocate with the left over quantity
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), LEFTOVER_QTY);
		declineUtil
				.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		//then verify the ATP
		// Global ATP should be 0, since Montreal Warehouse is banned and Boston's availability is 0 after fulfilling the previously declined order.
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Boston()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 3);

		//And verify the task assignment workflow
		sourcingUtil.validateConsignmentsAndTaskWorkflow(order, 1, 2, 1);

		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_MONTREAL, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertEquals(Boolean.TRUE, verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY - LEFTOVER_QTY,
						CAMERA_QTY - LEFTOVER_QTY));
	}
}
