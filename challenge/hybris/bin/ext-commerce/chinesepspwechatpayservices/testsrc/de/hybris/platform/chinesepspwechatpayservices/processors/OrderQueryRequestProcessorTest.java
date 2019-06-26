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
package de.hybris.platform.chinesepspwechatpayservices.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatPayQueryResult;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.OrderQueryRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class OrderQueryRequestProcessorTest
{
	private WeChatPayConfiguration config;

	private OrderQueryRequestProcessor processor;

	@Mock
	private WeChatPayHttpClient httpClient;

	@Mock
	private OrderModel order;

	@Mock
	private ProductModel product;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		config = new WeChatPayConfiguration();
		config.setAppId("wx901a348feec91234");
		config.setAccessTokenURL("https://api.weixin.qq.com/sns/oauth2/access_token");
		config.setAppSecret("381e161cff142a51ba70e554c5b3ae6c");
		config.setMechId("132193615701");
		config.setMechKey("56339cae2d537477fbj8171d9e5823ef2");
		config.setOauthURL("https://open.weixin.qq.com/connect/oauth2/authorize");
		config.setOrderQueryURL("https://api.mch.weixin.qq.com/pay/orderquery");
		config.setUnifiedOrderURL("https://api.mch.weixin.qq.com/pay/unifiedorder");
		config.setTestMode("true");


		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		given(product.getName()).willReturn("Product1");
		final OrderEntryModel orderEntry = new OrderEntryModel();
		orderEntry.setProduct(product);
		entries.add(orderEntry);

		given(order.getEntries()).willReturn(entries);
		given(order.getCode()).willReturn("00000001");
		given(order.getTotalPrice()).willReturn(new Double(10));

		processor = new OrderQueryRequestProcessor(httpClient, order.getCode(), config);
	}

	@Test
	public void test_Process()
	{
		final String responseXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
				+ "<return_msg><![CDATA[OK]]></return_msg>" + "<appid><![CDATA[wx2421b1c4370ec43b]]></appid>"
				+ "<mch_id><![CDATA[10000100]]></mch_id>" + "<nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>"
				+ "<sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>" + "<result_code><![CDATA[SUCCESS]]></result_code>"
				+ "<prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>"
				+ "<trade_type><![CDATA[JSAPI]]></trade_type>" + "<total_fee>1</total_fee>"
				+ "<transaction_id><![CDATA[4004052001201605266346333136]]></transaction_id>"
				+ "<out_trade_no><![CDATA[00040002]]></out_trade_no>" + "<attach><![CDATA[]]></attach>"
				+ "<time_end><![CDATA[20160526211618]]></time_end>" + "<trade_state><![CDATA[SUCCESS]]></trade_state>"
				+ "<cash_fee>1</cash_fee>" + "</xml>";

		given(processor.post()).willReturn(responseXml);

		final Optional<WeChatPayQueryResult> processResult = processor.process();
		assertTrue(processResult.isPresent());
		final WeChatPayQueryResult weChatPayQueryResult = processResult.get();
		assertEquals("SUCCESS", weChatPayQueryResult.getReturnCode());
		assertEquals("SUCCESS", weChatPayQueryResult.getResultCode());
		assertEquals("SUCCESS", weChatPayQueryResult.getTradeState());
		assertEquals("4004052001201605266346333136", weChatPayQueryResult.getTransactionId());
		assertEquals(1, weChatPayQueryResult.getTotalFee(), 0);
		assertEquals(1, weChatPayQueryResult.getCashFee(), 0);

	}

}
