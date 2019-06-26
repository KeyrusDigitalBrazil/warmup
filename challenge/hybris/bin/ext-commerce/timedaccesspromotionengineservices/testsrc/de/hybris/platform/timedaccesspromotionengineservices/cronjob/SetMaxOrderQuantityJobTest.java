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
package de.hybris.platform.timedaccesspromotionengineservices.cronjob;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.timedaccesspromotionengineservices.daos.FlashBuyDao;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCronJobModel;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
@IntegrationTest
public class SetMaxOrderQuantityJobTest extends ServicelayerTransactionalTest
{

	private static final String COUPON_ID = "couponId7";

	@Resource(name = "setMaxOrderQuantityJob")
	private SetMaxOrderQuantityJob job;

	@Resource
	private FlashBuyDao flashBuyDao;

	@Resource
	private ProductDao productDao;

	@Resource
	private CouponDao couponDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/timedaccesspromotionengineservices/test/FlashBuyTest.impex", "UTF-8");
	}

	@Test
	public void testPerform()
	{
		final FlashBuyCouponModel coupon = (FlashBuyCouponModel) couponDao.findCouponById(COUPON_ID);
		final FlashBuyCronJobModel setMaxQtyCronJob = new FlashBuyCronJobModel();
		setMaxQtyCronJob.setFlashBuyCoupon(coupon);
		final PerformResult result = job.perform(setMaxQtyCronJob);

		Assert.assertEquals(1, coupon.getProduct().getMaxOrderQuantity().intValue());
		Assert.assertEquals(5, coupon.getOriginalMaxOrderQuantity().intValue());

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}
}
