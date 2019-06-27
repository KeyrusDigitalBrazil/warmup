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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.factory.impl.DefaultItemFactory;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
@SuppressWarnings(
{ "javadoc", "unchecked" })
public class DefaultCartRestorationServiceTest
{
	private final DefaultCartRestorationService classUnderTest = new DefaultCartRestorationService();
	private SessionService sessionServiceMock;
	private CartService cartServiceMock;

	@Before
	public void init()
	{
		classUnderTest.setItemFactory(new DefaultItemFactory());
	}

	@Test
	public void testRestoreCartForProductsWithoutConfiguration()
	{


		final CartModel fromCart = getCartModel();
		prepareMocksForRestoreCart(fromCart);

		final CartRestorationData restData = classUnderTest.restoreCart(fromCart);
		assertTrue(restData != null);
		EasyMock.verify(sessionServiceMock);
	}

	@Test
	public void testCreateItemsFromCart()
	{
		final CartModel fromCart = getCartModel();
		final List<Item> items = classUnderTest.createItemsFromCart(fromCart);
		assertTrue(items.size() == 2);
	}


	private void prepareMocksForRestoreCart(final CartModel fromCart)
	{

		sessionServiceMock = EasyMock.createMock(SessionService.class);
		sessionServiceMock.setAttribute(DefaultCartRestorationService.SESSION_CART_PARAMETER_NAME, fromCart);
		classUnderTest.setSessionService(sessionServiceMock);

		cartServiceMock = EasyMock.createMock(CartService.class);

		cartServiceMock.addItemsToCart(EasyMock.isA(ArrayList.class));

		EasyMock.expectLastCall();

		classUnderTest.setCartService(cartServiceMock);


		EasyMock.replay(sessionServiceMock);

	}

	/**
	 * @return
	 */
	private CartModel getCartModel()
	{
		final CartModel fromCart = new CartModel();
		final List<AbstractOrderEntryModel> listEntry = new ArrayList<AbstractOrderEntryModel>();

		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		ProductModel pModel = new ProductModel();
		pModel.setSapConfigurable(Boolean.FALSE);
		pModel.setCode("KAP");
		entry1.setProduct(pModel);
		entry1.setQuantity(new Long(2));
		entry1.setExternalConfiguration(null);
		listEntry.add(entry1);



		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		pModel = new ProductModel();
		pModel.setCode("KAC");
		pModel.setSapConfigurable(Boolean.TRUE);
		entry2.setProduct(pModel);
		entry2.setQuantity(new Long(3));
		entry2.setExternalConfiguration(null);
		listEntry.add(entry2);

		fromCart.setEntries(listEntry);
		return fromCart;
	}

}
