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
package de.hybris.platform.timedaccesspromotionengineservices.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.CronJobDao;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.JobDao;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.daos.FlashBuyDao;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * * Test class for testing the functionality of the ResetMaxOrderQuantityJob.
 *
 */
@IntegrationTest
public class DefaultFlashBuyServicesIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String RESET_MAX_ORDER_QUANTITY_JOB_CODE = "resetMaxOrderQuantityJob";
	private static final String SET_MAX_ORDER_QUANTITY_JOB_CODE = "setMaxOrderQuantityJob";
	private static final String COUPON_NO_RULE = "couponId2";
	private static final String COUPON_RULE_UNPUBLISH = "couponId3";
	private static final String COUPON_RULE_NO_PRODUCT = "couponId4";
	private static final String COUPON_INACTIVE = "couponId5";
	private static final String COUPON_TWO_PRODUCTS = "couponId6";

	@Resource
	private JobDao jobDao;
	@Resource
	private CronJobDao cronJobDao;
	@Resource
	private FlashBuyDao flashBuyDao;
	@Resource
	private ModelService modelService;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private CouponDao couponDao;
	@Resource
	private FlashBuyService flashBuyService;
	@Resource
	private RuleMaintenanceService ruleMaintenanceService;


	@Before
	public void setUp() throws Exception
	{
		importCsv("/timedaccesspromotionengineservices/test/FlashBuyTest.impex", "UTF-8");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateCronJobForFlashBuyCoupon_coupon_null()
	{
		flashBuyService.createCronJobForFlashBuyCoupon(null);
	}

	@Test
	public void testCreateCronJobForFlashBuyCoupon_coupon_inactive()
	{
		executeTestData(COUPON_INACTIVE);
	}

	@Test
	public void testCreateCronJobForFlashBuyCoupon_coupon_no_enddate()
	{
		testCronJobForNoEndDateFlashBuyCoupon(COUPON_TWO_PRODUCTS);
	}

	@Test
	public void testCreateCronJobForFlashBuyCoupon_no_rule()
	{
		executeTestData(COUPON_NO_RULE);
	}

	@Test
	public void testCreateCronJobForFlashBuyCoupon_rule_unpublished()
	{
		executeTestData(COUPON_RULE_UNPUBLISH);
	}

	@Test
	public void testCreateCronJobForFlashBuyCoupon_rule_without_product()
	{
		executeTestData(COUPON_RULE_NO_PRODUCT);
	}

	@Test
	public void testCreateCronJobForFlashBuyCoupon()
	{
		final FlashBuyCouponModel flashBuyCoupon = (FlashBuyCouponModel) couponDao.findCouponById(COUPON_TWO_PRODUCTS);
		final DateTime dt = new DateTime(Calendar.getInstance().getTime());
		DateTime startDate = dt.plusDays(1);
		final DateTime endDate = dt.plusDays(10);
		flashBuyCoupon.setStartDate(startDate.toDate());
		flashBuyCoupon.setEndDate(endDate.toDate());
		flashBuyService.createCronJobForFlashBuyCoupon(flashBuyCoupon);

		final List<CronJobModel> resetQtyCronJobs = cronJobDao.findCronJobs(flashBuyCoupon.getCouponId()
				+ RESET_MAX_ORDER_QUANTITY_JOB_CODE);
		Assert.assertEquals(1, resetQtyCronJobs.size());
		final List<CronJobModel> setQtyCronJobs = cronJobDao.findCronJobs(flashBuyCoupon.getCouponId()
				+ SET_MAX_ORDER_QUANTITY_JOB_CODE);
		Assert.assertEquals(1, setQtyCronJobs.size());

		final List<TriggerModel> resetQtytriggerModels = resetQtyCronJobs.get(0).getTriggers();
		Assert.assertEquals(1, resetQtytriggerModels.size());
		final List<TriggerModel> setQtytriggerModels = setQtyCronJobs.get(0).getTriggers();
		Assert.assertEquals(1, setQtytriggerModels.size());

		final String resetQtyCxpress = resetQtytriggerModels.get(0).getCronExpression();
		final String expectedResetQtyCronExpress = String.format("%d %d %d %d %d ? %d", endDate.getSecondOfMinute(),
				endDate.getMinuteOfHour(), endDate.getHourOfDay(), endDate.getDayOfMonth(), endDate.getMonthOfYear(),
				endDate.getYear());
		Assert.assertTrue(resetQtyCxpress.equals(expectedResetQtyCronExpress));

		startDate = startDate.minusSeconds(5);
		final String setQtyCxpress = setQtytriggerModels.get(0).getCronExpression();
		final String expectedSetQtyCronExpress = String.format("%d %d %d %d %d ? %d", startDate.getSecondOfMinute(),
				startDate.getMinuteOfHour(), startDate.getHourOfDay(), startDate.getDayOfMonth(), startDate.getMonthOfYear(),
				startDate.getYear());
		Assert.assertTrue(setQtyCxpress.equals(expectedSetQtyCronExpress));
	}

	private void executeTestData(final String couponId)
	{
		final FlashBuyCouponModel flashBuyCoupon = (FlashBuyCouponModel) couponDao.findCouponById(couponId);
		flashBuyService.createCronJobForFlashBuyCoupon(flashBuyCoupon);

		final List<CronJobModel> setQtyCronJobs = cronJobDao.findCronJobs(flashBuyCoupon.getCouponId()
				+ SET_MAX_ORDER_QUANTITY_JOB_CODE);
		Assert.assertEquals(0, setQtyCronJobs.size());

		final List<CronJobModel> resetQtyCronJobs = cronJobDao.findCronJobs(flashBuyCoupon.getCouponId()
				+ RESET_MAX_ORDER_QUANTITY_JOB_CODE);
		Assert.assertEquals(0, resetQtyCronJobs.size());
	}

	private void testCronJobForNoEndDateFlashBuyCoupon(final String couponId)
	{
		final FlashBuyCouponModel flashBuyCoupon = (FlashBuyCouponModel) couponDao.findCouponById(couponId);
		flashBuyCoupon.setEndDate(null);
		flashBuyService.createCronJobForFlashBuyCoupon(flashBuyCoupon);
		final List<CronJobModel> resetQtyCronJobs = cronJobDao.findCronJobs(flashBuyCoupon.getCouponId()
				+ RESET_MAX_ORDER_QUANTITY_JOB_CODE);
		Assert.assertEquals(1, resetQtyCronJobs.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPerformFlashBuyCronJob_coupon_null()
	{
		flashBuyService.performFlashBuyCronJob(null);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testPerformFlashBuyCronJob_no_cronjob()
	{
		final FlashBuyCouponModel flashBuyCoupon = new FlashBuyCouponModel();
		flashBuyCoupon.setCouponId("testcode");
		flashBuyService.performFlashBuyCronJob(flashBuyCoupon);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetProductForPromotion_without_param()
	{
		flashBuyService.getProductForPromotion(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFlashBuyCouponByPromotionCode_without_param()
	{
		flashBuyService.getFlashBuyCouponByPromotionCode(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPromotionSourceRulesByProductCode_without_param()
	{
		flashBuyService.getPromotionSourceRulesByProductCode(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUndeployFlashBuyPromotion_without_param()
	{
		flashBuyService.undeployFlashBuyPromotion(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAllProductsByPromotionSourceRule_null()
	{
		flashBuyService.getAllProductsByPromotionSourceRule(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFlashBuyCouponByProduct_null()
	{
		flashBuyService.getFlashBuyCouponByProduct(null);
	}

}
