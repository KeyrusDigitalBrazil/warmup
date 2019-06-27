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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
/**
 * Unit tests for {@link SessionAccessFacadeImpl}
 */
public class SessionAccessFacadeImplTest
{
	SessionAccessFacadeImpl classUnderTest = new SessionAccessFacadeImpl();

	@Mock
	SessionAccessService sessionAccessService;

	@Mock
	ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	ConfigurationModelCacheStrategy configModelCacheStrategy;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigModelCacheStrategy(configModelCacheStrategy);
	}

	@Test
	public void testService()
	{
		assertNotNull(classUnderTest.getSessionAccessService());
	}

	@Test
	public void testConfigIdForCartEntry()
	{
		final String configId = "1";
		final String cartEntryKey = "X";

		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartEntryKey)).thenReturn(configId);
		assertEquals(configId, classUnderTest.getConfigIdForCartEntry(cartEntryKey));
	}

	@Test
	public void testUiStatusForCartEntry()
	{
		final Object status = "1";
		final String cartEntryKey = "X";

		Mockito.when(sessionAccessService.getUiStatusForCartEntry(cartEntryKey)).thenReturn(status);
		classUnderTest.setUiStatusForCartEntry(cartEntryKey, status);
		assertEquals(status, classUnderTest.getUiStatusForCartEntry(cartEntryKey));
	}

	@Test
	public void testUiStatusForProduct()
	{
		final Object status = "1";
		final String productKey = "X";

		Mockito.when(sessionAccessService.getUiStatusForProduct(productKey)).thenReturn(status);
		classUnderTest.setUiStatusForProduct(productKey, status);
		assertEquals(status, classUnderTest.getUiStatusForProduct(productKey));
	}

	@Test
	public void testRemoveStatusForProduct()
	{
		final String productKey = "A";
		classUnderTest.removeUiStatusForProduct(productKey);
		Mockito.verify(sessionAccessService, Mockito.times(1)).removeUiStatusForProduct((productKey));
	}

	@Test
	public void testRemoveStatusForCartEntry()
	{
		final String cartEntryKey = "A";
		classUnderTest.removeUiStatusForCartEntry(cartEntryKey);
		Mockito.verify(sessionAccessService, Mockito.times(1)).removeUiStatusForCartEntry((cartEntryKey));
	}

	@Test
	public void testCartEntryForProduct()
	{
		{
			final String productKey = "1";
			final String cartEntryKey = "X";


			Mockito.when(sessionAccessService.getCartEntryForProduct(productKey)).thenReturn(cartEntryKey);

			assertEquals(cartEntryKey, classUnderTest.getCartEntryForProduct(productKey));
		}
	}

	@Test
	public void testRemoveConfigIdForCartEntry()
	{
		final String configId = "1";
		final String cartEntryKey = "X";

		Mockito.when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartEntryKey)).thenReturn(configId);
		assertEquals(configId, classUnderTest.getConfigIdForCartEntry(cartEntryKey));
		classUnderTest.removeConfigIdForCartEntry(cartEntryKey);
		Mockito.verify(configurationAbstractOrderEntryLinkStrategy, Mockito.times(1)).removeConfigIdForCartEntry((cartEntryKey));
	}
}
