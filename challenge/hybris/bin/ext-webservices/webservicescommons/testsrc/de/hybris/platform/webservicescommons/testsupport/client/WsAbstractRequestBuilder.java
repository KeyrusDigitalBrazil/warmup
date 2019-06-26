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
package de.hybris.platform.webservicescommons.testsupport.client;

import de.hybris.platform.core.Registry;
import de.hybris.platform.util.Utilities;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.fest.util.Arrays;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;


public abstract class WsAbstractRequestBuilder<T extends WsAbstractRequestBuilder<?>>
{
	abstract protected T getThis();

	public static final String EMBEDDEDSERVER_HTTP_PORT_CONFIG_KEY = "embeddedserver.http.port";
	public static final String EMBEDDEDSERVER_HTTPS_PORT_CONFIG_KEY = "embeddedserver.ssl.port";
	public static final String WEBSERVICES_REQUIRED_CHANNEL_CONFIG_KEY = "webservicescommons.required.channel";

	private String host = "localhost";
	private boolean useHttps = getDefaultUseHttps();
	private int port = getDefaultUseHttps() ? getDefaultHttpsPort() : getDefaultHttpPort();
	private final Map<String, Object> queryParams = new HashMap<>();

	private String extensionName = null;
	private String path = null;
	private ClientConfig clientConfig = getDefaultClientConfig();

	private static int getDefaultHttpPort()
	{
		return Registry.getCurrentTenant().getConfig().getInt(EMBEDDEDSERVER_HTTP_PORT_CONFIG_KEY, 8001);
	}

	private static int getDefaultHttpsPort()
	{
		return Registry.getCurrentTenant().getConfig().getInt(EMBEDDEDSERVER_HTTPS_PORT_CONFIG_KEY, 8002);
	}

	private static boolean getDefaultUseHttps()
	{
		return Registry.getCurrentTenant().getConfig().getString(WEBSERVICES_REQUIRED_CHANNEL_CONFIG_KEY, "https").equals("https")
				? true : false;
	}

	private static ClientConfig getDefaultClientConfig()
	{
		final ClientConfig config = new ClientConfig();

		//json configuration
		final JacksonJsonProvider provider = new JacksonJaxbJsonProvider()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		config.register(provider);

		//Jersey needs its own logger to setup logging entity
		final java.util.logging.Logger loggerForJerseyLoggingFilter = java.util.logging.Logger
				.getLogger(WsAbstractRequestBuilder.class.getName());
		config.register(
				new LoggingFeature(loggerForJerseyLoggingFilter, Level.INFO, Verbosity.PAYLOAD_ANY, Integer.valueOf(8 * 1024)));

		config.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, Boolean.TRUE);
		config.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, Boolean.TRUE);
		return config;
	}

	protected Client createClient()
	{
		try
		{
			final TrustManager[] trustAllCerts = Arrays.array(new DummyTrustManager());
			final SSLContext sc = SSLContext.getInstance("TLSv1");
			System.setProperty("https.protocols", "TLSv1");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());

			return ClientBuilder.newBuilder().withConfig(clientConfig).hostnameVerifier(new DummyHostnameVerifier()).sslContext(sc)
					.build();
		}
		catch (final GeneralSecurityException gse)
		{
			throw new RuntimeException(gse);
		}

	}

	public T useDefaultHttpPort()
	{
		this.port = getDefaultHttpPort();
		this.useHttps = false;
		return getThis();
	}

	public T useDefaultHttpsPort()
	{
		this.port = getDefaultHttpsPort();
		this.useHttps = true;
		return getThis();
	}

	public T useSpecificPort(final int port, final boolean useHttps)
	{
		this.port = port;
		this.useHttps = useHttps;
		return getThis();
	}

	public T host(final String host)
	{
		this.host = host;
		return getThis();
	}

	public T extensionName(final String extensionName)
	{
		this.extensionName = extensionName;
		return getThis();
	}

	public T clientConfig(final ClientConfig clientConfig)
	{
		setClientConfig(clientConfig);
		return getThis();
	}

	public T path(final String path)
	{
		if (StringUtils.isEmpty(this.path))
		{
			this.path = path;
		}
		else
		{
			this.path = String.join("/", this.path, path);
		}
		return getThis();
	}

	public T queryParam(final String paramName, final Object paramValue)
	{
		this.queryParams.put(paramName, paramValue);
		return getThis();
	}

	public T registerConfig(final Class<?> providerClass)
	{
		clientConfig.register(providerClass);
		return getThis();
	}

	public T registerConfig(final Object provider)
	{
		clientConfig.register(provider);
		return getThis();
	}

	public Invocation.Builder build()
	{
		WebTarget wt = createWebTarget(host, port, useHttps, extensionName, path);
		for (final Entry<String, Object> queryParam : queryParams.entrySet())
		{
			wt = wt.queryParam(queryParam.getKey(), queryParam.getValue());
		}
		return wt.request();
	}

	protected WebTarget createWebTarget(final String host, final int port, final boolean useHttps, final String extensionName,
			final String path)
	{
		final String fromUri = useHttps ? "https://" + host + "/" : "http://" + host + "/";
		final UriBuilder uriBuilder = UriBuilder.fromUri(fromUri).port(port);
		if (!StringUtils.isEmpty(extensionName))
		{
			uriBuilder.path(Utilities.getWebroot(extensionName));
		}
		if (!StringUtils.isEmpty(path))
		{
			uriBuilder.path(path);
		}
		return createClient().target(uriBuilder.build());
	}

	public static class WsRequestBuilderException extends RuntimeException
	{
		public WsRequestBuilderException(final String message, final Throwable cause)
		{
			super(message, cause);
		}

		public WsRequestBuilderException(final String message)
		{
			super(message);
		}
	}

	protected String getHost()
	{
		return host;
	}

	protected void setHost(final String host)
	{
		this.host = host;
	}

	protected boolean isUseHttps()
	{
		return useHttps;
	}

	protected void setUseHttps(final boolean useHttps)
	{
		this.useHttps = useHttps;
	}

	protected int getPort()
	{
		return port;
	}

	protected void setPort(final int port)
	{
		this.port = port;
	}

	protected String getExtensionName()
	{
		return extensionName;
	}

	protected void setExtensionName(final String extensionName)
	{
		this.extensionName = extensionName;
	}

	protected String getPath()
	{
		return path;
	}

	protected void setPath(final String path)
	{
		this.path = path;
	}

	protected Map<String, Object> getQueryParams()
	{
		return queryParams;
	}

	protected void setClientConfig(final ClientConfig clientConfig)
	{
		this.clientConfig = clientConfig;
	}

	protected ClientConfig getClientConfig()
	{
		return clientConfig;
	}
}