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
package de.hybris.platform.yacceleratorordermanagement.integration.util;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.returns.OrderReturnRecordsHandlerException;
import de.hybris.platform.returns.ReturnService;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertEquals;


/**
 * this class is mainly to create and modify returns
 */
@Component
public class ReturnUtil extends ProcessUtil
{
	protected static final String APPROVE_RETURN_ACTION_EVENT_NAME = "ConfirmOrCancelRefundEvent";
	protected static final String APPROVE_RETURN_CHOICE = "approveReturn";
	protected static final String WAITFORGOODS_RETURN_ACTION_EVENT_NAME = "ApproveOrCancelGoodsEvent";
	protected static final String WAITFORGOODS_RETURN_CHOICE = "acceptGoods";
	protected static final String CANCEL_RETURN_CHOICE = "cancelReturn";
	private static final Logger LOG = LoggerFactory.getLogger(ReturnUtil.class);

	/**
	 * wait for return status
	 *
	 * @param returnProcess
	 * @param returnRequest
	 * @param returnStatus
	 * @param timeOut
	 * @throws InterruptedException
	 */
	public void waitForReturnStatus(final ReturnProcessModel returnProcess, final ReturnRequestModel returnRequest,
			final ReturnStatus returnStatus,
			final int timeOut) throws InterruptedException
	{
		int timeCount = 0;
		do
		{
			Thread.sleep(1000);
			getModelService().refresh(returnRequest);
		}
		while (!returnStatus.equals(returnProcess.getReturnRequest().getStatus()) && timeCount++ < timeOut);
		getModelService().refresh(returnRequest);
	}

	public ReturnProcessModel runDefaultReturnProcessForOrder(final ReturnRequestModel returnRequest, final ReturnStatus status)
			throws InterruptedException
	{
		ReturnProcessModel returnProcessModel = getBusinessProcessService().getProcess(
				RETURN_PROCESS_DEFINITION_NAME + "-" + returnRequest.getCode());
		if (returnProcessModel != null)
		{
			getModelService().remove(returnProcessModel);
		}
		returnProcessModel = getBusinessProcessService().<ReturnProcessModel>createProcess(
				RETURN_PROCESS_DEFINITION_NAME + "-" + returnRequest.getCode(), RETURN_PROCESS_DEFINITION_NAME);

		returnProcessModel.setReturnRequest(returnRequest);

		getModelService().save(returnProcessModel);
		assertProcessState(returnProcessModel, ProcessState.CREATED);
		businessProcessService.startProcess(returnProcessModel);
		LOG.info("Return process" + RETURN_PROCESS_DEFINITION_NAME + " started");
		waitForReturnStatus(returnProcessModel, returnRequest, status, timeOut);
		assertEquals(status, returnProcessModel.getReturnRequest().getStatus());
		LOG.info("Return process is in state " + returnProcessModel.getReturnRequest().getStatus());
		return returnProcessModel;
	}

	/**
	 * create return request
	 *
	 * @param orderModel
	 * @param expectedQuantity
	 * @param action
	 * @param refundReason
	 * @param refundAmount
	 * @param orderEntry
	 * @return
	 */
	public ReturnRequestModel createDefaultReturnRequest(final OrderModel orderModel, Long expectedQuantity,
			final ReturnAction action, final RefundReason refundReason, final BigDecimal refundAmount,
			final AbstractOrderEntryModel orderEntry)
	{
		Map<AbstractOrderEntryModel, Long> refundMap = new HashMap<>();
		refundMap.put(orderEntry, expectedQuantity);
		return createDefaultReturnRequest(orderModel, action, refundReason, refundAmount, refundMap);
	}

	/**
	 * create return request
	 *
	 * @param orderModel
	 * @param action
	 * @param refundReason
	 * @param refundAmount
	 * @param refundMap
	 * @return
	 */
	public ReturnRequestModel createDefaultReturnRequest(final OrderModel orderModel, final ReturnAction action,
			final RefundReason refundReason, final BigDecimal refundAmount, final Map<AbstractOrderEntryModel, Long> refundMap)
	{
		final ReturnRequestModel request = returnService.createReturnRequest(orderModel);
		refundMap.forEach((o, q) -> {
			RefundEntryModel returnEntry = returnService.createRefund(request, o, "", q, action, refundReason);
			returnEntry.setAmount(refundAmount);
			getModelService().save(returnEntry);
			// Recalculate the subTotal after updating a refund amount
			request.setSubtotal(
					request.getReturnEntries().stream().filter(entry -> entry instanceof RefundEntryModel)
							.map(refundEntry -> ((RefundEntryModel) refundEntry).getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add));
			getModelService().save(request);
		});
		try
		{
			refreshOrder(orderModel);
			refundService.apply(orderModel, request);
		}
		catch (OrderReturnRecordsHandlerException e1)
		{
			e1.printStackTrace();
		}
		catch (IllegalStateException ise)
		{
			LOG.info("Order " + orderModel.getCode() + " Return record already in progress");
		}
		return request;
	}

	public void approveDefaultReturn(final ReturnRequestModel request)
	{
		triggerReturnEvent(request, APPROVE_RETURN_ACTION_EVENT_NAME, APPROVE_RETURN_CHOICE);
	}

	public void cancelDefaultReturn_AfterApproval(final ReturnRequestModel request)
	{
		triggerReturnEvent(request, WAITFORGOODS_RETURN_ACTION_EVENT_NAME, CANCEL_RETURN_CHOICE);
	}

	public void cancelDefaultReturn_AfterCreation(final ReturnRequestModel request)
	{
		triggerReturnEvent(request, APPROVE_RETURN_ACTION_EVENT_NAME, CANCEL_RETURN_CHOICE);
	}

	public void confirmWaitForGoodsDefaultReturn(final ReturnRequestModel request)
	{
		triggerReturnEvent(request, WAITFORGOODS_RETURN_ACTION_EVENT_NAME, WAITFORGOODS_RETURN_CHOICE);
	}

	/**
	 * trigger return event
	 *
	 * @param request
	 * @param eventName
	 * @param eventAction
	 */
	public void triggerReturnEvent(final ReturnRequestModel request, final String eventName, final String eventAction)
	{
		try
		{
			refreshReturnRequest(request);
			waitUntilReturnProcessIsNotRunning(request, timeOut);
			getReturnBusinessProcessService().triggerChoiceEvent(request, eventName,
					eventAction);
			waitUntilReturnProcessIsNotRunning(request, timeOut);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void refreshReturnRequest(final ReturnRequestModel returnRequest)
	{
		getModelService().refresh(returnRequest);
		returnRequest.getReturnEntries().stream().forEach(e -> getModelService().refresh(e));
		getModelService().refresh(returnRequest.getStatus());
	}

	public ReturnService getReturnService()
	{
		return returnService;
	}

	public void setReturnService(final ReturnService returnService)
	{
		this.returnService = returnService;
	}
}
