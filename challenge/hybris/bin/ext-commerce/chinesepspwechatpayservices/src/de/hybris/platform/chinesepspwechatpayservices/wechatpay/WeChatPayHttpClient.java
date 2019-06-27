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
package de.hybris.platform.chinesepspwechatpayservices.wechatpay;

import de.hybris.platform.chinesepspwechatpayservices.exception.WeChatPayException;

import java.io.IOException;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

/**
 * A wrapper around Apache Common HttpClient
 */
public class WeChatPayHttpClient
{
	private static final String WECHAT_PAY_GET_METHOD_FAILED = "Wechat Pay get method failed: ";
	private static final String WECHAT_PAY_POST_METHOD_FAILED = "Wechat Pay post method failed: ";

	private final int maxConnPerRoute;
	private final int maxConnTotal;

	public WeChatPayHttpClient(final int maxConnPerRoute, final int  maxConnTotal)
	{
		this.maxConnPerRoute=maxConnPerRoute;
		this.maxConnTotal = maxConnTotal;
	}

	/**
	 * Process a POST request
	 *
	 * @param url
	 *           URL of the post target
	 * @param requestBody
	 *           body of the request, MUST be XML format
	 *
	 * @return the request response
	 */
	public String post(final String url, final String requestBody)
	{
		final HttpPost post = new HttpPost(url);
		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler());
		final CloseableHttpClient httpClient = httpClientBuilder.build();
		try
		{

			post.setEntity(new StringEntity(requestBody, ContentType.create(MediaType.APPLICATION_XML.toString(), CharEncoding.UTF_8)));
			final HttpResponse response = httpClient.execute(post);
			final int statusCode = response.getStatusLine().getStatusCode();

			final String result = EntityUtils.toString(response.getEntity());

			if (statusCode != HttpStatus.SC_OK || StringUtils.isEmpty(result))
			{
				throw new WeChatPayException(WECHAT_PAY_POST_METHOD_FAILED + response.getStatusLine());
			}
			httpClient.close();
			return result;
		}
		catch (final IOException e)
		{
			throw new WeChatPayException(WECHAT_PAY_POST_METHOD_FAILED, e);
		}
		finally
		{
			post.releaseConnection();
		}
	}

	/**
	 * Process a GET request
	 *
	 * @param url
	 *           URL of the get target
	 *
	 * @return the request response
	 */
	public String get(final String url)
	{
		final HttpGet get = new HttpGet(url);
		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler());
		final CloseableHttpClient httpClient = httpClientBuilder.build();
		try
		{
			final HttpResponse response = httpClient.execute(get);
			final int statusCode = response.getStatusLine().getStatusCode();

			final String result = EntityUtils.toString(response.getEntity());

			if (statusCode != HttpStatus.SC_OK || StringUtils.isEmpty(result))
			{
				throw new WeChatPayException(WECHAT_PAY_GET_METHOD_FAILED + response.getStatusLine());
			}

			httpClient.close();
			return result;
		}
		catch (final IOException e)
		{
			throw new WeChatPayException(WECHAT_PAY_GET_METHOD_FAILED, e);
		}
		finally
		{
			get.releaseConnection();
		}
	}

	public int getMaxConnPerRoute() {
		return maxConnPerRoute;
	}

	public int getMaxConnTotal() {
		return maxConnTotal;
	}

}