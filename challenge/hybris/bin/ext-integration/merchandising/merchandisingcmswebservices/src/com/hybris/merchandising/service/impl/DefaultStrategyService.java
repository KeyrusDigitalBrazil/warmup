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
package com.hybris.merchandising.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.hybris.merchandising.model.Strategy;
import com.hybris.merchandising.service.MerchStrategyServiceClient;
import com.hybris.merchandising.service.StrategyService;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;


/**
 * Default implementation of {@link StrategyService}.
 */
public class DefaultStrategyService implements StrategyService
{

	@Autowired
	@Qualifier("apiRegistryClientService")
	protected ApiRegistryClientService apiRegistryClientService;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategyService.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Strategy> getStrategies(final Integer pageNumber, final Integer pageSize)
	{
		final MerchStrategyServiceClient strategyClient = getClient();
		if(strategyClient != null)
		{
			if (pageSize != null && pageNumber != null)
			{
				return strategyClient.getStrategies(pageNumber, pageSize);
			}
			else
			{
				return strategyClient.getStrategies();
			}
		}
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Strategy getStrategy(final String id)
	{
		final MerchStrategyServiceClient strategyClient = getClient();
		if(strategyClient != null)
		{
			return strategyClient.getStrategy(id);
		}
		return null;
	}

	/**
	 * Retrieves the configured {@link MerchStrategyServiceClient}.
	 * @return configured client.
	 */
	private MerchStrategyServiceClient getClient()
	{
		try {
			return apiRegistryClientService.lookupClient(MerchStrategyServiceClient.class);
		} catch(CredentialException e)
		{
			LOG.error("Error retrieving client for Strategy Service", e);
		}
		return null;
	}
}
