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
package de.hybris.platform.yacceleratorordermanagement.actions.order.payment;

import de.hybris.platform.orderprocessing.events.PaymentFailedEvent;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.event.EventService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Send a notification that payment failed to execute for an order.
 */
public class SendPaymentFailedNotificationAction extends AbstractProceduralAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SendPaymentFailedNotificationAction.class);
	private EventService eventService;

	@Override
	public void executeAction(final OrderProcessModel process)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info("Process: {} in step {}", process.getCode(), getClass().getSimpleName());
		}
		getEventService().publishEvent(new PaymentFailedEvent(process));
	}

	protected EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}
