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
package de.hybris.platform.commerceservices.organization.cronjob;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.OrgUnitHierarchyService;
import de.hybris.platform.commerceservices.organization.services.impl.OrgUnitHierarchyException;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link GenerateOrgUnitPathsJob}.
 */
@UnitTest
public class GenerateOrgUnitPathsJobTest
{
	@Mock
	private OrgUnitHierarchyService orgUnitHierarchyService;
	@Mock
	private ConfigurationService configurationService;
	private final Class<? extends OrgUnitModel> type = OrgUnitModel.class;

	@InjectMocks
	private final GenerateOrgUnitPathsJob generateOrgUnitPathJob = new GenerateOrgUnitPathsJob(type);

	@Mock
	private Configuration configuration;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		given(configurationService.getConfiguration()).willReturn(configuration);
	}

	@Test
	public void shouldPerformWithSuccess()
	{
		given(Boolean.valueOf(configuration.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true)))
				.willReturn(Boolean.TRUE);
		doNothing().when(orgUnitHierarchyService).generateUnitPaths(type);
		final PerformResult result = generateOrgUnitPathJob.perform(new CronJobModel());

		assertEquals("Unexpexted cron job status", CronJobStatus.FINISHED, result.getStatus());
		assertEquals("Unexpexted cron job result", CronJobResult.SUCCESS, result.getResult());
	}

	@Test
	public void shouldPerformWithFailure()
	{
		given(Boolean.valueOf(configuration.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true)))
				.willReturn(Boolean.TRUE);
		doThrow(new OrgUnitHierarchyException()).when(orgUnitHierarchyService).generateUnitPaths(OrgUnitModel.class);
		final PerformResult result = generateOrgUnitPathJob.perform(new CronJobModel());

		assertEquals("Unexpexted cron job status", CronJobStatus.FINISHED, result.getStatus());
		assertEquals("Unexpexted cron job result", CronJobResult.FAILURE, result.getResult());
	}

	@Test
	public void shouldSkipPathGeneration()
	{
		given(Boolean.valueOf(configuration.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true)))
				.willReturn(Boolean.FALSE);

		verify(orgUnitHierarchyService, never()).generateUnitPaths(type);
		final PerformResult result = generateOrgUnitPathJob.perform(new CronJobModel());

		assertEquals("Unexpexted cron job status", CronJobStatus.FINISHED, result.getStatus());
		assertEquals("Unexpexted cron job result", CronJobResult.SUCCESS, result.getResult());
	}
}