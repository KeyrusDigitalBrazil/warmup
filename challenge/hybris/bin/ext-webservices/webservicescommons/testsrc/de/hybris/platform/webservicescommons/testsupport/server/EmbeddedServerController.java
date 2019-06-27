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
package de.hybris.platform.webservicescommons.testsupport.server;

import de.hybris.platform.core.Registry;
import de.hybris.platform.embeddedserver.api.EmbeddedServer;
import de.hybris.platform.embeddedserver.api.EmbeddedServerBuilder;
import de.hybris.platform.embeddedserver.api.EmbeddedServerBuilderContext;
import de.hybris.platform.embeddedserver.base.EmbeddedExtension;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.webservicescommons.cache.TenantAwareEhCacheManagerFactoryBean;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;


/**
 * EmbeddedServerController is responsible for starting/stopping embedded server. It is used by
 * {@link EmbeddedServerTestRunListener}. It is intended to be used as singleton, it stores global data like embedded
 * server instance.
 */
public class EmbeddedServerController implements InitializingBean
{
	private static final String EMBEDDEDSERVER_HTTP_PORT_CONFIG_KEY = "embeddedserver.http.port";

	private static final int APPLICATION_PING_TIMEOUT = 2000;
	private static final int APPLICATION_PING_RETRIES = 5;
	private static final int APPLICATION_PING_WAIT_INTERVAL = 1000;

	private static final Logger LOG = Logger.getLogger(EmbeddedServerController.class);

	private EmbeddedServerBuilder embeddedServerBuilder;

	private EmbeddedServer embeddedServer;

	private String originalCacheSuffix;

	public void start(final String[] webExtensionNames)
	{
		LOG.debug("Creating embedded server");
		embeddedServer = createEmbeddedServer(webExtensionNames);

		beforeStart();

		LOG.info("Starting embedded server " + embeddedServer.toString());
		embeddedServer.start();

		LOG.debug("Ensuring that web apps are started");
		ensureWebAppsAreStarted(webExtensionNames);
	}

	public void stop()
	{
		LOG.debug("Preparing to stop embedded server");
		if (embeddedServer != null)
		{
			try
			{
				if (embeddedServer.isRunning())
				{
					LOG.info("Stopping embedded server" + embeddedServer.toString());
					embeddedServer.stop();
					embeddedServer = null;
				}
			}
			finally
			{
				afterStop();
			}
		}

	}

	protected void beforeStart()
	{
		setCacheSuffix();
	}

	protected void afterStop()
	{
		restoreOriginalCacheSuffix();
	}

	public EmbeddedExtension getEmbeddedExtension(final String extensionName)
	{
		return new EmbeddedExtension(Utilities.getExtensionInfo(extensionName)).withContext(Utilities.getWebroot(extensionName));
	}

	public EmbeddedServer createEmbeddedServer(final String[] webExtensionNames)
	{
		final EmbeddedServerBuilderContext embeddedServerCtx = embeddedServerBuilder.needEmbeddedServer();
		for (final String webExtensionName : webExtensionNames)
		{
			embeddedServerCtx.withApplication(getEmbeddedExtension(webExtensionName));
		}
		return embeddedServerCtx.build();
	}

	public EmbeddedServerBuilder getEmbeddedServerBuilder()
	{
		return embeddedServerBuilder;
	}

	public void setEmbeddedServerBuilder(final EmbeddedServerBuilder embeddedServerBuilder)
	{
		this.embeddedServerBuilder = embeddedServerBuilder;
	}

	/**
	 * Function modifies the EhCache cache suffix. This is required in order to run embedded server by running platform
	 * (the same web application is deployed twice). It may cause side effects in host platform.
	 *
	 */
	protected void setCacheSuffix()
	{
		LOG.debug("Setting different cache suffix");
		Registry.getCurrentTenant().getConfig().setParameter(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY,
				"testCache");
	}

	protected void restoreOriginalCacheSuffix()
	{
		LOG.debug("Restoring original cache suffix");
		if (originalCacheSuffix == null)
		{
			Registry.getCurrentTenant().getConfig().removeParameter(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY);
		}
		else
		{
			Registry.getCurrentTenant().getConfig().setParameter(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY,
					originalCacheSuffix);
			originalCacheSuffix = null;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		originalCacheSuffix = Registry.getCurrentTenant().getConfig()
				.getString(TenantAwareEhCacheManagerFactoryBean.CACHE_SUFFIX_PROPERTY, null);
	}

	private int getDefaultHttpPort()
	{
		return Registry.getCurrentTenant().getConfig().getInt(EMBEDDEDSERVER_HTTP_PORT_CONFIG_KEY, 8001);
	}

	public Optional<HttpStatus> getWebAppHeadStatus(final String webExtentionName)
	{
		HttpURLConnection connection = null;
		try
		{
			final URL url = new URL("http", "localhost", getDefaultHttpPort(), Utilities.getWebroot(webExtentionName));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setConnectTimeout(APPLICATION_PING_TIMEOUT);
			connection.setReadTimeout(APPLICATION_PING_TIMEOUT);
			final int responseCode = connection.getResponseCode();
			return Optional.of(HttpStatus.valueOf(responseCode));
		}
		catch (final IOException e)
		{
			LOG.warn("Problem when trying to get web app " + webExtentionName + " status");
			return Optional.empty();
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	public boolean ensureWebAppsAreStarted(final String[] webExtentionNames)
	{
		final List<String> notStarted = Arrays.asList(webExtentionNames).stream()//
				.filter(webApp -> !ensureWebAppIsStarted(webApp))//
				.collect(Collectors.toList());

		notStarted.stream()//
				.forEach(webApp -> LOG.warn("Application " + webApp + " is not started!"));

		return notStarted.isEmpty();
	}

	public boolean ensureWebAppIsStarted(final String webExtentionName)
	{
		final Supplier<Boolean> webAppStartedCondition = (() -> {
			final Optional<HttpStatus> status = getWebAppHeadStatus(webExtentionName);
			return Boolean.valueOf(status.isPresent() && !status.get().is5xxServerError());
		});
		final boolean webAppStarted = retry(webAppStartedCondition, APPLICATION_PING_RETRIES, APPLICATION_PING_WAIT_INTERVAL);
		return webAppStarted;
	}

	private static boolean retry(final Supplier<Boolean> condition, final int maxRetries, final int interval)
	{
		int trial = 1;
		while (trial <= maxRetries)
		{
			final Boolean result = condition.get();

			if (Boolean.TRUE.equals(result))
			{
				return true;
			}

			try
			{
				Thread.sleep(interval);
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
			trial++;
		}
		return false;
	}
}
