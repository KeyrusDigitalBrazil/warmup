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
package com.hybris.ymkt.clickstream.listeners;

import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.clickstream.services.ClickStreamService;
import com.hybris.ymkt.common.user.UserContextService;


public class RegistrationEventListener extends AbstractEventListener<RegisterEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(RegistrationEventListener.class);

	protected ClickStreamService clickStreamService;
	protected UserContextService userContextService;

	@Override
	protected void onEvent(final RegisterEvent event)
	{
		if (this.userContextService.isIncognitoUser())
		{
			return;
		}

		final String anonymousUserId = userContextService.getAnonymousUserId();
		final String anonymousUserOrigin = userContextService.getAnonymousUserOrigin();
		final CustomerModel customer = event.getCustomer();
		final String loggedInUserId = customer.getCustomerID();
		final String loggedInUserOrigin = userContextService.getLoggedInUserOrigin();

		LOG.debug("anonymousUserId={} anonymousUserOrigin={} loggedInUserId={} loggedInUserOrigin={}", //
				anonymousUserId, anonymousUserOrigin, loggedInUserId, loggedInUserOrigin);

		this.clickStreamService.linkAnonymousAndLoggedInUsers(anonymousUserId, anonymousUserOrigin, loggedInUserId,
				loggedInUserOrigin);
	}

	@Required
	public void setClickStreamService(final ClickStreamService clickStreamService)
	{
		this.clickStreamService = Objects.requireNonNull(clickStreamService);
	}

	@Required
	public void setUserContextService(final UserContextService userContextService)
	{
		this.userContextService = Objects.requireNonNull(userContextService);
	}

}