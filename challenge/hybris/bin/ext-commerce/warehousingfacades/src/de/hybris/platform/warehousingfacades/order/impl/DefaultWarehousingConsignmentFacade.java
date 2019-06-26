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


import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;
import de.hybris.platform.commerceservices.search.dao.PagedGenericDao;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ordermanagementfacades.OmsBaseFacade;
import de.hybris.platform.ordermanagementfacades.payment.data.PaymentTransactionEntryData;
import de.hybris.platform.ordermanagementfacades.search.dao.impl.SearchByStatusPagedGenericDao;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.warehousing.consignment.strategies.ConsignmentAmountCalculationStrategy;
import de.hybris.platform.warehousing.constants.WarehousingConstants;
import de.hybris.platform.warehousing.data.allocation.DeclineEntries;
import de.hybris.platform.warehousing.data.allocation.DeclineEntry;
import de.hybris.platform.warehousing.enums.DeclineReason;
import de.hybris.platform.warehousing.labels.service.PrintMediaService;
import de.hybris.platform.warehousing.model.PackagingInfoModel;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;
import de.hybris.platform.warehousing.process.impl.DefaultConsignmentProcessService;
import de.hybris.platform.warehousing.shipping.service.WarehousingShippingService;
import de.hybris.platform.warehousing.sourcing.filter.SourcingFilterProcessor;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;
import de.hybris.platform.warehousing.taskassignment.services.WarehousingConsignmentWorkflowService;
import de.hybris.platform.warehousingfacades.order.WarehousingConsignmentFacade;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentCodeDataList;
import de.hybris.platform.warehousingfacades.order.data.ConsignmentReallocationData;
import de.hybris.platform.warehousingfacades.order.data.DeclineEntryData;
import de.hybris.platform.warehousingfacades.order.data.PackagingInfoData;
import de.hybris.platform.warehousingfacades.storelocator.data.WarehouseData;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfSingleResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static de.hybris.platform.warehousing.constants.WarehousingConstants.CONSOLIDATED_CONSIGNMENTS_BP_PARAM_NAME;
import static org.springframework.util.Assert.isTrue;


/**
 * Default implementation of {@link WarehousingConsignmentFacade}.
 */
public class DefaultWarehousingConsignmentFacade extends OmsBaseFacade implements WarehousingConsignmentFacade
{
	protected static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	protected static final String REALLOCATE_CONSIGNMENT_CHOICE = "reallocateConsignment";
	protected static final String DECLINE_ENTRIES = "declineEntries";
	protected static final String PICK_SLIP_DOCUMENT_TEMPLATE = "PickLabelDocumentTemplate";
	protected static final String PICKING_TEMPLATE_CODE = "NPR_Picking";
	protected static final String CONSOLIDATED_PICK_SLIP_DOCUMENT_TEMPLATE = "ConsolidatedPickLabelDocumentTemplate";
	protected static final String PACK_SLIP_DOCUMENT_TEMPLATE = "PackLabelDocumentTemplate";
	protected static final String PACKING_TEMPLATE_CODE = "NPR_Packing";
	protected static final String PACK_CONSIGNMENT_CHOICE = "packConsignment";
	protected static final String EXPORT_FORM_DOCUMENT_TEMPLATE = "ExportFormDocumentTemplate";
	protected static final String SHIPPING_LABEL_DOCUMENT_TEMPLATE = "ShippingLabelDocumentTemplate";
	protected static final String RETURN_SHIPPING_LABEL_DOCUMENT_TEMPLATE = "ReturnShippingLabelDocumentTemplate";
	protected static final String RETURN_FORM_DOCUMENT_TEMPLATE = "ReturnFormDocumentTemplate";
	protected static final String CAPTURE_PAYMENT_ON_CONSIGNMENT = "warehousing.capturepaymentonconsignment";
	protected static final String HANDLE_MANUAL_PAYMENT_CAPTURE_CHOICE = "handleManualCapture";
	protected static final String HANDLE_MANUAL_TAX_COMMIT_CHOICE = "handleManualCommit";

	private ConfigurationService configurationService;
	private Converter<ConsignmentModel, ConsignmentData> consignmentConverter;
	private Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter;
	private Converter<WarehouseModel, WarehouseData> warehouseConverter;
	private Converter<PackagingInfoModel, PackagingInfoData> packagingInfoConverter;
	private Converter<PackagingInfoData, PackagingInfoModel> reversePackagingInfoConverter;
	private PagedGenericDao<ConsignmentModel> consignmentPagedGenericDao;
	private GenericDao<ConsignmentModel> consignmentGenericDao;
	private PagedGenericDao<ConsignmentModel> consignmentEntryPagedDao;
	private SearchByStatusPagedGenericDao<ConsignmentModel> consignmentSearchByStatusPagedDao;
	private EnumerationService enumerationService;
	private WarehousingShippingService warehousingShippingService;
	private SourcingFilterProcessor sourcingFilterProcessor;
	private WarehouseService warehouseService;
	private WarehouseStockService warehouseStockService;
	private WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessProcessService;
	private List<ConsignmentStatus> reallocableConsignmentStatusList;
	private PrintMediaService printMediaService;
	private WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService;
	private Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> paymentTransactionEntryConverter;
	private ConsignmentAmountCalculationStrategy consignmentAmountCalculationStrategy;
	private PaymentService paymentService;

	@Override
	public SearchPageData<ConsignmentData> getConsignments(final PageableData pageableData)
	{
		return convertSearchPageData(getConsignmentPagedGenericDao().find(pageableData), getConsignmentConverter());
	}

	@Override
	public SearchPageData<ConsignmentData> getConsignmentsByStatuses(final PageableData pageableData,
			final Set<ConsignmentStatus> consignmentStatusSet)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(ConsignmentModel.STATUS, consignmentStatusSet);
		return convertSearchPageData(getConsignmentSearchByStatusPagedDao().find(params, pageableData), getConsignmentConverter());
	}

	@Override
	public SearchPageData<ConsignmentEntryData> getConsignmentEntriesForConsignmentCode(final String code,
			final PageableData pageableData)
	{
		final ConsignmentModel consignment = getConsignmentModelForCode(code);

		final Map<String, ConsignmentModel> consignmentEntryParams = new HashMap<>();
		consignmentEntryParams.put(ConsignmentEntryModel.CONSIGNMENT, consignment);
		return convertSearchPageData(getConsignmentEntryPagedDao().find(consignmentEntryParams, pageableData),
				getConsignmentEntryConverter());
	}

	@Override
	public ConsignmentData getConsignmentForCode(final String code)
	{
		return getConsignmentConverter().convert(getConsignmentModelForCode(code));
	}

	@Override
	public List<ConsignmentStatus> getConsignmentStatuses()
	{
		return getEnumerationService().getEnumerationValues(ConsignmentStatus._TYPECODE);
	}

	@Override
	public List<DeclineReason> getDeclineReasons()
	{
		return getEnumerationService().getEnumerationValues(DeclineReason._TYPECODE);
	}

	@Override
	public SearchPageData<WarehouseData> getSourcingLocationsForConsignmentCode(final String code, final PageableData pageableData)
	{
		validateParameterNotNullStandardMessage("code", code);
		validateParameterNotNullStandardMessage("pageableData", pageableData);

		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		final Set<WarehouseModel> locations = Sets.newHashSet();
		getSourcingFilterProcessor().filterLocations(consignmentModel.getOrder(), locations);
		final List<WarehouseModel> locationsList = new ArrayList<>(locations);
		locationsList.sort(Comparator.comparing(WarehouseModel::getCode));

		final SearchPageData<WarehouseModel> searchPageData = new SearchPageData<>();
		searchPageData.setPagination(createPaginationData(pageableData, locations.size()));
		searchPageData.setResults(getSublistOfSourcingLocations(pageableData, locationsList));

		return convertSearchPageData(searchPageData, getWarehouseConverter());
	}

	@Override
	public void confirmShipConsignment(final String code)
	{
		validateParameterNotNullStandardMessage("code", code);

		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		isTrue(consignmentModel.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignmentModel.getCode()));

		isTrue(isConsignmentConfirmable(code),
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.confirmable.consignment"), code,
						consignmentModel.getStatus()));
		isTrue(!(consignmentModel.getDeliveryMode() instanceof PickUpDeliveryModeModel),
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.ship.consignment"),
						consignmentModel.getCode()));

		getWarehousingShippingService().confirmShipConsignment(consignmentModel);
	}

	@Override
	public void confirmPickupConsignment(final String code)
	{
		validateParameterNotNullStandardMessage("code", code);

		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		isTrue(consignmentModel.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignmentModel.getCode()));

		isTrue(isConsignmentConfirmable(code),
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.confirmable.consignment"), code,
						consignmentModel.getStatus()));
		isTrue((consignmentModel.getDeliveryMode() instanceof PickUpDeliveryModeModel),
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.pickup.consignment"),
						consignmentModel.getCode()));

		getWarehousingShippingService().confirmPickupConsignment(consignmentModel);
	}

	@Override
	public boolean isConsignmentConfirmable(final String code)
	{
		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		return (consignmentModel.getFulfillmentSystemConfig() == null) && (getWarehousingShippingService()
				.isConsignmentConfirmable(consignmentModel));
	}

	@Override
	public PackagingInfoData getConsignmentPackagingInformation(final String code)
	{
		validateParameterNotNullStandardMessage("code", code);
		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		return getPackagingInfoConverter().convert(consignmentModel.getPackagingInfo());
	}

	@Override
	public ConsignmentData updateConsignmentPackagingInformation(final String code, final PackagingInfoData packagingInfoData)
	{
		validateParameterNotNullStandardMessage("code", code);
		validateParameterNotNullStandardMessage("packagingInfoData", packagingInfoData);

		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		final PackagingInfoModel newPackagingInfo = getModelService().create(PackagingInfoModel.class);
		getReversePackagingInfoConverter().convert(packagingInfoData, newPackagingInfo);
		newPackagingInfo.setConsignment(consignmentModel);
		consignmentModel.setPackagingInfo(newPackagingInfo);

		getModelService().save(consignmentModel);
		getModelService().save(newPackagingInfo);

		return getConsignmentConverter().convert(consignmentModel);
	}

	@Override
	public void reallocateConsignment(final String consignmentCode, final ConsignmentReallocationData consignmentReallocationData)
	{
		validateParameterNotNullStandardMessage("consignmentCode", consignmentCode);
		validateParameterNotNullStandardMessage("consignmentReallocationData", consignmentReallocationData);
		isTrue(CollectionUtils.isNotEmpty(consignmentReallocationData.getDeclineEntries()),
				Localization.getLocalizedString("warehousingfacade.consignments.reallocation.validation.nothing.to.decline"));

		final ConsignmentModel consignment = getConsignmentModelForCode(consignmentCode);

		if (getConfigurationService().getConfiguration().getBoolean(CAPTURE_PAYMENT_ON_CONSIGNMENT, Boolean.FALSE))
		{
			isTrue(ConsignmentStatus.READY.equals(consignment.getStatus()) || ConsignmentStatus.PAYMENT_NOT_CAPTURED
					.equals(consignment.getStatus()), Localization
					.getLocalizedString("warehousingfacade.consignments.reallocation.validation.reallocate.on.captured.consignment"));
		}

		isTrue(consignment.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignment.getCode()));

		Assert.state(getReallocableConsignmentStatusList().contains(consignment.getStatus()), String.format(
				Localization.getLocalizedString("warehousingfacade.consignments.reallocation.validation.invalid.consignment.status"),
				consignment.getStatus()));

		final String consignmentProcessCode = consignment.getCode() + WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX;
		final Optional<ConsignmentProcessModel> myConsignmentProcess = consignment.getConsignmentProcesses().stream()
				.filter(consignmentProcess -> consignmentProcessCode.equals(consignmentProcess.getCode())).findFirst();
		isTrue(myConsignmentProcess.isPresent(),
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.validation.null.process"),
						consignment.getCode()));

		final List<DeclineEntry> declineEntries = new ArrayList<>();
		for (final DeclineEntryData declineEntryData : consignmentReallocationData.getDeclineEntries())
		{
			boolean isConsignmentEntryPresent = false;
			for (final ConsignmentEntryModel consignmentEntryModel : consignment.getConsignmentEntries())
			{
				if (null != consignmentEntryModel.getOrderEntry().getProduct().getCode() && consignmentEntryModel.getOrderEntry()
						.getProduct().getCode().equalsIgnoreCase(declineEntryData.getProductCode()))
				{
					isConsignmentEntryPresent = true;
					final DeclineEntry declineEntry = populateDeclineEntry(declineEntryData, consignmentReallocationData,
							consignmentEntryModel);
					declineEntries.add(declineEntry);
				}
			}
			isTrue(isConsignmentEntryPresent, String.format(Localization
							.getLocalizedString("warehousingfacade.consignments.reallocation.validation.no.consignmententry.for.declineentry"),
					declineEntryData.getProductCode()));
		}

		buildDeclineParam(myConsignmentProcess.get(), declineEntries);
		getConsignmentBusinessProcessService()
				.triggerChoiceEvent(consignment, CONSIGNMENT_ACTION_EVENT_NAME, REALLOCATE_CONSIGNMENT_CHOICE);

	}

	@Override
	public String pickConsignment(final String code)
	{
		return pickConsignment(code, true);
	}

	@Override
	public String pickConsignment(final String code, final boolean printSlip)
	{
		validateParameterNotNullStandardMessage("code", code);
		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		isTrue(consignmentModel.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignmentModel.getCode()));

		performPickWorkflowAction(consignmentModel, printSlip);

		String template = null;

		if (printSlip)
		{
			// generate the html template even if the consignment is already picked
			final MediaModel pickListMedia = getPrintMediaService().getMediaForTemplate(PICK_SLIP_DOCUMENT_TEMPLATE,
					((DefaultConsignmentProcessService) getConsignmentBusinessProcessService())
							.getConsignmentProcess(consignmentModel));

			template = getPrintMediaService().generateHtmlMediaTemplate(pickListMedia);
		}
		return template;
	}

	@Override
	public String consolidatedPickSlip(final ConsignmentCodeDataList consignmentCodeDataList)
	{
		validateParameterNotNullStandardMessage("consignmentCodeDataList", consignmentCodeDataList);
		isTrue(CollectionUtils.isNotEmpty(consignmentCodeDataList.getCodes()),
				Localization.getLocalizedString("warehousingfacade.consignments.list.validation.null.empty"));
		final List<ConsignmentModel> consignments = new ArrayList<>();
		consignmentCodeDataList.getCodes()
				.forEach(consignmentCode -> consignments.add(getConsignmentModelForCode(consignmentCode)));

		consignments.forEach(consignment -> isTrue(consignment.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignment.getCode())));

		consignments.forEach(this::performPickWorkflowAction);

		final MediaModel consolidatedPickSlipMedia = getPrintMediaService()
				.getMediaForTemplate(CONSOLIDATED_PICK_SLIP_DOCUMENT_TEMPLATE, generateBusinessProcess(consignments));

		return getPrintMediaService().generateHtmlMediaTemplate(consolidatedPickSlipMedia);
	}

	@Override
	public String packConsignment(final String code)
	{
		return packConsignment(code, true);
	}

	@Override
	public String packConsignment(final String code, final boolean printSlip)
	{
		validateParameterNotNullStandardMessage("code", code);
		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		return packConsignment(consignmentModel, printSlip);
	}

	@Override
	public String getExportForm(final String code)
	{
		return generateMediaTemplate(code, EXPORT_FORM_DOCUMENT_TEMPLATE);
	}

	@Override
	public String getShippingLabel(final String code)
	{
		return generateMediaTemplate(code, SHIPPING_LABEL_DOCUMENT_TEMPLATE);
	}

	@Override
	public String getReturnShippingLabel(final String code)
	{
		return generateMediaTemplate(code, RETURN_SHIPPING_LABEL_DOCUMENT_TEMPLATE);
	}

	@Override
	public String getReturnForm(final String code)
	{
		return generateMediaTemplate(code, RETURN_FORM_DOCUMENT_TEMPLATE);
	}

	@Override
	public PaymentTransactionEntryData takePayment(final String code)
	{
		validateParameterNotNullStandardMessage("code", code);
		final ConsignmentModel consignment = getConsignmentModelForCode(code);

		if (ConsignmentStatus.READY.equals(consignment.getStatus()))
		{
			packConsignment(consignment, false);

			final PaymentTransactionEntryModel transaction = performPaymentCapture(consignment);

			return getPaymentTransactionEntryConverter().convert(transaction);
		}
		else
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("warehousingfacade.consignment.packed.error.wrongstatus"),
							consignment.getStatus()));
		}
	}

	@Override
	public void manuallyReleasePaymentCapture(final String code)
	{
		validateParameterNotNullStandardMessage("code", code);
		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);

		if (ConsignmentStatus.PAYMENT_NOT_CAPTURED.equals(consignmentModel.getStatus()))
		{
			getConsignmentBusinessProcessService()
					.triggerChoiceEvent(consignmentModel, CONSIGNMENT_ACTION_EVENT_NAME, HANDLE_MANUAL_PAYMENT_CAPTURE_CHOICE);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("warehousingfacade.consignment.validation.manualpaymentcapture.wrongstatus"), code,
					consignmentModel.getStatus()));
		}
	}

	@Override
	public void manuallyReleaseTaxCommit(final String code)
	{
		validateParameterNotNullStandardMessage("code", code);
		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);

		if (ConsignmentStatus.TAX_NOT_COMMITTED.equals(consignmentModel.getStatus()))
		{
				getConsignmentBusinessProcessService()
						.triggerChoiceEvent(consignmentModel, CONSIGNMENT_ACTION_EVENT_NAME, HANDLE_MANUAL_TAX_COMMIT_CHOICE);
		}
		else
		{
			throw new IllegalStateException(String.format(
					Localization.getLocalizedString("warehousingfacade.consignment.validation.manualtaxcommit.wrongstatus"), code,
					consignmentModel.getStatus()));
		}
	}

	/**
	 * Performs the payment capture operation for the given {@link ConsignmentModel}
	 *
	 * @param consignmentModel
	 * 		the {@link ConsignmentModel} whose amount is to be captured
	 * @return the {@link PaymentTransactionEntryModel} for the transaction
	 */
	protected PaymentTransactionEntryModel performPaymentCapture(final ConsignmentModel consignmentModel)
	{
		final BigDecimal amountToCapture = getConsignmentAmountCalculationStrategy().calculateCaptureAmount(consignmentModel);
		final Optional<PaymentTransactionModel> optionalPaymentTransaction = consignmentModel.getOrder().getPaymentTransactions()
				.stream().findFirst();

		PaymentTransactionEntryModel result = null;
		if (optionalPaymentTransaction.isPresent())
		{
			result = getPaymentService().partialCapture(optionalPaymentTransaction.get(), amountToCapture);
		}

		return result;
	}

	/**
	 * Performs common logic for packing a {@link ConsignmentModel}
	 *
	 * @param consignmentModel
	 * 		the {@link ConsignmentModel} to be packed
	 * @param printSlip
	 * 		whether or not the generate the pack slip
	 * @return the generated pack slip
	 * @throws IllegalStateException
	 * 		throw an {@link IllegalStateException} if the {@link ConsignmentModel} has already been
	 * 		packed and if the slip should not be generated
	 */
	protected String packConsignment(final ConsignmentModel consignmentModel, final boolean printSlip)
	{
		isTrue(consignmentModel.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignmentModel.getCode()));

		// check if the consignment has already been packed. If so do not invoke the decideWorkflowAction method
		if (ConsignmentStatus.READY.equals(consignmentModel.getStatus()))
		{
			getWarehousingConsignmentWorkflowService()
					.decideWorkflowAction(consignmentModel, PACKING_TEMPLATE_CODE, PACK_CONSIGNMENT_CHOICE);
		}
		// throw an exception if the consignment has already been packed and if the slip should not be generated
		else if (!printSlip)
		{
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("warehousingfacade.consignment.pack.error.wrongstatus"),
							consignmentModel.getCode(), consignmentModel.getStatus()));
		}
		String template = null;

		if (printSlip)
		{
			// generate the html template even if the consignment is already picked
			final MediaModel packListMedia = getPrintMediaService().getMediaForTemplate(PACK_SLIP_DOCUMENT_TEMPLATE,
					((DefaultConsignmentProcessService) getConsignmentBusinessProcessService())
							.getConsignmentProcess(consignmentModel));

			template = getPrintMediaService().generateHtmlMediaTemplate(packListMedia);
		}
		return template;
	}

	/**
	 * Finds {@link ConsignmentModel} for the given {@link ConsignmentModel#CODE}
	 *
	 * @param code
	 * 		the consignment's code
	 * @return the requested consignment for the given code
	 */
	protected ConsignmentModel getConsignmentModelForCode(final String code)
	{
		final Map<String, Object> params = new HashMap<>();
		params.put(ConsignmentModel.CODE, code);

		List<ConsignmentModel> consignments = getConsignmentGenericDao().find(params);
		validateIfSingleResult(consignments,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.validation.missing.code"), code),
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.validation.multiple.code"), code));

		return consignments.get(0);
	}

	/**
	 * Gets a sub list based on the pageable data object from the list containing all warehouses.
	 *
	 * @param pageableData
	 * 		the object which will filter the list with its page related information
	 * @param locations
	 * 		contains all available sourcing locations.
	 * @return the sub list which met the pageable data criteria
	 */
	protected List<WarehouseModel> getSublistOfSourcingLocations(final PageableData pageableData,
			final List<WarehouseModel> locations)
	{
		final int fromIndex = pageableData.getCurrentPage() == 0 ? 0 : pageableData.getCurrentPage() * pageableData.getPageSize();
		int toIndex = pageableData.getCurrentPage() == 0 ?
				pageableData.getPageSize() :
				(pageableData.getCurrentPage() + 1) * pageableData.getPageSize();
		toIndex = toIndex > locations.size() ? locations.size() : toIndex;

		return fromIndex > toIndex ? Collections.emptyList() : locations.subList(fromIndex, toIndex);
	}

	/**
	 * Populates and returns {@link DeclineEntry} from given {@link DeclineEntryData}
	 *
	 * @param declineEntryData
	 * 		the given {@link DeclineEntryData} to prepare {@link DeclineEntry}
	 * @param consignmentReallocationData
	 * 		the {@link ConsignmentReallocationData} to check if {@link ConsignmentReallocationData#globalComment}, {@link ConsignmentReallocationData#globalReallocationWarehouseCode} and {@link ConsignmentReallocationData#getGlobalReason()} are present
	 * @param consignmentEntryModel
	 * 		the {@link ConsignmentEntryModel}
	 * @return the {@link DeclineEntry} populated from the given params
	 */
	protected DeclineEntry populateDeclineEntry(final DeclineEntryData declineEntryData,
			final ConsignmentReallocationData consignmentReallocationData, final ConsignmentEntryModel consignmentEntryModel)
	{
		validateDeclineEntryDataToReallocate(consignmentReallocationData, declineEntryData, consignmentEntryModel);

		final DeclineEntry declineEntry = new DeclineEntry();
		declineEntry.setQuantity(declineEntryData.getQuantity());
		declineEntry.setConsignmentEntry(consignmentEntryModel);

		final String warehouseCode = declineEntryData.getReallocationWarehouseCode() != null ?
				declineEntryData.getReallocationWarehouseCode() :
				consignmentReallocationData.getGlobalReallocationWarehouseCode();
		if (!StringUtils.isEmpty(warehouseCode))
		{
			final WarehouseModel reallocationWarehouse = getWarehouseService().getWarehouseForCode(warehouseCode);
			final Long availabilityAtWarehouse = getWarehouseStockService()
					.getStockLevelForProductCodeAndWarehouse(declineEntryData.getProductCode(), reallocationWarehouse);
			isTrue(availabilityAtWarehouse != 0, String.format(Localization.getLocalizedString(
					"warehousingfacade.consignments.reallocation.validation.declineentry.warehouse.no.availability"),
					declineEntryData.getProductCode(), reallocationWarehouse.getCode()));
			declineEntry.setReallocationWarehouse(reallocationWarehouse);
		}

		final String comment = declineEntryData.getComment() != null ?
				declineEntryData.getComment() :
				consignmentReallocationData.getGlobalComment();
		declineEntry.setNotes(comment);

		final DeclineReason declineReason =
				declineEntryData.getReason() != null ? declineEntryData.getReason() : consignmentReallocationData.getGlobalReason();
		declineEntry.setReason(declineReason);

		return declineEntry;
	}

	/**
	 * Validation for {@link DeclineEntryData} to be reallocated against given {@link ConsignmentEntryModel}.
	 *
	 * @param consignmentReallocationData
	 * 		the {@link ConsignmentReallocationData} to be validated
	 * @param declineEntryData
	 * 		the {@link DeclineEntryData} to be validated
	 * @param consignmentEntry
	 * 		the {@link ConsignmentEntryModel}, against which given declineEntryData needs to be validated
	 */
	protected void validateDeclineEntryDataToReallocate(final ConsignmentReallocationData consignmentReallocationData,
			final DeclineEntryData declineEntryData, final ConsignmentEntryModel consignmentEntry)
	{
		validateParameterNotNullStandardMessage("consignmentReallocationData", consignmentReallocationData);
		validateParameterNotNullStandardMessage("declineEntryData", declineEntryData);
		validateParameterNotNullStandardMessage("consignmentEntry", consignmentEntry);
		Assert.notNull(declineEntryData.getQuantity(),
				Localization.getLocalizedString("warehousingfacade.consignments.reallocation.validation.null.declineentry.quantity"));

		isTrue(declineEntryData.getQuantity() <= consignmentEntry.getQuantityPending(), String.format(Localization
						.getLocalizedString(
								"warehousingfacade.consignments.reallocation.validation.declineentry.quantity.greater.than.pending"),
				consignmentEntry.getQuantityPending(), declineEntryData.getQuantity()));
		isTrue(declineEntryData.getQuantity() > 0, Localization
				.getLocalizedString("warehousingfacade.consignments.reallocation.validation.declineentry.quantity.less.than.zero"));

		final DeclineReason declineReason =
				declineEntryData.getReason() != null ? declineEntryData.getReason() : consignmentReallocationData.getGlobalReason();
		validateParameterNotNullStandardMessage("declineReason", declineReason);

		isTrue(!(consignmentEntry.getConsignment().getDeliveryMode() instanceof PickUpDeliveryModeModel),
				Localization.getLocalizedString("warehousingfacade.consignments.reallocation.validation.deliverymode.not.pickup"));
	}

	/**
	 * Build and save the context parameter for decline entries and set it into the given process
	 *
	 * @param processModel
	 * 		the process model for which the context parameters has to be register
	 * @param entriesToReallocate
	 * 		the entries to be reallocated
	 */
	protected void buildDeclineParam(final ConsignmentProcessModel processModel, final List<DeclineEntry> entriesToReallocate)
	{
		cleanDeclineParam(processModel);

		final Collection<BusinessProcessParameterModel> contextParams = new ArrayList<>();
		contextParams.addAll(processModel.getContextParameters());

		final DeclineEntries declinedEntries = new DeclineEntries();
		declinedEntries.setEntries(entriesToReallocate);
		final BusinessProcessParameterModel declineParam = new BusinessProcessParameterModel();
		declineParam.setName(DECLINE_ENTRIES);
		declineParam.setValue(declinedEntries);
		declineParam.setProcess(processModel);
		contextParams.add(declineParam);

		processModel.setContextParameters(contextParams);
		getModelService().save(processModel);
	}

	/**
	 * Removes the old decline entries from {@link ConsignmentProcessModel#CONTEXTPARAMETERS}(if any exists), before attempting to decline
	 *
	 * @param processModel
	 * 		the {@link ConsignmentProcessModel} for the consignment to be declined
	 */
	protected void cleanDeclineParam(final ConsignmentProcessModel processModel)
	{
		final Collection<BusinessProcessParameterModel> contextParams = new ArrayList<>();
		contextParams.addAll(processModel.getContextParameters());
		if (CollectionUtils.isNotEmpty(contextParams))
		{
			final Optional<BusinessProcessParameterModel> declineEntriesParamOptional = contextParams.stream()
					.filter(param -> param.getName().equals(DECLINE_ENTRIES)).findFirst();
			if (declineEntriesParamOptional.isPresent())
			{
				final BusinessProcessParameterModel declineEntriesParam = declineEntriesParamOptional.get();
				contextParams.remove(declineEntriesParam);
				getModelService().remove(declineEntriesParam);

				processModel.setContextParameters(contextParams);
				getModelService().save(processModel);
			}
		}
	}

	/**
	 * Performs the Pick {@link WorkflowActionModel} for the given {@link ConsignmentModel} if the action has not been performed yet.
	 * Throws an IllegalStateException if the slip should not be generated and if the consignment was already picked
	 *
	 * @param consignmentModel
	 * 		the {@link ConsignmentModel} for which the action will potentially be performed
	 * @throws IllegalStateException
	 * 		throw an {@link IllegalStateException} in either of the following 2 cases:
	 * 		1. If the {@link ConsignmentModel} has already been picked and if the slip should not be generated
	 * 		2. If the {@link WorkflowActionModel} is null and if the slip should not be generated
	 */
	protected void performPickWorkflowAction(final ConsignmentModel consignmentModel, boolean printSlip)
	{
		final WorkflowActionModel pickWorkflowAction = getWarehousingConsignmentWorkflowService()
				.getWorkflowActionForTemplateCode(PICKING_TEMPLATE_CODE, consignmentModel);

		// check if the consignment has already been picked. If so do not invoke the decideWorkflowAction method
		if (pickWorkflowAction != null)
		{
			if (!WorkflowActionStatus.COMPLETED.equals(pickWorkflowAction.getStatus()))
			{
				getWarehousingConsignmentWorkflowService().decideWorkflowAction(consignmentModel, PICKING_TEMPLATE_CODE, null);
			}
			// throw an exception if the consignment has already been picked and the slip is not to be generated
			else if (!printSlip)
			{
				throw new IllegalStateException(
						String.format(Localization.getLocalizedString("warehousingfacade.consignment.pick.error.wrongstatus"),
								consignmentModel.getCode(), pickWorkflowAction.getStatus()));
			}
		}
		else if (!printSlip)
		{
			// throw an exception if the WorkflowActionModel is null and the slip is not to be generated
			throw new IllegalStateException(
					String.format(Localization.getLocalizedString("warehousingfacade.consignment.pick.error.null.workflowactionmodel"),
							consignmentModel.getCode()));
		}
	}

	/**
	 * Performs the Pick {@link WorkflowActionModel} for the given {@link ConsignmentModel} if the action has not been performed yet.
	 *
	 * @param consignmentModel
	 * 		the {@link ConsignmentModel} for which the action will potentially be performed
	 */
	protected void performPickWorkflowAction(final ConsignmentModel consignmentModel)
	{
		// check if the consignment has already been picked. If so do not invoke the decideWorkflowAction method
		final WorkflowActionModel pickWorkflowAction = getWarehousingConsignmentWorkflowService()
				.getWorkflowActionForTemplateCode(PICKING_TEMPLATE_CODE, consignmentModel);

		if (pickWorkflowAction != null && !WorkflowActionStatus.COMPLETED.equals(pickWorkflowAction.getStatus()))
		{
			getWarehousingConsignmentWorkflowService().decideWorkflowAction(consignmentModel, PICKING_TEMPLATE_CODE, null);
		}
	}

	/**
	 * Generates a dummy {@link BusinessProcessModel} with a {@link List<ConsignmentModel>} so that it can be passed to the {@link PrintMediaService}
	 *
	 * @param consignmentModels
	 * 		the {@link List<ConsignmentModel>} which will be passed to the new business process
	 * @return the dummy {@link BusinessProcessModel}
	 */
	protected BusinessProcessModel generateBusinessProcess(final List<ConsignmentModel> consignmentModels)
	{
		final BusinessProcessModel businessProcessModel = new BusinessProcessModel();
		final BusinessProcessParameterModel businessProcessParameterModel = new BusinessProcessParameterModel();

		businessProcessParameterModel.setName(CONSOLIDATED_CONSIGNMENTS_BP_PARAM_NAME);
		businessProcessParameterModel.setValue(consignmentModels);
		businessProcessModel.setContextParameters(Collections.singletonList(businessProcessParameterModel));

		return businessProcessModel;
	}

	/**
	 * Generates a media template for the given {@link ConsignmentModel#CODE} and velocity template
	 *
	 * @param code
	 * 		the {@link ConsignmentModel#CODE} for which to generate a template
	 * @param template
	 * 		the template with which the media will be generated
	 * @return the newly generated media template
	 */
	protected String generateMediaTemplate(final String code, final String template)
	{
		validateParameterNotNullStandardMessage("code", code);
		validateParameterNotNullStandardMessage("template", template);

		final ConsignmentModel consignmentModel = getConsignmentModelForCode(code);
		isTrue(consignmentModel.getFulfillmentSystemConfig() == null,
				String.format(Localization.getLocalizedString("warehousingfacade.consignments.error.fulfillmentConfig"),
						consignmentModel.getCode()));

		// generate the html template for the export form
		final MediaModel media = getPrintMediaService().getMediaForTemplate(template,
				((DefaultConsignmentProcessService) getConsignmentBusinessProcessService()).getConsignmentProcess(consignmentModel));

		return getPrintMediaService().generateHtmlMediaTemplate(media);
	}

	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	protected GenericDao<ConsignmentModel> getConsignmentGenericDao()
	{
		return consignmentGenericDao;
	}

	@Required
	public void setConsignmentGenericDao(final GenericDao<ConsignmentModel> consignmentGenericDao)
	{
		this.consignmentGenericDao = consignmentGenericDao;
	}

	protected PagedGenericDao<ConsignmentModel> getConsignmentPagedGenericDao()
	{
		return consignmentPagedGenericDao;
	}

	@Required
	public void setConsignmentPagedGenericDao(final PagedGenericDao<ConsignmentModel> consignmentPagedGenericDao)
	{
		this.consignmentPagedGenericDao = consignmentPagedGenericDao;
	}

	protected Converter<ConsignmentModel, ConsignmentData> getConsignmentConverter()
	{
		return consignmentConverter;
	}

	@Required
	public void setConsignmentConverter(final Converter<ConsignmentModel, ConsignmentData> consignmentConverter)
	{
		this.consignmentConverter = consignmentConverter;
	}

	protected Converter<WarehouseModel, WarehouseData> getWarehouseConverter()
	{
		return warehouseConverter;
	}

	@Required
	public void setWarehouseConverter(final Converter<WarehouseModel, WarehouseData> warehouseConverter)
	{
		this.warehouseConverter = warehouseConverter;
	}

	protected Converter<PackagingInfoModel, PackagingInfoData> getPackagingInfoConverter()
	{
		return packagingInfoConverter;
	}

	@Required
	public void setPackagingInfoConverter(final Converter<PackagingInfoModel, PackagingInfoData> packagingInfoConverter)
	{
		this.packagingInfoConverter = packagingInfoConverter;
	}

	protected Converter<PackagingInfoData, PackagingInfoModel> getReversePackagingInfoConverter()
	{
		return reversePackagingInfoConverter;
	}

	@Required
	public void setReversePackagingInfoConverter(
			final Converter<PackagingInfoData, PackagingInfoModel> reversePackagingInfoConverter)
	{
		this.reversePackagingInfoConverter = reversePackagingInfoConverter;
	}

	protected SearchByStatusPagedGenericDao<ConsignmentModel> getConsignmentSearchByStatusPagedDao()
	{
		return consignmentSearchByStatusPagedDao;
	}

	@Required
	public void setConsignmentSearchByStatusPagedDao(
			final SearchByStatusPagedGenericDao<ConsignmentModel> consignmentSearchByStatusPagedDao)
	{
		this.consignmentSearchByStatusPagedDao = consignmentSearchByStatusPagedDao;
	}

	protected PagedGenericDao getConsignmentEntryPagedDao()
	{
		return consignmentEntryPagedDao;
	}

	@Required
	public void setConsignmentEntryPagedDao(final PagedGenericDao consignmentEntryPagedDao)
	{
		this.consignmentEntryPagedDao = consignmentEntryPagedDao;
	}

	protected Converter<ConsignmentEntryModel, ConsignmentEntryData> getConsignmentEntryConverter()
	{
		return consignmentEntryConverter;
	}

	@Required
	public void setConsignmentEntryConverter(
			final Converter<ConsignmentEntryModel, ConsignmentEntryData> consignmentEntryConverter)
	{
		this.consignmentEntryConverter = consignmentEntryConverter;
	}

	protected WarehousingShippingService getWarehousingShippingService()
	{
		return warehousingShippingService;
	}

	@Required
	public void setWarehousingShippingService(final WarehousingShippingService warehousingShippingService)
	{
		this.warehousingShippingService = warehousingShippingService;
	}

	protected SourcingFilterProcessor getSourcingFilterProcessor()
	{
		return sourcingFilterProcessor;
	}

	@Required
	public void setSourcingFilterProcessor(final SourcingFilterProcessor sourcingFilterProcessor)
	{
		this.sourcingFilterProcessor = sourcingFilterProcessor;
	}

	public WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	@Required
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

	protected WarehousingBusinessProcessService<ConsignmentModel> getConsignmentBusinessProcessService()
	{
		return consignmentBusinessProcessService;
	}

	@Required
	public void setConsignmentBusinessProcessService(
			final WarehousingBusinessProcessService<ConsignmentModel> consignmentBusinessProcessService)
	{
		this.consignmentBusinessProcessService = consignmentBusinessProcessService;
	}

	protected WarehouseStockService getWarehouseStockService()
	{
		return warehouseStockService;
	}

	@Required
	public void setWarehouseStockService(final WarehouseStockService warehouseStockService)
	{
		this.warehouseStockService = warehouseStockService;
	}

	protected List<ConsignmentStatus> getReallocableConsignmentStatusList()
	{
		return reallocableConsignmentStatusList;
	}

	@Required
	public void setReallocableConsignmentStatusList(final List<ConsignmentStatus> reallocableConsignmentStatusList)
	{
		this.reallocableConsignmentStatusList = reallocableConsignmentStatusList;
	}

	protected PrintMediaService getPrintMediaService()
	{
		return printMediaService;
	}

	@Required
	public void setPrintMediaService(final PrintMediaService printMediaService)
	{
		this.printMediaService = printMediaService;
	}

	protected WarehousingConsignmentWorkflowService getWarehousingConsignmentWorkflowService()
	{
		return warehousingConsignmentWorkflowService;
	}

	@Required
	public void setWarehousingConsignmentWorkflowService(
			final WarehousingConsignmentWorkflowService warehousingConsignmentWorkflowService)
	{
		this.warehousingConsignmentWorkflowService = warehousingConsignmentWorkflowService;
	}

	protected Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> getPaymentTransactionEntryConverter()
	{
		return paymentTransactionEntryConverter;
	}

	@Required
	public void setPaymentTransactionEntryConverter(
			final Converter<PaymentTransactionEntryModel, PaymentTransactionEntryData> paymentTransactionEntryConverter)
	{
		this.paymentTransactionEntryConverter = paymentTransactionEntryConverter;
	}

	protected ConsignmentAmountCalculationStrategy getConsignmentAmountCalculationStrategy()
	{
		return consignmentAmountCalculationStrategy;
	}

	@Required
	public void setConsignmentAmountCalculationStrategy(
			final ConsignmentAmountCalculationStrategy consignmentAmountCalculationStrategy)
	{
		this.consignmentAmountCalculationStrategy = consignmentAmountCalculationStrategy;
	}

	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
