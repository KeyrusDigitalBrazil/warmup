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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import de.hybris.platform.sap.productconfig.services.SessionAccessService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Base class for {@link SessionAccessService} aware lifecycle starategies
 */
public abstract class SessionServiceAware
{

	private SessionAccessService sessionAccessService;

	protected SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

}
