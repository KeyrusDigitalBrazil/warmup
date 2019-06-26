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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.timedaccesspromotionengineservices.daos.FlashBuyDao;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCronJobModel;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * * Test class for testing the functionality of the ResetMaxOrderQuantityJob.
 *
 */
@IntegrationTest
public class ResetMaxOrderQuantityJobTest extends ServicelayerTransactionalTest
{
	private FlashBuyCronJobModel resetMaxOrderQuantityCronJobModel;
	private PerformResult result;
	private static final String COUPON_NO_RULE = "couponId2";
	private static final String COUPON_RULE_UNPUBLISH = "couponId3";
	private static final String COUPON_RULE_NO_PRODUCT = "couponId4";

	@Resource
	private ResetMaxOrderQuantityJob resetMaxOrderQuantityJob;
	@Resource
	private FlashBuyDao flashBuyDao;
	@Resource
	private ProductDao productDao;
	@Resource
	private ModelService modelService;
	@Resource
	private CouponDao couponDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/timedaccesspromotionengineservices/test/FlashBuyTest.impex", "UTF-8");
	}

	/**
	 * Test that if there is no promotion rule, the job can be performed successfully
	 */
	@Test
	public void testPerform_No_PromotionRule()
	{
		executeTestData(COUPON_NO_RULE);
	}

	/**
	 * Test that if the promotion rule is unpublished, the job can be performed successfully
	 */
	@Test
	public void testPerform_PromotionRule_Unpublished()
	{
		executeTestData(COUPON_RULE_UNPUBLISH);
	}

	/**
	 * Test that if there is no product, the job can be performed successfully
	 */
	@Test
	public void testPerform_PromotionRule_No_Product()
	{
		executeTestData(COUPON_RULE_NO_PRODUCT);
	}


	private void executeTestData(final String couponId)
	{
		final FlashBuyCouponModel flashBuyCoupon = (FlashBuyCouponModel) couponDao.findCouponById(couponId);
		resetMaxOrderQuantityCronJobModel = new FlashBuyCronJobModel();
		resetMaxOrderQuantityCronJobModel.setFlashBuyCoupon(flashBuyCoupon);
		result = resetMaxOrderQuantityJob.perform(resetMaxOrderQuantityCronJobModel);
		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

}
