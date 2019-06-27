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
package de.hybris.platform.acceleratorservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.order.AcceleratorCheckoutService;
import de.hybris.platform.acceleratorservices.store.pickup.PickupPointOfServiceConsolidationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.commerceservices.storefinder.data.PointOfServiceDistanceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Accelerator specific implementation of {@link DefaultCommerceCheckoutService}
 */
public class DefaultAcceleratorCheckoutService implements AcceleratorCheckoutService
{
	private PickupPointOfServiceConsolidationStrategy pickupPointOfServiceConsolidationStrategy;
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private ModelService modelService;
	private CommerceStockService commerceStockService;
	private BaseStoreService baseStoreService;

	@Override
	public List<PointOfServiceDistanceData> getConsolidatedPickupOptions(final CartModel cartModel)
	{
		validateParameterNotNull(cartModel, "CartModel cannot be null");
		return getPickupPointOfServiceConsolidationStrategy().getConsolidationOptions(cartModel);
	}

	@Override
	public List<CommerceCartModification> consolidateCheckoutCart(final CartModel cartModel,
			final PointOfServiceModel consolidatedPickupPointModel) throws CommerceCartModificationException
	{
		validateParameterNotNull(cartModel, "CartModel cannot be null");
		validateParameterNotNull(consolidatedPickupPointModel, "PointOfServiceModel cannot be null");

		boolean needRefreshCart = false;
		final List<AbstractOrderEntryModel> entriesToBeRemovedDueToPOS = new ArrayList<>();
		final List<AbstractOrderEntryModel> consolidatedEntriesToBeRemoved = new ArrayList<>();
		final List<CommerceCartModification> unsuccessfulModifications = new ArrayList<>();
		final List<AbstractOrderEntryModel> entriesToConsolidate = getEntriesToConsolidate(cartModel, consolidatedPickupPointModel,
				entriesToBeRemovedDueToPOS, consolidatedEntriesToBeRemoved, unsuccessfulModifications);

		// consolidate entries
		for (final AbstractOrderEntryModel entryToConsolidate : entriesToConsolidate)
		{
			// update quantity for consolidated anchor entry
			final CommerceCartParameter parameter = createCartParameter(cartModel, entryToConsolidate,
					calculateProductQtyInCart(entryToConsolidate.getProduct(), cartModel));
			final CommerceCartModification updateQtyModification = getCommerceCartService().updateQuantityForCartEntry(parameter);
			if (!CommerceCartModificationStatus.SUCCESS.equals(updateQtyModification.getStatusCode()))
			{
				unsuccessfulModifications.add(updateQtyModification);
			}
		}

		// remove entries that are consolidated
		for (final AbstractOrderEntryModel entryToRemove : consolidatedEntriesToBeRemoved)
		{
			getModelService().remove(entryToRemove);
			needRefreshCart = true;
		}

		// remove entries that product is not available in the POS
		for (final AbstractOrderEntryModel entryToRemove : entriesToBeRemovedDueToPOS)
		{
			final AbstractOrderEntryModel clone = getModelService().clone(entryToRemove);
			getModelService().detach(clone);
			getModelService().remove(entryToRemove);
			needRefreshCart = true;

			// add modification
			final CommerceCartModification noStockModification = new CommerceCartModification();
			noStockModification.setEntry(clone);
			noStockModification.setQuantity(clone.getQuantity().longValue());
			noStockModification.setQuantityAdded(-clone.getQuantity().longValue());
			noStockModification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
			unsuccessfulModifications.add(noStockModification);
		}

		// refresh cart if any entry was removed
		if (needRefreshCart)
		{
			getModelService().refresh(cartModel);
		}
		return unsuccessfulModifications;
	}

	protected CommerceCartParameter createCartParameter(final CartModel cartModel, final AbstractOrderEntryModel entryToRemove,
			final long qty)
	{
		final CommerceCartParameter removeEntryParameter = new CommerceCartParameter();
		removeEntryParameter.setEnableHooks(true);
		removeEntryParameter.setCart(cartModel);
		removeEntryParameter.setEntryNumber(entryToRemove.getEntryNumber().longValue());
		removeEntryParameter.setQuantity(qty);
		return removeEntryParameter;
	}

	/**
	 * Gets the consolidate entries.
	 *
	 * @param cartModel
	 *           the cart model
	 * @param consolidatedPickupPointModel
	 *           the consolidated pickup point model
	 * @param entriesToBeRemovedDueToPOS
	 *           the entries to be removed due to out of stock in POS
	 * @param consolidatedEntriesToBeRemoved
	 *           the entries that has the same product but different POS from consolidate POS.
	 * @param unsuccessfulModifications
	 *           the unsuccessful modifications
	 * @return the entries to consolidate. Key: the entry that has the same POS as consolidate POS. Value: the entries
	 *         that has the same product as the entry in the key, but different POS.
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	protected List<AbstractOrderEntryModel> getEntriesToConsolidate(final CartModel cartModel,
			final PointOfServiceModel consolidatedPickupPointModel, final List<AbstractOrderEntryModel> entriesToBeRemovedDueToPOS,
			final List<AbstractOrderEntryModel> consolidatedEntriesToBeRemoved,
			final List<CommerceCartModification> unsuccessfulModifications) throws CommerceCartModificationException
	{
		final List<AbstractOrderEntryModel> entriesToConsolidate = new ArrayList<>();
		for (final AbstractOrderEntryModel entry : cartModel.getEntries())
		{
			if (entry.getDeliveryPointOfService() != null)
			{
				// entry POS is the same as consolidate POS
				if (entry.getDeliveryPointOfService().equals(consolidatedPickupPointModel))
				{
					// if no anchor entry in the map, add it
					if (!entriesToConsolidate.contains(entry))
					{
						entriesToConsolidate.add(entry);
					}
				}
				else
				{
					// entry POS is not the same as consolidate POS
					final AbstractOrderEntryModel anchorEntryToConsolidate = getExistingAnchorEntryByProduct(entry.getProduct(),
							entriesToConsolidate);
					if (anchorEntryToConsolidate != null)
					{
						consolidatedEntriesToBeRemoved.add(entry);
					}
					else
					{
						final AbstractOrderEntryModel anchorEntry = getAnchorEntryToConsolidate(entry, cartModel,
								consolidatedPickupPointModel);
						if (anchorEntry == null)
						{
							// if there is no entry that has the same POS as consolidate POS in the cart, check current entry stock level in the consolidate POS. If in stock, update the entry POS to consolidate POS and use it as anchor entry.
							if (isInStock(entry.getProduct(), consolidatedPickupPointModel))
							{
								updatePOS(cartModel, consolidatedPickupPointModel, unsuccessfulModifications, entry);
								entriesToConsolidate.add(entry);
							}
							else
							{
								// to be removed if it's not available in the POS
								entriesToBeRemovedDueToPOS.add(entry);
							}
						}
						else
						{
							// if there is one entry that has the same POS as consolidate POS in the cart, use the one in the cart as anchor and add current entry to the map value
							entriesToConsolidate.add(anchorEntry);
							consolidatedEntriesToBeRemoved.add(entry);
						}
					}
				}
			}
		}
		return entriesToConsolidate;
	}

	/**
	 * Gets the anchor entry to consolidate.
	 *
	 * @param entry
	 *           the entry
	 * @param cartModel
	 *           the cart model
	 * @param consolidatedPickupPointModel
	 *           the consolidated pickup point model
	 * @return the anchor entry to consolidate
	 */
	protected AbstractOrderEntryModel getAnchorEntryToConsolidate(final AbstractOrderEntryModel entry, final CartModel cartModel,
			final PointOfServiceModel consolidatedPickupPointModel)
	{
		return cartModel.getEntries().stream().filter(Objects::nonNull)
				.filter(cartEntry -> cartEntry.getDeliveryPointOfService() != null)
				.filter(cartEntry -> cartEntry.getDeliveryPointOfService().equals(consolidatedPickupPointModel))
				.filter(cartEntry -> cartEntry.getProduct().equals(entry.getProduct())).findFirst().orElse(null);
	}

	protected void updatePOS(final CartModel cartModel, final PointOfServiceModel consolidatedPickupPointModel,
			final List<CommerceCartModification> unsuccessfulModifications, final AbstractOrderEntryModel entry)
			throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryNumber(entry.getEntryNumber().longValue());
		parameter.setPointOfService(consolidatedPickupPointModel);
		final CommerceCartModification modification = getCommerceCartService().updatePointOfServiceForCartEntry(parameter);
		if (!CommerceCartModificationStatus.SUCCESS.equals(modification.getStatusCode()))
		{
			unsuccessfulModifications.add(modification);
		}
	}

	protected boolean isInStock(final ProductModel productModel, final PointOfServiceModel pointOfServiceModel)
	{
		if (!getCommerceStockService().isStockSystemEnabled(getBaseStoreService().getCurrentBaseStore()))
		{
			return true;
		}
		final Long availableStockLevel = getCommerceStockService().getStockLevelForProductAndPointOfService(productModel,
				pointOfServiceModel);
		return availableStockLevel == null || availableStockLevel.longValue() > 0;
	}

	protected AbstractOrderEntryModel getExistingAnchorEntryByProduct(final ProductModel product,
			final List<AbstractOrderEntryModel> entriesToConsolidate)
	{
		return entriesToConsolidate.stream().filter(Objects::nonNull).filter(entry -> Objects.equals(product, entry.getProduct()))
				.findFirst().orElse(null);
	}

	protected long calculateProductQtyInCart(final ProductModel productModel, final CartModel cartModel)
	{
		long cartLevel = 0;
		for (final CartEntryModel entryModel : getCartService().getEntriesForProduct(cartModel, productModel))
		{
			if (entryModel.getDeliveryPointOfService() != null)
			{
				cartLevel += entryModel.getQuantity() != null ? entryModel.getQuantity().longValue() : 0;
			}
		}
		return cartLevel;
	}

	protected PickupPointOfServiceConsolidationStrategy getPickupPointOfServiceConsolidationStrategy()
	{
		return pickupPointOfServiceConsolidationStrategy;
	}

	@Required
	public void setPickupPointOfServiceConsolidationStrategy(
			final PickupPointOfServiceConsolidationStrategy pickupPointOfServiceConsolidationStrategy)
	{
		this.pickupPointOfServiceConsolidationStrategy = pickupPointOfServiceConsolidationStrategy;
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

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
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

	protected CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	@Required
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
