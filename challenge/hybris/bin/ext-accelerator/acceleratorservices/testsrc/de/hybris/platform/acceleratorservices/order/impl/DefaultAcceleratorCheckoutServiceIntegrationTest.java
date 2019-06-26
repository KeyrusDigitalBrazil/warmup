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
package de.hybris.platform.acceleratorservices.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


/**
 * Integration test for {@link DefaultAcceleratorCheckoutService}
 *
 */
@IntegrationTest
@ContextConfiguration(locations =
{ "classpath:acceleratorservices/accelerator-checkout-service-spring-test.xml" })
public class DefaultAcceleratorCheckoutServiceIntegrationTest extends BaseCommerceBaseTest
{
	private static final String TEST_BASESITE_UID = "testSite";

	@Resource
	private DefaultAcceleratorCheckoutService testAcceleratorCheckoutService;
	@Resource
	private PointOfServiceService pointOfServiceService;
	@Resource
	private UserService userService;
	@Resource
	private BaseSiteService baseSiteService;

	private CartModel cartModel;
	private PointOfServiceModel consolidatedPickupPointModel;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();

		// importing test csv
		importCsv("/acceleratorservices/test/testCartConsolidate.impex", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);
		final UserModel ahertz = userService.getUserForUID("ahertz");
		final Collection<CartModel> cartModels = ahertz.getCarts();
		Assert.assertEquals(1, cartModels.size());
		cartModel = cartModels.iterator().next();
		consolidatedPickupPointModel = pointOfServiceService.getPointOfServiceForName("Nakano");
	}

	@Test
	public void shouldConsolidateCheckoutCart() throws CommerceCartModificationException
	{
		assertNotNull("Cart is null", cartModel);
		assertNotNull("Cart entries are null", cartModel.getEntries());
		assertEquals("Cart entries size is wrong", 7, cartModel.getEntries().size());
		assertEquals("Cart entry 0 product is wrong", "TCC-000", cartModel.getEntries().get(0).getProduct().getCode());
		assertEquals("Cart entry 0 quantity is wrong", Long.valueOf(2), cartModel.getEntries().get(0).getQuantity());
		assertNull("Cart entry 0 POS is wrong", cartModel.getEntries().get(0).getDeliveryPointOfService());

		assertEquals("Cart entry 1 product is wrong", "TCC-000", cartModel.getEntries().get(1).getProduct().getCode());
		assertEquals("Cart entry 1 quantity is wrong", Long.valueOf(2), cartModel.getEntries().get(1).getQuantity());
		assertEquals("Cart entry 1 POS is wrong", "Yokosuka", cartModel.getEntries().get(1).getDeliveryPointOfService().getName());

		assertEquals("Cart entry 2 product is wrong", "TCC-001", cartModel.getEntries().get(2).getProduct().getCode());
		assertEquals("Cart entry 2 quantity is wrong", Long.valueOf(4), cartModel.getEntries().get(2).getQuantity());
		assertEquals("Cart entry 2 POS is wrong", "Nakano", cartModel.getEntries().get(2).getDeliveryPointOfService().getName());

		assertEquals("Cart entry 3 product is wrong", "TCC-001", cartModel.getEntries().get(3).getProduct().getCode());
		assertEquals("Cart entry 3 quantity is wrong", Long.valueOf(3), cartModel.getEntries().get(3).getQuantity());
		assertEquals("Cart entry 3 POS is wrong", "Shinbashi", cartModel.getEntries().get(3).getDeliveryPointOfService().getName());

		assertEquals("Cart entry 4 product is wrong", "TCC-001", cartModel.getEntries().get(4).getProduct().getCode());
		assertEquals("Cart entry 4 quantity is wrong", Long.valueOf(6), cartModel.getEntries().get(4).getQuantity());
		assertNull("Cart entry 4 POS is wrong", cartModel.getEntries().get(4).getDeliveryPointOfService());

		assertEquals("Cart entry 5 product is wrong", "TCC-002", cartModel.getEntries().get(5).getProduct().getCode());
		assertEquals("Cart entry 5 quantity is wrong", Long.valueOf(2), cartModel.getEntries().get(5).getQuantity());
		assertEquals("Cart entry 5 POS is wrong", "Koto", cartModel.getEntries().get(5).getDeliveryPointOfService().getName());

		assertEquals("Cart entry 6 product is wrong", "TCC-002", cartModel.getEntries().get(6).getProduct().getCode());
		assertEquals("Cart entry 6 quantity is wrong", Long.valueOf(3), cartModel.getEntries().get(6).getQuantity());
		assertEquals("Cart entry 6 POS is wrong", "Nakano", cartModel.getEntries().get(6).getDeliveryPointOfService().getName());

		final List<CommerceCartModification> result = testAcceleratorCheckoutService.consolidateCheckoutCart(cartModel,
				consolidatedPickupPointModel);

		// verify modification result
		assertNotNull("Modification result is null", result);
		assertEquals("Modification result size is wrong", 2, result.size());
		assertEquals("Cart entry 0 product is wrong", CommerceCartModificationStatus.LOW_STOCK, result.get(0).getStatusCode());
		assertEquals("Cart entry 1 product is wrong", CommerceCartModificationStatus.NO_STOCK, result.get(1).getStatusCode());

		// verify consolidated cart
		assertNotNull("Cart entries are null", cartModel.getEntries());
		assertEquals("Cart entries size is wrong", 4, cartModel.getEntries().size());
		assertEquals("Cart entry 0 product is wrong", "TCC-000", cartModel.getEntries().get(0).getProduct().getCode());
		assertEquals("Cart entry 0 quantity is wrong", Long.valueOf(2), cartModel.getEntries().get(0).getQuantity());
		assertNull("Cart entry 0 POS is wrong", cartModel.getEntries().get(0).getDeliveryPointOfService());

		assertEquals("Cart entry 1 product is wrong", "TCC-001", cartModel.getEntries().get(1).getProduct().getCode());
		assertEquals("Cart entry 1 quantity is wrong", Long.valueOf(5), cartModel.getEntries().get(1).getQuantity());
		assertEquals("Cart entry 1 POS is wrong", "Nakano", cartModel.getEntries().get(1).getDeliveryPointOfService().getName());

		assertEquals("Cart entry 2 product is wrong", "TCC-001", cartModel.getEntries().get(2).getProduct().getCode());
		assertEquals("Cart entry 2 quantity is wrong", Long.valueOf(6), cartModel.getEntries().get(2).getQuantity());
		assertNull("Cart entry 2 POS is wrong", cartModel.getEntries().get(2).getDeliveryPointOfService());

		assertEquals("Cart entry 3 product is wrong", "TCC-002", cartModel.getEntries().get(3).getProduct().getCode());
		assertEquals("Cart entry 3 quantity is wrong", Long.valueOf(5), cartModel.getEntries().get(3).getQuantity());
		assertEquals("Cart entry 3 POS is wrong", "Nakano", cartModel.getEntries().get(3).getDeliveryPointOfService().getName());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckCartIsNotNull() throws CommerceCartModificationException
	{
		testAcceleratorCheckoutService.consolidateCheckoutCart(null, consolidatedPickupPointModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckPointOfServiceIsNotNull() throws CommerceCartModificationException
	{
		testAcceleratorCheckoutService.consolidateCheckoutCart(cartModel, null);
	}
}
