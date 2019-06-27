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
package de.hybris.platform.customercouponfacades.hooks.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponfacades.hooks.NotificationChannelHook;

import org.apache.commons.lang3.BooleanUtils;


/**
 * Email channel implementation of {@link NotificationChannelHook}.
 *
 * @deprecated since 6.7.
 */
@Deprecated
public class EmailChannelHook implements NotificationChannelHook
{

	@Override
	public Boolean isChannelOn(final CustomerModel customer)
	{
		return BooleanUtils.isTrue(customer.getEmailPreference());
	}

}