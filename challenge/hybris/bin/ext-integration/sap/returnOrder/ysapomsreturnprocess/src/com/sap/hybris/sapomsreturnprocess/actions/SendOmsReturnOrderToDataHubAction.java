/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.sapomsreturnprocess.actions;


import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPReturnRequestsModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapomsreturnprocess.constants.YsapomsreturnprocessConstants;
import com.sap.hybris.sapomsreturnprocess.enums.SAPReturnRequestOrderStatus;
import com.sap.hybris.sapomsreturnprocess.returns.keygenerator.KeyGeneratorLookup;
import com.sap.hybris.sapomsreturnprocess.returns.strategy.ReturnSourcingContext;


public class SendOmsReturnOrderToDataHubAction extends AbstractSimpleDecisionAction<ReturnProcessModel>
{
	private static final Logger LOGGER = Logger.getLogger(SendOmsReturnOrderToDataHubAction.class);
	public static final String ERROR_END_MESSAGE = "Sending to Datahub went wrong.";

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;
	private OrderService orderService;

	private SendToDataHubHelper<ReturnRequestModel> sendReturnOrderToDataHubHelper;

	private KeyGeneratorLookup keyGeneratorLookup;
	private ReturnSourcingContext returnSourcingContext;

	@Required
	public void setReturnSourcingContext(final ReturnSourcingContext returnSourcingContext)
	{
		this.returnSourcingContext = returnSourcingContext;
	}

	@Required
	public void setKeyGeneratorLookup(final KeyGeneratorLookup keyGeneratorLookup)
	{
		this.keyGeneratorLookup = keyGeneratorLookup;
	}

	public OrderService getOrderService()
	{
		return orderService;
	}

	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	public SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	@Required
	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}

	@SuppressWarnings("javadoc")
	public SendToDataHubHelper<ReturnRequestModel> getSendReturnOrderToDataHubHelper()
	{
		return sendReturnOrderToDataHubHelper;
	}

	@SuppressWarnings("javadoc")
	@Required
	public void setSendReturnOrderToDataHubHelper(final SendToDataHubHelper<ReturnRequestModel> sendOrderAsCSVHelper)
	{
		this.sendReturnOrderToDataHubHelper = sendOrderAsCSVHelper;
	}


	@Override
	public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException
	{
		final ReturnRequestModel returnRequest = process.getReturnRequest();
		final OrderModel order = returnRequest.getOrder();
		final List<SAPReturnRequestsModel> sapReturnRequestOrders = new ArrayList<>();

		final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap = new HashMap<>();

		populateReturnEntryConsignmentListMap(returnRequest, returnEntryConsignmentListMap);
		returnSourcingContext.splitConsignment(returnEntryConsignmentListMap);
		final List<SendToDataHubResult> results = new ArrayList<>();
		final Set<Set<ConsignmentEntryModel>> consignmentSet = groupReturnOrderConsignments(order, returnEntryConsignmentListMap);

		consignmentSet.stream().forEach(
				consignmentEntry -> sendOMSReturnOrder(order, results, consignmentEntry, returnRequest, sapReturnRequestOrders));

		if (!results.isEmpty() && results.stream().allMatch(result -> result.isSuccess()))
		{

			resetEndMessage(process);
			saveSapReturnRequest(sapReturnRequestOrders, returnRequest);
			LOGGER.info("Return order sent successfully");
			return Transition.OK;

		}
		else
		{
			LOGGER.info("Return order not sent.");
			return Transition.NOK;
		}

	}

	/**
	 * This method makes the map of order entry and list of consignment entry model.
	 *
	 * @param returnRequest
	 * @param returnEntryConsignmentListMap
	 */
	private void populateReturnEntryConsignmentListMap(final ReturnRequestModel returnRequest,
			final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap)
	{
		final List<ReturnEntryModel> returnEntryModels = returnRequest.getReturnEntries();
		for (final ReturnEntryModel returnEntryModel : returnEntryModels)
		{
			final AbstractOrderEntryModel entryModel = returnEntryModel.getOrderEntry();

			final Set<ConsignmentEntryModel> consignmentSet = entryModel.getConsignmentEntries();
			final List<ConsignmentEntryModel> consignmentList = new ArrayList<>(consignmentSet);
			returnEntryConsignmentListMap.put(returnEntryModel, getSortedConsignmentEntries(consignmentList));

		}
	}

	/**
	 * This method sorts the entries in descending order of quantity shipped attribute
	 */
	private List<ConsignmentEntryModel> getSortedConsignmentEntries(final List<ConsignmentEntryModel> consignmentList)
	{
		Collections.sort(consignmentList, new Comparator<ConsignmentEntryModel>()
		{
			@Override
			public int compare(final ConsignmentEntryModel consignment1, final ConsignmentEntryModel consignment2)
			{
				return consignment2.getShippedQuantity().compareTo(consignment1.getShippedQuantity());
			}
		});
		return consignmentList;
	}

	/**
	 * This method saves the SAPReturnRequest in db.
	 *
	 * @param sapReturnRequestOrders
	 *           - List of SAPReturnRequest
	 * @param returnRequest
	 *           - original return request
	 */
	private void saveSapReturnRequest(final List<SAPReturnRequestsModel> sapReturnRequestOrders,
			final ReturnRequestModel returnRequest)
	{
		for (final SAPReturnRequestsModel sapReturnRequestOrderModel : sapReturnRequestOrders)
		{
			sapReturnRequestOrderModel.setSapReturnRequestOrderStatus(SAPReturnRequestOrderStatus.SENT_TO_BACKEND);
			sapReturnRequestOrderModel.setReturnRequest(returnRequest);
			sapReturnRequestOrderModel.getConsignmentsEntry().forEach(consignmentEntry -> getModelService().save(consignmentEntry));
			getModelService().save(sapReturnRequestOrderModel);

		}

	}


	/**
	 * This method send the cloned return request to data hub.
	 *
	 * @param order
	 *           - original order
	 * @param results
	 *           - This is a list of SendToDataHubResult. It contains the status of each result.
	 * @param consignments-set
	 *           of consingmentEntry
	 * @param returnRequest-
	 *           Original return request model.
	 * @param sapReturnRequestOrders
	 *           It contains the list of SAPReturnRequest
	 */
	private void sendOMSReturnOrder(final OrderModel order, final List<SendToDataHubResult> results,
			final Set<ConsignmentEntryModel> consignments, final ReturnRequestModel returnRequest,
			final List<SAPReturnRequestsModel> sapReturnRequestOrders)
	{

		final ReturnRequestModel clonedReturnModel = modelService.clone(returnRequest);

		updateLogicalSystem(order, consignments, clonedReturnModel);
		final List<ReturnEntryModel> returnOrderEntryList = new ArrayList<>();
		final String cloneReturnedRequestCode = keyGeneratorLookup.lookupGenerator(clonedReturnModel.getSapLogicalSystem());
		//setting cloned return request code.
		clonedReturnModel.setCode(cloneReturnedRequestCode);
		createSapReturnRequestOrder(sapReturnRequestOrders, consignments, cloneReturnedRequestCode);
		consignments.stream().forEach(consignmentEntry -> {
			final RefundEntryModel refundEntryModel = modelService.create(RefundEntryModel.class);
			refundEntryModel.setOrderEntry(consignmentEntry.getOrderEntry());
			refundEntryModel.setReturnRequest(clonedReturnModel);
			refundEntryModel.setExpectedQuantity(consignmentEntry.getReturnQuantity());
			refundEntryModel.setAmount(consignmentEntry.getAmount());
			refundEntryModel.setReason(findRefundReason(returnRequest, consignmentEntry.getOrderEntry()));
			returnOrderEntryList.add(refundEntryModel);
		});
		clonedReturnModel.setReturnEntries(returnOrderEntryList);

		LOGGER.info("Sending Return Order to data hub");
		results.add(sendReturnOrderToDataHubHelper.createAndSendRawItem(clonedReturnModel));
	}

	/**
	 * This method will find refund reason for each order entry.
	 */
	protected RefundReason findRefundReason(final ReturnRequestModel returnRequest, final AbstractOrderEntryModel orderEntry)
	{
		final List<ReturnEntryModel> returnEntryList = returnRequest.getReturnEntries();
		for (final ReturnEntryModel returnEntryModel : returnEntryList)
		{
			if (orderEntry.equals(returnEntryModel.getOrderEntry()))
			{
				final RefundEntryModel refundEntry = (RefundEntryModel) returnEntryModel;
				return refundEntry.getReason();
			}
		}
		return null;
	}

	/**
	 * This method will set logical system and sales organization details in cloned order model.
	 *
	 * @param order-
	 *           original order
	 * @param consignments-
	 *           set of consignment
	 * @param cloneReturnModel-
	 *           cloned Return request Model
	 */
	protected void updateLogicalSystem(final OrderModel order, final Set<ConsignmentEntryModel> consignments,
			final ReturnRequestModel cloneReturnModel)
	{
		final SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
				order.getStore(), consignments.stream().findFirst().get().getConsignment().getWarehouse().getCode());
		cloneReturnModel.setSapLogicalSystem(sapPlantLogSysOrgModel.getLogSys().getSapLogicalSystemName());
		cloneReturnModel.setSapSalesOrganization(sapPlantLogSysOrgModel.getSalesOrg());
		cloneReturnModel.setSapSystemType(sapPlantLogSysOrgModel.getLogSys().getSapSystemType());
		consignments.stream().forEach(consignment -> LOGGER.debug(" Products: " + consignment.getOrderEntry().getProduct().getName()
				+ " Quantity to be Return : " + consignment.getReturnQuantity()));

		LOGGER.debug(" Logical system: " + sapPlantLogSysOrgModel.getLogSys().getSapLogicalSystemName() + " Returned to plant :"
				+ sapPlantLogSysOrgModel.getSalesOrg().getSalesOrganization() + YsapomsreturnprocessConstants.UNDERSCORE
				+ sapPlantLogSysOrgModel.getSalesOrg().getDistributionChannel() + YsapomsreturnprocessConstants.UNDERSCORE
				+ sapPlantLogSysOrgModel.getSalesOrg().getDivision());
	}

	/**
	 * This method is used to create SAP Return request and this model in db for reference.
	 *
	 * @param sapReturnRequestOrders-
	 *           List of return request order
	 * @param consignments-
	 *           set of consignment
	 * @param cloneReturnedRequestCode-cloned
	 *           return request code value
	 */
	private void createSapReturnRequestOrder(final List<SAPReturnRequestsModel> sapReturnRequestOrders,
			final Set<ConsignmentEntryModel> consignments, final String cloneReturnedRequestCode)
	{
		final SAPReturnRequestsModel sapReturnRequestOrder = modelService.create(SAPReturnRequestsModel.class);
		sapReturnRequestOrder.setCode(cloneReturnedRequestCode);
		sapReturnRequestOrder.setReturnWarehouse(consignments.stream().findFirst().get().getConsignment().getWarehouse());
		sapReturnRequestOrder.setConsignmentsEntry(consignments);

		sapReturnRequestOrders.add(sapReturnRequestOrder);

	}

	/**
	 * This method group order entry as per the logical system and map it to the consignment set.
	 *
	 * @param order-
	 *           original order
	 * @param orderEntryConsignmentMap
	 *           - this map contains the mapping b/w each order entry and set of consignment entry model associated with
	 *           it.
	 * @return set of consignmentEntry set.
	 */
	private Set<Set<ConsignmentEntryModel>> groupReturnOrderConsignments(final OrderModel order,
			final Map<ReturnEntryModel, List<ConsignmentEntryModel>> orderEntryConsignmentMap)
	{

		final Set<ConsignmentEntryModel> entryModelSet = new HashSet<>();
		orderEntryConsignmentMap.values().stream().forEach(consignmentEntryModelList -> consignmentEntryModelList.stream()
				.forEach(consignmentEntryModel -> entryModelSet.add(consignmentEntryModel)));

		final Map<String, Map<String, Set<ConsignmentEntryModel>>> mapByLogSysSalesOrg = entryModelSet.stream()
				.collect(Collectors.groupingBy(consignmentEntry -> getSapLogSysName(order, consignmentEntry),
						Collectors.groupingBy(consignmentEntry -> getSapSalesOrgName(order, consignmentEntry), Collectors.toSet())));

		final Set<Set<ConsignmentEntryModel>> consignmentSets = new HashSet<Set<ConsignmentEntryModel>>();

		mapByLogSysSalesOrg.entrySet().stream().forEach(entryKey -> entryKey.getValue().entrySet().stream()
				.forEach(entryValue -> consignmentSets.add(entryValue.getValue())));

		/*
		 * Sample data
		 *
		 * [ [ConsignmentModel (8031240100875), ConsignmentModel (8437240100353)], [ConsignmentModel(8876240068231)],
		 *
		 * [ConsignmentModel (8980240100409), ConsignmentModel (8434240100619)], [ConsignmentModel(8261240068262)], ],
		 */
		return consignmentSets;
	}

	/**
	 * This method is used to append sales organization, distribution channel and division which fetch this information
	 * from consignment Entry.
	 *
	 * @param order-original
	 *           order
	 * @param consignmentEntry
	 *           - consignmentEntry will be used to get warehouse details
	 * @return combination of sales organization, distribution channel and division {1000_10_00}
	 */
	protected String getSapSalesOrgName(final OrderModel order, final ConsignmentEntryModel consignmentEntry)
	{
		final ConsignmentModel consignment = consignmentEntry.getConsignment();

		final SAPSalesOrganizationModel salesOrganization = getSapPlantLogSysOrgService()
				.getSapSalesOrganizationForPlant(order.getStore(), consignment.getWarehouse().getCode());

		if (salesOrganization.getSalesOrganization() != null)
		{

			return new StringBuilder(salesOrganization.getSalesOrganization()).append(YsapomsreturnprocessConstants.UNDERSCORE)
					.append(salesOrganization.getDistributionChannel()).append(YsapomsreturnprocessConstants.UNDERSCORE)
					.append(salesOrganization.getDivision()).toString();
		}

		return YsapomsreturnprocessConstants.MISSING_SALES_ORG;
	}

	/**
	 * This method fetch the logical system name to which the consignment has to return.
	 *
	 * @param order-original
	 *           order
	 * @param consignmentEntry
	 *           - consignmentEntry will be used to get warehouse details
	 * @return logical system name
	 */
	protected String getSapLogSysName(final OrderModel order, final ConsignmentEntryModel consignmentEntry)
	{
		final ConsignmentModel consignment = consignmentEntry.getConsignment();
		final String sapLogicalSystemName = getSapPlantLogSysOrgService()
				.getSapLogicalSystemForPlant(order.getStore(), consignment.getWarehouse().getCode()).getSapLogicalSystemName();

		if (sapLogicalSystemName != null)
		{
			return sapLogicalSystemName;
		}

		return YsapomsreturnprocessConstants.MISSING_LOGICAL_SYSTEM;
	}

	protected void resetEndMessage(final ReturnProcessModel process)
	{
		final String endMessage = process.getEndMessage();
		if (ERROR_END_MESSAGE.equals(endMessage))
		{
			process.setEndMessage("");
			modelService.save(process);
		}
	}

}
