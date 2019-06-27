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
package de.hybris.platform.cmssmarteditwebservices.filter;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.GenericFilterBean;


/**
 * Filter that creates a new session for the request and injects data into the session with restrictions disabled.
 * This filter is designed to create a new session and to inject any pertinent data into the current session, e.g. all the
 * catalog versions, before dispatching to the controller.
 */
public class RestSessionDataInjectionFilter extends GenericFilterBean
{

	private SearchRestrictionService searchRestrictionService;

	private CatalogVersionService catalogVersionService;

	private SessionService sessionService;

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException
	{
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		Session session = null;

		try
		{
			session = getSessionService().createNewSession();

			final Collection<CatalogVersionModel> catalogVersions = getSessionService().executeInLocalView(new SessionExecutionBody()
			{
				@Override
				public Object execute()
				{
					getSearchRestrictionService().disableSearchRestrictions();
					return getCatalogVersionService().getAllCatalogVersions();
				}
			});

			getSessionService().setAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS, catalogVersions);

			filterChain.doFilter(request, response);
		}
		finally
		{
			if (session != null)
			{
				getSessionService().closeSession(session);
			}

			final HttpSession httpSession = httpRequest.getSession(false);
			if (httpSession != null)
			{
				httpSession.invalidate();
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


	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	@Required
	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
