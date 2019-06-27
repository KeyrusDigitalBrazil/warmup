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

import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayParameters;

import org.apache.log4j.Logger;


/**
 * Abstraction of interactions with WeChat Pay, e.g. calling WeChat API, handle response from WeChat Pay
 */
public abstract class AbstractWeChatPayProcessor<T>
{
	private final WeChatPayParameters params;
	private final WeChatPayConfiguration config;

	protected final Logger logger = Logger.getLogger(getClass());

	public AbstractWeChatPayProcessor(final WeChatPayConfiguration config)
	{
		this.config = config;
		this.params = new WeChatPayParameters();
	}

	/**
	 * Add a parameter, will be ignored if the name or value is empty
	 *
	 * @param msg
	 *           message to be logged
	 */
	protected void debug(final String msg)
	{
		if (logger.isDebugEnabled())
		{
			logger.debug(msg);
		}
	}

	/**
	 * Process this operation
	 */
	public abstract T process();

	/**
	 * Add a parameter, will be ignored if the name or value is empty
	 *
	 * @param name
	 *           Parameter name
	 * @param value
	 *           Parameter value
	 */
	protected void addParameter(final String name, final String value)
	{
		this.params.add(name, value);
	}

	/**
	 * @return the params
	 */
	public WeChatPayParameters getParams()
	{
		return params;
	}

	/**
	 * @return the config
	 */
	public WeChatPayConfiguration getConfig()
	{
		return config;
	}

}
