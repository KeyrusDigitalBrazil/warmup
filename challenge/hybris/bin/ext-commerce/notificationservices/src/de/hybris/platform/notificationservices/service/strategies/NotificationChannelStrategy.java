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

public interface NotificationChannelStrategy
{
	/**
	 * get the channel value for the customer
	 *
	 * @param customer
	 *           the customer
	 * @return return the notification channel value (for example, return email address for EMAIl channel)
	 */
	String getChannelValue(CustomerModel customer);

}
