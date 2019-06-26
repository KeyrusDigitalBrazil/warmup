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
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.AbstractTenant;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.RedeployUtilities;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This listener sets up the organization branch & root unit of the currently logged in user in the.
 * <p/>
 * {@link SessionContext} as well as current currency from the list of available currencies from the list of cost
 * centers for the currently logged in user.
 *
 * @deprecated Since 4.4. Uses JaloSession migration pending fix https://jira.hybris.com/browse/PLA-10932
 */
@Deprecated
public class AfterSessionUserChangeListener extends AbstractEventListener<AfterSessionUserChangeEvent>
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AfterSessionUserChangeListener.class);

	private UserService userService;
	private CommonI18NService commonI18NService;
	private B2BCostCenterService b2BCostCenterService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private ModelService modelService;
	private SessionService sessionService;


	@Override
	protected void onEvent(final AfterSessionUserChangeEvent event)
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
				resetCurrency(currentUser);
			}
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
		return null;
	}

	/**
	 * @param event
	 * @return True if even executed.
	 */

	protected boolean executeEvent(final AfterSessionUserChangeEvent event)
	{
		final AbstractTenant currentTenant = (AbstractTenant) Registry.getTenantByID(event.getScope().getTenantId());

		final boolean isShutDownOfCurrentTenantInProgress = Registry.isCurrentTenant(currentTenant)
				&& !RedeployUtilities.isShutdownInProgress();
		final boolean isTenantStarted = currentTenant != null
				&& currentTenant.getState() == de.hybris.platform.core.AbstractTenant.State.STARTED;
		final boolean executeEventBody = isTenantStarted && isShutDownOfCurrentTenantInProgress
				&& !getUserService().isAnonymousUser(getUserService().getCurrentUser());
		return executeEventBody;
	}

	/**
	 * Sets the the current currency from the list of available currencies from the users cost centers.
	 *
	 * @param user
	 *           the session user.
	 */
	protected void resetCurrency(final UserModel user)
	{

		if (user instanceof B2BCustomerModel)
		{
			final Set<CurrencyModel> currency = getB2BCostCenterService().getAvailableCurrencies(user);
			if (CollectionUtils.isNotEmpty(currency))
			{
				getCommonI18NService().setCurrentCurrency(currency.iterator().next());
			}
		}
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

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected B2BCostCenterService getB2BCostCenterService()
	{
		return b2BCostCenterService;
	}

	@Required
	public void setB2BCostCenterService(final B2BCostCenterService b2bCostCenterService)
	{
		b2BCostCenterService = b2bCostCenterService;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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
