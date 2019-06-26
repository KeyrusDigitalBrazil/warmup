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
package de.hybris.platform.notificationservices.service.strategies;

import de.hybris.platform.core.model.user.CustomerModel;


/**
 * Strategy used to get mobile phone number in notification preference
 * 
 * @deprecated since 6.7. Use {@link NotificationChannelStrategy}
 */
@Deprecated
public interface SmsNotificationChannelStrategy
{
	/**
	 * Get the mobile phone number from project.properties configuration file
	 *
	 * @param customer
	 *           get phone number from this customer
	 *
	 * @return the mobile number will be returned
	 */
	String getMobilePhoneNumber(CustomerModel customer);
}
