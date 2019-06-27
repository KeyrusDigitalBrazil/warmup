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
package com.hybris.backoffice.config.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.media.storage.impl.DefaultMediaStorageConfigService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.model.BackofficeConfigurationMediaModel;
import com.hybris.cockpitng.core.config.CockpitConfigurationException;
import com.hybris.cockpitng.core.config.ConfigContext;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.cache.DefaultConfigurationCache;
import com.hybris.cockpitng.core.config.impl.jaxb.Config;
import com.hybris.cockpitng.core.config.impl.jaxb.Context;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.Editors;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeCockpitConfigurationServiceConcurrentTest
{

	private static final String TEXT_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "<config xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
			+ "    xmlns=\"http://www.hybris.com/cockpit/config\"\n"
			+ "    xsi:schemaLocation=\"http://www.hybris.com/cockpit/config http://www.hybris.com/schema/cockpitng/config/cockpit-configuration.xsd\">\n"
			+ "  <context component=\"testBoolean\">\n" + "    <y:editors xmlns:y=\"http://www.hybris.com/cockpit/config/hybris\">\n"
			+ "      <y:group qualifier=\"common\" >\n" + "<y:label>testBoolean</y:label>\n"
			+ "        <y:property qualifier=\"boolean\" type=\"BOOLEAN(CHECKBOX)\"/>\n" + "\t\t\t</y:group>\n"
			+ "    \t</y:editors>\n" + "    </context>\n" + "\n" + "</config>\n";

	private static final int THREAD_TRIPLES_NUMBER = 42;
	public static final String ILLEGAL_SEMAPHORE_STATE = "Illegal semaphore state";

	private Config jaxbConfig;

	@Mock
	private UserService userService;

	@Mock
	private SessionService sessionService;

	@Mock
	private ModelService modelService;

	@Mock
	private MediaService mediaService;

	@Mock
	private TimeService timeService;

	@Spy
	private DefaultMediaCockpitConfigurationPersistenceStrategy persistenceStrategy;

	@Spy
	private LockingBackofficeCockpitConfigurationService service;

	@Spy
	private DefaultBackofficeConfigurationMediaHelper backofficeConfigMediaHelper;

	@Mock
	private MediaFolderModel mediaFolder;

	@Mock
	private DefaultMediaStorageConfigService defaultMediaStorageConfigService;

	@Before
	public void setUp()
	{
		jaxbConfig = new Config();
		final Context context = new Context();
		context.setComponent("test-component");
		jaxbConfig.getContext().add(context);
		doAnswer(invocationOnMock -> {
			((SessionExecutionBody) (invocationOnMock.getArguments()[0])).executeWithoutResult();
			return null;
		}).when(sessionService).executeInLocalView(any());

		service.setPersistenceStrategy(persistenceStrategy);
		service.setTimeService(timeService);
		persistenceStrategy.setUserService(userService);
		persistenceStrategy.setSessionService(sessionService);
		persistenceStrategy.setBackofficeConfigurationMediaHelper(backofficeConfigMediaHelper);
		doAnswer(x -> IOUtils.toInputStream(TEXT_CONFIG)).when(persistenceStrategy).getConfigurationInputStream();

		backofficeConfigMediaHelper.setUserService(userService);
		backofficeConfigMediaHelper.setSessionService(sessionService);
		backofficeConfigMediaHelper.setModelService(modelService);
		backofficeConfigMediaHelper.setMediaService(mediaService);

		backofficeConfigMediaHelper.setMediaStorageConfigService(defaultMediaStorageConfigService);

		when(mediaFolder.getQualifier()).thenReturn("test_q");
		when(defaultMediaStorageConfigService.getSecuredFolders()).thenReturn(Arrays.asList("test_q"));
		when(mediaService.getFolder(any())).thenReturn(mediaFolder);

		doReturn(-1L).when(service).getCurrentTimeInMillis();
		doAnswer(invocationOnMock -> new Date(System.currentTimeMillis())).when(timeService).getCurrentTime();
		doAnswer(x -> new BackofficeConfigurationMediaModel()).when(modelService).create(BackofficeConfigurationMediaModel.class);
		service.setConfigurationCache(new DefaultConfigurationCache());
	}

	@Test
	public void configurationStorageThatUsesConfigBufferMustBeThreadSafe() throws InterruptedException, ExecutionException
	{

		final ExecutorService executor = Executors.newFixedThreadPool(3 * THREAD_TRIPLES_NUMBER);

		final CyclicBarrier barrier = new CyclicBarrier(3 * THREAD_TRIPLES_NUMBER);

		final Collection<Future> executionResults = new ArrayList<>();

		for (int i = 0; i < THREAD_TRIPLES_NUMBER; i++)
		{
			executionResults.add(executor.submit(storeRootConfig(service, jaxbConfig, barrier)));
			executionResults.add(executor.submit(storeTextConfig(service, TEXT_CONFIG, barrier)));
			executionResults.add(executor
					.submit(storeConfiguration(new DefaultConfigContext("test-" + System.nanoTime()), new Editors(), barrier)));
		}

		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		for (final Future result : executionResults)
		{
			result.get();// to fail on exceptions
		}
	}

	private Runnable storeTextConfig(final LockingBackofficeCockpitConfigurationService service, final String textConfig,
			final CyclicBarrier barrier)
	{
		return () -> {
			try
			{
				barrier.await();
				service.setConfigAsString(textConfig);
			}
			catch (final InterruptedException | BrokenBarrierException e)
			{
				fail(ILLEGAL_SEMAPHORE_STATE, e);
				Thread.currentThread().interrupt();
			}
		};
	}

	private Runnable storeRootConfig(final LockingBackofficeCockpitConfigurationService service, final Config jaxbConfig,
			final CyclicBarrier barrier)
	{
		return () -> {
			try
			{
				barrier.await();
				service.storeRootConfig(jaxbConfig);
			}
			catch (final InterruptedException | BrokenBarrierException e)
			{
				fail(ILLEGAL_SEMAPHORE_STATE, e);
				Thread.currentThread().interrupt();
			}
			catch (final CockpitConfigurationException e)
			{
				fail("Could not store configuration", e);
			}
		};
	}

	private Runnable storeConfiguration(final ConfigContext context, final Object config, final CyclicBarrier barrier)
	{
		return () -> {
			try
			{
				barrier.await();
				service.storeConfiguration(context, config);
			}
			catch (final InterruptedException | BrokenBarrierException e)
			{
				fail(ILLEGAL_SEMAPHORE_STATE, e);
				Thread.currentThread().interrupt();
			}
			catch (final CockpitConfigurationException e)
			{
				fail("Could not store configuration", e);
			}
		};
	}

	@Test
	public void shouldNotBlockOnLoadConfiguration() throws InterruptedException, ExecutionException, TimeoutException
	{
		doReturn(new ByteArrayInputStream(TEXT_CONFIG.getBytes())).when(persistenceStrategy).getDefaultConfigurationInputStream();
		doReturn(new NullInputStream(0)).when(persistenceStrategy).getConfigurationInputStream();

		final ForkJoinTask<?> task = ForkJoinPool.commonPool().submit(() -> {
			try
			{
				final Editors c = service.loadConfiguration(new DefaultConfigContext("test"), Editors.class);
				assertThat(c).isNotNull();

			}
			catch (final Exception e)
			{
				// expected - the strategy mock returns empty stream
			}
		});

		final Object result = task.get(5, TimeUnit.SECONDS);
		verify(service).setConfigAsString(any());
		assertThat(result).isNull();
	}

	public class LockingBackofficeCockpitConfigurationService extends BackofficeCockpitConfigurationService
	{

		private final Map guard = new WeakHashMap();

		/**
		 * @return output stream
		 * @deprecated
		 */
		@Deprecated
		@Override
		protected synchronized ByteArrayOutputStream getConfigFileOutputStream()
		{
			final long id = Thread.currentThread().getId();
			final ByteArrayOutputStream fos = super.getConfigFileOutputStream();
			if (guard.containsKey(fos))
			{
				assertThat(guard.get(fos)).isEqualTo(id);
			}
			else
			{
				guard.put(fos, id);
			}
			return fos;
		}
	}
}
