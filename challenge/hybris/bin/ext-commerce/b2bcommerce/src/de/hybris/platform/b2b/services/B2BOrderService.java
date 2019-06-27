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
package de.hybris.platform.b2b.services;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderscheduling.model.CartToOrderCronJobModel;

import java.util.List;


/**
 * Service around {@link OrderModel}
 * 
 * @spring.bean b2bOrderService
 */
public interface B2BOrderService extends OrderService
{

	/**
	 * Gets the orders with a status of {@link OrderStatus#REJECTED} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return the rejected orders
	 */
	public abstract <T extends UserModel> List<OrderModel> getRejectedOrders(final T user);

	/**
	 * Gets the orders with a status of {@link OrderStatus#REJECTED_BY_MERCHANT} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return the merchant rejected orders
	 * @deprecated Since 4.4. Use {@link #getRejectedForMerchantOrders(UserModel)} instead
	 */
	@Deprecated
	public abstract <T extends UserModel> List<OrderModel> getRejectedByMerchantOrders(final T user);

	/**
	 * Gets the orders with a status of {@link OrderStatus#REJECTED_BY_MERCHANT} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return the merchant rejected orders
	 */
	public abstract <T extends UserModel> List<OrderModel> getRejectedForMerchantOrders(final T user);

	/**
	 * @deprecated Since 6.3. Please see quote functionality from commerce.
	 *
	 * Gets the orders with a status of {@link OrderStatus#REJECTED_QUOTE} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return the rejected orders
	 */
	@Deprecated
	public abstract <T extends UserModel> List<OrderModel> getRejectedQuoteOrders(final T user);

	/**
	 * @deprecated Since 6.3. Please see quote functionality from commerce.
	 *
	 * Gets the orders with a status of {@link OrderStatus#APPROVED_QUOTE} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return the approved orders
	 */
	@Deprecated
	public abstract <T extends UserModel> List<OrderModel> getApprovedQuoteOrders(final T user);

	/**
	 * @deprecated Since 6.3. Please see quote functionality from commerce.
	 *
	 * Gets the orders with a status of {@link OrderStatus#PENDING_QUOTE} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return List of orders
	 */
	@Deprecated
	public abstract <T extends UserModel> List<OrderModel> getPendingQuoteOrders(final T user);

	/**
	 * Gets the orders with a status of {@link OrderStatus#APPROVED} for a user.
	 * 
	 * @param user
	 *           the user
	 * @return the approved orders
	 */
	public abstract <T extends UserModel> List<OrderModel> getApprovedOrders(final T user);

	/**
	 * Gets the order by code.
	 * 
	 * @param code
	 *           the code
	 * @return the order by code
	 * @deprecated Since 4.4. Use {@link #getOrderForCode(String)} instead
	 */
	@Deprecated
	public abstract OrderModel getOrderByCode(final String code);

	/**
	 * Gets the order based on it's {@link OrderModel#CODE}.
	 * 
	 * @param code
	 *           the code
	 * @return the order
	 */
	public abstract OrderModel getOrderForCode(final String code);

	/**
	 * Gets the orders which are in {@link OrderStatus#PENDING_APPROVAL}
	 * 
	 * @param user
	 *           the user
	 * @return the pending approval orders
	 */
	public <T extends UserModel> List<OrderModel> getPendingApprovalOrders(final T user);

	/**
	 * Gets the orders which are in {@link OrderStatus#PENDING_APPROVAL_FROM_MERCHANT}
	 * 
	 * @param user
	 *           the user
	 * @return the pending approval orders by merchant
	 */
	public <T extends UserModel> List<OrderModel> getPendingApprovalOrdersFromMerchant(final T user);

	/**
	 * Finds scheduled CartToOrderCronJob's for a given user.
	 * 
	 * @param user
	 *           A user who had a cart scheduled for replenishment.
	 * @return A list of cronjobs responsible for replenishment of users cart.
	 */
	public <T extends UserModel> List<CartToOrderCronJobModel> getScheduledOrders(final T user);

	/**
	 * Find scheduled cart to order job.
	 * 
	 * @param code
	 *           the code
	 * @return the cart to order cron job model
	 * @deprecated Since 4.4. Use {@link #getScheduledCartToOrderJobForCode(String)} instead
	 */
	@Deprecated
	public CartToOrderCronJobModel findScheduledCartToOrderJob(final String code);

	/**
	 * Get the CartToOrderCronJob with a particular {@link CartToOrderCronJobModel#CODE}.
	 * 
	 * @param code
	 *           the cron job's {@link CartToOrderCronJobModel#CODE}
	 * @return the cart to order cron job model
	 */
	public CartToOrderCronJobModel getScheduledCartToOrderJobForCode(final String code);


	/**
	 * Gets orders for a user with status {@link OrderStatus#B2B_PROCESSING_ERROR}
	 * 
	 * @param user
	 *           the user
	 * @return orders that filed to process for any reason
	 */
	public abstract <T extends UserModel> List<OrderModel> getErroredOrders(T user);

	/**
	 * @deprecated Since 6.3.
	 *
	 * Gets quote allowed flag based on Order detail
	 * 
	 * @param source
	 *           the order
	 * @return true if quote is allowed
	 */
	@Deprecated
	public boolean isQuoteAllowed(final AbstractOrderModel source);

	/**
	 * Delete order - currently we only allow delete quote order
	 * 
	 * @param code
	 *           the order to delete
	 */
	public void deleteOrder(final String code);

	/**
	 * Get the amount discounted from the original price. For example if the original price is 15 and the discount is 5
	 * off this method will return 10.
	 * 
	 * @param entry
	 *           An {@link AbstractOrderEntryModel}
	 * @return The discounted amount of the original price.
	 */
	public abstract double getOrderEntryDiscountAmount(final AbstractOrderEntryModel entry);

	/**
	 * Checks for item discounts.
	 * 
	 * @param order
	 *           the order
	 * @return true, if successful
	 */
	public abstract boolean hasItemDiscounts(final AbstractOrderModel order);

	/**
	 * Gets the total discount of a line item entry.
	 * 
	 * @param entry
	 *           the entry line item of an order
	 * @return the total discount
	 */
	public abstract double getTotalDiscount(final AbstractOrderEntryModel entry);

	/**
	 * Gets the order with a particular {@link AbstractOrderModel#CODE}.
	 * 
	 * @param code
	 *           the {@link AbstractOrderModel#CODE} of the order
	 * @return the order
	 */
	public abstract AbstractOrderModel getAbstractOrderForCode(String code);


}
