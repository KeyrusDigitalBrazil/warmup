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
package de.hybris.platform.commerceservices.order.impl;

import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.fest.util.Collections;
import org.springframework.beans.factory.annotation.Required;


public class DefaultCommerceAddToCartStrategy extends AbstractCommerceAddToCartStrategy
{
	private EntryMergeStrategy entryMergeStrategy;

	@Override
	public List<CommerceCartModification> addToCart(final List<CommerceCartParameter> parameterList)
			throws CommerceCartMergingException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		try
		{
			if (!Collections.isEmpty(parameterList))
			{
				// add items to cart
				final Map<CommerceCartParameter, CommerceCartModification> paramModificationMap = new HashMap<>();
				for (final CommerceCartParameter parameter : parameterList)
				{
					final CommerceCartModification modification = doAddToCart(parameter);
					paramModificationMap.put(parameter, modification);
					modifications.add(modification);
				}

				// calculate the cart. Using recalculateCart() to force calculate the entries
				getCommerceCartCalculationStrategy().recalculateCart(parameterList.get(0));

				// after add to cart
				for (final Entry<CommerceCartParameter, CommerceCartModification> entry : paramModificationMap.entrySet())
				{
					final CommerceCartParameter storedParameter = entry.getKey();
					final CommerceCartModification storedModification = entry.getValue();
					afterAddToCart(storedParameter, storedModification);
					mergeEntry(storedModification, storedParameter);
				}
			}
		}
		catch (final CommerceCartModificationException e)
		{
			throw new CommerceCartMergingException(e.getMessage(), e);
		}
		return modifications;
	}

	/**
	 * Adds an item to the cart for pickup in a given location
	 *
	 * @param parameter
	 *           Cart parameters
	 * @return Cart modification information
	 * @throws de.hybris.platform.commerceservices.order.CommerceCartModificationException
	 *
	 */
	@Override
	public CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final CommerceCartModification modification = doAddToCart(parameter);
		afterAddToCart(parameter, modification);
		// Here the entry is fully populated, so we can search for a similar one and merge.
		mergeEntry(modification, parameter);
		getCommerceCartCalculationStrategy().calculateCart(parameter);
		return modification;
	}

	/**
	 * Do add to cart.
	 *
	 * @param parameter
	 *           the parameter
	 * @return the commerce cart modification
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	protected CommerceCartModification doAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		CommerceCartModification modification;

		final CartModel cartModel = parameter.getCart();
		final ProductModel productModel = parameter.getProduct();
		final long quantityToAdd = parameter.getQuantity();
		final PointOfServiceModel deliveryPointOfService = parameter.getPointOfService();

		this.beforeAddToCart(parameter);
		validateAddToCart(parameter);

		if (isProductForCode(parameter).booleanValue())
		{
			// So now work out what the maximum allowed to be added is (note that this may be negative!)
			final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, productModel, quantityToAdd,
					deliveryPointOfService);
			final Integer maxOrderQuantity = productModel.getMaxOrderQuantity();
			final long cartLevel = checkCartLevel(productModel, cartModel, deliveryPointOfService);
			final long cartLevelAfterQuantityChange = actualAllowedQuantityChange + cartLevel;

			if (actualAllowedQuantityChange > 0)
			{
				// We are allowed to add items to the cart
				final CartEntryModel entryModel = addCartEntry(parameter, actualAllowedQuantityChange);
				getModelService().save(entryModel);

				final String statusCode = getStatusCodeAllowedQuantityChange(actualAllowedQuantityChange, maxOrderQuantity,
						quantityToAdd, cartLevelAfterQuantityChange);

				modification = createAddToCartResp(parameter, statusCode, entryModel, actualAllowedQuantityChange);
			}
			else
			{
				// Not allowed to add any quantity, or maybe even asked to reduce the quantity
				// Do nothing!
				final String status = getStatusCodeForNotAllowedQuantityChange(maxOrderQuantity, maxOrderQuantity);

				modification = createAddToCartResp(parameter, status, createEmptyCartEntry(parameter), 0);

			}
		}
		else
		{
			modification = createAddToCartResp(parameter, CommerceCartModificationStatus.UNAVAILABLE,
					createEmptyCartEntry(parameter), 0);
		}

		return modification;
	}

	protected Boolean isProductForCode(final CommerceCartParameter parameter)
	{

		final ProductModel productModel = parameter.getProduct();
		try
		{
			getProductService().getProductForCode(productModel.getCode());
		}
		catch (final UnknownIdentifierException e)
		{
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	protected CommerceCartModification createAddToCartResp(final CommerceCartParameter parameter, final String status,
			final CartEntryModel entry, final long quantityAdded)
	{
		final long quantityToAdd = parameter.getQuantity();

		final CommerceCartModification modification = new CommerceCartModification();
		modification.setStatusCode(status);
		modification.setQuantityAdded(quantityAdded);
		modification.setQuantity(quantityToAdd);

		modification.setEntry(entry);

		return modification;
	}

	protected UnitModel getUnit(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final ProductModel productModel = parameter.getProduct();
		try
		{
			return getProductService().getOrderableUnit(productModel);
		}
		catch (final ModelNotFoundException e)
		{
			throw new CommerceCartModificationException(e.getMessage(), e);
		}
	}

	protected CartEntryModel addCartEntry(final CommerceCartParameter parameter, final long actualAllowedQuantityChange)
			throws CommerceCartModificationException
	{
		if (parameter.getUnit() == null)
		{
			parameter.setUnit(getUnit(parameter));
		}

		final CartEntryModel cartEntryModel = getCartService().addNewEntry(parameter.getCart(), parameter.getProduct(),
				actualAllowedQuantityChange, parameter.getUnit(), APPEND_AS_LAST, false);
		cartEntryModel.setDeliveryPointOfService(parameter.getPointOfService());

		return cartEntryModel;
	}

	protected void mergeEntry(@Nonnull
	final CommerceCartModification modification, @Nonnull
	final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("modification", modification);
		if (modification.getEntry() == null || Objects.equals(modification.getEntry().getQuantity(), Long.valueOf(0L)))
		{
			// nothing to merge
			return;
		}
		ServicesUtil.validateParameterNotNullStandardMessage("parameter", parameter);
		if (parameter.isCreateNewEntry())
		{
			return;
		}
		final AbstractOrderModel cart = modification.getEntry().getOrder();
		if (cart == null)
		{
			// The entry is not in cart (most likely it's a stub)
			return;
		}
		final AbstractOrderEntryModel mergeTarget = getEntryMergeStrategy().getEntryToMerge(cart.getEntries(),
				modification.getEntry());
		if (mergeTarget == null)
		{
			if (parameter.getEntryNumber() != CommerceCartParameter.DEFAULT_ENTRY_NUMBER)
			{
				throw new CommerceCartModificationException(
						"The new entry can not be merged into the entry #" + parameter.getEntryNumber() + ". Give a correct value or "
								+ CommerceCartParameter.DEFAULT_ENTRY_NUMBER + " to accept any suitable entry.");
			}
		}
		else
		{
			// Merge the original entry into the merge target and remove the original entry.
			final Map<Integer, Long> entryQuantities = new HashMap<>(2);
			entryQuantities.put(mergeTarget.getEntryNumber(),
					Long.valueOf(modification.getEntry().getQuantity().longValue() + mergeTarget.getQuantity().longValue()));
			entryQuantities.put(modification.getEntry().getEntryNumber(), Long.valueOf(0L));
			getCartService().updateQuantities(parameter.getCart(), entryQuantities);
			modification.setEntry(mergeTarget);
		}

	}

	protected String getStatusCodeAllowedQuantityChange(final long actualAllowedQuantityChange, final Integer maxOrderQuantity,
			final long quantityToAdd, final long cartLevelAfterQuantityChange)
	{
		// Are we able to add the quantity we requested?
		if (isMaxOrderQuantitySet(maxOrderQuantity) && (actualAllowedQuantityChange < quantityToAdd)
				&& (cartLevelAfterQuantityChange == maxOrderQuantity.longValue()))
		{
			return CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED;
		}
		else if (actualAllowedQuantityChange == quantityToAdd)
		{
			return CommerceCartModificationStatus.SUCCESS;
		}
		else
		{
			return CommerceCartModificationStatus.LOW_STOCK;
		}
	}

	protected String getStatusCodeForNotAllowedQuantityChange(final Integer maxOrderQuantity,
			final Integer cartLevelAfterQuantityChange)
	{

		if (isMaxOrderQuantitySet(maxOrderQuantity) && (cartLevelAfterQuantityChange.longValue() == maxOrderQuantity.longValue()))
		{
			return CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED;
		}
		else
		{
			return CommerceCartModificationStatus.NO_STOCK;
		}
	}

	protected CartEntryModel createEmptyCartEntry(final CommerceCartParameter parameter)
	{

		final ProductModel productModel = parameter.getProduct();
		final PointOfServiceModel deliveryPointOfService = parameter.getPointOfService();

		final CartEntryModel entry = new CartEntryModel()
		{
			@Override
			public Double getBasePrice()
			{
				return null;
			}

			@Override
			public Double getTotalPrice()
			{
				return null;
			}
		};
		entry.setProduct(productModel);
		entry.setDeliveryPointOfService(deliveryPointOfService);

		return entry;
	}

	protected EntryMergeStrategy getEntryMergeStrategy()
	{
		return entryMergeStrategy;
	}

	@Required
	public void setEntryMergeStrategy(final EntryMergeStrategy entryMergeStrategy)
	{
		this.entryMergeStrategy = entryMergeStrategy;
	}
}
