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
package de.hybris.platform.yacceleratorordermanagement.impl;

import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.returns.ReturnActionAdapter;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.Collection;


/**
 * Specific OMS adapter implementation for {@link ReturnActionAdapter}
 */
public class OmsReturnActionAdapter implements ReturnActionAdapter
{
	protected static final String CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME = "ConfirmOrCancelRefundEvent";
	protected static final String FAIL_CAPTURE_ACTION_EVENT_NAME = "FailCaptureActionEvent";
	protected static final String APPROVE_CANCEL_GOODS_EVENT_NAME = "ApproveOrCancelGoodsEvent";
	protected static final String WAIT_FOR_FAIL_CAPTURE_ACTION = "waitForFailCaptureAction";
	protected static final String WAIT_FOR_CONFIRM_OR_CANCEL_REFUND_ACTION = "waitForConfirmOrCancelReturnAction";
	protected static final String WAIT_FOR_GOODS_ACTION = "waitForGoodsAction";
	protected static final String FAIL_CAPTURE_EVENT = "FailCaptureActionEvent";

	protected static final String APPROVAL_CHOICE = "approveReturn";
	protected static final String ACCEPT_GOODS_CHOICE = "acceptGoods";
	protected static final String CANCEL_REFUND_CHOICE = "cancelReturn";
	protected static final String BY_PASS_CAPTURE = "bypassCapture";
	protected static final String TAX_REVERSE_EVENT_NAME = "FailTaxReverseEvent";

	private BusinessProcessService businessProcessService;

	@Override
	public void requestReturnApproval(final ReturnRequestModel returnRequest)
	{
		validateReturnRequest(returnRequest);

		returnRequest.getReturnProcess().stream()
				.filter(process -> process.getCode().startsWith(returnRequest.getOrder().getStore().getCreateReturnProcessCode()))
				.forEach(filteredProcess -> getBusinessProcessService().triggerEvent(
						BusinessProcessEvent.builder(filteredProcess.getCode() + "_" + CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME)
								.withChoice(APPROVAL_CHOICE).withEventTriggeringInTheFutureDisabled().build()));
	}

	@Override
	public void requestReturnReception(final ReturnRequestModel returnRequest)
	{
		validateReturnRequest(returnRequest);

		returnRequest.getReturnProcess().stream()
				.filter(process -> process.getCode().startsWith(returnRequest.getOrder().getStore().getCreateReturnProcessCode()))
				.forEach(filteredProcess -> getBusinessProcessService().triggerEvent(
						BusinessProcessEvent.builder(filteredProcess.getCode() + "_" + APPROVE_CANCEL_GOODS_EVENT_NAME)
								.withChoice(ACCEPT_GOODS_CHOICE).withEventTriggeringInTheFutureDisabled().build()));

	}

	@Override
	public void requestReturnCancellation(final ReturnRequestModel returnRequest)
	{
		validateReturnRequest(returnRequest);

		returnRequest.getReturnProcess().stream()
				.filter(process -> process.getCode().startsWith(returnRequest.getOrder().getStore().getCreateReturnProcessCode()))
				.forEach(filteredProcess -> cancelReturnRequest(returnRequest, filteredProcess));
	}

	@Override
	public void requestManualPaymentReversalForReturnRequest(final ReturnRequestModel returnRequest)
	{
		validateReturnRequest(returnRequest);

		returnRequest.getReturnProcess().stream()
				.filter(process -> process.getCode().startsWith(returnRequest.getOrder().getStore().getCreateReturnProcessCode()))
				.forEach(filteredProcess ->
				{
					final BusinessProcessEvent businessProcessEvent = BusinessProcessEvent
							.builder(filteredProcess.getCode() + "_" + FAIL_CAPTURE_EVENT).withChoice(BY_PASS_CAPTURE)
							.withEventTriggeringInTheFutureDisabled().build();
					getBusinessProcessService().triggerEvent(businessProcessEvent);
				});
	}

	@Override
	public void requestManualTaxReversalForReturnRequest(final ReturnRequestModel returnRequest)
	{
		validateReturnRequest(returnRequest);

		returnRequest.getReturnProcess().stream()
				.filter(process -> process.getCode().startsWith(returnRequest.getOrder().getStore().getCreateReturnProcessCode()))
				.forEach(filteredProcess -> getBusinessProcessService()
						.triggerEvent(filteredProcess.getCode() + "_" + TAX_REVERSE_EVENT_NAME));
	}

	/**
	 * Validates {@link ReturnRequestModel} before triggering an event in returns workflow
	 *
	 * @param returnRequest
	 */
	protected void validateReturnRequest(final ReturnRequestModel returnRequest)
	{
		Assert.notNull(returnRequest, "ReturnRequest cannot be null");
		Assert.isTrue(CollectionUtils.isNotEmpty(returnRequest.getReturnProcess()),
				String.format("No return process found for the ReturnRequest [%s]", returnRequest.getCode()));

		Assert.notNull(returnRequest.getOrder(),
				String.format("Order can not be null for the requested ReturnRequest [%s]", returnRequest.getCode()));
		Assert.notNull(returnRequest.getOrder().getStore(),
				String.format("Store can not be null for the requested ReturnRequest [%s]", returnRequest.getCode()));
	}

	/**
	 * Cancels the given {@link ReturnRequestModel} by triggering a {@link BusinessProcessEvent}
	 * depending on the state of the {@link ReturnProcessModel}
	 *
	 * @param returnRequest
	 * 		the {@link ReturnRequestModel} for which a process event will be triggered
	 * @param filteredProcess
	 * 		{@link ReturnProcessModel}
	 */
	protected void cancelReturnRequest(final ReturnRequestModel returnRequest, final ReturnProcessModel filteredProcess)
	{
		String event = null;

		final Collection<ProcessTaskModel> currentTasks = filteredProcess.getCurrentTasks();
		Assert.isTrue(CollectionUtils.isNotEmpty(currentTasks),
				String.format("No available process tasks found for the ReturnRequest to be cancelled [%s]",
						returnRequest.getCode()));
		if (currentTasks.stream().anyMatch(task -> WAIT_FOR_FAIL_CAPTURE_ACTION.equals(task.getAction())))//NOSONAR
		{
			event = FAIL_CAPTURE_ACTION_EVENT_NAME;
		}
		else if (currentTasks.stream().anyMatch(task -> WAIT_FOR_CONFIRM_OR_CANCEL_REFUND_ACTION.equals(task.getAction())))
		{
			event = CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME;
		}
		else if (currentTasks.stream().anyMatch(task -> WAIT_FOR_GOODS_ACTION.equals(task.getAction())))
		{
			event = APPROVE_CANCEL_GOODS_EVENT_NAME;
		}

		getBusinessProcessService().triggerEvent(
				BusinessProcessEvent.builder(filteredProcess.getCode() + "_" + event).withChoice(CANCEL_REFUND_CHOICE)
						.withEventTriggeringInTheFutureDisabled().build());
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

}
