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
package de.hybris.platform.b2bpunchoutaddon.security;

import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


public class PunchOutAuthenticationProvider extends PunchOutCoreAuthenticationProvider
{
	private static final String NONE_PROVIDED = "NONE_PROVIDED";

	private UserService userService;
	private ModelService modelService;
	private CartService cartService;
	private ConfigurationService configurationService;

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException
	{
		final String username = (authentication.getPrincipal() == null) ? NONE_PROVIDED : authentication.getName();

		// check if the user of the cart matches the current user and if the
		// user is not anonymous. If otherwise, delete the session cart as it might
		// be stolen / from another user
		final String sessionCartUserId = getCartService().getSessionCart().getUser().getUid();

		if (!username.equals(sessionCartUserId) && !sessionCartUserId.equals(getUserService().getAnonymousUser().getUid()))
		{
			getCartService().setSessionCart(null);
		}

		return super.authenticate(authentication);
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

}
