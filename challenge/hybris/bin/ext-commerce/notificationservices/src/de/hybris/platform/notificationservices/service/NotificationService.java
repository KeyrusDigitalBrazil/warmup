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
package de.hybris.platform.notificationservices.service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.enums.NotificationType;

import java.util.Map;
import java.util.Set;


/**
 * notify customer via EMAIL/SMS
 */
public interface NotificationService
{
	/**
	 *
	 * Send notification to customer using channels from customer's preferences
	 *
	 * @param notificationType
	 *           one specific type
	 * @param customer
	 *           one customer you want to notify
	 * @param dataMap
	 *           some models needed by VM file to renderer
	 */
	void notifyCustomer(final NotificationType notificationType, final CustomerModel customer,
			final Map<String, ? extends ItemModel> dataMap);

	/**
	 * Send notification to customer using specific channels
	 *
	 * @param notificationType
	 *           one specific type
	 * @param customer
	 *           one customer you want to notify
	 * @param channelSet
	 *           assigned channels
	 * @param dataMap
	 *           some models needed by VM to renderer
	 */
	void notifyCustomer(final NotificationType notificationType, final CustomerModel customer,
			final Set<NotificationChannel> channelSet, final Map<String, ? extends ItemModel> dataMap);

	/**
	 * return the notificaiton channel's value
	 *
	 * @param channel
	 *           the notification channel
	 * @param customer
	 *           the customer want to get the channel value
	 *
	 * @return return the notification channel's value for customer
	 */
	String getChannelValue(final NotificationChannel channel, final CustomerModel customer);
}



