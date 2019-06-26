/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Validates mandatory gigya information exists in model
 */
public class GigyaUserValidateInterceptor implements ValidateInterceptor<CustomerModel>
{

	private static final Logger LOG = Logger.getLogger(GigyaUserValidateInterceptor.class);

	private UserService userService;

	private GigyaLoginService gigyaLoginService;

	@Override
	public void onValidate(final CustomerModel gigyaUser, final InterceptorContext interceptorContext) throws InterceptorException
	{
		// Checks if user is modified from backoffice and then it is synched to gigya
		if (!interceptorContext.isNew(gigyaUser) && interceptorContext.isModified(gigyaUser) && checkModifyingUser()
				&& checkMandatoryAttributes(gigyaUser, interceptorContext))
		{
			sendDataToGigya(gigyaUser);
		}
	}

	/**
	 * Send data to gigya
	 *
	 * @param gigyaUser
	 */
	private void sendDataToGigya(final CustomerModel gigyaUser) throws InterceptorException
	{
		if (!gigyaLoginService.sendUserToGigya(gigyaUser))
		{
			throw new InterceptorException("Error while sending user information to gigya.");
		}
	}

	/**
	 * This method checks if the modifying user is an employee or customer himself, if employee then data needs to be
	 * synched to gigya
	 *
	 * @return boolean, true if user is an employee
	 */
	private boolean checkModifyingUser()
	{
		return userService.getCurrentUser() instanceof EmployeeModel;
	}

	/**
	 * Checks if the attributes configured as in project.properties are modified, if yes then we send data to gigya
	 *
	 * @param gigyaUser
	 * @param interceptorContext
	 */
	private boolean checkMandatoryAttributes(final CustomerModel gigyaUser, final InterceptorContext interceptorContext)
	{
		final String attributes = Config.getString("gigya.mandatory.attributes.list", "name,uid");
		final String[] attributeArray = attributes.split(",");
		for (final String attribute : attributeArray)
		{
			boolean modified = false;
			try
			{
				modified = interceptorContext.isModified(gigyaUser, attribute);
			}
			catch (final AttributeNotSupportedException e)
			{
				LOG.warn("Invalid attribute configured in property 'gigya.mandatory.attributes.list' for monitoring.");
			}
			if (modified && StringUtils.isNoneEmpty(gigyaUser.getGyApiKey()) && StringUtils.isNoneEmpty(gigyaUser.getGyUID()))
			{
				return true;
			}
		}
		return false;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public GigyaLoginService getGigyaLoginService()
	{
		return gigyaLoginService;
	}

	@Required
	public void setGigyaLoginService(final GigyaLoginService gigyaLoginService)
	{
		this.gigyaLoginService = gigyaLoginService;
	}
}
