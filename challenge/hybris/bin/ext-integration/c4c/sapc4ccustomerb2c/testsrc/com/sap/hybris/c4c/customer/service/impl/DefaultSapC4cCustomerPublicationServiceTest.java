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
package com.sap.hybris.c4c.customer.service.impl;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.scpiconnector.data.ResponseData;
import com.sap.hybris.scpiconnector.httpconnection.CloudPlatformIntegrationConnection;

/**
 *
 */
@UnitTest
public class DefaultSapC4cCustomerPublicationServiceTest
{

	@InjectMocks
	private final DefaultSapC4cCustomerPublicationService sapC4cCustomerPublicationService = new DefaultSapC4cCustomerPublicationService();

	@Mock
	private CloudPlatformIntegrationConnection cloudPlatformIntegrationConnection;

	@Mock
	private ConfigurationService configurationService;

	private static final String DUMMY_TEXT = "dummy";

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPublishCustomerToCloudPlatformIntegration() throws IOException
	{
		final Configuration config = Mockito.mock(Configuration.class);
		final C4CCustomerData customerJson = Mockito.mock(C4CCustomerData.class);
		final ResponseData response = Mockito.mock(ResponseData.class);
		when(configurationService.getConfiguration()).thenReturn(config);
		when(config.getString(Mockito.anyString())).thenReturn(DUMMY_TEXT);
		when(customerJson.toString()).thenReturn(DUMMY_TEXT);
		when(cloudPlatformIntegrationConnection.sendPost(Mockito.anyString(), Mockito.anyString())).thenReturn(response);

		sapC4cCustomerPublicationService.publishCustomerToCloudPlatformIntegration(customerJson);

		verify(cloudPlatformIntegrationConnection, times(1)).sendPost(Mockito.anyString(), Mockito.anyString());

	}
}
