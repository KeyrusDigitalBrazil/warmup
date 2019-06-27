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
package de.hybris.platform.yacceleratorordermanagement.actions.order;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.sourcing.SourcingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SourceOrderActionTest
{
	OrderProcessModel orderProcessModel;
	ConsignmentEntryModel consignmentEntryModel;
	OrderModel orderModel;
	SourcingResults results;
	SourcingResult result;
	Collection<ConsignmentModel> consignments;
	ConsignmentModel consignment;

	@Mock
	OrderEntryModel orderEntryModel;

	@InjectMocks
	SourceOrderAction action = new SourceOrderAction();

	@Mock
	private ModelService modelService;

	@Mock
	private SourcingService sourcingService;

	@Mock
	private AllocationService allocationService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private ConsignmentProcessModel consignmentProcessModel;

	@Before
	public void setup()
	{
		orderEntryModel = spy(new OrderEntryModel());
		when(orderEntryModel.getQuantity()).thenReturn(Long.valueOf(10L));
		List<AbstractOrderEntryModel> orderEntriesModel = new ArrayList<>();
		orderEntriesModel.add(orderEntryModel);

		orderModel = new OrderModel();
		orderModel.setEntries(orderEntriesModel);
		orderModel.setStatus(OrderStatus.CREATED);

		orderProcessModel = new OrderProcessModel();
		orderProcessModel.setOrder(orderModel);


		results = new SourcingResults();
		result = null;
		Set<SourcingResult> resultset = new HashSet<SourcingResult>();
		resultset.add(result);
		results.setResults(resultset);

		Set<ConsignmentEntryModel> consignmentEntriesModel = new HashSet<>();
		consignmentEntriesModel.add(consignmentEntryModel);

		consignment = new ConsignmentModel();
		consignment.setConsignmentEntries(consignmentEntriesModel);
		consignments = new HashSet<ConsignmentModel>();
		consignment = new ConsignmentModel();
		consignments.add(consignment);
	}

	@Test
	public void shouldGoOnHoldWhenSourcingResultIsNull() throws Exception
	{
		when(sourcingService.sourceOrder(orderModel)).thenReturn(null);

		action.execute(orderProcessModel);
		assertTrue(orderModel.getStatus().toString().equals(OrderStatus.SUSPENDED.toString()));
	}

	@Test
	public void shouldGoOnHoldWhenSourcingConfigIsNull() throws Exception
	{
		//Given
		when(sourcingService.sourceOrder(orderModel)).thenThrow(new IllegalArgumentException());

		//When
		action.execute(orderProcessModel);

		//Then
		assertTrue(orderModel.getStatus().toString().equals(OrderStatus.SUSPENDED.toString()));
	}

	@Test
	public void shouldGoOnHoldWhenSourcingResultIsIncomplete() throws Exception
	{

		results.setComplete(false);
		when(sourcingService.sourceOrder(orderModel)).thenReturn(results);
		when(orderEntryModel.getQuantityUnallocated()).thenReturn(Long.valueOf(10L));
		when(orderEntryModel.getQuantityAllocated()).thenReturn(Long.valueOf(0L));

		action.execute(orderProcessModel);
		assertTrue(orderModel.getStatus().toString().equals(OrderStatus.SUSPENDED.toString()));
	}

	@Test
	public void shouldGoReadyWhenSourcingResultIsComplete() throws Exception
	{
		results.setComplete(true);
		when(sourcingService.sourceOrder(orderModel)).thenReturn(results);
		when(allocationService.createConsignments(orderModel, "cons" + orderModel.getCode(), results)).thenReturn(consignments);
		when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(consignmentProcessModel);
		when(orderEntryModel.getQuantityUnallocated()).thenReturn(Long.valueOf(0L));
		when(orderEntryModel.getQuantityAllocated()).thenReturn(Long.valueOf(10L));

		action.execute(orderProcessModel);
		verify(businessProcessService).createProcess(anyString(), anyString());
		verify(consignmentProcessModel).setParentProcess(orderProcessModel);
		verify(consignmentProcessModel).setConsignment(consignment);

		assertTrue(orderModel.getStatus().toString().equals(OrderStatus.READY.toString()));
	}

}
