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
package de.hybris.platform.acceleratorfacades.order;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;

import java.util.List;


/**
 * Facade that extends CheckoutFacade with accelerator related functionality.
 */
public interface AcceleratorCheckoutFacade extends CheckoutFacade
{
	/**
	 * Gets the checkout flow group
	 *
	 * @return the checkout flow group value
	 */
	String getCheckoutFlowGroupForCheckout();

	public enum ExpressCheckoutResult
	{
		SUCCESS, ERROR_NOT_AVAILABLE, ERROR_DELIVERY_ADDRESS, ERROR_DELIVERY_MODE, ERROR_CHEAPEST_DELIVERY_MODE, ERROR_PAYMENT_INFO
	}

	/**
	 * Gets the points of service for item pickup
	 *
	 * @return a {@link List} of {@link PointOfServiceData}
	 */
	List<PointOfServiceData> getConsolidatedPickupOptions();

	/**
	 * Runs calculation again and updates cart
	 *
	 * @param pickupPointOfServiceName
	 *           the point of service name
	 * @return a {@link List} of {@link CartModificationData} containing each unsuccessful cart modification
	 * @throws CommerceCartModificationException
	 *            when the cart could not be modified
	 */
	List<CartModificationData> consolidateCheckoutCart(String pickupPointOfServiceName) throws CommerceCartModificationException;

	/**
	 * Checks if the current cart is allow to go through express checkout
	 *
	 * @return true if express checkout is allowed for cart
	 */
	boolean isExpressCheckoutAllowedForCart();

	/**
	 * Checks if the current store is eligible for express checkout option
	 *
	 * @return true if checkout if express checkout of available for store
	 */
	boolean isExpressCheckoutEnabledForStore();

	/**
	 * Checks if the current store is eligible tax estimation
	 *
	 * @return true if tax estimation enabled for store
	 */
	boolean isTaxEstimationEnabledForCart();

	/**
	 * Checks if creating a new delivery address is allowed for the current cart
	 *
	 * @return true if creating a new delivery address is allowed for the cart
	 */
	boolean isNewAddressEnabledForCart();

	/**
	 * Checks if removing an address from the address book is allowed during checkout for the current cart
	 *
	 * @return true if removing an address is allowed for the cart
	 */
	boolean isRemoveAddressEnabledForCart();

	/**
	 * Checks the required conditions and performs the express checkout
	 *
	 * @return the result of the operation
	 */
	ExpressCheckoutResult performExpressCheckout();

	/**
	 * Checks if there is a valid cart for checkout
	 *
	 * @return if there is a valid cart
	 */
	boolean hasValidCart();

	/**
	 * Checks if there is no delivery address
	 *
	 * @return true if there is no delivery address
	 */
	boolean hasNoDeliveryAddress();

	/**
	 * Checks if there is no delivery mode
	 *
	 * @return true if there is no delivery mode
	 */
	boolean hasNoDeliveryMode();

	/**
	 * Checks if there is no payment info
	 *
	 * @return true if there is no payment info
	 */
	boolean hasNoPaymentInfo();

}
