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
 package de.hybris.platform.notificationservices.service.strategies.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultEmailChannelStrategyTest
{
	private DefaultEmailChannelStrategy emailStrategy;

	@Before
	public void setUp()
	{
		emailStrategy = new DefaultEmailChannelStrategy();
	}
	@Test
	public void testGetChannelValue()
	{
		final String testEmail = "test@hybris.com";
		final CustomerModel customer = mock(CustomerModel.class);
		customer.setCustomerID(testEmail);
		when(customer.getContactEmail()).thenReturn(testEmail);
		Assert.assertEquals(testEmail, emailStrategy.getChannelValue(customer));
	}

}
