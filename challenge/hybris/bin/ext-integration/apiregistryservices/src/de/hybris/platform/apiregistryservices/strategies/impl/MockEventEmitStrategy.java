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
package de.hybris.platform.apiregistryservices.strategies.impl;

import de.hybris.platform.apiregistryservices.strategies.EventEmitStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Mock impl of @{@link EventEmitStrategy}
 */
public class MockEventEmitStrategy implements EventEmitStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(MockEventEmitStrategy.class);

	@Override
	public void sendEvent(final Object payload)
	{
		if (LOG.isInfoEnabled())
		{
			LOG.info(MessageFormat.format("Sending event : {0}", payload.getClass()));
		}
	}
}
