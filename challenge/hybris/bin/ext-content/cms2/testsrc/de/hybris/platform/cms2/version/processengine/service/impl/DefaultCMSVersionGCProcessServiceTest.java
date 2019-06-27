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
package de.hybris.platform.cms2.version.processengine.service.impl;

import static de.hybris.platform.cms2.constants.Cms2Constants.DEFAULT_VERSION_GC_MAX_AGE_DAYS;
import static de.hybris.platform.cms2.constants.Cms2Constants.DEFAULT_VERSION_GC_MAX_NUMBER_VERSIONS;
import static de.hybris.platform.cms2.constants.Cms2Constants.VERSION_GC_MAX_AGE_DAYS_PROPERTY;
import static de.hybris.platform.cms2.constants.Cms2Constants.VERSION_GC_MAX_NUMBER_VERSIONS_PROPERTY;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.processing.CMSVersionGCProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSVersionGCProcessServiceTest
{
	private static final int MAX_AGE_DAYS = 5;
	private static final int MAX_NUMBER_VERSIONS = 20;

	@InjectMocks
	private DefaultCMSVersionGCProcessService processService;

	@Mock
	private BusinessProcessService businessProcessService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private ModelService modelService;

	@Mock
	private Configuration configuration;

	@Mock
	private CMSVersionGCProcessModel cmsVersionGCProcessModel;

	@Before
	public void setup()
	{
		when(businessProcessService.createProcess(anyString(), anyString())).thenReturn(cmsVersionGCProcessModel);

		when(configurationService.getConfiguration()).thenReturn(configuration);

		when(configuration.getInt(VERSION_GC_MAX_AGE_DAYS_PROPERTY, DEFAULT_VERSION_GC_MAX_AGE_DAYS)).thenReturn(MAX_AGE_DAYS);
		when(configuration.getInt(
				VERSION_GC_MAX_NUMBER_VERSIONS_PROPERTY, DEFAULT_VERSION_GC_MAX_NUMBER_VERSIONS)).thenReturn(MAX_NUMBER_VERSIONS);
	}

	@Test
	public void whenCreateProcessCalledVerifyProcessConfigurationValid()
	{
		// WHEN
		final CMSVersionGCProcessModel result = processService.createProcess();

		// VERIFY
		verify(result).setMaxAgeDays(MAX_AGE_DAYS);
		verify(result).setMaxNumberVersions(MAX_NUMBER_VERSIONS);
		verify(modelService).save(cmsVersionGCProcessModel);
	}

	@Test
	public void whenStartProcessCalledVerifyBusinessProcessStarts()
	{
		// WHEN
		processService.startProcess(cmsVersionGCProcessModel);

		// VERIFY
		verify(businessProcessService).startProcess(cmsVersionGCProcessModel);
	}
}
