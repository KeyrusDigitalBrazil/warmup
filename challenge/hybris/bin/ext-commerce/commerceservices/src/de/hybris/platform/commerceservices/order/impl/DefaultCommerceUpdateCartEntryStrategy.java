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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceUpdateCartEntryStrategy;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.commerceservices.order.strategies.EntryMergeStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCommerceUpdateCartEntryStrategy extends AbstractCommerceCartStrategy
		implements CommerceUpdateCartEntryStrategy
{
	private List<CommerceUpdateCartEntryHook> commerceUpdateCartEntryHooks;
	private ConfigurationService configurationService;
	private EntryMergeStrategy entryMergeStrategy;

	@Override
	public CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException
	{
		beforeUpdateCartEntry(parameters);
		final CartModel cartModel = parameters.getCart();
		final long newQuantity = parameters.getQuantity();
		final long entryNumber = parameters.getEntryNumber();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		CommerceCartModification modification;

		final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) entryNumber);
		validateEntryBeforeModification(newQuantity, entryToUpdate);
		final Integer maxOrderQuantity = entryToUpdate.getProduct().getMaxOrderQuantity();
		// Work out how many we want to add (could be negative if we are
		// removing items)
		final long quantityToAdd = newQuantity - entryToUpdate.getQuantity().longValue();

		// So now work out what the maximum allowed to be added is (note that
		// this may be negative!)
		final long actualAllowedQuantityChange = getAllowedCartAdjustmentForProduct(cartModel, entryToUpdate.getProduct(),
				quantityToAdd, entryToUpdate.getDeliveryPointOfService());
		//Now do the actual cartModification
		modification = modifyEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity);
		afterUpdateCartEntry(parameters, modification);
		return modification;

	}

	@Override
	public CommerceCartModification updatePointOfServiceForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException
	{
		final CartModel cartModel = parameters.getCart();
		final PointOfServiceModel pointOfServiceModel = parameters.getPointOfService();
		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(pointOfServiceModel, "PointOfService Model cannot be null");

		final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) parameters.getEntryNumber());

		if (entryToUpdate == null)
		{
			throw new CommerceCartModificationException("Unknown entry number");
		}

		if (!isOrderEntryUpdatable(entryToUpdate))
		{
			throw new CommerceCartModificationException("Entry is not updatable");
		}

		final AbstractOrderEntryModel mergeTarget = getEntryMergeStrategy().getEntryToMerge(cartModel.getEntries(), entryToUpdate);
		if (mergeTarget != null)
		{
			return mergeEntries(entryToUpdate, mergeTarget, cartModel);
		}
		else
		{
			final CommerceCartModification modification = new CommerceCartModification();
			final long stockLevel = getAvailableStockLevel(entryToUpdate.getProduct(), parameters.getPointOfService());
			if (stockLevel == 0)
			{
				final AbstractOrderEntryModel cloneEntry = getModelService().clone(entryToUpdate);
				getModelService().detach(cloneEntry);
				getModelService().remove(entryToUpdate);
				modification.setEntry(cloneEntry);
				modification.setQuantityAdded(-cloneEntry.getQuantity().longValue());
				modification.setQuantity(0);
				modification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
			}
			else if (stockLevel < entryToUpdate.getQuantity().longValue())
			{
				entryToUpdate.setQuantity(Long.valueOf(stockLevel));
				entryToUpdate.setDeliveryPointOfService(pointOfServiceModel);
				getModelService().save(entryToUpdate);
				modification.setEntry(entryToUpdate);
				modification.setQuantity(stockLevel);
				modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			}
			else
			{
				entryToUpdate.setDeliveryPointOfService(pointOfServiceModel);
				getModelService().save(entryToUpdate);
				modification.setEntry(entryToUpdate);
				modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			}
			getModelService().refresh(cartModel);
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(parameter);
			getModelService().refresh(entryToUpdate);
			modification.setEntry(entryToUpdate);
			return modification;
		}
	}

	@Override
	public CommerceCartModification updateToShippingModeForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException
	{
		final CartModel cartModel = parameters.getCart();

		final AbstractOrderEntryModel entryToUpdate = getEntryForNumber(cartModel, (int) parameters.getEntryNumber());
		final CommerceCartModification modification = new CommerceCartModification();

		validateEntryBeforeSetShippingMode(entryToUpdate);

		final AbstractOrderEntryModel matchingShippingModeEntry = getEntryMergeStrategy().getEntryToMerge(cartModel.getEntries(),
				entryToUpdate);
		if (matchingShippingModeEntry != null)
		{
			return mergeEntries(entryToUpdate, matchingShippingModeEntry, cartModel);
		}
		else
		{
			final long quantityBeforeChange = entryToUpdate.getQuantity().longValue();
			final long stockLevel = getAvailableStockLevel(entryToUpdate.getProduct(), parameters.getPointOfService());
			// Now limit that to the total available in stock
			final long newTotalQuantityAfterStockLimit = Math.min(quantityBeforeChange, stockLevel);

			entryToUpdate.setDeliveryPointOfService(null);
			entryToUpdate.setQuantity(Long.valueOf(newTotalQuantityAfterStockLimit));
			getModelService().save(entryToUpdate);
			getModelService().refresh(cartModel);
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(parameter);

			if (quantityBeforeChange == newTotalQuantityAfterStockLimit)
			{
				modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			}
			else
			{
				modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			}
			modification.setQuantity(entryToUpdate.getQuantity().longValue());
			modification.setEntry(entryToUpdate);
		}

		return modification;
	}

	protected CommerceCartModification mergeEntries(final AbstractOrderEntryModel mergeCandidate,
			final AbstractOrderEntryModel mergeTarget, final CartModel cart) throws CommerceCartModificationException
	{
		final long quantityForMerge = mergeTarget.getQuantity().intValue() + (long) mergeCandidate.getQuantity().intValue();
		getModelService().remove(mergeCandidate);

		final CommerceCartParameter updateQuantityParams = new CommerceCartParameter();
		updateQuantityParams.setEnableHooks(true);
		updateQuantityParams.setCart(cart);
		updateQuantityParams.setEntryNumber(mergeTarget.getEntryNumber().longValue());
		updateQuantityParams.setQuantity(quantityForMerge);

		return updateQuantityForCartEntry(updateQuantityParams);
	}


	protected void validateEntryBeforeSetShippingMode(final AbstractOrderEntryModel entryToUpdate)
			throws CommerceCartModificationException
	{
		if (entryToUpdate == null)
		{
			throw new CommerceCartModificationException("Unknown entry number");
		}

		if (!isOrderEntryUpdatable(entryToUpdate))
		{
			throw new CommerceCartModificationException("Entry is not updatable");
		}

		if (entryToUpdate.getDeliveryPointOfService() == null)
		{
			throw new CommerceCartModificationException("Entry is already in shipping mode");
		}
	}

	protected void validateEntryBeforeModification(final long newQuantity, final AbstractOrderEntryModel entryToUpdate)
			throws CommerceCartModificationException
	{
		if (newQuantity < 0)
		{
			throw new CommerceCartModificationException("New quantity must not be less than zero");
		}

		if (entryToUpdate == null)
		{
			throw new CommerceCartModificationException("Unknown entry number");
		}

		if (!isOrderEntryUpdatable(entryToUpdate))
		{
			throw new CommerceCartModificationException("Entry is not updatable");
		}
	}

	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity)
	{
		// Now work out how many that leaves us with on this entry
		final long entryNewQuantity = entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange;

		final ModelService modelService = getModelService();

		if (entryNewQuantity <= 0)
		{
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
			entry.setOrder(cartModel);
			entry.setEntryGroupNumbers(new HashSet<>(entryToUpdate.getEntryGroupNumbers()));
			entry.setProduct(entryToUpdate.getProduct());

			// The allowed new entry quantity is zero or negative
			// just remove the entry
			modelService.remove(entryToUpdate);
			modelService.refresh(cartModel);
			normalizeEntryNumbers(cartModel);

			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(parameter);

			// Return an empty modification
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setEntry(entry);
			modification.setQuantity(0);
			// We removed all the quantity from this row
			modification.setQuantityAdded(-entryToUpdate.getQuantity().longValue());

			if (newQuantity == 0)
			{
				modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			}
			else
			{
				modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			}

			return modification;
		}
		else
		{
			// Adjust the entry quantity to the new value
			entryToUpdate.setQuantity(Long.valueOf(entryNewQuantity));
			modelService.save(entryToUpdate);
			modelService.refresh(cartModel);
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			getCommerceCartCalculationStrategy().calculateCart(parameter);
			modelService.refresh(entryToUpdate);

			// Return the modification data
			final CommerceCartModification modification = new CommerceCartModification();
			modification.setQuantityAdded(actualAllowedQuantityChange);
			modification.setEntry(entryToUpdate);
			modification.setQuantity(entryNewQuantity);

			if (isMaxOrderQuantitySet(maxOrderQuantity) && entryNewQuantity == maxOrderQuantity.longValue())
			{
				modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
			}
			else if (newQuantity == entryNewQuantity)
			{
				modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			}
			else
			{
				modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
			}

			return modification;
		}
	}

	protected void beforeUpdateCartEntry(final CommerceCartParameter parameter)
	{
		if (getCommerceUpdateCartEntryHooks() != null && (parameter.isEnableHooks() && getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.UPDATECARTENTRYHOOK_ENABLED, true)))
		{
			for (final CommerceUpdateCartEntryHook commerceUpdateCartEntryHook : getCommerceUpdateCartEntryHooks())
			{
				commerceUpdateCartEntryHook.beforeUpdateCartEntry(parameter);
			}
		}
	}

	protected void afterUpdateCartEntry(final CommerceCartParameter parameter, final CommerceCartModification result)
	{
		if (getCommerceUpdateCartEntryHooks() != null && parameter.isEnableHooks() && getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.UPDATECARTENTRYHOOK_ENABLED, true))
		{
			for (final CommerceUpdateCartEntryHook commerceUpdateCartEntryHook : getCommerceUpdateCartEntryHooks())
			{
				commerceUpdateCartEntryHook.afterUpdateCartEntry(parameter, result);
			}
		}
	}

	protected List<CommerceUpdateCartEntryHook> getCommerceUpdateCartEntryHooks()
	{
		return commerceUpdateCartEntryHooks;
	}

	public void setCommerceUpdateCartEntryHooks(final List<CommerceUpdateCartEntryHook> commerceUpdateCartEntryHooks)
	{
		this.commerceUpdateCartEntryHooks = commerceUpdateCartEntryHooks;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
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
