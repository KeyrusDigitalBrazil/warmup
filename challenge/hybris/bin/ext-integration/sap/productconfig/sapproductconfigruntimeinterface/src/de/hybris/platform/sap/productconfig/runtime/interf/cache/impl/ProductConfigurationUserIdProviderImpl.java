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
package de.hybris.platform.sap.productconfig.runtime.interf.cache.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.runtime.interf.cache.ProductConfigurationUserIdProvider;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;


public class ProductConfigurationUserIdProviderImpl implements ProductConfigurationUserIdProvider
{
	private UserService userService;
	private SessionService sessionService;

	@Override
	public String getCurrentUserId()
	{
		if (isAnonymousUser())
		{
			return getSessionService().getCurrentSession().getSessionId();
		}
		final UserModel user = getUserService().getCurrentUser();
		return user.getUid();
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


	@Override
	public boolean isAnonymousUser()
	{
		final UserModel user = getUserService().getCurrentUser();
		return getUserService().isAnonymousUser(user);
	}

}
