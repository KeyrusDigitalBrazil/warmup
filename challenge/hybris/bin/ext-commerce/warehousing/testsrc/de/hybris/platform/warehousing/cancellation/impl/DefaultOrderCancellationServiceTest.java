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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.OrderCancelState;
import de.hybris.platform.basecommerce.enums.OrderModificationEntryStatus;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordercancel.OrderCancelEntry;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.OrderCancelResponseExecutor;
import de.hybris.platform.ordercancel.OrderCancelStateMappingStrategy;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.ordercancel.impl.executors.WarehouseResponseExecutor;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderModificationRecordModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.comment.WarehousingCommentService;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.CancellationEventModel;

import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderCancellationServiceTest
{
	@InjectMocks
	private DefaultOrderCancelService orderCancelService;

	@Mock
	private GuidKeyGenerator guidKeyGenerator;
	@Mock
	private ModelService modelService;
	@Mock
	private InventoryEventService inventoryEventService;
	@Mock
	private WarehousingCommentService orderEntryCommentService;
	@Mock
	private WarehousingCommentService consignmentEntryCommentService;
	@Mock
	private ConsignmentModel consignment1;
	@Mock
	private ConsignmentModel consignment2;
	@Mock
	private ConsignmentEntryModel consignmentEntry1;
	@Mock
	private ConsignmentEntryModel consignmentEntry2;
	@Mock
	private ConsignmentEntryModel consignmentEntry3;
	@Mock
	private ConsignmentEntryModel consignmentEntry4;
	@Mock
	private ProductModel mouse;
	@Mock
	private ProductModel batteries;
	@Mock
	private OrderModel order;
	@Mock
	private OrderEntryModel orderEntry1;
	@Mock
	private OrderEntryModel orderEntry2;
	@Mock
	private OrderCancelRecordEntryModel orderCancelRecordEntryModel;
	@Mock
	private OrderModificationRecordModel orderModificationRecordModel;
	@Mock
	private OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntryModel1;
	@Mock
	private OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntryModel2;
	@Mock
	private WarehouseResponseExecutor warehouseResponseExecutor;
	@Mock
	private OrderCancelRecordModel orderCancelRecordModel;
	@Mock
	private OrderCancelRecordsHandler orderCancelRecordsHandler;
	@Mock
	private OrderCancelStateMappingStrategy stateMappingStrategy;
	@Mock
	private Map<OrderCancelState, OrderCancelResponseExecutor> responseExecutorsMap;
	@Mock
	private OrderCancelResponseExecutor orderCancelResponseExecutor;

	private CancellationEventModel orderCancellationEvent;
	private CancellationEventModel consignmentCancellationEvent;

	@Before
	public void setUp()
	{
		orderCancellationEvent = new CancellationEventModel();
		consignmentCancellationEvent = new CancellationEventModel();
		when(modelService.create(CancellationEventModel.class)).thenReturn(orderCancellationEvent);
		when(modelService.create(CancellationEventModel.class)).thenReturn(consignmentCancellationEvent);
		when(guidKeyGenerator.generate()).thenReturn(UUID.randomUUID());

		when(order.getEntries()).thenReturn(Lists.newArrayList(orderEntry1, orderEntry2));
		when(orderEntry1.getEntryNumber()).thenReturn(Integer.valueOf(1));
		when(orderEntry1.getQuantity()).thenReturn(Long.valueOf(20L));
		when(orderEntry1.getOrder()).thenReturn(order);
		when(orderEntry1.getProduct()).thenReturn(mouse);
		when(mouse.getName()).thenReturn("Wireless Mouse");
		when(orderEntry2.getEntryNumber()).thenReturn(Integer.valueOf(2));
		when(orderEntry2.getQuantity()).thenReturn(Long.valueOf(10L));
		when(orderEntry2.getOrder()).thenReturn(order);
		when(orderEntry2.getProduct()).thenReturn(batteries);
		when(batteries.getName()).thenReturn("Rechargeable batteries");

		when(consignmentEntry1.getOrderEntry()).thenReturn(orderEntry1);
		when(consignmentEntry1.getQuantity()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry2.getOrderEntry()).thenReturn(orderEntry2);
		when(consignmentEntry2.getQuantity()).thenReturn(Long.valueOf(5L));
		when(consignment1.getCode()).thenReturn("consignment1234_1");
		when(consignment1.getConsignmentEntries()).thenReturn(Sets.newHashSet(consignmentEntry1, consignmentEntry2));

		when(consignmentEntry3.getOrderEntry()).thenReturn(orderEntry1);
		when(consignmentEntry3.getQuantity()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry4.getOrderEntry()).thenReturn(orderEntry2);
		when(consignmentEntry4.getQuantity()).thenReturn(Long.valueOf(5L));
		when(consignment2.getCode()).thenReturn("consignment1234_2");
		when(consignment2.getConsignmentEntries()).thenReturn(Sets.newHashSet(consignmentEntry3, consignmentEntry4));

		when(orderCancelRecordEntryModel.getModificationRecord()).thenReturn(orderModificationRecordModel);
		when(orderModificationRecordModel.getOrder()).thenReturn(order);

		when(orderCancelRecordEntryModel.getOrderEntriesModificationEntries())
				.thenReturn(Lists.newArrayList(orderEntryCancelRecordEntryModel1, orderEntryCancelRecordEntryModel2));
		when(orderEntryCancelRecordEntryModel1.getOrderEntry()).thenReturn(orderEntry1);
		when(orderEntryCancelRecordEntryModel2.getOrderEntry()).thenReturn(orderEntry2);

		orderCancelService.setResponseExecutorsMap(responseExecutorsMap);

		when(orderCancelRecordsHandler.getCancelRecord(order)).thenReturn(orderCancelRecordModel);
		when(Boolean.valueOf(orderCancelRecordModel.isInProgress())).thenReturn(Boolean.TRUE);
		when(orderCancelRecordModel.getModificationRecordEntries()).thenReturn(Lists.newArrayList(orderCancelRecordEntryModel));
		when(orderCancelRecordEntryModel.getStatus()).thenReturn(OrderModificationEntryStatus.INPROGRESS);
		when(stateMappingStrategy.getOrderCancelState(order)).thenReturn(OrderCancelState.CANCELIMPOSSIBLE);
		when(responseExecutorsMap.get(OrderCancelState.CANCELIMPOSSIBLE)).thenReturn(warehouseResponseExecutor);
	}

	@Test
	public void shouldCancelNonAllocatedQuantities_WithNote() throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L, "notes");
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(10L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test
	public void shouldCancelNonAllocatedQuantities_WithoutNote() throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L);
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(10L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test
	public void shouldCancelAllocatedQuantities_1Consignment() throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L, "notes");
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(20L));
		when(consignmentEntry1.getQuantityPending()).thenReturn(Long.valueOf(10L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test
	public void shouldCancelAllocatedQuantities_2Consignment() throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 20L, "notes");
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(20L));
		when(consignmentEntry1.getQuantityPending()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry3.getQuantityPending()).thenReturn(Long.valueOf(10L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test
	public void shouldCancelAllocatedQuantities_2Consignments_2CancelEntries_affectAllConsignmentEntries()
			throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry1 = new OrderCancelEntry(orderEntry2, 10L);
		final OrderCancelEntry cancellationEntry2 = new OrderCancelEntry(orderEntry1, 20L);
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order,
				Lists.newArrayList(cancellationEntry2, cancellationEntry1));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(20L));
		when(orderEntry2.getQuantityAllocated()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry1.getQuantityPending()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry2.getQuantityPending()).thenReturn(Long.valueOf(5L));
		when(consignmentEntry3.getQuantityPending()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry4.getQuantityPending()).thenReturn(Long.valueOf(5L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
		verify(modelService, times(0)).save(orderCancellationEvent);
	}

	@Test
	public void shouldCancelAllocatedQuantities_2Consignments_2CancelEntries_affect3ConsignmentEntries()
			throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry1 = new OrderCancelEntry(orderEntry2, 7L);
		final OrderCancelEntry cancellationEntry2 = new OrderCancelEntry(orderEntry1, 10L);
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order,
				Lists.newArrayList(cancellationEntry2, cancellationEntry1));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(20L));
		when(orderEntry2.getQuantityAllocated()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry1.getQuantityPending()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry2.getQuantityPending()).thenReturn(Long.valueOf(5L));
		when(consignmentEntry3.getQuantityPending()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry4.getQuantityPending()).thenReturn(Long.valueOf(5L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test
	public void shouldCancelHalfAllocatedHalfNotAllocatedQuantities_1Consignment() throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 10L);
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(15L));
		when(consignmentEntry1.getQuantityPending()).thenReturn(Long.valueOf(5L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test
	public void shouldCancelHalfAllocatedHalfNotAllocatedQuantities_2Consignment() throws OrderCancelException
	{
		// Given
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 20L);
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		when(orderEntry1.getQuantityAllocated()).thenReturn(Long.valueOf(10L));
		when(consignmentEntry1.getQuantityPending()).thenReturn(Long.valueOf(5L));
		when(consignmentEntry3.getQuantityPending()).thenReturn(Long.valueOf(5L));

		// When
		final OrderCancelResponse orderCancelResponse = new OrderCancelResponse(order);
		orderCancelService.onOrderCancelResponse(orderCancelResponse);

		// Then
		verify(warehouseResponseExecutor, times(1)).processCancelResponse(any(OrderCancelResponse.class),
				any(OrderCancelRecordEntryModel.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowCancellingMoreThanOriginalQuantity() throws OrderCancelException
	{
		final OrderCancelEntry cancellationEntry = new OrderCancelEntry(orderEntry1, 30L);
		final OrderCancelRequest cancelRequest = new OrderCancelRequest(order, Lists.newArrayList(cancellationEntry));
		verifyZeroInteractions(cancelRequest);
	}
}
