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
package de.hybris.platform.chinesepaymentservices.checkout;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.task.TaskModel;

import java.util.Optional;


/**
 * Service for Chinese checkout
 */
public interface ChineseCheckoutService
{
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
	 * @return If reserve stock successfully
	 * @throws InsufficientStockLevelException
	 *            when stock level is insufficient
	 */
	boolean reserveStock(final String orderCode, final String productCode, final int quantity, Optional<PointOfServiceModel> pos)
			throws InsufficientStockLevelException;

	/**
	 * Release Stock after cancel order
	 *
	 * @param orderCode
	 *           The code of the order
	 */
	void releaseStock(final String orderCode);

	/**
	 * Delete the StockLevelReservationHistoryEntry after the user pay the order successfully
	 *
	 * @param code
	 *           The code of the order
	 */
	void deleteStockLevelReservationHistoryEntry(final String code);

	/**
	 * Check whether the payment is authorized
	 *
	 * @param securityCode
	 *           The security code
	 * @param cartModel
	 *           The current cart
	 * @return true if the owner of the cart is current use, false otherwise
	 */
	boolean authorizePayment(final String securityCode, final CartModel cartModel);

	/**
	 * Save the PaymentMode information in the cart
	 *
	 * @param paymentMode
	 *           The selected PaymentMode
	 * @param cartModel
	 *           The current cart
	 */
	void setPaymentMode(final PaymentModeModel paymentMode, final CartModel cartModel);

	/**
	 * Getting the OrderModel by code
	 *
	 * @param code
	 *           The code of the order
	 * @return order model
	 */
	OrderModel getOrderByCode(final String code);


	/**
	 * Gets task model by order code
	 * 
	 * @param orderCode
	 *           the order code
	 * @return the optional of task model
	 */
	Optional<TaskModel> getSubmitOrderEventTask(String orderCode);
}
