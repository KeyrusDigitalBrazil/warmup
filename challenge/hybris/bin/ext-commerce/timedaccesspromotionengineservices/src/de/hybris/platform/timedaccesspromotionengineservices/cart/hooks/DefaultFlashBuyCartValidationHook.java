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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.strategies.hooks.CartValidationHook;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.services.CouponService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Applies flash buy coupon before checkout
 */
public class DefaultFlashBuyCartValidationHook implements CartValidationHook
{

	private static final String FLASHBUY_INVALID = "flashbuyinvalid";
	private static final String COUPON_NOT_VALID = "couponNotValid";

	private CouponDao couponDao;
	private CartService cartService;
	private CouponService couponService;
	private FlashBuyService flashBuyService;
	private ModelService modelService;
	private PromotionsService promotionsService;

	/**
	 * Checks if flash buy coupon is redeemable, which is executed before the cart validation
	 *
	 * @param parameter
	 *           the information for validation
	 * @param modifications
	 *           list containing the validation results
	 */
	@Override
	public void beforeValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)
	{
		final CartModel cart = parameter.getCart();
		final List<FlashBuyCouponModel> appliedFlashBuyCoupons = getFlashBuyCouponByAppliedCouponCodes(cart);
		final List<FlashBuyCouponModel> potentialFlashBuyCoupons = getFlashBuyCouponByAppliedPromotions(cart);

		if (!(cart.isProcessingFlashBuyOrder()))
		{
			appliedFlashBuyCoupons.forEach(c -> getCouponService().releaseCouponCode(c.getCouponId(), cart));

			potentialFlashBuyCoupons.forEach(c -> {
				if (!getCouponService().redeemCoupon(c.getCouponId(), cart).getSuccess())
				{
					addFlashBuyInvalidModification(cart, c.getProduct(), modifications);
				}
			});
		}
		else
		{
			appliedFlashBuyCoupons.forEach(c -> {
				if (!getCouponService().validateCouponCode(c.getCouponId(), null).getSuccess())
				{
					addFlashBuyInvalidModification(cart, c.getProduct(), modifications);
				}
			});

			if (!potentialFlashBuyCoupons.isEmpty())
			{
				potentialFlashBuyCoupons.forEach(c -> {
					if ((CollectionUtils.isEmpty(appliedFlashBuyCoupons) || !appliedFlashBuyCoupons.contains(c))
							&& !getCouponService().redeemCoupon(c.getCouponId(), cart).getSuccess())
					{
						addFlashBuyInvalidModification(cart, c.getProduct(), modifications);
					}
				});
				cart.setCalculated(Boolean.FALSE);
			}

			cart.setProcessingFlashBuyOrder(false);
			getModelService().save(cart);
		}
	}

	/**
	 * Removes unnessary modification message, which is executed after the cart validation
	 *
	 * @param parameter
	 *           the information for validation
	 * @param modifications
	 *           list containing the validation results
	 */
	@Override
	public void afterValidateCart(final CommerceCartParameter parameter, final List<CommerceCartModification> modifications)//NOSONAR
	{
		removeFlashBuyCouponModification(modifications);
	}

	protected void removeFlashBuyCouponModification(final List<CommerceCartModification> modifications)
	{
		if (modifications.stream().filter(value -> FLASHBUY_INVALID.equals(value.getStatusCode())).findFirst().isPresent())
		{
			modifications.removeIf(value -> COUPON_NOT_VALID.equals(value.getStatusCode()));
		}
	}

	protected List<FlashBuyCouponModel> getFlashBuyCouponByAppliedCouponCodes(final CartModel cart)
	{
		final Collection<String> couponCodes = cart.getAppliedCouponCodes();
		final List<FlashBuyCouponModel> flashBuyCouponList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(couponCodes))
		{
			couponCodes.forEach(code -> {
				final AbstractCouponModel coupon = getCouponDao().findCouponById(code);
				if (coupon instanceof FlashBuyCouponModel)
				{
					flashBuyCouponList.add((FlashBuyCouponModel) coupon);
				}
			});
		}

		return flashBuyCouponList;
	}

	protected List<FlashBuyCouponModel> getFlashBuyCouponByAppliedPromotions(final CartModel cart)
	{
		final List<PromotionResult> appliedProductPromotions = getPromotionsService().getPromotionResults(cart)
				.getAppliedProductPromotions();

		final List<PromotionResult> appliedOrderPromotions = getPromotionsService().getPromotionResults(cart)
				.getAppliedOrderPromotions();

		final List<PromotionResult> appliedPromotions = new ArrayList();
		appliedPromotions.addAll(appliedProductPromotions);
		appliedPromotions.addAll(appliedOrderPromotions);
		
		if (CollectionUtils.isNotEmpty(appliedPromotions))
		{
			return appliedPromotions.stream()
					.map(p -> getFlashBuyService().getFlashBuyCouponByPromotionCode(p.getPromotion().getCode()))
					.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	protected void addFlashBuyInvalidModification(final CartModel cart, final ProductModel product,
			final List<CommerceCartModification> modifications)
	{
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setStatusCode(FLASHBUY_INVALID);
		modification.setEntry(getCartService().getEntriesForProduct(cart, product).get(0));
		modifications.add(modification);
	}

	protected CouponDao getCouponDao()
	{
		return couponDao;
	}

	@Required
	public void setCouponDao(final CouponDao couponDao)
	{
		this.couponDao = couponDao;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected CouponService getCouponService()
	{
		return couponService;
	}

	@Required
	public void setCouponService(final CouponService couponService)
	{
		this.couponService = couponService;
	}

	protected FlashBuyService getFlashBuyService()
	{
		return flashBuyService;
	}

	@Required
	public void setFlashBuyService(final FlashBuyService flashBuyService)
	{
		this.flashBuyService = flashBuyService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

}
