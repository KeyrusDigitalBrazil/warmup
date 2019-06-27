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
package de.hybris.platform.notificationfacades.facades;

import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceDataList;
import de.hybris.platform.notificationservices.enums.NotificationChannel;

import java.util.List;
import java.util.Set;


/**
 * Facade to get the notification preference and update notification preference
 */
public interface NotificationPreferenceFacade
{
	/**
	 * Update the notification preference to database
	 *
	 * @param notificationPreferenceData
	 *           The new notification preference to be updated
	 * @deprecated since 6.7. Use updateNotificationPreference(List
	 *             <NotificationPreferenceData> notificationPreferenceDataList)
	 */
	@Deprecated
	void updateNotificationPreference(NotificationPreferenceData notificationPreferenceData);

	/**
	 * Get the notification preference
	 *
	 * @return The NotificationPreferenceData contains the enabled channels,and email address and mobile phone number if
	 *         exist
	 *
	 * @deprecated since 6.7.Use getNotificationPreferences()
	 */
	@Deprecated
	NotificationPreferenceData getNotificationPreference();


	/**
	 * Update the notification preference to database
	 *
	 * @param notificationPreferenceDataList
	 *           The new notification preference to be updated
	 */
	void updateNotificationPreference(List<NotificationPreferenceData> notificationPreferenceDataList);


	/**
	 * Get the notification preference data list
	 *
	 * @param notificationPreferences
	 *           the notificationPreference data list
	 * @return The NotificationPreferenceDataList contains all channels info
	 *
	 */
	NotificationPreferenceDataList getNotificationPreferences(List<NotificationPreferenceData> notificationPreferences);

	/**
	 * Get the notification preference data list
	 *
	 * @return The NotificationPreferenceDataList contains all channels info
	 *
	 */
	List<NotificationPreferenceData> getNotificationPreferences();

	/**
	 * Get the valid notification preference data, for example the mail has mail address, the sms has phone number
	 *
	 * @return The NotificationPreferenceDataList contains the valid channels info
	 *
	 */
	List<NotificationPreferenceData> getValidNotificationPreferences();

	/**
	 * Get the valid notification preference data according to the enabledChannels
	 *
	 * @param enabledChannels
	 *           the enabled channels
	 * @return The NotificationPreferenceDataList contains the valid channels info
	 *
	 */
	List<NotificationPreferenceData> getValidNotificationPreferences(Set<NotificationChannel> enabledChannels);

	/**
	 * Get all notification channels according to enabledChannels
	 *
	 * @param enabledChannels
	 *           the enabled channels
	 * @return return all notification channels according to enabledChannels
	 */
	List<NotificationPreferenceData> getNotificationPreferences(Set<NotificationChannel> enabledChannels);

	/**
	 * Get the channel value for channel
	 *
	 * @param channel
	 *           the notification channel
	 * @return return the related channel value for the channel
	 */
	String getChannelValue(NotificationChannel channel);

}
