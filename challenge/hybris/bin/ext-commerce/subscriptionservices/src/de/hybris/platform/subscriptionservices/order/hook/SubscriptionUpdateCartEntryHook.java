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
package de.hybris.platform.subscriptionservices.order.hook;

import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handles subscription product quantity change.
 */
public class SubscriptionUpdateCartEntryHook implements CommerceUpdateCartEntryHook
{
	private SubscriptionProductService subscriptionProductService;
	private ModelService modelService;
	private CommerceCartCalculationStrategy calculationStrategy;
	private SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy;

	@Override
	public void beforeUpdateCartEntry(final CommerceCartParameter parameter)
	{
		final CartModel cart = parameter.getCart();
		if (!isMasterCart(cart))
		{
			throw new IllegalArgumentException("Provided cart '" + cart.getCode() + "' is not a master cart");
		}
		final AbstractOrderEntryModel entry = getEntry(cart, parameter.getEntryNumber());
		if (!getSubscriptionProductService().isSubscription(entry.getProduct()))
		{
			return;
		}
		if (parameter.getQuantity() < 0L || parameter.getQuantity() > 1L)
		{
			throw new IllegalArgumentException("Subscription product '" + entry.getProduct().getCode()
					+ "' must have a new quantity of 0 or 1, quantity given: " + parameter.getQuantity());
		}
	}

	@Override
	public void afterUpdateCartEntry(final CommerceCartParameter parameter, final CommerceCartModification result)
	{
		final CartModel cart = parameter.getCart();
		if (!isMasterCart(cart))
		{
			throw new IllegalArgumentException("Provided cart '" + cart.getCode() + "' is not a master cart");
		}
		final AbstractOrderEntryModel entry = result.getEntry();
		if (!getSubscriptionProductService().isSubscription(entry.getProduct()))
		{
			return;
		}
		if (parameter.getQuantity() == 0)
		{
			removeOrphanChildEntries(cart);
			removeEmptyChildCarts(cart);
			normalizeEntriesInChildCarts(cart);
		}
		else
		{
			if (entry.getChildEntries() != null)
			{
				entry.getChildEntries().forEach(childEntry ->
				{
					childEntry.setQuantity(Long.valueOf(parameter.getQuantity()));
					childEntry.setCalculated(Boolean.FALSE);
				});
			}
		}
		if (cart.getChildren() != null)
		{
			cart.getChildren().forEach(child -> {
				final CommerceCartParameter calculationParameter = new CommerceCartParameter();
				calculationParameter.setCart((CartModel) child);
				getCalculationStrategy().calculateCart(calculationParameter);
			});
		}
	}

	protected void normalizeEntriesInChildCarts(final CartModel cart)
	{
		for (final AbstractOrderModel childCart : cart.getChildren())
		{
			getSubscriptionCommerceCartStrategy().normalizeEntryNumbers((CartModel) childCart);
		}
	}

	protected AbstractOrderEntryModel getEntry(@Nonnull final CartModel cart, final long entryNumber)
	{
		if (CollectionUtils.isEmpty(cart.getEntries()))
		{
			throw new IllegalArgumentException("Cart " + cart.getCode() + " has no entries");
		}
		return cart.getEntries().stream()
				.filter(e -> e.getEntryNumber() != null)
				.filter(e -> entryNumber == e.getEntryNumber().longValue())
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Entry #" + entryNumber + " was not found in cart " + cart.getCode()));
	}

	protected void removeEmptyChildCarts(@Nonnull final CartModel cartModel)
	{
		if (CollectionUtils.isEmpty(cartModel.getChildren()))
		{
			return;
		}
		cartModel.getChildren().stream()
				.peek(getModelService()::refresh)
				.filter(childCart -> CollectionUtils.isEmpty(childCart.getEntries()))
				.forEach(getModelService()::remove);
		getModelService().refresh(cartModel);
		cartModel.setCalculated(Boolean.FALSE);
	}

	/**
	 * {@link SubscriptionUpdateCartEntryHook#afterUpdateCartEntry(CommerceCartParameter, CommerceCartModification)} gets
	 * a fake entry, because the original one is already removed at that moment.
	 * <p>
	 * So the fake instance does not have any child entries. Here we search within the child carts what entries have got
	 * lost its {@link AbstractOrderEntryModel#getMasterEntry()}. That would mean the master entry has been removed, and
	 * the child entry should be removed as well.
	 * </p>
	 * <p>
	 * WARNING Be careful optimizing the refresh calls - the system is very sensitive to it.
	 * </p>
	 *
	 * @param cartModel
	 *           master cart
	 */
	protected void removeOrphanChildEntries(@Nonnull final CartModel cartModel)
	{
		if (CollectionUtils.isEmpty(cartModel.getChildren()))
		{
			return;
		}
		cartModel.getChildren().stream()
				.map(AbstractOrderModel::getEntries)
				.flatMap(Collection::stream)
				.peek(getModelService()::refresh)
				.filter(entry -> entry.getMasterEntry() == null)
				.peek(getModelService()::remove)
				.peek(entry -> getModelService().refresh(entry.getOrder()))
				.forEach(entry -> entry.getOrder().setCalculated(Boolean.FALSE));
	}

	protected boolean isMasterCart(final CartModel cartModel)
	{
		return cartModel != null && cartModel.getParent() == null;
	}

	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
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

	protected CommerceCartCalculationStrategy getCalculationStrategy()
	{
		return calculationStrategy;
	}

	@Required
	public void setCalculationStrategy(final CommerceCartCalculationStrategy calculationStrategy)
	{
		this.calculationStrategy = calculationStrategy;
	}

	protected SubscriptionCommerceCartStrategy getSubscriptionCommerceCartStrategy()
	{
		return subscriptionCommerceCartStrategy;
	}

	@Required
	public void setSubscriptionCommerceCartStrategy(final SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy)
	{
		this.subscriptionCommerceCartStrategy = subscriptionCommerceCartStrategy;
	}
}
