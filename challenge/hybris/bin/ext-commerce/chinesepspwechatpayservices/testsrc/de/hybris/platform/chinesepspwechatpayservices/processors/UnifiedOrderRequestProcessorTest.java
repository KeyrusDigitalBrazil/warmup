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
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspwechatpayservices.constants.WeChatPaymentConstants;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.UnifiedOrderRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class UnifiedOrderRequestProcessorTest
{

	private WeChatPayConfiguration config;

	private UnifiedOrderRequestProcessor processor;

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
		config.setMechKey("56339caf2d55495fba8171d9b8823ef2");
		config.setOauthURL("https://open.weixin.qq.com/connect/oauth2/authorize");
		config.setOrderQueryURL("https://api.mch.weixin.qq.com/pay/orderquery");
		config.setUnifiedOrderURL("https://api.mch.weixin.qq.com/pay/unifiedorder");
		config.setTestMode("true");

		final String openId = "12345678910";
		final String clientIp = "127.1.1.1";
		final String baseUrl="https://electronics.local:9002/yacceleratorstorefront";

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		given(product.getName()).willReturn("Product1");
		final OrderEntryModel orderEntry = new OrderEntryModel();
		orderEntry.setProduct(product);
		entries.add(orderEntry);

		given(order.getEntries()).willReturn(entries);
		given(order.getCode()).willReturn("00000001");
		given(order.getTotalPrice()).willReturn(new Double(10));

		processor = new UnifiedOrderRequestProcessor(config, httpClient, openId, order, clientIp,baseUrl);
	}

	@Test
	public void test_Init()
	{
		final Map<String, String> parameters = processor.getParams().getParameters();

		assertEquals("https://api.mch.weixin.qq.com/pay/unifiedorder", processor.getUrl());
		assertEquals("wx901a348feec91234", parameters.get("appid"));
		assertEquals("132193615701", parameters.get("mch_id"));
		assertEquals("WEB", parameters.get("device_info"));
		assertEquals("Product1", parameters.get("body"));
		assertEquals("00000001", parameters.get("out_trade_no"));
		assertEquals("1", parameters.get("total_fee"));
		assertEquals("127.1.1.1", parameters.get("spbill_create_ip"));
		assertEquals("1", parameters.get("total_fee"));
		assertEquals("https://electronics.local:9002/yacceleratorstorefront" + WeChatPaymentConstants.Controller.NOTIFY_URL,
				parameters.get("notify_url"));
		assertEquals("12345678910", parameters.get("openid"));
	}

	@Test
	public void test_Process()
	{
		final String responseXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
				+ "<return_msg><![CDATA[OK]]></return_msg>" + "<appid><![CDATA[wx2421b1c4370ec43b]]></appid>"
				+ "<mch_id><![CDATA[10000100]]></mch_id>" + "<nonce_str><![CDATA[IITRi8Iabbblz1Jc]]></nonce_str>"
				+ "<sign><![CDATA[7921E432F65EB8ED0CE9755F0E86D72F]]></sign>" + "<result_code><![CDATA[SUCCESS]]></result_code>"
				+ "<prepay_id><![CDATA[wx201411101639507cbf6ffd8b0779950874]]></prepay_id>"
				+ "<trade_type><![CDATA[JSAPI]]></trade_type>" + "</xml>";

		given(processor.post()).willReturn(responseXml);

		final String prepayId = processor.process();
		assertEquals("wx201411101639507cbf6ffd8b0779950874", prepayId);
	}



}
