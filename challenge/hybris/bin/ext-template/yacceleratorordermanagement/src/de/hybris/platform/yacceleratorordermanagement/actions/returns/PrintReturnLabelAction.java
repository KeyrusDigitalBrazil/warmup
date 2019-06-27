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

package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.platform.warehousing.event.SendReturnLabelEvent;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Creates the return label for the {@link de.hybris.platform.returns.model.ReturnRequestModel}
 */
public class PrintReturnLabelAction extends AbstractProceduralAction<ReturnProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(PrintReturnLabelAction.class);
	private EventService eventService;

	@Override
	public void executeAction(final ReturnProcessModel returnProcessModel) throws RetryLaterException, Exception
	{
		LOG.info("Process: {} in step {}", returnProcessModel.getCode(), getClass().getSimpleName());
		validateParameterNotNull(returnProcessModel, "ReturnProcess cannot be null");

		final ReturnRequestModel returnRequestModel = returnProcessModel.getReturnRequest();
		validateParameterNotNull(returnRequestModel, "ReturnRequest cannot be null");

		final SendReturnLabelEvent sendReturnLabelEvent = new SendReturnLabelEvent();
		sendReturnLabelEvent.setReturnRequest(returnProcessModel.getReturnRequest());
		getEventService().publishEvent(sendReturnLabelEvent);
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	protected EventService getEventService()
	{
		return eventService;
	}

}
