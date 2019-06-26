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
package de.hybris.platform.customercouponservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Unit test for {@link DefaultCouponCampaignURLGenerationStrategy}
 */
@UnitTest
public class DefaultGenerateCouponClaimingURLStrategyTest
{

	private static final String COUPON_ID = "test";
	private static final String URL_PREFIX_KEY = "coupon.claiming.url.prefix";
	private static final String URL_PREFIX = "/url/";
	private static final String DEFAULT_VAL = StringUtils.EMPTY;

	@Spy
	private final DefaultCouponCampaignURLGenerationStrategy strategy = new DefaultCouponCampaignURLGenerationStrategy();

	@Mock
	private CustomerCouponModel coupon;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(coupon.getCouponId()).thenReturn(COUPON_ID);
		Mockito.doReturn(URL_PREFIX).when(strategy).getUrlPrefix();
	}

	@Test
	public void testGenerate()
	{
		final String url = strategy.generate(coupon);
		Assert.assertEquals(URL_PREFIX + COUPON_ID, url);
	}

	@Test
	public void testGenerate_null_param()
	{
		final String url = strategy.generate(null);
		Assert.assertTrue(StringUtils.isBlank(url));
	}

	@Test
	public void testGenerate_null_prefix()
	{
		Mockito.doReturn(null).when(strategy).getUrlPrefix();
		final String url = strategy.generate(coupon);
		Assert.assertTrue(StringUtils.isBlank(url));
	}

	@Test
	public void testGenerate_null_couponId()
	{
		Mockito.when(coupon.getCouponId()).thenReturn(null);
		final String url = strategy.generate(coupon);
		Assert.assertTrue(StringUtils.isBlank(url));
	}
}
