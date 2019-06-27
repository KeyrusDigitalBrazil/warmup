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

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.y2ysync.services.SyncConfigService;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class Y2YStreamConfigurationContainerPrepareTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;

	@Resource
	private SyncConfigService syncConfigService;
	private Y2YStreamConfigurationContainerModel testContainer;

	@Before
	public void setUp() throws Exception
	{
		testContainer = syncConfigService.createStreamConfigurationContainer("testContainer");
	}

	@Test
	public void shouldGenerateFeedAndPool()
	{
		testContainer = syncConfigService.createStreamConfigurationContainer("testContainer");

		modelService.save(testContainer);

		assertThat(testContainer.getFeed()).isEqualTo("testContainer_feed");
		assertThat(testContainer.getPool()).isEqualTo("testContainer_pool");
	}

	@Test
	public void shouldNotGenerateFeedAndPoolIfExplicitlySet()
	{
		testContainer = syncConfigService.createStreamConfigurationContainer("testContainer");
		testContainer.setFeed("customFeed");
		testContainer.setPool("customPool");

		modelService.save(testContainer);

		assertThat(testContainer.getFeed()).isEqualTo("customFeed");
		assertThat(testContainer.getPool()).isEqualTo("customPool");
	}

}
