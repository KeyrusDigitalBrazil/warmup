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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.ssc.SSCSessionAccessService;
import de.hybris.platform.servicelayer.session.SessionService;

import org.springframework.beans.factory.annotation.Required;


public class SSCSessionAccessServiceImpl implements SSCSessionAccessService
{
	protected static final String CONFIG_PROVIDER_SESSION_ATTR_NAME = "productConfigProviderSSCSessionAttr";
	private SessionService sessionService;

	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		final Object sessionAttribute = getSessionService().getAttribute(CONFIG_PROVIDER_SESSION_ATTR_NAME);
		if (sessionAttribute != null && !(sessionAttribute instanceof ConfigurationProvider))
		{
			throw new IllegalStateException("SSC Configuration Provider could not be found in session");
		}
		return (ConfigurationProvider) sessionAttribute;
	}

	@Override
	public void setConfigurationProvider(final ConfigurationProvider provider)
	{
		getSessionService().setAttribute(CONFIG_PROVIDER_SESSION_ATTR_NAME, provider);

	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
