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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationModelCacheStrategyImplTest
{
	private static final String CONFIG_ID = "123";

	@InjectMocks
	private DefaultConfigurationModelCacheStrategyImpl classUnderTest;

	@Mock
	private SessionAccessService mockedSessionAccessService;
	@Mock
	private ProductConfigurationCacheAccessService mockedCacheAccessService;
	@Mock
	private ConfigModel mockedConfigModel;

	@Before
	public void setUp()
	{
		when(mockedCacheAccessService.getConfigurationModelEngineState(CONFIG_ID)).thenReturn(mockedConfigModel);
	}

	@Test
	public void testPurge()
	{
		classUnderTest.purge();
		verify(mockedSessionAccessService).purge();
	}

	@Test
	public void testConfigAttributeStatesWithParameter()
	{
		classUnderTest.removeConfigAttributeState(CONFIG_ID);
		verify(mockedCacheAccessService).removeConfigAttributeState(CONFIG_ID);
	}

	@Test
	public void testGetConfigurationModelEngineState()
	{
		final ConfigModel result = classUnderTest.getConfigurationModelEngineState(CONFIG_ID);
		assertNotNull(result);
		verify(mockedCacheAccessService).getConfigurationModelEngineState(CONFIG_ID);
	}

	@Test
	public void testSetConfigurationModelEngineState()
	{
		classUnderTest.setConfigurationModelEngineState(CONFIG_ID, mockedConfigModel);
		verify(mockedCacheAccessService).setConfigurationModelEngineState(CONFIG_ID, mockedConfigModel);
	}
}
