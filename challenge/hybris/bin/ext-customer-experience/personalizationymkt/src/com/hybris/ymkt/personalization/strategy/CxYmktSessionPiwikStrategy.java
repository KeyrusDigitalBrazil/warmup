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
/**
 *
 */
package com.hybris.ymkt.personalization.strategy;

import de.hybris.platform.personalizationservices.model.process.CxPersonalizationProcessModel;
import de.hybris.platform.personalizationservices.process.strategies.impl.AbstractCxProcessParameterStrategy;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.constants.SapymktcommonConstants;


/**
 * This class will inject a PiwikId key into a background process in order to be later retrieved from the user session
 * for personalization evaluation.
 */
public class CxYmktSessionPiwikStrategy extends AbstractCxProcessParameterStrategy
{
	private static final String PIWIK_ID_SESSION_KEY = SapymktcommonConstants.PERSONALIZATION_PIWIK_ID_SESSION_KEY;

	private SessionService sessionService;

	@Override
	public void load(final CxPersonalizationProcessModel process)
	{
		consumeProcessParameter(process, PIWIK_ID_SESSION_KEY, this::setPiwikIdAttribute);
	}

	@Override
	public void store(final CxPersonalizationProcessModel process)
	{
		Optional.ofNullable(this.sessionService.getAttribute(PIWIK_ID_SESSION_KEY)) //
				.ifPresent(piwikId -> this.getProcessParameterHelper().setProcessParameter(process, PIWIK_ID_SESSION_KEY, piwikId));
	}

	protected void setPiwikIdAttribute(final String piwikId)
	{
		sessionService.setAttribute(PIWIK_ID_SESSION_KEY, piwikId);
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}


}
