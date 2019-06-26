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
package de.hybris.platform.customercouponfacades.strategies;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customercouponfacades.strategies.impl.DefaultCustomerNotificationPreferenceCheckStrategy;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



/**
 * Unit test for {@link DefaultCustomerNotificationPreferenceCheckStrategy}
 */
@UnitTest
public class DefaultCustomerNotificationPreferenceCheckStrategyTest
{
	@Mock
	private UserService userService;
	@Mock
	private CustomerModel customer;

	private DefaultCustomerNotificationPreferenceCheckStrategy strategy;

	private Set<NotificationChannel> channel;


	@Before
	public void init(){
		MockitoAnnotations.initMocks(this);
		strategy = new DefaultCustomerNotificationPreferenceCheckStrategy();
		strategy.setUserService(userService);
		channel = new HashSet();
		Mockito.doReturn(customer).when(userService).getCurrentUser();
	}

	@Test
	public void testcheckCustomerNotificationPreference()
	{

		Mockito.when(customer.getNotificationChannels()).thenReturn(channel);
		Assert.assertFalse(strategy.checkCustomerNotificationPreference());

		channel.add(NotificationChannel.EMAIL);

		Assert.assertTrue(strategy.checkCustomerNotificationPreference());

	}

}
