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
package de.hybris.platform.notificationservices.strategies;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;

import java.util.List;


/**
 * Strategy to send site message
 */
public interface SendSiteMessageStrategy
{

	/**
	 * Send message for specific customer
	 *
	 * @param customer
	 *           the recevier
	 * @param message
	 *           the message to be sent
	 */
	default void sendMessage(final CustomerModel customer, final SiteMessageModel message)
	{
		// Default empty implementation.
	}

	/**
	 * Send messages for specific customer
	 *
	 * @param customer
	 *           the receiver
	 * @param messages
	 *           the messages to be sent
	 */
	default void sendMessage(final CustomerModel customer, final List<SiteMessageModel> messages)
	{
		// Default empty implementation.
	}

	/**
	 * Send message for given customers
	 *
	 * @param customers
	 *           the receivers
	 * @param message
	 *           the message to be sent
	 */
	default void sendMessage(final List<CustomerModel> customers, final SiteMessageModel message)
	{
		// Default empty implementation.
	}
}
