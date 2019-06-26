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
package de.hybris.platform.customerinterestsservices.productinterest.cronjob;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ExpiredInterestsCleanUpJobIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource
	private ExpiredInterestsCleanUpJob expiredInterestsCleanUpJob;

	@Before
	public void setup() throws IOException, ImpExException
	{
		importCsv("/customerinterestsservices/test/impex/customerinterestsservices-test-data.impex", "utf-8");
		Assert.assertEquals(1, expiredInterestsCleanUpJob.getProductInterestDao().findExpiredProductInterests().size());
	}

	@Test
	public void testBackInStockProductsSize()
	{
		final PerformResult result = expiredInterestsCleanUpJob.perform(new CronJobModel());
		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Assert.assertEquals(0, expiredInterestsCleanUpJob.getProductInterestDao().findExpiredProductInterests().size());
	}

}