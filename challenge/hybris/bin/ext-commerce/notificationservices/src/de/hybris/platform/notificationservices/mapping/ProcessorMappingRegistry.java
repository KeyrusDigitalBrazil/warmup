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
package de.hybris.platform.notificationservices.mapping;

import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.processor.Processor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * Get the processor by using specific channel and notification type
 */
public class ProcessorMappingRegistry
{
	private final Map<NotificationChannel, Map<NotificationType, Processor>> channelMapping = new ConcurrentHashMap<>();

	/**
	 *
	 * add new processor to this registry's map
	 *
	 * @param notificationChannel
	 *           one notification channel such as email, sms
	 * @param notificationType
	 *           represents one notification type
	 * @param processor
	 *           one specific processor
	 */
	public void addMapping(final NotificationChannel notificationChannel, final NotificationType notificationType,
			final Processor processor)
	{
		channelMapping.putIfAbsent(notificationChannel, new EnumMap<NotificationType, Processor>(NotificationType.class));
		channelMapping.get(notificationChannel).put(notificationType, processor);
	}

	public Map<NotificationChannel, Map<NotificationType, Processor>> getMappings()
	{
		return Collections.unmodifiableMap(channelMapping);
	}
}
