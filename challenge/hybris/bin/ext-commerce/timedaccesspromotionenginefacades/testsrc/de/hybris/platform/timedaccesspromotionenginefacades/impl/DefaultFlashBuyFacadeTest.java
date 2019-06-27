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
package de.hybris.platform.timedaccesspromotionenginefacades.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultFlashBuyFacade}
 */
@UnitTest
public class DefaultFlashBuyFacadeTest
{
	private static final String COUPON_CODE = "couponId";
	private static final String PROMOTION_CODE = "promotionCode";

	private DefaultFlashBuyFacade flashBuyFacade;

	@Mock
	private FlashBuyService flashBuyService;

	@Mock
	private ProductData product;
	@Mock
	private PromotionData promotion;
	@Mock
	private FlashBuyCouponModel coupon;

	private Optional<FlashBuyCouponModel> couponOptional;
	private Collection<PromotionData> promotions;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		flashBuyFacade = new DefaultFlashBuyFacade();
		flashBuyFacade.setFlashBuyService(flashBuyService);

		promotions = new ArrayList<PromotionData>(0);
		couponOptional = Optional.of(coupon);

		Mockito.when(product.getPotentialPromotions()).thenReturn(promotions);
		Mockito.when(coupon.getCouponId()).thenReturn(COUPON_CODE);
		Mockito.when(promotion.getCode()).thenReturn(PROMOTION_CODE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPrepareFlashBuyInfoNullArg()
	{
		flashBuyFacade.prepareFlashBuyInfo(null);
	}


	@Test
	public void testPrepareFlashBuyInfo_empty_promotions()
	{
		final String result = flashBuyFacade.prepareFlashBuyInfo(product);
		Assert.assertEquals(StringUtils.EMPTY, result);
	}

	@Test
	public void testPrepareFlashBuyInfo_null_coupon()
	{
		promotions.add(promotion);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(PROMOTION_CODE)).thenReturn(Optional.empty());

		final String result = flashBuyFacade.prepareFlashBuyInfo(product);
		Assert.assertEquals(StringUtils.EMPTY, result);
	}

	@Test
	public void testPrepareFlashBuyInfo_inactivate_coupon()
	{
		promotions.add(promotion);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(PROMOTION_CODE)).thenReturn(couponOptional);
		Mockito.when(coupon.getActive()).thenReturn(Boolean.FALSE);

		final String result = flashBuyFacade.prepareFlashBuyInfo(product);
		Assert.assertEquals(StringUtils.EMPTY, result);
	}

	@Test
	public void testPrepareFlashBuyInfo()
	{
		promotions.add(promotion);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(PROMOTION_CODE)).thenReturn(couponOptional);
		Mockito.when(coupon.getActive()).thenReturn(Boolean.TRUE);

		final String result = flashBuyFacade.prepareFlashBuyInfo(product);
		Assert.assertEquals(COUPON_CODE, result);
	}

}
