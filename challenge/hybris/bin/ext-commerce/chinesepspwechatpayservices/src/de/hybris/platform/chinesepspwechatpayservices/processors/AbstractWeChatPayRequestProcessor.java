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
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;


/**
 * Base class for calling WeChat Pay API, each different API needs to extend it and customize the parameters and result
 * parsing
 */
public abstract class AbstractWeChatPayRequestProcessor<T> extends AbstractWeChatPayProcessor<T>
{
	private String url;
	private final WeChatPayHttpClient httpClient;

	public AbstractWeChatPayRequestProcessor(final WeChatPayConfiguration config, final WeChatPayHttpClient httpClient)
	{
		super(config);
		this.httpClient = httpClient;
		super.addParameter("appid", config.getAppId());
	}

	/**
	 * Process a POST request
	 *
	 * @return the request response
	 */
	protected String post()
	{
		return this.httpClient.post(this.url, getParams().generateXml());
	}

	/**
	 * Process a GET request
	 *
	 * @return the request response
	 */
	protected String get()
	{
		return this.httpClient.get(getParams().generateGetURL(this.url));
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url
	 *           the url to set
	 */
	public void setUrl(final String url)
	{
		this.url = url;
	}
}
