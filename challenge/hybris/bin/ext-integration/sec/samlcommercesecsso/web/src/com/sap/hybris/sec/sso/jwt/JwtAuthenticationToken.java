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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken
{
	private final String token;


	/**
	 * @param user_name
	 * @param token
	 */
	public JwtAuthenticationToken(final String user_name, final String token)
	{
		super(user_name, token);
		this.token = token;
	}

	/**
	 * @return token
	 */
	public String getToken()
	{
		return token;
	}

}
