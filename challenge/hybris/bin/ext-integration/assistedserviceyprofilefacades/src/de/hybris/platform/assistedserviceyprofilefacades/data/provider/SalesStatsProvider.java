/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.assistedserviceyprofilefacades.data.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedserviceyprofilefacades.data.SalesStatsData;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;


/**
 * YProfile Sales Stats fragment provider
 */
public class SalesStatsProvider implements FragmentModelProvider<SalesStatsData>
{
	private UserService userService;

	@Override
	public SalesStatsData getModel(final Map<String, String> parameters)
	{
		final SalesStatsData salesStatsData = new SalesStatsData();
		salesStatsData.setName(getUserService().getCurrentUser().getName());
		return salesStatsData;
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
