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
package de.hybris.platform.warehousingfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.consignment.strategies.ConsignmentAmountCalculationStrategy;
import de.hybris.platform.warehousing.constants.WarehousingConstants;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.labels.service.PrintMediaService;
import de.hybris.platform.warehousing.process.impl.DefaultConsignmentProcessService;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentCodeDataList;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentReallocationData;
import de.hybris.platform.warehousingfacades.order.data.DeclineEntryData;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehousingConsignmentFacadeTest
{
	protected static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	protected static final String REALLOCATE_CONSIGNMENT_CHOICE = "reallocateConsignment";
	protected static final String PICKING_TEMPLATE_CODE = "NPR_Picking";
	protected static final String PICK_SLIP_DOCUMENT_TEMPLATE = "PickLabelDocumentTemplate";
	protected static final String PACKING_TEMPLATE_CODE = "NPR_Packing";
	protected static final String PACK_SLIP_DOCUMENT_TEMPLATE = "PackLabelDocumentTemplate";
	protected static final String PACK_CONSIGNMENT_CHOICE = "packConsignment";
	protected static final String EXPORT_FORM_DOCUMENT_TEMPLATE = "ExportFormDocumentTemplate";
	protected static final String SHIPPING_LABEL_DOCUMENT_TEMPLATE = "ShippingLabelDocumentTemplate";
	protected static final String RETURN_SHIPPING_LABEL_DOCUMENT_TEMPLATE = "ReturnShippingLabelDocumentTemplate";
	protected static final String RETURN_FORM_DOCUMENT_TEMPLATE = "ReturnFormDocumentTemplate";
	protected static final String CONSIGNMENT_CODE = "Consignment_Code_1";
	protected static final String CAMERA_CODE = "camera";
	protected static final String LENS_CODE = "lens";
	protected static final String REALLOCATION_WAREHOUSE_CODE = "reallocation_warehouse";
	protected static final String GLOBAL_REALLOCATION_WAREHOUSE_CODE = "global_reallocation_warehouse";
	protected static final String OK_PICKUP_CHOICE = "okPickup";
	protected static final String OK_SHIP_CHOICE = "okShip";
	protected static final String CAPTURE_PAYMENT_ON_CONSIGNMENT = "warehousing.capturepaymentonconsignment";
	protected static final String HANDLE_MANUAL_PAYMENT_CAPTURE_CHOICE = "handleManualCapture";
	protected static final String HANDLE_MANUAL_TAX_COMMIT_CHOICE = "handleManualCommit";
	protected static final String WRONG_CODE = "wrongCode";

	@InjectMocks
	private DefaultWarehousingConsignmentFacade warehousingConsignmentFacade;

	@Mock
	private ConsignmentModel consignment;
	@Mock
	private ConsignmentEntryModel cameraConsignmentEntry;
	@Mock
	private ConsignmentEntryModel lensConsignmentEntry;
	@Mock
	private OrderEntryModel cameraEntry;
	@Mock
	private ProductModel camera;
	@Mock
	private OrderEntryModel lensEntry;
	@Mock
	private ProductModel lens;
	@Mock
	private WarehouseModel reallocationWarehouse;
	@Mock
	private WarehouseModel globalReallocationWarehouse;
	@Mock
	private ZoneDeliveryModeModel shippingDeliveryMode;
	@Mock
	private PickUpDeliveryModeModel pickUpDeliveryMode;
	@Mock
	private ConsignmentProcessModel consignmentProcess;
	@Mock
	private ConsignmentReallocationData consignmentReallocationData;
	@Mock
	private DeclineEntryData cameraDeclineEntryData;
	@Mock
	private DeclineEntryData lensDeclineEntryData;
	@Mock
	private DefaultConsignmentProcessService consignmentBusinessProcessService;
	@Mock
	private GenericDao<ConsignmentModel> consignmentGenericDao;
	@Mock
	private ModelService modelService;
	@Mock
	private WarehouseService warehouseService;
	@Mock
	private WarehouseStockService warehouseStockService;
	@Mock
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	@Mock
	private WorkflowActionModel pickWorkflowAction;
	@Mock
	private PrintMediaService printMediaService;
	@Mock
	private MediaModel pickSlipMediaModel;
	@Mock
	private MediaModel packSlipMediaModel;
	@Mock
	private MediaModel exportFormMediaModel;
	@Mock
	private MediaModel shippingLabelMediaModel;
	@Mock
	private MediaModel returnShippingLabelMediaModel;
	@Mock
	private MediaModel returnFormMediaModel;
	@Mock
	private MediaModel consolidatedPickSlipMediaModel;
	@Mock
	private Object externalFulfillmentSystem;
	@Mock
	private ConsignmentAmountCalculationStrategy consignmentAmountCalculationStrategy;
	@Mock
	private PaymentService paymentService;
	@Mock
	private PaymentTransactionModel paymentTransactionModel;
	@Mock
	private PaymentTransactionEntryData paymentTransactionEntryData;
	@Mock
	private PaymentTransactionEntryModel paymentTransactionEntryModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> paymentTransactionEntryConverter;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	private ConsignmentCodeDataList consignmentCodeDataList;

	@Before
	public void setUp()
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(ConsignmentModel.CODE, CONSIGNMENT_CODE);
		when(consignmentGenericDao.find(params)).thenReturn(Collections.singletonList(consignment));
		doNothing().when(modelService).save(any());
		when(reallocationWarehouse.getCode()).thenReturn(REALLOCATION_WAREHOUSE_CODE);
		when(warehouseService.getWarehouseForCode(REALLOCATION_WAREHOUSE_CODE)).thenReturn(reallocationWarehouse);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(LENS_CODE, reallocationWarehouse)).thenReturn(200L);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(CAMERA_CODE, reallocationWarehouse)).thenReturn(200L);
		when(globalReallocationWarehouse.getCode()).thenReturn(GLOBAL_REALLOCATION_WAREHOUSE_CODE);
		when(warehouseService.getWarehouseForCode(GLOBAL_REALLOCATION_WAREHOUSE_CODE)).thenReturn(globalReallocationWarehouse);
		when(warehouseStockService.getStockLevelForProductCodeAndWarehouse(CAMERA_CODE, globalReallocationWarehouse))
				.thenReturn(200L);

		warehousingConsignmentFacade.setPaymentTransactionEntryConverter(paymentTransactionEntryConverter);

		final List<ConsignmentStatus> reallocableConsignmentStatusList = new ArrayList<>();
		reallocableConsignmentStatusList.add(ConsignmentStatus.READY);
		warehousingConsignmentFacade.setReallocableConsignmentStatusList(reallocableConsignmentStatusList);

		when(consignment.getCode()).thenReturn(CONSIGNMENT_CODE);
		when(consignmentProcess.getCode()).thenReturn(CONSIGNMENT_CODE + WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX);
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignment.getDeliveryMode()).thenReturn(shippingDeliveryMode);
		when(consignment.getConsignmentProcesses()).thenReturn(Collections.singletonList(consignmentProcess));
		when(consignment.getConsignmentEntries()).thenReturn(Sets.newHashSet(cameraConsignmentEntry, lensConsignmentEntry));
		when(cameraConsignmentEntry.getOrderEntry()).thenReturn(cameraEntry);
		when(cameraConsignmentEntry.getQuantityPending()).thenReturn(10L);
		when(cameraConsignmentEntry.getConsignment()).thenReturn(consignment);
		when(cameraEntry.getProduct()).thenReturn(camera);
		when(cameraEntry.getQuantity()).thenReturn(10L);
		when(camera.getCode()).thenReturn(CAMERA_CODE);
		when(lensConsignmentEntry.getOrderEntry()).thenReturn(lensEntry);
		when(lensConsignmentEntry.getQuantityPending()).thenReturn(5L);
		when(lensConsignmentEntry.getConsignment()).thenReturn(consignment);
		when(lensEntry.getProduct()).thenReturn(lens);
		when(lensEntry.getQuantity()).thenReturn(5L);
		when(lens.getCode()).thenReturn(LENS_CODE);
		when(consignmentReallocationData.getDeclineEntries())
				.thenReturn(Lists.newArrayList(cameraDeclineEntryData, lensDeclineEntryData));
		when(cameraDeclineEntryData.getProductCode()).thenReturn(CAMERA_CODE);
		when(cameraDeclineEntryData.getQuantity()).thenReturn(2L);
		when(cameraDeclineEntryData.getReason()).thenReturn(DeclineReason.DAMAGED);
		when(lensDeclineEntryData.getProductCode()).thenReturn(LENS_CODE);
		when(lensDeclineEntryData.getQuantity()).thenReturn(1L);
		when(lensDeclineEntryData.getReason()).thenReturn(DeclineReason.DAMAGED);
		when(paymentTransactionEntryConverter.convert(paymentTransactionEntryModel)).thenReturn(paymentTransactionEntryData);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getBoolean(CAPTURE_PAYMENT_ON_CONSIGNMENT, Boolean.FALSE)).thenReturn(Boolean.FALSE);
	}

	@Test
	public void testReallocateConsignmentMultiEntriesAutoDecline()
	{
		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);

		//Then
		verify(consignmentBusinessProcessService)
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
	}

	@Test
	public void testReallocateConsignmentMultiEntriesGlobalReallocationWarehouse()
	{
		//Given
		when(consignmentReallocationData.getGlobalReallocationWarehouseCode()).thenReturn(REALLOCATION_WAREHOUSE_CODE);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);

		//Then
		verify(warehouseService, times(2)).getWarehouseForCode(REALLOCATION_WAREHOUSE_CODE);
		verify(warehouseStockService).getStockLevelForProductCodeAndWarehouse(LENS_CODE, reallocationWarehouse);
		verify(warehouseStockService).getStockLevelForProductCodeAndWarehouse(CAMERA_CODE, reallocationWarehouse);
		verify(consignmentBusinessProcessService)
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
	}

	@Test
	public void testReallocateConsignmentMultiEntriesAutoAndManualDecline()
	{
		//Given
		when(lensDeclineEntryData.getReallocationWarehouseCode()).thenReturn(REALLOCATION_WAREHOUSE_CODE);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);

		//Then
		verify(warehouseService).getWarehouseForCode(REALLOCATION_WAREHOUSE_CODE);
		verify(warehouseStockService).getStockLevelForProductCodeAndWarehouse(LENS_CODE, reallocationWarehouse);
		verify(consignmentBusinessProcessService)
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
	}

	@Test
	public void testReallocateConsignmentMultiEntriesGlobalAndLocalReallocationWarehouse()
	{
		//Given
		when(consignmentReallocationData.getGlobalReallocationWarehouseCode()).thenReturn(GLOBAL_REALLOCATION_WAREHOUSE_CODE);
		when(lensDeclineEntryData.getReallocationWarehouseCode()).thenReturn(REALLOCATION_WAREHOUSE_CODE);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);

		//Then
		verify(warehouseService).getWarehouseForCode(REALLOCATION_WAREHOUSE_CODE);
		verify(warehouseService).getWarehouseForCode(GLOBAL_REALLOCATION_WAREHOUSE_CODE);
		verify(warehouseStockService).getStockLevelForProductCodeAndWarehouse(LENS_CODE, reallocationWarehouse);
		verify(warehouseStockService).getStockLevelForProductCodeAndWarehouse(CAMERA_CODE, globalReallocationWarehouse);
		verify(consignmentBusinessProcessService)
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReallocateConsignmentNoDeclineEntriesData()
	{
		//Given
		when(consignmentReallocationData.getDeclineEntries()).thenReturn(Collections.emptyList());

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);
	}

	@Test(expected = IllegalStateException.class)
	public void testReallocateConfirmedConsignment()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.SHIPPED);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReallocateCapturedConsignment()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY_FOR_SHIPPING);
		when(configuration.getBoolean(CAPTURE_PAYMENT_ON_CONSIGNMENT, Boolean.FALSE)).thenReturn(Boolean.TRUE);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReallocatePickupConsignment()
	{
		//Given
		when(consignment.getDeliveryMode()).thenReturn(pickUpDeliveryMode);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReallocateConsignmentQtyHigherThanPending()
	{
		//Given
		when(cameraDeclineEntryData.getQuantity()).thenReturn(20L);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReallocateConsignmentExternalFulfillmentConfiguration()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.reallocateConsignment(CONSIGNMENT_CODE, consignmentReallocationData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPickConsignmentWhenCodeIsNull()
	{
		//When
		warehousingConsignmentFacade.pickConsignment(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testPickConsignmentWhenCodeIsWrong()
	{
		//When
		warehousingConsignmentFacade.pickConsignment(WRONG_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPickConsignmentWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.pickConsignment(CONSIGNMENT_CODE);
	}

	@Test
	public void testPickConsignmentWhenAlreadyPicked()
	{
		//Given
		when(warehousingConsignmentWorkflowService.getWorkflowActionForTemplateCode(PICKING_TEMPLATE_CODE, consignment))
				.thenReturn(pickWorkflowAction);
		when(pickWorkflowAction.getStatus()).thenReturn(WorkflowActionStatus.COMPLETED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PICK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(pickSlipMediaModel);

		//When
		warehousingConsignmentFacade.pickConsignment(CONSIGNMENT_CODE);

		//Then
		verify(warehousingConsignmentWorkflowService, times(0)).decideWorkflowAction(consignment, PICKING_TEMPLATE_CODE, null);
	}

	@Test
	public void testPickConsignmentWhenNoWorkflowActionAttached()
	{
		//Given
		when(warehousingConsignmentWorkflowService.getWorkflowActionForTemplateCode(PICKING_TEMPLATE_CODE, consignment))
				.thenReturn(null);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PICK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(pickSlipMediaModel);

		//When
		warehousingConsignmentFacade.pickConsignment(CONSIGNMENT_CODE);

		//Then
		verify(warehousingConsignmentWorkflowService, times(0)).decideWorkflowAction(consignment, PICKING_TEMPLATE_CODE, null);
	}

	@Test
	public void testPickConsignmentSuccess()
	{
		//Given
		when(warehousingConsignmentWorkflowService.getWorkflowActionForTemplateCode(PICKING_TEMPLATE_CODE, consignment))
				.thenReturn(pickWorkflowAction);
		when(pickWorkflowAction.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PICK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(pickSlipMediaModel);

		//When
		warehousingConsignmentFacade.pickConsignment(CONSIGNMENT_CODE);

		//Then
		verify(warehousingConsignmentWorkflowService).decideWorkflowAction(consignment, PICKING_TEMPLATE_CODE, null);
		verify(printMediaService).generateHtmlMediaTemplate(pickSlipMediaModel);
	}

	@Test
	public void testPickConsignmentWhenPrintSlipFlagIsFalse()
	{
		//Given
		when(warehousingConsignmentWorkflowService.getWorkflowActionForTemplateCode(PICKING_TEMPLATE_CODE, consignment))
				.thenReturn(pickWorkflowAction);
		when(pickWorkflowAction.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PICK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(pickSlipMediaModel);

		//When
		warehousingConsignmentFacade.pickConsignment(CONSIGNMENT_CODE, false);

		//Then
		verify(warehousingConsignmentWorkflowService).decideWorkflowAction(consignment, PICKING_TEMPLATE_CODE, null);
		verify(printMediaService, never()).generateHtmlMediaTemplate(pickSlipMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPackConsignmentWhenCodeIsNull()
	{
		//When
		warehousingConsignmentFacade.packConsignment(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testPackConsignmentWhenCodeIsWrong()
	{
		//When
		warehousingConsignmentFacade.packConsignment(WRONG_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPackConsignmentWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.packConsignment(CONSIGNMENT_CODE);
	}

	@Test
	public void testPackConsignmentWhenAlreadyPacked()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY_FOR_SHIPPING);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PACK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(packSlipMediaModel);

		//When
		warehousingConsignmentFacade.packConsignment(CONSIGNMENT_CODE);

		//Then
		verify(warehousingConsignmentWorkflowService, times(0))
				.decideWorkflowAction(consignment, PACKING_TEMPLATE_CODE, PACK_CONSIGNMENT_CHOICE);
	}

	@Test
	public void testPackConsignmentSuccess()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PACK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(packSlipMediaModel);

		//When
		warehousingConsignmentFacade.packConsignment(CONSIGNMENT_CODE);

		//Then
		verify(warehousingConsignmentWorkflowService)
				.decideWorkflowAction(consignment, PACKING_TEMPLATE_CODE, PACK_CONSIGNMENT_CHOICE);
		verify(printMediaService).generateHtmlMediaTemplate(packSlipMediaModel);
	}

	@Test
	public void testPackConsignmentWhenPrintSlipFlagIsFalse()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(PACK_SLIP_DOCUMENT_TEMPLATE, consignmentProcess)).thenReturn(packSlipMediaModel);

		//When
		warehousingConsignmentFacade.packConsignment(CONSIGNMENT_CODE, false);

		//Then
		verify(warehousingConsignmentWorkflowService)
				.decideWorkflowAction(consignment, PACKING_TEMPLATE_CODE, PACK_CONSIGNMENT_CHOICE);
		verify(printMediaService, never()).generateHtmlMediaTemplate(packSlipMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetExportFormWhenCodeIsNull()
	{
		//When
		warehousingConsignmentFacade.getExportForm(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetExportFormWhenCodeIsWrong()
	{
		//When
		warehousingConsignmentFacade.getExportForm(WRONG_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetExportFormWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.getExportForm(CONSIGNMENT_CODE);
	}

	@Test
	public void testGetExportFormSuccess()
	{
		//Given
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(EXPORT_FORM_DOCUMENT_TEMPLATE, consignmentProcess))
				.thenReturn(exportFormMediaModel);

		//When
		warehousingConsignmentFacade.getExportForm(CONSIGNMENT_CODE);

		//Then
		verify(printMediaService).generateHtmlMediaTemplate(exportFormMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetShippingLabelCodeIsNull()
	{
		//When
		warehousingConsignmentFacade.getShippingLabel(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetShippingLabelWhenCodeIsWrong()
	{
		//When
		warehousingConsignmentFacade.getShippingLabel(WRONG_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetShippingLabelWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.getShippingLabel(CONSIGNMENT_CODE);
	}

	@Test
	public void testGetShippingLabelSuccess()
	{
		//Given
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(SHIPPING_LABEL_DOCUMENT_TEMPLATE, consignmentProcess))
				.thenReturn(shippingLabelMediaModel);

		//When
		warehousingConsignmentFacade.getShippingLabel(CONSIGNMENT_CODE);

		//Then
		verify(printMediaService).generateHtmlMediaTemplate(shippingLabelMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReturnShippingLabelCodeIsNull()
	{
		//When
		warehousingConsignmentFacade.getReturnShippingLabel(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetReturnShippingLabelWhenCodeIsWrong()
	{
		//When
		warehousingConsignmentFacade.getReturnShippingLabel(WRONG_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReturnShippingLabelWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.getReturnShippingLabel(CONSIGNMENT_CODE);
	}

	@Test
	public void testGetReturnShippingLabelSuccess()
	{
		//Given
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(RETURN_SHIPPING_LABEL_DOCUMENT_TEMPLATE, consignmentProcess))
				.thenReturn(returnShippingLabelMediaModel);

		//When
		warehousingConsignmentFacade.getReturnShippingLabel(CONSIGNMENT_CODE);

		//Then
		verify(printMediaService).generateHtmlMediaTemplate(returnShippingLabelMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReturnFormWhenCodeIsNull()
	{
		//When
		warehousingConsignmentFacade.getReturnForm(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetReturnFormWhenCodeIsWrong()
	{
		//When
		warehousingConsignmentFacade.getReturnForm(WRONG_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReturnFormWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.getReturnForm(CONSIGNMENT_CODE);
	}

	@Test
	public void testGetReturnFormSuccess()
	{
		//Given
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);
		when(printMediaService.getMediaForTemplate(RETURN_FORM_DOCUMENT_TEMPLATE, consignmentProcess))
				.thenReturn(returnFormMediaModel);

		//When
		warehousingConsignmentFacade.getReturnForm(CONSIGNMENT_CODE);

		//Then
		verify(printMediaService).generateHtmlMediaTemplate(returnFormMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConsolidatedPickSlipWhenListIsNull()
	{
		//When
		warehousingConsignmentFacade.consolidatedPickSlip(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConsolidatedPickSlipWhenConsignmentCodeListIsNull()
	{
		//Given
		consignmentCodeDataList = new ConsignmentCodeDataList();

		//When
		warehousingConsignmentFacade.consolidatedPickSlip(consignmentCodeDataList);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConsolidatedPickSlipWhenConsignmentCodeListEmpty()
	{
		//Given
		consignmentCodeDataList = new ConsignmentCodeDataList();
		consignmentCodeDataList.setCodes(new ArrayList<>());

		//When
		warehousingConsignmentFacade.consolidatedPickSlip(consignmentCodeDataList);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testConsolidatedPickSlipWhenConsignmentCodeIsWrong()
	{
		//Given
		consignmentCodeDataList = new ConsignmentCodeDataList();
		consignmentCodeDataList.setCodes(Collections.singletonList(WRONG_CODE));

		//When
		warehousingConsignmentFacade.consolidatedPickSlip(consignmentCodeDataList);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConsolidatedPickSlipWhenFulfillmentConfigurationIsExternal()
	{
		//Given
		consignmentCodeDataList = new ConsignmentCodeDataList();
		consignmentCodeDataList.setCodes(Collections.singletonList(CONSIGNMENT_CODE));
		when(consignment.getFulfillmentSystemConfig()).thenReturn(externalFulfillmentSystem);

		//When
		warehousingConsignmentFacade.consolidatedPickSlip(consignmentCodeDataList);
	}

	@Test
	public void testConsolidatedPickSlipSuccess()
	{
		//Given
		consignmentCodeDataList = new ConsignmentCodeDataList();
		consignmentCodeDataList.setCodes(Collections.singletonList(CONSIGNMENT_CODE));
		when(printMediaService.getMediaForTemplate(any(), any())).thenReturn(consolidatedPickSlipMediaModel);

		//When
		warehousingConsignmentFacade.consolidatedPickSlip(consignmentCodeDataList);

		//Then
		verify(printMediaService).generateHtmlMediaTemplate(consolidatedPickSlipMediaModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTakePaymentWhenCodeIsNull()
	{
		warehousingConsignmentFacade.takePayment(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testTakePaymentWhenConsignmentIsInReadyForShippingStatus()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY_FOR_SHIPPING);

		//When
		warehousingConsignmentFacade.takePayment(CONSIGNMENT_CODE);
	}

	@Test(expected = IllegalStateException.class)
	public void testTakePaymentWhenConsignmentIsInReadyForPickupStatus()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY_FOR_PICKUP);

		//When
		warehousingConsignmentFacade.takePayment(CONSIGNMENT_CODE);
	}

	@Test
	public void testTakePaymentSuccess()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.READY);
		when(consignment.getOrder()).thenReturn(orderModel);
		when(orderModel.getPaymentTransactions()).thenReturn(Collections.singletonList(paymentTransactionModel));
		when(paymentService.partialCapture(any(), any())).thenReturn(paymentTransactionEntryModel);

		//When
		warehousingConsignmentFacade.takePayment(CONSIGNMENT_CODE);

		//Then
		verify(paymentService).partialCapture(any(), any());
		verify(paymentTransactionEntryConverter).convert(paymentTransactionEntryModel);
	}

	@Test
	public void testManuallyReleasePaymentCaptureConsignmentSuccess()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.PAYMENT_NOT_CAPTURED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);

		//When
		warehousingConsignmentFacade.manuallyReleasePaymentCapture(CONSIGNMENT_CODE);

		//Then
		verify(consignmentBusinessProcessService)
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, HANDLE_MANUAL_PAYMENT_CAPTURE_CHOICE);
	}

	@Test(expected = IllegalStateException.class)
	public void testManuallyReleasePaymentCaptureConsignmentWithInvalidStatus()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.SHIPPED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);

		//When
		warehousingConsignmentFacade.manuallyReleasePaymentCapture(CONSIGNMENT_CODE);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testManuallyReleasePaymentCaptureConsignmentWithInvalidCode()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.SHIPPED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);

		//When
		warehousingConsignmentFacade.manuallyReleasePaymentCapture(WRONG_CODE);
	}

	@Test
	public void testManuallyReleaseTaxCommitConsignmentSuccess()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.TAX_NOT_COMMITTED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);

		//When
		warehousingConsignmentFacade.manuallyReleaseTaxCommit(CONSIGNMENT_CODE);

		//Then
		verify(consignmentBusinessProcessService)
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, HANDLE_MANUAL_TAX_COMMIT_CHOICE);
	}

	@Test(expected = IllegalStateException.class)
	public void testManuallyReleaseTaxCommitConsignmentWithInvalidStatus()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.SHIPPED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);

		//When
		warehousingConsignmentFacade.manuallyReleaseTaxCommit(CONSIGNMENT_CODE);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testManuallyReleaseTaxCommitConsignmentWithInvalidCode()
	{
		//Given
		when(consignment.getStatus()).thenReturn(ConsignmentStatus.SHIPPED);
		when(consignmentBusinessProcessService.getConsignmentProcess(consignment)).thenReturn(consignmentProcess);

		//When
		warehousingConsignmentFacade.manuallyReleaseTaxCommit(WRONG_CODE);
	}
}
