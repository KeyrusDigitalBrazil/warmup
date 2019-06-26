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
package de.hybris.platform.assistedservicestorefront.customer360.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicestorefront.customer360.FavoriteColorsData;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Model provider implementation for Customer favorite colors fragment.
 */
public class CustomerFavoriteColorsProvider implements FragmentModelProvider<FavoriteColorsData>
{

	private UserService userService;

	@Override
	public FavoriteColorsData getModel(final Map<String, String> parameters)
	{
		final FavoriteColorsData customerFavoriteColors = new FavoriteColorsData();
		customerFavoriteColors.setName(getUserService().getCurrentUser().getName().split(" ")[0]);
		return customerFavoriteColors;
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
