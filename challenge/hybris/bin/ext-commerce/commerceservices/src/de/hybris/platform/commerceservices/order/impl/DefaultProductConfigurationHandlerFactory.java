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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandlerFactory;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;


/**
 * Default implementation of {@code ProductConfigurationHandlerFactory}.
 */
public class DefaultProductConfigurationHandlerFactory implements ProductConfigurationHandlerFactory
{
	private Map<String, ProductConfigurationHandler> registeredHandlers;

	@Override
	public ProductConfigurationHandler handlerOf(final ConfiguratorType configuratorType)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("configuratorType", configuratorType);
		final ProductConfigurationHandler handler = getRegisteredHandlers().get(configuratorType.getCode());
		if (handler == null)
		{
			throw new IllegalArgumentException("Unregistered product configuration type " + configuratorType.toString());
		}
		return handler;
	}

	protected Map<String, ProductConfigurationHandler> getRegisteredHandlers()
	{
		return registeredHandlers;
	}

	@Required
	public void setRegisteredHandlers(
			final Map<String, ProductConfigurationHandler> registeredHandlers)
	{
		this.registeredHandlers = registeredHandlers;
	}
}
