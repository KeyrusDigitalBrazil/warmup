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

import de.hybris.platform.chinesepspwechatpayservices.data.StartPaymentData;
import de.hybris.platform.chinesepspwechatpayservices.processors.AbstractWeChatPayProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;

import java.time.Instant;
import java.util.Map;


/**
 * Processor to generator data for start payment
 */
public class StartPaymentRequestProcessor extends AbstractWeChatPayProcessor<StartPaymentData>
{
	public StartPaymentRequestProcessor(final WeChatPayConfiguration config, final String prepayId)
	{
		super(config);
		super.addParameter("appId", config.getAppId());
		super.addParameter("timeStamp", String.valueOf(Instant.now().getEpochSecond()));
		super.addParameter("nonceStr", super.getParams().generateNonce());
		super.addParameter("package", "prepay_id=" + prepayId);
		super.addParameter("signType", "MD5");
		super.addParameter("paySign", super.getParams().generateSignature(config.getMechKey()));
	}

	@Override
	public StartPaymentData process()
	{
		final StartPaymentData paymentData = new StartPaymentData();
		final Map<String, String> m = getParams().getParameters();
		paymentData.setAppId(m.get("appId"));
		paymentData.setTimeStamp(m.get("timeStamp"));
		paymentData.setNonceStr(m.get("nonceStr"));
		paymentData.setPackageName(m.get("package"));
		paymentData.setPaySign(m.get("paySign"));
		paymentData.setSignType(m.get("signType"));
		return paymentData;
	}
}
