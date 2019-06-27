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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationAbstractOrderIntegrationHelper;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConfigurationSavedCartIntegrationFacadeImplTest
{
	private static final String code = "1234";

	ConfigurationSavedCartIntegrationFacadeImpl classUnderTest = new ConfigurationSavedCartIntegrationFacadeImpl();

	@Mock
	CommerceCartService commerceCartService;

	@Mock
	UserService userService;

	@Mock
	ConfigurationAbstractOrderIntegrationHelper configurationAbstractOrderIntegrationHelper;

	@Mock
	private UserModel user;

	@Mock
	private CartModel cart;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(commerceCartService.getCartForCodeAndUser(code, user)).thenReturn(cart);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		classUnderTest.setCommerceCartService(commerceCartService);
		classUnderTest.setUserService(userService);
		classUnderTest.setConfigurationAbstractOrderIntegrationHelper(configurationAbstractOrderIntegrationHelper);
	}

	@Test
	public void testCommerceCartService()
	{
		assertEquals(commerceCartService, classUnderTest.getCommerceCartService());
	}

	@Test
	public void testUserService()
	{
		assertEquals(userService, classUnderTest.getUserService());
	}

	@Test
	public void testIntegrationHelper()
	{
		assertEquals(configurationAbstractOrderIntegrationHelper, classUnderTest.getConfigurationAbstractOrderIntegrationHelper());
	}

	@Test
	public void testFindSavedCart() throws CommerceSaveCartException
	{
		assertEquals(cart, classUnderTest.findSavedCart(code));
	}

	@Test(expected = CommerceSaveCartException.class)
	public void testFindSavedCartNotFound() throws CommerceSaveCartException
	{
		classUnderTest.findSavedCart("NOT_EXISTING");
	}
}
