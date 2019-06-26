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
package de.hybris.platform.apiregistryservices.event.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.messaging.Message;

import de.hybris.platform.apiregistryservices.strategies.EventEmitStrategy;

/**
 * Default service activator, able to send event to emitStrategy only
 */
public class EventServiceActivator
{
	private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceActivator.class);

	private EventEmitStrategy eventEmitStrategy;

	public void handle(final Message message)
	{
		LOGGER.info("{}", message);
		eventEmitStrategy.sendEvent(message);
	}

	protected EventEmitStrategy getEventEmitStrategy()
	{
		return eventEmitStrategy;
	}

	@Required
	public void setEventEmitStrategy(final EventEmitStrategy eventEmitStrategy)
	{
		this.eventEmitStrategy = eventEmitStrategy;
	}
}
