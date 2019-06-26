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
package de.hybris.platform.yacceleratorordermanagement.actions.order.cancel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.ordercancel.OrderCancelCallbackService;
import de.hybris.platform.ordercancel.OrderCancelRecordsHandler;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.exceptions.OrderCancelRecordsHandlerException;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.ordermodify.model.OrderEntryModificationRecordEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProcessOrderCancellationActionTest
{
	private OrderEntryModel orderEntryModel;
	private OrderProcessModel orderProcessModel;
	private OrderModel orderModel;
	private ConsignmentModel consignment;
	private OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntryModel;
	private Collection<OrderEntryModificationRecordEntryModel> orderEntryModificationRecordEntryModels = new ArrayList<>();

	@InjectMocks
	private ProcessOrderCancellationAction action;

	@Mock
	private UserService userService;

	@Mock
	private OrderCancelRecordsHandler orderCancelRecordsHandler;

	@Mock
	private OrderCancelRecordEntryModel orderCancelRecordEntryModel;

	@Mock
	private OrderCancelCallbackService orderCancelCallbackService;

	@Mock
	private OrderCancelService orderCancelService;

	@Mock
	private CalculationService calculationService;

	@Mock
	private TimeService timeService;

	@Mock
	private PromotionsService promotionsService;

	@Mock
	private ModelService modelService;

	@Mock
	private ImpersonationService impersonationService = new ImpersonationService()
	{
		@Override
		public <R, T extends Throwable> R executeInContext(final ImpersonationContext context, final Executor<R, T> wrapper)
				throws T
		{
			return wrapper.execute();
		}
	};

	@Before
	public void setup() throws OrderCancelRecordsHandlerException
	{
		consignment = new ConsignmentModel();
		consignment.setStatus(ConsignmentStatus.CANCELLED);

		orderEntryModel = spy(new OrderEntryModel());

		final List<AbstractOrderEntryModel> orderEntriesModel = new ArrayList<>();
		orderEntriesModel.add(orderEntryModel);

		orderModel = spy(new OrderModel());
		orderModel.setEntries(orderEntriesModel);
		orderModel.setConsignments(Sets.newHashSet(consignment));
		orderModel.setSite(new BaseSiteModel());

		orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);

		when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(5L));
		when(orderEntryModel.getQuantityPending()).thenReturn(Long.valueOf(0L));
		when(orderEntryModel.getOrder()).thenReturn(orderModel);
		when(orderEntryModel.getQuantityUnallocated()).thenReturn(Long.valueOf(0L));

		when(userService.getCurrentUser()).thenReturn(new UserModel());

		orderEntryCancelRecordEntryModel = new OrderEntryCancelRecordEntryModel();
		orderEntryCancelRecordEntryModel.setOrderEntry(orderEntryModel);
		orderEntryCancelRecordEntryModel.setCancelRequestQuantity(Integer.valueOf(3));
		orderEntryModificationRecordEntryModels.add(orderEntryCancelRecordEntryModel);

		when(orderCancelRecordsHandler.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getOrderEntriesModificationEntries()).thenReturn(orderEntryModificationRecordEntryModels);


	}

	@Test
	public void shouldWaitWhenQuantityPendingIsMoreThanZero() throws Exception
	{
		//Given
		when(orderEntryModel.getQuantityPending()).thenReturn(Long.valueOf(5L));
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.PARTIAL);

		//When
		final String transition = action.execute(orderProcessModel);

		//Then
		assertEquals(ProcessOrderCancellationAction.Transition.WAIT.toString(), transition);
	}

	@Test
	public void shouldOKWhenQuantityPendingIsZero() throws Exception
	{
		//Given
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.FULL);

		//When
		final String transition = action.execute(orderProcessModel);

		//Then
		assertEquals(ProcessOrderCancellationAction.Transition.OK.toString(), transition);
	}

	@Test
	public void shouldSourcingWhenQuantityUnAllocatedIsMoreThanZeroAndOrderNotOnHold() throws Exception
	{
		//Given
		orderModel.setStatus(OrderStatus.SUSPENDED);
		when(orderEntryModel.getQuantityUnallocated()).thenReturn(Long.valueOf(3L));
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.PARTIAL);

		//When
		final String transition = action.execute(orderProcessModel);

		//Then
		assertEquals(ProcessOrderCancellationAction.Transition.SOURCING.toString(), transition);
	}

	@Test
	public void shouldWaitWhenQuantityUnAllocatedIsMoreThanZeroAndOrderOnHold() throws Exception
	{
		//Given
		orderModel.setStatus(OrderStatus.ON_HOLD);
		when(orderEntryModel.getQuantityUnallocated()).thenReturn(Long.valueOf(3L));
		when(orderEntryModel.getQuantityPending()).thenReturn(Long.valueOf(3L));
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.PARTIAL);

		//When
		final String transition = action.execute(orderProcessModel);

		//Then
		assertEquals(ProcessOrderCancellationAction.Transition.WAIT.toString(), transition);
	}

	@Test
	public void shouldSetOrderStatusToCancelledWhenQuantityPendingIsZero() throws Exception
	{
		//Given
		orderModel.setStatus(OrderStatus.READY);
		when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(0L));
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		orderEntryCancelRecordEntryModel.setCancelRequestQuantity(Integer.valueOf(0));
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.FULL);

		//When
		action.execute(orderProcessModel);

		//Then
		assertTrue(orderModel.getStatus().equals(OrderStatus.CANCELLED));
	}

	@Test
	public void shouldKeepOrderOnHoldWhenSuchOrderCancelled() throws Exception
	{
		//Given
		orderModel.setStatus(OrderStatus.ON_HOLD);
		when(orderEntryModel.getQuantityPending()).thenReturn(Long.valueOf(5L));
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.PARTIAL);

		//When
		final String transition = action.execute(orderProcessModel);

		//Then
		assertEquals(ProcessOrderCancellationAction.Transition.WAIT.toString(), transition);
		assertTrue(orderModel.getStatus().equals(OrderStatus.ON_HOLD));
	}

	@Test
	public void shouldSetOrderStatusToSuspendedWhenSuchOrderCancelled() throws Exception
	{
		//Given
		orderModel.setStatus(OrderStatus.READY);
		when(orderEntryModel.getQuantityUnallocated()).thenReturn(Long.valueOf(5L));
		when(orderCancelService.getPendingCancelRecordEntry(orderModel)).thenReturn(orderCancelRecordEntryModel);
		when(orderCancelRecordEntryModel.getCancelResult()).thenReturn(OrderCancelEntryStatus.PARTIAL);

		//When
		final String transition = action.execute(orderProcessModel);

		//Then
		assertEquals(ProcessOrderCancellationAction.Transition.SOURCING.toString(), transition);
		assertTrue(orderModel.getStatus().equals(OrderStatus.SUSPENDED));
	}
}
