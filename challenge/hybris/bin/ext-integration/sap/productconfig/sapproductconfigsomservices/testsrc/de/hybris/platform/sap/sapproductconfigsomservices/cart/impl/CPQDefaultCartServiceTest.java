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
package de.hybris.platform.sap.sapproductconfigsomservices.cart.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.basket.businessobject.impl.BasketImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemSalesDoc;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.CPQBolCartFacade;
import de.hybris.platform.sap.sapproductconfigsomservices.bolfacade.impl.CPQDefaultBolCartFacade;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */

@UnitTest
public class CPQDefaultCartServiceTest
{
	private CPQDefaultCartService classUnderTest;
	CPQBolCartFacade bolCartFacade = null;
	private Basket cart = null;
	private String productId;
	private final Item newItem = new ItemSalesDoc();
	final long quantity = 1;
	private final String itemKey = "A";
	private final MessageList messageList = new MessageList();
	private SessionService sessionServiceMock;

	/**
	 *
	 */
	@Before
	public void setUp()
	{
		classUnderTest = new CPQDefaultCartService();
		bolCartFacade = EasyMock.createMock(CPQDefaultBolCartFacade.class);
		cart = new BasketImpl();
		productId = "A";
		newItem.setQuantity(new BigDecimal(quantity));
		newItem.setProductId(productId);
		newItem.setTechKey(new TechKey(itemKey));
		final ConfigModel configModel = new ConfigModelImpl();



		cart.setHeader(new HeaderSalesDocument());
		final ItemListImpl itemList = new ItemListImpl();
		itemList.add(newItem);
		cart.setItemList(itemList);

		EasyMock.expect(bolCartFacade.addToCart(productId, quantity)).andReturn(newItem);
		EasyMock.expect(bolCartFacade.validateCart()).andReturn(messageList);
		EasyMock.expect(bolCartFacade.getCart()).andReturn(cart).anyTimes();
		EasyMock.expect(bolCartFacade.hasCart()).andReturn(new Boolean(false));
		EasyMock.expect(bolCartFacade.createCart()).andReturn(cart);
		EasyMock.expect(bolCartFacade.addConfigurationToCart(configModel)).andReturn(itemKey);
		EasyMock.expect(bolCartFacade.updateConfigurationInCart("A", configModel)).andReturn(itemKey);
		bolCartFacade.releaseCart();
		EasyMock.replay(bolCartFacade);
		classUnderTest.setBolCartFacade(bolCartFacade);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapproductconfigsomservices.cart.impl.CPQDefaultCartService#addConfigurationToCart(de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel)}.
	 */
	@Test
	public void testAddConfigurationToCart()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final String itemKey = classUnderTest.addConfigurationToCart(configModel);
		assertNotNull(itemKey);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapproductconfigsomservices.cart.impl.CPQDefaultCartService#updateConfigurationInCart(java.lang.String, de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel)}.
	 */
	@Test
	public void testUpdateConfigurationInCart()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		final String key = "A";
		final String itemKey = classUnderTest.updateConfigurationInCart(key, configModel);
		assertNotNull(itemKey);
	}

}
