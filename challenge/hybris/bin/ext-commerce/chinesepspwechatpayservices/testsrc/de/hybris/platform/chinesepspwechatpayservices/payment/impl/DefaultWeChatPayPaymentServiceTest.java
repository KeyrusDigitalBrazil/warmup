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
package de.hybris.platform.chinesepspwechatpayservices.payment.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayOrderDao;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentInfoStrategy;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentTransactionStrategy;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultWeChatPayPaymentServiceTest
{
	private static final String CART_UID = "c000001";
	private static final String PAYMENT_UID = "p000001";
	private static final String ORDER_UID = "o000001";
	private static final String LOGO_URL = "/images/theme/wechatpay.png";
	private static final String REQUEST_URL = "/checkout/multi/wechat/pay/o000001?showwxpaytitle=1";

	private DefaultWeChatPayPaymentService defaultWeChatPayNotificationService;

	@Mock
	private ModelService modelService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private WeChatPayPaymentInfoStrategy weChatPayPaymentInfoStrategy;

	@Mock
	private CommerceCheckoutService commerceCheckoutService;

	@Mock
	private MediaService mediaService;

	@Mock
	private MediaModel mediaModel;

	@Mock
	private WeChatPayOrderDao orderDao;

	@Mock
	private WeChatPayPaymentTransactionStrategy weChatPayPaymentTransactionStrategy;


	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		defaultWeChatPayNotificationService = Mockito.spy(new DefaultWeChatPayPaymentService());

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testHandleAsyncResponse()
	{
		defaultWeChatPayNotificationService.handleAsyncResponse(request, response);
	}


	@Test(expected = UnsupportedOperationException.class)
	public void testHandleSyncResponse()
	{
		defaultWeChatPayNotificationService.handleSyncResponse(request, response);
	}

	@Test
	public void testCancelPayment()
	{
		final boolean isResult = defaultWeChatPayNotificationService.cancelPayment(ORDER_UID);

		assertFalse(isResult);
	}

	@Test
	public void testGetPaymentRequestUrl()
	{
		final String requestUrl = defaultWeChatPayNotificationService.getPaymentRequestUrl(ORDER_UID);

		assertEquals(REQUEST_URL, requestUrl);
	}

	@Test
	public void testSetPaymentInfo()
	{
		final boolean isPayment = false;
		final CartModel cartModel = new CartModel();
		cartModel.setCode(CART_UID);
		final ChinesePaymentInfoModel paymentInfo = new ChinesePaymentInfoModel();
		paymentInfo.setCode(PAYMENT_UID);
		defaultWeChatPayNotificationService.setWeChatPayPaymentInfoStrategy(weChatPayPaymentInfoStrategy);
		Mockito.doReturn(paymentInfo).when(weChatPayPaymentInfoStrategy)
				.updatePaymentInfoForPayemntMethod(Mockito.any());
		defaultWeChatPayNotificationService.setCommerceCheckoutService(commerceCheckoutService);

		Mockito.doReturn(isPayment).when(commerceCheckoutService).setPaymentInfo(Mockito.any());

		defaultWeChatPayNotificationService.setPaymentInfo(cartModel, paymentInfo);

		Mockito.verify(commerceCheckoutService).setPaymentInfo(Mockito.any());
	}

	@Test
	public void testGetPspLogoUrl()
	{
		Mockito.when(mediaModel.getURL()).thenReturn(LOGO_URL);
		Mockito.when(mediaService.getMedia(Mockito.anyString())).thenReturn(mediaModel);

		defaultWeChatPayNotificationService.setMediaService(mediaService);

		final String url = defaultWeChatPayNotificationService.getPspLogoUrl();
		assertEquals(LOGO_URL, url);
	}

	@Test
	public void testUpdatePaymentInfoForPlaceOrder()
	{
		final Optional<OrderModel> orderModel = Optional.of(new OrderModel());
		Mockito.doReturn(orderModel).when(orderDao).findOrderByCode(Mockito.any());

		defaultWeChatPayNotificationService.setWeChatPayPaymentInfoStrategy(weChatPayPaymentInfoStrategy);
		Mockito.doNothing().when(weChatPayPaymentInfoStrategy).updatePaymentInfoForPlaceOrder(orderModel.get());

		defaultWeChatPayNotificationService.setOrderDao(orderDao);
		defaultWeChatPayNotificationService.updatePaymentInfoForPlaceOrder(ORDER_UID);

		Mockito.verify(weChatPayPaymentInfoStrategy).updatePaymentInfoForPlaceOrder(orderModel.get());
	}

	@Test
	public void testCreateTransactionForNewRequest()
	{
		final Optional<OrderModel> orderModel = Optional.of(new OrderModel());
		Mockito.doReturn(orderModel).when(orderDao).findOrderByCode(Mockito.any());

		defaultWeChatPayNotificationService.setWeChatPayPaymentTransactionStrategy(weChatPayPaymentTransactionStrategy);
		Mockito.doNothing().when(weChatPayPaymentTransactionStrategy).createForNewRequest(orderModel.get());

		defaultWeChatPayNotificationService.setOrderDao(orderDao);
		defaultWeChatPayNotificationService.createTransactionForNewRequest(ORDER_UID);

		Mockito.verify(weChatPayPaymentTransactionStrategy).createForNewRequest(orderModel.get());

	}

}
