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
 package de.hybris.platform.chineseprofileservices.strategies.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.service.strategies.impl.DefaultSmsChannelStrategy;


public class ChineseProfileSMSChannelStrategy extends DefaultSmsChannelStrategy
{
	@Override
	public String getChannelValue(final CustomerModel customer)
	{
		return customer.getMobileNumber();
	}

}
