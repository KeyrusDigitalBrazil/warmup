/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.inboundservices.util;

import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.inboundservices.model.InboundRequestModel;
import de.hybris.platform.integrationservices.util.RequestPersistenceContext;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundRequestPersistenceContext extends RequestPersistenceContext
{
	private static final Logger LOG = LoggerFactory.getLogger(InboundRequestPersistenceContext.class);

	protected InboundRequestPersistenceContext()
	{
	}

	public static InboundRequestPersistenceContext create()
	{
		return new InboundRequestPersistenceContext();
	}

	@Override
	protected void after()
	{
		super.after();
		try
		{
			modelService().removeAll(searchAllInboundRequest());
			modelService().removeAll(searchAllInboundRequestErrors());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Failed to clean up media", e);
		}
	}

	public Collection<InboundRequestModel> searchAllInboundRequest()
	{
		return findAll(InboundRequestModel.class);
	}

	public Collection<InboundRequestErrorModel> searchAllInboundRequestErrors()
	{
		return findAll(InboundRequestErrorModel.class);
	}

	@Override
	protected String getMonitoringProperty()
	{
		return "inboundservices.monitoring.enabled";
	}
}
