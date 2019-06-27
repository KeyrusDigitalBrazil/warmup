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

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.apache.log4j.Logger;


/**
 * This bean listens to the {@link AfterSessionUserChangeEvent}. It will clear the read cache of the configuration
 * engine for any configuration stored in the user session. This will ensure that configuration data is re-read and that
 * rule evaluation is re-triggered. So rule framework and configuration engine can immediately react on the user change.
 * <br>
 * Otherwise configuration engine and rules changes would only be visible after the next change of the corresponding
 * configuration.
 */
public class ProductConfigUserChangedEventListener extends AbstractEventListener<AfterSessionUserChangeEvent>
{
	private static final Logger LOG = Logger.getLogger(ProductConfigUserChangedEventListener.class);

	@Override
	protected void onEvent(final AfterSessionUserChangeEvent evt)
	{
		logUserInfo(evt);
		getConfigurationLifecycleStrategy()
				.updateUserLinkToConfiguration(getProductConfigEventListenerUtil().getUserSessionId(evt));
	}

	protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getConfigurationLifecycleStrategy().");
	}

	protected ProductConfigEventListenerUtil getProductConfigEventListenerUtil()
	{
		throw new UnsupportedOperationException(
				"Please define in the spring configuration a <lookup-method> for getProductConfigEventListenerUtil().");
	}

	protected void logUserInfo(final AfterSessionUserChangeEvent evt)
	{
		if (LOG.isDebugEnabled())
		{
			final User user = getCurrentUser(evt);
			String newUser = null;
			if (user != null)
			{
				newUser = user.getUid();
			}
			LOG.debug("User Changed from " + evt.getPreviousUserUID() + " to " + newUser);
		}
	}

	protected User getCurrentUser(final AfterSessionUserChangeEvent evt)
	{
		User currentUser = null;
		final Object source = evt.getSource();
		if (source instanceof JaloSession)
		{
			currentUser = ((JaloSession) source).getUser();
		}
		return currentUser;
	}

}
