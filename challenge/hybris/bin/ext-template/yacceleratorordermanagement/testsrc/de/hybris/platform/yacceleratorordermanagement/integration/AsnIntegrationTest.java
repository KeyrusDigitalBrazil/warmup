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


import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.warehousing.asn.service.AsnService;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousing.util.VerifyOrderAndConsignment;
import de.hybris.platform.warehousing.util.models.Asns;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AsnIntegrationTest extends BaseAcceleratorSourcingIntegrationTest
{
	@Resource
	private Asns asns;
	@Resource
	private AsnService asnService;

	private AdvancedShippingNoticeModel advancedShippingNotice;
	private AdvancedShippingNoticeModel advancedShippingNotice2;
	private Map<ConsignmentEntryModel, Long> declineEntryInfo;
	private VerifyOrderAndConsignment verifyOrderAndConsignment = new VerifyOrderAndConsignment();

	@Before
	public void setUp()
	{
		declineEntryInfo = new HashMap<ConsignmentEntryModel, Long>();
	}

	@After
	public void cleanUp()
	{
		cleanUpData();
		cleanUpModel("AdvancedShippingNotice");
	}

	@Test
	public void testOrderSourcingFromAsnStock() throws InterruptedException
	{
		//Given
		createDefaultAsn();
		//When
		order = sourcingUtil.createCameraShippedOrder();
		sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		//Then
		assertEquals(0L,
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()).longValue());
		assertEquals(Boolean.TRUE,
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_BOSTON, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

	}

	@Test
	public void testDeclineAfterSourceFromAsn_ThenCancelAsn() throws InterruptedException
	{
		//Given
		createDefaultAsn();
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		stockLevels.Camera(warehouses.Montreal(), 4);
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), CAMERA_QTY);
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.TOOBUSY);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));
		//Then verify the atp
		assertEquals(1L,
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()).longValue());
		//then verify the new consignment
		modelService.refresh(order);
		assertEquals(order.getConsignments().size(), 2);

		assertTrue(verifyOrderAndConsignment
				.verifyConsignment_Camera(order, CODE_BOSTON, CAMERA_QTY, Long.valueOf(0L), Long.valueOf(0L)));
		assertTrue(
				verifyOrderAndConsignment.verifyConsignment_Camera(order, CODE_MONTREAL, Long.valueOf(0L), CAMERA_QTY, CAMERA_QTY));

		//When Cancel Asn
		asnService.cancelAsn(Asns.INTERNAL_ID_CAMERA_BOSTON);

		//Then verify consignment
		modelService.refresh(order);
		assertEquals(OrderStatus.READY, order.getStatus());
	}

	@Test
	public void testSourcingFail_CancelledAsn() throws InterruptedException
	{
		//Given
		createDefaultAsn();
		asnService.cancelAsn(Asns.INTERNAL_ID_CAMERA_BOSTON);
		//When
		order = sourcingUtil.createCameraShippedOrder();
		//Then
		sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.SUSPENDED);
		assertEquals(0L,
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()).longValue());
	}

	@Test
	public void testAutoReallocate_SingleEntry_OutOfStock_AsnCreated_AllocatedToRegularStock() throws InterruptedException
	{
		// Given
		//create asn stock level in montreal with quantity 3
		advancedShippingNotice = asns.CameraAsn_Montreal();
		asnService.processAsn(advancedShippingNotice);

		//create stock levels for montreal with quantity 6
		stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), 6, null);

		// When create order & consignment
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP - Global and pickup ATP should be 2, since Montreal Warehouse is banned
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(2), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	@Test
	public void testAutoReallocate_SingleEntry_Damaged_AsnCreated_AllocatedToRegularStock() throws InterruptedException
	{
		// Given
		//create asn stock level in montreal with quantity 3
		advancedShippingNotice = asns.CameraAsn_Montreal();
		asnService.processAsn(advancedShippingNotice);

		//create stock levels for montreal with quantity 6
		stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), 6, null);

		// When create order & consignment
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.DAMAGED);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP - Global and pickup ATP should be 2, since Montreal Warehouse is banned
		assertEquals(Long.valueOf(2),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(2), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	@Test
	public void testAutoReallocate_SingleEntry_OutOfStock_AsnReceived_AllocatedToRegularStock() throws InterruptedException
	{
		// Given
		//create asn stock level in montreal with quantity 3
		advancedShippingNotice = asns.CameraAsn_Montreal();
		asnService.processAsn(advancedShippingNotice);
		advancedShippingNotice.setStatus(AsnStatus.RECEIVED);
		modelService.save(advancedShippingNotice);

		//create stock levels for montreal with quantity 6
		stockLevels.NewStockLevel(products.Camera(), warehouses.Montreal(), 6, null);

		// When create order & consignment
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.OUTOFSTOCK);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP - Global and pickup ATP should be 0, since Montreal Warehouse is banned
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));
	}

	@Test
	//TODO OMSE-2811
	public void testAutoReallocate_SingleEntry_OutOfStock_AsnCreated_AllocatedToAsn() throws InterruptedException
	{
		// Given
		//create asn stock level in montreal with quantity 3
		advancedShippingNotice = asns.CameraAsn_Montreal();
		asnService.processAsn(advancedShippingNotice);

		// When create order & consignment
		order = sourcingUtil.createCameraShippedOrder();
		final OrderProcessModel orderProcessModel = sourcingUtil.runOrderProcessForOrderBasedPriority(order, OrderStatus.READY);
		final ConsignmentModel consignmentResult = order.getConsignments().iterator().next();

		//when decline the order
		declineEntryInfo.put(consignmentResult.getConsignmentEntries().stream().findFirst().get(), Long.valueOf(1L));
		declineUtil.autoDeclineDefaultConsignment(consignmentResult, declineEntryInfo, orderProcessModel, DeclineReason.DAMAGED);
		assertTrue(ProcessState.WAITING.equals(orderProcessModel.getProcessState()));

		//then verify the ATP - Global and pickup ATP should be 0, since Montreal Warehouse is banned
		assertEquals(Long.valueOf(0),
				commerceStockService.getStockLevelForProductAndBaseStore(products.Camera(), baseStores.NorthAmerica()));
		assertEquals(Long.valueOf(0), commerceStockService
				.getStockLevelForProductAndPointOfService(products.Camera(), pointsOfService.Montreal_Downtown()));

		//then verify consignment sourced to same location
		modelService.refresh(order.getStatus());
		modelService.refresh(consignmentResult.getStatus());
		assertTrue(!order.getStatus().equals(OrderStatus.SUSPENDED));
		assertTrue(consignmentResult.getStatus().equals(ConsignmentStatus.READY));
	}

	/**
	 * create default asn for boston with camera quantity 3
	 */
	protected void createDefaultAsn()
	{
		advancedShippingNotice = asns.CameraAsn_Boston();
		asnService.processAsn(advancedShippingNotice);
	}
}
