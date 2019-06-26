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

package de.hybris.platform.outboundservices.util;

import de.hybris.platform.integrationservices.util.RequestPersistenceContext;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A rule for cleaning up media created in the platform as a side effect of a test
 */
public class OutboundRequestPersistenceContext extends RequestPersistenceContext
{
	private static final Logger LOG = LoggerFactory.getLogger(OutboundRequestPersistenceContext.class);

	protected OutboundRequestPersistenceContext()
	{
	}

	public static OutboundRequestPersistenceContext create()
	{
		return new OutboundRequestPersistenceContext();
	}

	@Override
	protected void after()
	{
		super.after();
		try
		{
			modelService().removeAll(searchAllOutboundRequest());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Failed to clean up media", e);
		}
	}

	public Collection<OutboundRequestModel> searchAllOutboundRequest()
	{
		return findAll(OutboundRequestModel.class);
	}

	@Override
	protected String getMonitoringProperty()
	{
		return "outboundservices.monitoring.enabled";
	}
}
