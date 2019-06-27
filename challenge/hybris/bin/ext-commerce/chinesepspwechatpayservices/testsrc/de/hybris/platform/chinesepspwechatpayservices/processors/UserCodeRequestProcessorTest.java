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

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.UserCodeRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class UserCodeRequestProcessorTest
{

	private static final Logger log = Logger.getLogger(UserCodeRequestProcessorTest.class);

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private WeChatPayConfiguration config;

	private UserCodeRequestProcessor processor;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		config = new WeChatPayConfiguration();
		config.setAppId("wx901a348feec9d417");
		config.setAccessTokenURL("https://api.weixin.qq.com/sns/oauth2/access_token");
		config.setAppSecret("381e161cff142a51ba70e554c5b3ae6c");
		config.setMechId("132193615701");
		config.setMechKey("56339caf2d55495fba8171d9b8823ef2");
		config.setOauthURL("https://open.weixin.qq.com/connect/oauth2/authorize");
		config.setOrderQueryURL("https://api.mch.weixin.qq.com/pay/orderquery");
		config.setUnifiedOrderURL("https://api.mch.weixin.qq.com/pay/unifiedorder");

	}

	@Test
	public void test_process()
	{
		given(request.getRequestURL()).willReturn(
				new StringBuffer("https://abc129.cn/yacceleratorstorefront/electronics/en/checkout/multi/wechat/pay/00007001"));

		processor = new UserCodeRequestProcessor(config, request, response);
		processor.process();
		try
		{
			Mockito.verify(response, Mockito.times(1)).sendRedirect(Mockito.anyString());
		}
		catch (final IOException e)
		{
			log.error("Error in UserCodeRequestProcessorTest.test_process()", e);
		}
	}
}
