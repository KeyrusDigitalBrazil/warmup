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
import static java.lang.Boolean.valueOf;
import static java.lang.Integer.valueOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SingleCodeCouponValidateInterceptorUnitTest
{

	private static final String COUPON_ID = "testCouponId123";

	private SingleCodeCouponValidateInterceptor validator;
	@Mock
	private InterceptorContext ctx;

	@Before
	public void setUp()
	{
		validator = new SingleCodeCouponValidateInterceptor();
		setCouponIdModified(FALSE);
		when(valueOf(ctx.isNew(any(SingleCodeCouponModel.class)))).thenReturn(FALSE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnValidateModelIsNull() throws InterceptorException
	{
		validator.onValidate(null, ctx);
	}

	@Test
	public void testOnValidateMaxRedemptionsPerCustomerIsNull() throws InterceptorException
	{
		final SingleCodeCouponModel model = getSingleCodeCouponModel(valueOf(1), valueOf(2));
		model.setMaxRedemptionsPerCustomer(null);
		validator.onValidate(model, ctx);
	}

	@Test
	public void testOnValidateMaxTotalRedemptionsIsNull() throws InterceptorException
	{
		final SingleCodeCouponModel model = getSingleCodeCouponModel(valueOf(1), valueOf(2));
		model.setMaxTotalRedemptions(null);
		validator.onValidate(model, ctx);
	}

	@Test
	public void testOnValidateBothMaxRedemptionsAreNull() throws InterceptorException
	{
		final SingleCodeCouponModel model = getSingleCodeCouponModel(valueOf(1), valueOf(2));
		model.setMaxTotalRedemptions(null);
		model.setMaxRedemptionsPerCustomer(null);
		validator.onValidate(model, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateWrongMaxRedemptionsPerCustomer() throws InterceptorException
	{
		final SingleCodeCouponModel model = getSingleCodeCouponModel(valueOf(0), valueOf(2));
		validator.onValidate(model, ctx);
	}

	@Test
	public void testOnValidateTrue() throws InterceptorException
	{
		validator.onValidate(getSingleCodeCouponModel(valueOf(1), valueOf(2)), ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateFalse() throws InterceptorException
	{
		validator.onValidate(getSingleCodeCouponModel(valueOf(2), valueOf(1)), ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateFalseWhenActiveAndCouponIdModified() throws InterceptorException
	{
		setCouponIdModified(TRUE);
		validator.onValidate(getSingleCodeCouponModel(valueOf(1), valueOf(2)), ctx);
	}

	@Test
	public void testOnValidateTrueWhenNonActiveAndCouponIdModified() throws InterceptorException
	{
		final SingleCodeCouponModel singleCodeCouponModel = getSingleCodeCouponModel(valueOf(1), valueOf(2));
		singleCodeCouponModel.setActive(FALSE);
		setCouponIdModified(TRUE);

		validator.onValidate(singleCodeCouponModel, ctx);
	}

	private SingleCodeCouponModel getSingleCodeCouponModel(final Integer maxRedemptionsPerCustomer,
			final Integer maxTotalRedemptions)
	{
		final SingleCodeCouponModel model = new SingleCodeCouponModel();
		model.setCouponId(COUPON_ID);
		model.setActive(TRUE);
		model.setMaxRedemptionsPerCustomer(maxRedemptionsPerCustomer);
		model.setMaxTotalRedemptions(maxTotalRedemptions);
		return model;
	}

	private void setCouponIdModified(final Boolean modified)
	{
		when(valueOf(ctx.isModified(any(SingleCodeCouponModel.class), eq(SingleCodeCouponModel.COUPONID)))).thenReturn(modified);
	}

}
