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
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.testframework.HybrisJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(HybrisJUnit4ClassRunner.class)
@IntegrationTest
public class DefaultB2BCartFacadeIntegrationTest extends BaseCommerceBaseTest
{
	@Resource
	private B2BCartService b2bCartService;
	@Resource
	private CartFacade b2bCartFacade;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/b2bacceleratorfacades/test/testOrganizations.csv", "utf-8");
		importCsv("/b2bacceleratorfacades/test/testB2BCommerceCart.csv", "utf-8");

		final CartModel modelByExample = new CartModel();
		modelByExample.setCode("dc_shhCart_b2baf");
		final CartModel cart = flexibleSearchService.getModelByExample(modelByExample);

		b2bCartService.setSessionCart(cart);
	}

	@Test
	public void testAddOrderEntry() throws Exception
	{
		Assert.assertNotNull("cart not null", b2bCartService.getSessionCart());
		Assert.assertNotNull("user not null", b2bCartService.getSessionCart().getUser());
		Assert.assertEquals("DC S HH", b2bCartService.getSessionCart().getUser().getUid());

		final OrderEntryData orderEntry = new OrderEntryData();
		orderEntry.setQuantity((long) 5);
		orderEntry.setProduct(new ProductData());
		orderEntry.getProduct().setCode("HW1210-3422");
		orderEntry.setEntryNumber(null);

		final CartModificationData cartModificationData = b2bCartFacade.addOrderEntry(orderEntry);
		assertNotNull(cartModificationData);
		assertNotNull(cartModificationData.getStatusCode());
		assertEquals("dc_shhCart_b2baf", cartModificationData.getCartCode());
		assertEquals("success", cartModificationData.getStatusCode());

	}

	@Test
	public void testUpdateOrderEntry() throws Exception
	{
		Assert.assertNotNull("cart not null", b2bCartService.getSessionCart());
		Assert.assertNotNull("user not null", b2bCartService.getSessionCart().getUser());
		Assert.assertEquals("DC S HH", b2bCartService.getSessionCart().getUser().getUid());

		final OrderEntryData orderEntry = new OrderEntryData();
		orderEntry.setQuantity((long) 100);
		orderEntry.setProduct(new ProductData());
		orderEntry.getProduct().setCode("HW1210-3422");
		orderEntry.setEntryNumber(10);

		final CartModificationData cartModificationData = b2bCartFacade.updateOrderEntry(orderEntry);
		assertNotNull(cartModificationData);
		assertNotNull(cartModificationData.getStatusCode());
		assertEquals("dc_shhCart_b2baf", cartModificationData.getCartCode());
		assertEquals("success", cartModificationData.getStatusCode());

	}

	@Test
	public void testAddOrderEntryList() throws Exception
	{
		Assert.assertNotNull("cart not null", b2bCartService.getSessionCart());
		Assert.assertNotNull("user not null", b2bCartService.getSessionCart().getUser());
		Assert.assertEquals("DC S HH", b2bCartService.getSessionCart().getUser().getUid());

		final List<OrderEntryData> cartEntries = new ArrayList<OrderEntryData>();

		final OrderEntryData orderEntry1 = new OrderEntryData();
		orderEntry1.setQuantity((long) 10);
		orderEntry1.setProduct(new ProductData());
		orderEntry1.getProduct().setCode("HW1210-3422");
		orderEntry1.setEntryNumber(null);

		final OrderEntryData orderEntry2 = new OrderEntryData();
		orderEntry2.setQuantity((long) 20);
		orderEntry2.setProduct(new ProductData());
		orderEntry2.getProduct().setCode("HW1210-3423");
		orderEntry2.setEntryNumber(null);

		cartEntries.add(orderEntry1);
		cartEntries.add(orderEntry2);

		final List<CartModificationData> cartModificationData = b2bCartFacade.addOrderEntryList(cartEntries);
		for (final CartModificationData tempData : cartModificationData)
		{
			assertNotNull(tempData);
			assertNotNull(tempData.getStatusCode());
			assertEquals("dc_shhCart_b2baf", tempData.getCartCode());
			assertEquals("success", tempData.getStatusCode());
		}
	}

	@Test
	public void testUpdateOrderEntryList() throws Exception
	{
		Assert.assertNotNull("cart not null", b2bCartService.getSessionCart());
		Assert.assertNotNull("user not null", b2bCartService.getSessionCart().getUser());
		Assert.assertEquals("DC S HH", b2bCartService.getSessionCart().getUser().getUid());

		final OrderEntryData orderEntry1 = new OrderEntryData();
		orderEntry1.setQuantity((long) 100);
		orderEntry1.setProduct(new ProductData());
		orderEntry1.getProduct().setCode("HW1210-3422");
		orderEntry1.setEntryNumber(10);

		final OrderEntryData orderEntry2 = new OrderEntryData();
		orderEntry2.setQuantity((long) 20);
		orderEntry2.setProduct(new ProductData());
		orderEntry2.getProduct().setCode("HW1210-3423");
		orderEntry2.setEntryNumber(2);

		final List<OrderEntryData> cartEntries = new ArrayList<OrderEntryData>();

		cartEntries.add(orderEntry1);
		cartEntries.add(orderEntry2);

		final List<CartModificationData> cartModificationData = b2bCartFacade.updateOrderEntryList(cartEntries);
		for (final CartModificationData tempData : cartModificationData)
		{
			assertNotNull(tempData);
			assertNotNull(tempData.getStatusCode());
			assertEquals("dc_shhCart_b2baf", tempData.getCartCode());
			assertEquals("success", tempData.getStatusCode());
		}

	}
}