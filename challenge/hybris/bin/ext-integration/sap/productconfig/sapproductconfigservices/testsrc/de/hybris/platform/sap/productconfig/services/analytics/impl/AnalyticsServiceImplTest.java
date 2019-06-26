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
package de.hybris.platform.sap.productconfig.services.analytics.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("javadoc")
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AnalyticsServiceImplTest
{
	private static final String CONFIG_ID = "configId";
	@InjectMocks
	private AnalyticsServiceImpl classUnderTest;
	@Mock
	private AnalyticsProvider mockedAnalyticsProvider;
	@Mock
	private ProviderFactory mockedProviderFactory;
	@Mock
	private ProductConfigurationCacheAccessService mockedProductConfigurationCacheAccessService;
	@Mock
	private ConfigurationModelCacheStrategy mockedConfigurationModelCacheStrategy;
	private AnalyticsDocument analyticsDocument;



	@Before
	public void setUp() throws Exception
	{
		analyticsDocument = new AnalyticsDocument();
		given(mockedProviderFactory.getAnalyticsProvider()).willReturn(mockedAnalyticsProvider);
		when(mockedProductConfigurationCacheAccessService.getAnalyticData(CONFIG_ID)).thenReturn(analyticsDocument);
	}

	@Test
	public void testGetAnalyticsDataIsCached()
	{
		final AnalyticsDocument result = classUnderTest.getAnalyticData(CONFIG_ID);
		assertNotNull(result);
		assertEquals(analyticsDocument, result);
		verifyZeroInteractions(mockedAnalyticsProvider);
	}

	@Test
	public void testGetAnalyticsDataShouldBeCached()
	{
		final String configId = "123";
		final ConfigModel configModel = new ConfigModelImpl();
		final AnalyticsDocument newAnalyticsDocument = new AnalyticsDocument();
		when(mockedConfigurationModelCacheStrategy.getConfigurationModelEngineState(configId)).thenReturn(configModel);
		when(mockedAnalyticsProvider.getPopularity(configModel)).thenReturn(newAnalyticsDocument);
		final AnalyticsDocument result = classUnderTest.getAnalyticData(configId);
		assertNotNull(result);
		assertEquals(newAnalyticsDocument, result);
		verify(mockedProductConfigurationCacheAccessService, times(1)).setAnalyticData(configId, newAnalyticsDocument);
	}

	@Test
	public void testIsActiveTrue()
	{
		given(Boolean.valueOf(mockedAnalyticsProvider.isActive())).willReturn(Boolean.TRUE);
		assertTrue(classUnderTest.isActive());
	}

	@Test
	public void testIsActiveFalse()
	{
		given(Boolean.valueOf(mockedAnalyticsProvider.isActive())).willReturn(Boolean.FALSE);
		assertFalse(classUnderTest.isActive());
	}

}
