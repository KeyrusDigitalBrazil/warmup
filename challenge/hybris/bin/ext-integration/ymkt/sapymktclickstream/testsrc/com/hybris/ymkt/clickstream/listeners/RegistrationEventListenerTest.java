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
/**
 *
 */
package com.hybris.ymkt.clickstream.listeners;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.core.model.user.CustomerModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.ymkt.clickstream.services.ClickStreamService;
import com.hybris.ymkt.common.user.UserContextService;


@UnitTest
public class RegistrationEventListenerTest
{
	@Mock
	ClickStreamService clickStreamService;

	RegistrationEventListener listener = new RegistrationEventListener();

	@Mock
	UserContextService userContextService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		listener.setClickStreamService(clickStreamService);
		listener.setUserContextService(userContextService);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		final RegisterEvent event = new RegisterEvent();
		event.setCustomer(new CustomerModel());

		listener.onEvent(event);
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(Boolean.TRUE);
		listener.onEvent(event);
	}

}
