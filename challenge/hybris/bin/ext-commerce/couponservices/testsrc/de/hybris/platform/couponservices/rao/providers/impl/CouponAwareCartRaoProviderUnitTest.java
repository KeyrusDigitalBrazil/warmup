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
package de.hybris.platform.couponservices.rao.providers.impl;

import static java.util.Collections.singleton;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.couponservices.converters.populator.CouponRaoPopulator;
import de.hybris.platform.couponservices.model.SingleCodeCouponModel;
import de.hybris.platform.couponservices.rao.CouponRAO;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.couponservices.util.CouponAwareCartTestContextBuilder;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderConsumedRAO;
import de.hybris.platform.ruleengineservices.rao.providers.impl.DefaultCartRAOProviderUnitTest;
import de.hybris.platform.ruleengineservices.ruleengine.impl.CartTestContextBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponAwareCartRaoProviderUnitTest extends DefaultCartRAOProviderUnitTest
{
	private final static String COUPON_CODE = "testCouponCode";

	@Mock
	private CouponService couponService;

	@Before
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		final CouponCartRaoExtractor couponCartRaoExtractor = new CouponCartRaoExtractor();
		getCartRAOProvider().setFactExtractorList(Collections.singletonList(couponCartRaoExtractor));
		getCartRAOProvider().afterPropertiesSet();

		final SingleCodeCouponModel singleCodeCoupon = new SingleCodeCouponModel();
		singleCodeCoupon.setCouponId(COUPON_CODE);

		when(couponService.getValidatedCouponForCode(Matchers.anyString())).thenReturn(Optional.of(singleCodeCoupon));
	}

	@Test
	public void testExpandFactModelExpandWithCoupons()
	{
		final CartTestContextBuilder contextBuilder = ((CouponAwareCartTestContextBuilder) getCartTestContextBuilder())
				.withCouponCodes(singleton(COUPON_CODE));

		final Set<?> facts = getCartRAOProvider().expandFactModel(contextBuilder.getCartModel(),
				singleton(CouponCartRaoExtractor.EXPAND_COUPONS));

		final CouponRAO expectedCouponRAO = new CouponRAO();
		expectedCouponRAO.setCouponCode(COUPON_CODE);
		expectedCouponRAO.setCouponId(COUPON_CODE);

		final CartRAO cartRAO = contextBuilder.getCartRAO();
		cartRAO.setCode(contextBuilder.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, expectedCouponRAO);
	}

	@Test
	public void testExpandFactModelExpand()
	{
		final CartTestContextBuilder contextBuilder = ((CouponAwareCartTestContextBuilder) getCartTestContextBuilder())
				.withCouponCodes(singleton(COUPON_CODE));

		final Set<?> facts = getCartRAOProvider().expandFactModel(contextBuilder.getCartModel());

		final CouponRAO expectedCouponRAO = new CouponRAO();
		expectedCouponRAO.setCouponCode(COUPON_CODE);
		expectedCouponRAO.setCouponId(COUPON_CODE);

		final CartRAO cartRAO = contextBuilder.getCartRAO();
		cartRAO.setCode(contextBuilder.getCartModel().getCode());
		assertThat(facts).isNotEmpty().containsOnly(cartRAO, expectedCouponRAO);
	}

	@Override
	protected CartTestContextBuilder createNewCartTestContextBuilder()
	{
		return new CouponAwareCartTestContextBuilder();
	}

	@Override
	protected List<Populator<AbstractOrderModel, CartRAO>> getCartPopulators()
	{
		final List<Populator<AbstractOrderModel, CartRAO>> superPopulators = super.getCartPopulators();
		final CouponRaoPopulator couponRaoPopulator = new CouponRaoPopulator();
		couponRaoPopulator.setCouponService(couponService);

		final List<Populator<AbstractOrderModel, CartRAO>> populators = new ArrayList<>();
		populators.addAll(superPopulators);
		populators.add(couponRaoPopulator);
		return populators;
	}

}
