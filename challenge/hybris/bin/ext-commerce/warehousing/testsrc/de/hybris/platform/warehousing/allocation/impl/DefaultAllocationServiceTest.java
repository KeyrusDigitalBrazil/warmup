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
package de.hybris.platform.warehousing.allocation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.util.GuidKeyGenerator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.stock.impl.StockLevelDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.allocation.AllocationException;
import de.hybris.platform.warehousing.allocation.strategy.ShippingDateStrategy;
import de.hybris.platform.warehousing.comment.WarehousingCommentService;
import de.hybris.platform.warehousing.externalfulfillment.dao.WarehousingFulfillmentConfigDao;
import de.hybris.platform.warehousing.data.allocation.DeclineEntries;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.data.comment.WarehousingCommentContext;
import de.hybris.platform.warehousing.data.sourcing.SourcingResult;
import de.hybris.platform.warehousing.data.sourcing.SourcingResults;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.inventoryevent.service.InventoryEventService;
import de.hybris.platform.warehousing.model.AllocationEventModel;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.model.DeclineConsignmentEntryEventModel;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.warehousing.taskassignment.strategy.UserSelectionStrategy;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAllocationServiceTest
{
	private static final Long BOOK_QTY = 20L;
	private static final Long HEADSET_QTY = 5L;
	private static final Long MOUSE_QTY = 2L;
	private static final Long PEN_QTY = 50L;

	@Mock
	private ModelService modelService;
	@Mock
	private DeliveryModeService deliveryModeService;
	@Mock
	private TimeService timeService;
	@Mock
	private WarehousingCommentService consignmentEntryCommentService;
	@Mock
	private StockLevelDao stockLevelDao;
	@Mock
	private ShippingDateStrategy shippingDateStrategy;
	@Mock
	private WorkflowTemplateService workflowTemplateService;
	@Mock
	private WorkflowService workflowService;
	@Mock
	private WorkflowProcessingService workflowProcessingService;
	@Mock
	private UserSelectionStrategy userSelectionStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private WarehousingFulfillmentConfigDao warehousingFulfillmentConfigDao;
	@Mock
	private Object externalSystemConfiguration;

	@Spy
	@InjectMocks
	private final DefaultAllocationService allocationService = new DefaultAllocationService();

	@Mock
	private InventoryEventService inventoryEventService;

	private AddressModel shippingAddress;
	private AbstractOrderModel order;
	private SourcingResults results;

	private SourcingResult electronicResult;
	private OrderEntryModel headsetEntry;
	private OrderEntryModel mouseEntry;
	private ProductModel headset;
	private ProductModel mouse;
	private PointOfServiceModel montreal;
	private PointOfServiceModel newyork;
	private WarehouseModel montrealWarehouse;
	private WarehouseModel newyorkWarehouse;
	private BaseStoreModel baseStore;
	private AtpFormulaModel atpFormula;

	private SourcingResult stationaryResult;
	private OrderEntryModel bookEntry;
	private OrderEntryModel penEntry;
	private ProductModel book;
	private ProductModel pen;
	private UserModel author;
	private WorkflowModel workflow;
	private WorkflowActionModel workflowAction;
	private WorkflowTemplateModel workflowTemplate;

	private ConsignmentModel consignment;
	private ConsignmentEntryModel consHeadsetEntry;
	private ConsignmentEntryModel consMouseEntry;
	private DeclineConsignmentEntryEventModel consignmentEntryEvent;
	private GuidKeyGenerator guidKeyGenerator;
	private StockLevelModel stockLevel;


	@Before
	public void setUp() throws Exception
	{
		allocationService.setStockLevelDao(stockLevelDao);
		order = new OrderModel();
		results = new SourcingResults();
		electronicResult = new SourcingResult();
		stationaryResult = new SourcingResult();
		baseStore = new BaseStoreModel();
		atpFormula = new AtpFormulaModel();
		baseStore.setDefaultAtpFormula(atpFormula);
		order.setStore(baseStore);

		stockLevel = new StockLevelModel();

		book = new ProductModel();
		book.setCode("bookcode");
		bookEntry = new OrderEntryModel();
		bookEntry.setProduct(book);

		headset = new ProductModel();
		headset.setCode("headsetcode");
		headsetEntry = new OrderEntryModel();
		headsetEntry.setProduct(headset);
		headsetEntry.setOrder(order);

		mouse = new ProductModel();
		mouse.setCode("mousecode");
		mouseEntry = new OrderEntryModel();
		mouseEntry.setProduct(mouse);
		mouseEntry.setOrder(order);

		pen = new ProductModel();
		pen.setCode("pencode");
		penEntry = new OrderEntryModel();
		penEntry.setProduct(pen);
		penEntry.setOrder(order);

		newyork = new PointOfServiceModel();
		newyork.setDisplayName("new york");
		newyorkWarehouse = new WarehouseModel();
		newyork.setWarehouses(Lists.newArrayList(newyorkWarehouse));
		montreal = new PointOfServiceModel();
		montreal.setDisplayName("montreal");
		montrealWarehouse = new WarehouseModel();
		montreal.setWarehouses(Lists.newArrayList(montrealWarehouse));
		shippingAddress = new AddressModel();

		consignment = new ConsignmentModel();
		consignment.setCode("original");
		consignment.setOrder(order);
		consignment.setWarehouse(newyorkWarehouse);
		order.setConsignments(Sets.newHashSet(consignment));
		consHeadsetEntry = spy(new ConsignmentEntryModel());
		consHeadsetEntry.setOrderEntry(headsetEntry);
		consHeadsetEntry.setConsignment(consignment);
		consHeadsetEntry.setQuantity(5L);
		headsetEntry.setConsignmentEntries(Sets.newHashSet(consHeadsetEntry));
		consMouseEntry = spy(new ConsignmentEntryModel());
		consMouseEntry.setOrderEntry(mouseEntry);
		consMouseEntry.setConsignment(consignment);
		consMouseEntry.setQuantity(5L);
		mouseEntry.setConsignmentEntries(Sets.newHashSet(consMouseEntry));
		consignment.setConsignmentEntries(Sets.newHashSet(consHeadsetEntry, consMouseEntry));

		consignmentEntryEvent = new DeclineConsignmentEntryEventModel();

		author = new UserModel();
		order.setUser(author);
		consignment.setOrder(order);

		workflowTemplate = new WorkflowTemplateModel();
		workflowAction = new WorkflowActionModel();
		workflow = new WorkflowModel();
		workflow.setActions(Arrays.asList(workflowAction));


		doNothing().when(modelService).save(any(Object.class));
		when(modelService.create(ConsignmentModel.class)).thenReturn(new ConsignmentModel()).thenReturn(new ConsignmentModel())
				.thenReturn(new ConsignmentModel()).thenReturn(new ConsignmentModel());
		when(modelService.create(ConsignmentEntryModel.class)).thenReturn(new ConsignmentEntryModel())
				.thenReturn(new ConsignmentEntryModel()).thenReturn(new ConsignmentEntryModel())
				.thenReturn(new ConsignmentEntryModel());
		when(modelService.create(AllocationEventModel.class)).thenReturn(new AllocationEventModel());
		when(timeService.getCurrentTime()).thenReturn(new Date());

		when(consHeadsetEntry.getQuantityPending()).thenReturn(HEADSET_QTY);
		when(consMouseEntry.getQuantityPending()).thenReturn(MOUSE_QTY);
		when(modelService.create(DeclineConsignmentEntryEventModel.class)).thenReturn(consignmentEntryEvent);
		doReturn(new Date()).when(shippingDateStrategy).getExpectedShippingDate(any(ConsignmentModel.class));

		final AllocationEventModel allocationEventModel = Mockito.spy(new AllocationEventModel());
		allocationEventModel.setQuantity(10);
		doReturn(Arrays.asList(allocationEventModel)).when(inventoryEventService).getAllocationEventsForConsignmentEntry(Mockito.any());

		when(workflowTemplateService.getWorkflowTemplateForCode(anyString())).thenReturn(workflowTemplate);
		when(workflowProcessingService.startWorkflow(workflow)).thenReturn(true);

		when(workflowService.createWorkflow(any(), any(), any(), any())).thenReturn(workflow);
		when(userSelectionStrategy.getUserForConsignmentAssignment(workflow)).thenReturn(author);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configurationService.getConfiguration().getString("warehousing.consignment.workflow.template")).thenReturn("");

		guidKeyGenerator = new GuidKeyGenerator();
		allocationService.setGuidKeyGenerator(guidKeyGenerator);
	}

	/*
	 * Tests for Reallocation
	 */
	@Test
	public void shouldReallocateAndPopulateConsignmentEntryEvent_WithNote()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry = new DeclineEntry();
		entry.setConsignmentEntry(consHeadsetEntry);
		entry.setQuantity(1L);
		entry.setReason(DeclineReason.OUTOFSTOCK);
		entry.setNotes("notes");
		entry.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Collections.singleton(entry));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		allocationService.manualReallocate(entries);

		verify(modelService).save(consignmentEntryEvent);
		verify(consignmentEntryCommentService).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
		verify(modelService, times(1)).refresh(entry.getConsignmentEntry());
		verify(modelService, times(1)).save(entry.getConsignmentEntry());
		assertEquals(Long.valueOf(4), consHeadsetEntry.getQuantity());
		assertEquals(consHeadsetEntry, consignmentEntryEvent.getConsignmentEntry());
		assertEquals(montrealWarehouse, consignmentEntryEvent.getReallocatedWarehouse());
		assertEquals(DeclineReason.OUTOFSTOCK, consignmentEntryEvent.getReason());
	}

	@Test
	public void shouldAutoReallocateTriggerSourcing()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry = new DeclineEntry();
		entry.setConsignmentEntry(consHeadsetEntry);
		entry.setQuantity(1L);
		entry.setReason(DeclineReason.OUTOFSTOCK);
		entry.setNotes("notes");
		entries.setEntries(Collections.singleton(entry));

		allocationService.autoReallocate(entries);

		verify(modelService).save(consignmentEntryEvent);
		verify(consignmentEntryCommentService).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
		verify(modelService, times(1)).refresh(entry.getConsignmentEntry());
		verify(modelService, times(1)).save(entry.getConsignmentEntry());
		assertEquals(Long.valueOf(1), consignmentEntryEvent.getQuantity());
		assertEquals(Long.valueOf(4), consHeadsetEntry.getQuantity());
		assertEquals(consHeadsetEntry, consignmentEntryEvent.getConsignmentEntry());
		assertEquals(DeclineReason.OUTOFSTOCK, consignmentEntryEvent.getReason());
	}

	@Test
	public void shouldReallocateAndPopulateConsignmentEntryEvent_NoNote()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry = new DeclineEntry();
		entry.setConsignmentEntry(consHeadsetEntry);
		entry.setQuantity(1L);
		entry.setReason(DeclineReason.OUTOFSTOCK);
		entry.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Collections.singleton(entry));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		allocationService.manualReallocate(entries);

		verify(modelService).save(consignmentEntryEvent);
		verifyZeroInteractions(consignmentEntryCommentService);
		verify(modelService, times(1)).refresh(entry.getConsignmentEntry());
		verify(modelService, times(1)).save(entry.getConsignmentEntry());
		assertEquals(Long.valueOf(1), consignmentEntryEvent.getQuantity());
		assertEquals(Long.valueOf(4), consHeadsetEntry.getQuantity());
		assertEquals(consHeadsetEntry, consignmentEntryEvent.getConsignmentEntry());
		assertEquals(montrealWarehouse, consignmentEntryEvent.getReallocatedWarehouse());
		assertEquals(DeclineReason.OUTOFSTOCK, consignmentEntryEvent.getReason());
	}

	@Test
	public void shouldReallocate_PartialConsignmentEntryOnly()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry = new DeclineEntry();
		entry.setConsignmentEntry(consHeadsetEntry);
		entry.setQuantity(1L);
		entry.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Collections.singleton(entry));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		final ConsignmentModel reallocatedConsignment = allocationService.manualReallocate(entries).stream().findFirst().get();
		final ConsignmentEntryModel reallocatedEntry = reallocatedConsignment.getConsignmentEntries().iterator().next();

		assertEquals("original_0", reallocatedConsignment.getCode());
		assertEquals(1, reallocatedConsignment.getConsignmentEntries().size());
		assertEquals(Long.valueOf(1), reallocatedEntry.getQuantity());
		assertEquals(headsetEntry, reallocatedEntry.getOrderEntry());
		assertEquals(montrealWarehouse, reallocatedConsignment.getWarehouse());
	}

	@Test
	public void shouldReallocate_PartialConsignmentEntry_SecondDecline()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry = new DeclineEntry();
		entry.setConsignmentEntry(consHeadsetEntry);
		entry.setQuantity(1L);
		entry.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Collections.singleton(entry));

		when(consHeadsetEntry.getQuantityPending()).thenReturn(HEADSET_QTY - 1);
		when(consHeadsetEntry.getDeclineEntryEvents()).thenReturn(Collections.singleton(new DeclineConsignmentEntryEventModel()));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));
		final ConsignmentModel reallocatedConsignment = allocationService.manualReallocate(entries).stream().findFirst().get();
		final ConsignmentEntryModel reallocatedEntry = reallocatedConsignment.getConsignmentEntries().iterator().next();

		assertEquals("original_1", reallocatedConsignment.getCode());
		assertEquals(1, reallocatedConsignment.getConsignmentEntries().size());
		assertEquals(Long.valueOf(1), reallocatedEntry.getQuantity());
		assertEquals(headsetEntry, reallocatedEntry.getOrderEntry());
		assertEquals(montrealWarehouse, reallocatedConsignment.getWarehouse());
	}

	@Test
	public void shouldReallocate_NoMoreThanConsignmentEntryQuantityPending()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry = new DeclineEntry();
		entry.setConsignmentEntry(consHeadsetEntry);
		entry.setQuantity(10L);
		entry.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Collections.singleton(entry));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		final ConsignmentModel reallocatedConsignment = allocationService.manualReallocate(entries).stream().findFirst().get();
		final ConsignmentEntryModel reallocatedEntry = reallocatedConsignment.getConsignmentEntries().iterator().next();

		assertEquals(Long.valueOf(HEADSET_QTY), consignmentEntryEvent.getQuantity());
		assertEquals(1, reallocatedConsignment.getConsignmentEntries().size());
		assertEquals(Long.valueOf(HEADSET_QTY), reallocatedEntry.getQuantity());
		assertEquals(headsetEntry, reallocatedEntry.getOrderEntry());
		assertEquals(montrealWarehouse, reallocatedConsignment.getWarehouse());
	}

	@Test
	public void shouldReallocate_Partial_MultipleEntriesSameLocation()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry1 = new DeclineEntry();
		entry1.setConsignmentEntry(consHeadsetEntry);
		entry1.setQuantity(1L);
		entry1.setNotes("notes 1");
		entry1.setReallocationWarehouse(montrealWarehouse);
		final DeclineEntry entry2 = new DeclineEntry();
		entry2.setConsignmentEntry(consMouseEntry);
		entry2.setQuantity(2L);
		entry2.setNotes("notes 2");
		entry2.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Sets.newHashSet(entry1, entry2));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));
		when(stockLevelDao.findStockLevels(mouse.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		final ConsignmentModel reallocatedConsignment = allocationService.manualReallocate(entries).stream().findFirst().get();
		final Collection<ConsignmentEntryModel> reallocatedConsignmentEntries = reallocatedConsignment.getConsignmentEntries();

		final ConsignmentEntryModel reallocatedHeadsetConsEntry = reallocatedConsignmentEntries.stream()
				.filter(entry -> entry.getOrderEntry().equals(headsetEntry)).findFirst().get();
		final ConsignmentEntryModel reallocatedMouseConsEntry = reallocatedConsignmentEntries.stream()
				.filter(entry -> entry.getOrderEntry().equals(mouseEntry)).findFirst().get();

		verify(modelService, times(2)).save(consignmentEntryEvent);
		verify(consignmentEntryCommentService, times(2)).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
		verify(modelService, times(1)).refresh(consHeadsetEntry);
		verify(modelService, times(1)).save(consHeadsetEntry);
		assertEquals(Long.valueOf(4), consHeadsetEntry.getQuantity());
		verify(modelService, times(1)).refresh(consMouseEntry);
		verify(modelService, times(1)).save(consMouseEntry);
		assertEquals(Long.valueOf(3), consMouseEntry.getQuantity());

		assertEquals(2, reallocatedConsignmentEntries.size());

		assertEquals(Long.valueOf(1L), reallocatedHeadsetConsEntry.getQuantity());
		assertEquals(Long.valueOf(2L), reallocatedMouseConsEntry.getQuantity());
		assertEquals(montrealWarehouse, reallocatedHeadsetConsEntry.getConsignment().getWarehouse());
		assertEquals(montrealWarehouse, reallocatedMouseConsEntry.getConsignment().getWarehouse());
	}

	@Test
	public void shouldReallocate_Partial_MultipleEntriesDiffLocation()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry1 = new DeclineEntry();
		entry1.setConsignmentEntry(consHeadsetEntry);
		entry1.setQuantity(1L);
		entry1.setNotes("notes 1");
		entry1.setReallocationWarehouse(montrealWarehouse);
		final DeclineEntry entry2 = new DeclineEntry();
		entry2.setConsignmentEntry(consMouseEntry);
		entry2.setQuantity(2L);
		entry2.setNotes("notes 2");
		entry2.setReallocationWarehouse(newyorkWarehouse);
		entries.setEntries(Sets.newHashSet(entry1, entry2));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));
		when(stockLevelDao.findStockLevels(mouse.getCode(), Collections.singletonList(newyorkWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		final Collection<ConsignmentModel> reallocatedConsignments = allocationService.manualReallocate(entries);
		final Collection<ConsignmentEntryModel> reallocatedConsignmentEntries = reallocatedConsignments.stream()
				.flatMap(consignment -> consignment.getConsignmentEntries().stream()).collect(Collectors.toList());

		final ConsignmentEntryModel reallocatedHeadsetConsEntry = reallocatedConsignmentEntries.stream()
				.filter(entry -> entry.getOrderEntry().equals(headsetEntry)).findFirst().get();
		final ConsignmentEntryModel reallocatedMouseConsEntry = reallocatedConsignmentEntries.stream()
				.filter(entry -> entry.getOrderEntry().equals(mouseEntry)).findFirst().get();

		verify(modelService, times(2)).save(consignmentEntryEvent);
		verify(consignmentEntryCommentService, times(2)).createAndSaveComment(any(WarehousingCommentContext.class), anyString());
		verify(modelService, times(1)).refresh(consHeadsetEntry);
		verify(modelService, times(1)).save(consHeadsetEntry);
		assertEquals(Long.valueOf(4), consHeadsetEntry.getQuantity());
		verify(modelService, times(1)).refresh(consMouseEntry);
		verify(modelService, times(1)).save(consMouseEntry);
		assertEquals(Long.valueOf(3), consMouseEntry.getQuantity());

		assertEquals(2, reallocatedConsignments.size());
		assertEquals(2, reallocatedConsignmentEntries.size());

		assertEquals(Long.valueOf(1L), reallocatedHeadsetConsEntry.getQuantity());
		assertEquals(Long.valueOf(2L), reallocatedMouseConsEntry.getQuantity());
		assertEquals(montrealWarehouse, reallocatedHeadsetConsEntry.getConsignment().getWarehouse());
		assertEquals(newyorkWarehouse, reallocatedMouseConsEntry.getConsignment().getWarehouse());
	}

	@Test
	public void shouldReallocate_EntireConsignment()
	{
		order.setDeliveryAddress(shippingAddress);

		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry1 = new DeclineEntry();
		entry1.setConsignmentEntry(consHeadsetEntry);
		entry1.setQuantity(HEADSET_QTY);
		entry1.setReallocationWarehouse(montrealWarehouse);
		final DeclineEntry entry2 = new DeclineEntry();
		entry2.setConsignmentEntry(consMouseEntry);
		entry2.setQuantity(MOUSE_QTY);
		entry2.setReallocationWarehouse(montrealWarehouse);
		entries.setEntries(Sets.newHashSet(entry1, entry2));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));
		when(stockLevelDao.findStockLevels(mouse.getCode(), Collections.singletonList(montrealWarehouse))).thenReturn(Collections.singletonList(stockLevel));

		final ConsignmentModel reallocatedConsignment = allocationService.manualReallocate(entries).stream().findFirst().get();
		final Collection<ConsignmentEntryModel> reallocatedConsignmentEntries = reallocatedConsignment.getConsignmentEntries();
		final ConsignmentEntryModel reallocatedHeadsetConsEntry = reallocatedConsignmentEntries.stream()
				.filter(entry -> entry.getOrderEntry().equals(headsetEntry)).findFirst().get();
		final ConsignmentEntryModel reallocatedMouseConsEntry = reallocatedConsignmentEntries.stream()
				.filter(entry -> entry.getOrderEntry().equals(mouseEntry)).findFirst().get();

		verify(modelService, times(2)).save(consignmentEntryEvent);
		verify(modelService, times(1)).refresh(consHeadsetEntry);
		verify(modelService, times(1)).save(consHeadsetEntry);
		assertEquals(Long.valueOf(0), consHeadsetEntry.getQuantity());
		verify(modelService, times(1)).refresh(consMouseEntry);
		verify(modelService, times(1)).save(consMouseEntry);
		assertEquals(Long.valueOf(3), consMouseEntry.getQuantity());


		assertEquals(2, reallocatedConsignmentEntries.size());
		assertEquals(Long.valueOf(HEADSET_QTY), reallocatedHeadsetConsEntry.getQuantity());
		assertEquals(Long.valueOf(MOUSE_QTY), reallocatedMouseConsEntry.getQuantity());
		assertEquals(montrealWarehouse, reallocatedHeadsetConsEntry.getConsignment().getWarehouse());
		assertEquals(montrealWarehouse, reallocatedMouseConsEntry.getConsignment().getWarehouse());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailReallocate_NullEntries()
	{
		allocationService.manualReallocate(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailReallocate_EmptyEntries()
	{
		final DeclineEntries entries = new DeclineEntries();
		entries.setEntries(Collections.emptySet());

		allocationService.manualReallocate(entries);
	}

	/*
	 * Tests for Allocation (consignment creation)
	 */
	@Test
	public void shouldMakePickupConsignment_OrderEntryHasDeliveryPos()
	{
		final DeliveryModeModel pickupDeliveryMode = new DeliveryModeModel();
		when(deliveryModeService.getDeliveryModeForCode(any())).thenReturn(pickupDeliveryMode);

		final AddressModel newyorkAddress = new AddressModel();
		newyork.setAddress(newyorkAddress);
		headsetEntry.setDeliveryPointOfService(newyork);
		order.setEntries(Lists.newArrayList(headsetEntry));

		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(electronicAllocation);
		electronicResult.setWarehouse(newyorkWarehouse);

		final ConsignmentModel pickupConsignment = allocationService.createConsignment(order, "consignment1234", electronicResult);

		assertEquals(ConsignmentStatus.READY, pickupConsignment.getStatus());
		assertEquals(pickupDeliveryMode, pickupConsignment.getDeliveryMode());
		assertEquals(newyorkAddress, pickupConsignment.getShippingAddress());
	}

	@Test
	public void shouldMakeShipConsignment_OrderEntryHasNoDeliveryPos()
	{
		final DeliveryModeModel shippingDeliveryMode = new DeliveryModeModel();
		order.setDeliveryMode(shippingDeliveryMode);

		order.setEntries(Lists.newArrayList(headsetEntry));

		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(electronicAllocation);
		electronicResult.setWarehouse(newyorkWarehouse);

		final ConsignmentModel shipConsignment = allocationService.createConsignment(order, "consignment1234", electronicResult);

		assertEquals(ConsignmentStatus.READY, shipConsignment.getStatus());
		assertEquals(shippingDeliveryMode, shipConsignment.getDeliveryMode());
		assertEquals(order.getDeliveryAddress(), shipConsignment.getShippingAddress());
		verify(shippingDateStrategy, times(1)).getExpectedShippingDate(any(ConsignmentModel.class));
	}

	@Test
	public void createShipConsignment_MultipleConsignments_MultipleResults()
	{
		order.setEntries(Lists.newArrayList(headsetEntry));
		order.setDeliveryAddress(shippingAddress);
		electronicResult.setWarehouse(montrealWarehouse);
		stationaryResult.setWarehouse(newyorkWarehouse);

		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicAllocation.put(mouseEntry, MOUSE_QTY);
		electronicResult.setAllocation(electronicAllocation);

		final Map<AbstractOrderEntryModel, Long> stationaryAllocation = Maps.newHashMap();
		stationaryAllocation.put(bookEntry, BOOK_QTY);
		stationaryAllocation.put(penEntry, PEN_QTY);
		stationaryResult.setAllocation(stationaryAllocation);
		results.setResults(Sets.newHashSet(electronicResult, stationaryResult));

		final Collection<ConsignmentModel> consignments = allocationService.createConsignments(order, "consignment1234", results);
		assertNotNull(consignments);
		assertEquals(2, consignments.size());

		for (final ConsignmentModel consignment : consignments)
		{
			if (consignment.getWarehouse().equals(montrealWarehouse))
			{
				validateElectronicConsignment_MultipleResults(consignment);
			}
			else
			{
				validateStationaryConsignment_MultipleResults(consignment);
			}
		}

		// verify consignment codes are unique
		assertFalse(consignments.stream().allMatch(cons -> cons.getCode().equals("consignment1234_0")));
	}

	@Test
	public void createShipConsignment_MultipleConsignments_SingleResult()
	{
		order.setEntries(Lists.newArrayList(headsetEntry));
		order.setDeliveryAddress(shippingAddress);
		electronicResult.setWarehouse(montrealWarehouse);
		stationaryResult.setWarehouse(newyorkWarehouse);

		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(electronicAllocation);

		final Map<AbstractOrderEntryModel, Long> stationaryAllocation = Maps.newHashMap();
		stationaryAllocation.put(bookEntry, BOOK_QTY);
		stationaryResult.setAllocation(stationaryAllocation);
		results.setResults(Sets.newHashSet(electronicResult, stationaryResult));

		final Collection<ConsignmentModel> consignments = allocationService.createConsignments(order, "consignment1234", results);
		assertNotNull(consignments);
		assertEquals(2, consignments.size());

		for (final ConsignmentModel consignment : consignments)
		{
			if (consignment.getWarehouse().equals(montrealWarehouse))
			{
				validateElectronicConsignment_SingleResult(consignment);
			}
			else
			{
				validateStationaryConsignment_SingleResult(consignment);
			}
		}

		// verify consignment codes are unique
		assertFalse(consignments.stream().allMatch(cons -> cons.getCode().equals("consignment1234_0")));
	}


	@Test
	public void createShipConsignment_SingleConsignment_MultipleResults()
	{
		order.setEntries(Lists.newArrayList(headsetEntry));
		order.setDeliveryAddress(shippingAddress);
		electronicResult.setWarehouse(montrealWarehouse);

		final Map<AbstractOrderEntryModel, Long> allocation = Maps.newHashMap();
		allocation.put(headsetEntry, HEADSET_QTY);
		allocation.put(mouseEntry, MOUSE_QTY);
		electronicResult.setAllocation(allocation);
		results.setResults(Sets.newHashSet(electronicResult));

		final Collection<ConsignmentModel> consignments = allocationService.createConsignments(order, "consignment1234", results);
		assertNotNull(consignments);
		assertEquals(1, consignments.size());

		validateElectronicConsignment_MultipleResults(consignments.iterator().next());
	}

	@Test
	public void createShipConsignment_SingleConsignment_SingleResult()
	{
		order.setEntries(Lists.newArrayList(headsetEntry));
		order.setDeliveryAddress(shippingAddress);
		electronicResult.setWarehouse(montrealWarehouse);
		final Map<AbstractOrderEntryModel, Long> allocation = Maps.newHashMap();
		allocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(allocation);

		final ConsignmentModel consignment = allocationService.createConsignment(order, "consignment1234", electronicResult);
		assertNotNull(consignment);
		validateElectronicConsignment_SingleResult(consignment);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_nullCode()
	{
		allocationService.createConsignment(new AbstractOrderModel(), null, new SourcingResult());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFail_nullSourcingResult()
	{
		allocationService.createConsignment(new AbstractOrderModel(), null, new SourcingResult());
	}

	@Test
	public void createAllocationEvents_MultipleConsignments()
	{
		order.setEntries(Lists.newArrayList(headsetEntry));
		order.setDeliveryAddress(shippingAddress);
		electronicResult.setWarehouse(montrealWarehouse);
		stationaryResult.setWarehouse(newyorkWarehouse);

		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(electronicAllocation);

		final Map<AbstractOrderEntryModel, Long> stationaryAllocation = Maps.newHashMap();
		stationaryAllocation.put(bookEntry, BOOK_QTY);
		stationaryResult.setAllocation(stationaryAllocation);
		results.setResults(Sets.newHashSet(electronicResult, stationaryResult));

		final Collection<ConsignmentModel> consignments = allocationService.createConsignments(order, "consignment1234", results);
		assertNotNull(consignments);
		assertEquals(2, consignments.size());

		final WarehouseModel warehouse = new WarehouseModel();
		warehouse.setCode("warehouse_NY");
		consignments.forEach(consignment -> consignment.setWarehouse(warehouse));

		final Collection<AllocationEventModel> allocationEvents = consignments.stream()
				.map(result -> inventoryEventService.createAllocationEvents(result)).flatMap(result -> result.stream())
				.collect(Collectors.toList());
		assertNotNull(allocationEvents);

		for (final AllocationEventModel event : allocationEvents)
		{
			if (event.getConsignmentEntry().getOrderEntry() == bookEntry)
			{
				assertEquals(BOOK_QTY, Long.valueOf(event.getQuantity()));
			}
			else
			{
				assertEquals(headsetEntry, event.getConsignmentEntry().getOrderEntry());
				assertEquals(HEADSET_QTY, Long.valueOf(event.getQuantity()));
			}
		}
	}

	@Test
	public void warehouseWithNotNullSystemConfig()
	{
		when(warehousingFulfillmentConfigDao.getConfiguration(newyorkWarehouse)).thenReturn(externalSystemConfiguration);

		order.setEntries(Lists.newArrayList(headsetEntry));
		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(electronicAllocation);
		electronicResult.setWarehouse(newyorkWarehouse);

		consignment = allocationService.createConsignment(order, "consignment1234", electronicResult);
		verify(warehousingConsignmentWorkflowService, never()).startConsignmentWorkflow(consignment);
	}

	@Test
	public void warehouseWithNullSystemConfig()
	{
		when(warehousingFulfillmentConfigDao.getConfiguration(newyorkWarehouse)).thenReturn(null);
		order.setEntries(Lists.newArrayList(headsetEntry));
		final Map<AbstractOrderEntryModel, Long> electronicAllocation = Maps.newHashMap();
		electronicAllocation.put(headsetEntry, HEADSET_QTY);
		electronicResult.setAllocation(electronicAllocation);
		electronicResult.setWarehouse(newyorkWarehouse);

		consignment = allocationService.createConsignment(order, "consignment1234", electronicResult);

		assertEquals(ConsignmentStatus.READY, consignment.getStatus());
		assertEquals(order.getDeliveryAddress(), consignment.getShippingAddress());
		verify(shippingDateStrategy, times(1)).getExpectedShippingDate(any(ConsignmentModel.class));

		verify(warehousingConsignmentWorkflowService).startConsignmentWorkflow(consignment);

	}

	@Test(expected = AllocationException.class)
	public void shouldFail_manualReallocate_NoStockEntry()
	{
		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry1 = new DeclineEntry();
		entry1.setReallocationWarehouse(montrealWarehouse);
		entry1.setConsignmentEntry(consHeadsetEntry);

		final DeclineEntry entry2 = new DeclineEntry();
		entry2.setReallocationWarehouse(montrealWarehouse);
		entry2.setConsignmentEntry(consMouseEntry);

		entries.setEntries(Arrays.asList(entry1, entry2));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse)))
				.thenReturn(Collections.singletonList(stockLevel));
		// Second entry has no stock entry.
		when(stockLevelDao.findStockLevels(mouse.getCode(), Collections.singletonList(montrealWarehouse)))
				.thenReturn(Collections.emptyList());

		allocationService.manualReallocate(entries);
	}

	@Test(expected = AllocationException.class)
	public void shouldFail_manualReallocate_NoReallocationWarehouse()
	{
		final DeclineEntries entries = new DeclineEntries();
		final DeclineEntry entry1 = new DeclineEntry();
		entry1.setReallocationWarehouse(montrealWarehouse);
		entry1.setConsignmentEntry(consHeadsetEntry);

		final DeclineEntry entry2 = new DeclineEntry();
		// Second entry has no reallocation warehouse
		entry2.setReallocationWarehouse(null);
		entry2.setConsignmentEntry(consMouseEntry);

		entries.setEntries(Arrays.asList(entry1, entry2));

		when(stockLevelDao.findStockLevels(headset.getCode(), Collections.singletonList(montrealWarehouse)))
				.thenReturn(Collections.singletonList(stockLevel));
		// Second entry has no stock entry.
		when(stockLevelDao.findStockLevels(mouse.getCode(), Collections.singletonList(montrealWarehouse)))
				.thenReturn(Collections.singletonList(stockLevel));

		allocationService.manualReallocate(entries);
	}

	private void validateStationaryConsignment_SingleResult(final ConsignmentModel consignment)
	{
		assertNotNull(consignment.getShippingAddress());
		assertEquals(shippingAddress, consignment.getShippingAddress());

		assertNotNull(consignment.getConsignmentEntries());
		assertFalse(consignment.getConsignmentEntries().isEmpty());
		assertEquals(1, consignment.getConsignmentEntries().size());

		final ConsignmentEntryModel entry = consignment.getConsignmentEntries().iterator().next();
		assertEquals(bookEntry, entry.getOrderEntry());
		assertEquals(BOOK_QTY, entry.getQuantity());
	}

	private void validateElectronicConsignment_SingleResult(final ConsignmentModel consignment)
	{
		assertNotNull(consignment.getShippingAddress());
		assertEquals(shippingAddress, consignment.getShippingAddress());

		assertNotNull(consignment.getConsignmentEntries());
		assertFalse(consignment.getConsignmentEntries().isEmpty());
		assertEquals(1, consignment.getConsignmentEntries().size());

		final ConsignmentEntryModel entry = consignment.getConsignmentEntries().iterator().next();
		assertEquals(headsetEntry, entry.getOrderEntry());
		assertEquals(HEADSET_QTY, entry.getQuantity());
	}

	private void validateStationaryConsignment_MultipleResults(final ConsignmentModel stationaryConsignment)
	{
		assertNotNull(stationaryConsignment.getShippingAddress());
		assertEquals(shippingAddress, stationaryConsignment.getShippingAddress());

		assertNotNull(stationaryConsignment.getConsignmentEntries());
		assertFalse(stationaryConsignment.getConsignmentEntries().isEmpty());
		assertEquals(2, stationaryConsignment.getConsignmentEntries().size());

		for (final ConsignmentEntryModel entry : stationaryConsignment.getConsignmentEntries())
		{
			if (entry.getOrderEntry().equals(bookEntry))
			{
				assertEquals(bookEntry, entry.getOrderEntry());
				assertEquals(BOOK_QTY, entry.getQuantity());
			}
			else
			{
				assertEquals(penEntry, entry.getOrderEntry());
				assertEquals(PEN_QTY, entry.getQuantity());
			}
		}
	}

	private void validateElectronicConsignment_MultipleResults(final ConsignmentModel electronicConsignment)
	{
		assertNotNull(electronicConsignment.getShippingAddress());
		assertEquals(shippingAddress, electronicConsignment.getShippingAddress());

		assertNotNull(electronicConsignment.getConsignmentEntries());
		assertFalse(electronicConsignment.getConsignmentEntries().isEmpty());
		assertEquals(2, electronicConsignment.getConsignmentEntries().size());

		for (final ConsignmentEntryModel entry : electronicConsignment.getConsignmentEntries())
		{
			if (entry.getOrderEntry().equals(headsetEntry))
			{
				assertEquals(headsetEntry, entry.getOrderEntry());
				assertEquals(HEADSET_QTY, entry.getQuantity());
			}
			else
			{
				assertEquals(mouseEntry, entry.getOrderEntry());
				assertEquals(MOUSE_QTY, entry.getQuantity());
			}
		}
	}
}
