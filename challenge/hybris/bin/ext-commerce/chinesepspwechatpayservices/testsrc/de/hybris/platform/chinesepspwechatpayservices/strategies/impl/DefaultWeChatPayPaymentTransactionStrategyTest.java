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
package de.hybris.platform.chinesepspwechatpayservices.strategies.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.chinesepspwechatpayservices.constants.WeChatPaymentConstants;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayPaymentTransactionDao;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayPaymentTransactionEntryDao;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatPayQueryResult;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatRawDirectPayNotification;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@IntegrationTest
public class DefaultWeChatPayPaymentTransactionStrategyTest extends ServicelayerTransactionalTest
{
	@Resource(name = "weChatPayPaymentTransactionStrategy")
	private DefaultWeChatPayPaymentTransactionStrategy defaultWeChatPayPaymentTransactionStrategy;

	private OrderModel order;

	private CurrencyModel currencyModel;

	private WeChatRawDirectPayNotification weChatPayNotifyResponseData;

	private WeChatPayQueryResult weChatPayQueryResult;

	private WeChatPayPaymentTransactionModel weChatPayPaymentTransactionModel;

	private WeChatPayPaymentTransactionEntryModel weChatPayPaymentTransactionEntryModel;

	@Resource(name = "weChatPayPaymentTransactionEntryDao")
	private WeChatPayPaymentTransactionEntryDao weChatPayPaymentTransactionEntryDao;

	@Resource(name = "weChatPayPaymentTransactionDao")
	private WeChatPayPaymentTransactionDao weChatPayPaymentTransactionDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Mock
	private KeyGenerator paymentTransactionKeyGenerator;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		defaultWeChatPayPaymentTransactionStrategy = new DefaultWeChatPayPaymentTransactionStrategy();
		defaultWeChatPayPaymentTransactionStrategy.setModelService(modelService);
		defaultWeChatPayPaymentTransactionStrategy.setPaymentTransactionKeyGenerator(paymentTransactionKeyGenerator);
		defaultWeChatPayPaymentTransactionStrategy.setWeChatPayPaymentTransactionDao(weChatPayPaymentTransactionDao);
		defaultWeChatPayPaymentTransactionStrategy.setWeChatPayPaymentTransactionEntryDao(weChatPayPaymentTransactionEntryDao);

		weChatPayPaymentTransactionModel = new WeChatPayPaymentTransactionModel();
		weChatPayPaymentTransactionModel.setCode("000001");
		weChatPayPaymentTransactionModel.setOrder(order);
		weChatPayPaymentTransactionModel.setWeChatPayCode("000111");
		weChatPayPaymentTransactionModel.setRequestId("000011");

		weChatPayPaymentTransactionEntryModel = new WeChatPayPaymentTransactionEntryModel();
		weChatPayPaymentTransactionEntryModel.setPaymentTransaction(weChatPayPaymentTransactionModel);
		weChatPayPaymentTransactionEntryModel.setType(PaymentTransactionType.WECHAT_REQUEST);
		weChatPayPaymentTransactionEntryModel.setCode(String.valueOf(paymentTransactionKeyGenerator.generate()) + "44");

		final List<PaymentTransactionModel> paymentTransactionModels = new ArrayList<>();
		paymentTransactionModels.add(weChatPayPaymentTransactionModel);

		currencyModel = new CurrencyModel();
		currencyModel.setIsocode("USD");

		order = new OrderModel();
		order.setCode("00000001");
		order.setTotalPrice((Double.valueOf(1524.62)));
		order.setCurrency(currencyModel);
		order.setDate(new Date());
		order.setUser(userService.getCurrentUser());
		order.setPaymentTransactions(paymentTransactionModels);

		weChatPayNotifyResponseData = new WeChatRawDirectPayNotification();
		weChatPayNotifyResponseData.setOutTradeNo("00000001");
		weChatPayNotifyResponseData.setOpenid("openid000001");
		weChatPayNotifyResponseData.setCouponFee(9542);
		weChatPayNotifyResponseData.setTotalFee(152462);
		weChatPayNotifyResponseData.setTransactionId("111111");

		weChatPayQueryResult = new WeChatPayQueryResult();
		weChatPayQueryResult.setOutTradeNo("00000001");
		weChatPayQueryResult.setOpenid("openid000001");
		weChatPayQueryResult.setCouponFee(9542);
		weChatPayQueryResult.setTotalFee(152462);
		weChatPayQueryResult.setTransactionId("111111");

		modelService.save(weChatPayPaymentTransactionEntryModel);
		modelService.save(weChatPayPaymentTransactionModel);
		modelService.save(order);
		modelService.refresh(weChatPayPaymentTransactionEntryModel);
		modelService.refresh(weChatPayPaymentTransactionModel);
		modelService.refresh(order);

		Mockito.doReturn("001111").when(paymentTransactionKeyGenerator).generate();
	}

	@Test
	public void test_Update_For_Successful_Notification()
	{
		weChatPayNotifyResponseData.setResultCode(WeChatPaymentConstants.Notification.RESULT_SUCCESS);

		Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(order, TransactionStatus.ACCEPTED);
		assertFalse(weChatPayTransaction.isPresent());

		defaultWeChatPayPaymentTransactionStrategy.updateForNotification(order, weChatPayNotifyResponseData);

		weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(order,
				TransactionStatus.ACCEPTED);
		assertTrue(weChatPayTransaction.isPresent());
	}

	@Test
	public void test_Update_For_Failed_Notification()
	{
		weChatPayNotifyResponseData.setResultCode(WeChatPaymentConstants.Notification.RESULT_FAIL);

		Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(order, TransactionStatus.REJECTED);
		assertFalse(weChatPayTransaction.isPresent());

		defaultWeChatPayPaymentTransactionStrategy.updateForNotification(order, weChatPayNotifyResponseData);

		weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(order,
				TransactionStatus.REJECTED);
		assertTrue(weChatPayTransaction.isPresent());
	}

	@Test
	public void test_Save_For_Status_Check_With_Succuss_Result()
	{
		weChatPayQueryResult.setTradeState("SUCCESS");
		weChatPayQueryResult.setTradeStateDesc("SUCCESS");

		Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(order, TransactionStatus.ACCEPTED);
		assertFalse(weChatPayTransaction.isPresent());

		defaultWeChatPayPaymentTransactionStrategy.saveForStatusCheck(order, weChatPayQueryResult);

		weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(order,
				TransactionStatus.ACCEPTED);
		assertTrue(weChatPayTransaction.isPresent());
	}

	@Test
	public void test_Save_For_Status_Check_With_Closed_Result()
	{
		weChatPayQueryResult.setTradeState("CLOSED");
		weChatPayQueryResult.setTradeStateDesc("CLOSED");

		Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(order, TransactionStatus.REJECTED);
		assertFalse(weChatPayTransaction.isPresent());

		defaultWeChatPayPaymentTransactionStrategy.saveForStatusCheck(order, weChatPayQueryResult);

		weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(order,
				TransactionStatus.REJECTED);
		assertTrue(weChatPayTransaction.isPresent());
	}

	@Test
	public void test_Save_For_Status_Check_With_Payerror_Result()
	{
		weChatPayQueryResult.setTradeState("PAYERROR");
		weChatPayQueryResult.setTradeStateDesc("PAYERROR");

		Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy
				.getPaymentTransactionWithCaptureEntry(order, TransactionStatus.ERROR);
		assertFalse(weChatPayTransaction.isPresent());

		defaultWeChatPayPaymentTransactionStrategy.saveForStatusCheck(order, weChatPayQueryResult);

		weChatPayTransaction = defaultWeChatPayPaymentTransactionStrategy.getPaymentTransactionWithCaptureEntry(order,
				TransactionStatus.ERROR);
		assertTrue(weChatPayTransaction.isPresent());
	}

	@Test
	public void test_create_Transacion_ForNew_Request()
	{

		final OrderModel newOrder = new OrderModel();
		newOrder.setCode("00000002");
		newOrder.setTotalPrice((Double.valueOf(1524.62)));
		newOrder.setCurrency(currencyModel);
		newOrder.setDate(new Date());
		newOrder.setUser(userService.getCurrentUser());

		defaultWeChatPayPaymentTransactionStrategy.createForNewRequest(newOrder);
		final Optional<WeChatPayPaymentTransactionModel> weChatPayTransaction = weChatPayPaymentTransactionDao
				.findTransactionByLatestRequestEntry(newOrder, true);
		assertTrue(weChatPayTransaction.isPresent());


		final List<WeChatPayPaymentTransactionEntryModel> weChatTransactionEntries = weChatPayPaymentTransactionEntryDao
				.findPaymentTransactionEntryByTypeAndStatus(PaymentTransactionType.WECHAT_REQUEST, TransactionStatus.ACCEPTED,
						weChatPayTransaction.get());

		assertTrue(weChatTransactionEntries.size() > 0);
	}
}
