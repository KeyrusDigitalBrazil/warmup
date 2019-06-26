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
package com.hybris.backoffice.sync.impl;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.event.EventSender;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.backoffice.cockpitng.events.BackofficePlatformEventAdapter;
import com.hybris.backoffice.events.processes.ProcessFinishedEvent;
import com.hybris.backoffice.events.processes.ProcessStartEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.core.util.CockpitProperties;


@IntegrationTest
public class BackofficeSyncIntegrationTest extends ServicelayerBaseTest
{
	private CatalogModel catalog;
	private CatalogVersionModel sourceCatalogVersion;
	private CatalogVersionModel targetCatalogVersion;
	private SyncItemJobModel baseSyncItemJob;

	@Resource
	private CatalogSynchronizationService catalogSynchronizationService;
	@Resource
	private EventSender backofficeEventSender;
	@Mock
	private CockpitEventQueue cockpitEventQueue;
	@Mock
	private CockpitProperties cockpitProperties;
	@Resource
	private ModelService modelService;


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		catalog = createCatalog(String.format("%s%s", "test_catalog", RandomStringUtils.randomAlphanumeric(3)));
		sourceCatalogVersion = createCatalogVersion(catalog,
				String.format("%s%s", "test_source_version", RandomStringUtils.randomAlphanumeric(3)));
		targetCatalogVersion = createCatalogVersion(catalog,
				String.format("%s%s", "test_target_version", RandomStringUtils.randomAlphanumeric(3)));

		baseSyncItemJob = createSyncJob(RandomStringUtils.randomAlphanumeric(5), sourceCatalogVersion, targetCatalogVersion);
		modelService.saveAll();

		final BackofficePlatformEventAdapter backofficeAdapter = new BackofficePlatformEventAdapter();
		backofficeAdapter.setCockpitEventQueue(cockpitEventQueue);
		backofficeAdapter.setCockpitProperties(cockpitProperties);
		backofficeAdapter.setBackofficeEventSender(backofficeEventSender);
		backofficeAdapter.afterPropertiesSet();
	}

	@Test
	public void testBeforeAndAfterSyncEventsTriggered()
	{

		// given
		Mockito.when(cockpitProperties.getProperty("cockpitng.globaleventtimer.enabled")).thenReturn("true");

		final SyncConfig config = new SyncConfig();
		config.setCreateSavedValues(Boolean.TRUE);
		config.setForceUpdate(Boolean.FALSE);
		config.setLogToDatabase(Boolean.FALSE);
		config.setLogLevelDatabase(JobLogLevel.INFO);
		config.setLogToFile(Boolean.FALSE);
		config.setLogLevelFile(JobLogLevel.INFO);

		// when
		catalogSynchronizationService.synchronize(baseSyncItemJob, config);

		// then
		final ArgumentMatcher<DefaultCockpitEvent> startedMatcher = new ArgumentMatcher<DefaultCockpitEvent>()
		{

			@Override
			public boolean matches(final Object argument)
			{
				final DefaultCockpitEvent event = (DefaultCockpitEvent) argument;
				return ProcessStartEvent.EVENT_NAME.equals(event.getName()) && event.getData() instanceof ProcessStartEvent
						&& ((ProcessStartEvent) event.getData()).getProcessEvent() != null;
			}

		};
		final ArgumentMatcher<DefaultCockpitEvent> finishedMatcher = new ArgumentMatcher<DefaultCockpitEvent>()
		{

			@Override
			public boolean matches(final Object argument)
			{
				final DefaultCockpitEvent event = (DefaultCockpitEvent) argument;
				return ProcessFinishedEvent.EVENT_NAME.equals(event.getName()) && event.getData() instanceof ProcessFinishedEvent
						&& ((ProcessFinishedEvent) event.getData()).getProcessEvent() != null;
			}

		};

		Mockito.verify(cockpitEventQueue).publishEvent(Mockito.argThat(startedMatcher));
		Mockito.verify(cockpitEventQueue).publishEvent(Mockito.argThat(finishedMatcher));
	}

	protected SyncItemJobModel createSyncJob(final String code, final CatalogVersionModel source, final CatalogVersionModel target)
	{

		final CatalogVersionSyncJobModel job = modelService.create(CatalogVersionSyncJobModel.class);
		job.setCode(code);
		job.setSourceVersion(source);
		job.setTargetVersion(target);
		job.setMaxThreads(Integer.valueOf(10));
		job.setCreateNewItems(Boolean.TRUE);
		job.setRemoveMissingItems(Boolean.TRUE);
		return job;
	}

	protected CatalogModel createCatalog(final String id)
	{

		final CatalogModel catalogModel = modelService.create(CatalogModel.class);
		catalogModel.setId(id);
		return catalogModel;
	}

	protected CatalogVersionModel createCatalogVersion(final CatalogModel catalog, final String version)
	{
		final CatalogVersionModel catalogVersionModel = modelService.create(CatalogVersionModel.class);
		catalogVersionModel.setCatalog(catalog);
		catalogVersionModel.setVersion(version);
		return catalogVersionModel;
	}

}
