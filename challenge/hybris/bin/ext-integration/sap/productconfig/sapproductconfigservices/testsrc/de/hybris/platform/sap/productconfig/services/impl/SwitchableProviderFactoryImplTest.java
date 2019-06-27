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
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SwitchableProviderFactoryImplTest
{
	private static final String NON_REGISTERED_FACTORY = "nonRegisteredFactory";
	private static final String ANOTHER_PROVIDER_FACTORY = "anotherProviderFactory";
	private static final String DEFAULT_PROVIDER_FACTORY = "defaultProviderFactory";

	@InjectMocks
	private SwitchableProviderFactoryImpl classUnderTest;

	@Mock
	private ProviderFactory anotherFactory;
	@Mock
	private ProviderFactory defaultFactory;
	@Mock
	private ConfigurationProvider defaultConfigProvider;
	@Mock
	private PricingProvider defaultPricingProvider;
	@Mock
	private AnalyticsProvider defaultAnalyticsProvider;
	@Mock
	private ProductCsticAndValueParameterProvider defaultValueParameterProvider;
	@Mock
	private PricingConfigurationParameter defaultPricingConfigurationParamter;
	@Mock
	private ConfigurationProvider anotherConfigurationProvider;
	@Mock
	private PricingProvider anotherPricingProvider;
	@Mock
	private ApplicationContext applCtxt;
	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		given(defaultFactory.getConfigurationProvider()).willReturn(defaultConfigProvider);
		given(defaultFactory.getPricingProvider()).willReturn(defaultPricingProvider);
		given(defaultFactory.getAnalyticsProvider()).willReturn(defaultAnalyticsProvider);
		given(defaultFactory.getProductCsticAndValueParameterProvider()).willReturn(defaultValueParameterProvider);
		given(defaultFactory.getPricingParameter()).willReturn(defaultPricingConfigurationParamter);
		final List<String> providerFactories = new ArrayList();
		providerFactories.add(ANOTHER_PROVIDER_FACTORY);
		providerFactories.add(DEFAULT_PROVIDER_FACTORY);
		classUnderTest.setRegisteredProviderFactories(providerFactories);
		given(anotherFactory.getConfigurationProvider()).willReturn(anotherConfigurationProvider);
		given(anotherFactory.getPricingProvider()).willReturn(anotherPricingProvider);
		given(applCtxt.getBean(ANOTHER_PROVIDER_FACTORY)).willReturn(anotherFactory);
		given(applCtxt.getBean(DEFAULT_PROVIDER_FACTORY)).willReturn(defaultFactory);

	}

	@Test
	public void testGetProviderFactoryDefault()
	{
		assertEquals(defaultFactory, classUnderTest.getActiveProviderFactory());
	}

	@Test
	public void testGetProviderFactory()
	{
		classUnderTest.setActiveProviderFactoryBeanName(DEFAULT_PROVIDER_FACTORY);
		assertEquals(defaultFactory, classUnderTest.getActiveProviderFactory());
	}

	@Test
	public void testGetProviderFactoryNotRegistered()
	{
		classUnderTest.setActiveProviderFactoryBeanName(null);
		given(sessionService.getAttribute(SwitchableProviderFactoryImpl.SESSION_ATTR_ACTIVE_PROVIDER_NAME))
				.willReturn(NON_REGISTERED_FACTORY);
		assertEquals(defaultFactory, classUnderTest.getActiveProviderFactory());
	}

	@Test
	public void testGetConfigurationProviderDefault()
	{
		assertSame(defaultConfigProvider, classUnderTest.getConfigurationProvider());
	}

	@Test
	public void testGetPricingProviderDefault()
	{
		assertSame(defaultPricingProvider, classUnderTest.getPricingProvider());
	}

	@Test
	public void testGetAnalyticsProviderDefault()
	{
		assertSame(defaultAnalyticsProvider, classUnderTest.getAnalyticsProvider());
	}

	@Test
	public void testGetValueParameterProviderProviderDefault()
	{
		assertSame(defaultValueParameterProvider, classUnderTest.getProductCsticAndValueParameterProvider());
	}

	@Test
	public void testGetPricingConfigurationParamterDefault()
	{
		assertSame(defaultPricingConfigurationParamter, classUnderTest.getPricingParameter());
	}

	@Test
	public void isProviderFactoryAvailable()
	{
		assertTrue(classUnderTest.isProviderFactoryAvailable(ANOTHER_PROVIDER_FACTORY));
	}

	@Test
	public void isProviderFactoryNotAvailable()
	{
		assertFalse(classUnderTest.isProviderFactoryAvailable(NON_REGISTERED_FACTORY));
	}

	@Test
	public void isProviderFactorySwitchAllowed()
	{
		assertTrue(classUnderTest.isProviderFactorySwitchAllowed());
	}

	@Test
	public void isProviderFactorySwitchAllowedWhenAleadySet()
	{
		classUnderTest.setActiveProviderFactoryBeanName(ANOTHER_PROVIDER_FACTORY);
		assertTrue(classUnderTest.isProviderFactorySwitchAllowed());
	}

	@Test
	public void switchProviderFactoryAndReturnAnotherConfigProvider()
	{
		classUnderTest.setProviderFactoryInstances(Collections.singletonMap(ANOTHER_PROVIDER_FACTORY, anotherFactory));
		classUnderTest.switchProviderFactory(ANOTHER_PROVIDER_FACTORY);
		assertSame(anotherConfigurationProvider, classUnderTest.getConfigurationProvider());
	}

	@Test
	public void switchProviderFactoryAndLazyLoadIt()
	{
		classUnderTest.switchProviderFactory(ANOTHER_PROVIDER_FACTORY);
		assertSame(anotherPricingProvider, classUnderTest.getPricingProvider());
	}

	@Test(expected = IllegalArgumentException.class)
	public void switchToUnknownProviderFactory()
	{
		classUnderTest.setProviderFactoryInstances(Collections.singletonMap(ANOTHER_PROVIDER_FACTORY, anotherFactory));
		classUnderTest.switchProviderFactory(NON_REGISTERED_FACTORY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void switchToProviderFactoryNothingRegistered()
	{
		classUnderTest.setRegisteredProviderFactories(null);
		classUnderTest.switchProviderFactory(DEFAULT_PROVIDER_FACTORY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void switchToProviderFactoryNothingRegisteredEmptyList()
	{
		classUnderTest.setRegisteredProviderFactories(Collections.emptyList());
		classUnderTest.switchProviderFactory(DEFAULT_PROVIDER_FACTORY);
	}

	@Test
	public void testSetRegisteredProviderFactoriesEmptyList()
	{
		classUnderTest.setRegisteredProviderFactories(Collections.EMPTY_LIST);
		assertTrue(classUnderTest.getRegisteredProviderFactories().isEmpty());
	}

	@Test
	public void testSetRegisteredProviderFactoriesNull()
	{
		classUnderTest.setRegisteredProviderFactories(null);
		assertNull(classUnderTest.getRegisteredProviderFactories());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetRegisteredProviderFactoriesImmutable()
	{
		final List<String> registeredProviderFactories = new ArrayList<>();
		registeredProviderFactories.add(ANOTHER_PROVIDER_FACTORY);
		classUnderTest.setRegisteredProviderFactories(registeredProviderFactories);
		classUnderTest.getRegisteredProviderFactories().add("yet another provider factory bean name");
	}

	@Test
	public void testGetRegisteredProviderFactoriesNoChangePossibleFromSetList()
	{
		final List<String> registeredProviderFactories = new ArrayList<>();
		registeredProviderFactories.add(ANOTHER_PROVIDER_FACTORY);
		classUnderTest.setRegisteredProviderFactories(registeredProviderFactories);
		registeredProviderFactories.add("yet another provider factory bean name");
		assertEquals(2, registeredProviderFactories.size());
		assertEquals(1, classUnderTest.getRegisteredProviderFactories().size());
	}

	@Test
	public void testGetActiveProviderFactoryWritesNameToSession()
	{
		classUnderTest.setProviderFactoryInstances(Collections.singletonMap(DEFAULT_PROVIDER_FACTORY, defaultFactory));
		classUnderTest.setActiveProviderFactoryBeanName(DEFAULT_PROVIDER_FACTORY);
		final ProviderFactory factory = classUnderTest.getActiveProviderFactory();
		verify(sessionService).setAttribute(SwitchableProviderFactoryImpl.SESSION_ATTR_ACTIVE_PROVIDER_NAME,
				DEFAULT_PROVIDER_FACTORY);
		assertSame(defaultFactory, factory);
	}

	@Test
	public void testGetActiveProviderFactoryReadsNameFromSession()
	{
		given(sessionService.getAttribute(SwitchableProviderFactoryImpl.SESSION_ATTR_ACTIVE_PROVIDER_NAME))
				.willReturn(ANOTHER_PROVIDER_FACTORY);
		classUnderTest.setProviderFactoryInstances(Collections.singletonMap(ANOTHER_PROVIDER_FACTORY, anotherFactory));
		classUnderTest.setActiveProviderFactoryBeanName(DEFAULT_PROVIDER_FACTORY);
		final ProviderFactory factory = classUnderTest.getActiveProviderFactory();
		assertSame(anotherFactory, factory);
	}

}
