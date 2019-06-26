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
package de.hybris.platform.acceleratorfacades.cart.action.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCartEntryActionFacadeIntegrationTest extends ServicelayerTest
{
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String USER = "abrode";

	@Resource
	private DefaultCartEntryActionFacade defaultCartEntryActionFacade;
	@Resource
	private CartFacade cartFacade;
	@Resource
	private CartService cartService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/acceleratorfacades/test/testCommerceCart.csv", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		final UserModel user = userService.getUserForUID(USER);
		userService.setCurrentUser(user);
	}

	@Test
	public void shouldGetErrorMessageKey()
	{
		final Optional<String> errorMessageKeyOptional = defaultCartEntryActionFacade.getErrorMessageKey(CartEntryAction.REMOVE);
		Assert.assertTrue("Error message key should be present for a valid CartEntryAction", errorMessageKeyOptional.isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetErrorMessageKey()
	{
		defaultCartEntryActionFacade.getErrorMessageKey(null);
	}

	@Test
	public void shouldGetSuccessMessageKeyForAction()
	{
		final Optional<String> successMessageKeyOptional = defaultCartEntryActionFacade
				.getSuccessMessageKey(CartEntryAction.REMOVE);
		Assert.assertTrue(successMessageKeyOptional.isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotGetSuccessMessageKeyForNullAction()
	{
		defaultCartEntryActionFacade.getSuccessMessageKey(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfExecuteNullAction() throws CartEntryActionException
	{
		defaultCartEntryActionFacade.executeAction(null, Collections.singletonList(Long.valueOf(1)));
	}

	@Test
	public void shouldExecuteRemoveAction() throws CommerceCartModificationException, CartEntryActionException
	{
		final String productCode = "HW1210-3422";

		// add products to cart
		cartFacade.addToCart("HW1210-3423", 1);
		cartFacade.addToCart("HW1210-3424", 1);
		cartFacade.addToCart(productCode, 1);
		final CartModel cart = cartService.getSessionCart();
		Assert.assertEquals("entry size", 3, cart.getEntries().size());

		// remove cart entries
		defaultCartEntryActionFacade.executeAction(CartEntryAction.REMOVE, Arrays.asList(Long.valueOf(0), Long.valueOf(1)));
		Assert.assertEquals("entry size", 1, cart.getEntries().size());
		Assert.assertEquals("product code", productCode, cart.getEntries().get(0).getProduct().getCode());
	}
}
