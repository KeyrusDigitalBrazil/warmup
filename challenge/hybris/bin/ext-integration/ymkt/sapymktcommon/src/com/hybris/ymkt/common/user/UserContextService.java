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
package com.hybris.ymkt.common.user;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hybris.ymkt.common.consent.YmktConsentService;
import com.hybris.ymkt.common.constants.SapymktcommonConstants;

/**
 * Provide utility methods such as {@link #getUserOrigin()} and {@link #getUserId()}.
 *
 */
public class UserContextService
{
	private static final Logger LOG = LoggerFactory.getLogger(UserContextService.class);

	protected String anonymousUserOrigin;
	protected UserService userService;
	protected YmktConsentService ymktConsentService;

	/**
	 * @return <code>SAP_HYBRIS_CONSUMER</code> or the property value of
	 *         <code>sapymktcommon.user.origin.SAP_HYBRIS_CONSUMER</code>.
	 */
	public static String getOriginIdSapHybrisConsumer()
	{
		return Config.getString("sapymktcommon.user.origin.SAP_HYBRIS_CONSUMER", "SAP_HYBRIS_CONSUMER");
	}

	/**
	 * @return <code>ANONYMOUS</code> or the property value of <code>sapymktcommon.user.origin.incognito</code>.
	 */
	public static String getOriginIdIncognito()
	{
		return Config.getString("sapymktcommon.user.origin.incognito", "ANONYMOUS");
	}

	public String getAnonymousUserId()
	{
		try
		{
			return Optional.ofNullable(RequestContextHolder.currentRequestAttributes()) //
					.map(ServletRequestAttributes.class::cast) //
					.map(ServletRequestAttributes::getRequest) //
					.map(HttpServletRequest::getCookies) //
					.map(Arrays::stream).orElse(Stream.empty()) //
					.filter(this::isPiwikCookie) //
					.map(Cookie::getValue) //
					.map(s -> s.substring(0, 16)) //
					.findAny().orElse("");
		}
		catch (final IllegalStateException e)
		{
			LOG.info("Not executing within a web request", e);
		}
		return "";
	}

	public String getAnonymousUserOrigin()
	{
		return this.anonymousUserOrigin;
	}

	public String getLoggedInUserOrigin()
	{
		return getOriginIdSapHybrisConsumer();
	}

	/**
	 * @return User ID according to the {@link #getUserOrigin()} if user consent is provided. Otherwise return empty
	 *         string.
	 */
	public String getUserId()
	{
		if (!isIncognitoUser())
		{
			return this.isAnonymousUser() ? this.getAnonymousUserId() : ((CustomerModel) this.userService.getCurrentUser())
					.getCustomerID();
		}
		return "";
	}

	/**
	 * ID Origin is a synonym of User Type.
	 *
	 * @return COOKIE_ID or SAP_HYBRIS_CONSUMER
	 */
	public String getUserOrigin()
	{
		if (!isIncognitoUser())
		{
			return this.isAnonymousUser() ? this.anonymousUserOrigin : getOriginIdSapHybrisConsumer();
		}
		return getOriginIdIncognito();
	}

	public boolean isAnonymousUser()
	{
		final UserModel currentUser = userService.getCurrentUser();
		return currentUser == null || userService.isAnonymousUser(currentUser);
	}

	/**
	 * Checks user consent for personalization consent template. When true, Marketing considers the user 'incognito'.
	 *
	 * @return boolean true when user consent is not given
	 */
	public boolean isIncognitoUser()
	{
		return !ymktConsentService.getUserConsent(SapymktcommonConstants.PERSONALIZATION_CONSENT_ID);
	}

	protected boolean isPiwikCookie(final Cookie c)
	{
		return c.getName().startsWith("_pk_id");
	}

	@Required
	public void setAnonymousUserOrigin(final String anonymousUserOrigin)
	{
		LOG.debug("anonymousUserOrigin={}", anonymousUserOrigin);
		this.anonymousUserOrigin = anonymousUserOrigin.intern();
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Required
	public void setYmktConsentService(final YmktConsentService ymktConsentService)
	{
		this.ymktConsentService = ymktConsentService;
	}

}
