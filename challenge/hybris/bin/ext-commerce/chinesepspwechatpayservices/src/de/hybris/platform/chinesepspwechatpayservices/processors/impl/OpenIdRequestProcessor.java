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
package de.hybris.platform.chinesepspwechatpayservices.processors.impl;

import de.hybris.platform.chinesepspwechatpayservices.exception.WeChatPayException;
import de.hybris.platform.chinesepspwechatpayservices.processors.AbstractWeChatPayRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;

import java.util.Map;

import groovy.json.JsonSlurper;


/**
 * Processor for fetching openid.
 */
public class OpenIdRequestProcessor extends AbstractWeChatPayRequestProcessor<String>
{
	public OpenIdRequestProcessor(final WeChatPayConfiguration config, final WeChatPayHttpClient httpClient, final String code)
	{
		super(config, httpClient);
		super.setUrl(config.getAccessTokenURL());
		super.addParameter("secret", config.getAppSecret());
		super.addParameter("code", code);
		super.addParameter("grant_type", "authorization_code");
	}

	@Override
	public String process()
	{
		final JsonSlurper slurper = new JsonSlurper();
		final Map<String, Object> map = (Map<String, Object>) slurper.parseText(get());
		final Object openId = map.get("openid");
		if (openId == null)
		{
			throw new WeChatPayException("Get openid error!");
		}
		else
		{
			return openId.toString();
		}
	}
}
