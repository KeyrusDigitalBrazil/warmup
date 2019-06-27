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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.servicelayer.session.SessionService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SSCSessionAccessServiceImplTest
{
	@Mock
	private ConfigurationProvider configurationProvider;
	@Mock
	private SessionService sessionService;
	@InjectMocks
	private SSCSessionAccessServiceImpl classUnderTest;

	@Test
	public void testSetConfigurationProvider()
	{
		classUnderTest.setConfigurationProvider(configurationProvider);
		verify(sessionService).setAttribute(SSCSessionAccessServiceImpl.CONFIG_PROVIDER_SESSION_ATTR_NAME, configurationProvider);
	}

	@Test
	public void testGetConfigurationProvider()
	{
		when(sessionService.getAttribute(SSCSessionAccessServiceImpl.CONFIG_PROVIDER_SESSION_ATTR_NAME))
				.thenReturn(configurationProvider);
		assertEquals(configurationProvider, classUnderTest.getConfigurationProvider());
		verify(sessionService).getAttribute(SSCSessionAccessServiceImpl.CONFIG_PROVIDER_SESSION_ATTR_NAME);
	}

	@Test
	public void testGetConfigurationProviderNotAvailable()
	{
		when(sessionService.getAttribute(SSCSessionAccessServiceImpl.CONFIG_PROVIDER_SESSION_ATTR_NAME)).thenReturn(null);
		assertNull(classUnderTest.getConfigurationProvider());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetConfigurationProviderWrongObject()
	{
		when(sessionService.getAttribute(SSCSessionAccessServiceImpl.CONFIG_PROVIDER_SESSION_ATTR_NAME)).thenReturn(new String());
		classUnderTest.getConfigurationProvider();
	}
}
