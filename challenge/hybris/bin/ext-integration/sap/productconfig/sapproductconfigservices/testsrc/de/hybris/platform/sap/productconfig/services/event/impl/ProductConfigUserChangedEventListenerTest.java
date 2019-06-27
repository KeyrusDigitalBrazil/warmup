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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.sap.productconfig.services.event.util.impl.ProductConfigEventListenerUtil;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigUserChangedEventListenerTest
{
	private static final String USER_SESSION_ID = "userSessionId";

	private static final String CONFIG_ID = "123";

	@Mock
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;

	@Mock
	private ProductConfigEventListenerUtil productConfigEventListener;
	private ProductConfigUserChangedEventListener classUnderTest;
	@Mock
	private AfterSessionUserChangeEvent evt;
	@Mock
	private JaloSession jaloSession;
	@Mock
	User currentUser;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigUserChangedEventListenerForTest();
		when(productConfigEventListener.getUserSessionId(evt)).thenReturn(USER_SESSION_ID);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetterNotImplementedLifecycle()
	{
		classUnderTest = new ProductConfigUserChangedEventListener();
		classUnderTest.getConfigurationLifecycleStrategy();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetterNotImplementedUtil()
	{
		classUnderTest = new ProductConfigUserChangedEventListener();
		classUnderTest.getProductConfigEventListenerUtil();
	}

	@Test
	public void testOnEvent()
	{
		classUnderTest.onEvent(evt);
		verify(configurationLifecycleStrategy).updateUserLinkToConfiguration(USER_SESSION_ID);
	}

	@Test
	public void testGetCurrentUser()
	{
		when(evt.getSource()).thenReturn(jaloSession);
		when(jaloSession.getUser()).thenReturn(currentUser);
		final User result = classUnderTest.getCurrentUser(evt);
		assertNotNull(result);
		assertEquals(currentUser, result);
	}

	@Test
	public void testGetCurrentUserNull()
	{
		when(evt.getSource()).thenReturn(jaloSession);
		final User result = classUnderTest.getCurrentUser(evt);
		assertNull(result);
	}

	@Test
	public void testGetCurrentUserDifferentObject()
	{
		when(evt.getSource()).thenReturn(new String());
		final User result = classUnderTest.getCurrentUser(evt);
		assertNull(result);
	}

	public class ProductConfigUserChangedEventListenerForTest extends ProductConfigUserChangedEventListener
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
