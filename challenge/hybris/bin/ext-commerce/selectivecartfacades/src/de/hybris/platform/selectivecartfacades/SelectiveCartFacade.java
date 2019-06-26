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
package de.hybris.platform.selectivecartfacades;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;

import java.util.List;


/**
 * Deals with selective cart related DTOs using existing service
 */
public interface SelectiveCartFacade
{
	/**
	 * Gets the wishlist2data for selective cart of the current user
	 *
	 * @return the Wishlist2Data
	 */
	Wishlist2Data getWishlistForSelectiveCart();

	/**
	 * Removes the entry from wishlist by product code
	 *
	 * @param productCode
	 *           the product code used for removing entry
	 */
	void removeWishlistEntryForProduct(String productCode);

	/**
	 * Removes the entry from wishlist and adds wishlist entry to cart as a cart entry
	 *
	 * @param productCode
	 *           the product code used for getting entry from wishlist
	 * @throws CommerceCartModificationException
	 *            throws when removing wish list entry error
	 */
	void addToCartFromWishlist(String productCode) throws CommerceCartModificationException;

	/**
	 * Removes the entry from wishlist and adds wishlist entry to cart as a cart entry
	 *
	 * @throws CommerceCartModificationException
	 *            throws when cart could not be modified
	 */
	void updateCartFromWishlist() throws CommerceCartModificationException;

	/**
	 * Removes the entry from cart and adds cart entry to wishlist
	 *
	 * @param orderEntry
	 *           the order entry data
	 * @throws CommerceCartModificationException
	 *            throws when cart could not be modified
	 */
	void addToWishlistFromCart(OrderEntryData orderEntry) throws CommerceCartModificationException;

	/**
	 * Removes the entry from cart and adds cart entry to wishlist
	 *
	 * @param entryNumber
	 *           the entry number used for getting cart entry
	 * @throws CommerceCartModificationException
	 *            throws when cart could not be modified
	 */
	void addToWishlistFromCart(Integer entryNumber) throws CommerceCartModificationException;

	/**
	 * Removes the entry from cart and adds the entry to wishlist
	 *
	 * @param productCodes
	 *           the product code list used for getting product data from cart entry
	 * @throws CommerceCartModificationException
	 *            throws when the cart could not be modified
	 */
	void addToWishlistFromCart(final List<String> productCodes) throws CommerceCartModificationException;

	/**
	 * Gets order entries that are converted from Wishlist2EntryModel
	 *
	 * @return the list of OrderEntryData
	 */
	List<OrderEntryData> getWishlistOrdersForSelectiveCart();
}
