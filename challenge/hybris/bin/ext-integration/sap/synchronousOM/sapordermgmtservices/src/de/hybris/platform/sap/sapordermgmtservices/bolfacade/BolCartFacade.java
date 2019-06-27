/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapordermgmtservices.bolfacade;

import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;

import java.util.List;
import java.util.Map;


/**
 * Facade to the BOL layer. Allows to work with the BOL cart.
 */
public interface BolCartFacade
{
	/**
	 * Return current cart from BOL.
	 *
	 * @return Current cart, might be null if not created yet.
	 */
	public Basket getCart();

	/**
	 * Does a session cart exist? This cart is connected to a (non-persisted) order in the SAP back end.
	 *
	 * @return Does a session cart exist?
	 */
	public Boolean hasCart();

	/**
	 * Creates a new session cart. This call creates a (non-persisted) order in the SAP back end. At this point in time, the
	 * sold-to party needs to be known.
	 *
	 * @return Newly created cart BO instance
	 */
	public Basket createCart();

	/**
	 * Updates the session cart and re-reads it from the SAP back end.
	 */
	public void updateCart();

	/**
	 * Adds a product to the cart
	 *
	 * @param code
	 *           Product ID to be added to the cart
	 * @param quantity
	 *           Quantity to be added to the cart
	 * @return BOL representation of new item
	 */
	public Item addToCart(String code, long quantity);

	/**
	 * Fetches cart item per item number
	 *
	 * @param itemNumber
	 *           Internal item number
	 * @return BOL representation of cart item
	 */
	public Item getCartItem(int itemNumber);


	/**
	 * Fetches available delivery types from the cache. Cache is filled from the SAP back end customizing (ERP customizing
	 * table TVSB)
	 *
	 * @return Map of delivery types. Key is the shipping condition code, value the language dependent description
	 */
	public Map<String, String> getAllowedDeliveryTypes();

	/**
	 * Creates a new address instance.
	 *
	 * @return BOL Address representation
	 */
	public Address createAddress();

	/**
	 * Places an order based on a cart, and returns this order. At this point in time, the order is persisted in the SAP
	 * back end.
	 *
	 * @param sapCart
	 *           SAP representation of a cart
	 * @return SAP representation of an order which has been submitted
	 */
	public Order placeOrderFromCart(Basket sapCart);

	/**
	 * Releases the current session cart. A new, initial session cart will be created. An existing session in the SAP back
	 * end is released as well, therefore reservations are gone.
	 *
	 */
	public void releaseCart();



	/**
	 * Validates the cart and compiles the list of cart error messages. Performs a back end call and touches all items to be
	 * sure all error situations are handled. Depending on the customizing in the back end, this can be avoided by either
	 * redefining this method or the corresponding one in {@link SalesDocument}
	 *
	 * @return List of cart error messages arising from validation.
	 */
	MessageList validateCart();

	/**
	 * Adds a list of items to the cart, keeping their handles
	 *
	 * @param items
	 *           BOL items
	 */
	void addItemsToCart(List<Item> items);


	/**
	 * Adds a list of items to the cart, keeping their handles
	 *
	 * @param items
	 *           BOL items item list
	 * @return list of items
	 */
	public List<Item> addEntriesToCart(final List<Item> items);

	/**
	 * @return Is back end not available for a planned downtime?
	 */
	boolean isBackendDown();







}
