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
package de.hybris.platform.subscriptionservices.subscription;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import java.util.List;

import javax.annotation.Nonnull;


/**
 * Overrides the DefaultCommerceCartService where necessary to implement the multi-cart functionality for subscription
 * products. The master cart which always has the billing time 'paynow' stores an entry for each product that is added
 * to cart. Entries of subscription products that do no have a paynow price are stored with a 0.00 price in the master
 * cart. The master cart may have one or more child carts which contain additional entries for those subscription
 * products that have other/additional billing times. One child cart per billing time is created.
 * <p/>
 * On a method level it adds or overrides existing methods in order to support SubscriptionProducts and especially their
 * billing times. It creates or resolves the specific child cart based on the BillingFrequency and delegates the add,
 * update (incl. remove) or calculate operation to that child cart.
 */
public interface SubscriptionCommerceCartService extends CommerceCartService
{
	/**
	 * @deprecated Since 5.2 (see BIT-168)
	 *
	 * @param masterCartModel
	 *           master cart
	 * @param productModel
	 *           the {@link ProductModel} to add
	 * @param quantityToAdd
	 *           the quantity to add
	 * @param unit
	 *           the UnitModel of the product
	 * @param forceNewEntry
	 *           the flag for creating a new
	 * @param xmlProduct
	 * @param originalSubscriptionId
	 * @param originalEntry
	 * @return the cart modification
	 * @throws CommerceCartModificationException
	 */
	@Deprecated
	CommerceCartModification addToCart(final CartModel masterCartModel, final ProductModel productModel, final long quantityToAdd,
			final UnitModel unit, final boolean forceNewEntry, final String xmlProduct, final String originalSubscriptionId,
			final AbstractOrderEntryModel originalEntry) throws CommerceCartModificationException;


	/**
	 * @deprecated Since 6.4
	 *
	 * @param masterCartModel
	 *           master cart
	 * @param productModel
	 *           the {@link ProductModel} to add
	 * @param quantityToAdd
	 *           the quantity to add
	 * @param unit
	 *           the UnitModel of the product
	 * @param forceNewEntry
	 *           the flag for creating a new
	 * @param xmlProduct
	 * @return the cart modification
	 * @throws CommerceCartModificationException
	 */
	@Deprecated
	@Nonnull
	CommerceCartModification addToCart(@Nonnull final CartModel masterCartModel, @Nonnull final ProductModel productModel, 
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry, String xmlProduct)
			throws CommerceCartModificationException;

	/**
	 * Get the master cart for the given <code>cartEntryModel</code>. The <code>cartEntryModel</code> may be a master or
	 * child cart entry.
	 *
	 * @param cartEntryModel
	 *           the {@link CartEntryModel} whose master {@link CartModel} is searched
	 * @return the given <code>cartEntryModel</code>'s master {@link CartModel}
	 */
	@Nonnull
	CartModel getMasterCartForCartEntry(@Nonnull CartEntryModel cartEntryModel);

	/**
	 * Get the child cart for the given <code>BillingTimeModel</code>, if any.
	 *
	 * @param masterCart
	 *           the {@link CartModel} of the master cart
	 * @param billFreq
	 *           the {@link BillingTimeModel} to look for
	 * @return the {@link CartModel} for the given <code>BillingTimeModel</code> or null.
	 */
	CartModel getChildCartForBillingTime(CartModel masterCart, BillingTimeModel billFreq);

	/**
	 * Get the billing frequency code for the master cart.
	 *
	 * @return a String with the billing frequency code for the master cart
	 */
	String getMasterCartBillingTimeCode();


	/**
	 * Checks if given cart is a master cart and if its billing time is correct.
	 *
	 * @param cartModel
	 *           {@link CartModel} to be checked
	 * @param masterCartBillingTimeModel
	 * @throws IllegalArgumentException
	 *            if given cart is not a master cart
	 * @throws CommerceCartModificationException
	 *            if billing time is not correct
	 */
	void checkMasterCart(CartModel cartModel, BillingTimeModel masterCartBillingTimeModel) throws IllegalArgumentException,
			CommerceCartModificationException;

	/**
	 * Checks if given quantity can be added.
	 *
	 * @param quantityToAdd
	 * @throws CommerceCartModificationException
	 *            If given quantity is not allowed to add.
	 */
	void checkQuantityToAdd(long quantityToAdd) throws CommerceCartModificationException;

	/**
	 * @param entry
	 *           {@link AbstractOrderEntryModel} to get list of billing frequencies for
	 * @return List of {@link BillingTimeModel}s
	 */
	@Nonnull
	List<BillingTimeModel> getBillingFrequenciesForMasterEntry(@Nonnull AbstractOrderEntryModel entry);

	/**
	 * Creates child cart for given master cart and billing time.
	 *
	 * @param masterCart
	 *           {@link CartModel} representing master cart for which child cart should be created
	 * @param billFreq
	 *           {@link BillingTimeModel} for child cart
	 * @return Created child cart.
	 */
	CartModel createChildCartForBillingTime(CartModel masterCart, BillingTimeModel billFreq);
}
