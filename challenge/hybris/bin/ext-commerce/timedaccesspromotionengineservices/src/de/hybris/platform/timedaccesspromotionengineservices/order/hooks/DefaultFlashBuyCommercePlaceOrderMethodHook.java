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
package de.hybris.platform.timedaccesspromotionengineservices.order.hooks;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.couponservices.redemption.strategies.CouponRedemptionStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Applys flash buy coupon after placing order
 */
public class DefaultFlashBuyCommercePlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private FlashBuyService flashBuyService;
	private CouponRedemptionStrategy<FlashBuyCouponModel> couponRedemptionStrategy;

	/**
	 * Stops the completed flash buy, which is executed after placing order
	 *
	 * @param parameter
	 *           the information for checkout
	 * @param result
	 *           the order model
	 */
	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		final OrderModel order = result.getOrder();

		order.getEntries()
				.stream()
				.forEach(
						orderEntry -> {
							final List<PromotionSourceRuleModel> promotionSourceRules = getFlashBuyService()
									.getPromotionSourceRulesByProductCode(orderEntry.getProduct().getCode());
							if (CollectionUtils.isNotEmpty(promotionSourceRules))
							{
								promotionSourceRules.stream().forEach(
										promotionSourceRule -> {
											final Optional<FlashBuyCouponModel> flashBuyCoupon = getFlashBuyService()
													.getFlashBuyCouponByPromotionCode(promotionSourceRule.getCode());
											if (flashBuyCoupon.isPresent()
													&& isFlashBuyCouponCompleted(promotionSourceRule.getCode(), flashBuyCoupon.get()))
											{
												getFlashBuyService().performFlashBuyCronJob(flashBuyCoupon.get());
											}
										});
							}
						});

	}

	protected boolean isFlashBuyCouponCompleted(final String code, final FlashBuyCouponModel flashBuyCoupon)
	{
		validateParameterNotNull(code, "Code must not be null");

		return !getCouponRedemptionStrategy().isCouponRedeemable(flashBuyCoupon, null, flashBuyCoupon.getCouponId());
	}


	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		//empty
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		//empty
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

	protected CouponRedemptionStrategy<FlashBuyCouponModel> getCouponRedemptionStrategy()
	{
		return couponRedemptionStrategy;
	}

	@Required
	public void setCouponRedemptionStrategy(final CouponRedemptionStrategy<FlashBuyCouponModel> couponRedemptionStrategy)
	{
		this.couponRedemptionStrategy = couponRedemptionStrategy;
	}

}
