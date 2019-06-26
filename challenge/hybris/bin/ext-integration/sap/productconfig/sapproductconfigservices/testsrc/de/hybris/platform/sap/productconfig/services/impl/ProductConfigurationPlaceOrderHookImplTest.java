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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;



/**
 * Tests: ProductConfigurationPlaceOrderHookImpl
 *
 */
@UnitTest
public class ProductConfigurationPlaceOrderHookImplTest
{
	private static final String PRODUCT_CODE_1 = "product1";
	private static final String PRODUCT_CODE_2 = "product2";
	private static final String CONFIG_ID_1 = "config1";
	private static final String CONFIG_ID_2 = "config2";
	private static final long ORDER_ENTRY_KEY = 345;
	private static final String CONFIG_ID_ORDER_ENTRY = "configOrderEntry";

	private final ProductConfigurationPlaceOrderHookImpl classUnderTest = new ProductConfigurationPlaceOrderHookImpl();
	@Mock
	private ProductConfigurationService productConfigurationService;
	@Mock
	private CommerceCheckoutParameter parameter;
	@Mock
	private CommerceOrderResult orderModel;

	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Mock
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;


	@Mock
	private AbstractOrderEntryModel entry1;

	@Mock
	private AbstractOrderEntryModel entry2;

	@Mock
	private ProductModel product1;

	@Mock
	private ProductModel product2;

	@Mock
	private OrderModel placedOrder;
	private final List<AbstractOrderEntryModel> orderEntryList = new ArrayList<>();
	@Mock
	private AbstractOrderEntryModel orderEntry;
	private final PK orderPk = PK.fromLong(ORDER_ENTRY_KEY);


	/**
	 * Setup method
	 */
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(entry1.getEntryNumber()).thenReturn(Integer.valueOf(1));
		when(entry1.getPk()).thenReturn(PK.fromLong(1));
		when(entry1.getProduct()).thenReturn(product1);

		when(entry2.getEntryNumber()).thenReturn(Integer.valueOf(2));
		when(entry2.getPk()).thenReturn(PK.fromLong(2));
		when(entry2.getProduct()).thenReturn(product2);

		when(product1.getCode()).thenReturn(PRODUCT_CODE_1);
		when(product2.getCode()).thenReturn(PRODUCT_CODE_2);
		when(orderModel.getOrder()).thenReturn(placedOrder);
		when(placedOrder.getEntries()).thenReturn(orderEntryList);

		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry("1")).thenReturn(CONFIG_ID_1);
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		classUnderTest.setConfigurationAbstractOrderIntegrationStrategy(configurationAbstractOrderIntegrationStrategy);
		orderEntryList.add(orderEntry);
		when(orderEntry.getPk()).thenReturn(orderPk);
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(String.valueOf(ORDER_ENTRY_KEY)))
				.thenReturn(CONFIG_ID_ORDER_ENTRY);
	}

	/**
	 * Access to productConfigurationService
	 */
	@Test
	public void testConfigService()
	{
		assertNotNull(classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testAfterPlaceOrderNoConfigurables() throws InvalidCartException
	{
		prepareEntryList(false);
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(String.valueOf(ORDER_ENTRY_KEY))).thenReturn("");

		classUnderTest.afterPlaceOrder(parameter, orderModel);

		verify(configurationAbstractOrderIntegrationStrategy, times(1)).finalizeCartEntry(entry1);
		verify(configurationAbstractOrderIntegrationStrategy, times(0)).finalizeCartEntry(entry2);
		verify(configurationAbstractOrderIntegrationStrategy, times(0)).prepareForOrderReplication(orderEntry);
	}

	@Test
	public void testAfterPlaceOrder() throws InvalidCartException
	{
		prepareEntryList(true);

		classUnderTest.afterPlaceOrder(parameter, orderModel);

		verify(configurationAbstractOrderIntegrationStrategy, times(1)).finalizeCartEntry(entry1);
		verify(configurationAbstractOrderIntegrationStrategy, times(1)).finalizeCartEntry(entry2);
		verify(configurationAbstractOrderIntegrationStrategy, times(1)).prepareForOrderReplication(orderEntry);
	}

	private void prepareEntryList(final boolean withCfg)
	{
		final CartModel cartModel = new CartModel();
		final ArrayList entries = new ArrayList<>();
		if (withCfg)
		{
			when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry("2")).thenReturn(CONFIG_ID_2);
		}
		entries.add(entry1);
		entries.add(entry2);

		cartModel.setEntries(entries);

		when(parameter.getCart()).thenReturn(cartModel);
	}

	@Test
	public void testHasConfigurationAttached()
	{
		final boolean hasConfigurationAttached = classUnderTest.hasConfigurationAttached(entry1);
		assertTrue(hasConfigurationAttached);
	}

	@Test
	public void testHasConfigurationAttachedNoConfig()
	{
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(PK.fromLong(1).toString())).thenReturn(null);
		assertFalse(classUnderTest.hasConfigurationAttached(entry1));
	}

	@Test
	public void testHasConfigurationAttachedBlankConfigId()
	{
		when(configurationAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(PK.fromLong(1).toString())).thenReturn("");
		assertFalse(classUnderTest.hasConfigurationAttached(entry1));
	}

	@Test
	public void testPrepareForOrderReplication()
	{
		classUnderTest.prepareForOrderReplication(entry1);
		verify(configurationAbstractOrderIntegrationStrategy).prepareForOrderReplication(entry1);
	}

}
