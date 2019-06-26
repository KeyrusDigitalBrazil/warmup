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
package de.hybris.platform.warehousing.cancellation.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.comment.WarehousingCommentService;
import de.hybris.platform.warehousing.data.comment.WarehousingCommentContext;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.CancellationEventModel;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConsignmentCancellationServiceTest
{
	@InjectMocks
	private final DefaultConsignmentCancellationService cancellationService = new DefaultConsignmentCancellationService();

	@Mock
	private InventoryEventService inventoryEventService;
	@Mock
	private ModelService modelService;
	@Mock
	private WarehousingCommentService consignmentEntryCommentService;
	@Mock
	private WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessProcessService;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private OrderCancelResponse orderCancelResponse;
	@Mock
	private ProductModel mouse;
	@Mock
	private ProductModel batteries;
	@Mock
	private UserModel author;
	@Spy
	private ConsignmentModel consignment1;
	@Spy
	private ConsignmentModel consignment2;
	@Spy
	private ConsignmentEntryModel consignmentEntry1;
	@Spy
	private ConsignmentEntryModel consignmentEntry2;
	@Spy
	private ConsignmentEntryModel consignmentEntry3;
	@Spy
	private ConsignmentEntryModel consignmentEntry4;
	@Spy
	private WarehouseModel warehouse;
	@Spy
	private AbstractOrderEntryModel orderEntry1;
	@Spy
	private AbstractOrderEntryModel orderEntry2;
	private OrderModel order;
	private CancellationEventModel consignmentCancellationEvent;
	private CancellationEventModel orderCancellationEvent;
	private GuidKeyGenerator guidKeyGenerator;

	@Before
	public void setUp()
	{
		order = new OrderModel();
		orderEntry1.setEntryNumber(1);
		orderEntry1.setQuantity(20L);
		orderEntry1.setOrder(order);
		orderEntry2.setEntryNumber(2);
		orderEntry2.setQuantity(10L);
		orderEntry2.setOrder(order);
		order.setEntries(Lists.newArrayList(orderEntry1, orderEntry2));
		order.setUser(author);

		warehouse.setExternal(true);

		consignment1.setCode("consignment1234_1");
		consignment1.setWarehouse(warehouse);
		consignmentEntry1.setOrderEntry(orderEntry1);
		consignmentEntry1.setQuantity(10L);
		consignmentEntry1.setConsignment(consignment1);

		consignmentEntry2.setOrderEntry(orderEntry2);
		consignmentEntry2.setQuantity(5L);
		consignmentEntry2.setConsignment(consignment1);
		consignment1.setConsignmentEntries(Sets.newHashSet(consignmentEntry1, consignmentEntry2));
		orderEntry1.setConsignmentEntries(Sets.newHashSet(consignmentEntry1, consignmentEntry2));

		consignment2 = new ConsignmentModel();
		consignment2.setCode("consignment1234_2");
		consignmentEntry3.setOrderEntry(orderEntry1);
		consignmentEntry3.setQuantity(10L);

		consignmentEntry4.setOrderEntry(orderEntry2);
		consignmentEntry4.setQuantity(5L);
		consignment2.setConsignmentEntries(Sets.newHashSet(consignmentEntry3, consignmentEntry4));
		orderEntry2.setConsignmentEntries(Sets.newHashSet(consignmentEntry3, consignmentEntry4));

		order.setConsignments(Sets.newHashSet(consignment1, consignment2));
		consignmentCancellationEvent = new CancellationEventModel();
		when(modelService.create(CancellationEventModel.class)).thenReturn(consignmentCancellationEvent);
		orderCancellationEvent = new CancellationEventModel();
		when(modelService.create(CancellationEventModel.class)).thenReturn(orderCancellationEvent);

		when(orderEntry1.getProduct()).thenReturn(mouse);
		when(mouse.getName()).thenReturn("Wireless Mouse");
		when(orderEntry2.getProduct()).thenReturn(batteries);
		when(batteries.getName()).thenReturn("Rechargeable batteries");

		guidKeyGenerator = new GuidKeyGenerator();
		cancellationService.setGuidKeyGenerator(guidKeyGenerator);
		cancellationService.setNonCancellableConsignmentStatus(
				Lists.newArrayList(ConsignmentStatus.CANCELLED, ConsignmentStatus.PICKUP_COMPLETE, ConsignmentStatus.SHIPPED));
		cancellationService.setConsignmentBusinessProcessService(consignmentBusinessProcessService);
		cancellationService.setWarehousingConsignmentWorkflowService(warehousingConsignmentWorkflowService);
	}

	@Test
	public void shouldCancelConsignment()
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L, "notes", CancelReason.LATEDELIVERY);
		when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry));
		when(consignmentEntry1.getQuantityPending()).thenReturn(10L);
		when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);

		// When
		cancellationService.cancelConsignment(consignment1, orderCancelResponse);

		// Then
		verify(consignmentEntryCommentService).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
	}

	@Test
	public void shouldCancelConsignment_InternalWarehouse()
	{
		// Given
		warehouse.setExternal(false);
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L, "notes", CancelReason.LATEDELIVERY);
		when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry));
		when(consignmentEntry1.getQuantityPending()).thenReturn(10L);
		when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);

		// When
		cancellationService.cancelConsignment(consignment1, orderCancelResponse);

		// Then
		verify(consignmentEntryCommentService).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
	}

	@Test
	public void shouldProcessConsignmentCancellation_MultipleCancellations()
	{
		// Given
		final OrderCancelEntry cancellationEntry1 = new OrderCancelEntry(orderEntry1, 5L, "notes", CancelReason.LATEDELIVERY);
		final OrderCancelEntry cancellationEntry2 = new OrderCancelEntry(orderEntry1, 5L, "notes", CancelReason.LATEDELIVERY);
		when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry1, cancellationEntry2));
		when(consignmentEntry1.getQuantityPending()).thenReturn(10L);
		when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);
		when(consignment1.getStatus()).thenReturn(ConsignmentStatus.READY);

		// When
		cancellationService.processConsignmentCancellation(orderCancelResponse);

		// Then
		verify(consignmentBusinessProcessService, atLeastOnce())
				.triggerChoiceEvent(any(ConsignmentModel.class), anyString(), anyString());
		verify(warehousingConsignmentWorkflowService, atLeastOnce()).terminateConsignmentWorkflow(any(ConsignmentModel.class));
	}

	@Test
	public void shouldNotProcessConsignmentCancellation_WrongConsignmentStatus()
	{
		// Given
		final OrderCancelEntry cancellationEntry1 = new OrderCancelEntry(orderEntry1, 5L, "notes", CancelReason.LATEDELIVERY);
		final OrderCancelEntry cancellationEntry2 = new OrderCancelEntry(orderEntry1, 5L, "notes", CancelReason.LATEDELIVERY);
		when(orderCancelResponse.getEntriesToCancel()).thenReturn(ImmutableList.of(cancellationEntry1, cancellationEntry2));
		when(consignmentEntry1.getQuantityPending()).thenReturn(10L);
		when(orderCancelResponse.getCancelReason()).thenReturn(CancelReason.OTHER);
		when(consignment1.getStatus()).thenReturn(ConsignmentStatus.CANCELLED);

		// When
		cancellationService.processConsignmentCancellation(orderCancelResponse);

		// Then
		verify(consignmentBusinessProcessService, never())
				.triggerChoiceEvent(any(ConsignmentModel.class), anyString(), anyString());
		verify(warehousingConsignmentWorkflowService, never()).terminateConsignmentWorkflow(any(ConsignmentModel.class));
	}

}
