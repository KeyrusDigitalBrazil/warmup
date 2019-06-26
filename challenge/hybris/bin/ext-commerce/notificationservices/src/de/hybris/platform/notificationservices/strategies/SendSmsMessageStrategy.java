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

/**
 * Strategy to send SMS message
 */
public interface SendSmsMessageStrategy
{

	/**
	 * Send message for specific phone number
	 *
	 * @param phoneNumber
	 *           the receiver phone number
	 * @param message
	 *           the message to be sent
	 */
	void sendMessage(String phoneNumber, String message);
}
