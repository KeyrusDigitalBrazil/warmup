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
package de.hybris.platform.subscriptionservices.subscription.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartStrategy;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceSaveCartResult;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;


/**
 * Subscription specific pre and post hooks for the {@link CommerceSaveCartStrategy#saveCart} method.
 */
public class DefaultSubscriptionCommerceSaveCartHook implements CommerceSaveCartMethodHook
{
	private CartService cartService;
	private ModelService modelService;

	/**
	 * Pre-hook for the {@link CommerceSaveCartStrategy#saveCart} method which validates that only master carts are
	 * saved.
	 *
	 * @param parameters
	 *           {@link CommerceSaveCartParameter} parameter object that holds the cart to be saved along with some
	 *           additional details
	 * @throws CommerceSaveCartException
	 *            if a validation for saving a cart fails
	 */
	@Override
	public void beforeSaveCart(@Nonnull final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{
		validateParameterNotNull(parameters, "parameters cannot be null");
		final CartModel cartToBeSaved = parameters.getCart() == null ? getCartService().getSessionCart() : parameters.getCart();

		if (cartToBeSaved.getParent() != null)
		{
			throw new CommerceSaveCartException("The provided cart [" + cartToBeSaved.getCode()
					+ "] is a child cart. Only master carts can be saved.");
		}
	}

	/**
	 * Post-hook for the {@link CommerceSaveCartStrategy#saveCart} method which takes care of saving the master cart's
	 * child carts.
	 *
	 * @param parameters
	 *           {@link CommerceSaveCartParameter} parameter object that holds the cart to be saved along with some
	 *           additional details
	 * @param saveCartResult
	 *           {@link CommerceSaveCartResult}
	 */
	@Override
	public void afterSaveCart(@Nonnull final CommerceSaveCartParameter parameters,@Nonnull final CommerceSaveCartResult saveCartResult)
	{
		validateParameterNotNull(parameters, "parameters cannot be null");
		validateParameterNotNull(saveCartResult, "saveCartResult cannot be null");
		validateParameterNotNull(saveCartResult.getSavedCart(), "savedCart cannot be null");

		final CartModel masterCart = saveCartResult.getSavedCart();

		if (CollectionUtils.isNotEmpty(masterCart.getChildren()))
		{
			for (final AbstractOrderModel abstractOrder : masterCart.getChildren())
			{
				final CartModel childCart = (CartModel) abstractOrder;
				childCart.setSaveTime(masterCart.getSaveTime());
				childCart.setExpirationTime(masterCart.getExpirationTime());
			}
			getModelService().saveAll(masterCart.getChildren());
			getModelService().refresh(masterCart);
		}
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
