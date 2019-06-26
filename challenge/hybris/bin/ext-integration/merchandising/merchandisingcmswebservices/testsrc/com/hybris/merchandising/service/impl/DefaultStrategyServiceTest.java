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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.merchandising.model.Strategy;
import com.hybris.merchandising.service.MerchStrategyServiceClient;

import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;


/**
 * DefaultStrategyServiceTest is a unit test class for the {@link DefaultStrategyService}.
 */
public class DefaultStrategyServiceTest extends StrategyTest
{
	DefaultStrategyService strategyService;
	MerchStrategyServiceClient mockStrategyClient;

	@Before
	public void setUp() throws Exception
	{
		final ApiRegistryClientService apiRegistryClientService = Mockito.mock(ApiRegistryClientService.class);
		mockStrategyClient = Mockito.mock(MerchStrategyServiceClient.class);
		Mockito.when(apiRegistryClientService.lookupClient(MerchStrategyServiceClient.class)).thenReturn(mockStrategyClient);

		strategyService = new DefaultStrategyService();
		strategyService.apiRegistryClientService = apiRegistryClientService;
	}

	@Test
	public void testNoPagination()
	{
		final List<Strategy> strategies = getMockStrategies(100);
		Assert.assertNotNull("Expected the generated strategies to not be null", strategies);
		Mockito.when(mockStrategyClient.getStrategies()).thenReturn(strategies);

		final List<Strategy> retrievedStrategies = strategyService.getStrategies(null, null);
		Assert.assertNotNull("Expected the retrieved strategies to not be null", retrievedStrategies);
		Mockito.verify(mockStrategyClient, Mockito.times(1)).getStrategies();
		for (int i = 0; i < retrievedStrategies.size(); i++)
		{
			verifyStrategy(i, retrievedStrategies.get(i));
		}
	}

	@Test
	public void testPagination()
	{
		final List<Strategy> strategies = getMockStrategies(100);
		Assert.assertNotNull("Expected the generated strategies to not be null", strategies);
		Mockito.when(mockStrategyClient.getStrategies(Integer.valueOf(1), Integer.valueOf(10)))
				.thenReturn(strategies.subList(0, 10));
		Mockito.when(mockStrategyClient.getStrategies(Integer.valueOf(2), Integer.valueOf(10)))
				.thenReturn(strategies.subList(10, 20));
		final List<Strategy> retrievedStrategiesPage1 = strategyService.getStrategies(Integer.valueOf(1), Integer.valueOf(10));
		final List<Strategy> retrievedStrategiesPage2 = strategyService.getStrategies(Integer.valueOf(2), Integer.valueOf(10));

		Mockito.verify(mockStrategyClient, Mockito.times(1)).getStrategies(Integer.valueOf(1), Integer.valueOf(10));
		Mockito.verify(mockStrategyClient, Mockito.times(1)).getStrategies(Integer.valueOf(2), Integer.valueOf(10));

		for (int i = 0; i < 10; i++)
		{
			verifyStrategy(i, retrievedStrategiesPage1.get(i));
		}

		for (int i = 10; i < 20; i++)
		{
			verifyStrategy(i, retrievedStrategiesPage2.get(i - 10));
		}
	}
}
