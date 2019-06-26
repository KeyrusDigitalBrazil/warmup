/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.initiateyaasconfigurationsync.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.platform.servicelayer.cronjob.JobDao;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationContainerModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationModel;
import de.hybris.y2ysync.model.Y2YSyncJobModel;
import de.hybris.y2ysync.services.SyncExecutionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

@UnitTest
public class DefaultYaasConfigurationSyncServiceUnitTest
{
	@InjectMocks
	private DefaultYaasConfigurationSyncService defaultYaasConfigurationSyncService;
	@Mock
	private SyncExecutionService syncExecutionService;
	@Mock
	private ChangeDetectionService changeDetectionService;
	@Mock
	private JobDao jobDao;
	@Mock
	private Y2YSyncJobModel y2YSyncJobModel;
	@Mock
	private ServicelayerJobModel otherJob;
	@Mock
	private Y2YStreamConfigurationContainerModel y2YStreamConfigurationContainerModel;
	@Mock
	private Y2YStreamConfigurationModel y2YStreamConfigurationModel;

	private String y2ySyncYaasConfigurationsJob;

	@Before
	public void setup()
	{
		defaultYaasConfigurationSyncService = new DefaultYaasConfigurationSyncService();
		MockitoAnnotations.initMocks(this);
		y2ySyncYaasConfigurationsJob = "jobCode";
		defaultYaasConfigurationSyncService.setY2ySyncYaasConfigurationsJobCode(y2ySyncYaasConfigurationsJob);

		Mockito.when(jobDao.findJobs(y2ySyncYaasConfigurationsJob)).thenReturn(Lists.newArrayList(y2YSyncJobModel, otherJob));
		Mockito.when(y2YSyncJobModel.getCode()).thenReturn(y2ySyncYaasConfigurationsJob);
	}

	@Test
	public void testSynchYaasConfiguration()
	{
		defaultYaasConfigurationSyncService.syncYaasConfiguration();

		verify(syncExecutionService, times(1)).startSync(y2ySyncYaasConfigurationsJob,
				SyncExecutionService.ExecutionMode.ASYNC);
	}
}
