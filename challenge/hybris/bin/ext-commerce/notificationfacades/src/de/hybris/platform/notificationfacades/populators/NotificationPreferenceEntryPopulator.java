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
package de.hybris.platform.notificationfacades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;

import reactor.util.Assert;


/**
 * populator to populate NotificationChannelData from notification channel
 */
public class NotificationPreferenceEntryPopulator implements Populator<NotificationChannel, NotificationPreferenceData>
{

	private ConfigurationService configurationService;

	private static final String CHANNEL_VISIBLE_PATTERN = "notificationfacades.channel.{0}.visible";

	@Override
	public void populate(final NotificationChannel source, final NotificationPreferenceData target)
	{
		Assert.notNull(source, "the source can't be null");
		Assert.notNull(target, "the target can't be null");

		target.setChannel(source);
		final String key = MessageFormat.format(CHANNEL_VISIBLE_PATTERN, source.getCode().toLowerCase(Locale.ROOT));
		target.setVisible(
				getConfigurationService().getConfiguration().getBoolean(key, true));

	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
