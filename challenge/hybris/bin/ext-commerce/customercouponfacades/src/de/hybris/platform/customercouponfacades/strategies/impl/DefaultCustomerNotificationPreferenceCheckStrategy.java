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
package de.hybris.platform.customercouponfacades.strategies.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponfacades.strategies.CustomerNotificationPreferenceCheckStrategy;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CustomerNotificationPreferenceCheckStrategy}
 */
public class DefaultCustomerNotificationPreferenceCheckStrategy implements CustomerNotificationPreferenceCheckStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultCustomerNotificationPreferenceCheckStrategy.class);

	private UserService userService;

	@Override
	public Boolean checkCustomerNotificationPreference()
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
		if (CollectionUtils.isEmpty(customer.getNotificationChannels()))
		{
			LOG.warn("You haven't chosen any channel in Notification Preference, so no notification would be sent.");
			return false;
		}

		return true;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
