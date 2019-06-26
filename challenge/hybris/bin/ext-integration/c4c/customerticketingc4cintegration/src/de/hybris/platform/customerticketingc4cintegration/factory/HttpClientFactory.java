/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.customerticketingc4cintegration.factory;

import de.hybris.platform.util.Config;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Apache HTTP client wrapper, used for setting proxy stuff.
 */
public class HttpClientFactory
{
	private final HttpClientBuilder builder;

	public HttpClientFactory()
	{
		builder = HttpClientBuilder.create();
	}

	/**
	 * Construct HttpClient with proxy settings.
	 */
	public HttpClient getHttpClient()
	{
		final String proxyHost = Config.getParameter("http.proxy.host");
		final String proxyPort = Config.getParameter("http.proxy.port");
		if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort))
		{
			builder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
		}
		return builder.build();
	}
}
