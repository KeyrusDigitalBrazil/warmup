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
package de.hybris.platform.chinesepaymentfacades.checkout;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.exceptions.BusinessException;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Optional;


/**
 * The facade of ChineseCheckout
 */
public interface ChineseCheckoutFacade extends AcceleratorCheckoutFacade
{
	/**
	 * Getting the SessionCart
	 *
	 * @return CartModel
	 */
	CartModel getCart();

	/**
	 * Merge the cart
	 *
	 * @param cartModel
	 *           cart model
	 */
	void mergeCart(final CartModel cartModel);

	/**
	 * Convert the CartModel into CartData
	 *
	 * @param cartModel
	 *           The CartModel to be converted
	 * @return cart data converted from cart model
	 */
	CartData convertCart(final CartModel cartModel);

	/**
	 * Save the PaymentMode in the cart
	 *
	 * @param paymentMode
	 *           The selected PaymentMode
	 */
	void setPaymentMode(final PaymentModeModel paymentMode);

	/**
	 * Reserve the stock after place order
	 *
	 * @param orderCode
	 *           The code of the order
	 * @param productCode
	 *           The code of the product in the order
	 * @param quantity
	 *           The quantity to be reserved
	 * @param pos
	 *           The point of service to find stock
	 * @return true if reserve stock successfully, false otherwise
	 * @throws InsufficientStockLevelException
	 *            when stock level is insufficient
	 */
	boolean reserveStock(final String orderCode, final String productCode, final int quantity,
			final Optional<PointOfServiceModel> pos) throws InsufficientStockLevelException;

	/**
	 * Getting the OrderDetails for code
	 *
	 * @param code
	 *           The code of the order
	 * @return order data
	 */
	OrderData getOrderDetailsForCode(final String code);

	/**
	 * Delete StockLevelReservationHistoryEntry after the user pay the order successfully
	 *
	 * @param code
	 *           The code of the order
	 */
	void deleteStockLevelReservationHistoryEntry(final String code);

	/**
	 * Check whether the cart has the ChinesePaymentInfo
	 *
	 * @return false if the cart has chinese payment info, true otherwise
	 */
	boolean hasNoChinesePaymentInfo();

	/**
	 * Create an order
	 *
	 * @return order data after place order
	 * @throws BusinessException
	 *            when business error both before place order and after place order
	 */
	OrderData createOrder() throws BusinessException;

	/**
	 * Publish the SubmitOrderEvent
	 *
	 * @param orderCode
	 *           The code of the order
	 */
	void publishSubmitOrderEvent(final String orderCode);

	/**
	 * Getting the OrderData by code
	 *
	 * @param code
	 *           The code of the order
	 * @return order data
	 */
	OrderData getOrderByCode(final String code);


	/**
	 * Submits order when paying order
	 * 
	 * @param orderCode
	 *           the order code
	 */
	void submitOrder(String orderCode);
}
