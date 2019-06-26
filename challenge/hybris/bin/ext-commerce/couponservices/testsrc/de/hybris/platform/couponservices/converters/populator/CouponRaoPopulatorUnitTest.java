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
package de.hybris.platform.couponservices.converters.populator;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.couponservices.rao.CouponRAO;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponRaoPopulatorUnitTest
{
	private static final String COUPON_CODE = "testCouponCode";

	@InjectMocks
	private CouponRaoPopulator populator;

	@Mock
	private CouponService couponService;

	@Mock
	private AbstractCouponModel couponModel;

	@Test
	public void testPopulateOk() throws ConversionException
	{
		final CartModel cartModel = new CartModel();
		cartModel.setAppliedCouponCodes(Collections.singleton(COUPON_CODE));

		final SingleCodeCouponModel singleCodeCoupon = new SingleCodeCouponModel();
		singleCodeCoupon.setCouponId(COUPON_CODE);

		when(couponService.getValidatedCouponForCode(Matchers.anyString())).thenReturn(Optional.of(couponModel));
		when(couponModel.getCouponId()).thenReturn(COUPON_CODE);


		final CartRAO cartRao = new CartRAO();

		populator.populate(cartModel, cartRao);

		final CouponRAO resultingCouponRAO = new CouponRAO();
		resultingCouponRAO.setCouponCode(COUPON_CODE);
		resultingCouponRAO.setCouponId(COUPON_CODE);

		assertThat(cartRao.getCoupons()).hasSize(1);
		assertThat(cartRao.getCoupons().get(0)).isEqualTo(resultingCouponRAO);
	}

	@Test(expected = NullPointerException.class)
	public void testPopulateKOmodelMissing()
	{
		populator.populate(null, new CartRAO());
	}

	@Test(expected = NullPointerException.class)
	public void testPopulateKOraoMissing()
	{
		populator.populate(new CartModel(), null);
	}

	@Test
	public void testPopulateWithInvalidCouponCode()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setAppliedCouponCodes(Collections.singleton(COUPON_CODE));

		when(couponService.getValidatedCouponForCode(Matchers.anyString())).thenReturn(Optional.empty());

		final CartRAO cartRao = new CartRAO();

		populator.populate(cartModel, cartRao);

		assertThat(cartRao.getCoupons()).isEmpty();
	}

	@Test
	public void testPopulateWithEmptyCouponCode()
	{
		final CartModel cartModel = new CartModel();
		cartModel.setAppliedCouponCodes(null);

		final CartRAO cartRao = new CartRAO();

		populator.populate(cartModel, cartRao);

		assertTrue(CollectionUtils.isEmpty(cartRao.getCoupons()));
	}
}
