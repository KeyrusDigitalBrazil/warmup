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
package de.hybris.platform.notificationfacades.facades.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceDataList;
import de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.service.NotificationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Facade used to get and update notification preference
 */
public class DefaultNotificationPreferenceFacade implements NotificationPreferenceFacade
{
	private ModelService modelService;

	private UserService userService;

	private NotificationService notificationService;

	private Converter<CustomerModel, NotificationPreferenceData> notificationPreferenceConverter;

	private Converter<List<NotificationPreferenceData>, NotificationPreferenceDataList> notificationPreferencesConverter;

	private Converter<NotificationChannel, NotificationPreferenceData> notificationPreferenceEntryConverter;

	public Converter<NotificationChannel, NotificationPreferenceData> getNotificationPreferenceEntryConverter()
	{
		return notificationPreferenceEntryConverter;
	}

	public void setNotificationPreferenceEntryConverter(
			final Converter<NotificationChannel, NotificationPreferenceData> notificationPreferenceElementConverter)
	{
		this.notificationPreferenceEntryConverter = notificationPreferenceElementConverter;
	}


	public NotificationService getNotificationService()
	{
		return notificationService;
	}

	public void setNotificationService(final NotificationService notificationService)
	{
		this.notificationService = notificationService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public Converter<CustomerModel, NotificationPreferenceData> getNotificationPreferenceConverter()
	{
		return notificationPreferenceConverter;
	}

	@Required
	public void setNotificationPreferenceConverter(
			final Converter<CustomerModel, NotificationPreferenceData> notificationPreferenceConverter)
	{
		this.notificationPreferenceConverter = notificationPreferenceConverter;
	}

	public Converter<List<NotificationPreferenceData>, NotificationPreferenceDataList> getNotificationPreferencesConverter()
	{
		return notificationPreferencesConverter;
	}

	@Required
	public void setNotificationPreferencesConverter(
			final Converter<List<NotificationPreferenceData>, NotificationPreferenceDataList> notificationPreferencesConverter)
	{
		this.notificationPreferencesConverter = notificationPreferencesConverter;
	}

	@Override
	public void updateNotificationPreference(final NotificationPreferenceData notificationPreferenceData)
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		customer.setEmailPreference(Boolean.valueOf(notificationPreferenceData.isEmailEnabled()));
		customer.setSmsPreference(Boolean.valueOf(notificationPreferenceData.isSmsEnabled()));
		getModelService().save(customer);
	}

	@Override
	public NotificationPreferenceData getNotificationPreference()
	{
		return getNotificationPreferenceConverter().convert((CustomerModel) getUserService().getCurrentUser());
	}

	@Override
	public void updateNotificationPreference(final List<NotificationPreferenceData> notificationPreferenceDataList)
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();

		final Set<NotificationChannel> channels = notificationPreferenceDataList.stream().filter(p -> p.isEnabled())
				.map(c -> c.getChannel()).collect(Collectors.toSet());

		customer.setNotificationChannels(channels);
		getModelService().save(customer);
	}

	@Override
	public List<NotificationPreferenceData> getNotificationPreferences()
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		return getNotificationPreferences(customer.getNotificationChannels());
	}

	@Override
	public List<NotificationPreferenceData> getNotificationPreferences(final Set<NotificationChannel> enabledChannels)
	{
		final List<NotificationPreferenceData> allChannels = getNotificationPreferenceEntryConverter()
				.convertAll(Arrays.stream(NotificationChannel.values()).collect(Collectors.toList()));

		updateAllChannels(allChannels, enabledChannels);

		return allChannels;
	}

	@Override
	public List<NotificationPreferenceData> getValidNotificationPreferences()
	{
		final List<NotificationPreferenceData> channels = getNotificationPreferences();

		return filterValidNotificationPreference(channels);

	}

	@Override
	public List<NotificationPreferenceData> getValidNotificationPreferences(final Set<NotificationChannel> enabledChannels)
	{
		final List<NotificationPreferenceData> channels = getNotificationPreferences(enabledChannels);

		return filterValidNotificationPreference(channels);
	}

	protected List<NotificationPreferenceData> filterValidNotificationPreference(
			final List<NotificationPreferenceData> preferenceData)
	{
		return preferenceData.stream().filter(p -> validateNotificationPreferenceData(p)).collect(Collectors.toList());

	}


	@Override
	public String getChannelValue(final NotificationChannel channel)
	{
		final CustomerModel currentCustomer = (CustomerModel) userService.getCurrentUser();
		return notificationService.getChannelValue(channel, currentCustomer);
	}

	protected boolean validateNotificationPreferenceData(final NotificationPreferenceData data)
	{

		return (data.getChannel().equals(NotificationChannel.SMS) || data.getChannel().equals(NotificationChannel.EMAIL))
				&& StringUtils.isBlank(data.getValue()) ? false : true;
	}

	@Override
	public NotificationPreferenceDataList getNotificationPreferences(
			final List<NotificationPreferenceData> notificationPreferences)
	{

		return getNotificationPreferencesConverter().convert(notificationPreferences);
	}

	protected void updateAllChannels(final List<NotificationPreferenceData> allChannels,
			final Set<NotificationChannel> enabledChannels)
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();

		allChannels.forEach(c -> {
			if (enabledChannels.contains(c.getChannel()))
			{
				c.setEnabled(Boolean.TRUE);
			}
			c.setValue(notificationService.getChannelValue(c.getChannel(), customer));
		});

	}

}
