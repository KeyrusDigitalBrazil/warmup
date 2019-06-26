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
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationAccessControlServiceImplTest
{
	private static final String PRODUCT_CODE = "product code";

	private static final String CONFIG_ID_CART_BOUND_DRAFT = "123";

	private static final String CART_ENTRY_ID = "ab1524fc";

	private static final String CONFIG_ID_PRODUCT_BOUND = "453";

	private static final String CONFIG_ID_CART_BOUND = "1234";

	private static final String CONFIG_ID_FROM_EXT = "ext123";

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;

	@Mock
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;

	@InjectMocks
	ProductConfigurationAccessControlServiceImpl classUnderTest = new ProductConfigurationAccessControlServiceImpl();

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		when(configurationAbstractOrderEntryLinkStrategy.getCartEntryForDraftConfigId(CONFIG_ID_CART_BOUND_DRAFT))
				.thenReturn(CART_ENTRY_ID);
		when(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID_CART_BOUND_DRAFT)).thenReturn(null);
		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_CART_BOUND_DRAFT)).thenReturn(true);

		when(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID_CART_BOUND)).thenReturn(CART_ENTRY_ID);
		when(configurationAbstractOrderEntryLinkStrategy.getCartEntryForDraftConfigId(CONFIG_ID_CART_BOUND)).thenReturn(null);
		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_CART_BOUND)).thenReturn(true);

		when(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID_PRODUCT_BOUND)).thenReturn(null);
		when(configurationAbstractOrderEntryLinkStrategy.getCartEntryForDraftConfigId(CONFIG_ID_PRODUCT_BOUND)).thenReturn(null);
		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_PRODUCT_BOUND)).thenReturn(true);

		when(configurationProductLinkStrategy.retrieveProductCode(CONFIG_ID_PRODUCT_BOUND)).thenReturn(PRODUCT_CODE);
		when(configurationProductLinkStrategy.retrieveProductCode(CONFIG_ID_CART_BOUND_DRAFT)).thenReturn(null);
		when(configurationProductLinkStrategy.retrieveProductCode(CONFIG_ID_CART_BOUND)).thenReturn(null);

		when(configurationLifecycleStrategy.isConfigKnown(anyString())).thenReturn(true);

		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_FROM_EXT))
				.thenThrow(new ConfigurationNotFoundException("NOT FOUND"));
		when(configurationLifecycleStrategy.isConfigKnown(CONFIG_ID_FROM_EXT)).thenReturn(false);

	}

	@Test
	public void testConfigurationAbstractOrderEntryLinkStrategy()
	{
		assertEquals(configurationAbstractOrderEntryLinkStrategy, classUnderTest.getConfigurationAbstractOrderEntryLinkStrategy());
	}

	@Test
	public void testIsUpdateAllowed()
	{
		assertTrue(classUnderTest.isUpdateAllowed(CONFIG_ID_CART_BOUND_DRAFT));
		assertTrue(classUnderTest.isUpdateAllowed(CONFIG_ID_PRODUCT_BOUND));
		assertFalse(classUnderTest.isUpdateAllowed(CONFIG_ID_CART_BOUND));
	}

	@Test
	public void testIsUpdateAllowedCartBound()
	{
		assertFalse(classUnderTest.isUpdateAllowed(CONFIG_ID_CART_BOUND));
	}

	@Test
	public void testIsUpdateAllowedDraftBelongsToDifferentUser()
	{
		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_CART_BOUND_DRAFT)).thenReturn(false);
		assertFalse(classUnderTest.isUpdateAllowed(CONFIG_ID_CART_BOUND_DRAFT));
	}

	@Test
	public void testIsRelatedToNonDraftDocumentTrue()
	{
		assertTrue(classUnderTest.isRelatedToNonDraftDocument(CONFIG_ID_CART_BOUND));
	}

	@Test
	public void testIsRelatedToNonDraftDocumentFalse()
	{
		assertFalse(classUnderTest.isRelatedToNonDraftDocument(CONFIG_ID_PRODUCT_BOUND));
	}

	@Test
	public void testIsReadAllowedProductBound()
	{
		assertTrue(classUnderTest.isReadAllowed(CONFIG_ID_PRODUCT_BOUND));
	}

	@Test
	public void testIsReadAllowedCartBoundTrue()
	{
		given(configurationAbstractOrderEntryLinkStrategy.isDocumentRelated(CONFIG_ID_CART_BOUND)).willReturn(true);
		given(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_CART_BOUND)).willReturn(true);
		assertTrue(classUnderTest.isReadAllowed(CONFIG_ID_CART_BOUND));
	}

	@Test
	public void testIsReadAllowedCartBoundFalse()
	{
		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_CART_BOUND)).thenReturn(false);
		given(configurationAbstractOrderEntryLinkStrategy.isDocumentRelated(CONFIG_ID_CART_BOUND)).willReturn(true);
		assertFalse(classUnderTest.isReadAllowed(CONFIG_ID_CART_BOUND));
	}

	@Test
	public void testIsReleaseAllowed()
	{
		assertTrue(classUnderTest.isReleaseAllowed(CONFIG_ID_CART_BOUND));
		assertTrue(classUnderTest.isReleaseAllowed(CONFIG_ID_PRODUCT_BOUND));
		assertTrue(classUnderTest.isReleaseAllowed(CONFIG_ID_CART_BOUND_DRAFT));
		Mockito.verify(configurationLifecycleStrategy, Mockito.times(3)).isConfigForCurrentUser(Mockito.anyString());
	}

	@Test
	public void testIsReleaseAllowedFalse()
	{
		when(configurationLifecycleStrategy.isConfigForCurrentUser(CONFIG_ID_CART_BOUND)).thenReturn(false);
		assertFalse(classUnderTest.isReleaseAllowed(CONFIG_ID_CART_BOUND));
	}


	@Test
	public void testIsReadAllowedExternalCreatedConfig()
	{
		assertTrue(classUnderTest.isReadAllowed(CONFIG_ID_FROM_EXT));
	}
}

