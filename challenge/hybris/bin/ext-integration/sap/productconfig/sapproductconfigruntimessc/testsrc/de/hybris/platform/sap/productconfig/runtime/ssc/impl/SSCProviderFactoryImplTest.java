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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.DummyConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.ssc.SSCSessionAccessService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class SSCProviderFactoryImplTest
{

	private static final String SAP_PRODUCT_CONFIG_CONFIGURATION_PROVIDER = "sapProductConfigConfigurationProvider";

	private SSCProviderFactoryImpl classUnderTest;

	@Mock
	private SSCSessionAccessService sessionAccessService;

	@Mock
	private ApplicationContext mockApplicationContext;

	private final ConfigurationProvider dummyConfigurationProvider = new DummyConfigurationProvider();

	@Before
	public void setUp()
	{

		classUnderTest = new SSCProviderFactoryImpl();
		classUnderTest = Mockito.spy(classUnderTest);
		MockitoAnnotations.initMocks(this);

		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setApplicationContext(mockApplicationContext);
		classUnderTest.setConfigurationProviderBeanName(SAP_PRODUCT_CONFIG_CONFIGURATION_PROVIDER);

		Mockito.when(mockApplicationContext.getBean(SAP_PRODUCT_CONFIG_CONFIGURATION_PROVIDER))
				.thenReturn(dummyConfigurationProvider);
	}

	@Test
	public void testGetConfigurationProviderNewSession()
	{
		Mockito.when(sessionAccessService.getConfigurationProvider()).thenReturn(null);
		final ConfigurationProvider provider = classUnderTest.getConfigurationProvider();
		assertNotNull(provider);
		assertSame(dummyConfigurationProvider, provider);
	}

	@Test
	public void testGetConfigurationProviderExistingSession()
	{
		Mockito.when(sessionAccessService.getConfigurationProvider()).thenReturn(dummyConfigurationProvider);
		final ConfigurationProvider provider = classUnderTest.getConfigurationProvider();
		assertNotNull(provider);
		assertSame(dummyConfigurationProvider, provider);
	}
}
