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
package de.hybris.platform.commerceservices.order;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.exceptions.IllegalQuoteStateException;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.CommerceTaxEstimateResult;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceFactory;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Nonnull;


/**
 * Commerce service that exposes methods to deal with cart operations. This includes promotions and stock support.
 */
public interface CommerceCartService
{
	/**
	 * @deprecated Since 5.2. Use {@link #addToCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 * 
	 * @param cartModel
	 *           the user's cart in session
	 * @param productModel
	 *           the {@link ProductModel} to add
	 * @param quantity
	 *           the quantity to add
	 * @param unit
	 *           the UnitModel of the product @see 
	 *           {@link de.hybris.platform.core.model.product.ProductModel#getUnit()}
	 * @param forceNewEntry
	 *           the flag for creating a new
	 *           {@link de.hybris.platform.core.model.order.CartEntryModel}
	 * @return the cart modification
	 */
	@Deprecated
	CommerceCartModification addToCart(CartModel cartModel, ProductModel productModel, long quantity, UnitModel unit,
			boolean forceNewEntry) throws CommerceCartModificationException;

	/**
	 * Adds to the (existing) {@link CartModel} the (existing) {@link ProductModel} in the given {@link UnitModel} and
	 * with the given <code>quantity</code>. If in the cart already an entry with the given product and given unit exists
	 * the given <code>quantity</code> is added to the the quantity of this cart entry unless <code>forceNewEntry</code>
	 * is set to true. After this the cart is calculated.
	 *
	 * @param parameter
	 *           - A parameter object containing all attributes needed for add to cart
	 *           <P>
	 *           {@link CommerceCartParameter#cart} - The user's cart in session
	 *           {@link CommerceCartParameter#pointOfService} - The store object for pick up in store items (only needs
	 *           to be passed in if you are adding an item to pick up {@link CommerceCartParameter#product} - The
	 *           {@link ProductModel} to add {@link CommerceCartParameter#quantity} - The quantity to add
	 *           {@link CommerceCartParameter#unit} - The UnitModel of the product @see
	 *           {@link de.hybris.platform.core.model.product.ProductModel#getUnit()}
	 *           {@link CommerceCartParameter#createNewEntry} - The flag for creating a new
	 *           {@link de.hybris.platform.core.model.order.CartEntryModel}
	 *           </P>
	 * @return the cart modification data that includes a statusCode and the actual quantity added to the cart
	 * @throws CommerceCartModificationException
	 *            if the <code>product</code> is a base product OR the quantity is less than 1 or no usable unit was
	 *            found (only when given <code>unit</code> is also <code>null</code>) or any other reason the cart could
	 *            not be modified.
	 */
	CommerceCartModification addToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException;

	/**
	 * @deprecated Since 5.2. Use {@link #addToCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 * 
	 * @param cartModel
	 *           the user's cart in session
	 * @param productModel
	 *           the {@link ProductModel} to add
	 * @param deliveryPointOfService
	 *           the delivery store 
	 * @param quantity
	 *           the quantity to add
	 * @param unit
	 *           the UnitModel of the product @see 
	 *           {@link de.hybris.platform.core.model.product.ProductModel#getUnit()}
	 * @param forceNewEntry
	 *           the flag for creating a new
	 *           {@link de.hybris.platform.core.model.order.CartEntryModel}
	 * @return the cart modification     
	 */
	@Deprecated
	CommerceCartModification addToCart(CartModel cartModel, ProductModel productModel, PointOfServiceModel deliveryPointOfService,
			long quantity, UnitModel unit, boolean forceNewEntry) throws CommerceCartModificationException;

	/**
	 * Method for validating every entry in the cart
	 *
	 * @param parameter
	 * @return a list of cart modifications data that includes a statusCode and the actual quantity that the entry was
	 *         updated to
	 * @throws CommerceCartModificationException
	 *            if the cart could not be modified.
	 */
	List<CommerceCartModification> validateCart(final CommerceCartParameter parameter) throws CommerceCartModificationException;

	/**
	 * @deprecated Since 5.2. Use {@link #validateCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 * 
	 * @param cartModel
	 *           the user's cart in session
	 * @return a {@link List} of cart modifications 
	 */
	@Deprecated
	List<CommerceCartModification> validateCart(CartModel cartModel) throws CommerceCartModificationException;

	/**
	 * Calculates the given <code>cartModel</code> and returns <code>true</code> if each entry alone and after this the
	 * {@link CartModel} was calculated. Thereby any invalid entry will be automatically removed. The net/gross prices
	 * depends on the current session user, see {@link PriceFactory#isNetUser(de.hybris.platform.jalo.user.User)} for
	 * more information.
	 *
	 * @param parameter
	 *           the parameter object holding the existing {@link CartModel} that will be calculated
	 * @return <code>false</code> if the <code>cartModel</code> was already calculated.
	 */
	boolean calculateCart(CommerceCartParameter parameter);

	/**
	 * @deprecated Since 5.2. Use {@link #calculateCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             instead.
	 *   
	 * @param cartModel
	 *           the user's cart in session 
	 * @return <code>false</code> if the <code>cartModel</code> was already calculated
	 */
	@Deprecated
	boolean calculateCart(CartModel cartModel);

	/**
	 * @deprecated Since 5.2. Use {@link #recalculateCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             instead
	 *             
	 * @param cartModel
	 *           the user's cart in session 
	 * @throws CalculationException
	 */
	@Deprecated
	void recalculateCart(CartModel cartModel) throws CalculationException;

	/**
	 * Recalculates the whole cart and all its entries. This includes finding prices, taxes, discounts, payment and
	 * delivery costs by calling the currently installed price factory.
	 *
	 * @param parameter
	 *           A parameter object holding the cart model {@link CartModel} (must exist) that will be recalculated
	 *           {@link CartModel}
	 * @throws CalculationException
	 *            the calculation exception
	 */
	void recalculateCart(CommerceCartParameter parameter) throws CalculationException;

	/**
	 * @deprecated Since 5.2. Use {@link #removeAllEntries(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             instead
	 *
	 * @param cartModel
	 *           the user's cart in session
	 */
	@Deprecated
	void removeAllEntries(CartModel cartModel);

	/**
	 * Removes all entries from the given {@link CartModel}.
	 *
	 * @param parameter
	 *           A parameter object holding the {@link CartModel} that will be emptied
	 */
	void removeAllEntries(CommerceCartParameter parameter);

	/**
	 * @deprecated Since 5.2. Use
	 *             {@link #updateQuantityForCartEntry(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             
	 * @param cartModel
	 *           the user's cart in session
	 * @param entryNumber
	 * 			 the cart entry number
	 * @param newQuantity
	 * 			 the new quantity
	 * @return the cart modification 
	 */
	@Deprecated
	CommerceCartModification updateQuantityForCartEntry(CartModel cartModel, long entryNumber, long newQuantity)
			throws CommerceCartModificationException;

	/**
	 * Update quantity for the cart entry with given <code>entryNumber</code> with the given <code>newQuantity</code>.
	 * Then cart is calculated.
	 *
	 * @param parameter
	 *           - A parameter object containing all attributes needed for add to cart
	 *           <P>
	 *           {@link CommerceCartParameter#cart} - The user's cart in session
	 *           {@link CommerceCartParameter#pointOfService} - The store object for pick up in store items (only needs
	 *           to be passed in if you are adding an item to pick up {@link CommerceCartParameter#product} - The
	 *           {@link ProductModel} to add {@link CommerceCartParameter#quantity} - The quantity to add
	 *           {@link CommerceCartParameter#unit} - The UnitModel of the product @see
	 *           {@link de.hybris.platform.core.model.product.ProductModel#getUnit()}
	 *           {@link CommerceCartParameter#createNewEntry} - The flag for creating a new
	 *           {@link de.hybris.platform.core.model.order.CartEntryModel}
	 *           </P>
	 * @return the cart modification data that includes a statusCode and the actual quantity that the entry was updated
	 *         to
	 * @throws CommerceCartModificationException
	 *            if the <code>product</code> is a base product OR the quantity is less than 1 or no usable unit was
	 *            found (only when given <code>unit</code> is also <code>null</code>) or any other reason the cart could
	 *            not be modified.
	 */
	CommerceCartModification updateQuantityForCartEntry(final CommerceCartParameter parameter)
			throws CommerceCartModificationException;


	/**
	 * @deprecated Since 5.2. Use
	 *             {@link #updatePointOfServiceForCartEntry(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             
	 * @param cartModel
	 *           the user's cart in session
	 * @param entryNumber
	 * 			 the cart entry number
	 * @param pointOfServiceModel
	 * 			 the point of service
	 * @return the cart modification             
	 */
	@Deprecated
	CommerceCartModification updatePointOfServiceForCartEntry(CartModel cartModel, long entryNumber,
			PointOfServiceModel pointOfServiceModel) throws CommerceCartModificationException;

	/**
	 * Updates given cart entry model with point of service.
	 *
	 * @param parameters
	 *           - A bean holding any number of additional parameters a client may want to pass to the method for
	 *           extension purposes, the attributes can be added to the bean via beans.xml in your extensions
	 * @return {@link CommerceCartModification} object
	 * @throws CommerceCartModificationException
	 *
	 */
	CommerceCartModification updatePointOfServiceForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException;


	/**
	 * @deprecated Since 5.2. Use
	 *             {@link #updateToShippingModeForCartEntry(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             
	 * @param cartModel
	 *           the user's cart in session
	 * @param entryNumber
	 * 			 the cart entry number
	 * @return the cart modification
	 * @throws CommerceCartModificationException      
	 */
	@Deprecated
	CommerceCartModification updateToShippingModeForCartEntry(CartModel cartModel, long entryNumber)
			throws CommerceCartModificationException;

	/**
	 * Update the shipping mode for a given cart entry
	 *
	 * @param parameters
	 *           - A bean holding any number of additional parameters a client may want to pass to the method for
	 *           extension purposes, the attributes can be added to the bean via beans.xml in your extensions
	 * @return {@link CommerceCartModification} object
	 * @throws CommerceCartModificationException
	 *
	 */
	CommerceCartModification updateToShippingModeForCartEntry(final CommerceCartParameter parameters)
			throws CommerceCartModificationException;


	/**
	 * @deprecated Since 5.2. Use {@link #split(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *
	 * @param cartModel
	 *           the user's cart in session
	 * @param entryNumber
	 * 			 the cart entry number
	 * @return the number of the newly created entry
	 */
	@Deprecated
	long split(CartModel cartModel, long entryNumber) throws CommerceCartModificationException;


	/**
	 * Split existing cart entry. The entry must have a quantity greater than 1. One of the quantity from the original
	 * entry is moved to the new cart entry.
	 *
	 * @param parameters
	 *           - A bean holding any number of additional parameters a client may want to pass to the method for
	 *           extension purposes, the attributes can be added to the bean via beans.xml in your extensions
	 * @return The number of the newly created entry.
	 */
	long split(final CommerceCartParameter parameters) throws CommerceCartModificationException;


	/**
	 * @deprecated Since 5.2. Use {@link #restoreCart(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 * 
	 * @param oldCart
	 *           the old cart to restore
	 * @return details of any items that could not be restored in part or in full
	 */
	@Deprecated
	CommerceCartRestoration restoreCart(CartModel oldCart) throws CommerceCartRestorationException;


	/**
	 * Restores the current customer's cart.
	 *
	 * @param parameters
	 *           - A bean holding any number of additional parameters a client may want to pass to the method for
	 *           extension purposes, the attributes can be added to the bean via beans.xml in your extensions
	 * @return details of any items that could not be restored in part or in full.
	 * @throws CommerceCartRestorationException
	 *            if any problems occur in restoring the cart
	 */
	CommerceCartRestoration restoreCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException;

	/**
	 * Locate a cart for the specified guid and site and user. If guid is null, the most recently modified user cart is
	 * returned.
	 *
	 * @param guid
	 *           a unique identifier
	 * @param site
	 *           the current BaseSite - may not be null
	 * @param user
	 *           the current user - may not be null
	 * @return the specified cart
	 */
	CartModel getCartForGuidAndSiteAndUser(String guid, BaseSiteModel site, UserModel user);

	/**
	 * Locate a cart for the specified guid and site
	 *
	 * @param guid
	 * @param site
	 * @return the specified cart
	 */
	CartModel getCartForGuidAndSite(String guid, BaseSiteModel site);

	/**
	 * Locate a cart for the specified guid and site and user.
	 *
	 * @param code
	 * @param user
	 * @return the specified cart
	 */
	CartModel getCartForCodeAndUser(String code, UserModel user);

	/**
	 * Locate all carts for the specified site and user.
	 *
	 * @param site
	 * @param user
	 * @return list of specified carts
	 */
	List<CartModel> getCartsForSiteAndUser(BaseSiteModel site, UserModel user);

	/**
	 * Estimate taxes for the cartModel and using the deliveryZipCode as the delivery zip code.
	 *
	 * @param cartModel
	 *           cart to estimate taxes for
	 * @param deliveryZipCode
	 *           zip code to use as the delivery address
	 * @param deliveryCountryIso
	 *           country isocode used as the delivery address
	 * @return total of the estimated taxes
	 * @deprecated Since 5.2. Use {@link #estimateTaxes(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             instead.
	 */
	@Deprecated
	BigDecimal estimateTaxes(CartModel cartModel, String deliveryZipCode, String deliveryCountryIso);

	/**
	 * Estimate taxes for the cartModel and using the deliveryZipCode as the delivery zip code.
	 *
	 *
	 * @param parameters
	 *           A parameter object holding the following properties - cartModel cart to estimate taxes for -
	 *           deliveryZipCode zip code to use as the delivery address - deliveryCountryIso country iso code used as
	 *           the delivery address
	 * @return A result object with the total of the estimated taxes
	 */
	CommerceTaxEstimateResult estimateTaxes(final CommerceCartParameter parameters);

	/**
	 * Remove the old carts for the user.
	 *
	 * @param currentCart
	 *           the current user's cart
	 * @param baseSite
	 *           the current base site
	 * @param user
	 *           the current user
	 * @deprecated Since 5.2. Use {@link #removeStaleCarts(de.hybris.platform.commerceservices.service.data.CommerceCartParameter)}
	 *             instead.
	 */
	@Deprecated
	void removeStaleCarts(CartModel currentCart, BaseSiteModel baseSite, UserModel user);


	/**
	 * @param parameters
	 *           a parameter object holding the following values - cart Users Cart - baseSite Current base site - user
	 *           Current user
	 */
	void removeStaleCarts(final CommerceCartParameter parameters);

	/**
	 * Merge two carts and add modifications
	 *
	 * @param fromCart
	 *           the cart to merge
	 * @param toCart
	 *           the target cart
	 * @param modifications
	 *           the list of modifications
	 * @throws CommerceCartMergingException
	 */
	void mergeCarts(CartModel fromCart, CartModel toCart, List<CommerceCartModification> modifications)
			throws CommerceCartMergingException;

	/**
 	 * Update configuration on a configurable product.
	 *
 	 * @param parameters
	 * <ul>
	 *    <li>{@code cart}</li>
	 *    <li>{@code entryNumber}</li>
	 *    <li>{@code product}</li>
	 *    <li>{@code productConfigurator}</li>
	 *    <li>{@code productConfiguration}</li>
	 * </ul>
 	 * @throws CommerceCartModificationException if product is not configurable or {@code entry} is invalid
 	 * @throws IllegalArgumentException if {@code configuration} contains
  	 */
	void configureCartEntry(CommerceCartParameter parameters) throws CommerceCartModificationException;

	/**
	 * Updates cart metadata, such as: name, description, expiration time.
	 *
	 * @param parameter
	 *           a bean holding any number of additional attributes a client may want to pass to the method for extension
	 *           purposes, the attributes can be added to the bean via beans.xml in your extensions
	 * @throws IllegalArgumentException
	 *            if any attributes fail validation
	 * @throws IllegalQuoteStateException
	 *            if the cart is associated with a quote for which the operation cannot be performed
	 */
	void updateCartMetadata(CommerceCartMetadataParameter parameter);

	/**
	 * Remove entry group and all cart entries of the group and it's descendants from the cart.
	 *
	 * @param parameters a parameter object containing all attributes needed for remove group from the cart
	 *                   <ul>
	 *                      <li>{@code cart} user's cart</li>
	 *                      <li>{@code entryGroupNumber} number of entry group to be removed</li>
	 *                      <li>{@code enableHooks} should the method hooks be executed</li>
	 *                   </ul>
	 * @return {@link CommerceCartModification} object
	 * @throws CommerceCartModificationException if related cart entry wasn't removed.
	 */
	@Nonnull
	CommerceCartModification removeEntryGroup(@Nonnull RemoveEntryGroupParameter parameters) throws CommerceCartModificationException;
}
