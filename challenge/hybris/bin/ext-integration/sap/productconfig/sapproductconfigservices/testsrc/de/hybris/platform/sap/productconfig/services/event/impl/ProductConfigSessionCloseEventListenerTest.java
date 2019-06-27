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
package de.hybris.platform.sap.productconfig.services.event.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.event.events.BeforeSessionCloseEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigSessionCloseEventListenerTest
{
	private static final String CONFIG_ID = "123";
	private static final String USER_SESSION_ID = "userSessionId";

	private ProductConfigSessionCloseEventListener classUnderTest;
	@Mock
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;
	@Mock
	private ProductConfigEventListenerUtil productConfigEventListener;
	private BeforeSessionCloseEvent evt;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigSessionCloseEventListenerForTest();
		evt = new BeforeSessionCloseEvent();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetterNotImplementedLifecycleStrategy()
	{
		classUnderTest = new ProductConfigSessionCloseEventListener();
		classUnderTest.getConfigurationLifecycleStrategy();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetterNotImplementedEventListenerUtil()
	{
		classUnderTest = new ProductConfigSessionCloseEventListener();
		classUnderTest.getProductConfigEventListenerUtil();
	}

	@Test
	public void testOnEventNull()
	{
		when(productConfigEventListener.getUserSessionId(null)).thenReturn(null);
		classUnderTest.onEvent(null);
		verify(configurationLifecycleStrategy).releaseExpiredSessions(null);
	}

	@Test
	public void testOnEvent()
	{
		when(productConfigEventListener.getUserSessionId(evt)).thenReturn(USER_SESSION_ID);
		classUnderTest.onEvent(evt);
		verify(configurationLifecycleStrategy).releaseExpiredSessions(USER_SESSION_ID);
	}

	public class ProductConfigSessionCloseEventListenerForTest extends ProductConfigSessionCloseEventListener
	{

		@Override
		protected ConfigurationLifecycleStrategy getConfigurationLifecycleStrategy()
		{
			return configurationLifecycleStrategy;
		}

		@Override
		protected ProductConfigEventListenerUtil getProductConfigEventListenerUtil()
		{
			return productConfigEventListener;
		}
	}
}
