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
package de.hybris.platform.chinesepspalipayservices.dao.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAlipayPaymentTransactionDaoTest extends ServicelayerTransactionalTest
{
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "alipayPaymentTransactionDao")
	private DefaultAlipayPaymentTransactionDao defaultAlipayPaymentTransactionDao;

	private OrderModel orderModel;

	@Before
	public void prepare()
	{
		CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("USD");

		orderModel = new OrderModel();
		orderModel.setCode("00000001");
		orderModel.setTotalPrice(1.5);
		orderModel.setCurrency(currencyModel);
		orderModel.setDate(new Date());
		orderModel.setUser(userService.getCurrentUser());
	}


	@Test
	public void testFindTransactionByAlipayCode()
	{
		AlipayPaymentTransactionModel alipayPaymentTransaction1 = new AlipayPaymentTransactionModel();
		alipayPaymentTransaction1.setAlipayCode("123456");
		alipayPaymentTransaction1.setRequestId("1000001");

		alipayPaymentTransaction1.setOrder(orderModel);

		modelService.save(orderModel);
		modelService.save(alipayPaymentTransaction1);

		AlipayPaymentTransactionModel fundTransaction = defaultAlipayPaymentTransactionDao.findTransactionByAlipayCode("123456");

		assertEquals("123456", fundTransaction.getAlipayCode());
		assertEquals("1000001", fundTransaction.getRequestId());
		assertEquals("00000001", fundTransaction.getOrder().getCode());
		
		AlipayPaymentTransactionModel fundTransaction2 = defaultAlipayPaymentTransactionDao.findTransactionByAlipayCode("");
		assertEquals(null,fundTransaction2);
	}

	@Test
	public void testFindTransactionByLatestRequestEntry()
	{
		AlipayPaymentTransactionModel transaction1 = new AlipayPaymentTransactionModel();
		transaction1.setRequestId("100000011");
		AlipayPaymentTransactionEntryModel entry1 = new AlipayPaymentTransactionEntryModel();
		entry1.setType(PaymentTransactionType.REQUEST);
		entry1.setPaymentTransaction(transaction1);
		entry1.setTime(new Date());
		entry1.setCode("111111");
		transaction1.setOrder(orderModel);
		modelService.save(entry1);
		modelService.save(transaction1);

		AlipayPaymentTransactionModel transaction2 = new AlipayPaymentTransactionModel();
		transaction2.setRequestId("100000022");
		AlipayPaymentTransactionEntryModel entry2 = new AlipayPaymentTransactionEntryModel();
		entry2.setType(PaymentTransactionType.REQUEST);
		entry2.setPaymentTransaction(transaction2);
		entry2.setTime(new Date());
		entry2.setCode("222222");
		transaction2.setOrder(orderModel);
		modelService.save(entry2);
		modelService.save(transaction2);

		modelService.refresh(orderModel);

		AlipayPaymentTransactionModel fundTransaction = defaultAlipayPaymentTransactionDao
				.findTransactionByLatestRequestEntry(orderModel, true);

		assertEquals("100000022", fundTransaction.getRequestId());
		

	}

}
