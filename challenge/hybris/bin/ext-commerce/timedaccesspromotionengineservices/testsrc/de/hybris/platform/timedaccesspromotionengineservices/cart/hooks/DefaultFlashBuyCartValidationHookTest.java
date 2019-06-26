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
package de.hybris.platform.timedaccesspromotionengineservices.cart.hooks;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.couponservices.service.data.CouponResponse;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.AbstractPromotion;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultFlashBuyCartValidationHook}
 */
@UnitTest
public class DefaultFlashBuyCartValidationHookTest
{

	private static final String FLASHBUY_INVALID = "flashbuyinvalid";
	private static final String COUPON_CODE = "testcouponid";
	private static final String FLASHBUY_COUPON_CODE = "testflashbuycouponid";
	private static final String PROMOTION_CODE = "promotioncode";


	private DefaultFlashBuyCartValidationHook hook;

	@Mock
	private CouponDao couponDao;
	@Mock
	private CartService cartService;
	@Mock
	private CouponService couponService;
	@Mock
	private FlashBuyService flashBuyService;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private ModelService modelService;

	@Mock
	private CartModel cart;
	@Mock
	private CommerceCartParameter parameter;
	@Mock
	private FlashBuyCouponModel flashBuyCoupon;
	@Mock
	private MultiCodeCouponModel multiCodeCoupon;
	@Mock
	private AbstractPromotionModel promotionModel;
	@Mock
	private ProductModel product;
	@Mock
	private CartEntryModel cartEntry;
	@Mock
	private PromotionOrderResults promotionOrderResults;
	@Mock
	private AbstractPromotion promotion;
	@Mock
	private PromotionResult promotionResult;

	private List<PromotionResult> appliedProductPromotions;
	private Collection<String> couponCodes;
	private Collection<String> flashBuyCouponCodes;
	private List<CommerceCartModification> modifications;
	private CommerceCartModification commerceCartModification;
	private CommerceCartModification commerceCartModification2;
	private CouponResponse response;
	private Optional<FlashBuyCouponModel> flashBuyCouponOptional;
	private Optional<ProductModel> productOptional;
	private List<CartEntryModel> cartEntries;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		appliedProductPromotions = Collections.singletonList(promotionResult);

		hook = new DefaultFlashBuyCartValidationHook();
		hook.setCouponDao(couponDao);
		hook.setCartService(cartService);
		hook.setCouponService(couponService);
		hook.setFlashBuyService(flashBuyService);
		hook.setPromotionsService(promotionsService);
		hook.setModelService(modelService);

		modifications = new ArrayList<>(0);
		commerceCartModification = new CommerceCartModification();
		commerceCartModification2 = new CommerceCartModification();
		couponCodes = Collections.singletonList(COUPON_CODE);
		flashBuyCouponCodes = Collections.singletonList(FLASHBUY_COUPON_CODE);
		response = new CouponResponse();
		flashBuyCouponOptional = Optional.of(flashBuyCoupon);
		productOptional = Optional.of(product);
		cartEntries = Collections.singletonList(cartEntry);

		Mockito.when(parameter.getCart()).thenReturn(cart);
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(false);
		Mockito.when(cart.getAppliedCouponCodes()).thenReturn(couponCodes);
		Mockito.when(flashBuyCoupon.getProduct()).thenReturn(product);

		Mockito.when(promotionsService.getPromotionResults(cart)).thenReturn(promotionOrderResults);
		Mockito.when(promotionOrderResults.getAppliedProductPromotions()).thenReturn(appliedProductPromotions);
		Mockito.when(promotionResult.getPromotion()).thenReturn(promotion);
		Mockito.when(promotion.getCode()).thenReturn(PROMOTION_CODE);
		Mockito.when(flashBuyService.getPromotionByCode(PROMOTION_CODE)).thenReturn(promotionModel);
		Mockito.when(couponDao.findCouponById(COUPON_CODE)).thenReturn(flashBuyCoupon);


		Mockito.when(promotionResult.getPromotion()).thenReturn(promotion);
		Mockito.when(promotion.getCode()).thenReturn(PROMOTION_CODE);
		Mockito.when(flashBuyService.getPromotionByCode(PROMOTION_CODE)).thenReturn(promotionModel);
		Mockito.when(promotionModel.getCode()).thenReturn(PROMOTION_CODE);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(PROMOTION_CODE)).thenReturn(flashBuyCouponOptional);
		Mockito.when(flashBuyCoupon.getCouponId()).thenReturn(FLASHBUY_COUPON_CODE);
		Mockito.when(couponService.redeemCoupon(FLASHBUY_COUPON_CODE, cart)).thenReturn(response);
		Mockito.when(couponService.validateCouponCode(FLASHBUY_COUPON_CODE, null)).thenReturn(response);
	}


	@Test
	public void testBeforeValidateCart_checkout()
	{
		response.setSuccess(false);
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(false);
		Mockito.when(cartService.getEntriesForProduct(cart, product)).thenReturn(cartEntries);

		hook.beforeValidateCart(parameter, modifications);

		Mockito.verify(couponService).releaseCouponCode(FLASHBUY_COUPON_CODE, cart);
		Assert.assertFalse(CollectionUtils.isEmpty(modifications));
		Assert.assertEquals(FLASHBUY_INVALID, modifications.get(0).getStatusCode());
		Assert.assertEquals(cartEntry, modifications.get(0).getEntry());
	}

	@Test
	public void testBeforeValidateCart_non_flashBuyCouppon_checkout()
	{
		response.setSuccess(false);
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(false);
		Mockito.when(couponDao.findCouponById(COUPON_CODE)).thenReturn(multiCodeCoupon);
		Mockito.when(flashBuyService.getProductForPromotion(promotionModel)).thenReturn(productOptional);
		Mockito.when(cartService.getEntriesForProduct(cart, product)).thenReturn(cartEntries);

		hook.beforeValidateCart(parameter, modifications);

		Mockito.verify(couponService, Mockito.times(0)).releaseCouponCode(Mockito.any(String.class), Mockito.any(CartModel.class));
		Assert.assertFalse(CollectionUtils.isEmpty(modifications));
		Assert.assertEquals(FLASHBUY_INVALID, modifications.get(0).getStatusCode());
		Assert.assertEquals(cartEntry, modifications.get(0).getEntry());
	}

	@Test
	public void testBeforeValidateCart_non_flashBuyPromotion_checkout()
	{
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(false);
		Mockito.when(promotionsService.getPromotionResults(cart)).thenReturn(promotionOrderResults);
		Mockito.when(promotionOrderResults.getAppliedProductPromotions()).thenReturn(Collections.emptyList());
		hook.beforeValidateCart(parameter, modifications);

		Mockito.verify(couponService).releaseCouponCode(FLASHBUY_COUPON_CODE, cart);
		Assert.assertTrue(CollectionUtils.isEmpty(modifications));
	}

	@Test
	public void testBeforeValidateCart_coupon_apply_success_checkout()
	{
		response.setSuccess(true);
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(false);

		hook.beforeValidateCart(parameter, modifications);

		Mockito.verify(couponService).releaseCouponCode(FLASHBUY_COUPON_CODE, cart);
		Mockito.verify(flashBuyService).getFlashBuyCouponByPromotionCode(Mockito.any(String.class));
		Assert.assertTrue(CollectionUtils.isEmpty(modifications));
	}

	@Test
	public void testBeforeValidateCart_non_flashCoupon_placeorder()
	{
		response.setSuccess(false);
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(true);
		Mockito.when(couponDao.findCouponById(COUPON_CODE)).thenReturn(multiCodeCoupon);
		Mockito.when(flashBuyService.getFlashBuyCouponByPromotionCode(PROMOTION_CODE)).thenReturn(Optional.empty());

		hook.beforeValidateCart(parameter, modifications);

		Assert.assertTrue(CollectionUtils.isEmpty(modifications));
	}

	@Test
	public void testBeforeValidateCart_flashCoupon_placeorder()
	{
		response.setSuccess(true);
		Mockito.when(cart.isProcessingFlashBuyOrder()).thenReturn(true);
		Mockito.when(cart.getAppliedCouponCodes()).thenReturn(flashBuyCouponCodes);
		Mockito.when(couponDao.findCouponById(FLASHBUY_COUPON_CODE)).thenReturn(flashBuyCoupon);

		hook.beforeValidateCart(parameter, modifications);

		Assert.assertTrue(CollectionUtils.isEmpty(modifications));
	}

	@Test
	public void testafterValidateCart()
	{
		commerceCartModification.setStatusCode(FLASHBUY_INVALID);
		commerceCartModification.setStatusCode("couponNotValid");
		modifications.add(commerceCartModification);

		hook.afterValidateCart(parameter, modifications);
		Assert.assertEquals(1, (modifications).size());
	}
}
