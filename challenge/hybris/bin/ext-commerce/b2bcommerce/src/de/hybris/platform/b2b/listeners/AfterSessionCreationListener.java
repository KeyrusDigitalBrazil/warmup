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
package de.hybris.platform.b2b.listeners;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.event.events.AfterSessionCreationEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.RedeployUtilities;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This listener sets up the organization branch & root unit of the currently logged in user in the.
 * <p/>
 * {@link SessionContext}
 *
 * @deprecated Since 4.4. Uses JaloSession migration pending fix https://jira.hybris.com/browse/PLA-10932
 */
@Deprecated
public class AfterSessionCreationListener extends AbstractEventListener<AfterSessionCreationEvent>
{
	private static final Logger LOG = Logger.getLogger(AfterSessionCreationListener.class);

	private UserService userService;
	private SessionService sessionService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Override
	protected void onEvent(final AfterSessionCreationEvent event)
	{
		try
		{
			final boolean executeEventBody = executeEvent(event);
			if (executeEventBody)
			{
				final JaloSession jaloSession = (JaloSession) event.getSource();
				final UserModel currentUser = getUserService().getCurrentUser();
				final Session session;
				if ((session = getSession(jaloSession)) != null)
				{
					getB2BUnitService().updateBranchInSession(session, currentUser);
				}
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error while handling after session creation event.", e);
		}
	}

	/**
	 * Looks up {@link Session} via {@link SessionService} ignores {@link NullPointerException} thown from
	 * {@link SessionService#getSession(String)} assuming that system is initializing when the listener got called
	 *
	 * @param jaloSession
	 *           A hybris JaloSession
	 * @return A service layer Session object.
	 * @deprecated Since 4.4. Use SystemService when it becomes availabe from hybris
	 */
	@Deprecated
	protected Session getSession(final JaloSession jaloSession)
	{
		if (jaloSession != null && !jaloSession.isClosed() && !jaloSession.isExpired())
		{
			try
			{
				return this.getSessionService().getSession(jaloSession.getSessionID());
			}
			catch (final NullPointerException e)
			{
				// ignore de.hybris.platform.servicelayer.session.impl.DefaultSessionService.getOrBindSession
				// (DefaultSessionService.java:189)
				// happens if hybris is initializing, starting or shutting down.
				LOG.debug("Failed to look up session. Server possibly initializing, starting, or shutting down.", e);
			}
		}
		if (getUserService().getCurrentUser() instanceof B2BCustomerModel)
		{
			// BUG FIX:
			// When The VjdbcConnectionFilter calls
			//  JaloSession session = WebSessionFunctions.getSession(Collections.EMPTY_MAP, null,  	//NOSONAR
			//	      request.getSession(), request, response);													//NOSONAR
			// When intercepted above produces a null Session given the sessionID
			// In this case where it is a B2BCustomerModel making the call
			// using getCurrentSession correctly sets the session.branch allowing the filters to work
			return getSessionService().getCurrentSession();
		}
		else
		{
			return null;
		}
	}


	/**
	 * @param event
	 * @return True if even executed.
	 */
	protected boolean executeEvent(final AfterSessionCreationEvent event)
	{
		final boolean executeEventBody = !RedeployUtilities.isShutdownInProgress() && Registry.hasCurrentTenant()
				&& !getUserService().isAnonymousUser(getUserService().getCurrentUser());
		return executeEventBody;
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

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		b2BUnitService = b2bUnitService;
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

}
