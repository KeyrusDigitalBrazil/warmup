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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultWeChatPayPaymentTransactionEntryDaoTest extends ServicelayerTransactionalTest
{
	private static final String PAY_CODE = "p000001";
	private static final String PAYENTRY_CODE1 = "e000001";
	private static final String PAYENTRY_CODE2 = "e000002";
	private static final String WECHATPAY_CODE = "w000001";
	private static final String REQUEST_ID = "r000001";
	private static final String ORDER_CODE = "o000001";
	private static final String ISO_USD = "USD";
	private static final Double TOTAL_PRICE = Double.valueOf(1524.62);

	@Resource(name = "weChatPayOrderDao")
	private DefaultWeChatPayOrderDao weChatPayOrderDao;

	@Resource(name = "userService")
	private UserService userService;

	private OrderModel order;

	private DefaultWeChatPayPaymentTransactionEntryDao weChatPayPaymentTransactionEntryDao;

	private WeChatPayPaymentTransactionModel weChatPayPaymentTransactionModel;

	@Resource
	private ModelService modelService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void prepare()
	{
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
		weChatPayPaymentTransactionEntryModel1.setType(PaymentTransactionType.AUTHORIZATION);

		final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntryModel2 = new WeChatPayPaymentTransactionEntryModel();
		weChatPayPaymentTransactionEntryModel2.setCode(PAYENTRY_CODE2);
		weChatPayPaymentTransactionEntryModel2.setPaymentTransaction(weChatPayPaymentTransactionModel);
		weChatPayPaymentTransactionEntryModel2.setTransactionStatus(TransactionStatus.REJECTED.name());
		weChatPayPaymentTransactionEntryModel2.setType(PaymentTransactionType.REQUEST);


		modelService.save(weChatPayPaymentTransactionModel);
		modelService.save(weChatPayPaymentTransactionEntryModel1);
		modelService.save(weChatPayPaymentTransactionEntryModel2);

		modelService.refresh(weChatPayPaymentTransactionModel);
		modelService.refresh(weChatPayPaymentTransactionEntryModel1);
		modelService.refresh(weChatPayPaymentTransactionEntryModel2);

		weChatPayPaymentTransactionEntryDao = new DefaultWeChatPayPaymentTransactionEntryDao();

	}


	@Test
	public void testFindPaymentTransactionEntryByTypeAndStatus()
	{
		weChatPayPaymentTransactionEntryDao.setFlexibleSearchService(flexibleSearchService);

		final List<WeChatPayPaymentTransactionEntryModel> list = weChatPayPaymentTransactionEntryDao
				.findPaymentTransactionEntryByTypeAndStatus(PaymentTransactionType.AUTHORIZATION, TransactionStatus.ACCEPTED,
						weChatPayPaymentTransactionModel);

		final WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntry = list.get(0);

		assertEquals(1, list.size());
		assertEquals(PAYENTRY_CODE1, weChatPayPaymentTransactionEntry.getCode());
		assertEquals(TransactionStatus.ACCEPTED.name(), weChatPayPaymentTransactionEntry.getTransactionStatus());

	}

}
