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
package de.hybris.platform.assistedserviceservices.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;


/**
 * Test class for {@link DefaultAssistedServiceService}
 */
@IntegrationTest
public class AssistedServiceServiceTest extends ServicelayerTest
{
	@Resource
	private DefaultAssistedServiceService assistedServiceService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private UserService userService;

	private final String customerUID = "ascustomer";
	private final String firstCart = "00000001";
	private final String secondCart = "00000002";

	@Before
	public void setup() throws Exception
	{
		importCsv("/assistedserviceservices/test/cart_data.impex", "UTF-8");
		baseSiteService.setCurrentBaseSite("testSite", true);
	}

	@Test
	public void latestModifiedCartTest()
	{
		assertEquals("00000002", assistedServiceService.getLatestModifiedCart(userService.getUserForUID("ascustomer")).getCode());
		assertEquals("00000003", assistedServiceService.getLatestModifiedCart(userService.getUserForUID("ascustomer2")).getCode());
		assertNull(assistedServiceService.getLatestModifiedCart(userService.getUserForUID("ascustomer3")));
	}

	@Test
	public void testGetCartByCode()
	{
		final CartModel firstCartModel = assistedServiceService.getCartByCode(firstCart, userService.getUserForUID(customerUID));
		assertTrue(firstCartModel.getCode().equals(firstCart));

		final CartModel secondCartModel = assistedServiceService.getCartByCode(secondCart, userService.getUserForUID(customerUID));
		assertTrue(secondCartModel.getCode().equals(secondCart));
	}

	@Test
	public void testGetCarts()
	{
		final Collection<CartModel> cartsForCustomer = assistedServiceService.getCartsForCustomer((CustomerModel) userService.getUserForUID(customerUID));
		assertTrue(cartsForCustomer.stream().anyMatch(cartModel -> cartModel.getCode().equals(firstCart)));
		assertTrue(cartsForCustomer.stream().anyMatch(cartModel -> cartModel.getCode().equals(secondCart)));
	}
}
