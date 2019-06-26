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
package com.hybris.ymkt.common.http;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;


public class HttpURLConnectionRequest
{
	private static final byte[] ZERO_BYTES = new byte[0];

	protected int connectTimeout = -1;
	protected byte[] payload = ZERO_BYTES;
	protected Proxy proxy = Proxy.NO_PROXY;
	protected int readTimeout = -1;
	protected final String requestMethod;
	protected final Map<String, String> requestProperties = new HashMap<>();
	protected final URL url;

	/**
	 * @param requestMethod
	 *           Any values of : GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
	 * @param url
	 *           {@link URL} of the service end point including the query parameters.
	 *
	 * @see HttpURLConnection#setRequestMethod
	 * @see URL#openConnection()
	 */
	public HttpURLConnectionRequest(final String requestMethod, final URL url)
	{
		this.requestMethod = Objects.requireNonNull(requestMethod);
		this.url = Objects.requireNonNull(url);
	}

	public int getConnectTimeout()
	{
		return this.connectTimeout;
	}

	@Nonnull
	public byte[] getPayload()
	{
		return this.payload;
	}

	@Nonnull
	public Proxy getProxy()
	{
		return this.proxy;
	}

	public int getReadTimeout()
	{
		return this.readTimeout;
	}

	@Nonnull
	public String getRequestMethod()
	{
		return this.requestMethod;
	}

	/**
	 * @return Modifiable map containing the request properties to be added to the URLConnection.
	 * @see HttpURLConnection#addRequestProperty(String, String)
	 */
	@Nonnull
	public Map<String, String> getRequestProperties()
	{
		return this.requestProperties;
	}

	@Nonnull
	public URL getURL()
	{
		return this.url;
	}

	/**
	 * @param connectTimeout
	 *           an int that specifies the connect timeout value in milliseconds.<br>
	 *           Replace the value set in HttpURLConnectionService#setConnectTimeout(int).<Br>
	 *           The default value is -1. Any negative value will be ignore and will use the
	 *           HttpURLConnectionService#setConnectTimeout(int) set instead.
	 * @see HttpURLConnectionService#setConnectTimeout(int)
	 * @see URLConnection#setConnectTimeout(int)
	 */
	public void setConnectTimeout(final int connectTimeout)
	{
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @param payload
	 *           bytes sent as part of the HTTP request.
	 * @see URLConnection#getOutputStream()
	 * @see HttpURLConnection#setFixedLengthStreamingMode(int)
	 */
	public void setPayload(final byte[] payload)
	{
		this.payload = Objects.requireNonNull(payload);
	}

	/**
	 * @param proxy
	 * @see URL#openConnection(Proxy)
	 */
	public void setProxy(final Proxy proxy)
	{
		this.proxy = Objects.requireNonNull(proxy);
	}

	/**
	 * @param readTimeout
	 *           an int that specifies the timeout value to be used in milliseconds.<br>
	 *           Replace the value set in HttpURLConnectionService#setReadTimeout(int).<Br>
	 *           The default value is -1. Any negative value will be ignore and will use the
	 *           HttpURLConnectionService#setReadTimeout(int) set instead.
	 * @see HttpURLConnectionService#setReadTimeout(int)
	 * @see URLConnection#setReadTimeout(int)
	 */
	public void setReadTimeout(final int readTimeout)
	{
		this.readTimeout = readTimeout;
	}

}
