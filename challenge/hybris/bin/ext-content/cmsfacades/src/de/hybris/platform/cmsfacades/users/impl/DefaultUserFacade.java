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
package de.hybris.platform.cmsfacades.users.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cmsfacades.data.UserData;
import de.hybris.platform.cmsfacades.users.UserFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link UserFacade}
 */
public class DefaultUserFacade implements UserFacade
{
	private UserService userService;
	private Converter<UserModel, UserData> cmsUserModelToDataConverter;

	@Override
	public UserData getUserById(final String userUID) throws CMSItemNotFoundException
	{
		try
		{
			final UserModel userModel = getUserService().getUserForUID(userUID);
			return getCmsUserModelToDataConverter().convert(userModel);
		}
		catch (final UnknownIdentifierException e)
		{
			throw new CMSItemNotFoundException("Cannot find user with uid [" + userUID + "]", e);
		}
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

	protected Converter<UserModel, UserData> getCmsUserModelToDataConverter()
	{
		return cmsUserModelToDataConverter;
	}

	@Required
	public void setCmsUserModelToDataConverter(final Converter<UserModel, UserData> cmsUserModelToDataConverter)
	{
		this.cmsUserModelToDataConverter = cmsUserModelToDataConverter;
	}
}
