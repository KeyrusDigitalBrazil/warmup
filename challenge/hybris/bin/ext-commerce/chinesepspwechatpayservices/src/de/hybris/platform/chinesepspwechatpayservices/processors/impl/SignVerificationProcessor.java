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

import de.hybris.platform.chinesepspwechatpayservices.processors.AbstractWeChatPayProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;

import java.util.Map;
import java.util.Objects;



/**
 * Processor to valid signature
 */
public class SignVerificationProcessor extends AbstractWeChatPayProcessor<Boolean>
{
	private final String signToCheck;

	public SignVerificationProcessor(final WeChatPayConfiguration config, final Map<String, String> params)
	{
		super(config);
		this.signToCheck = params.get("sign");
		params.keySet().stream().filter(x -> !"sign".equalsIgnoreCase(x)).forEach(x -> this.addParameter(x, params.get(x)));
	}

	@Override
	public Boolean process()
	{
		final String mySign = getParams().generateSignature(getConfig().getMechKey());
		return Boolean.valueOf(Objects.equals(mySign, signToCheck));
	}
}
