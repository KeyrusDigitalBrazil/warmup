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


import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.warehousing.process.WarehousingBusinessProcessService;

import org.springframework.stereotype.Component;


/**
 * this class is mainly to create and modify processes
 */
@Component
public class ProcessUtil extends BaseUtil
{
	protected static final String ORDER_TEST_PROCESS = "orderTest";
	protected static final String ORDER_PROCESS_DEFINITION_NAME = "order-process";
	protected static final String RETURN_PROCESS_DEFINITION_NAME = "return-process";
	protected static final String CONSIGNMENT_ACTION_EVENT_NAME = "ConsignmentActionEvent";
	protected static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	protected final static int timeOut = 15; //seconds

	/**
	 * wait until process is not running
	 *
	 * @param process
	 * @param timeOut
	 * @throws InterruptedException
	 */
	public void waitUntilProcessIsNotRunning(final BusinessProcessModel process, final int timeOut) throws InterruptedException
	{
		int timeCount = 0;
		do
		{
			Thread.sleep(1000);
			getModelService().refresh(process);
		}
		while (ProcessState.RUNNING.equals(process.getState()) && timeCount++ < timeOut);
	}

	public void waitUntilReturnProcessExist(final ReturnRequestModel returnRequest, final String processName, final int timeOut)
			throws InterruptedException
	{
		int timeCount = 0;
		long returnProcess;
		do
		{
			Thread.sleep(1000);
			getModelService().refresh(returnRequest);
			returnProcess = returnRequest.getReturnProcess().stream().filter(p -> p.getCode().contains(processName)).count();
		}
		while (returnProcess != 1L && timeCount++ < timeOut);
	}

	/**
	 * wait for consignment process is not running
	 *
	 * @param orderProcessModel
	 * @param consignment
	 * @param timeOut
	 * @throws InterruptedException
	 */
	public void waitUntilConsignmentProcessIsNotRunning(final OrderProcessModel orderProcessModel,
			final ConsignmentModel consignment, final int timeOut) throws InterruptedException
	{
		int timeCount = 0;
		do
		{
			Thread.sleep(1000);
			getModelService().refresh(orderProcessModel);
			getModelService().refresh(consignment);
		}
		while (ProcessState.RUNNING.equals(consignment.getConsignmentProcesses().iterator().next().getProcessState())
				&& ProcessState.RUNNING.equals(orderProcessModel.getProcessState()) && timeCount++ < timeOut);
	}

	/**
	 * wait for return process is not running
	 *
	 * @param returnRequestModel
	 * @param timeOut
	 * @throws InterruptedException
	 */
	public void waitUntilReturnProcessIsNotRunning(final ReturnRequestModel returnRequestModel, final int timeOut)
			throws InterruptedException
	{
		int timeCount = 0;
		do
		{
			Thread.sleep(1000);
			getModelService().refresh(returnRequestModel);
		}
		while (returnRequestModel.getReturnProcess().stream().allMatch(p -> ProcessState.RUNNING.equals(p.getProcessState()))
				&& timeCount++ < timeOut);
	}

	/**
	 * Moves the {@link OrderProcessModel} according to the given choice and waits for the consignment and order process to finish or until timeout occurs
	 *
	 * @param order
	 * 		the given {@link OrderModel}
	 * @param orderProcessModel
	 * 		the associated {@link OrderProcessModel} with the given {@link OrderModel}
	 * @param choice
	 * 		the choice with which to move the given {@link OrderProcessModel}
	 * @throws InterruptedException
	 */
	public void moveOrderProcess(final OrderModel order, final OrderProcessModel orderProcessModel, final String choice)
			throws InterruptedException
	{
		final ConsignmentModel consignmentResult = this.order.getConsignments().iterator().next();

		orderBusinessProcessService.triggerEvent(
				BusinessProcessEvent.builder(orderProcessModel.getCode() + "_" + ORDER_ACTION_EVENT_NAME).withChoice(choice)
						.withEventTriggeringInTheFutureDisabled().build());
		waitUntilConsignmentProcessIsNotRunning(orderProcessModel, consignmentResult, timeOut);
		waitUntilProcessIsNotRunning(orderProcessModel, timeOut);
	}

	protected WarehousingBusinessProcessService<ReturnRequestModel> getReturnBusinessProcessService()
	{
		return returnBusinessProcessService;
	}

	public void setReturnBusinessProcessService(
			final WarehousingBusinessProcessService<ReturnRequestModel> returnBusinessProcessService)
	{
		this.returnBusinessProcessService = returnBusinessProcessService;
	}

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}
}
