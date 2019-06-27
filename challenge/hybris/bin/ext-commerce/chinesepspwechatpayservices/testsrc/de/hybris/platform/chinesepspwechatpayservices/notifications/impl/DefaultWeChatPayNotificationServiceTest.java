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
package de.hybris.platform.chinesepspwechatpayservices.notifications.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.order.service.ChineseOrderService;
import de.hybris.platform.chinesepspwechatpayservices.constants.WeChatPaymentConstants;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayOrderDao;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatRawDirectPayNotification;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentTransactionStrategy;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class DefaultWeChatPayNotificationServiceTest
{
	private DefaultWeChatPayNotificationService defaultWeChatPayNotificationService;

	@Mock
	private ModelService modelService;

	@Mock
	private WeChatPayPaymentTransactionStrategy weChatPayPaymentTransactionStrategy;

	@Mock
	private WeChatPayOrderDao weChatPayOrderDao;

	@Mock
	private ChineseOrderService chineseOrderService;

	private OrderModel order;

	private WeChatRawDirectPayNotification weChatpayNotification;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		defaultWeChatPayNotificationService = new DefaultWeChatPayNotificationService();

		defaultWeChatPayNotificationService.setModelService(modelService);
		defaultWeChatPayNotificationService.setChineseOrderService(chineseOrderService);
		defaultWeChatPayNotificationService.setWeChatPayOrderDao(weChatPayOrderDao);
		defaultWeChatPayNotificationService.setWeChatPayPaymentTransactionStrategy(weChatPayPaymentTransactionStrategy);

		weChatpayNotification = new WeChatRawDirectPayNotification();
		weChatpayNotification.setResultCode(WeChatPaymentConstants.Notification.RESULT_SUCCESS);
		weChatpayNotification.setReturnCode(WeChatPaymentConstants.Notification.RETURN_SUCCESS);

		order = new OrderModel();
		order.setCode("00000001");
		order.setTotalPrice((Double.valueOf(1524.62)));
		order.setPaymentStatus(PaymentStatus.NOTPAID);

		Mockito.doReturn(Optional.of(order)).when(weChatPayOrderDao).findOrderByCode(Mockito.any());
		Mockito.doNothing().when(weChatPayPaymentTransactionStrategy).updateForNotification(order, weChatpayNotification);
		Mockito.doNothing().when(modelService).save(Mockito.any());
	}

	@Test
	public void test_Handle_WeChatPay_Payment_Success_Response()
	{
		weChatpayNotification.setResultCode(WeChatPaymentConstants.Notification.RESULT_SUCCESS);
		weChatpayNotification.setReturnCode(WeChatPaymentConstants.Notification.RETURN_SUCCESS);

		defaultWeChatPayNotificationService.handleWeChatPayPaymentResponse(weChatpayNotification);

		assertEquals("PAID", order.getPaymentStatus().toString());
	}

	@Test
	public void test_Handle_WeChatPay_Payment_Failed_Response()
	{
		weChatpayNotification.setResultCode(WeChatPaymentConstants.Notification.RESULT_SUCCESS);
		weChatpayNotification.setReturnCode(WeChatPaymentConstants.Notification.RETURN_FAIL);

		defaultWeChatPayNotificationService.handleWeChatPayPaymentResponse(weChatpayNotification);

		assertEquals("NOTPAID", order.getPaymentStatus().toString());
	}
}
