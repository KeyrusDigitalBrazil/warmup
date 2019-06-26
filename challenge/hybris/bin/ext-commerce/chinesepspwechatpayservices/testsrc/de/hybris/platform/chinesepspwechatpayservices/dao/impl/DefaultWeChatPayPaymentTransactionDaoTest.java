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
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultWeChatPayPaymentTransactionDaoTest extends ServicelayerTransactionalTest
{
	private static final String WECHAT_CODE = "o000001";
	private static final String WECHATPAY_CODE = "w000001";
	private static final String PAY_CODE = "p000001";
	private static final String PAYENTRY_CODE1 = "e000001";
	private static final String PAYENTRY_CODE2 = "e000002";
	private static final String PAYENTRY_CODE3 = "e000003";
	private static final String REQUEST_ID = "r000001";
	private static final String ORDER_CODE = "o000001";
	private static final String ISO_USD = "USD";
	private static final Double TOTAL_PRICE = Double.valueOf(1524.62);
	private static final Boolean LIMIT = true;

	private OrderModel order;

	private DefaultWeChatPayPaymentTransactionEntryDao weChatPayPaymentTransactionEntryDao;

	private WeChatPayPaymentTransactionModel weChatPayPaymentTransactionModel;

	private Optional<WeChatPayPaymentTransactionModel> weChatPayPaymentTransaction;

	@Resource(name = "weChatPayPaymentTransactionDao")
	private DefaultWeChatPayPaymentTransactionDao weChatPayPaymentTransactionDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Before
	public void setUp()
	{
		final WeChatPayPaymentTransactionModel weChatPayPaymentTransaction = new WeChatPayPaymentTransactionModel();
		weChatPayPaymentTransaction.setCode(WECHAT_CODE);
		weChatPayPaymentTransaction.setWeChatPayCode(WECHATPAY_CODE);

		modelService.save(weChatPayPaymentTransaction);

		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode(ISO_USD);

		order = new OrderModel();
		order.setCode(ORDER_CODE);
		order.setTotalPrice(TOTAL_PRICE);
		order.setCurrency(currencyModel);
		order.setDate(new Date());
		order.setUser(userService.getCurrentUser());

		weChatPayPaymentTransactionModel = new WeChatPayPaymentTransactionModel();
		weChatPayPaymentTransactionModel.setCode(PAY_CODE);
		weChatPayPaymentTransactionModel.setOrder(order);
		weChatPayPaymentTransactionModel.setWeChatPayCode(WECHATPAY_CODE);
		weChatPayPaymentTransactionModel.setRequestId(REQUEST_ID);

		final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntryModel1 = new WeChatPayPaymentTransactionEntryModel();
		weChatPayPaymentTransactionEntryModel1.setCode(PAYENTRY_CODE1);
		weChatPayPaymentTransactionEntryModel1.setPaymentTransaction(weChatPayPaymentTransactionModel);
		weChatPayPaymentTransactionEntryModel1.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		weChatPayPaymentTransactionEntryModel1.setType(PaymentTransactionType.REQUEST);
		weChatPayPaymentTransactionEntryModel1.setTime(new Date());

		final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntryModel2 = new WeChatPayPaymentTransactionEntryModel();
		weChatPayPaymentTransactionEntryModel2.setCode(PAYENTRY_CODE2);
		weChatPayPaymentTransactionEntryModel2.setPaymentTransaction(weChatPayPaymentTransactionModel);
		weChatPayPaymentTransactionEntryModel2.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		weChatPayPaymentTransactionEntryModel2.setType(PaymentTransactionType.WECHAT_REQUEST);
		weChatPayPaymentTransactionEntryModel2.setTime(new Date());

		final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntryModel3 = new WeChatPayPaymentTransactionEntryModel();
		weChatPayPaymentTransactionEntryModel3.setCode(PAYENTRY_CODE3);
		weChatPayPaymentTransactionEntryModel3.setPaymentTransaction(weChatPayPaymentTransactionModel);
		weChatPayPaymentTransactionEntryModel3.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		weChatPayPaymentTransactionEntryModel3.setType(PaymentTransactionType.WECHAT_REQUEST);
		weChatPayPaymentTransactionEntryModel3.setTime(new Date());


		modelService.save(weChatPayPaymentTransactionModel);
		modelService.save(weChatPayPaymentTransactionEntryModel1);
		modelService.save(weChatPayPaymentTransactionEntryModel2);
		modelService.save(weChatPayPaymentTransactionEntryModel3);

		modelService.refresh(weChatPayPaymentTransactionModel);
		modelService.refresh(weChatPayPaymentTransactionEntryModel1);
		modelService.refresh(weChatPayPaymentTransactionEntryModel2);
		modelService.refresh(weChatPayPaymentTransactionEntryModel3);

		weChatPayPaymentTransactionEntryDao = new DefaultWeChatPayPaymentTransactionEntryDao();

	}

	@Test
	public void testFindTransactionByLatestRequestEntry()
	{
		final Optional<WeChatPayPaymentTransactionModel> weChatPayPaymentTransactionModel = weChatPayPaymentTransactionDao
				.findTransactionByLatestRequestEntry(order, !LIMIT);

		assertEquals(PAY_CODE, weChatPayPaymentTransactionModel.get().getCode());
		assertTrue(weChatPayPaymentTransactionModel.isPresent());
	}

	@Test
	public void testFindTransactionByWeChatPayCode()
	{
		weChatPayPaymentTransaction = weChatPayPaymentTransactionDao.findTransactionByWeChatPayCode(WECHATPAY_CODE);

		assertEquals(WECHATPAY_CODE, weChatPayPaymentTransaction.get().getWeChatPayCode());
		assertTrue(weChatPayPaymentTransaction.isPresent());
	}
}
