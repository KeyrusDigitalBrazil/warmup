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
package de.hybris.platform.yacceleratorordermanagement.actions.consignment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.allocation.AllocationService;
import de.hybris.platform.warehousing.allocation.decline.action.DeclineActionStrategy;
import de.hybris.platform.warehousing.allocation.decline.action.impl.DefaultDamagedStockDeclineStrategy;
import de.hybris.platform.warehousing.allocation.decline.action.impl.DefaultTimedBanDeclineStrategy;
import de.hybris.platform.warehousing.data.allocation.DeclineEntries;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.yacceleratorordermanagement.constants.YAcceleratorOrderManagementConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReAllocateConsignmentActionTest
{

	private OrderModel orderModel;
	private ConsignmentProcessModel consignmentProcessModel;
	private ConsignmentProcessModel newConsignmentProcessModel;
	private OrderProcessModel orderProcessModel;
	private ConsignmentModel consignmentModel;
	private ConsignmentModel newConsignmentModel;
	private ConsignmentEntryModel consignmentEntryModel;
	private ConsignmentEntryModel newConsignmentEntryModel;
	private DeclineEntries declineEntries;
	private static final String ORDER_PROCESS_CODE = "orderProcessCode";

	@Spy
	private Map<DeclineReason, DeclineActionStrategy> declineActionsMap= new HashMap<>();
	@Spy
	private Map<DeclineReason, DeclineActionStrategy> externalWarehouseDeclineActionsMap = new HashMap<>();
	@Spy
	private  Collection<BusinessProcessParameterModel> contextParameters = new ArrayList<>();
	@Spy
	private DeclineEntry autoEntry = new DeclineEntry();
	@Spy
	private DeclineEntry manualEntry = new DeclineEntry();

	@InjectMocks
	ReAllocateConsignmentAction action = new ReAllocateConsignmentAction();

	@Mock
	private ModelService modelService;
	@Mock
	private AllocationService allocationService;
	@Mock
	private WarehousingBusinessProcessService<AbstractOrderModel> orderBusinessProcessService;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private DefaultDamagedStockDeclineStrategy damagedStockDeclineStrategy;
	@Mock
	private DefaultTimedBanDeclineStrategy timedBanDeclineStrategy;
	@Mock
	private WarehouseModel originalWarehouse;
	@Mock
	private WarehouseModel warehouse;

	@Before
	public void setup()
	{
		consignmentModel = new ConsignmentModel();
		newConsignmentModel = new ConsignmentModel();
		orderModel = new OrderModel();
		consignmentModel.setOrder(orderModel);
		newConsignmentModel.setOrder(orderModel);
		consignmentModel.setWarehouse(originalWarehouse);
		newConsignmentModel.setWarehouse(warehouse);
		consignmentEntryModel = new ConsignmentEntryModel();
		consignmentEntryModel.setConsignment(consignmentModel);
		newConsignmentEntryModel = new ConsignmentEntryModel();
		newConsignmentEntryModel.setConsignment(newConsignmentModel);

		orderProcessModel = new OrderProcessModel();
		orderProcessModel.setCode(ORDER_PROCESS_CODE);
		orderProcessModel.setOrder(orderModel);

		consignmentProcessModel = new ConsignmentProcessModel();
		consignmentProcessModel.setConsignment(consignmentModel);
		newConsignmentProcessModel = new ConsignmentProcessModel();
		newConsignmentProcessModel.setConsignment(newConsignmentModel);


		action.setDeclineActionsMap(declineActionsMap);
		when(declineActionsMap.get(any(DeclineReason.class))).thenReturn(damagedStockDeclineStrategy);

		action.setExternalWarehouseDeclineActionsMap(externalWarehouseDeclineActionsMap);
		when(externalWarehouseDeclineActionsMap.get(any(DeclineReason.class))).thenReturn(timedBanDeclineStrategy);

		declineEntries = new DeclineEntries();
		final BusinessProcessParameterModel param = new BusinessProcessParameterModel();
		param.setName(action.DECLINE_ENTRIES);
		param.setProcess(consignmentProcessModel);
		param.setValue(declineEntries);


		contextParameters.add(param);
		consignmentProcessModel.setContextParameters(contextParameters);

		action.setOrderBusinessProcessService(orderBusinessProcessService);
		action.setAllocationService(allocationService);


		autoEntry.setConsignmentEntry(consignmentEntryModel);
		autoEntry.setReason(DeclineReason.DAMAGED);


		manualEntry.setConsignmentEntry(consignmentEntryModel);
		manualEntry.setReallocationWarehouse(warehouse);
		manualEntry.setReason(DeclineReason.DAMAGED);

		when(originalWarehouse.isExternal()).thenReturn(false);
		when(orderBusinessProcessService.getProcess(ORDER_PROCESS_CODE)).thenReturn(orderProcessModel);
		when(orderBusinessProcessService
				.createProcess(anyString(), eq(YAcceleratorOrderManagementConstants.CONSIGNMENT_SUBPROCESS_NAME)))
				.thenReturn(newConsignmentProcessModel);
		when(allocationService.manualReallocate(any(DeclineEntries.class))).thenReturn(Collections.singleton(newConsignmentModel));
		doNothing().when(damagedStockDeclineStrategy).execute(anyCollectionOf(DeclineEntry.class));
		doNothing().when(timedBanDeclineStrategy).execute(anyCollectionOf(DeclineEntry.class));
		doNothing().when(modelService).save(any());
		doNothing().when(orderBusinessProcessService)
				.triggerChoiceEvent(orderModel, YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME,
						action.RE_SOURCE_CHOICE);

	}

	@Test
	public void shouldPerformAutoReallocation() throws Exception
	{
		//Given
		declineEntries.setEntries(Arrays.asList(autoEntry));

		//When
		action.executeAction(consignmentProcessModel);

		//Then
		verify(allocationService).autoReallocate(any(DeclineEntries.class));
		verify(allocationService, never()).manualReallocate(any(DeclineEntries.class));
		verify(warehousingConsignmentWorkflowService,never()).startConsignmentWorkflow(any(ConsignmentModel.class));
		verify(orderBusinessProcessService)
				.triggerChoiceEvent(orderModel, YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME,
						action.RE_SOURCE_CHOICE);
		verify(damagedStockDeclineStrategy).execute(anyCollectionOf(DeclineEntry.class));
	}

	@Test
	public void shouldPerformManualReallocationInternalWarehouse() throws Exception
	{
		//Given
		declineEntries.setEntries(Arrays.asList(manualEntry));

		//When
		action.executeAction(consignmentProcessModel);

		//Then
		verify(allocationService).manualReallocate(any(DeclineEntries.class));
		verify(allocationService, never()).autoReallocate(any(DeclineEntries.class));
		verify(orderBusinessProcessService, never())
				.triggerChoiceEvent(orderModel, YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME,
						action.RE_SOURCE_CHOICE);
		verify(damagedStockDeclineStrategy).execute(anyCollectionOf(DeclineEntry.class));
	}

	@Test
	public void shouldPerformManualAndAutoReallocationInternalWarehouse() throws Exception
	{
		//Given
		declineEntries.setEntries(Arrays.asList(manualEntry, autoEntry));

		//When
		action.executeAction(consignmentProcessModel);

		//Then
		verify(allocationService).manualReallocate(any(DeclineEntries.class));
		verify(allocationService).autoReallocate(any(DeclineEntries.class));
		verify(orderBusinessProcessService)
				.triggerChoiceEvent(orderModel, YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME,
						action.RE_SOURCE_CHOICE);
		verify(damagedStockDeclineStrategy).execute(anyCollectionOf(DeclineEntry.class));
	}

	@Test
	public void shouldPerformManualAndAutoReallocationExternalWarehouse() throws Exception
	{
		//Given
		when(originalWarehouse.isExternal()).thenReturn(true);
		declineEntries.setEntries(Arrays.asList(manualEntry, autoEntry));

		//When
		action.executeAction(consignmentProcessModel);

		//Then
		verify(allocationService).manualReallocate(any(DeclineEntries.class));
		verify(allocationService).autoReallocate(any(DeclineEntries.class));
		verify(orderBusinessProcessService)
				.triggerChoiceEvent(orderModel, YAcceleratorOrderManagementConstants.ORDER_ACTION_EVENT_NAME,
						action.RE_SOURCE_CHOICE);
		verify(timedBanDeclineStrategy).execute(anyCollectionOf(DeclineEntry.class));
	}

}
