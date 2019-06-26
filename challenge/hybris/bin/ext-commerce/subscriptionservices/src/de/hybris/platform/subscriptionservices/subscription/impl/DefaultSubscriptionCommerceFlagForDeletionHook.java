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

import de.hybris.platform.commerceservices.order.CommerceFlagForDeletionStrategy;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.commerceservices.order.hook.CommerceFlagForDeletionMethodHook;
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
 * Subscription specific pre and post hooks for the {@link CommerceFlagForDeletionStrategy#flagForDeletion} method.
 */

public class DefaultSubscriptionCommerceFlagForDeletionHook implements CommerceFlagForDeletionMethodHook
{
	private CartService cartService;
	private ModelService modelService;

	/**
	 * Pre-hook for the {@link de.hybris.platform.commerceservices.order.CommerceFlagForDeletionStrategy#flagForDeletion}
	 * method which validates that only master carts are flagged for deletion.
	 *
	 * @param parameters
	 *           {@link CommerceSaveCartParameter} parameter object that holds the cart to be saved along with some
	 *           additional details
	 * @throws CommerceSaveCartException
	 *            if a validation for saving a cart fails
	 */
	@Override
	public void beforeFlagForDeletion(@Nonnull final CommerceSaveCartParameter parameters) throws CommerceSaveCartException
	{
		validateParameterNotNull(parameters, "parameters cannot be null");
		validateParameterNotNull(parameters.getCart(), "cart to be flagged for deletion cannot be null");

		final CartModel cartToFlg = parameters.getCart();

		if (cartToFlg.getParent() != null)
		{
			throw new CommerceSaveCartException("The provided cart [" + cartToFlg.getCode()
					+ "] is a child cart. Only master carts can be saved.");
		}
	}

	/**
	 * Post-hook for the
	 * {@link de.hybris.platform.commerceservices.order.CommerceFlagForDeletionStrategy#flagForDeletion} method which
	 * takes care of flagging cart for deletion for the master cart's child carts.
	 *
	 * @param parameters
	 *           {@link CommerceSaveCartParameter} parameter object that holds the cart to be flagged for deletion along
	 *           with some additional details
	 * @param flaggedCartResult
	 *           {@link CommerceSaveCartResult}
	 */
	@Override
	public void afterFlagForDeletion(final CommerceSaveCartParameter parameters, final CommerceSaveCartResult flaggedCartResult)
	{
		validateParameterNotNull(flaggedCartResult, "flaggedCartResult cannot be null");

		final CartModel masterCart = flaggedCartResult.getSavedCart();

		if (masterCart != null && CollectionUtils.isNotEmpty(masterCart.getChildren()))
		{
			for (final AbstractOrderModel abstractOrder : masterCart.getChildren())
			{
				final CartModel childCart = (CartModel) abstractOrder;
				childCart.setSaveTime(null);
				childCart.setExpirationTime(null);
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
