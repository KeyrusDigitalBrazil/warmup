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
package de.hybris.platform.couponfacades.converters.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.coupon.data.CouponData;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Junit Test Suite for {@link CouponDataPopulator }
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponDataPopulatorTest
{

	private final static String COUPON_CODE = "couponCode";

	@InjectMocks
	private CouponDataPopulator couponDataPopulator;

	private CouponData couponData;

	private AbstractCouponModel couponModel;

	@Before
	public void setUp()
	{
		couponData = new CouponData();

		final ItemContextBuilder builder = ItemContextBuilder.createDefaultBuilder(AbstractCouponModel.class);
		builder.setLocaleProvider(new StubLocaleProvider(Locale.ENGLISH));
		couponModel = new AbstractCouponModel(builder.build());

		couponModel.setCouponId(COUPON_CODE);
		couponModel.setActive(Boolean.TRUE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull()
	{
		couponDataPopulator.populate(null, couponData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		couponDataPopulator.populate(couponModel, null);
	}

	@Test
	public void testPopulate()
	{
		couponDataPopulator.populate(couponModel, couponData);

		assertEquals(COUPON_CODE, couponData.getCouponId());
		assertTrue(couponData.isActive());
	}
}
