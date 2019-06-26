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
package de.hybris.platform.warehousing.shipping.service;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.shipping.service.impl.DefaultWarehousingShippingService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehousingShippingServiceTest
{
	@Spy
	@InjectMocks
	private DefaultWarehousingShippingService warehousingShippingService;
	@Mock
	private ConsignmentProcessModel consignmentProcess;
	@Mock
	private ModelService modelService;

	private OrderEntryModel headsetEntry;
	private ProductModel headset;
	private OrderModel order;
	private ConsignmentModel consignment;
	private ConsignmentEntryModel consHeadsetEntry;
	private List<OrderStatus> validConsConfirmOrderStatusList;
	private List<ConsignmentStatus> validConsConfirmConsignmentStatusList;

	@Before
	public void setUp()
	{
		warehousingShippingService.setModelService(modelService);
		when(modelService.isUpToDate(any(ConsignmentModel.class))).thenReturn(true);
		doNothing().when(modelService).save(any());

		order = new OrderModel();
		order.setStatus(OrderStatus.READY);
		headset = new ProductModel();
		headset.setCode("headset");
		headsetEntry = new OrderEntryModel();
		headsetEntry.setProduct(headset);
		headsetEntry.setQuantity(5L);

		consHeadsetEntry = spy(new ConsignmentEntryModel());
		consHeadsetEntry.setOrderEntry(headsetEntry);
		consHeadsetEntry.setConsignment(consignment);
		consHeadsetEntry.setQuantity(5L);
		headsetEntry.setConsignmentEntries(Sets.newHashSet(consHeadsetEntry));

		consignment = new ConsignmentModel();
		consignment.setCode("cons_0");
		consignment.setOrder(order);
		consignment.setStatus(ConsignmentStatus.READY);
		order.setConsignments(Sets.newHashSet(consignment));

		consignment.setConsignmentProcesses(Collections.singletonList(consignmentProcess));
		consignment.setConsignmentEntries(Sets.newHashSet(consHeadsetEntry));

		validConsConfirmConsignmentStatusList = Arrays
				.asList(ConsignmentStatus.READY, ConsignmentStatus.READY_FOR_PICKUP, ConsignmentStatus.READY_FOR_SHIPPING);
		validConsConfirmOrderStatusList = Arrays.asList(OrderStatus.READY.READY, OrderStatus.SUSPENDED);
		warehousingShippingService.setValidConsConfirmConsignmentStatusList(validConsConfirmConsignmentStatusList);
		warehousingShippingService.setValidConsConfirmOrderStatusList(validConsConfirmOrderStatusList);

		when(consignmentProcess.getCode()).thenReturn("cons_0_ordermanagement");
		when(consignmentProcess.getState()).thenReturn(ProcessState.RUNNING);
		when(consHeadsetEntry.getQuantityPending()).thenReturn(5L);

	}

	@Test
	public void shouldAllowConfirmation()
	{
		Assert.assertTrue(warehousingShippingService.isConsignmentConfirmable(consignment));
	}

	@Test
	public void shouldNotAllowConfirmation_InvalidOrderStatus()
	{
		order.setStatus(OrderStatus.CANCELLING);
		Assert.assertFalse(warehousingShippingService.isConsignmentConfirmable(consignment));
	}

	@Test
	public void shouldNotAllowConfirmation_NoQuantityPending()
	{
		when(consHeadsetEntry.getQuantityPending()).thenReturn(0L);
		Assert.assertFalse(warehousingShippingService.isConsignmentConfirmable(consignment));
	}

	@Test
	public void shouldNotAllowConfirmation_InvalidConsStatus()
	{
		consignment.setStatus(ConsignmentStatus.SHIPPED);
		Assert.assertFalse(warehousingShippingService.isConsignmentConfirmable(consignment));
	}


}
