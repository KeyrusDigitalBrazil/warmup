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
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.AlipayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.AlipayPaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultAlipayPaymentTransactionEntryDaoTest extends ServicelayerTransactionalTest
{
	@Resource(name = "alipayPaymentTransactionEntryDao")
	private DefaultAlipayPaymentTransactionEntryDao defaultAlipayPaymentTransactionEntryDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	private OrderModel orderModel;

	@Before
	public void prepare()
	{
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("USD");

		orderModel = new OrderModel();
		orderModel.setCode("00000001");
		orderModel.setTotalPrice(1.5);
		orderModel.setCurrency(currencyModel);
		orderModel.setDate(new Date());
		orderModel.setUser(userService.getCurrentUser());
	}

	@Test
	public void testFindPaymentTransactionEntryByTypeAndStatus()
	{
		final AlipayPaymentTransactionModel transaction1 = new AlipayPaymentTransactionModel();
		transaction1.setRequestId("100000011");
		final AlipayPaymentTransactionEntryModel entry1 = new AlipayPaymentTransactionEntryModel();
		entry1.setType(PaymentTransactionType.REQUEST);
		entry1.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry1.setPaymentTransaction(transaction1);
		entry1.setTime(new Date());
		entry1.setCode("111111");
		transaction1.setOrder(orderModel);
		modelService.save(entry1);
		modelService.save(transaction1);
		modelService.refresh(orderModel);

		final List<AlipayPaymentTransactionEntryModel> fundEntries = defaultAlipayPaymentTransactionEntryDao
				.findPaymentTransactionEntryByTypeAndStatus(PaymentTransactionType.REQUEST, TransactionStatus.ACCEPTED, transaction1);

		assertEquals(1, fundEntries.size());
		assertEquals("111111", fundEntries.get(0).getCode());
	}

	@Test
	public void testFindPaymentTransactionEntryByTypeAndStatusNull()
	{
		final AlipayPaymentTransactionModel transaction = new AlipayPaymentTransactionModel();
		transaction.setRequestId("100000011");
		final AlipayPaymentTransactionEntryModel entry = new AlipayPaymentTransactionEntryModel();
		entry.setType(PaymentTransactionType.CANCEL);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setPaymentTransaction(transaction);
		entry.setTime(new Date());
		entry.setCode("111111");
		transaction.setOrder(orderModel);
		modelService.save(entry);
		modelService.save(transaction);
		modelService.refresh(orderModel);


		final List<AlipayPaymentTransactionEntryModel> fundEntries = defaultAlipayPaymentTransactionEntryDao
				.findPaymentTransactionEntryByTypeAndStatus(PaymentTransactionType.REQUEST, TransactionStatus.ACCEPTED, transaction);

		assertEquals(0, fundEntries.size());
	}

}
