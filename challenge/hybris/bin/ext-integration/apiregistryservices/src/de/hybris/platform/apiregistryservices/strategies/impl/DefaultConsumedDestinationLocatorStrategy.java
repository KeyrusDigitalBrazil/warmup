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

package de.hybris.platform.apiregistryservices.strategies.impl;

import de.hybris.platform.apiregistryservices.model.AbstractDestinationModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 *  Default implementation of interface {@link ConsumedDestinationLocatorStrategy}:
 *  Find the {@link ConsumedDestinationModel} by client type name
 */
public class DefaultConsumedDestinationLocatorStrategy implements ConsumedDestinationLocatorStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultConsumedDestinationLocatorStrategy.class);
	public static final String CLIENT_CLASS_NAME = "clientClassName";

	private DestinationService<AbstractDestinationModel> destinationService;

	@Override
	public ConsumedDestinationModel lookup(final String clientTypeName)
	{
		final List<AbstractDestinationModel> destinations = getDestinationService().getAllDestinations();

		final Optional<AbstractDestinationModel> destination = destinations.stream()
				.filter(ConsumedDestinationModel.class::isInstance)
				.filter(dest -> dest.getAdditionalProperties().containsKey(CLIENT_CLASS_NAME)
						&& dest.getAdditionalProperties().get(CLIENT_CLASS_NAME).equals(clientTypeName))
				.findFirst();

		if (!destination.isPresent())
		{
			LOG.warn("Failed to find consumed destination for the given client type name [{}].", clientTypeName);
			return null;
		}

		return (ConsumedDestinationModel) destination.get();
	}

	protected DestinationService<AbstractDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<AbstractDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}
}
