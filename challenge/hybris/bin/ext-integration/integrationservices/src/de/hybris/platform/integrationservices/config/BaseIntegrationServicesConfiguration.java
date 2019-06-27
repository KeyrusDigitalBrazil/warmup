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
 */
package de.hybris.platform.integrationservices.config;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Provides common functionality for child classes
 */
public class BaseIntegrationServicesConfiguration
{
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseIntegrationServicesConfiguration.class);

	private static final String FALLBACK_MESSAGE = "Property '{}' was not configured or not configured correctly. Using fallback value '{}'.";

	private ConfigurationService configurationService;

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets the String property from the {@link ConfigurationService}.
	 * If there's an exception, the default value is returned
	 *
	 * @param property Property to get
	 * @param defaultValue Default value to return if an exception occurs
	 * @return The property value
	 */
	protected String getStringProperty(final String property, final String defaultValue)
	{
		try
		{
			final String value = getConfigurationService().getConfiguration().getString(property);
			if (StringUtils.isNotBlank(value))
			{
				return value;
			}
			LOGGER.warn(FALLBACK_MESSAGE, property, defaultValue);
		}
		catch(final NoSuchElementException | ConversionException e)
		{
			LOGGER.warn(FALLBACK_MESSAGE, property, defaultValue, e);
		}
		return defaultValue;
	}

	/**
	 * Gets the boolean property from the {@link ConfigurationService}.
	 * If there's an exception, the default value is returned
	 *
	 * @param property Property to get
	 * @param defaultValue Default value to return if an exception occurs
	 * @return The property value
	 */
	protected boolean getBooleanProperty(final String property, final boolean defaultValue)
	{
		try
		{
			return getConfigurationService().getConfiguration().getBoolean(property);
		}
		catch (final NoSuchElementException | ConversionException e)
		{
			LOGGER.warn(FALLBACK_MESSAGE, property, defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * Gets the integer property from the {@link ConfigurationService}.
	 * If there's an exception, the default value is returned
	 *
	 * @param property Property to get
	 * @param defaultValue Default value to return if an exception occurs
	 * @return The property value
	 */
	protected int getIntegerProperty(final String property, final int defaultValue)
	{
		try
		{
			return getConfigurationService().getConfiguration().getInt(property);
		}
		catch(final NoSuchElementException | ConversionException e)
		{
			LOGGER.warn(FALLBACK_MESSAGE, property, defaultValue, e);
			return defaultValue;
		}
	}
}
