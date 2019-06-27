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
package de.hybris.b2cangularaddon.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;


public interface AngularAuthenticationStrategy
{
	/**
	 * Generates a new token and sets it as a cookie and session attribute
	 *
	 * @param request
	 * @param response
	 */
	void login(HttpServletRequest request, HttpServletResponse response);

	/**
	 * Removes the token from the cookies and DB
	 *
	 * @param request
	 * @param response
	 */
	void logout(HttpServletRequest request, HttpServletResponse response, final Authentication authentication);
}
