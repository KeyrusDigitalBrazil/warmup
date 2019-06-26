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
package com.sap.hybris.sec.sso.jwt;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.sap.hybris.sec.sso.jwt.util.DefalutJWTUtil;


/**
 *
 */
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{

	private DefalutJWTUtil defalutJWTUtil;

	@Override
	public boolean supports(final Class<?> authentication)
	{
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}


	@Override
	protected void additionalAuthenticationChecks(final UserDetails paramUserDetails, final UsernamePasswordAuthenticationToken paramUsernamePasswordAuthenticationToken)
			throws AuthenticationException
	{
		// Any additional checks of a returned (or cached) UserDetails for a given authentication request can be added
	}


	@Override
	protected UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException, IllegalArgumentException
	{
		final JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
		final String token = jwtAuthenticationToken.getToken();
		final UserDetails userDetails = defalutJWTUtil.getUserDetails(token);
		return userDetails;

	}



	public DefalutJWTUtil getDefalutJWTUtil()
	{
		return defalutJWTUtil;
	}


	@Required
	public void setDefalutJWTUtil(final DefalutJWTUtil defalutJWTUtil)
	{
		this.defalutJWTUtil = defalutJWTUtil;
	}
}
