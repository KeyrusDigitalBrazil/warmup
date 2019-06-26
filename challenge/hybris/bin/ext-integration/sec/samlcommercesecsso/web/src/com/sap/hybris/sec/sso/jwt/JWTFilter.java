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


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.sap.hybris.sec.sso.jwt.util.DefalutJWTUtil;


/**
 *
 */
public class JWTFilter extends AbstractAuthenticationProcessingFilter
{
	private DefalutJWTUtil defalutJWTUtil;

	protected JWTFilter()
	{
		super("/**");
	}

	@Override
	protected boolean requiresAuthentication(final HttpServletRequest request, final HttpServletResponse response)
	{
		return true;
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, IllegalArgumentException
	{
		final String token_Filed_Name = "jwt";
		final String jWTNotFound = "No JWT token found in request headers";
		final String notValidJwt = "You do not have access rights to perform this operation. Please contact your System Administrator.";
		//final String header = request.getHeader("Authorization"); //if pass the jwt token in hearder

		final String header = request.getParameter(token_Filed_Name);

		if (StringUtils.isEmpty(header))
		{
			throw new IllegalArgumentException(jWTNotFound);
		}
		// as header have token in format 'Bearer: <token>' , doing substring(7) to get token only without Bearer
		final String authToken = header.substring(7);

		
		if (!defalutJWTUtil.isValidToken(authToken))
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, notValidJwt);
			throw new IllegalArgumentException(notValidJwt);
		}
		
	

		final JwtAuthenticationToken authRequest = new JwtAuthenticationToken(defalutJWTUtil.getUserNameFromJWTToken(authToken,"user_name"),
				authToken);

		return getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, final Authentication authResult) throws IOException, ServletException
	{
		super.successfulAuthentication(request, response, chain, authResult);

		// As this authentication is in HTTP header, after success we need to continue the request normally
		// and return the response as if the resource was not secured at all
		chain.doFilter(request, response);
	}

	@Override
	@Autowired
	public void setAuthenticationManager(final AuthenticationManager authenticationManager)
	{
		super.setAuthenticationManager(authenticationManager);
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
