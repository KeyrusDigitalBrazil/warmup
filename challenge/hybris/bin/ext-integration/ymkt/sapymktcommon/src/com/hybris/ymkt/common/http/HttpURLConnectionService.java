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

import de.hybris.platform.util.Config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This bean shall be instantiated only once for the entire server/tenant.<br>
 * All methods could have been public static but are defined this way to allow overriding.<br>
 * This service only works with byte[] instead of {@link InputStream} & {@link OutputStream}. It is important that all
 * intended input and output payloads fit into memory.
 *
 * @see HttpURLConnectionRequest
 * @see HttpURLConnectionResponse
 */
public class HttpURLConnectionService
{
	private static final Logger LOG = LoggerFactory.getLogger(HttpURLConnectionService.class);
	private static final byte[] ZERO_BYTES = new byte[0];

	protected int connectTimeout = 10000; // 10 sec
	protected int readTimeout = 10000; // 10 sec

	/**
	 * Utility method to extract all bytes of the {@link InputStream} and return them as a byte array.
	 *
	 * @param stream
	 *           {@link InputStream} to be converted into byte[].
	 * @return byte array containing all bytes of the {@link InputStream}. The length of the array equals to total number
	 *         of bytes read.
	 * @throws IOException
	 *            May be thrown by the following 2 methods : {@link InputStream#read()} and {@link InputStream#close()}
	 */
	@Nonnull
	public byte[] bufferStream(final InputStream stream) throws IOException
	{
		if (stream == null)
		{
			return ZERO_BYTES;
		}

		final byte[] buffer = new byte[2048];
		int bytesRead;
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		while ((bytesRead = stream.read(buffer)) != -1)
		{
			baos.write(buffer, 0, bytesRead);
		}
		return baos.toByteArray();
	}

	protected void disableHttpsCertificate(final HttpsURLConnection connection)
	{
		try
		{
			final TrustManager[] trustAllCerts =
			{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
					.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

			final SSLContext ctx = SSLContext.getInstance("SSL");
			ctx.init(null, trustAllCerts, new SecureRandom());
			connection.setSSLSocketFactory(ctx.getSocketFactory());
			connection.setHostnameVerifier((hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost()));
		}
		catch (ReflectiveOperationException | GeneralSecurityException e)
		{
			throw new IllegalStateException("Error disabling certificate validation.", e);
		}
	}

	/**
	 * This method execute a request and return a response.<br>
	 * No exception will be thrown as the caller is responsible to check the
	 * {@link HttpURLConnectionResponse#getIOException()}.
	 *
	 * @param request
	 *           {@link HttpURLConnectionRequest}
	 * @return {@link HttpURLConnectionResponse}
	 */
	@Nonnull
	public HttpURLConnectionResponse execute(final HttpURLConnectionRequest request)
	{
		final HttpURLConnectionResponse response = new HttpURLConnectionResponse();
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection) request.getURL().openConnection(request.getProxy());

			if (connection instanceof HttpsURLConnection && //
					Config.getBoolean("sapymktcommon.odata.url.disable_https_certificate", false))
			{
				// Disable https certificate validation for demo and dev environment.
				this.disableHttpsCertificate((HttpsURLConnection) connection);
			}

			connection.setConnectTimeout(request.getConnectTimeout() >= 0 ? request.getConnectTimeout() : this.connectTimeout);
			connection.setReadTimeout(request.getReadTimeout() >= 0 ? request.getReadTimeout() : this.readTimeout);
			connection.setUseCaches(false);

			connection.setRequestMethod(request.getRequestMethod());
			request.getRequestProperties().forEach(connection::setRequestProperty);

			if (request.getPayload().length > 0)
			{
				connection.setDoOutput(true);
				connection.setFixedLengthStreamingMode(request.getPayload().length);
			}

			this.trace(connection.getRequestProperties());

			// connectTimeout
			connection.connect();

			if (request.getPayload().length > 0)
			{
				try (OutputStream outputStream = connection.getOutputStream())
				{
					outputStream.write(request.getPayload());
					outputStream.flush();
				}
			}

			// readTimeout
			final int responseCode = connection.getResponseCode();
			response.setResponseCode(responseCode);

			try (InputStream inputStream = connection.getErrorStream())
			{
				response.setPayloadError(this.bufferStream(inputStream));
			}

			if (responseCode < HttpURLConnection.HTTP_BAD_REQUEST)
			{
				this.trace(connection.getHeaderFields());

				response.setHeaderFields(connection.getHeaderFields());

				try (InputStream inputStream = connection.getInputStream())
				{
					response.setPayload(this.bufferStream(inputStream));
				}
			}
		}
		catch (final IOException e)
		{
			response.setIOException(e);
		}
		finally
		{
			Optional.ofNullable(connection).ifPresent(HttpURLConnection::disconnect);
		}

		this.logExecution(request, response);

		return response;
	}

	protected void logExecution(final HttpURLConnectionRequest request, final HttpURLConnectionResponse response)
	{
		final int payloadInLength = Math.max(response.getPayload().length, response.getPayloadError().length);

		LOG.info("{}({}) {}ms {}Bout {}Bin - {}", request.getRequestMethod(), response.getResponseCode(), response.getDuration(),
				request.getPayload().length, payloadInLength, request.getURL());
	}

	/**
	 * @param connectTimeout
	 *           an int that specifies the connect timeout value in milliseconds
	 * @see URLConnection#setConnectTimeout(int)
	 */
	public void setConnectTimeout(final int connectTimeout)
	{
		LOG.debug("connectTimeout={}", connectTimeout);
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @param readTimeout
	 *           an int that specifies the timeout value to be used in milliseconds
	 * @see URLConnection#setReadTimeout(int)
	 */
	public void setReadTimeout(final int readTimeout)
	{
		LOG.debug("readTimeout={}", readTimeout);
		this.readTimeout = readTimeout;
	}

	protected void trace(final Map<String, List<String>> fields)
	{
		if (LOG.isTraceEnabled())
		{
			fields.forEach((k, l) -> l.forEach(v -> LOG.trace("{} -> {}", k, v)));
		}
	}

}
