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
import de.hybris.platform.notificationservices.service.strategies.SmsNotificationChannelStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;


/**
 * Default Strategy used to get the mobile phone number from project.properties configuration file
 * 
 * @deprecated since 6.7 . use {@link DefaultSmsChannelStrategy}
 */
@Deprecated
public class DefaultSmsNotificationChannelStrategy implements SmsNotificationChannelStrategy
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
	public String getMobilePhoneNumber(final CustomerModel customer)
	{
		return getConfigurationService().getConfiguration().getString(SMS_CONFIGURED_MOBIL_NUMBER);
	}
}
