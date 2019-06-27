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

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.SignVerificationProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class SignVerificationProcessorTest
{
	private WeChatPayConfiguration config;

	private SignVerificationProcessor processor;

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

		final Map<String, String> params = new HashMap<>();
		params.put("return_code", "SUCCESS");
		params.put("mch_id", "132193615701");
		params.put("result_code", "SUCCESS");
		params.put("total_fee", "1");
		params.put("cash_fee", "1");
		params.put("transaction_id", "1217752501201407033233368018");
		params.put("out_trade_no", "0000001");
		params.put("sign", "1FAE63F6431A13EB43580D14759FAE4E");

		processor = new SignVerificationProcessor(config, params);
	}

	@Test
	public void test_Process()
	{
		assertTrue(processor.process().booleanValue());
	}

}
