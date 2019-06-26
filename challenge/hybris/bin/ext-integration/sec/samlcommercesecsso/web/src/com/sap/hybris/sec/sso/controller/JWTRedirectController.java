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
package com.sap.hybris.sec.sso.controller;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.persistence.security.EJBPasswordEncoderNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sap.hybris.sec.sso.constants.SamlcommercesecssoConstants;


@Controller
public class JWTRedirectController
{

	private final static Logger LOGGER = Logger.getLogger(JWTRedirectController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/oauth/**", method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String redirect(final HttpServletResponse response, final HttpServletRequest request)
	{

		String referenceURL = StringUtils.substringAfter(request.getServletPath(), "/oauth/");

		if (!StringUtils.isEmpty(request.getQueryString()))
		{
			referenceURL += request.getQueryString().isEmpty() ? "" : "?" + request.getQueryString();
		}

		try
		{
			final User contextUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			final UserModel user = userService.getUserForUID(contextUser.getUsername());

			storeTokenFromJWT(response, user);

			final String redirectURL = StringUtils.defaultIfEmpty(Config.getParameter(SamlcommercesecssoConstants.SSO_REDIRECT_URL),
					SamlcommercesecssoConstants.DEFAULT_REDIRECT_URL);

			return SamlcommercesecssoConstants.REDIRECT_PREFIX + redirectURL + referenceURL;

		}
		catch (final IllegalArgumentException e)
		{
			//the user is not belonging to any valid group
			LOGGER.error(e);
		}

		catch (final Exception e)
		{
			//something went wrong and we need to log that
			LOGGER.error(e);
		}

		return "/error";

	}

	public void storeTokenFromJWT(final HttpServletResponse response, final UserModel user)
	{
		try
		{

			final String cookiePath = StringUtils.defaultIfEmpty(Config.getParameter(SamlcommercesecssoConstants.SSO_COOKIE_PATH),
					SamlcommercesecssoConstants.DEFAULT_COOKIE_PATH);

			final String cookieMaxAgeStr = StringUtils.defaultIfEmpty(
					Config.getParameter(SamlcommercesecssoConstants.SSO_COOKIE_MAX_AGE),
					String.valueOf(SamlcommercesecssoConstants.DEFAULT_COOKIE_MAX_AGE));

			int cookieMaxAge;

			if (!NumberUtils.isNumber(cookieMaxAgeStr))
			{
				cookieMaxAge = SamlcommercesecssoConstants.DEFAULT_COOKIE_MAX_AGE;
			}
			else
			{
				cookieMaxAge = Integer.parseInt(cookieMaxAgeStr);
			}

			UserManager.getInstance().storeLoginTokenCookie(
					//
					StringUtils.defaultIfEmpty(Config.getParameter(SamlcommercesecssoConstants.SSO_COOKIE_NAME),
							SamlcommercesecssoConstants.SSO_DEFAULT_COOKIE_NAME), // cookie name
					user.getUid(), // user id
					"en", // language iso code
					null, // credentials to check later
					cookiePath, // cookie path
					StringUtils.defaultIfEmpty(Config.getParameter(SamlcommercesecssoConstants.SSO_COOKIE_DOMAIN),
							SamlcommercesecssoConstants.SSO_DEFAULT_COOKIE_DOMAIN), // cookie domain
					true, // secure cookie
					cookieMaxAge, // max age in seconds
					response);
		}
		catch (final EJBPasswordEncoderNotFoundException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

}
