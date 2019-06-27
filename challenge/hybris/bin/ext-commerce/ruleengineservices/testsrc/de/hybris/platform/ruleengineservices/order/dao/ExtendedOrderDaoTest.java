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
package de.hybris.platform.ruleengineservices.order.dao;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class ExtendedOrderDaoTest extends ServicelayerTransactionalTest
{
	private static final String CODE = "testCode";
	private static final String VERSION_ID = "1";

	@Resource(name = "extendedOrderDao")
	private ExtendedOrderDao extendedOrderDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	private CurrencyModel currencyModel;
	private UserModel userModel;

	@Before
	public void setup()
	{
		currencyModel = createCurrency();
		userModel = new UserModel();
		userModel.setUid("myUID");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindOrderByNullCode()
	{
		extendedOrderDao.findOrderByCode(null);
	}

	@Test(expected = ModelNotFoundException.class)
	public void testFindOrderByEmptyCode()
	{
		extendedOrderDao.findOrderByCode("");
	}

	@Test(expected = ModelNotFoundException.class)
	public void testNoOrdersByCode()
	{
		extendedOrderDao.findOrderByCode("nonexistent");
	}

	@Test
	public void testFindOrderByCode()
	{
		createOrder(CODE);
		assertTrue(extendedOrderDao.findOrderByCode(CODE) instanceof OrderModel);
		assertEquals(CODE, extendedOrderDao.findOrderByCode(CODE).getCode());
	}

	@Test
	public void testFindCartByCode()
	{
		createCart(CODE);
		assertTrue(extendedOrderDao.findOrderByCode(CODE) instanceof CartModel);
		assertEquals(CODE, extendedOrderDao.findOrderByCode(CODE).getCode());
	}

	@Test
	public void testFindOrderByCodeFilteringSnapshots()
	{
		//given an order and a snapshot of the order
		createOrder(CODE);
		createOrder(CODE, VERSION_ID);

		//when
		final OrderModel result = (OrderModel) extendedOrderDao.findOrderByCode(CODE);

		//then verify that the retrieved object is not a snapshot
		assertEquals(CODE, result.getCode());
		assertNull(result.getVersionID());
	}


	private void createOrder(final String code)
	{
		createOrder(code, null);
	}

	private void createOrder(final String code, final String versionId)
	{
		final OrderModel order = new OrderModel();
		order.setCode(code);
		order.setCurrency(currencyModel);
		order.setDate(new Date());
		order.setVersionID(versionId);
		order.setUser(userModel);
		modelService.save(order);
	}

	private void createCart(final String code)
	{
		final CartModel cart = new CartModel();
		cart.setCode(code);
		cart.setCurrency(currencyModel);
		cart.setDate(new Date());
		cart.setUser(userModel);
		modelService.save(cart);
	}

	private CurrencyModel createCurrency()
	{
		final CurrencyModel currency = modelService.create(CurrencyModel.class);
		currency.setActive(Boolean.TRUE);
		currency.setIsocode("MCURR");
		currency.setName("myCurrency");
		currency.setSymbol("mc");
		currency.setConversion(Double.valueOf(1.3));
		modelService.save(currency);
		return currency;
	}
}
