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
package com.sap.hybris.sapomsreturnprocess.adapter.impl;

import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.yacceleratorordermanagement.impl.OmsReturnActionAdapter;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;

import com.sap.hybris.returnsexchange.constants.SapreturnsexchangeConstants;


/**
 *
 */
public class SapOmsReturnActionAdapter extends OmsReturnActionAdapter
{
	public static final String WAIT_FOR_NOTIFICATION_FROM_BACKEND = "waitForNotificationfromBackEnd";
	public static final String STILL_WAIT_FOR_CONFIRMATION_FROM_BACKEND = "stillWaitForConfirmationFromBackend";
	
	@Override
	protected void cancelReturnRequest(final ReturnRequestModel returnRequest, final ReturnProcessModel filteredProcess)
	{
		String event = null;
		final String orderNumber = returnRequest.getCode();
		String eventName = null;
		final Collection<ProcessTaskModel> currentTasks = filteredProcess.getCurrentTasks();		
	
		Assert.isTrue(CollectionUtils.isNotEmpty(currentTasks),
				String.format("No available process tasks found for the ReturnRequest to be cancelled [%s]", returnRequest.getCode()));
		if(currentTasks!=null)
		{
			if (currentTasks.stream().anyMatch(task -> WAIT_FOR_FAIL_CAPTURE_ACTION.equals(task.getAction())))
			{
				event = FAIL_CAPTURE_ACTION_EVENT_NAME;
				eventName = filteredProcess.getCode() + "_" + event;
			}
			else if (currentTasks.stream().anyMatch(task -> WAIT_FOR_CONFIRM_OR_CANCEL_REFUND_ACTION.equals(task.getAction())))
			{
				event = CONFIRM_OR_CANCEL_REFUND_ACTION_EVENT_NAME;
				eventName = filteredProcess.getCode() + "_" + event;
			}
			else if (currentTasks.stream().anyMatch(task -> WAIT_FOR_GOODS_ACTION.equals(task.getAction())) && !orderNumber.isEmpty())
			{
				eventName = SapreturnsexchangeConstants.RETURNORDER_GOOD_EVENT + orderNumber;
			}
			else if ((currentTasks.stream().anyMatch(task -> WAIT_FOR_NOTIFICATION_FROM_BACKEND.equals(task.getAction()))
				|| currentTasks.stream().anyMatch(task -> STILL_WAIT_FOR_CONFIRMATION_FROM_BACKEND.equals(task.getAction())))
				&& !orderNumber.isEmpty())
			{
				eventName = SapreturnsexchangeConstants.RETURNORDER_CONFIRMATION_EVENT + orderNumber;
			}
		}
		getBusinessProcessService().triggerEvent(
				BusinessProcessEvent.builder(eventName).withChoice(CANCEL_REFUND_CHOICE).withEventTriggeringInTheFutureDisabled()
						.build());
	}
}


