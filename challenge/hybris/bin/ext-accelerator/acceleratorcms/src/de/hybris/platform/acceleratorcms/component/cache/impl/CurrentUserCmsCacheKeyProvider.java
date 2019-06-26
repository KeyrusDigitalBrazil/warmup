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
package de.hybris.platform.acceleratorcms.component.cache.impl;

import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;


public class CurrentUserCmsCacheKeyProvider extends DefaultCmsCacheKeyProvider
{
	private UserService userService;

	@Override
	public StringBuilder getKeyInternal(final HttpServletRequest request, final SimpleCMSComponentModel component)
	{
		final StringBuilder buffer = new StringBuilder(super.getKeyInternal(request, component));
		final UserModel currentUser = getUserService().getCurrentUser();
		buffer.append(currentUser.getUid());
		return buffer;
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
