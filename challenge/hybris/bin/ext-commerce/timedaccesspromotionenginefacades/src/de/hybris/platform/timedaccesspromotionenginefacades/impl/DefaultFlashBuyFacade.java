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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.timedaccesspromotionenginefacades.FlashBuyFacade;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link FlashBuyFacade}
 */
public class DefaultFlashBuyFacade implements FlashBuyFacade
{
	private FlashBuyService flashBuyService;
	private ModelService modelService;
	private CartService cartService;

	@Override
	public String prepareFlashBuyInfo(final ProductData product)
	{
		validateParameterNotNullStandardMessage("product", product);

		final List<FlashBuyCouponModel> flashBuyCouponList = new ArrayList<>();

		final Collection<PromotionData> promotionDataList = product.getPotentialPromotions();
		if (CollectionUtils.isNotEmpty(promotionDataList))
		{
			promotionDataList.forEach(p -> getFlashBuyService().getFlashBuyCouponByPromotionCode(p.getCode()).ifPresent(coupon -> {
				if (coupon.getActive())
				{
					flashBuyCouponList.add(coupon);
				}
			}));
		}

		if (CollectionUtils.isNotEmpty(flashBuyCouponList))
		{
			return flashBuyCouponList.get(0).getCouponId();
		}
		return StringUtils.EMPTY;
	}


	@Override
	public void updateFlashBuyStatusForCart()
	{
		final CartModel cart = getCartService().getSessionCart();
		cart.setProcessingFlashBuyOrder(true);
		getModelService().save(cart);
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

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

}
