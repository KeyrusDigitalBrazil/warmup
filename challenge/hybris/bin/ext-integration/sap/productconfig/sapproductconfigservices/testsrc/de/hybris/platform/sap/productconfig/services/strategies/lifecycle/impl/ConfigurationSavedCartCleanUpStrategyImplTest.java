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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartRestorationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationSavedCartCleanUpStrategyImplTest
{
	private static final String productKey = "DRAGON_CAR";
	private static final long longValue = 1234;
	private static final String DRAFT_CONFIG_ID = "83736";
	private static final String CONFIG_ID = "123";
	private static final String CONFIG_ID_SECOND = "1254";


	@Mock
	private CartModel cartModel;

	@Mock
	private CartEntryModel cartEntry;

	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;

	@Mock
	private CartService cartService;

	@Mock
	private CPQConfigurableChecker cpqConfigurableChecker;

	@Mock
	private ProductModel productModel;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private ConfigurationLifecycleStrategy configurationLifecycleStrategy;

	@InjectMocks
	private ConfigurationSavedCartCleanUpStrategyImpl classUnderTest;


	private PK pk;
	private final CommerceCartParameter parameters = new CommerceCartParameter();
	private final CommerceCartRestoration commerceCartRestoration = new CommerceCartRestoration();
	private final List<AbstractOrderEntryModel> entryList = new ArrayList<AbstractOrderEntryModel>();
	private final CommerceCartRestorationStrategy commerceSaveCartRestorationStrategy = new DefaultCommerceCartRestorationStrategy();

	@Before
	public void initialize() throws CommerceCartRestorationException
	{
		entryList.add(cartEntry);
		parameters.setCart(cartModel);
		when(cartModel.getEntries()).thenReturn(entryList);
		pk = PK.fromLong(longValue);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartEntry.getPk()).thenReturn(pk);
		when(cartEntry.getProduct()).thenReturn(productModel);
		when(productModel.getCode()).thenReturn(productKey);
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(productModel)).thenReturn(true);
		when(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(pk.toString())).thenReturn(DRAFT_CONFIG_ID);
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(pk.toString())).thenReturn(CONFIG_ID);
		when(configurationProductLinkStrategy.getConfigIdForProduct(productKey)).thenReturn(CONFIG_ID);
	}

	@Test
	public void testAbstractOrderEntryLinkStrategy()
	{
		assertEquals(configurationAbstractOrderEntryLinkStrategy, classUnderTest.getAbstractOrderEntryLinkStrategy());
	}

	@Test
	public void testLifecycleStrategy()
	{
		assertEquals(configurationLifecycleStrategy, classUnderTest.getConfigurationLifecycleStrategy());
	}

	@Test
	public void testCleanUpNoSessionCart()
	{
		when(cartService.hasSessionCart()).thenReturn(false);

		classUnderTest.cleanUpCart();
		verify(cartModel, times(0)).getEntries();
		verify(configurationProductLinkStrategy, times(0)).removeConfigIdForProduct(productKey);
	}

	@Test
	public void testCleanUpForNonConfigurable()
	{
		when(cpqConfigurableChecker.isCPQConfiguratorApplicableProduct(productModel)).thenReturn(Boolean.FALSE);

		classUnderTest.cleanUpCart();
		verify(cartModel, times(1)).getEntries();
		verify(configurationProductLinkStrategy, times(0)).removeConfigIdForProduct(productKey);
	}

	@Test
	public void testCleanUpForEntryConfigurable()
	{
		classUnderTest.cleanUpCart();
		verify(cartModel, times(1)).getEntries();
		verify(configurationLifecycleStrategy, times(1)).releaseSession(DRAFT_CONFIG_ID);
		verify(configurationProductLinkStrategy, times(1)).removeConfigIdForProduct(productKey);
	}

	@Test(expected = NullPointerException.class)
	public void testCleanUpForEntryNoConfig()
	{
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(pk.toString())).thenReturn(null);
		classUnderTest.cleanUpCart();
	}

	@Test
	public void testCleanUpForEntryConfigurableNoProductLink()
	{
		when(configurationProductLinkStrategy.getConfigIdForProduct(productKey)).thenReturn(CONFIG_ID_SECOND);
		classUnderTest.cleanUpCart();
		verify(configurationLifecycleStrategy, times(1)).releaseSession(DRAFT_CONFIG_ID);
		verify(configurationProductLinkStrategy, times(0)).removeConfigIdForProduct(productKey);
	}

	@Test
	public void testCleanUpCartEntryNoDraft()
	{
		when(configurationAbstractOrderEntryLinkStrategy.getDraftConfigIdForCartEntry(pk.toString())).thenReturn(null);
		classUnderTest.cleanUpCartEntry(cartEntry);
		verify(configurationLifecycleStrategy, times(0)).releaseSession(DRAFT_CONFIG_ID);
		verify(configurationProductLinkStrategy, times(1)).removeConfigIdForProduct(productKey);
	}

}
