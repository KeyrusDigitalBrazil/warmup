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
package de.hybris.platform.cmswebservices.filter;

import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;



/**
 * 
 * Filter that adds a session and custom header fields to the current request
 *
 */
public class RestSessionFilter extends OncePerRequestFilter
{
	private SessionService sessionService;
	private List<RestSessionFilterArgument> filterArguments;


	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
			throws ServletException, IOException
	{
		final HttpServletRequest httpRequest = request;
		Session session = null;

		try
		{
			session = sessionService.createNewSession();

			for (final RestSessionFilterArgument argument : getFilterArguments())
			{
				argument.addSessionArgument(httpRequest, response, session);
			}

			filterChain.doFilter(request, response);
		}
		finally
		{
			if (session != null)
			{
				sessionService.closeSession(session);
			}
		}
		
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}
	
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected List<RestSessionFilterArgument> getFilterArguments()
	{
		return filterArguments;
	}

	@Required
	public void setFilterArguments(final List<RestSessionFilterArgument> filterArguments)
	{
		this.filterArguments = filterArguments;
	}


}
