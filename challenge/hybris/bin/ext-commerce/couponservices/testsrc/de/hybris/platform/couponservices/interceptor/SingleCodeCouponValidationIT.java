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
package de.hybris.platform.couponservices.interceptor;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.valueOf;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Calendar;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class SingleCodeCouponValidationIT extends ServicelayerTest
{

	private static final String COUPON_ID = "testCouponId123";

	@Resource
	private ModelService modelService;

	private SingleCodeCouponModel couponModel;

	@Before
	public void setUp()
	{
		couponModel = getSingleCodeCouponModel(valueOf(10), valueOf(20));
	}

	@Test
	public void testSave()
	{
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyWithWrongEndDate()
	{
		modelService.save(couponModel);
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		couponModel.setEndDate(yesterday.getTime());
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveNewWithWrongEndDate()
	{
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		couponModel.setEndDate(yesterday.getTime());
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveWithStartDateAfterEndDate()
	{
		final Calendar today = Calendar.getInstance();
		final Calendar startDate = (Calendar) today.clone();
		startDate.add(Calendar.DAY_OF_YEAR, 20);
		final Calendar endDate = (Calendar) today.clone();
		endDate.add(Calendar.DAY_OF_YEAR, 10);

		couponModel.setStartDate(startDate.getTime());
		couponModel.setEndDate(endDate.getTime());
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyCouponIdWhenActive()
	{
		modelService.save(couponModel);
		couponModel.setCouponId("newCouponId");
		modelService.save(couponModel);
	}

	@Test
	public void testModifyCouponIdWhenNonActive()
	{
		modelService.save(couponModel);
		couponModel.setActive(FALSE);
		couponModel.setCouponId("newCouponId");
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveWrongMaxRedemptionsPerCustomer()
	{
		couponModel.setMaxRedemptionsPerCustomer(valueOf(0));
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveWrongMaxTotalRedemptions()
	{
		couponModel.setMaxTotalRedemptions(valueOf(0));
		modelService.save(couponModel);
	}

	@Test
	public void testSaveBothMaxRedemptionsAreNull()
	{
		couponModel.setMaxTotalRedemptions(null);
		couponModel.setMaxRedemptionsPerCustomer(null);

		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveRedemtionsPerCustomerBiggerTotalRedemtions()
	{
		couponModel.setMaxRedemptionsPerCustomer(valueOf(11));
		couponModel.setMaxTotalRedemptions(valueOf(10));
		modelService.save(couponModel);
	}

	private SingleCodeCouponModel getSingleCodeCouponModel(final Integer maxRedemptionsPerCustomer,
			final Integer maxTotalRedemptions)
	{
		final SingleCodeCouponModel model = new SingleCodeCouponModel();
		model.setCouponId(COUPON_ID);
		model.setActive(TRUE);
		model.setMaxRedemptionsPerCustomer(maxRedemptionsPerCustomer);
		model.setMaxTotalRedemptions(maxTotalRedemptions);
		final Calendar today = Calendar.getInstance();
		final Calendar startDate = (Calendar) today.clone();
		startDate.add(Calendar.DAY_OF_YEAR, -10);
		model.setStartDate(startDate.getTime());
		final Calendar endDate = (Calendar) today.clone();
		endDate.add(Calendar.DAY_OF_YEAR, 10);
		model.setEndDate(endDate.getTime());

		return model;
	}
}
