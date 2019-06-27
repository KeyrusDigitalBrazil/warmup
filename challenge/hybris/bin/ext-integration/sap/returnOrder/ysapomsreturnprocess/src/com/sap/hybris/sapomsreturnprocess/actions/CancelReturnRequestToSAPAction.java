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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubHelper;
import de.hybris.platform.sap.orderexchange.outbound.SendToDataHubResult;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPReturnRequestsModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;



/**
 *
 */
public class CancelReturnRequestToSAPAction extends SendOmsReturnOrderToDataHubAction
{

	private static final Logger LOGGER = Logger.getLogger(CancelReturnRequestToSAPAction.class);
	public static final String ERROR_END_MESSAGE = "Sending to Datahub went wrong.";
	private SendToDataHubHelper<ReturnRequestModel> sendCancelReturnOrderToDatahubHelper;

	@Override
	public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException
	{
		final ReturnRequestModel returnRequest = process.getReturnRequest();
		final OrderModel order = returnRequest.getOrder();

		final List<SendToDataHubResult> results = new ArrayList<>();
		final Set<SAPReturnRequestsModel> sapReturnRequests = returnRequest.getSapReturnRequests();

		for (final SAPReturnRequestsModel sapReturnRequest : sapReturnRequests)
		{
			sendOMSReturnOrder(order, results, sapReturnRequest.getConsignmentsEntry(), returnRequest, sapReturnRequest);
		}

		if (!results.isEmpty() && results.stream().allMatch(result -> result.isSuccess()))
		{

			resetEndMessage(process);
			LOGGER.info("Cancel Return order sent successfully");
			return Transition.OK;

		}
		else
		{
			LOGGER.info("Cancel Return order not sent.");
			return Transition.NOK;
		}

	}

	/**
	 * This method send the cloned return request to data hub.
	 *
	 * @param order
	 *           - original order
	 * @param results
	 *           - This is a list of SendToDataHubResult. It contains the status of each result.
	 * @param consignments
	 *           -set of consingmentEntry
	 * @param returnRequest
	 *           - Original return request model.
	 * @param sapReturnRequestOrders
	 *           It contains the list of SAPReturnRequest
	 */
	private void sendOMSReturnOrder(final OrderModel order, final List<SendToDataHubResult> results,
			final Set<ConsignmentEntryModel> consignments, final ReturnRequestModel returnRequest,
			final SAPReturnRequestsModel sapReturnRequest)
	{

		final ReturnRequestModel clonedReturnModel = modelService.clone(returnRequest);
		updateLogicalSystem(order, consignments, clonedReturnModel);
		final List<ReturnEntryModel> returnOrderEntryList = new ArrayList<>();
		final String cloneReturnedRequestCode = sapReturnRequest.getCode();
		clonedReturnModel.setCode(cloneReturnedRequestCode);
		consignments.stream().forEach(
				consignmentEntry -> {
					final RefundEntryModel refundEntryModel = modelService.create(RefundEntryModel.class);
					refundEntryModel.setOrderEntry(consignmentEntry.getOrderEntry());
					refundEntryModel.setReturnRequest(clonedReturnModel);
					refundEntryModel.setExpectedQuantity(consignmentEntry.getReturnQuantity());
					refundEntryModel.setReason(findRefundReason(returnRequest, consignmentEntry.getOrderEntry()));
					returnOrderEntryList.add(refundEntryModel);

					for (final ReturnEntryModel returnEntry : returnRequest.getReturnEntries())
					{
						for (final ConsignmentEntryModel orderConsignmentEntry : returnEntry.getOrderEntry().getConsignmentEntries())
						{
							if (orderConsignmentEntry.getPk().equals(consignmentEntry.getPk()))
							{
								orderConsignmentEntry.setQuantityReturnedUptil(consignmentEntry.getQuantityReturnedUptil()
										- consignmentEntry.getReturnQuantity());
								modelService.save(orderConsignmentEntry);
							}
						}
					}
				});

		sapReturnRequest.setConsignmentsEntry(consignments);

		clonedReturnModel.setReturnEntries(returnOrderEntryList);

		LOGGER.info("Sending Cancel update for return order to data hub");
		results.add(sendCancelReturnOrderToDatahubHelper.createAndSendRawItem(clonedReturnModel));
	}

	@Override
	protected void updateLogicalSystem(final OrderModel order, final Set<ConsignmentEntryModel> consignments,
			final ReturnRequestModel cloneReturnModel)
	{
		super.updateLogicalSystem(order, consignments, cloneReturnModel);
		final SAPPlantLogSysOrgModel sapPlantLogSysOrgModel = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
				order.getStore(), consignments.stream().findFirst().get().getConsignment().getWarehouse().getCode());
		cloneReturnModel.setReasonCodeCancellation(sapPlantLogSysOrgModel.getReasonCodeCancellation());
	}

	/**
	 * @return the sendCancelReturnOrderToDatahubHelper
	 */
	public SendToDataHubHelper<ReturnRequestModel> getSendCancelReturnOrderToDatahubHelper()
	{
		return sendCancelReturnOrderToDatahubHelper;
	}

	/**
	 * @param sendCancelReturnOrderToDatahubHelper
	 *           the sendCancelReturnOrderToDatahubHelper to set
	 */
	public void setSendCancelReturnOrderToDatahubHelper(
			final SendToDataHubHelper<ReturnRequestModel> sendCancelReturnOrderToDatahubHelper)
	{
		this.sendCancelReturnOrderToDatahubHelper = sendCancelReturnOrderToDatahubHelper;
	}


}
