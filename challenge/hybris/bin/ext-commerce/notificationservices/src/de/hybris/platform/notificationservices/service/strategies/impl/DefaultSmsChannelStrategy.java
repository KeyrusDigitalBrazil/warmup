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
 package de.hybris.platform.notificationservices.service.strategies.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.service.strategies.NotificationChannelStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;


/**
 * Sms notification channel strategy to get phone number
 */
public class DefaultSmsChannelStrategy implements NotificationChannelStrategy
{
	private static final String SMS_CONFIGURED_MOBIL_NUMBER = "sms.channel.mobilePhone";
	private ConfigurationService configurationService;

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	@Override
	public String getChannelValue(final CustomerModel customer)
	{

		return getConfigurationService().getConfiguration().getString(SMS_CONFIGURED_MOBIL_NUMBER);
	}

}
