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
package de.hybris.platform.stocknotificationservices.cronjob;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;



@IntegrationTest
public class StockLevelStatusJobIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private StockLevelStatusJob stockLevelStatusJob;

	@Before
	public void setup() throws IOException, ImpExException
	{
		importCsv("/stocknotificationservices/test/impex/stocklevelstatusjob-test-data.impex", "utf-8");
	}

	@Test
	public void testBackInStockProductsSize()
	{
		final SyncTaskExecutor syncTaskExecutor = new SyncTaskExecutor();

		stockLevelStatusJob.setTaskExecutor(syncTaskExecutor);

		final PerformResult result = stockLevelStatusJob.perform(new CronJobModel());

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		final List<ProductInterestModel> interests = stockLevelStatusJob.getInStockProductInterests();
		Assert.assertEquals(5, interests.size());
		Assert.assertTrue(interests.stream().allMatch(interest -> interest.getExpiryDate().before(new Date())));
	}

}
