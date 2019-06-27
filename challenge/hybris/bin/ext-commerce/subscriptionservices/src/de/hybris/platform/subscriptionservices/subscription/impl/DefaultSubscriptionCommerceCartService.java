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
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionCommerceCartStrategy;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SubscriptionCommerceCartService}.
 */
public class DefaultSubscriptionCommerceCartService extends DefaultCommerceCartService implements SubscriptionCommerceCartService
{
	private static final long SUBSCRIPTION_PRODUCT_QUANTITY = 1;

	private String masterCartBillingTimeCode;
	private int subscriptionProductStockQuantity;
	private BillingTimeService billingTimeService;
	private SubscriptionCommercePriceService commercePriceService;
	private SubscriptionCommerceCartStrategy subscriptionCommerceCartStrategy;
	private BaseStoreService baseStoreService;
	private CommerceStockService commerceStockService;
	private SubscriptionProductService subscriptionProductService;

	/**
	 * @deprecated Since ages.
	 */
	@Deprecated
	@Override
	public CommerceCartModification addToCart(final CartModel masterCartModel, final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry) throws CommerceCartModificationException
	{
		if (getSubscriptionProductService().isSubscription(productModel))
		{
			throw new CommerceCartModificationException("Method is deprecated for SubscriptionProducts. Use method public "
					+ "CommerceCartModification addToCart(CartModel masterCartModel, "
					+ "ProductModel productModel, long quantityToAdd, UnitModel unit, "
					+ "boolean forceNewEntry, String productXml) throws CommerceCartModificationException instead");
		}

		return addToCart(masterCartModel, productModel, quantityToAdd, unit, forceNewEntry, null);
	}

	@Override
	@Nonnull
	public CommerceCartModification addToCart(@Nonnull final CartModel masterCartModel,@Nonnull final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry, final String xmlProduct,
			@Nonnull final String originalSubscriptionId, final AbstractOrderEntryModel originalEntry)
			throws CommerceCartModificationException
	{
		validateParameterNotNull(masterCartModel, "Cart model cannot be null");
		validateParameterNotNull(originalSubscriptionId, "Subscription Id cannot be null");

		// check if the cart already contains an entry for the given subscriptionId.
		// if so delete it as a subscription cannot be upgraded several times
		for (final AbstractOrderEntryModel cartEntry : masterCartModel.getEntries())
		{
			if (StringUtils.equals(originalSubscriptionId, cartEntry.getOriginalSubscriptionId()))
			{
				updateQuantityForCartEntry(masterCartModel, cartEntry.getEntryNumber().intValue(), 0);
			}
		}

		final CommerceCartModification modification = addToCart(masterCartModel, productModel, quantityToAdd, unit, forceNewEntry,
				xmlProduct);

		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			final AbstractOrderEntryModel newEntry = modification.getEntry();
			newEntry.setOriginalSubscriptionId(originalSubscriptionId);
			newEntry.setOriginalOrderEntry((OrderEntryModel) originalEntry);
			getModelService().save(newEntry);
		}
		return modification;
	}

	/**
	 *
	 * @deprecated Since 6.4
	 */
	@Deprecated
	@Override
	@Nonnull
	public CommerceCartModification addToCart(@Nonnull final CartModel masterCartModel,@Nonnull final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry, final String xmlProduct)
			throws CommerceCartModificationException
	{
		validateParameterNotNull(masterCartModel, "Cart model cannot be null");
		validateParameterNotNull(productModel, "Product model cannot be null");

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(masterCartModel);
		parameter.setProduct(productModel);
		parameter.setQuantity(quantityToAdd);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setXmlProduct(xmlProduct);

		final CommerceCartModification modification = addToCart(parameter);

		getModelService().refresh(masterCartModel);

		return modification;
	}

	@Override
	@Nonnull
	public List<BillingTimeModel> getBillingFrequenciesForMasterEntry(@Nonnull final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("entry", entry);

		if (!getSubscriptionProductService().isSubscription(entry.getProduct()))
		{
			return Collections.emptyList();
		}

		final List<BillingTimeModel> billingFrequencies = new ArrayList<BillingTimeModel>();

		final ProductModel subscriptionProduct = entry.getProduct();

		final SubscriptionPricePlanModel subscriptionPricePlan = getCommercePriceService().getSubscriptionPricePlanForEntry(entry);

		if (useBillingFrequencyForProduct(subscriptionProduct, subscriptionPricePlan))
		{
			billingFrequencies.add(subscriptionProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency());
			return billingFrequencies;
		}

		if (subscriptionPricePlan == null)
		{
			return Collections.emptyList();
		}

		// The one recurring frequency
		addOneRecurringFrequency(subscriptionPricePlan, billingFrequencies);

		// multiple one time frequencies
		billingFrequencies.addAll(subscriptionPricePlan.getOneTimeChargeEntries().stream()
				.map(OneTimeChargeEntryModel::getBillingEvent).collect(Collectors.toList()));

		if (CollectionUtils.isEmpty(billingFrequencies))
		{
			return Collections.emptyList();
		}
		else
		{
			return billingFrequencies;
		}
	}

	protected void addOneRecurringFrequency(final SubscriptionPricePlanModel subscriptionPricePlan,
			final List<BillingTimeModel> billingFrequencies)
	{
		if (CollectionUtils.isNotEmpty(subscriptionPricePlan.getRecurringChargeEntries()))
		{
			final RecurringChargeEntryModel recurringChargeEntry = subscriptionPricePlan.getRecurringChargeEntries().iterator()
					.next();
			if (recurringChargeEntry.getBillingTime() != null)
			{
				billingFrequencies.add(recurringChargeEntry.getBillingTime());
			}
		}
	}

	protected boolean useBillingFrequencyForProduct(final ProductModel subscriptionProduct,
			final SubscriptionPricePlanModel subscriptionPricePlan)
	{
		return subscriptionPricePlan == null && subscriptionProduct.getSubscriptionTerm() != null
				&& subscriptionProduct.getSubscriptionTerm().getBillingPlan() != null
				&& subscriptionProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency() != null;
	}

	@Override
	public void removeAllEntries(final CartModel masterCartModel)
	{
		validateParameterNotNull(masterCartModel, "Cart model cannot be null");

		if (!isMasterCart(masterCartModel))
		{
			throw new IllegalArgumentException("Provided cart '" + masterCartModel.getCode() + "' is not a master cart");
		}

		for (final AbstractOrderModel childCart : masterCartModel.getChildren())
		{
			getModelService().removeAll(childCart.getEntries());
			getModelService().remove(childCart);
			getModelService().refresh(masterCartModel);
		}

		super.removeAllEntries(masterCartModel);
	}

	/**
	 * Searches in the given <code>cartModel</code> for the cart entry with has the given <code>entryNumber</code> and
	 * returns it.
	 *
	 * @param cartModel
	 *           the cart
	 * @param entryNumber
	 *           number of the cart entry
	 * @return the cart entry with the given <code>entryNumber</code>
	 *
	 * @throws CommerceCartModificationException
	 *            if there is no cart entry with the given <code>entryNumber</code>
	 */
	protected AbstractOrderEntryModel getCartEntryToBeUpdated(final CartModel cartModel, final long entryNumber)
			throws CommerceCartModificationException
	{
		final AbstractOrderEntryModel cartEntryModel = getSubscriptionCommerceCartStrategy().getEntryForNumber(cartModel,
				(int) entryNumber);

		if (cartEntryModel == null)
		{
			throw new CommerceCartModificationException("Unknown entry number " + entryNumber + " for cart " + cartModel.getCode());
		}

		return cartEntryModel;
	}

	/**
	 * Find the available stock quantity for a product. Overrides the method in the super class in order to handle stock
	 * availability also for subscription products which do not have a physically available stock
	 *
	 * @param productModel
	 *           the product in the cart
	 * @return the available stock level
	 */
	protected long getAvailableStockLevel(final ProductModel productModel, final PointOfServiceModel pointOfServiceModel)
	{
		final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (!getCommerceStockService().isStockSystemEnabled(baseStore))
		{
			return getSubscriptionCommerceCartStrategy().getForceInStockMaxQuantity();
		}

		if (getSubscriptionProductService().isSubscription(productModel))
		{
			return getSubscriptionProductStockQuantity();
		}

		return getSubscriptionCommerceCartStrategy().getAvailableStockLevel(productModel, pointOfServiceModel);

	}

	@Override
	@Nullable
	public CartModel getChildCartForBillingTime(@Nonnull
	final CartModel masterCart, @Nonnull
	final BillingTimeModel billFreq)
	{
		for (final AbstractOrderModel curChildCart : masterCart.getChildren())
		{
			final BillingTimeModel curBillFreq = curChildCart.getBillingTime();
			if (billFreq.equals(curBillFreq))
			{
				return (CartModel) curChildCart;
			}
		}

		return null;
	}

	/**
	 * Creates a new child cart for the given billing frequency and assigns it as child cart to the given master cart.
	 *
	 */
	@Override
	public CartModel createChildCartForBillingTime(final CartModel masterCart, final BillingTimeModel billFreq)
	{
		final CartModel childCart = getModelService().create(CartModel.class);
		final Collection<AbstractOrderModel> siblings = new ArrayList<>();
		if (masterCart.getChildren() != null)
		{
			siblings.addAll(masterCart.getChildren());
		}
		siblings.add(childCart);
		childCart.setParent(masterCart);
		childCart.setBillingTime(billFreq);
		childCart.setCurrency(masterCart.getCurrency());
		childCart.setUser(masterCart.getUser());
		childCart.setDate(masterCart.getDate());

		masterCart.setChildren(siblings);
		getModelService().save(masterCart);

		return childCart;
	}

	protected boolean isMasterCart(final CartModel cartModel)
	{
		return cartModel != null && cartModel.getParent() == null;
	}

	/**
	 * Checks if the given {@link CartModel} is a master cart and has the correct {@link BillingTimeModel}.
	 *
	 * @param cartModel
	 *           the cart model to be checked whether it is a master cart
	 * @param masterCartBillingTimeModel
	 *           the {@link BillingTimeModel} that a master cart must have. If the given cart's billing frequency is
	 *           empty the <code>masterCartBillingTimeModel</code> will be set as billing frequency
	 * @throws IllegalArgumentException
	 *            if the given cart is not a master cart
	 * @throws CommerceCartModificationException
	 *            if the given cart's billing frequency does no equal the given <code>masterCartBillingTimeModel</code>
	 */
	@Override
	public void checkMasterCart(final CartModel cartModel, final BillingTimeModel masterCartBillingTimeModel)
			throws IllegalArgumentException, CommerceCartModificationException
	{
		if (!isMasterCart(cartModel))
		{
			throw new IllegalArgumentException("Provided cart '" + cartModel.getCode() + "' is not a master cart");
		}

		if (cartModel.getBillingTime() == null)
		{
			cartModel.setBillingTime(masterCartBillingTimeModel);
			getModelService().save(cartModel);
		}
		else if (!masterCartBillingTimeModel.equals(cartModel.getBillingTime()))
		{
			throw new CommerceCartModificationException("Given cart's billing frequency '" + cartModel.getBillingTime().getCode()
					+ "' does not equal the required master cart billing frequency '" + masterCartBillingTimeModel.getCode() + "'");
		}

	}

	/**
	 * Overrides the super class' method to make sure that in a multi-cart system the master cart and its child carts are
	 * re-calculated. This includes finding prices, taxes, discounts, payment and delivery costs by calling the currently
	 * installed price factory.
	 *
	 * @param masterCartModel
	 *           the cart model {@link CartModel} (must exist) that will be recalculated {@link CartModel}. It must be
	 *           the master cart of the multi-cart system. In addition, the master cart's child carts will also be
	 *           re-calculated.
	 * @throws IllegalArgumentException
	 *            in case the given <code>masterCartModel</code> is not a master cart
	 */
	@Override
	public void recalculateCart(final CartModel masterCartModel) throws CalculationException
	{
		if (!isMasterCart(masterCartModel))
		{
			throw new IllegalArgumentException("Provided cart '" + masterCartModel.getCode() + "' is not a master cart");
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setCart(masterCartModel);
		parameter.setEnableHooks(parameter.isEnableHooks());

		recalculateCart(parameter);
	}

	/**
	 * Overrides the super class' method to make sure that in a multi-cart system the master cart and its child carts are
	 * re-calculated. This includes finding prices, taxes, discounts, payment and delivery costs by calling the currently
	 * installed price factory.
	 *
	 * @param parameter
	 *           checkout parameter containing the the master cart of the multi-cart
	 */
	@Override
	public void recalculateCart(final CommerceCartParameter parameter)
	{
		final CartModel masterCartModel = parameter.getCart();

		validateParameterNotNullStandardMessage("masterCartModel", masterCartModel);

		super.recalculateCart(parameter);

		for (final AbstractOrderModel childCartModel : masterCartModel.getChildren())
		{
			childCartModel.setCurrency(masterCartModel.getCurrency());
			getModelService().save(childCartModel);

			final CommerceCartParameter childParameter = new CommerceCartParameter();
			childParameter.setCart((CartModel) childCartModel);
			childParameter.setEnableHooks(parameter.isEnableHooks());

			super.recalculateCart(childParameter);
		}
	}

	@Override
	@Nonnull
	public CartModel getMasterCartForCartEntry(@Nonnull
	final CartEntryModel cartEntryModel)
	{
		validateParameterNotNull(cartEntryModel, "CartEntry model cannot be null");

		if (cartEntryModel.getMasterEntry() == null)
		{
			return cartEntryModel.getOrder();
		}
		else
		{
			return (CartModel) cartEntryModel.getMasterEntry().getOrder();
		}
	}

	protected void checkQuantityToAdd(final long quantityToAdd, final long maxQuantity) throws CommerceCartModificationException
	{
		if (quantityToAdd > maxQuantity)
		{
			throw new CommerceCartModificationException("The given quantityToAdd (" + quantityToAdd
					+ ") exceeds the max. allowed quantity (" + maxQuantity + ")");
		}
	}

	@Override
	public void checkQuantityToAdd(final long quantityToAdd) throws CommerceCartModificationException
	{
		checkQuantityToAdd(quantityToAdd, SUBSCRIPTION_PRODUCT_QUANTITY);
	}

	protected BillingTimeModel getBillingTimeForCode(final String code)
	{
		return getBillingTimeService().getBillingTimeForCode(code);
	}

	@Required
	public void setMasterCartBillingTimeCode(final String masterCartBillingTimeCode)
	{
		this.masterCartBillingTimeCode = masterCartBillingTimeCode;
	}

	@Override
	public String getMasterCartBillingTimeCode()
	{
		return masterCartBillingTimeCode;
	}

	@Required
	public void setBillingTimeService(final BillingTimeService billingTimeService)
	{
		this.billingTimeService = billingTimeService;
	}


	protected BillingTimeService getBillingTimeService()
	{
		return billingTimeService;
	}

	@Required
	public void setSubscriptionProductStockQuantity(final int subscriptionProductStockQuantity)
	{
		this.subscriptionProductStockQuantity = subscriptionProductStockQuantity;
	}

	protected int getSubscriptionProductStockQuantity()
	{
		return subscriptionProductStockQuantity;
	}

	protected SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}

	@Required
	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
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

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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

	/**
	 * @return subscription product service
	 */
	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	@Required
	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}

}
