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
package de.hybris.platform.warehousingfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.onhold.service.OrderOnHoldService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehousingOrderFacadeTest
{
	protected static final String ORDER_CODE = "order1";
	protected static final String ORDER_PROCESS = "order-process";

	@InjectMocks
	private DefaultWarehousingOrderFacade warehousingOrderFacade;

	@Mock
	private OrderOnHoldService orderOnHoldService;
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private GenericDao<OrderModel> orderGenericDao;
	@Mock
	private OrderModel order;
	@Mock
	private OrderProcessModel orderProcess;
	@Mock
	private OrderEntryModel orderEntry;
	@Mock
	private BaseStoreModel baseStore;

	private List<OrderStatus> onHoldableOrderStatusList;

	@Before
	public void setup()
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(OrderModel.CODE, ORDER_CODE);
		when(orderGenericDao.find(params)).thenReturn(Collections.singletonList(order));
		when(order.getOrderProcess()).thenReturn(Collections.singletonList(orderProcess));
		when(orderProcess.getCode()).thenReturn(ORDER_PROCESS);
		when(order.getStore()).thenReturn(baseStore);
		when(baseStore.getSubmitOrderProcessCode()).thenReturn(ORDER_PROCESS);

		when(order.getStatus()).thenReturn(OrderStatus.READY);
		when(order.getEntries()).thenReturn(Collections.singletonList(orderEntry));
		when(orderEntry.getQuantityPending()).thenReturn(10L);

		onHoldableOrderStatusList = Arrays.asList(OrderStatus.READY, OrderStatus.SUSPENDED);
		warehousingOrderFacade.setOnHoldableOrderStatusList(onHoldableOrderStatusList);
	}

	@Test
	public void testResourceSuccessOnHoldStatus()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.ON_HOLD);

		//When
		warehousingOrderFacade.reSource(ORDER_CODE);

		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test
	public void testResourceSuccessSuspendedStatus()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.SUSPENDED);

		//When
		warehousingOrderFacade.reSource(ORDER_CODE);

		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test(expected = IllegalStateException.class)
	public void testResourceFailureWrongStatus()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.READY);

		//When
		warehousingOrderFacade.reSource(ORDER_CODE);
	}

	@Test
	public void shouldPutOnHoldReadyOrder()
	{
		//When
		warehousingOrderFacade.putOrderOnHold(ORDER_CODE);
		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test
	public void shouldPutOnHoldSuspendedOrder()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.SUSPENDED);
		when(orderEntry.getQuantityPending()).thenReturn(10L);
		//When
		warehousingOrderFacade.putOrderOnHold(ORDER_CODE);
		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPutOnHoldNullOrderCode()
	{
		//When
		warehousingOrderFacade.putOrderOnHold(null);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotPutOnHoldCompletedOrder()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.COMPLETED);
		//When
		warehousingOrderFacade.putOrderOnHold(ORDER_CODE);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotPutOnHoldAlreadyOnHoldOrder()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.ON_HOLD);
		//When
		warehousingOrderFacade.putOrderOnHold(ORDER_CODE);
	}

	/**
	 * This scenario can happen when the associated consignment has got shipped, while the order has not received that event
	 */
	@Test(expected = IllegalStateException.class)
	public void shouldNotPutOnHoldShippedOrder()
	{
		//Given
		when(order.getStatus()).thenReturn(OrderStatus.READY);
		when(orderEntry.getQuantityPending()).thenReturn(0L);
		//When
		warehousingOrderFacade.putOrderOnHold(ORDER_CODE);
	}
}
