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
package de.hybris.platform.chinesepspwechatpayservices.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultWeChatPayOrderDaoTest extends ServicelayerTransactionalTest
{
	private static final String ORDER_CODE = "o000001";
	private static final String ISO_USD = "USD";
	private static final Double TOTAL_PRICE = Double.valueOf(1524.62);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "weChatPayOrderDao")
	private DefaultWeChatPayOrderDao weChatPayOrderDao;

	@Resource(name = "userService")
	private UserService userService;

	@Before
	public void setUp()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(ISO_USD);

		final OrderModel order = new OrderModel();
		order.setCode(ORDER_CODE);
		order.setTotalPrice(TOTAL_PRICE);
		order.setCurrency(currencyModel);
		order.setDate(new Date());
		order.setUser(userService.getCurrentUser());

		modelService.save(order);

	}

	@Test
	public void testFindOrderByCode()
	{
		final Optional<OrderModel> order = weChatPayOrderDao.findOrderByCode(ORDER_CODE);

		assertTrue(order.isPresent());
		assertEquals(ORDER_CODE, order.get().getCode());
	}
}
