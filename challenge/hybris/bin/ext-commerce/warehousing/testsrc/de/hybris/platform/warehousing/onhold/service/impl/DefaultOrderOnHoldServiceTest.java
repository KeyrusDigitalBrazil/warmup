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
package de.hybris.platform.warehousing.onhold.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderOnHoldServiceTest
{
	protected static final String HEADSET_CODE = "headset";
	protected static final String CONS_0_CODE = "cons_0";
	protected static final String CONS_1_CODE = "cons_1";
	protected static final Long ORDER_ENTRY_QUANTITY = 5L;
	protected static final Long CONS_ENTRY_1_QUANTITY = 3L;
	protected static final Long CONS_ENTRY_2_QUANTITY = 2L;

	@InjectMocks
	private DefaultOrderOnHoldService orderOnHoldService;

	@Mock
	private ModelService modelService;
	@Mock
	private WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessPorcessService;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private InventoryEventService inventoryEventService;

	private List<ConsignmentStatus> nonCancellableConsignmentStatus;
	private OrderModel order;
	private ProductModel headset;
	private OrderEntryModel headsetEntry;
	private ConsignmentModel consignment1;
	private ConsignmentModel consignment2;
	private ConsignmentEntryModel consHeadsetEntry1;
	private ConsignmentEntryModel consHeadsetEntry2;

	@Before
	public void setup()
	{
		nonCancellableConsignmentStatus = Arrays
				.asList(ConsignmentStatus.CANCELLED, ConsignmentStatus.PICKUP_COMPLETE, ConsignmentStatus.SHIPPED);
		orderOnHoldService.setNonCancellableConsignmentStatus(nonCancellableConsignmentStatus);

		order = new OrderModel();
		order.setStatus(OrderStatus.READY);
		headset = new ProductModel();
		headset.setCode(HEADSET_CODE);
		headsetEntry = new OrderEntryModel();
		headsetEntry.setProduct(headset);
		headsetEntry.setQuantity(ORDER_ENTRY_QUANTITY);

		consHeadsetEntry1 = new ConsignmentEntryModel();
		consHeadsetEntry1.setOrderEntry(headsetEntry);
		consHeadsetEntry1.setConsignment(consignment1);
		consHeadsetEntry1.setQuantity(CONS_ENTRY_1_QUANTITY);
		headsetEntry.setConsignmentEntries(Sets.newHashSet(consHeadsetEntry1));

		consignment1 = new ConsignmentModel();
		consignment1.setCode(CONS_0_CODE);
		consignment1.setOrder(order);
		consignment1.setStatus(ConsignmentStatus.READY);
		order.setConsignments(Sets.newHashSet(consignment1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutOnHoldWhenOrderIsNull()
	{
		orderOnHoldService.processOrderOnHold(null);
	}

	@Test
	public void testPutOnHoldSuccessWithOneConsignment()
	{
		orderOnHoldService.processOrderOnHold(order);

		assertTrue(ConsignmentStatus.CANCELLED.equals(order.getConsignments().iterator().next().getStatus()));
	}

	@Test
	public void testPutOnHoldSuccessWhenOneConsignmentIsAlreadyShipped()
	{
		//Given
		consHeadsetEntry2 = new ConsignmentEntryModel();
		consHeadsetEntry2.setOrderEntry(headsetEntry);
		consHeadsetEntry2.setConsignment(consignment2);
		consHeadsetEntry2.setQuantity(CONS_ENTRY_2_QUANTITY);
		headsetEntry.setConsignmentEntries(Sets.newHashSet(consHeadsetEntry2));

		consignment2 = new ConsignmentModel();
		consignment2.setCode(CONS_1_CODE);
		consignment2.setOrder(order);
		consignment2.setStatus(ConsignmentStatus.SHIPPED);
		order.setConsignments(Sets.newHashSet(consignment1, consignment2));

		//When
		orderOnHoldService.processOrderOnHold(order);

		//Then
		assertTrue(ConsignmentStatus.CANCELLED.equals(
				order.getConsignments().stream().filter(consignment -> consignment.getCode().equals(CONS_0_CODE)).findFirst().get()
						.getStatus()));
		assertTrue(ConsignmentStatus.SHIPPED.equals(
				order.getConsignments().stream().filter(consignment -> consignment.getCode().equals(CONS_1_CODE)).findFirst().get()
						.getStatus()));
	}

	@Test
	public void testPutOnHoldWhenOrderIsNotCancellable()
	{
		//Given
		consignment1.setStatus(ConsignmentStatus.SHIPPED);

		//When
		orderOnHoldService.processOrderOnHold(order);

		//Then
		assertTrue(ConsignmentStatus.SHIPPED.equals(order.getConsignments().iterator().next().getStatus()));
	}
}
