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
package de.hybris.platform.sap.productconfig.services.event.impl;

import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.event.events.BeforeSessionCloseEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;


/**
 * This bean listens to the {@link BeforeSessionCloseEvent}. It will clear configurations stored in the expired user
 * session which do not have any order.
 */
public class ProductConfigSessionCloseEventListener extends AbstractEventListener<BeforeSessionCloseEvent>
{

	private static final Logger LOG = Logger.getLogger(ProductConfigSessionCloseEventListener.class);

	@Override
	protected void onEvent(final BeforeSessionCloseEvent evt)
	{
		logUserInfo(evt);
		getConfigurationLifecycleStrategy().releaseExpiredSessions(getProductConfigEventListenerUtil().getUserSessionId(evt));
	}

	protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getSessionAccessService().");
	}

	protected ProductConfigEventListenerUtil getProductConfigEventListenerUtil()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getSessionAccessService().");
	}

	protected void logUserInfo(final BeforeSessionCloseEvent evt)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Session with Id " + getProductConfigEventListenerUtil().getUserSessionId(evt)
					+ " is expired and will be released ");
		}
	}
}
