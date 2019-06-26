/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.context.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.merchandising.constants.MerchandisingConstants;
import com.hybris.merchandising.context.ContextRepository;
import com.hybris.merchandising.context.ContextService;

import de.hybris.platform.servicelayer.session.SessionService;


/**
 * Default implementation of {@link ContextService}
 *
 */
public class DefaultContextService implements ContextService
{
	SessionService sessionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hybris.merchandising.context.ContextService#getContextRepository()
	 */
	@Override
	public ContextRepository getContextRepository()
	{
		return Optional.ofNullable(sessionService.getAttribute(MerchandisingConstants.CONTEXT_STORE_KEY))
				.map(ContextRepository.class::cast)
				.orElseGet(() -> {
					final DefaultContextRepository defaultContextStore = new DefaultContextRepository();
					sessionService.setAttribute(MerchandisingConstants.CONTEXT_STORE_KEY, defaultContextStore);
					return defaultContextStore;
				});
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
