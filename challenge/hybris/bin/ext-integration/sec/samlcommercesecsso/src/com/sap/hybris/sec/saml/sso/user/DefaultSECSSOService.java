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
package com.sap.hybris.sec.saml.sso.user;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.samlsinglesignon.SSOUserService;
import de.hybris.platform.servicelayer.internal.service.AbstractService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Override the create user functionality.
 * 
 */
public class DefaultSECSSOService extends AbstractService implements SSOUserService
{
	private static final Logger LOGGER = Logger.getLogger(DefaultSECSSOService.class);

	private UserService userService;

	@Override
	public UserModel getOrCreateSSOUser(final String id, final String name, final Collection<String> roles)
	{
		LOGGER.info("Calling the DefaultSECSSOService getUser");
		if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name))
		{
			throw new IllegalArgumentException("User info must not be empty");
		}

		return userService.getUserForUID(id);
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}