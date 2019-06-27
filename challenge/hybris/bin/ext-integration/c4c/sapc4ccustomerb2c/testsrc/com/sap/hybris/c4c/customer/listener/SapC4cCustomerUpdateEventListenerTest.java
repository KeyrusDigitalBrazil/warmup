/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.listener;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;


/**
 *
 */
@UnitTest
public class SapC4cCustomerUpdateEventListenerTest
{
	@InjectMocks
	private final SapC4cCustomerUpdateEventListener eventListener = new SapC4cCustomerUpdateEventListener();

	@Mock
	private SapC4cCustomerPublicationService c4cCustomerPublicationService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testOnEvent() throws IOException
	{

		final SapC4cCustomerUpdateEvent event = new SapC4cCustomerUpdateEvent();
		final C4CCustomerData customerData = new C4CCustomerData();
		event.setCustomerData(customerData);

		doNothing().when(c4cCustomerPublicationService).publishCustomerToCloudPlatformIntegration(customerData);

		eventListener.onEvent(event);

		verify(c4cCustomerPublicationService, times(1)).publishCustomerToCloudPlatformIntegration(customerData);
	}

}
