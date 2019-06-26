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
package de.hybris.platform.acceleratorservices.payment.strategies.impl;

import de.hybris.platform.acceleratorservices.payment.strategies.FraudCallbackStrategy;
import java.util.Map;
import org.apache.log4j.Logger;


public class DefaultFraudCallbackStrategy implements FraudCallbackStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultFraudCallbackStrategy.class);

	@Override
	public void handleFraudCallback(final Map<String, String> parameters)
	{
		LOG.warn("An empty implementation of fraudulent transaction handling called.");
	}
}
