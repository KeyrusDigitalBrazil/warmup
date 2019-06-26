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
package de.hybris.y2ysync.model;

import static junit.framework.TestCase.fail;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Utilities;
import de.hybris.y2ysync.services.SyncConfigService;
import de.hybris.y2ysync.services.SyncExecutionService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


public class Y2YStreamConfigurationContainerRemoveInterceptorTest extends ServicelayerBaseTest
{
	@Resource
	private SyncConfigService syncConfigService;
	@Resource
	private SyncExecutionService syncExecutionService;
	@Resource
	private ModelService modelService;
	private Y2YStreamConfigurationContainerModel container;

	@Before
	public void setUp() throws Exception
	{
		container = syncConfigService.createStreamConfigurationContainer("TEST_STREAM");
        modelService.save(container);
	}

	@Test
	public void shouldPreventRemovingContainerIfItHasConnectedJobs() throws Exception
	{
		// given
		final Y2YSyncJobModel testJob = syncExecutionService.createSyncJobForDataHub("TEST_JOB", container);
        modelService.save(testJob);

		try
		{
			// when
			modelService.remove(container);
			fail("Should throw ModelRemovalException");
		}
		catch (final ModelRemovalException e)
		{
			// then
            assertThat(e.getCause()).isInstanceOf(InterceptorException.class);
			assertThat(modelService.isRemoved(container)).isFalse();
			assertThat(modelService.isRemoved(testJob)).isFalse();
		}
	}

    @Test
    public void shouldAllowRemoveContainerIfItHasNoJobsConnected() throws Exception
    {
    	// when
        modelService.remove(container);

    	// then
        assertThat(modelService.isRemoved(container)).isTrue();
    }

}
