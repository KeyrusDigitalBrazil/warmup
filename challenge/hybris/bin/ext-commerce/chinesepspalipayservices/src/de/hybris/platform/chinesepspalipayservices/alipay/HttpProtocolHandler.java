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
package de.hybris.platform.chinesepspalipayservices.alipay;

import de.hybris.platform.chinesepspalipayservices.constants.PaymentConstants;
import de.hybris.platform.chinesepspalipayservices.data.HttpRequest;
import de.hybris.platform.chinesepspalipayservices.data.HttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;


/**
 * Handles http/https request
 */
public class HttpProtocolHandler
{
	private static final Logger LOG = Logger.getLogger(HttpProtocolHandler.class);

	private static final String DEFAULT_CHARSET = "utf-8";

	private static final int DEFAULT_CONNECTION_TIMEOUT = 20000;

	private static final int DEFAULT_SO_TIMEOUT = 30000;

	private static final int DEFAULT_IDLECONN_TIMEOUT = 60000;

	private static final int DEFAULT_MAX_CONN_PERHOST = 30;

	private static final int DEFAULT_MAX_TOTAL_CONN = 80;

	private static final int DEFAULT_HTTPCONNECTIONMANAGER_TIMEOUT = 3000;

	private static PoolingHttpClientConnectionManager connectionManager;

	private static HttpProtocolHandler httpProtocolHandler = null;

	private HttpProtocolHandler()
	{

	}

	/**
	 * Gets handler instance
	 *
	 * @return http protocal handler instance
	 */
	public static HttpProtocolHandler getInstance()
	{
		if (httpProtocolHandler == null)
		{
			connectionManager = new PoolingHttpClientConnectionManager();
			connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONN_PERHOST);
			connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONN);

			final IdleConnectionEvictor ice = new IdleConnectionEvictor(connectionManager, DEFAULT_IDLECONN_TIMEOUT,
					TimeUnit.SECONDS);
			ice.start();

			httpProtocolHandler = new HttpProtocolHandler();
		}
		return httpProtocolHandler;
	}


	/**
	 * Sends http/https request
	 *
	 * @param request
	 *           the http/https request
	 * @return the http/https response
	 */
	public HttpResponse execute(final HttpRequest request)
	{

		final String charset = StringUtils.isEmpty(request.getCharset()) ? DEFAULT_CHARSET : request.getCharset();

		HttpRequestBase httpRequest = new HttpPost(request.getUrl());
		if (PaymentConstants.HTTP.METHOD_GET.equals(request.getMethod()))
		{
			try
			{
				final URIBuilder urlBuilder = new URIBuilder(request.getUrl());
				urlBuilder.setParameter("http.protocol.credential-charset", charset);
				httpRequest = new HttpGet(urlBuilder.build());
			}
			catch (final URISyntaxException e) //NOSONAR
			{
				LOG.warn("URISyntaxException on executing http request");
			}
		}
		else
		{
			try
			{
				((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(request.getParameters(), charset));
			}
			catch (final UnsupportedEncodingException e)//NOSONAR
			{
				LOG.warn("UnsupportedEncodingException on executing http request");
			}
			httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=" + charset);

		}

		httpRequest.setConfig(getHttpRequestConfig(request));
		httpRequest.setHeader("User-Agent", "Mozilla/4.0");
		final HttpResponse response = new HttpResponse();

		try
		{
			final CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connectionManager)
					.setConnectionManagerShared(true).build();
			final CloseableHttpResponse httpResponse = httpclient.execute(httpRequest);
			response.setStringResult(EntityUtils.toString(httpResponse.getEntity()));
			response.setResponseHeaders(Arrays.asList(httpResponse.getAllHeaders()));

			httpclient.close();
		}
		catch (final IOException e)//NOSONAR
		{
			LOG.error("IOException on executing http request");
			return null;
		}
		finally

		{
			httpRequest.releaseConnection();
		}
		return response;

	}

	protected RequestConfig getHttpRequestConfig(final HttpRequest request)
	{
		final int connectionTimeout = request.getConnectionTimeout() > 0 ? request.getConnectionTimeout()
				: DEFAULT_CONNECTION_TIMEOUT;
		final int soTimeout = request.getTimeout() > 0 ? request.getTimeout() : DEFAULT_SO_TIMEOUT;
		return RequestConfig.custom().setSocketTimeout(soTimeout).setConnectTimeout(connectionTimeout)
				.setConnectionRequestTimeout(DEFAULT_HTTPCONNECTIONMANAGER_TIMEOUT).build();
	}

}
